package com.nsoz.lib;

import java.sql.Connection;
import java.sql.SQLException;
import com.nsoz.db.jdbc.DbManager;

public class ZConnection {

    private Connection connection;
    private int timeOut;

    public ZConnection(int timeOut) {
        this.timeOut = timeOut;
    }

    public Connection getConnection() {
        try {
            if (connection != null) {
                if (!connection.isValid(timeOut)) {
                    connection.close();
                }
            }
            if (connection == null || connection.isClosed()) {
                connection = DbManager.getInstance().getConnection();
                return getConnection();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }
}
