package com.nijiko.coelho.iConomy.net;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import com.nijiko.coelho.iConomy.util.Constants;
import com.nijiko.coelho.iConomy.util.Misc;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcConnectionPool;

public class Database {
    private JdbcConnectionPool h2pool;
    private String driver;
    private String dsn;
    private String username;
    private String password;

    public Database() {
        if(Misc.is(Constants.Database_Type, new String[] { "sqlite", "h2", "h2sql", "h2db" })) {
            driver = "org.h2.Driver";
            dsn = "jdbc:h2:" + Constants.Plugin_Directory + File.separator + Constants.SQL_Database + ";AUTO_RECONNECT=TRUE";
            username = "sa";
            password = "sa";
        } else if (Constants.Database_Type.equalsIgnoreCase("mysql")) {
            driver = "com.mysql.jdbc.Driver";
            dsn = "jdbc:mysql://" + Constants.SQL_Hostname + ":" + Constants.SQL_Port + "/" + Constants.SQL_Database;
            username = Constants.SQL_Username;
            password = Constants.SQL_Password;
        }

        try {
            Class.forName(driver).newInstance();
        } catch (Exception e) { System.out.println("[iConomy] Driver error: " + e); }

        if(Misc.is(Constants.Database_Type, new String[] { "sqlite", "h2", "h2sql", "h2db" })) {
            if(h2pool == null) {
                h2pool = JdbcConnectionPool.create(dsn, username, password);
            }
        }
    }

    public Connection checkOut() {
        try {
            if(username.equalsIgnoreCase("") && password.equalsIgnoreCase(""))
                return (DriverManager.getConnection(dsn));
            else {
                if(Misc.is(Constants.Database_Type, new String[] { "sqlite", "h2", "h2sql", "h2db" })) {
                    return h2pool.getConnection();
                } else {
                    return (DriverManager.getConnection(dsn, username, password));
                }
            }
        } catch (SQLException e) {
            System.out.println("[iConomy] Could not create connection: " + e);
            return (null);
        }
    }

    public void checkIn(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ex) {}
    }

    public JdbcConnectionPool connectionPool() {
        return h2pool;
    }
}
