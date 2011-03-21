package com.nijiko.coelho.iConomy.system;

import java.text.DecimalFormat;
import java.util.TimerTask;

import org.bukkit.entity.Player;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.util.Constants;

public class Interest extends TimerTask {

    @Override
    public void run() {
        DecimalFormat DecimalFormat = new DecimalFormat("#.##");
        double amount = 0.0;
        boolean percentage = (Constants.Interest_Percentage != 0.0);

        if(!percentage) {
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

        Player players[] = iConomy.getBukkitServer().getOnlinePlayers();

        for (Player p : players) {
            if (iConomy.getBank().hasAccount(p.getName())) {
                Account account = iConomy.getBank().getAccount(p.getName());

                if(account != null) {
                    if(percentage) {
                        amount = Math.round(account.getBalance()/Constants.Interest_Percentage);
                    }

                    account.add(amount);
                    account.save();

                    if(amount < 0.0)
                        iConomy.getTransactions().insert("[System Interest]", p.getName(), 0.0, account.getBalance(), 0.0, 0.0, amount);
                    else {
                        iConomy.getTransactions().insert("[System Interest]", p.getName(), 0.0, account.getBalance(), 0.0, amount, 0.0);
                    }
                }
            }
        }
    }
}
