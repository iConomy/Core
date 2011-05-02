package com.iConomy.system;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class Bank {
    private int id = 0;
    private String name = "";

    public Bank(String name) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int id = 0;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT id FROM " + Constants.SQLTable + "_Banks WHERE name = ? LIMIT 1");
            ps.setString(1, name);
            rs = ps.executeQuery();

            if(rs.next()) {
                id = rs.getInt("id");
            }
        } catch (Exception e) {
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        this.id = id;
        this.name = name;
    }

    public Bank(int id) {
        this.id = id;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT name FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if(rs.next()) {
                this.name = rs.getString("name");
            }
        } catch (Exception e) {
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getMinor() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> minor = Constants.Minor;
        String asString = Constants.Minor.get(0) + "," + Constants.Minor.get(1);

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT minor FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();

            if(rs.next()) {
                asString = rs.getString("minor");

                String[] denoms = asString.split(",");
                minor.set(0, denoms[0]);
                minor.set(1, denoms[1]);
            }
        } catch (Exception e) {
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return minor;
    }

    public List<String> getMajor() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> major = Constants.Major;
        String asString = Constants.Major.get(0) + "," + Constants.Major.get(1);

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT major FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();

            if(rs.next()) {
                asString = rs.getString("major");

                String[] denoms = asString.split(",");
                major.set(0, denoms[0]);
                major.set(1, denoms[1]);
            }
        } catch (Exception e) {
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return major;
    }

    public double getInitialHoldings() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        double initial = Constants.BankHoldings;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT initial FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();

            if(rs.next()) {
                initial = rs.getDouble("initial");
            }
        } catch (Exception e) {
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return initial;
    }

    public double getFee() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        double fee = Constants.BankFee;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT fee FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();

            if(rs.next()) {
                fee = rs.getDouble("fee");
            }
        } catch (Exception e) {
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return fee;
    }

    public void setName(String name) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();

            ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_Banks SET name = ? WHERE id = ?");
            ps.setString(1, name);
            ps.setInt(2, this.id);
            ps.executeUpdate();

            this.name = name;
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to update bank name: ");
            e.printStackTrace();
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }
    }

    public void setMajor(String singular, String plural) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();

            ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_Banks SET major = ? WHERE id = ?");
            ps.setString(1, singular + "," + plural);
            ps.setInt(2, this.id);

            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to update bank major: ");
            e.printStackTrace();
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }
    }

    public void setMinor(String singular, String plural) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();

            ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_Banks SET minor = ? WHERE id = ?");
            ps.setString(1, singular + "," + plural);
            ps.setInt(2, this.id);

            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to update bank minor: ");
            e.printStackTrace();
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }
    }

    public void setInitialHoldings(double amount) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();

            ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_Banks SET initial = ? WHERE id = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, this.id);

            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to update bank initial amount: ");
            e.printStackTrace();
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }
    }

    public void setFee(double amount) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();

            ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_Banks SET fee = ? WHERE id = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to update bank fee: ");
            e.printStackTrace();
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }
    }

    /**
     * Does the bank have record of the account in question?
     * hasAccount or accountExists ?
     * @param account The account in question
     * @return boolean - Does the account exist?
     */
    public boolean hasAccount(String account) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        boolean exists = false;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ? AND bank_id = ? LIMIT 1");
            ps.setString(1, account);
            ps.setInt(2, id);
            rs = ps.executeQuery();

            exists = rs.next();
        } catch (Exception e) {
            exists = false;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return exists;
    }

    public HashMap<String, Double> getAccounts() {
        HashMap<String, Double> accounts = new HashMap<String, Double>();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE id = ? ORDER BY balance DESC");
            ps.setInt(1, this.id);
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

            iConomy.getiCoDatabase().close(conn);
        }

        return accounts;
    }

    /**
     * Fetch the account, Does not check for existance.
     * Do that prior to using this to prevent null errors or any other issues.
     *
     * @param account The account to grab
     * @return Account (If it exists, null if not)
     */
    public BankAccount getAccount(String account) {
        if(hasAccount(account)) {
            return new BankAccount(this.name, this.id, account);
        } else {
            return null;
        }
    }

    /**
     * Add an account to the bank, if it already exists return false.
     *
     * @param account Name of the account being created
     */
    public boolean createAccount(String account) {
        if (!this.hasAccount(account)) {
            Connection conn = null;
            ResultSet rs = null;
            PreparedStatement ps = null;

            try {
                conn = iConomy.getiCoDatabase().getConnection();
                ps = conn.prepareStatement("INSERT INTO " + Constants.SQLTable + "_BankRelations(account_name, bank_id, holdings) VALUES (?, ?, ?)");
                ps.setString(1, account);
                ps.setInt(2, this.id);
                ps.setDouble(3, getInitialHoldings());
                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("[iConomy] Error inserting bank account: " + e);
                return false;
            } finally {
                if(ps != null)
                    try { ps.close(); } catch (SQLException ex) { }

                if(conn != null)
                    try { conn.close(); } catch (SQLException ex) { }
            }

            return true;
        }

        return false;
    }

    /**
     * Add an account to the bank, if it already exists return false.
     *
     * @param account Name of the account being created
     * @param holdings Initial holdings of the account
     */
    public boolean createAccount(String account, double holdings) {
        if (!this.hasAccount(account)) {
            Connection conn = null;
            ResultSet rs = null;
            PreparedStatement ps = null;

            try {
                conn = iConomy.getiCoDatabase().getConnection();
                ps = conn.prepareStatement("INSERT INTO " + Constants.SQLTable + "_BankRelations(account_name, bank_id, holdings) VALUES (?, ?, ?)");
                ps.setString(1, account);
                ps.setInt(2, this.id);
                ps.setDouble(3, holdings);
                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("[iConomy] Error inserting bank account: " + e);
                return false;
            } finally {
                if(ps != null)
                    try { ps.close(); } catch (SQLException ex) { }

                if(conn != null)
                    try { conn.close(); } catch (SQLException ex) { }
            }

            return true;
        }

        return false;
    }

    /**
     * Completely remove an account from the bank and the database.
     *
     * @param account
     */
    public void removeAccount(String account) {
        if(hasAccount(account)) {
            (new BankAccount(this.name, this.id, account)).remove();
        }
    }
}
