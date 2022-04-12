package application.rdg;

import application.DbContext;

import java.sql.*;

public class Currency {
    private Integer id;
    private String code;

    private final static String insertSql = "INSERT INTO \"currency\" (code) VALUES (?)";
    private final static String updateSql = "UPDATE \"currency\" SET code = ? WHERE id = ?";
    private final static String deleteSql = "DELETE FROM \"currency\" WHERE id = ?";

    public int getId() {
        return id;
    }

    public Currency setId(int id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Currency setCode(String code) {
        this.code = code;
        return this;
    }

    public Currency insert() throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, this.getCode());
            statement.executeUpdate();

            try (ResultSet r = statement.getGeneratedKeys()) {
                r.next();
                id = r.getInt(1);
            }

            return this;
        }
    }

    public Currency update() throws SQLException {
        if (id == null)
            throw new IllegalStateException("Column id is not set");

        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(updateSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, this.getCode());
            statement.setInt(2, this.getId());

            int rowCount = statement.executeUpdate();
            if(rowCount == 0)
                throw new SQLException("No row was updated");
            else if(1 < rowCount)
                throw new SQLException("More than one row was updated");

            return this;
        }
    }

    public Currency delete() throws SQLException {
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
