package com.nijiko.coelho.iConomy.system;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.util.Constants;
import com.nijiko.coelho.iConomy.util.Misc;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Transactions {

    public Transactions() { }

    public void load() throws Exception {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        if (Constants.Log_Data) {
            conn = iConomy.getDatabase().getConnection();

            if (Misc.is(Constants.Database_Type, new String[] { "sqlite", "h2", "h2sql", "h2db" })) {
                try {
                    ps = conn.prepareStatement("CREATE TABLE " + Constants.SQL_Table + "_Transactions(id INT AUTO_INCREMENT PRIMARY KEY, account_from TEXT, account_to TEXT, account_from_balance DECIMAL(65, 2), account_to_balance DECIMAL(65, 2), timestamp TEXT , set DECIMAL(65, 2), gain DECIMAL(65, 2), loss DECIMAL(65, 2));");
                    ps.executeUpdate();
                } catch(SQLException E) { }
            } else {
                DatabaseMetaData dbm = conn.getMetaData();
                rs = dbm.getTables(null, null, Constants.SQL_Table + "_Transactions", null);

                if (!rs.next()) {
                    System.out.println("[iConomy] Creating logging database.. [" + Constants.SQL_Table + "_Transactions]");
                    ps = conn.prepareStatement("CREATE TABLE " + Constants.SQL_Table + "_Transactions (`id` INT(255) NOT NULL AUTO_INCREMENT, `account_from` TEXT NOT NULL, `account_to` TEXT NOT NULL, `account_from_balance` DECIMAL(65, 2) NOT NULL, `account_to_balance` DECIMAL(65, 2) NOT NULL, `timestamp` TEXT NOT NULL, `set` DECIMAL(65, 2) NOT NULL, `gain` DECIMAL(65, 2) NOT NULL, `loss` DECIMAL(65, 2) NOT NULL, PRIMARY KEY (`id`))");

                    if(ps != null) {
                        ps.executeUpdate();
                        System.out.println("[iConomy] Database Created.");
                    }
                }
                System.out.println("[iConomy] Logging enabled.");
            }
        } else {
            System.out.println("[iConomy] Logging is currently disabled.");
        }

        if(ps != null)
            try { ps.close(); } catch (SQLException ex) { }

        if(rs != null)
            try { rs.close(); } catch (SQLException ex) { }

        if(conn != null)
            iConomy.getDatabase().close(conn);
    }

    /**
     * Inserts data into transaction without using seperate methods, direct method.
     *
     * @param from
     * @param to
     * @param gain
     * @param loss
     */
    public void insert(String from, String to, double from_balance, double to_balance, double set, double gain, double loss) {
        if (!Constants.Log_Data) {
            return;
        }

        int i = 1;
        long timestamp = System.currentTimeMillis() / 1000;

        Object[] data = new Object[]{from, to, from_balance, to_balance, timestamp, set, gain, loss};

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getDatabase().getConnection();
            ps = conn.prepareStatement("INSERT INTO " + Constants.SQL_Table + "_Transactions(account_from, account_to, account_from_balance, account_to_balance, `timestamp`, `set`, gain, loss) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            for (Object obj : data) {
                ps.setObject(i, obj);
                i++;
            }

            ps.executeUpdate();
        } catch (SQLException e) {
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(rs != null)
                try { rs.close(); } catch (SQLException ex) { }

            iConomy.getDatabase().close(conn);
        }
    }
}