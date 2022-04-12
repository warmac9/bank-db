package application.rdg;

import application.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyFinder {
    private final static String findByCodeSql = "SELECT * FROM \"currency\" WHERE code = ?";

    private static CurrencyFinder instance;

    public static CurrencyFinder getInstance() {
        if(instance == null)
            instance = new CurrencyFinder();
        return instance;
    }

    public Currency findByCode(String code) throws SQLException {
        try(PreparedStatement statement = DbContext.getConnection().prepareStatement(findByCodeSql)) {
            statement.setString(1, code);

            try(ResultSet res = statement.executeQuery()) {
                if(!res.next())
                    return null;

                Currency currency = new Currency()
                        .setId(res.getInt("id"))
                        .setCode(res.getString("code"));

                if (res.next())
                    throw new RuntimeException("More than one row was returned");

                return currency;
            }
        }
    }
}
