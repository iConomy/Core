package com.iConomy.handlers;

import java.util.LinkedHashMap;

import com.iConomy.command.Handler;
import com.iConomy.command.Parser.Argument;
import com.iConomy.command.exceptions.InvalidUsage;

import com.iConomy.iConomy;

import com.iConomy.util.Messaging;
import com.iConomy.util.Template;

import org.bukkit.command.CommandSender;

public class Take extends Handler {

    public Take(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(isConsole(sender))
            Messaging.send(sender, "`rCannot check money on non-living organism.");

        String name = arguments.get("name").getStringValue();
        String tag = template.color(Template.Node.TAG_MONEY);
        Double amount;

        if(name.equals("0"))
            throw new InvalidUsage("Missing name parameter: /money set <name> <amount>");

        if(arguments.get("amount").getStringValue().equals("empty"))
            throw new InvalidUsage("Missing amount parameter: /money set <name> <amount>");

        try {
            amount = arguments.get("amount").getDoubleValue();
        } catch(NumberFormatException e) {
            throw new InvalidUsage("Invalid amount parameter, must be double.");
        }

        if(Double.isInfinite(amount) || Double.isNaN(amount))
            throw new InvalidUsage("Invalid amount parameter, must be double.");

        return false;
    }
}
