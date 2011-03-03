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
		double amount = Double.parseDouble(DecimalFormat.format(Math.random() * 
				(Constants.Interest_Max_Interval - Constants.Interest_Min_Interval) + 
				(Constants.Interest_Min_Interval)));
		Player players[] = iConomy.getBukkitServer().getOnlinePlayers();
		for(Player p : players)
			if(iConomy.getBank().hasAccount(p.getName())) {
				iConomy.getBank().getAccount(p.getName()).add(amount);
			}
	}

}
