package application.rdg;

import application.DbContext;

import java.sql.*;

public class Card {
    private Integer id;
    private Integer accountId;
    private String number;
    private Integer cvv;
    private Date validTo;

    private final static String insertSql = "INSERT INTO \"card\" (account_id, number, cvv, valid_to) VALUES (?, ?, ?, ?)";
    private final static String updateSql = "UPDATE \"card\" SET account_id = ?, number = ?, cvv = ?, valid_to = ? WHERE id = ?";
    private final static String deleteSql = "DELETE FROM \"card\" WHERE id = ?";

    public int getId() {
        return id;
    }

    public Card setId(int id) {
        this.id = id;
        return this;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public Card setAccountId(Integer accountId) {
        this.accountId = accountId;
        return this;
    }

    public String getNumber() {
        return number;
    }

    public Card setNumber(String number) {
        this.number = number;
        return this;
    }

    public Integer getCvv() {
        return cvv;
    }

    public Card setCvv(Integer cvv) {
        this.cvv = cvv;
        return this;
    }

    public Date getValidTo() {
        return validTo;
    }

    public Card setValidTo(Date isDeactivated) {
        this.validTo = isDeactivated;
        return this;
    }

    public Card insert() throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.getAccountId());
            statement.setString(2, this.getNumber());
            statement.setInt(3, this.getCvv());
            statement.setDate(4, this.getValidTo());
            statement.executeUpdate();

            try (ResultSet r = statement.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1);
            }

            return this;
        }
    }

    public Card update() throws SQLException {
        if (id == null)
            throw new IllegalStateException("Column id is not set");

        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(updateSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.getAccountId());
            statement.setString(2, this.getNumber());
            statement.setInt(3, this.getCvv());
            statement.setDate(4, this.getValidTo());
            statement.setInt(5, this.getId());

            int rowCount = statement.executeUpdate();
            if(rowCount == 0)
                throw new SQLException("No row was updated");
            else if(1 < rowCount)
                throw new SQLException("More than one row was updated");

            return this;
        }
    }

    public Card delete() throws SQLException {
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
