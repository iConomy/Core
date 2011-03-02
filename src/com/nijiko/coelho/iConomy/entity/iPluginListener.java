package com.nijiko.coelho.iConomy.entity;

import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;

import com.nijiko.coelho.iConomy.iConomy;

import com.nijikokun.bukkit.Permissions.Permissions;

public class iPluginListener extends ServerListener {
	
    public iPluginListener() {
    	
    }

	@Override
    public void onPluginEnabled(PluginEvent event) {
    	
        if(event.getPlugin().getDescription().getName().equals("Permissions")) {
            iConomy.setPermissions((Permissions)event.getPlugin());
            
            System.out.println("[iConomy] Successfully linked with Permissions.");
        }
        
    }
    
}
