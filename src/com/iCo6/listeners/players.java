package com.iCo6.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.iCo6.system.Accounts;

public class players implements Listener {
	
	HashMap<UUID, String> map = new HashMap<UUID, String> ();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Accounts accounts = new Accounts();
        Player player = event.getPlayer();
        
        if (map.containsKey(player.getUniqueId())) {
            if(player != null)
                if(accounts.exists(map.get(player.getUniqueId())))
                    accounts.get(map.get(player.getUniqueId()), player.getUniqueId()).updateName(player.getName());
        }

        if(player != null)
            if(!accounts.exists(player.getName()))
                accounts.create(player.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin (AsyncPlayerPreLoginEvent event) {
    	if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) return;
    	OfflinePlayer off = Bukkit.getOfflinePlayer(event.getUniqueId());
    	if (off == null) return;
    	map.put(off.getUniqueId(), off.getName());
    }
}
