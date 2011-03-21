package com.nijiko.coelho.iConomy.system;

import java.sql.ResultSet;
import java.text.DecimalFormat;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.util.Constants;
import java.sql.SQLException;

public class Account {
    private String name;
    private boolean altered = false;
    private boolean exists = false;

    public Account(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        ResultSet rs = null;

        try {
            rs = iConomy.getDatabase().resultQuery(
                "SELECT balance FROM " + Constants.SQL_Table + " WHERE username = ? LIMIT 1",
                new Object[]{ this.name }
            );

            if (rs != null)
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to grab player balance: " + e);
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) { }
            }

            iConomy.getDatabase().close();
        }

        return Constants.Initial_Balance;
    }

    public void setBalance(double balance) {
        ResultSet rs = null;
        boolean hasAccount = false;

        try {
            rs = iConomy.getDatabase().resultQuery(
                "SELECT * FROM " + Constants.SQL_Table + " WHERE username = ? LIMIT 1",
                new Object[]{ this.name }
            );

            hasAccount = rs.next();
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to set balance: " + e);
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) { }
            }
        }

        iConomy.getDatabase().close();

        try {
            if (!hasAccount) {
                iConomy.getDatabase().executeQuery(
                    "INSERT INTO " + Constants.SQL_Table + "(username, balance) VALUES (?, ?)",
                    new Object[]{ this.name, balance }
                );
            } else {
                iConomy.getDatabase().executeQuery(
                    "UPDATE " + Constants.SQL_Table + " SET balance = ? WHERE username = ?",
                    new Object[]{ balance, this.name }
                );
            }
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to set balance: " + e);
        }

        iConomy.getDatabase().close();
    }

    public void resetBalance() {
        this.setBalance(Constants.Initial_Balance);
    }

    public boolean hasEnough(double amount) {
        return amount <= this.getBalance();
    }

    public boolean hasOver(double amount) {
        return amount < this.getBalance();
    }

    public boolean isNegative() {
        return this.getBalance() < 0.0;
    }

    public void add(double amount) {
        this.setBalance(this.getBalance() + amount);
    }

    public void multiply(double amount) {
        this.setBalance(this.getBalance() * amount);
    }

    public void divide(double amount) {
        this.setBalance(this.getBalance() / amount);
    }

    public void subtract(double amount) {
        this.setBalance(this.getBalance() - amount);
    }

    public void remove() {
        ResultSet rs = null;
        boolean hasAccount = false;

        try {
            rs = iConomy.getDatabase().resultQuery(
                    "SELECT * FROM " + Constants.SQL_Table + " WHERE username = ?",
                    new Object[]{ this.name }
            );

            hasAccount = rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) { }
            }
        }

        iConomy.getDatabase().close();

        try {
            if (hasAccount) {
                iConomy.getDatabase().executeQuery(
                        "DELETE FROM " + Constants.SQL_Table + " WHERE username = ?",
                        new Object[]{ this.name }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        iConomy.getDatabase().close();
    }

    @Deprecated
    public void save() { }

    @Override
    public String toString() {
        DecimalFormat formatter = new DecimalFormat("#,##0.##");
        String formatted = formatter.format(this.getBalance());

        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted;
    }
}
