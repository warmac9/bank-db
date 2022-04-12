package application.menu;

import application.rdg.Account;
import application.rdg.AccountFinder;
import application.rdg.CurrencyFinder;
import application.rdg.UserFinder;
import application.ts.BankException;
import application.ts.BankService;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;

public class TerminalAccountMenu extends Menu {
    public TerminalAccountMenu() {
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
            if(elem.getType() != elem.TERMINAL) continue;
            System.out.format("| %3s %20s %10s %s %s\n", elem.getId(), elem.getIban(), df.format(elem.getBalance()), elem.getCurrency(),
                    elem.getIsDeactivated() ? "DEACTIVATED" : "");
        }
        System.out.println("________________");
    }

    private void createAccount() throws BankException, SQLException {
        int userId;
        String currency;
        Account account;

        System.out.println("Zadajte id zakaznika:");
        userId = getNumberInput();
        if(UserFinder.getInstance().findById(userId) == null) {
            throw new BankException("Zakaznik s danym id neexistuje");
        }

        System.out.println("Zadajte menu:");
        currency = getInput().toUpperCase();
        if(CurrencyFinder.getInstance().findByCode(currency) == null)
            throw new BankException("Dana mena neexistuje");

        account = new Account()
                .setUserId(userId)
                .setType(Account.TERMINAL)
                .setCurrency(currency)
                .setIban(BankService.getInstance().getRandomIban())
                .setIsDeactivated(false)
                .setActivatedAt(BankService.getInstance().getCurrentDate())
                .setInterestRate(new BigDecimal(1.1))
                .setInterestTo(Date.valueOf(BankService.getInstance().getCurrentDate().toLocalDate().plusYears(3)))
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
        if(account == null || account.getType() != Account.TERMINAL)
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
                System.out.println("Terminovany ucet:");
                System.out.println("1. Vypis zoznam terminovanych uctov");
                System.out.println("2. Zaloz novy terminovany ucet");
                System.out.println("3. Aktivuj deaktivuj terminovany ucet");

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
