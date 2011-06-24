package com.iConomy.system;

import com.iConomy.Constants;
import com.iConomy.iConomy;
import com.iConomy.util.Common;
import com.mini.Arguments;
import com.mini.Mini;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

import org.apache.commons.dbutils.ResultSetHandler;

class Queried {
    static Mini database;

    static ResultSetHandler<String> returnName = new ResultSetHandler<String>() {
        public String handle(ResultSet rs) throws SQLException {
            if(rs.next())
                return rs.getString("name");

            return null;
        }
    };

    static ResultSetHandler<Boolean> returnBoolean = new ResultSetHandler<Boolean>() {
        public Boolean handle(ResultSet rs) throws SQLException {
            return rs.next();
        }
    };

    static ResultSetHandler<Double> returnBalance = new ResultSetHandler<Double>() {
        public Double handle(ResultSet rs) throws SQLException {
            if(rs.next())
                return rs.getDouble("balance");

            return null;
        }
    };

    static boolean useSQL() {
        String type = Constants.Nodes.DatabaseType.toString();

        if(Common.matches(type, "mini", "minidb", "flatfile", "file", "ff")) {
            if(database == null)
                database = iConomy.Database.getDatabase();

            return false;
        }

        return true;
    }

    static boolean createAccount(String name, Double balance, Integer status) {
        Boolean created = false;

        if(!useSQL())
            if(!hasAccount(name)) {
                Arguments Row = new Arguments(name);
                Row.setValue("balance", balance);
                Row.setValue("status", status);

                database.addIndex(Row.getKey(), Row);
                database.update();
                return true;
            } else {
                database.setArgument(name, "balance", balance);
                database.update();
                return true;
            }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try{
                String t = Constants.Nodes.DatabaseTable.toString();
                Integer amount = run.update(c, "INSERT INTO " + t + "(username, balance, status) values (?, ?, ?)", name, balance, status);

                if(amount > 0)
                    created = true;
            } catch (SQLException ex) {
                System.out.println("[iConomy] Error issueing SQL query: " + ex);
            } finally {
                DbUtils.close(c);
            }
        } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
        }

        return false;
    }

    static boolean removeAccount(String name) {
        Boolean removed = false;
        
        if(!useSQL()) {
            database.removeIndex(name);
            database.update();
            return true;
        }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try{
                String t = Constants.Nodes.DatabaseTable.toString();
                Integer amount = run.update(c, "DELETE FROM " + t + " WHERE username=?", name);

                if(amount > 0)
                    removed = true;
            } catch (SQLException ex) {
                System.out.println("[iConomy] Error issueing SQL query: " + ex);
            } finally {
                DbUtils.close(c);
            }
        } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
        }

        return removed;
    }

    static boolean hasAccount(String name) {
        Boolean exists = false;

        if (!useSQL()) {
            return database.hasIndex(name);
        }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try {
                String t = Constants.Nodes.DatabaseTable.toString();
                exists = run.query(c, "SELECT id FROM " + t + " WHERE username=?", returnBoolean, name);
            } catch (SQLException ex) {
                System.out.println("[iConomy] Error issueing SQL query: " + ex);
            } finally {
                DbUtils.close(c);
            }
        } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
        }

        return exists;
    }

    static double getBalance(String name) {
        Double balance = Constants.Nodes.Balance.getDouble();

        if(!hasAccount(name))
            return balance;

        if(!useSQL())
            return database.getArguments(name).getDouble("balance");

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try{
                String t = Constants.Nodes.DatabaseTable.toString();
                balance = run.query(c, "SELECT balance FROM " + t + " WHERE username=?", returnBalance, name);
            } catch (SQLException ex) {
                System.out.println("[iConomy] Error issueing SQL query: " + ex);
            } finally {
                DbUtils.close(c);
            }
        } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
        }

        return balance;
    }

    static void setBalance(String name, double balance) {
        if(!hasAccount(name)) {
            createAccount(name, balance, 0);
            return;
        }

        if(!useSQL()) {
            database.setArgument(name, "balance", balance);
            database.update();
            return;
        }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try{
                String t = Constants.Nodes.DatabaseTable.toString();
                int update = run.update(c, "UPDATE " + t + " SET balance=? WHERE username=?", balance, name);
            } catch (SQLException ex) {
                System.out.println("[iConomy] Error issueing SQL query: " + ex);
            } finally {
                DbUtils.close(c);
            }
        } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
        }
    }
    
    static void purgeDatabase() {
        if(!useSQL()) {
            for(String index: database.getIndices().keySet())
                if(database.getArguments(index).getDouble("balance") == Constants.Nodes.Balance.getDouble())
                    database.removeIndex(index);
            
            database.update();
        }
        
        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try {
                String t = Constants.Nodes.DatabaseTable.toString();
                Integer amount = run.update(c, "DELETE FROM " + t + " WHERE balance=?", Constants.Nodes.Balance.getDouble());
            } catch (SQLException ex) {
                System.out.println("[iConomy] Error issueing SQL query: " + ex);
            } finally {
                DbUtils.close(c);
            }
        } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
        }
    }

    static void emptyDatabase() {
        if(!useSQL()) {
            for(String index: database.getIndices().keySet())
                database.removeIndex(index);

            database.update();
        }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try {
                String t = Constants.Nodes.DatabaseTable.toString();
                Integer amount = run.update(c, "TRUNCATE TABLE " + t);
            } catch (SQLException ex) {
                System.out.println("[iConomy] Error issueing SQL query: " + ex);
            } finally {
                DbUtils.close(c);
            }
        } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
        }
    }
}
