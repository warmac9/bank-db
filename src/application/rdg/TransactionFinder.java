package application.rdg;

import application.DbContext;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionFinder {
    private final static String findByIdSql = "SELECT * FROM \"transaction\" WHERE id = ?";
    private final static String findAllByAccountIdSql = "SELECT * FROM \"transaction\" WHERE account_id = ?";
    private final static String findAllFromSavingInMonthByAccountIdSql = "SELECT * FROM \"transaction\" WHERE account_id = ? AND EXTRACT(YEAR FROM executed_at) = ? AND EXTRACT(MONTH FROM executed_at) = ? AND type = 2";
    private final static String findAllInMonthSql = "SELECT * FROM \"transaction\" WHERE EXTRACT(YEAR FROM created_at) = ? AND EXTRACT(MONTH FROM created_at) = ?";
    private final static String findAllInMonthByIbanSql = "SELECT * FROM \"transaction\" WHERE EXTRACT(YEAR FROM created_at) = ? AND EXTRACT(MONTH FROM created_at) = ? AND (iban_from = ? OR iban_to = ?)";

    private static TransactionFinder instance;

    public static TransactionFinder getInstance() {
        if(instance == null)
            instance = new TransactionFinder();
        return instance;
    }

    public Transaction resToTransaction(ResultSet res) throws SQLException {
        return new Transaction()
                .setId(res.getInt("id"))
                .setAccountId(res.getInt("account_id"))
                .setType(res.getInt("type"))
                .setIbanFrom(res.getString("iban_from"))
                .setIbanTo(res.getString("iban_to"))
                .setAmount(res.getBigDecimal("amount"))
                .setCurrency(res.getString("currency"))
                .setAmountAccount(res.getBigDecimal("amount_account"))
                .setFee(res.getBigDecimal("fee"))
                .setBalance(res.getBigDecimal("balance"))
                .setCreatedAt(res.getDate("created_at"))
                .setExecutedAt(res.getDate("executed_at"));
    }

    public Transaction findById(int id) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findByIdSql)) {
            statement.setInt(1, id);

            try(ResultSet res = statement.executeQuery()) {
                if(!res.next())
                    return null;

                Transaction transaction = resToTransaction(res);

                if (res.next())
                    throw new RuntimeException("More than one row was returned");

                return transaction;
            }
        }
    }

    public List<Transaction> findAllByAccountId(int userId) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findAllByAccountIdSql)) {
            statement.setInt(1, userId);

            List<Transaction> list = new ArrayList<>();

            try(ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    list.add(resToTransaction(res));
                }

                return list;
            }
        }
    }

    public List<Transaction> findAllFromSavingInMonthByAccountId(int userId, Date date) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findAllFromSavingInMonthByAccountIdSql)) {
            statement.setInt(1, userId);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            statement.setInt(2, calendar.get(Calendar.YEAR));
            statement.setInt(3, calendar.get(Calendar.MONTH) + 1);

            List<Transaction> list = new ArrayList<>();

            try(ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    list.add(resToTransaction(res));
                }

                return list;
            }
        }
    }

    public List<Transaction> findAllInMonth(Date date) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findAllInMonthSql)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            statement.setInt(1, calendar.get(Calendar.YEAR));
            statement.setInt(2, calendar.get(Calendar.MONTH) + 1);

            List<Transaction> list = new ArrayList<>();

            try(ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    list.add(resToTransaction(res));
                }

                return list;
            }
        }
    }

    public List<Transaction> findAllInMonthByIban(Date date, String iban) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findAllInMonthByIbanSql)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            statement.setInt(1, calendar.get(Calendar.YEAR));
            statement.setInt(2, calendar.get(Calendar.MONTH) + 1);
            statement.setString(3, iban);
            statement.setString(4, iban);

            List<Transaction> list = new ArrayList<>();

            try(ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    list.add(resToTransaction(res));
                }

                return list;
            }
        }
    }
}
