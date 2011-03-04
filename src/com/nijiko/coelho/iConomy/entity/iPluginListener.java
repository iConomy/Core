package com.nijiko.coelho.iConomy.entity;

import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;

import com.nijiko.coelho.iConomy.iConomy;

import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

/**
 * iPluginListener
 * Allows us to hook into permissions even if it is loaded later on.
 *
 * Checks for Plugins on the event that they are enabled,
 * checks the name given with the usual name of the plugin to
 * verify the existence. If the name matches we pass the plugin along
 * to iConomy to utilize in various ways.
 *
 * @author Nijikokun
 */

public class iPluginListener extends ServerListener {
    public iPluginListener() { 
    	
    }

    @Override
    public void onPluginEnabled(PluginEvent event) {
        if(iConomy.getPermissions() == null) {
            Plugin permissions = iConomy.getBukkitServer().getPluginManager().getPlugin("Permissions");

            if (permissions != null) {
                iConomy.setPermissions(((Permissions)permissions).getHandler());
                System.out.println("[iConomy] Successfully linked with Permissions.");
            }
        }
    }
}
