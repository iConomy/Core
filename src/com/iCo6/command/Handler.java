package com.iCo6.command;

import java.util.LinkedHashMap;
import java.util.List;

import com.iCo6.iConomy;
import com.iCo6.command.Parser.Argument;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.util.Template;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Handler {

    protected final iConomy plugin;
    protected final Template template;

    public Handler(iConomy plugin, Template template) {
        this.plugin = plugin;
        this.template = template;
    }

    public abstract boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage;

    protected static boolean isConsole(CommandSender sender) {
        return !(sender instanceof Player);
    }


    protected boolean hasPermissions(CommandSender sender, String command) {
        if(sender instanceof Player) {
            Player player = (Player)sender;

            if(plugin.Commands.hasPermission(command)) {
                String node = plugin.Commands.getPermission(command);

                if(plugin.Permissions != null)
                    return plugin.Permissions.Security.permission(player, node);
                else
                    return player.isOp();
            }
        }

        return true;
    }

    protected static Player getPlayer(CommandSender sender, String[] args, int index) {
        if (args.length > index) {
            List<Player> players = sender.getServer().matchPlayer(args[index]);

            if (players.isEmpty()) {
                sender.sendMessage("Could not find player with the name: " + args[index]);
                return null;
            } else {
                return players.get(0);
            }
        } else {
            if (isConsole(sender))
                return null;
            else
                return (Player)sender;
        }
    }
}
