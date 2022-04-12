package application.menu;

import application.ts.BankException;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public abstract class Menu {
   Map<String, String> state = new HashMap<String, String>();

    protected String getInput()  {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        return input;
    }

    protected int getNumberInput() throws BankException {
        try {
            return Integer.parseInt(getInput());
        } catch (NumberFormatException e) {
            throw new BankException("Zadali ste text alebo velke cislo");
        }
    }

    protected void printError(String message) {
        System.out.println("Nastala chyba:");
        System.out.println(message);
    }

    public Menu() {}

    public Menu(Map<String, String> state) {
        this.state = state;
    }

    abstract void execute() throws SQLException, BankException;
}
