package com.iCo6.listeners;

import com.iCo6.system.Accounts;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class players implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Accounts accounts = new Accounts();
        Player player = event.getPlayer();

        if(player != null)
            if(!accounts.exists(player.getName()))
                accounts.create(player.getName());
    }
}
