package com.nijiko.coelho.iConomy.system;

import java.text.DecimalFormat;
import java.util.TimerTask;

import org.bukkit.entity.Player;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.util.Constants;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Interest extends TimerTask {

    @Override
    public void run() {
        Connection conn = null;
        PreparedStatement ps = null;

        DecimalFormat DecimalFormat = new DecimalFormat("#.##");
        Player players[] = iConomy.getBukkitServer().getOnlinePlayers();

        double amount = 0.0;
        boolean percentage = false;

        int result = 0;
        int totalRowUpdate = 0;
        boolean updateAll = false;
        boolean updateEmpty = false;
        boolean updateFail = false;

        if(Constants.Interest_Percentage != 0.0){
            percentage = true;
        } else {
            try {
                amount = (Constants.Interest_FlatRate == 0.0) ? Double.valueOf(
                    DecimalFormat.format(
                        Math.random() * (
                            Constants.Interest_Max_Interval - Constants.Interest_Min_Interval
                        ) + (
                            Constants.Interest_Min_Interval
                        )
                    )
                ).doubleValue() : Double.valueOf(Constants.Interest_FlatRate).doubleValue();
            } catch (NumberFormatException e) {
                System.out.println("[iConomy] Invalid Interest: " + e);
            }
        }

        try {
            conn = iConomy.getDatabase().getConnection();
            conn.setAutoCommit(false);

            String updateSQL = "UPDATE " + Constants.SQL_Table + " SET balance = ? WHERE username = ?";
            ps = conn.prepareStatement(updateSQL);

            for (Player p : players) {
                if (iConomy.getBank().hasAccount(p.getName())) {
                    Account account = iConomy.getBank().getAccount(p.getName());

                    if(account != null) {
                        double balance = account.getBalance();

                        if(percentage) {
                            amount = Math.round((Constants.Interest_Percentage*balance)/100);
                        }

                        ps.setDouble(1, balance+amount);
                        ps.setString(2, p.getName());
                        ps.addBatch();

                        if(amount < 0.0)
                            iConomy.getTransactions().insert("[System Interest]", p.getName(), 0.0, account.getBalance(), 0.0, 0.0, amount);
                        else {
                            iConomy.getTransactions().insert("[System Interest]", p.getName(), 0.0, account.getBalance(), 0.0, amount, 0.0);
                        }
                    }
                }
            }
            
            //Execute the batch.
            ps.executeBatch();

            // Commit
            conn.commit();
            
            ps.clearBatch();
        } catch (BatchUpdateException e) {
            System.out.println(e);
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                iConomy.getDatabase().close(conn);
        }
    }
}
