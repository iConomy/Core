package com.iCo6.listeners;

import com.iCo6.system.Accounts;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class players extends PlayerListener {

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Accounts accounts = new Accounts();
        Player player = event.getPlayer();

        if(player != null)
            if(!accounts.exists(player.getName()))
                accounts.create(player.getName());
    }
}
