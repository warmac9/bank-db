package application.rdg;

import application.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class User {
    private Integer id;
    private String fullName;
    private String email;
    private String idNumber;
    private boolean isDeactivated;

    private final static String insertSql = "INSERT INTO \"user\" (full_name, email, id_number, is_deactivated) VALUES (?, ?, ?, ?)";
    private final static String updateSql = "UPDATE \"user\" SET full_name = ?, email = ?, id_number = ?, is_deactivated = ? WHERE id = ?";
    private final static String deleteSql = "DELETE FROM \"user\" WHERE id = ?";

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public User setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public User setIdNumber(String idNumber) {
        this.idNumber = idNumber;
        return this;
    }

    public boolean getIsDeactivated() {
        return isDeactivated;
    }

    public User setIsDeactivated(boolean isDeactivated) {
        this.isDeactivated = isDeactivated;
        return this;
    }

    public User insert() throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, this.getFullName());
            statement.setString(2, this.getEmail());
            statement.setString(3, this.getIdNumber());
            statement.setBoolean(4, this.getIsDeactivated());
            statement.executeUpdate();

            try (ResultSet r = statement.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1);
            }

            return this;
        }
    }

    public User update() throws SQLException {
        if (id == null)
            throw new IllegalStateException("Column id is not set");

        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(updateSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, this.getFullName());
            statement.setString(2, this.getEmail());
            statement.setString(3, this.getIdNumber());
            statement.setBoolean(4, this.getIsDeactivated());
            statement.setInt(5, this.getId());

            int rowCount = statement.executeUpdate();
            if(rowCount == 0)
                throw new SQLException("No row was updated");
            else if(1 < rowCount)
                throw new SQLException("More than one row was updated");

            return this;
        }
    }

    public User delete() throws SQLException {
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
