package com.nijiko.coelho.iConomy.system;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.util.Constants;
import com.nijiko.coelho.iConomy.util.Misc;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Bank {
    private double initial;

    /**
     * Loads all bank accounts into a hashmap.
     *
     * @throws Exception
     */
    public void load() throws Exception {
        this.initial = Constants.Initial_Balance;

        Connection conn = iConomy.getDatabase().checkOut();
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (Misc.is(Constants.Database_Type, new String[] { "sqlite", "h2", "h2sql" })) {
            try {
                ps = conn.prepareStatement("CREATE TABLE " + Constants.SQL_Table + "(id INT auto_increment PRIMARY KEY, username VARCHAR(32), balance DECIMAL (65, 2));");
                ps.executeUpdate();
            } catch(SQLException E) { }
        } else {
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, Constants.SQL_Table, null);

            if (!rs.next()) {
                if (Constants.Database_Type.equalsIgnoreCase("mysql")) {
                    ps = conn.prepareStatement("CREATE TABLE " + Constants.SQL_Table + " (`id` INT(10) NOT NULL AUTO_INCREMENT, `username` TEXT NOT NULL, `balance` DECIMAL(65, 2) NOT NULL, PRIMARY KEY (`id`))");
                } else if (Misc.is(Constants.Database_Type, new String[] { "sqlite", "h2", "h2sql" })) {
                    ps = conn.prepareStatement("CREATE TABLE " + Constants.SQL_Table + "(id INT auto_increment PRIMARY KEY, username VARCHAR(32), balance DECIMAL (65, 2));");
                }

                if(ps != null) {
                    ps.executeUpdate();
                }
            }
        }

        if(ps != null)
            try { ps.close(); } catch (SQLException ex) { }

        if(rs != null)
            try { rs.close(); } catch (SQLException ex) { }

        if(conn != null)
            iConomy.getDatabase().checkIn(conn);
    }

    /**
     * Formats the balance in a human readable form with the currency attached:<br /><br />
     * 20000.53 = 20,000.53 Coin<br />
     * 20000.00 = 20,000 Coin
     *
     * @param account The name of the account you wish to be formatted
     * @return String
     */
    public String format(String account) {
        return this.getAccount(account).toString();
    }

    /**
     * Formats the money in a human readable form with the currency attached:<br /><br />
     * 20000.53 = 20,000.53 Coin<br />
     * 20000.00 = 20,000 Coin
     *
     * @param amount double
     * @return String
     */
    public String format(double amount) {
        DecimalFormat formatter = new DecimalFormat("#"+Constants.GroupSeperator+"##0"+Constants.DecimalSeperator+"##");
        String formatted = formatter.format(amount);
        String currency = "";

        if (formatted.endsWith(String.valueOf(Constants.DecimalSeperator))) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted + " " + ((amount <= 1 && amount >= -1) ? Constants.Currency : Constants.Currency_Plural);
    }

    /**
     * Does the bank have record of the account in question?
     *
     * @param account The account in question
     * @return boolean - Does the account exist?
     */
    public boolean hasAccount(String account) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getDatabase().checkOut();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQL_Table + " WHERE username = ? LIMIT 1");
            ps.setString(1, account);
            rs = ps.executeQuery();

            return (rs.next());
        } catch (Exception e) {
            return false;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                iConomy.getDatabase().checkIn(conn);
        }
    }

    public HashMap<String, Double> getAccounts() {
        HashMap<String, Double> accounts = new HashMap<String, Double>();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getDatabase().checkOut();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQL_Table + " ORDER BY balance DESC");
            rs = ps.executeQuery();

            while (rs.next()) {
                accounts.put(rs.getString("username"), rs.getDouble("balance"));
            }
        } catch (Exception e) {
            return accounts;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                iConomy.getDatabase().checkIn(conn);
        }

        return accounts;
    }

    /**
     * Fetch the account, Does not check for existance.
     * Do that prior to using this to prevent null errors or any other issues.
     *
     * @param account The account to grab
     * @return Account - Child object of bank
     */
    public Account getAccount(String account) {
        Account Account = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        
        try {
            conn = iConomy.getDatabase().checkOut();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQL_Table + " WHERE username = ? LIMIT 1");
            ps.setString(1, account);
            rs = ps.executeQuery();

            if(rs.next()) {
                Account = new Account(account);
            }
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to grab account " + e);
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                iConomy.getDatabase().checkIn(conn);
        }

        return Account;
    }

    /**
     * Get the current currency name.
     *
     * @return String - Currency name
     */
    public String getCurrency() {
        return Constants.Currency;
    }

    /**
     * Get the initial balance amount upon creation of new accounts.
     *
     * @return double
     */
    public double getInitial() {
        return initial;
    }

    /**
     *  Grabs the account ranks based on input amount
     *
     * @param output
     * @return Arraylist of account names
     */
    public ArrayList<String> getAccountRanks(int output) {
        ArrayList<String> players = new ArrayList<String>();

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getDatabase().checkOut();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQL_Table + " ORDER BY balance DESC LIMIT " + output);
            rs = ps.executeQuery();

            for (int i = 0; i < output; i++) {
                if (rs.next()) {
                    players.add(rs.getString("username"));
                } else {
                    break;
                }
            }

            return players;
        } catch (Exception e) {
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                iConomy.getDatabase().checkIn(conn);
        }

        return new ArrayList<String>();
    }

    /**
     * Returns the ranking number of an account
     *
     * @param name
     * @return Integer
     */
    public int getAccountRank(String name) {
        int i = 1;
        
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        
        try {
            conn = iConomy.getDatabase().checkOut();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQL_Table + " ORDER BY balance DESC");
            rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getString("username").equalsIgnoreCase(name)) {
                    return i;
                } else {
                    i++;
                }
            }
        } catch (Exception e) {
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                iConomy.getDatabase().checkIn(conn);
        }

        return -1;
    }

    /**
     * Set the currency name, only do so if the server owner is knowingly allowing you to do this.
     *
     * @param currency
     */
    public void setCurrency(String currency) {
        Constants.Currency = currency;
    }

    /**
     * Changes the initial balance amount upon account creation.
     *
     * @param initial
     */
    public void setInitial(double initial) {
        this.initial = initial;
    }

    /**
     * Add an account to the bank, if it already exists it updates the balance.
     * This does not utilize the initial balance setting. If you want to do that make sure you
     * grab the initial balance and put it as the second parameter.
     *
     * @param account
     * @param balance
     */
    public void addAccount(String account) {
        if (!this.hasAccount(account)) {
            Account initialized = new Account(account);
            initialized.setBalance(this.initial);
        } else {
            getAccount(account).setBalance(this.initial);
        }
    }

    /**
     * Add an account to the bank, if it already exists it updates the balance.
     * This does not utilize the initial balance setting. If you want to do that make sure you
     * grab the initial balance and put it as the second parameter.
     *
     * @param account
     * @param balance
     */
    public void addAccount(String account, double balance) {
        if (!this.hasAccount(account)) {
            Account initialized = new Account(account);
            initialized.setBalance(balance);
        } else {
            getAccount(account).setBalance(balance);
        }
    }

    /**
     * Update an account or create one if it doesn't exist.
     *
     * @param account
     * @param amount
     */
    public void updateAccount(String account, double amount) {
        if (this.hasAccount(account)) {
            Account updating = getAccount(account);
            updating.setBalance(amount);
        } else {
            addAccount(account, amount);
        }
    }

    /**
     * Reset account or create a new one if it doesn't exist.
     *
     * @param account
     */
    public void resetAccount(String account) {
        if (this.hasAccount(account)) {
            Account updating = getAccount(account);
            updating.setBalance(initial);
        } else {
            addAccount(account, initial);
        }
    }

    /**
     * Completely remove an account from the bank and the database.
     *
     * @param account
     */
    public void removeAccount(String account) {
        if(hasAccount(account)) {
            Connection conn = null;
            PreparedStatement ps = null;

            try {
                conn = iConomy.getDatabase().checkOut();
                ps = conn.prepareStatement("DELETE FROM `" + Constants.SQL_Table + "` WHERE username = ?");
                ps.setString(1, account);
                ps.executeUpdate();
            } catch(Exception e) {
                System.out.println("[iConomy] Failed to remove account: " + e);
            } finally {
                if(ps != null)
                    try { ps.close(); } catch (SQLException ex) { }

                if(conn != null)
                    iConomy.getDatabase().checkIn(conn);
            }
        }
    }
}
