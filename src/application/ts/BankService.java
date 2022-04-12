package application.ts;

import application.rdg.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BankService {
    private final static BigDecimal FEE_TRANSACTION_FROM_SPENDING = new BigDecimal(5);

    private static BankService instance;

    public static BankService getInstance() {
        if(instance == null)
            instance = new BankService();
        return instance;
    }

    public String getRandomIban() {
        Random rand = new Random();
        return String.format("SK%02d %04d %04d", rand.nextInt(99), rand.nextInt(9999), rand.nextInt(9999));
    }

    public Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    public void transfer(String ibanFrom, String ibanTo, BigDecimal amount, Date transferDate) throws BankException, SQLException {
        if(amount.compareTo(new BigDecimal(0)) < 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Account accountFrom = AccountFinder.getInstance().findByIban(ibanFrom);
        Account accountTo = AccountFinder.getInstance().findByIban(ibanTo);

        Integer type = Transaction.ORDINARY;
        String currency;
        BigDecimal conversedAmount;
        BigDecimal fee = new BigDecimal(0);
        Date executedDate = null;
        BigDecimal balance;

        if(accountFrom == null) {
            throw new BankException("Sender's account does not exist");
        }

        if(!(1 <= accountFrom.getType() && accountFrom.getType() <= 3)) {
            throw new BankException("Sender's account type does not exist");
        }

        if(accountFrom.getIsDeactivated() || (accountTo != null && accountTo.getIsDeactivated())) {
            throw new BankException("Account in transfer can not be deactivated");
        }

        if(accountFrom.getType() == Account.SAVING &&
            (accountTo == null || accountFrom.getSavingAccountId() == null || accountFrom.getSavingAccountId() != accountTo.getId())) {
                throw new BankException("Recipient for saving account must be the ordinary account assigned to it");
        }

        if(accountFrom.getType() == Account.TERMINAL &&
            accountFrom.getInterestTo().compareTo(transferDate) > 0) {
                throw new BankException("Withdrawal from terminal account to the moment of expiration of interest is forbidden");
        }

        if(accountTo == null || Objects.equals(accountFrom.getCurrency(), accountTo.getCurrency())) {
            conversedAmount = amount;
        } else {
            ExchangeRate exchangeRate = ExchangeRateFinder.getInstance().findByCurrency(accountFrom.getCurrency(), accountTo.getCurrency());

            if(exchangeRate == null) {
                throw new BankException("Exchange rate does not exist");
            }

            conversedAmount = amount.multiply(exchangeRate.getAmount());
        }

        balance = accountFrom.getBalance().subtract(conversedAmount);

        if(balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BankException("Sender's account does not dispose of enough resources");
        }

        accountFrom.setBalance(balance);
        currency = accountFrom.getCurrency();

        if(accountTo != null) {
            accountTo.setBalance(accountTo.getBalance().add(conversedAmount));
            executedDate = transferDate;
            currency = accountTo.getCurrency();
        }

        if(accountFrom.getType() == Account.SAVING) {
            type = Transaction.FROM_SAVING;

            if(1 <= TransactionFinder.getInstance().findAllFromSavingInMonthByAccountId(accountFrom.getUserId(), transferDate).size()) {
                fee = FEE_TRANSACTION_FROM_SPENDING;
                chargeFee(accountFrom, fee, transferDate);
            }
        }

        new Transaction()
                .setAccountId(accountFrom.getUserId())
                .setType(type)
                .setIbanFrom(ibanFrom)
                .setIbanTo(ibanTo)
                .setAmount(amount)
                .setCurrency(currency)
                .setAmountAccount(conversedAmount)
                .setFee(fee)
                .setBalance(balance)
                .setCreatedAt(transferDate)
                .setExecutedAt(executedDate)
                .insert();

        accountFrom.update();
        if(accountTo != null)
            accountTo.update();
    }

    private void chargeFee(Account account, BigDecimal fee, Date transferDate) throws BankException, SQLException {
        BigDecimal balance = account.getBalance().subtract(fee);

        if(balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BankException("Sender's account does not dispose of enough resources");
        }

        account.setBalance(balance);
        account.update();

        new Transaction()
                .setAccountId(account.getUserId())
                .setType(Transaction.FEE)
                .setIbanFrom(account.getIban())
                .setAmount(fee)
                .setCurrency(account.getCurrency())
                .setBalance(balance)
                .setCreatedAt(transferDate)
                .setExecutedAt(transferDate)
                .insert();
    }

    public void dailyClosing() throws SQLException {
        List<Transaction> transactionList = TransactionFinder.getInstance().findAllInMonth(BankService.getInstance().getCurrentDate());
        for(Transaction transaction : transactionList) {
            if(transaction.getExecutedAt() == null) {
                transaction.setExecutedAt(BankService.getInstance().getCurrentDate());
                transaction.update();
            }
        }
    }

    public void monthlyClosing() throws SQLException {
        for(Account account : AccountFinder.getInstance().findAll()) {
            if(account.getInterestRate() == null || account.getIsDeactivated())
                continue;

            if(account.getType() == Account.TERMINAL &&
            BankService.getInstance().getCurrentDate().compareTo(account.getInterestTo()) > 0) {
                if(account.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                    account.setDeactivatedAt(BankService.getInstance().getCurrentDate())
                            .setIsDeactivated(true)
                            .update();
                }
                continue;
            }

            BigDecimal newBalance = account.getBalance().multiply(account.getInterestRate());
            BigDecimal diff = newBalance.subtract(account.getBalance());
            account.setBalance(newBalance).update();
            new Transaction()
                    .setAccountId(account.getUserId())
                    .setType(Transaction.FROM_SAVING)
                    .setIbanTo(account.getIban())
                    .setAmount(diff)
                    .setCurrency(account.getCurrency())
                    .setBalance(newBalance)
                    .setCreatedAt(BankService.getInstance().getCurrentDate())
                    .setExecutedAt(BankService.getInstance().getCurrentDate())
                    .insert();
        }

        for(User user : UserFinder.getInstance().findAll()) {
            BigDecimal expenses = new BigDecimal(0);
            BigDecimal incomes = new BigDecimal(0);
            BigDecimal difference = new BigDecimal(0);

            for(Account account : AccountFinder.getInstance().findAllByUserId(user.getId())) {
                for(Transaction transaction : TransactionFinder.getInstance().findAllInMonthByIban(BankService.getInstance().getCurrentDate(), account.getIban())) {

                    if(Objects.equals(transaction.getIbanFrom(), account.getIban())) {
                        try {
                            expenses = expenses.add(transaction.getAmount());
                        } catch (Exception e) {}
                    }
                    else {
                        try {
                            incomes = incomes.add(transaction.getAmountAccount());
                        } catch (Exception e) {}
                    }
                }
            }

            difference = incomes.subtract(expenses);
            new Statement()
                    .setUserId(user.getId())
                    .setCreatedAt(BankService.getInstance().getCurrentDate())
                    .setTextDocument(String.format("Vydavky: %s\nPrijmy: %s\nZmena: %s", expenses, incomes, difference))
                    .insert();
        }
    }

    public void deactivateAccount(int accountId) throws SQLException, BankException {
        Account account = AccountFinder.getInstance().findById(accountId);
        if(account == null)
            throw new BankException("Nespravne id uctu");

        if(account.getType() == Account.SAVING) {
            Account ordinary = AccountFinder.getInstance().findById(account.getSavingAccountId());
            ordinary.setBalance(account.getBalance().add(ordinary.getBalance()))
                    .update();
            account.setDeactivatedAt(BankService.getInstance().getCurrentDate())
                    .setIsDeactivated(true)
                    .setBalance(BigDecimal.ZERO)
                    .update();
        }

        if(account.getType() == Account.ORDINARY || account.getType() == Account.TERMINAL) {
            if(account.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                account.setDeactivatedAt(BankService.getInstance().getCurrentDate())
                        .setIsDeactivated(true);

                for(Account saving : AccountFinder.getInstance().findAllSaving(account.getId())) {
                    account.setBalance(account.getBalance().add(saving.getBalance()));
                    saving.setDeactivatedAt(BankService.getInstance().getCurrentDate())
                            .setIsDeactivated(true)
                            .setBalance(BigDecimal.ZERO)
                            .update();
                }

                account.update();
            }
            else {
                throw new BankException("Ucet nema nulovy zostatok");
            }
        }
    }

    public void deactivateUser(int userId) throws SQLException, BankException {
        List<Account> accounts = AccountFinder.getInstance().findAllByUserId(userId);

        for(Account account : accounts) {
            if(account.getType() == Account.ORDINARY || account.getType() == Account.TERMINAL) {
                if(!(account.getBalance().compareTo(BigDecimal.ZERO) == 0)) {
                    throw new BankException("Nemozno deaktivovat zakaznika\nUcet nema nulovy zostatok");
                }
            }
        }

        for(Account account : accounts) {
            if(account.getType() == Account.ORDINARY || account.getType() == Account.TERMINAL) {
                if(account.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                    account.setDeactivatedAt(BankService.getInstance().getCurrentDate())
                            .setIsDeactivated(true);

                    for(Account saving : AccountFinder.getInstance().findAllSaving(account.getId())) {
                        account.setBalance(account.getBalance().add(saving.getBalance()));
                        saving.setDeactivatedAt(BankService.getInstance().getCurrentDate())
                                .setIsDeactivated(true)
                                .setBalance(BigDecimal.ZERO)
                                .update();
                    }

                    account.update();
                }
                else {
                    throw new BankException("Ucet nema nulovy zostatok");
                }
            }
        }

        UserFinder.getInstance().findById(userId)
                .setIsDeactivated(true)
                .update();
    }
}
