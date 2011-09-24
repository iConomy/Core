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

public class Set extends Handler {
    private Accounts Accounts = new Accounts();

    public Set(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(!hasPermissions(sender, "set"))
            throw new InvalidUsage("You do not have permission to do that.");

        String name = arguments.get("name").getStringValue();
        String tag = template.color(Template.Node.TAG_MONEY);
        Double amount;

        if(name.equals("0"))
            throw new InvalidUsage("Missing <white>name<rose>: /money set <name> <amount>");

        if(arguments.get("amount").getStringValue().equals("empty"))
            throw new InvalidUsage("Missing <white>amount<rose>: /money set <name> <amount>");

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
        account.getHoldings().setBalance(amount);

        template.set(Template.Node.PLAYER_SET);
        template.add("name", name);
        template.add("amount", account.getHoldings().toString());

        Messaging.send(sender, tag + template.parse());
        return false;
    }
}
