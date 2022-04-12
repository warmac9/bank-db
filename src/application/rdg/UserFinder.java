package application.rdg;

import application.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserFinder {
    private final static String findByIdSql = "SELECT * FROM \"user\" WHERE id = ?";
    private final static String findByIdNumberSql = "SELECT * FROM \"user\" WHERE id_number = ?";
    private final static String findAllSql = "SELECT * FROM \"user\"";

    private static UserFinder instance;

    public static UserFinder getInstance() {
        if(instance == null)
            instance = new UserFinder();
        return instance;
    }

    public User findById(int id) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findByIdSql)) {
            statement.setInt(1, id);

            try(ResultSet res = statement.executeQuery()) {
                if(!res.next())
                    return null;

                User user = new User()
                        .setId(res.getInt("id"))
                        .setFullName(res.getString("full_name"))
                        .setEmail(res.getString("email"))
                        .setIdNumber(res.getString("id_number"))
                        .setIsDeactivated(res.getBoolean("is_deactivated"));

                if (res.next())
                    throw new RuntimeException("More than one row was returned");

                return user;
            }
        }
    }

    public User findByIdNumber(String idNumber) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findByIdNumberSql)) {
            statement.setString(1, idNumber);

            try(ResultSet res = statement.executeQuery()) {
                if(!res.next())
                    return null;

                User user = new User()
                        .setId(res.getInt("id"))
                        .setFullName(res.getString("full_name"))
                        .setEmail(res.getString("email"))
                        .setIdNumber(res.getString("id_number"))
                        .setIsDeactivated(res.getBoolean("is_deactivated"));

                if (res.next())
                    throw new RuntimeException("More than one row was returned");

                return user;
            }
        }
    }

    public List<User> findAll() throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findAllSql)) {
            List<User> list = new ArrayList<>();

            try(ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    User user = new User()
                            .setId(res.getInt("id"))
                            .setFullName(res.getString("full_name"))
                            .setEmail(res.getString("email"))
                            .setIdNumber(res.getString("id_number"))
                            .setIsDeactivated(res.getBoolean("is_deactivated"));
                    list.add(user);
                }

                return list;
            }
        }
    }
}
