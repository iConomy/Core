package com.iConomy.handlers;

import java.util.LinkedHashMap;

import com.iConomy.command.Handler;
import com.iConomy.command.Parser.Argument;
import com.iConomy.command.exceptions.InvalidUsage;

import com.iConomy.iConomy;
import com.iConomy.system.Accounts;

import com.iConomy.util.Messaging;
import com.iConomy.util.Template;

import org.bukkit.command.CommandSender;

public class Remove extends Handler {
    private Accounts Accounts = new Accounts();

    public Remove(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(!hasPermissions(sender, "remove"))
            throw new InvalidUsage("You do not have permission to do that.");

        String name = arguments.get("name").getStringValue();
        String tag = template.color(Template.Node.TAG_MONEY);

        if(name.equals("0"))
            throw new InvalidUsage("Missing <white>name<rose>: /money remove <name>");

        if(!Accounts.exists(name)) {
            template.set(Template.Node.ERROR_ACCOUNT);
            template.add("name", name);
            Messaging.send(sender, tag + template.parse());
            return false;
        }

        if(!Accounts.remove(name)) {
            template.set(Template.Node.ERROR_CREATE);
            template.add("name", name);
            Messaging.send(sender, tag + template.parse());
            return false;
        }

        template.set(Template.Node.ACCOUNTS_REMOVE);
        template.add("name", name);
        Messaging.send(sender, tag + template.parse());
        return false;
    }
}
