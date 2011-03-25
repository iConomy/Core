package com.nijiko.coelho.iConomy.net;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.nijiko.coelho.iConomy.util.Constants;
import com.nijiko.coelho.iConomy.util.Misc;
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
            if(Misc.is(Constants.Database_Type, new String[] { "sqlite", "h2", "h2sql" })) {
                Pool = new ConnectionPool(
                    "org.h2.Driver",
                    "jdbc:h2:" + Constants.Plugin_Directory + File.separator + Constants.SQL_Database + ";AUTO_RECONNECT=TRUE;FILE_LOCK=SERIALIZED",
                    "sa",
                    "sa"
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
