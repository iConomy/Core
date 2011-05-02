package com.iConomy.system;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Banks, holder of all banks.
 * @author Nijikokun
 */
public class Banks {
    
    /**
     * Check and see if the bank exists through id.
     *
     * @param id
     * @return Boolean
     */
    public boolean exists(int id) {
        if(!Constants.Banking)
            return false;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        boolean exists = false;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_Banks WHERE id = ? LIMIT 1");
            ps.setInt(1, id);
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

    /**
     * Check and see if the bank actually exists, through name.
     *
     * @param name
     * @return Boolean
     */
    public boolean exists(String name) {
        if(!Constants.Banking)
            return false;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        boolean exists = false;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_Banks WHERE name = ? LIMIT 1");
            ps.setString(1, name);
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

    /**
     * Fetch the id through the name, no questions.
     *
     * @param name
     * @return Integer
     */
    private int getId(String name) {
        if(!Constants.Banking)
            return -1;

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
            id = 0;
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

    /**
     * Create a totally customized bank.
     *
     * @param name
     * @param currency
     * @param currency_plural
     * @param initial
     * @return Bank
     */
    public Bank create(String name, String major, String minor, double initial, double fee) {
        if(!Constants.Banking)
            return null;

        if(!exists(name)) {
            Connection conn = null;
            ResultSet rs = null;
            PreparedStatement ps = null;

            try {
                conn = iConomy.getiCoDatabase().getConnection();
                ps = conn.prepareStatement("INSERT INTO " + Constants.SQLTable + "_Banks(name, major, minor, initial, fee) VALUES (?, ?, ?, ?, ?)");

                ps.setString(1, name);
                ps.setString(2, Constants.Major.get(0) + "," + Constants.Major.get(1));
                ps.setString(3, Constants.Minor.get(0) + "," + Constants.Minor.get(1));
                ps.setDouble(4, initial);
                ps.setDouble(5, fee);

                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("[iConomy] Failed to set holdings balance: " + e);
            } finally {
                if(ps != null)
                    try { ps.close(); } catch (SQLException ex) { }

                if(conn != null)
                    try { conn.close(); } catch (SQLException ex) { }
            }
        }

        return new Bank(name);
    }

    /**
     * Uses the default settings for a bank upon creation with a different name.
     * @param name
     * @return Bank
     */
    public Bank create(String name) {
        if(!Constants.Banking)
            return null;

        if(!exists(name)) {
            Connection conn = null;
            ResultSet rs = null;
            PreparedStatement ps = null;

            try {
                conn = iConomy.getiCoDatabase().getConnection();
                ps = conn.prepareStatement("INSERT INTO " + Constants.SQLTable + "_Banks(name, major, minor, initial, fee) VALUES (?, ?, ?, ?, ?)");

                ps.setString(1, name);
                ps.setString(2, Constants.BankMajor.get(0) + "," + Constants.BankMajor.get(1));
                ps.setString(3, Constants.BankMinor.get(0) + "," + Constants.BankMinor.get(1));
                ps.setDouble(4, Constants.BankHoldings);
                ps.setDouble(5, Constants.BankFee);

                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("[iConomy] Failed to set holdings balance: " + e);
            } finally {
                if(ps != null)
                    try { ps.close(); } catch (SQLException ex) { }

                if(conn != null)
                    try { conn.close(); } catch (SQLException ex) { }
            }
        }

        return new Bank(name);
    }

    public int count() {
        if(!Constants.Banking)
            return -1;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = -1;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT COUNT(id) AS count FROM " + Constants.SQLTable + "_Banks");
            rs = ps.executeQuery();

            if(rs.next()) {
                count = rs.getInt("count");
            }
        } catch (Exception e) {
            return count;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return count;
    }

    /**
     * Count the number of accounts a person has.
     *
     * @param name
     * @return Integer - Account count.
     */
    public int count(String name) {
        if(!Constants.Banking)
            return -1;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        int count = -1;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT COUNT(id) AS count FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();

            if(rs.next()) {
                count = rs.getInt("count");
            }
        } catch (Exception e) {
            return count;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return count;
    }

    /**
     * Purge all accounts in all banks with the default holding value.
     *
     * @return true or false based on the outcome of whether it was successful or not.
     */
    public boolean purge() {
        if(!Constants.Banking)
            return false;

        Connection conn = null;
        ResultSet rs = null;
        Statement s = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_Banks");
            rs = ps.executeQuery();

            ps = conn.prepareStatement("DELETE FROM " + Constants.SQLTable + "_BankRelations WHERE bank_id = ? AND holdings = ?");
            
            while(rs.next()) {
                ps.setInt(1, rs.getInt("id"));
                ps.setDouble(2, rs.getDouble("initial"));
                ps.addBatch();
            }
            
            ps.executeBatch();
            conn.commit();
            ps.clearBatch();
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

    /**
     * Remove all accounts in a specific bank that have the default value.
     *
     * @param name Name of bank in question.
     * @return True if successful, false if error.
     * @see #purge(int)
     */
    public boolean purge(String name) {
        if(!Constants.Banking)
            return false;

        Bank bank = iConomy.getBank(name);

        if(bank != null) {
            return purge(bank.getId());
        }

        return false;
    }

    /**
     * Remove all accounts in a specific bank that have the default value.
     *
     * @param id Bank id
     * @return True if successful, false if error.
     */
    public boolean purge(int id) {
        if(!Constants.Banking)
            return false;

        Bank bank = iConomy.getBank(id);

        if(bank != null) {
            Connection conn = null;
            ResultSet rs = null;
            PreparedStatement ps = null;

            try {
                conn = iConomy.getiCoDatabase().getConnection();
                ps = conn.prepareStatement("DELETE FROM " + Constants.SQLTable + "_BankRelations WHERE bank_id = ? AND holdings = ?");
                ps.setInt(1, id);
                ps.setDouble(2, bank.getInitialHoldings());
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

        return false;
    }

    /**
     * Grab the bank, if it doesn't exist, return null.
     *
     * @param name Name of bank to grab
     * @return Bank object
     */
    public Bank get(String name) {
        if(!Constants.Banking)
            return null;

        if(exists(name))
            return new Bank(name);
        else {
            return null;
        }
    }

    /**
     * Grab the bank, if it doesn't exist, return null.
     *
     * @param id Bank id
     * @return Bank object
     */
    public Bank get(int id) {
        if(!Constants.Banking)
            return null;
        
        if(exists(id))
            return new Bank(id);
        else {
            return null;
        }
    }

    /**
     * Grab a list of all the balances / holdings inside banks.
     *
     * Allows us to utilize the entire economic status of banks for statistics.
     *
     * @return List<Double>
     */
    public List<Double> values() {
        if(!Constants.Banking)
            return null;

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<Double> Values = new ArrayList<Double>();

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT holdings FROM " + Constants.SQLTable + "_BankRelations");
            rs = ps.executeQuery();

            while(rs.next()) {
                Values.add(rs.getDouble("holdings"));
            }
        } catch (Exception e) {
            return null;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return Values;
    }
}
