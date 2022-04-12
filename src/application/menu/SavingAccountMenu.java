package application.menu;

import application.rdg.Account;
import application.rdg.AccountFinder;
import application.rdg.CurrencyFinder;
import application.rdg.UserFinder;
import application.ts.BankException;
import application.ts.BankService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class SavingAccountMenu extends Menu {
    public SavingAccountMenu() {
        super();
    }

    private void printAccount() throws BankException, SQLException {
        int userId;

        System.out.println("Zadajte id zakaznika:");
        userId = getNumberInput();

        DecimalFormat df = new DecimalFormat("#,###.00");
        System.out.println("________________");
        System.out.format("| %3s %20s %10s %s\n", "ID", "IBAN", "ZOSTATOK", "MENA");
        for (Account elem : AccountFinder.getInstance().findAllByUserId(userId)) {
            if(elem.getType() != elem.SAVING) continue;
            System.out.format("| %3s %20s %10s %s %s\n", elem.getId(), elem.getIban(), df.format(elem.getBalance()), elem.getCurrency(),
                    elem.getIsDeactivated() ? "DEACTIVATED" : "");
        }
        System.out.println("________________");
    }

    private void createAccount() throws BankException, SQLException {
        int userId;
        String currency;
        Account account;
        Account userAccount;

        System.out.println("Zadajte id zakaznika:");
        userId = getNumberInput();
        if(UserFinder.getInstance().findById(userId) == null) {
            throw new BankException("Zakaznik s danym id neexistuje");
        }

        System.out.println("Zadajte id bezneho uctu zakaznika:");
        userAccount = AccountFinder.getInstance().findById(getNumberInput());
        if(userAccount == null || userAccount.getType() != Account.ORDINARY || userAccount.getUserId() != userId) {
            throw new BankException("Zadane id bezneho uctu je neplatne");
        }

        account = new Account()
                .setUserId(userId)
                .setType(Account.SAVING)
                .setCurrency(userAccount.getCurrency())
                .setIban(BankService.getInstance().getRandomIban())
                .setIsDeactivated(false)
                .setActivatedAt(BankService.getInstance().getCurrentDate())
                .setSavingAccountId(userAccount.getId())
                .setInterestRate(new BigDecimal(1.05))
                .setBalance(BigDecimal.valueOf(0))
                .insert();
        System.out.format("Iban: %s\n", account.getIban());
    }

    private void activateAccount() throws BankException, SQLException {
        int accountId;
        Account account;

        System.out.println("Zadajte id uctu:");
        accountId = getNumberInput();
        account = AccountFinder.getInstance().findById(accountId);
        if(account == null || account.getType() != Account.SAVING)
            throw new BankException("Ucet s danym id cislom neexistuje");
        account.setIsDeactivated(!account.getIsDeactivated());
        if(account.getIsDeactivated()) {
            account.setDeactivatedAt(BankService.getInstance().getCurrentDate());
            System.out.println("Ucet zakaznika deaktivovany");
        }
        else {
            account.setActivatedAt(BankService.getInstance().getCurrentDate());
            System.out.println("Ucet zakaznika je aktivny");
        }
        account.update();
    }

    public void execute() throws SQLException, BankException {
        boolean exit = false;

        do {
            try {
                System.out.println("---------");
                System.out.println("Sporiaci ucet:");
                System.out.println("1. Vypis zoznam sporiacych uctov");
                System.out.println("2. Zaloz novy sporiaci ucet");
                System.out.println("3. Aktivuj deaktivuj sporiaci ucet");

                switch (getInput()) {
                    case "1":
                        printAccount();
                        break;

                    case "2":
                        createAccount();
                        break;

                    case "3":
                        activateAccount();
                        break;

                    case "x":
                        exit = true;
                        break;

                    default:
                        System.out.println("Zla moznost");
                        break;
                }
            } catch (Exception e) {
                printError(e.getMessage());
            }
        } while(!exit);
    }

}
