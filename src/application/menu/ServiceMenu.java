package application.menu;

import application.rdg.Account;
import application.rdg.AccountFinder;
import application.ts.BankException;
import application.ts.BankService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class ServiceMenu extends Menu {
    public ServiceMenu() {
        super();
    }

    private void transfer() throws BankException, SQLException {
        String ibanFrom;
        String ibanTo;
        BigDecimal amount;

        System.out.println("Zadajte ibanFrom:");
        ibanFrom = getInput();
        System.out.println("Zadajte ibanTo:");
        ibanTo = getInput();
        System.out.println("Zadajte sumu:");
        amount = new BigDecimal(getInput());

        BankService.getInstance().transfer(ibanFrom, ibanTo, amount, BankService.getInstance().getCurrentDate());
    }

    private void dailyClosing() throws SQLException {
        BankService.getInstance().dailyClosing();
        System.out.println("Denna uzavierka prebehla uspesne");
    }

    private void monthlyClosing() throws SQLException {
        BankService.getInstance().monthlyClosing();
        System.out.println("Mesacna uzavierka prebehla uspesne");
    }

    private void deactivateAccount() throws BankException, SQLException {
        System.out.println("Zadajte id uctu:");
        BankService.getInstance().deactivateAccount(getNumberInput());
        System.out.println("Ucet uspesne deaktivovany");
    }

    private void deactivateUser() throws BankException, SQLException {
        System.out.println("Zadajte id zakaznika:");
        BankService.getInstance().deactivateUser(getNumberInput());
        System.out.println("Zakaznik uspesne deaktivovany");
    }

    public void execute() throws SQLException, BankException {
        boolean exit = false;

        do {
            try {
                System.out.println("---------");
                System.out.println("Zlozitejsie operacie:");
                System.out.println("1. Prevod");
                System.out.println("2. Denna uzavierka");
                System.out.println("3. Koncomesacna uzavierka");
                System.out.println("4. Deaktivovanie uctu");
                System.out.println("5. Deaktivovanie zakaznika");

                switch (getInput()) {
                    case "1":
                        transfer();
                        break;

                    case "2":
                        dailyClosing();
                        break;

                    case "3":
                        monthlyClosing();
                        break;

                    case "4":
                        deactivateAccount();
                        break;

                    case "5":
                        deactivateUser();
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
