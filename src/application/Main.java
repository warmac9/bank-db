package application;

import application.menu.MainMenu;
import application.rdg.*;
import application.ts.BankException;
import application.ts.BankService;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Main {
    public static final boolean isProduction = false;

    public static void main(String[] args) throws SQLException, BankException {
//        BankService.getInstance().transfer("SK42 1001", "SK42 0002", new BigDecimal(10), new Date(System.currentTimeMillis()));
        new MainMenu().execute();
    }
}
