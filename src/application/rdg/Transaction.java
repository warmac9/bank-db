package application.rdg;

import application.DbContext;

import java.math.BigDecimal;
import java.sql.*;

public class Transaction {
    public final static int ORDINARY = 1;
    public final static int FROM_SAVING = 2;
    public final static int FEE = 3;

    private Integer id;
    private Integer accountId;
    private Integer type;
    private String ibanFrom;
    private String ibanTo;
    private BigDecimal amount;
    private String currency;
    private BigDecimal amountAccount;
    private BigDecimal fee;
    private BigDecimal balance;
    private Date createdAt;
    private Date executedAt;

    private final static String insertSql = "INSERT INTO \"transaction\" (account_id, type, iban_from, iban_to, amount, currency, amount_account, fee, balance, created_at, executed_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String updateSql = "UPDATE \"transaction\" SET account_id = ?, type = ?, iban_from = ?, iban_to = ?, amount = ?, currency = ?, amount_account = ?, fee = ?, balance = ?, created_at = ?, executed_at = ? WHERE id = ?";
    private final static String deleteSql = "DELETE FROM \"transaction\" WHERE id = ?";

    public int getId() {
        return id;
    }

    public Transaction setId(int id) {
        this.id = id;
        return this;
    }

    public int getAccountId() {
        return accountId;
    }

    public Transaction setAccountId(int accountId) {
        this.accountId = accountId;
        return this;
    }

    public int getType() {
        return type;
    }

    public Transaction setType(int type) {
        this.type = type;
        return this;
    }

    public String getIbanFrom() {
        return ibanFrom;
    }

    public Transaction setIbanFrom(String ibanFrom) {
        this.ibanFrom = ibanFrom;
        return this;
    }

    public String getIbanTo() {
        return ibanTo;
    }

    public Transaction setIbanTo(String ibanTo) {
        this.ibanTo = ibanTo;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Transaction setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public Transaction setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public BigDecimal getAmountAccount() {
        return amountAccount;
    }

    public Transaction setAmountAccount(BigDecimal amountAccount) {
        this.amountAccount = amountAccount;
        return this;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public Transaction setFee(BigDecimal fee) {
        this.fee = fee;
        return this;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Transaction setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Transaction setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getExecutedAt() {
        return executedAt;
    }

    public Transaction setExecutedAt(Date executedAt) {
        this.executedAt = executedAt;
        return this;
    }

    public Transaction insert() throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.getAccountId());
            statement.setInt(2, this.getType());
            statement.setString(3, this.getIbanFrom());
            statement.setString(4, this.getIbanTo());
            statement.setBigDecimal(5, this.getAmount());
            statement.setString(6, this.getCurrency());
            statement.setBigDecimal(7, this.getAmountAccount());
            statement.setBigDecimal(8, this.getFee());
            statement.setBigDecimal(9, this.getBalance());
            statement.setDate(10, this.getCreatedAt());
            statement.setDate(11, this.getExecutedAt());
            statement.executeUpdate();

            try (ResultSet r = statement.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1);
            }

            return this;
        }
    }

    public Transaction update() throws SQLException {
        if (id == null)
            throw new IllegalStateException("Column id is not set");

        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(updateSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.getAccountId());
            statement.setInt(2, this.getType());
            statement.setString(3, this.getIbanFrom());
            statement.setString(4, this.getIbanTo());
            statement.setBigDecimal(5, this.getAmount());
            statement.setString(6, this.getCurrency());
            statement.setBigDecimal(7, this.getAmountAccount());
            statement.setBigDecimal(8, this.getFee());
            statement.setBigDecimal(9, this.getBalance());
            statement.setDate(10, this.getCreatedAt());
            statement.setDate(11, this.getExecutedAt());
            statement.setInt(12, this.getId());

            int rowCount = statement.executeUpdate();
            if(rowCount == 0)
                throw new SQLException("No row was updated");
            else if(1 < rowCount)
                throw new SQLException("More than one row was updated");

            return this;
        }
    }

    public Transaction delete() throws SQLException {
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
