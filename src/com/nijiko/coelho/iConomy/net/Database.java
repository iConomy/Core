package com.nijiko.coelho.iConomy.net;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.nijiko.coelho.iConomy.util.Constants;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private Connection Connection;
    private Statement Stmt;
    private PreparedStatement Statement;
    private ResultSet ResultSet;

    public Database() { }

    public void initialize() {
        try {
            connection();
        } catch (SQLException ex) {
            System.out.println("[iConomy] Failed to connect: " + ex);
        } catch (ClassNotFoundException e) {
            System.out.println("[iConomy] Connector not found: " + e);
        }
    }

    public Connection connection() throws ClassNotFoundException, SQLException {
        if (Constants.Database_Type.equalsIgnoreCase("sqlite")) {
            Class.forName("org.sqlite.JDBC");
            this.Connection = DriverManager.getConnection("jdbc:sqlite:" + Constants.Plugin_Directory + File.separator + Constants.SQL_Database + ".sqlite");
        } else if (Constants.Database_Type.equalsIgnoreCase("mysql")) {
            Class.forName("com.mysql.jdbc.Driver");
            this.Connection = DriverManager.getConnection("jdbc:mysql://" + Constants.SQL_Hostname + ":" + Constants.SQL_Port + "/" + Constants.SQL_Database, Constants.SQL_Username, Constants.SQL_Password);
        }

        return this.Connection;
    }

    public ResultSet resultQuery(String query) {
        initialize();

        try {
            this.Statement = this.Connection.prepareStatement(query);
            return this.Statement.executeQuery();
        } catch (Exception e) {
            return null;
        }
    }

    public ResultSet resultQuery(String query, Object[] parameters) {
        initialize();

        try {
            this.Statement = this.Connection.prepareStatement(query);
            int i = 1;

            for (Object obj : parameters) {
                this.Statement.setObject(i, obj);
                i++;
            }

            return this.Statement.executeQuery();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean executeQuery(String query) {
        initialize();

        try {
            this.Statement = this.Connection.prepareStatement(query);
            this.Statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println("[iConomy] Could not execute query: " + ex);
        }

        return false;
    }

    public boolean executeQuery(String query, Object[] parameters) {
        initialize();

        try {
            this.Statement = this.Connection.prepareStatement(query);
            int i = 1;

            for (Object obj : parameters) {
                this.Statement.setObject(i, obj);
                i++;
            }

            this.Statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println("[iConomy] Could not execute query: " + ex);
        }

        return false;
    }

    public Connection getConnection() {
        initialize();

        return this.Connection;
    }

    public void close() {
        try {
            if (this.Statement != null) {
                this.Statement.close();
            }

            if (this.ResultSet != null) {
                this.ResultSet.close();
            }

            if (this.Connection != null) {
                this.Connection.close();
            }

        } catch (SQLException ex) {
            System.out.println("[iConomy] Failed to close connection: " + ex);

            // Close anyway.
            this.Connection = null;
            this.Statement = null;
            this.ResultSet = null;
        }
    }

    protected void finalize() {
        close();
    }
}
