package com.iCo.handlers;

import java.util.LinkedHashMap;

import com.iCo.command.Handler;
import com.iCo.command.Parser.Argument;
import com.iCo.command.exceptions.InvalidUsage;

import com.iCo.iConomy;
import com.iCo.system.Accounts;

import com.iCo.util.Messaging;
import com.iCo.util.Template;

import org.bukkit.command.CommandSender;

public class Create extends Handler {

    private Accounts Accounts = new Accounts();

    public Create(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(!hasPermissions(sender, "create"))
            throw new InvalidUsage("You do not have permission to do that.");

        String name = arguments.get("name").getStringValue();
        String tag = template.color(Template.Node.TAG_MONEY);

        if(name.equals("0"))
            throw new InvalidUsage("Missing <white>name<rose>: /money create <name>");

        if(Accounts.exists(name)) {
            template.set(Template.Node.ERROR_EXISTS);
            Messaging.send(sender, tag + template.parse());
            return false;
        }

        if(!Accounts.create(name)) {
            template.set(Template.Node.ERROR_CREATE);
            template.add("name", name);
            Messaging.send(sender, tag + template.parse());
            return false;
        }

        template.set(Template.Node.ACCOUNTS_CREATE);
        template.add("name", name);
        Messaging.send(sender, tag + template.parse());
        return false;
    }
}
