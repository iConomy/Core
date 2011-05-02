package com.iConomy.system;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transactions {

    /**
     * Inserts data into transaction without using seperate methods, direct method.
     *
     * @param from
     * @param to
     * @param gain
     * @param loss
     */
    public void insert(String from, String to, double from_balance, double to_balance, double set, double gain, double loss) {
        if (!Constants.Logging)
            return;

        int i = 1;
        long timestamp = System.currentTimeMillis() / 1000;

        Object[] data = new Object[]{from, to, from_balance, to_balance, timestamp, set, gain, loss};

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("INSERT INTO " + Constants.SQLTable + "_Transactions(account_from, account_to, account_from_balance, account_to_balance, `timestamp`, `set`, gain, loss) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

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

            iConomy.getiCoDatabase().close(conn);
        }
    }
}