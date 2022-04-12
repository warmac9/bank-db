package application.rdg;

import application.DbContext;

import java.math.BigDecimal;
import java.sql.*;

public class Account {
    public final static int ORDINARY = 1;
    public final static int SAVING = 2;
    public final static int TERMINAL = 3;

    private Integer id;
    private Integer userId;
    private Integer type;
    private String currency;
    private String iban;
    private boolean isDeactivated;
    private Date activatedAt;
    private Date deactivatedAt;
    private Integer savingAccountId;
    private BigDecimal interestRate;
    private Date interestTo;
    private BigDecimal balance;

    private final static String insertSql = "INSERT INTO \"account\" (user_id, type, currency, iban, is_deactivated, activated_at, deactivated_at, saving_account_id, interest_rate, interest_to, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String updateSql = "UPDATE \"account\" SET user_id = ?, type = ?, currency = ?, iban = ?, is_deactivated = ?, activated_at = ?, deactivated_at = ?, saving_account_id = ?, interest_rate = ?, interest_to = ?, balance = ? WHERE id = ?";
    private final static String deleteSql = "DELETE FROM \"account\" WHERE id = ?";

    public int getId() {
        return id;
    }

    public Account setId(int id) {
        this.id = id;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Account setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getType() {
        return type;
    }

    public Account setType(int type) {
        this.type = type;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public Account setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String getIban() {
        return iban;
    }

    public Account setIban(String iban) {
        this.iban = iban;
        return this;
    }

    public boolean getIsDeactivated() {
        return isDeactivated;
    }

    public Account setIsDeactivated(boolean isDeactivated) {
        this.isDeactivated = isDeactivated;
        return this;
    }

    public Date getActivatedAt() {
        return activatedAt;
    }

    public Account setActivatedAt(Date activatedAt) {
        this.activatedAt = activatedAt;
        return this;
    }

    public Date getDeactivatedAt() {
        return deactivatedAt;
    }

    public Account setDeactivatedAt(Date deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
        return this;
    }

    public Integer getSavingAccountId() {
        return savingAccountId;
    }

    public Account setSavingAccountId(Integer savingAccountId) {
        this.savingAccountId = savingAccountId;
        return this;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public Account setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
        return this;
    }

    public Date getInterestTo() {
        return interestTo;
    }

    public Account setInterestTo(Date interestTo) {
        this.interestTo = interestTo;
        return this;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Account setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public Account insert() throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.getUserId());
            statement.setInt(2, this.getType());
            statement.setString(3, this.getCurrency());
            statement.setString(4, this.getIban());
            statement.setBoolean(5, this.getIsDeactivated());
            statement.setDate(6, this.getActivatedAt());
            statement.setDate(7, this.getDeactivatedAt());
            if (this.getSavingAccountId() == null)
                statement.setNull(8, Types.INTEGER);
            else
                statement.setInt(8, this.getSavingAccountId());
            statement.setBigDecimal(9, this.getInterestRate());
            statement.setDate(10, this.getInterestTo());
            statement.setBigDecimal(11, this.getBalance());
            statement.executeUpdate();

            try (ResultSet r = statement.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1);
            }

            return this;
        }
    }

    public Account update() throws SQLException {
        if (id == null)
            throw new IllegalStateException("Column id is not set");

        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(updateSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.getUserId());
            statement.setInt(2, this.getType());
            statement.setString(3, this.getCurrency());
            statement.setString(4, this.getIban());
            statement.setBoolean(5, this.getIsDeactivated());
            statement.setDate(6, this.getActivatedAt());
            statement.setDate(7, this.getDeactivatedAt());
            if (this.getSavingAccountId() == null)
                statement.setNull(8, Types.INTEGER);
            else
                statement.setInt(8, this.getSavingAccountId());
            statement.setBigDecimal(9, this.getInterestRate());
            statement.setDate(10, this.getInterestTo());
            statement.setBigDecimal(11, this.getBalance());
            statement.setInt(12, this.getId());

            int rowCount = statement.executeUpdate();
            if(rowCount == 0)
                throw new SQLException("No row was updated");
            else if(1 < rowCount)
                throw new SQLException("More than one row was updated");

            return this;
        }
    }

    public Account delete() throws SQLException {
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
