package com.iConomy.command;

import java.util.LinkedHashMap;
import java.util.List;

import com.iConomy.iConomy;
import com.iConomy.command.Parser.Argument;
import com.iConomy.command.exceptions.InvalidUsage;
import com.iConomy.util.Template;

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
        if (!(sender instanceof Player)) {
            return true;
        } else {
            return false;
        }
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
            if (isConsole(sender)) {
                return null;
            } else {
                return (Player)sender;
            }
        }
    }
}
