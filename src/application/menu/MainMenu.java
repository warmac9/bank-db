package application.menu;

import application.ts.BankException;

import java.sql.SQLException;

public class MainMenu extends Menu {
    public MainMenu() {
        super();
    }

    public void execute() throws SQLException, BankException {
        boolean exit;

        do {
            System.out.println("---------");
            System.out.println("Menu:");
            System.out.println("1. CRUD operacie");
            System.out.println("2. Zlozite operacie");
            System.out.println("3. Statistiky");

            exit = false;

            switch (getInput()) {
                case "1":
                    new CrudMenu().execute();
                    break;

                case "2":
                    new ServiceMenu().execute();
                    break;

                case "3":
                    new StatisticMenu().execute();
                    break;

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
