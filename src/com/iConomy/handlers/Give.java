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

public class Give extends Handler {

    private Accounts Accounts = new Accounts();

    public Give(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        String name = arguments.get("name").getStringValue();
        String tag = template.color(Template.Node.TAG_MONEY);
        Double amount;

        if(name.equals("0"))
            throw new InvalidUsage("Missing name parameter: /money give <name> <amount>");

        if(arguments.get("amount").getStringValue().equals("empty"))
            throw new InvalidUsage("Missing amount parameter: /money give <name> <amount>");

        try {
            amount = arguments.get("amount").getDoubleValue();
        } catch(NumberFormatException e) {
            throw new InvalidUsage("Invalid amount parameter, must be double.");
        }

        if(Double.isInfinite(amount) || Double.isNaN(amount))
            throw new InvalidUsage("Invalid amount parameter, must be double.");

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
        template.add("balance", account.getHoldings().toString());

        Messaging.send(sender, tag + template.parse());
        return false;
    }
}
