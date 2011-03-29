package com.nijiko.coelho.iConomy.system;

import com.nijiko.coelho.iConomy.events.AccountRemoveEvent;
import com.nijiko.coelho.iConomy.events.AccountResetEvent;
import com.nijiko.coelho.iConomy.events.AccountSetEvent;
import com.nijiko.coelho.iConomy.events.AccountUpdateEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.util.Constants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Account {
    private String name;

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
            conn = iConomy.getDatabase().getConnection();
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
                iConomy.getDatabase().close(conn);
        }

        return Constants.Initial_Balance;
    }

    public void setBalance(double balance) {
        AccountSetEvent Event = new AccountSetEvent(name, balance);
        iConomy.getBukkitServer().getPluginManager().callEvent(Event);

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getDatabase().getConnection();

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
                iConomy.getDatabase().close(conn);
        }
    }

    public boolean setHidden(boolean hidden) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getDatabase().getConnection();

            if(!iConomy.getBank().hasAccount(this.name)) {
                return false;
            } else {
                ps = conn.prepareStatement("UPDATE " + Constants.SQL_Table + " SET hidden = ? WHERE username = ?");
                ps.setBoolean(1, hidden);
                ps.setString(2, this.name);
            }

            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to set balance: " + e);
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                iConomy.getDatabase().close(conn);
        }

        return true;
    }

    public void resetBalance() {
        AccountResetEvent Event = new AccountResetEvent(name);
        iConomy.getBukkitServer().getPluginManager().callEvent(Event);

        if(!Event.isCancelled())
            this.setBalance(Constants.Initial_Balance);
    }

    public boolean hasEnough(double amount) {
        return amount <= this.getBalance();
    }

    public boolean hasOver(double amount) {
        return amount < this.getBalance();
    }

    public boolean isHidden() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getDatabase().getConnection();
            ps = conn.prepareStatement("SELECT hidden FROM " + Constants.SQL_Table + " WHERE username = ? LIMIT 1");
            ps.setString(1, this.name);
            rs = ps.executeQuery();

            if (rs != null) {
                if (rs.next()) {
                    return rs.getBoolean("hidden");
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
                iConomy.getDatabase().close(conn);
        }

        return false;
    }

    public boolean isNegative() {
        return this.getBalance() < 0.0;
    }

    public void add(double amount) {
        double balance = this.getBalance();
        double ending = (balance + amount);

        AccountUpdateEvent Event = new AccountUpdateEvent(name, balance, ending, amount);
        iConomy.getBukkitServer().getPluginManager().callEvent(Event);

        if(!Event.isCancelled())
            this.setBalance(ending);
    }

    public void multiply(double amount) {
        double balance = this.getBalance();
        double ending = (balance * amount);

        AccountUpdateEvent Event = new AccountUpdateEvent(name, balance, ending, amount);
        iConomy.getBukkitServer().getPluginManager().callEvent(Event);

        if(!Event.isCancelled())
            this.setBalance(ending);
    }

    public void divide(double amount) {
        double balance = this.getBalance();
        double ending = (balance / amount);

        AccountUpdateEvent Event = new AccountUpdateEvent(name, balance, ending, amount);
        iConomy.getBukkitServer().getPluginManager().callEvent(Event);

        if(!Event.isCancelled())
            this.setBalance(ending);
    }

    public void subtract(double amount) {
        double balance = this.getBalance();
        double ending = (balance - amount);

        AccountUpdateEvent Event = new AccountUpdateEvent(name, balance, ending, amount);
        iConomy.getBukkitServer().getPluginManager().callEvent(Event);

        if(!Event.isCancelled())
            this.setBalance(ending);
    }

    public void remove() {
        AccountRemoveEvent Event = new AccountRemoveEvent(name);
        iConomy.getBukkitServer().getPluginManager().callEvent(Event);
        
        if(!Event.isCancelled()) {
            Connection conn = null;
            PreparedStatement ps = null;

            try {
                conn = iConomy.getDatabase().getConnection();
                ps = conn.prepareStatement("DELETE FROM `" + Constants.SQL_Table + "` WHERE username = ?");
                ps.setString(1, this.name);
                ps.executeUpdate();
            } catch(Exception e) {
                System.out.println("[iConomy] Failed to remove account: " + e);
            } finally {
                if(ps != null)
                    try { ps.close(); } catch (SQLException ex) { }

                if(conn != null)
                    iConomy.getDatabase().close(conn);
            }
        }
    }

    @Deprecated
    public void save() { }

    @Override
    public String toString() {
        DecimalFormat formatter = new DecimalFormat("#,##0.##");
        Double balance = this.getBalance();
        String formatted = formatter.format(balance);
        
        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted + " " + ((balance <= 1 && balance >= -1) ? Constants.Currency : Constants.Currency_Plural);
    }
}
