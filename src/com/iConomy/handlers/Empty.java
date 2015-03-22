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

public class Empty extends Handler {

    private Accounts Accounts = new Accounts();

    public Empty(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(!hasPermissions(sender, "empty"))
            throw new InvalidUsage("You do not have permission to do that.");

        Accounts.empty();

        String tag = template.color(Template.Node.TAG_MONEY);
        template.set(Template.Node.ACCOUNTS_EMPTY);
        Messaging.send(sender, tag + template.parse());

        return false;
    }
}
