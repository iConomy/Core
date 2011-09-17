package com.iCo6.handlers;

import java.util.LinkedHashMap;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser.Argument;
import com.iCo6.command.exceptions.InvalidUsage;

import com.iCo6.iConomy;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;

import com.iCo6.util.Messaging;
import com.iCo6.util.Template;

import org.bukkit.command.CommandSender;

public class Give extends Handler {

    private Accounts Accounts = new Accounts();

    public Give(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(!hasPermissions(sender, "give"))
            throw new InvalidUsage("You do not have permission to do that.");

        String name = arguments.get("name").getStringValue();
        String tag = template.color(Template.Node.TAG_MONEY);
        Double amount;

        if(name.equals("0"))
            throw new InvalidUsage("Missing <white>name<rose>: /money give <name> <amount>");

        if(arguments.get("amount").getStringValue().equals("empty"))
            throw new InvalidUsage("Missing <white>amount<rose>: /money give <name> <amount>");

        try {
            amount = arguments.get("amount").getDoubleValue();
        } catch(NumberFormatException e) {
            throw new InvalidUsage("Invalid <white>amount<rose>, must be double.");
        }

        if(Double.isInfinite(amount) || Double.isNaN(amount))
            throw new InvalidUsage("Invalid <white>amount<rose>, must be double.");

        if(!Accounts.exists(name)) {
            template.set(Template.Node.ERROR_ACCOUNT);
            template.add("name", name);

            Messaging.send(sender, tag + template.parse());
            return false;
        }

        Account account = new Account(name);
        account.getHoldings().add(amount);

        template.set(Template.Node.PLAYER_CREDIT);
        template.add("name", name);
        template.add("amount", iConomy.format(amount));

        Messaging.send(sender, tag + template.parse());
        return false;
    }
}
