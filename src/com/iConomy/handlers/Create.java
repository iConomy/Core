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

public class Create extends Handler {

    private Accounts Accounts = new Accounts();

    public Create(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        String name = arguments.get("name").getStringValue();

        if(name.equals("0"))
            throw new InvalidUsage("Missing name parameter: /money create <name>");

        if(Accounts.exists(name)) {
            template.set(Template.Node.ERROR_EXISTS);
            template.add("name", name);
            Messaging.send(sender, template.parse());
            return false;
        }

        return false;
    }
}
