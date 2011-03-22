package com.nijiko.coelho.iConomy.net;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.nijiko.coelho.iConomy.util.Constants;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    private Connection Connection;
    private Statement Stmt;
    private PreparedStatement Statement;
    private ResultSet ResultSet;
    private ConnectionPool Pool = null;

    public Database() {
        if(Pool == null) {
            if (Constants.Database_Type.equalsIgnoreCase("sqlite")) {
                Pool = new ConnectionPool(
                    "org.sqlite.JDBC",
                    "jdbc:sqlite:" + Constants.Plugin_Directory + File.separator + Constants.SQL_Database + ".sqlite",
                    "",
                    ""
                );
            } else if (Constants.Database_Type.equalsIgnoreCase("mysql")) {
                Pool = new ConnectionPool(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://" + Constants.SQL_Hostname + ":" + Constants.SQL_Port + "/" + Constants.SQL_Database,
                    Constants.SQL_Username,
                    Constants.SQL_Password
                );
            }
        }
    }

    public Connection checkOut() {
        return Pool.checkOut();
    }

    public void checkIn(Connection connection) {
        this.Pool.checkIn(connection);
    }
}
