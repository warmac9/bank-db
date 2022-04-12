package application.menu;

import application.rdg.Account;
import application.rdg.AccountFinder;
import application.rdg.QuarterChangeStatistic;
import application.rdg.QuarterChangeStatisticFinder;
import application.ts.BankException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class StatisticMenu extends Menu {
    public StatisticMenu() {
        super();
    }

    private void newUsers() throws SQLException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        System.out.println("________________");
        System.out.format("| %20s\n", "ZMENA ZA KVARTAL");
        for (QuarterChangeStatistic elem : QuarterChangeStatisticFinder.getInstance().findAllQuarterStatistic()) {
            System.out.format("| %10s %10d\n", format.format(elem.getDate()), elem.getAmount());
        }
        System.out.println("________________");
    }

    public void execute() throws SQLException, BankException {
        boolean exit = false;

        do {
            try {
                System.out.println("---------");
                System.out.println("Statistic Menu:");
                System.out.println("1. Pocty novych zakaznikov");

                switch (getInput()) {
                    case "1":
                        newUsers();
                        break;

                    case "x":
                        exit = true;
                        break;

                    default:
                        System.out.println("Zla moznost");
                        break;
                }
            } catch (Exception e) {
                printError("Chyba pri spracovani udajov \n" + e.getMessage());
            }
        } while(!exit);
    }

}
