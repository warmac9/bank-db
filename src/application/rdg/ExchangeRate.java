package application.rdg;

import application.DbContext;

import java.math.BigDecimal;
import java.sql.*;

public class ExchangeRate {
    private Integer id;
    private String currencyFrom;
    private String currencyTo;
    private BigDecimal amount;

    private final static String insertSql = "INSERT INTO \"exchange_rate\" (currency_from, currency_to, amount) VALUES (?, ?, ?)";
    private final static String updateSql = "UPDATE \"exchange_rate\" SET currency_from = ?, currency_to = ?, amount = ? WHERE id = ?";
    private final static String deleteSql = "DELETE FROM \"exchange_rate\" WHERE id = ?";

    public int getId() {
        return id;
    }

    public ExchangeRate setId(int id) {
        this.id = id;
        return this;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public ExchangeRate setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
        return this;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public ExchangeRate setCurrencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ExchangeRate setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public ExchangeRate insert() throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, this.getCurrencyFrom());
            statement.setString(2, this.getCurrencyTo());
            statement.setBigDecimal(3, this.getAmount());
            statement.executeUpdate();

            try (ResultSet r = statement.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1);
            }

            return this;
        }
    }

    public ExchangeRate update() throws SQLException {
        if (id == null)
            throw new IllegalStateException("Column id is not set");

        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(updateSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, this.getCurrencyFrom());
            statement.setString(2, this.getCurrencyTo());
            statement.setBigDecimal(3, this.getAmount());
            statement.setInt(4, this.getId());

            int rowCount = statement.executeUpdate();
            if(rowCount == 0)
                throw new SQLException("No row was updated");
            else if(1 < rowCount)
                throw new SQLException("More than one row was updated");

            return this;
        }
    }

    public ExchangeRate delete() throws SQLException {
        if (id == null)
            throw new IllegalStateException("Column id is not set");

        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(deleteSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.getId());

            int rowCount = statement.executeUpdate();
            if(rowCount == 0)
                throw new SQLException("No row was deleted");
            else if(1 < rowCount)
                throw new SQLException("More than one row was deleted");

            return this;
        }
    }
}
