package com.iCo6.system;

import com.iCo6.Constants;
import com.iCo6.IO.InventoryDB;
import com.iCo6.iConomy;
import com.iCo6.util.Common;
import com.iCo6.IO.mini.Arguments;
import com.iCo6.IO.mini.Mini;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.iCo6.util.org.apache.commons.dbutils.DbUtils;
import com.iCo6.util.org.apache.commons.dbutils.QueryRunner;
import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;

class Queried {
    static Mini database;
    static InventoryDB inventory;

    static ResultSetHandler<String> returnName = new ResultSetHandler<String>() {
        public String handle(ResultSet rs) throws SQLException {
            if(rs.next())
                return rs.getString("name");

            return null;
        }
    };

    static ResultSetHandler<List<String>> returnList = new ResultSetHandler<List<String>>() {
        private List<String> accounts;
        public List<String> handle(ResultSet rs) throws SQLException {
            accounts = new ArrayList<String>();

            while(rs.next())
                accounts.add(rs.getString("username"));

            return accounts;
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

    static ResultSetHandler<Integer> returnStatus = new ResultSetHandler<Integer>() {
        public Integer handle(ResultSet rs) throws SQLException {
            if(rs.next())
                return rs.getInt("status");

            return null;
        }
    };

    static boolean useMiniDB() {
        if(iConomy.Database.getType().toString().equalsIgnoreCase("minidb")) {
            if(database == null)
                database = iConomy.Database.getDatabase();

            return true;
        }

        return false;
    }
    
    static boolean useInventoryDB() {
        if(iConomy.Database.getType().toString().equalsIgnoreCase("inventorydb")) {
            if(inventory == null)
                inventory = iConomy.Database.getInventoryDatabase();

            return true;
        }
        
        return false;
    }

    static List<String> accountList() {
        if(useMiniDB())
            return new ArrayList<String>(database.getIndices().keySet());
        
        if (useInventoryDB())
            return new ArrayList<String>(inventory.getAllPlayers());

        List<String> accounts = new ArrayList<String>();

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try{
                String t = Constants.Nodes.DatabaseTable.toString();
                accounts = run.query(c, "SELECT username FROM " + t, returnList);
            } catch (SQLException ex) {
                System.out.println("[iConomy] Error issueing SQL query: " + ex);
            } finally {
                DbUtils.close(c);
            }
        } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
        }

        return accounts;
    }

    static boolean createAccount(String name, Double balance, Integer status) {
        Boolean created = false;

        if(useMiniDB())
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
        
        if (useInventoryDB())
            if (!hasAccount(name)) {
                return false;
            } else {
                inventory.setBalance(name, balance);
                return true;
            }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try{
                String t = Constants.Nodes.DatabaseTable.toString();
                Integer amount = run.update(c, "INSERT INTO " + t + "(username, balance, status) values (?, ?, ?)", name.toLowerCase(), balance, status);

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
        
        if(useMiniDB()) {
            database.removeIndex(name);
            database.update();
            return true;
        }
        
        if (useInventoryDB()) {
            return false;
        }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try{
                String t = Constants.Nodes.DatabaseTable.toString();
                Integer amount = run.update(c, "DELETE FROM " + t + " WHERE username=?", name.toLowerCase());

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

        if (useMiniDB()) {
            return database.hasIndex(name);
        }
        
        if (useInventoryDB()) {
            return inventory.dataExists(name);
        }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try {
                String t = Constants.Nodes.DatabaseTable.toString();
                exists = run.query(c, "SELECT id FROM " + t + " WHERE username=?", returnBoolean, name.toLowerCase());
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

        if(useMiniDB())
            return database.getArguments(name).getDouble("balance");
        
        if(useInventoryDB()) {
            return inventory.getBalance(name);
        }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try{
                String t = Constants.Nodes.DatabaseTable.toString();
                balance = run.query(c, "SELECT balance FROM " + t + " WHERE username=?", returnBalance, name.toLowerCase());
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

        if(useMiniDB()) {
            database.setArgument(name, "balance", balance);
            database.update();
            return;
        }
        
        if (useInventoryDB()) {
            inventory.setBalance(name, balance);
            return;
        }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try{
                String t = Constants.Nodes.DatabaseTable.toString();
                int update = run.update(c, "UPDATE " + t + " SET balance=? WHERE username=?", balance, name.toLowerCase());
            } catch (SQLException ex) {
                System.out.println("[iConomy] Error issueing SQL query: " + ex);
            } finally {
                DbUtils.close(c);
            }
        } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
        }
    }

    static void doInterest(String query, LinkedHashMap<String, HashMap<String, Object>> queries) {
        Object[][] parameters = new Object[queries.size()][2];
        int i = 0;

        for(String name: queries.keySet()) {
            parameters[i][0] = queries.get(name).get("balance");
            parameters[i][1] = name;

            // Do Logging & Messaging here.
            /* if(amount < 0.0)
                iConomy.getTransactions().insert("[System Interest]", name, 0.0, original, 0.0, 0.0, amount);
            else {
                iConomy.getTransactions().insert("[System Interest]", name, 0.0, original, 0.0, amount, 0.0);
            } */
            i++;
        }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try{
                run.batch(c, query, parameters);
            } catch (SQLException ex) {
                System.out.println("[iConomy] Error with batching: " + ex);
            } finally {
                DbUtils.close(c);
            }
        } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
        }
    }
    
    static void purgeDatabase() {
        if(useMiniDB()) {
            for(String index: database.getIndices().keySet())
                if(database.getArguments(index).getDouble("balance") == Constants.Nodes.Balance.getDouble())
                    database.removeIndex(index);
            
            database.update();
        }
        
        if (useInventoryDB()) {
            // TODO: purge online accounts only?
            return;
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
        if(useMiniDB()) {
            for(String index: database.getIndices().keySet())
                database.removeIndex(index);

            database.update();
        }
        
        if (useInventoryDB()) {
            // TODO: clear all inventories of those items?
            // Unknown for now.
            return;
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

    static Integer getStatus(String name) {
        int status = 0;

        if(!hasAccount(name)) {
            return -1;
        }

        if(useMiniDB()) {
            return database.getArguments(name).getInteger("status");
        }

        if (useInventoryDB()) {
            return (inventory.dataExists(name)) ? 1 : 0;
        }

        try {
            QueryRunner run = new QueryRunner();
            Connection c = iConomy.Database.getConnection();

            try{
                String t = Constants.Nodes.DatabaseTable.toString();
                status = run.query(c, "SELECT status FROM " + t + " WHERE username=?", returnStatus, name.toLowerCase());
            } catch (SQLException ex) {
                System.out.println("[iConomy] Error issueing SQL query: " + ex);
            } finally {
                DbUtils.close(c);
            }
        } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
        }

        return status;
    }
}
