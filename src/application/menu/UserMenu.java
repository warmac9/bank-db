package application.menu;

import application.rdg.Account;
import application.rdg.AccountFinder;
import application.rdg.User;
import application.rdg.UserFinder;
import application.ts.BankException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class UserMenu extends Menu {
    public UserMenu() {
        super();
    }

    private void printUser() throws SQLException {
        System.out.println("________________");
        System.out.format("| %3s %20s %20s %15s\n", "ID", "MENO", "EMAIL", "RODNE CISLO");
        for (User elem : UserFinder.getInstance().findAll()) {
            System.out.format("| %3s %20s %20s %15s %s\n", elem.getId(), elem.getFullName(), elem.getEmail(), elem.getIdNumber(),
                    elem.getIsDeactivated() ? "DEACTIVATED" : "");
        }
        System.out.println("________________");
    }

    private void findUser() throws SQLException, BankException {
        String idNumber;
        User user;

        System.out.println("Zadajte rodne cislo zakaznika:");
        idNumber = getInput();
        user = UserFinder.getInstance().findByIdNumber(idNumber);
        if(user == null)
            throw new BankException("Zakaznik s danym rodnym cislom neexistuje");
        System.out.format("  %3s %20s %20s %15s\n", "ID", "MENO", "EMAIL", "RODNE CISLO");
        System.out.format("| %3s %20s %20s %15s %s\n", user.getId(), user.getFullName(), user.getEmail(), user.getIdNumber(),
                user.getIsDeactivated() ? "DEACTIVATED" : "");
    }

    private void updateUser() throws BankException, SQLException {
        User user;

        System.out.println("Zadajte id zakaznika:");
        user = UserFinder.getInstance().findById(getNumberInput());
        if(user == null)
            throw new BankException("Zakaznik s danym id cislom neexistuje");
        System.out.format("  %3s %20s %20s\n", "ID", "MENO", "EMAIL");
        System.out.format("| %3s %20s %20s\n", user.getId(), user.getFullName(), user.getEmail());

        System.out.println("Zadajte novy email:");
        user.setEmail(getInput());
        user.update();
    }

    public void execute() throws SQLException, BankException {
        boolean exit = false;
        String idNumber;
        int id;
        User user;

        do {
            try {
                System.out.println("---------");
                System.out.println("Zakaznik:");
                System.out.println("1. Vypis zoznam zakaznikov");
                System.out.println("2. Nájdi zákazníka podľa rodného čísla");
                System.out.println("3. Aktualizuj osobné údaje zákazníka");

                switch (getInput()) {
                    case "1":
                        printUser();
                        break;

                    case "2":
                        findUser();
                        break;

                    case "3":
                        updateUser();
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
