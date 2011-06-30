package com.iConomy.IO;

import com.iConomy.Constants;
import com.iConomy.IO.exceptions.MissingDriver;
import com.iConomy.iConomy;
import com.iConomy.util.Common;
import com.mini.Mini;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

public class Database {
    public static enum Type { MiniDB, InventoryDB, MySQL, SQLite, Postgre, H2DB };
    private String type;
    private String driver;
    private String url;
    private String username, password;

    public Database(String type, String url, String username, String password) throws MissingDriver {
        this.type = type;
        this.url = "jdbc:" + url;
        this.username = username;
        this.password = password;
        
        if(Common.matches(type, "flatfile", "ff", "mini", "minidb", "flat"))
            return;
        
        if(Common.matches(type, "item", "items", "inventory", "inventorydb"))
            return;
        
        if(Common.matches(type, "mysql", "mysqldb"))
            driver = "com.mysql.jdbc.Driver";
        
        if(Common.matches(type, "h2", "h2db", "h2sql"))
            driver = "org.h2.Driver";
        
        if(Common.matches(type, "postgresql", "postgre", "postgredb"))
            driver = "org.postgresql.Driver";
        
        if(Common.matches(type, "sqlite", "sqlite2", "sqlite3", "sqlitedb"))
            driver = "org.sqlite.JDBC";

        if(driver == null)
            return;

        if(!DbUtils.loadDriver(driver))
            throw new MissingDriver("Please make sure the " + type + " driver library jar exists.");
    }

    public Connection getConnection() throws SQLException {
        return (username.isEmpty() && password.isEmpty()) ? DriverManager.getConnection(url) : DriverManager.getConnection(url, username, password);
    }
    
    ResultSetHandler<Boolean> returnBoolean = new ResultSetHandler<Boolean>() {
        public Boolean handle(ResultSet rs) {
            try {
                rs.next();
            } catch (SQLException ex) {
                return false;
            }
            
            return true;
        }
    };

    public boolean tableExists(String table) {
        boolean exists = false;

        try {
            Connection conn = getConnection();
            QueryRunner run = new QueryRunner();

            try {
                String t = Constants.Nodes.DatabaseTable.toString();
                exists = run.query(conn, "SELECT id FROM " + table, returnBoolean);
            }  finally {
                DbUtils.close(conn);
            }
        } catch (SQLException e) {
            exists = false;
        }

        return exists;
    }

    public Type getType() {
        if(Common.matches(type, "flatfile", "ff", "mini", "minidb", "flat"))
            return Type.MiniDB;
        
        if(Common.matches(type, "item", "items", "inventory", "inventorydb"))
            return Type.InventoryDB;

        if(Common.matches(type, "mysql", "mysqldb"))
            return Type.MySQL;

        if(Common.matches(type, "h2", "h2db", "h2sql"))
            return Type.H2DB;

        if(Common.matches(type, "postgresql", "postgre", "postgredb"))
            return Type.Postgre;

        if(Common.matches(type, "sqlite", "sqlite2", "sqlite3", "sqlitedb"))
            return Type.SQLite;

        return Type.MiniDB;
    }

    public Mini getDatabase() {
        if(!Common.matches(type, "flatfile", "ff", "mini", "minidb", "flat"))
            return null;

        return new Mini(iConomy.directory.getPath(), "accounts.mini");
    }
    
    public InventoryDB getInventoryDatabase() {
        if(!Common.matches(type, "item", "items", "inventory", "inventorydb"))
            return null;
        
        return new InventoryDB();
    }
}