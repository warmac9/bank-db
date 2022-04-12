package application.menu;

import application.rdg.Transaction;
import application.rdg.TransactionFinder;
import application.rdg.User;
import application.rdg.UserFinder;
import application.ts.BankException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class CrudMenu extends Menu {
    public CrudMenu() {
        super();
    }

    private void printTransactions() throws BankException, SQLException {
        System.out.println("Zadajte id uctu");
        int accountId = getNumberInput();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD");

        System.out.println("________________");
        System.out.format("| %3s %20s %20s %10s %7s %15s\n", "ID", "ODOSIELATEL", "PRIJIMATEL", "SUMA", "MENA", "VYKONANE");
        for (Transaction elem : TransactionFinder.getInstance().findAllByAccountId(accountId)) {
            System.out.format("| %3s %20s %20s %10s %7s %15s\n", elem.getId(), elem.getIbanFrom(), elem.getIbanTo(), elem.getAmount(), elem.getCurrency(), format.format(elem.getExecutedAt()));
        }
        System.out.println("________________");
    }

    public void execute() throws SQLException, BankException {
        boolean exit;

        do {
            System.out.println("---------");
            System.out.println("CRUD Menu:");
            System.out.println("1. Zakaznici");
            System.out.println("2. Bezny ucet");
            System.out.println("3. Sporiaci ucet");
            System.out.println("4. Terminovany ucet");
            System.out.println("5. Prevodovy kurz");
            System.out.println("6. Transakcie");

            exit = false;

            switch (getInput()) {
                case "1":
                    new UserMenu().execute();
                    break;

                case "2":
                    new AccountMenu().execute();
                    break;

                case "3":
                    new SavingAccountMenu().execute();
                    break;

                case "4":
                    new TerminalAccountMenu().execute();
                    break;

                case "5":
                    new ExchangeRateMenu().execute();
                    break;

                case "6":
                    printTransactions();

                case "x":
                    exit = true;
                    break;

                default:
                    System.out.println("Zla moznost");
                    break;
            }
        } while(!exit);
    }

}
