package application.rdg;

import application.DbContext;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Statement {
    private Integer id;
    private Integer userId;
    private String textDocument;
    private Date createdAt;

    private final static String insertSql = "INSERT INTO \"statement\" (user_id, text_document, created_at) VALUES (?, ?, ?)";


    public int getId() {
        return id;
    }

    public Statement setId(int id) {
        this.id = id;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public Statement setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public String getTextDocument() {
        return textDocument;
    }

    public Statement setTextDocument(String textDocument) {
        this.textDocument = textDocument;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Statement setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Statement insert() throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(insertSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.getUserId());
            statement.setString(2, this.getTextDocument());
            statement.setDate(3, this.getCreatedAt());
            statement.executeUpdate();

            try (ResultSet r = statement.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1);
            }

            return this;
        }
    }
}
