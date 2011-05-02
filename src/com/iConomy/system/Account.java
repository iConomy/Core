package com.iConomy.system;

import com.iConomy.events.AccountRemoveEvent;
import java.sql.ResultSet;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Account {
    private String name;

    public Account(String name) {
        this.name = name;
    }

    public int getId() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int id = -1;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + " WHERE username = ? LIMIT 1");
            ps.setString(1, name);
            rs = ps.executeQuery();

            if(rs.next()) {
                id = rs.getInt("id");
            }
        } catch (Exception e) {
            id = -1;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return id;
    }

    public String getName() {
        return name;
    }

    public Holdings getHoldings() {
        return new Holdings(0, this.name);
    }

    public boolean createBankAccount(int bankID) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("INSERT INTO " + Constants.SQLTable + "_BankRelations (account_name, bank_id, holdings) VALUES (?, ?, ?)");
            ps.setString(1, this.name);
            ps.setInt(2, bankID);
            ps.setDouble(2, iConomy.getBank(bankID).getInitialHoldings());
            ps.executeUpdate();
        } catch (Exception e) {
            return false;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return true;
    }

    public boolean createBankAccount(String bank) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        int bankID = iConomy.getBank(bank).getId();

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("INSERT INTO " + Constants.SQLTable + "_BankRelations (account_name, bank_id, holdings) VALUES (?, ?, ?)");
            ps.setString(1, this.name);
            ps.setInt(2, bankID);
            ps.setDouble(2, iConomy.getBank(bankID).getInitialHoldings());
            ps.executeUpdate();
        } catch (Exception e) {
            return false;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return true;
    }

    public ArrayList<Bank> withBanks() {
        if(!Constants.Banking)
            return null;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList<Bank> banks = new ArrayList<Bank>();

        try {
            conn = iConomy.getiCoDatabase().getConnection();

            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ?");
            ps.setString(1, this.name);
            rs = ps.executeQuery();

            while(rs.next()) {
                Bank bank = new Bank(rs.getInt("bank_id"));
                banks.add(bank);
            }
        } catch (Exception e) {
            return null;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return banks;
    }

    public void setMainBank(String name) {
        Bank bank = iConomy.Banks.get(name);
        int id = iConomy.Banks.get(name).getId();

        if(bank.hasAccount(this.name)) {
            setMainBank(id);
        }
    }
    
    public void setMainBank(int id) {
        if(!Constants.Banking)
            return;
        
        if(!iConomy.Banks.get(id).hasAccount(this.name))
            return;
        
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Bank bank = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();

            ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_BankRelations SET main = 0 WHERE account_name = ? AND main = 1");
            ps.setString(1, this.name);
            ps.executeUpdate();
            ps.clearParameters();
            
            ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_BankRelations SET main = 1 WHERE account_name = ? AND bank_id = ?");
            ps.setString(1, this.name);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }
    }

    public Bank getMainBank() {
        if(!Constants.Banking)
            return null;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Bank bank = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();

            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ? AND main = 1 LIMIT 1");
            ps.setString(1, this.name);
            rs = ps.executeQuery();

            if(rs.next()) {
                bank = new Bank(rs.getInt("bank_id"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return bank;
    }

    public BankAccount getMainBankAccount() {
        if(!Constants.Banking)
            return null;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        BankAccount account = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();

            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ? AND main = 1 LIMIT 1");
            ps.setString(1, this.name);
            rs = ps.executeQuery();

            if(rs.next()) {
                Bank bank = new Bank(rs.getInt("bank_id"));
                account = new BankAccount(bank.getName(), rs.getInt("bank_id"), this.name);
            }
        } catch (Exception e) {
            return null;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return account;
    }

    public ArrayList<BankAccount> getBankAccounts() {
        if(!Constants.Banking)
            return null;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList<BankAccount> banks = new ArrayList<BankAccount>();

        try {
            conn = iConomy.getiCoDatabase().getConnection();

            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ?");
            ps.setString(1, this.name);
            rs = ps.executeQuery();

            while(rs.next()) {
                Bank bank = new Bank(rs.getInt("bank_id"));
                banks.add(new BankAccount(bank.getName(), rs.getInt("bank_id"), this.name));
            }
        } catch (Exception e) {
            return null;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return banks;
    }

    public Holdings getBankHoldings(int id) {
        if(!Constants.Banking)
            return null;

        if(id == 0)
            return new Holdings(id, this.name);
        else {
            return new Holdings(id, this.name, true);
        }
    }

    public boolean isHidden() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT hidden FROM " + Constants.SQLTable + " WHERE username = ? LIMIT 1");
            ps.setString(1, this.name);
            rs = ps.executeQuery();

            if (rs != null) {
                if (rs.next()) {
                    return rs.getBoolean("hidden");
                }
            }
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to check status: " + e);
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                iConomy.getiCoDatabase().close(conn);
        }

        return false;
    }

    public boolean setHidden(boolean hidden) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();

            ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + " SET hidden = ? WHERE username = ?");
            ps.setBoolean(1, hidden);
            ps.setString(2, this.name);

            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to update status: " + e);
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                iConomy.getiCoDatabase().close(conn);
        }

        return true;
    }

    /**
     * Returns the ranking number of an account
     *
     * @param name
     * @return Integer
     */
    public int getRank() {
        int i = 1;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + " WHERE hidden = 0 ORDER BY balance DESC");
            rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getString("username").equalsIgnoreCase(this.name)) {
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

            iConomy.getiCoDatabase().close(conn);
        }

        return -1;
    }

    public void remove() {
        AccountRemoveEvent Event = new AccountRemoveEvent(name);
        iConomy.getBukkitServer().getPluginManager().callEvent(Event);
        
        if(!Event.isCancelled()) {
            Connection conn = null;
            PreparedStatement ps = null;

            try {
                conn = iConomy.getiCoDatabase().getConnection();
                ps = conn.prepareStatement("DELETE FROM " + Constants.SQLTable + " WHERE username = ?");
                ps.setString(1, this.name);
                ps.executeUpdate();
            } catch(Exception e) {
                System.out.println("[iConomy] Failed to remove account: " + e);
            } finally {
                if(ps != null)
                    try { ps.close(); } catch (SQLException ex) { }

                if(conn != null)
                    iConomy.getiCoDatabase().close(conn);
            }
        }
    }
}