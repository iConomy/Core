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

public class Purge extends Handler {

    private Accounts Accounts = new Accounts();

    public Purge(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(!hasPermissions(sender, "purge"))
            throw new InvalidUsage("You do not have permission to do that.");

        Accounts.purge();

        String tag = template.color(Template.Node.TAG_MONEY);
        template.set(Template.Node.ACCOUNTS_PURGE);
        Messaging.send(sender, tag + template.parse());

        return false;
    }
}
