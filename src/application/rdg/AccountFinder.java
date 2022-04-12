package application.rdg;

import application.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountFinder {
    private final static String findByIdSql = "SELECT * FROM \"account\" WHERE id = ?";
    private final static String findByIbanSql = "SELECT * FROM \"account\" WHERE iban = ?";
    private final static String findAllByUserIdSql = "SELECT * FROM \"account\" WHERE user_id = ?";
    private final static String findAllSql = "SELECT * FROM \"account\"";
    private final static String findAllSavingSql = "SELECT * FROM \"account\" WHERE saving_account_id = ?";

    private static AccountFinder instance;

    public static AccountFinder getInstance() {
        if(instance == null)
            instance = new AccountFinder();
        return instance;
    }

    public Account resToAccount(ResultSet res) throws SQLException {
        Account account = new Account()
                .setId(res.getInt("id"))
                .setUserId(res.getInt("user_id"))
                .setType(res.getInt("type"))
                .setCurrency(res.getString("currency"))
                .setIban(res.getString("iban"))
                .setIsDeactivated(res.getBoolean("is_deactivated"))
                .setActivatedAt(res.getDate("activated_at"))
                .setDeactivatedAt(res.getDate("deactivated_at"))
                .setInterestRate(res.getBigDecimal("interest_rate"))
                .setInterestTo(res.getDate("interest_to"))
                .setBalance(res.getBigDecimal("balance"));

        account.setSavingAccountId(res.getInt("saving_account_id"));
        if(res.wasNull())
            account.setSavingAccountId(null);

        return account;
    }

    public Account findById(int id) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findByIdSql)) {
            statement.setInt(1, id);

            try(ResultSet res = statement.executeQuery()) {
                if(!res.next())
                    return null;

                Account account = resToAccount(res);

                if (res.next())
                    throw new RuntimeException("More than one row was returned");

                return account;
            }
        }
    }

    public Account findByIban(String iban) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findByIbanSql)) {
            statement.setString(1, iban);

            try(ResultSet res = statement.executeQuery()) {
                if(!res.next())
                    return null;

                Account account = resToAccount(res);

                if (res.next())
                    throw new RuntimeException("More than one row was returned");

                return account;
            }
        }
    }

    public List<Account> findAllByUserId(int userId) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findAllByUserIdSql)) {
            statement.setInt(1, userId);

            List<Account> list = new ArrayList<>();

            try(ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    list.add(resToAccount(res));
                }

                return list;
            }
        }
    }

    public List<Account> findAll() throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findAllSql)) {
            List<Account> list = new ArrayList<>();

            try(ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    list.add(resToAccount(res));
                }

                return list;
            }
        }
    }

    public List<Account> findAllSaving(int accountId) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findAllSavingSql)) {
            List<Account> list = new ArrayList<>();
            statement.setInt(1, accountId);

            try(ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    list.add(resToAccount(res));
                }

                return list;
            }
        }
    }
}
