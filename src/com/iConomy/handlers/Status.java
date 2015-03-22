package com.iConomy.handlers;

import java.util.LinkedHashMap;

import com.iConomy.command.Handler;
import com.iConomy.command.Parser.Argument;
import com.iConomy.command.exceptions.InvalidUsage;
import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Accounts;
import com.iConomy.util.Messaging;
import com.iConomy.util.Template;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Status extends Handler {

    private Accounts Accounts = new Accounts();

    public Status(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(!hasPermissions(sender, "status"))
            throw new InvalidUsage("You do not have permission to do that.");

        String name = arguments.get("name").getStringValue();
        String tag = template.color(Template.Node.TAG_MONEY);
        boolean self = false;

        if(!isConsole(sender))
            if(((Player)sender).getName().equalsIgnoreCase(name))
                self = true;

        if(name.equals("0"))
            throw new InvalidUsage("Missing <white>name<rose>: /money status <name> (new status)");

        if(!Accounts.exists(name)) {
            template.set(Template.Node.ERROR_ACCOUNT);
            template.add("name", name);

            Messaging.send(sender, tag + template.parse());
            return false;
        }

        Account account = new Account(name);

        if(arguments.get("status").getStringValue().equalsIgnoreCase("empty")) {
            int current = account.getStatus();

            if(self)
                template.set(Template.Node.PERSONAL_STATUS);
            else {
                template.set(Template.Node.PLAYER_STATUS);
                template.add("name", name);
            }

            template.add("status", current);
            Messaging.send(sender, tag + template.parse());

        } else {
            if(!hasPermissions(sender, "status+"))
                throw new InvalidUsage("You do not have permission to do that.");

            int status = arguments.get("status").getIntegerValue();
            account.setStatus(status);

            template.set(Template.Node.ACCOUNTS_STATUS);
            template.add("status", status);
            Messaging.send(sender, tag + template.parse());
        }

        return false;
    }
}
