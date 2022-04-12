package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbContext {
    private record ConnectionCreds(String url, String user, String password) {
    }

    private static ConnectionCreds testConnCreds = new ConnectionCreds(
            "jdbc:postgresql://localhost/postgres",
            "postgres",
            "admin"
    );
    private static ConnectionCreds prodConnCreds = new ConnectionCreds(
            "jdbc:postgresql://db.dai.fmph.uniba.sk/playground",
            "sidlo4@uniba.sk",
            "admin123"
    );
    private static ConnectionCreds connCreds = Main.isProduction ? prodConnCreds : testConnCreds;

    private static DbContext instance = null;
    private Connection conn;

    private DbContext(ConnectionCreds connectionCreds) throws SQLException {
        conn = DriverManager.getConnection(connectionCreds.url(), connectionCreds.user(), connectionCreds.password());
    }

    public static Connection getConnection() throws SQLException {
        if(instance == null) {
            instance = new DbContext(connCreds);
        }

        return instance.conn;
    }
}
