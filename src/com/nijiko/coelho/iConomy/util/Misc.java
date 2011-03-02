package com.nijiko.coelho.iConomy.util;

import org.bukkit.entity.Player;

import com.nijiko.coelho.iConomy.iConomy;

public class Misc {

    /**
     * Checks text against two variables, if it equals at least one returns true.
     *
     * @param text The text that we were provided with.
     * @param against The first variable that needs to be checked against
     * @param or The second variable that it could possibly be.
     *
     * @return <code>Boolean</code> - True or false based on text.
     */
    public static boolean isAny(String text, String[] is) {
    	for(String s : is) {
    		if(text.equalsIgnoreCase(s))
    			return true;
    	}
    	return false;
    }

    /**
     * Get the player from the server (matched)
     */
    
    public static Player playerMatch(String name) {
        Player[] online = iConomy.getBukkitServer().getOnlinePlayers();
        Player lastPlayer = null;

        for (Player player : online) {
            String playerName = player.getName();

            if (playerName.equalsIgnoreCase(name)) {
                lastPlayer = player;
                break;
            }

            if (playerName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
                if (lastPlayer != null) {
                    return null;
                }

                lastPlayer = player;
            }
        }

        return lastPlayer;
    }
}
