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
        return plugin.hasPermissions(sender, command);
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
