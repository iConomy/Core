package com.nijiko.coelho.iConomy.system;

import java.sql.ResultSet;
import java.text.DecimalFormat;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.util.Constants;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getDatabase().checkOut();
            ps = conn.prepareStatement("SELECT balance FROM " + Constants.SQL_Table + " WHERE username = ? LIMIT 1");
            ps.setString(1, this.name);
            rs = ps.executeQuery();

            if (rs != null) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to grab player balance: " + e);
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                iConomy.getDatabase().checkIn(conn);
        }

        return Constants.Initial_Balance;
    }

    public void setBalance(double balance) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getDatabase().checkOut();

            if(!iConomy.getBank().hasAccount(this.name)) {
                ps = conn.prepareStatement("INSERT INTO " + Constants.SQL_Table + "(username, balance) VALUES (?, ?)");
                ps.setString(1, this.name);
                ps.setDouble(2, balance);
            } else {
                ps = conn.prepareStatement("UPDATE " + Constants.SQL_Table + " SET balance = ? WHERE username = ?");
                ps.setDouble(1, balance);
                ps.setString(2, this.name);
            }

            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to set balance: " + e);
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                iConomy.getDatabase().checkIn(conn);
        }
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
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getDatabase().checkOut();
            ps = conn.prepareStatement("DELETE FROM `" + Constants.SQL_Table + "` WHERE username = ?");
            ps.setString(1, this.name);
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
