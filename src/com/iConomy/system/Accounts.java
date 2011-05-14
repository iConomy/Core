package com.iConomy.system;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Manage Account.
 * 
 * @author Nijikokun
 */
public class Accounts {

    public Accounts() { }

    public boolean exists(String name) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        boolean exists = false;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + " WHERE username = ? LIMIT 1");
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

    public boolean create(String name) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("INSERT INTO " + Constants.SQLTable + "(username, balance, hidden) VALUES (?, ?, 0)");
            ps.setString(1, name);
            ps.setDouble(2, Constants.Holdings);
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

    public boolean remove(String name) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("DELETE FROM " + Constants.SQLTable + " WHERE username = ? LIMIT 1");
            ps.setString(1, name);
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

    public boolean removeCompletely(String name) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("DELETE FROM " + Constants.SQLTable + " WHERE username = ? LIMIT 1");
            ps.setString(1, name);
            ps.executeUpdate();

            ps.clearParameters();

            ps = conn.prepareStatement("DELETE FROM " + Constants.SQLTable + " WHERE account_name = ?");
            ps.setString(1, name);
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

    public boolean purge() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("DELETE FROM " + Constants.SQLTable + " WHERE balance = ?");
            ps.setDouble(1, Constants.Holdings);
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

    /**
     * Removes all accounts from the database.
     * Do not use this.
     * 
     * @return
     */
    public boolean emptyDatabase() {
        Connection conn = null;
        Statement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.createStatement();
            ps.execute("TRUNCATE TABLE " + Constants.SQLTable);
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

    public List<Double> values() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<Double> Values = new ArrayList<Double>();

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT balance FROM " + Constants.SQLTable);
            rs = ps.executeQuery();

            while(rs.next()) {
                Values.add(rs.getDouble("balance"));
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

    public LinkedHashMap<String, Double> ranking(int amount) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        LinkedHashMap<String, Double> Ranking = new LinkedHashMap<String, Double>();

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT username,balance FROM " + Constants.SQLTable + " WHERE hidden = 0 ORDER BY balance DESC LIMIT ?");
            ps.setInt(1, amount);
            rs = ps.executeQuery();

            while(rs.next()) {
                Ranking.put(rs.getString("username"), rs.getDouble("balance"));
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

        return Ranking;
    }

    public Account get(String name) {
        if(exists(name)) {
            return new Account(name);
        } else {
            if(!create(name)) {
                return null;
            }
        }

        return new Account(name);
    }
}
