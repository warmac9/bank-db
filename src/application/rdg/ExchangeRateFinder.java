package application.rdg;

import application.DbContext;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExchangeRateFinder {
    private final static String findByIdSql = "SELECT * FROM \"exchange_rate\" WHERE id = ?";
    private final static String findByCurrencySql = "SELECT * FROM \"exchange_rate\" WHERE currency_from = ? AND currency_to = ?";
    private final static String findAllSql = "SELECT * FROM \"exchange_rate\"";

    private static ExchangeRateFinder instance;

    public static ExchangeRateFinder getInstance() {
        if(instance == null)
            instance = new ExchangeRateFinder();
        return instance;
    }

    public ExchangeRate findById(int id) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findByIdSql)) {
            statement.setInt(1, id);

            try(ResultSet res = statement.executeQuery()) {
                if(!res.next())
                    return null;

                ExchangeRate exchangeRate = new ExchangeRate()
                        .setId(res.getInt("id"))
                        .setCurrencyFrom(res.getString("currency_from"))
                        .setCurrencyTo(res.getString("currency_to"))
                        .setAmount(res.getBigDecimal("amount"));

                if (res.next())
                    throw new RuntimeException("More than one row was returned");

                return exchangeRate;
            }
        }
    }

    public ExchangeRate findByCurrency(String currencyFrom, String currencyTo) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findByCurrencySql)) {
            statement.setString(1, currencyFrom);
            statement.setString(2, currencyTo);

            try(ResultSet res = statement.executeQuery()) {
                if(!res.next())
                    return null;

                ExchangeRate exchangeRate = new ExchangeRate()
                        .setId(res.getInt("id"))
                        .setCurrencyFrom(res.getString("currency_from"))
                        .setCurrencyTo(res.getString("currency_to"))
                        .setAmount(res.getBigDecimal("amount"));

                if (res.next())
                    throw new RuntimeException("More than one row was returned");

                return exchangeRate;
            }
        }
    }

    public List<ExchangeRate> findAll() throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findAllSql)) {
            List<ExchangeRate> list = new ArrayList<>();
            ExchangeRate exchangeRate;

            try(ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    exchangeRate = new ExchangeRate()
                            .setId(res.getInt("id"))
                            .setCurrencyFrom(res.getString("currency_from"))
                            .setCurrencyTo(res.getString("currency_to"))
                            .setAmount(res.getBigDecimal("amount"));
                    list.add(exchangeRate);
                }

                return list;
            }
        }
    }
}
