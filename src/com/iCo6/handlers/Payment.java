package com.iCo6.handlers;

import java.util.LinkedHashMap;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser.Argument;
import com.iCo6.command.exceptions.InvalidUsage;

import com.iCo6.iConomy;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;
import com.iCo6.system.Holdings;
import com.iCo6.util.Common;

import com.iCo6.util.Messaging;
import com.iCo6.util.Template;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Payment extends Handler {

    private Accounts Accounts = new Accounts();

    public Payment(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(!hasPermissions(sender, "pay"))
            return false;

        if(isConsole(sender)) {
            Messaging.send(sender, "`rCannot remove money from a non-living organism.");
            return false;
        }

        Player from = (Player) sender;
        String name = arguments.get("name").getStringValue();
        String tag = template.color(Template.Node.TAG_MONEY);
        Double amount;

        if(name.equals("0"))
            throw new InvalidUsage("Missing <white>name<rose>: /money pay <name> <amount>");

        if(arguments.get("amount").getStringValue().equals("empty"))
            throw new InvalidUsage("Missing <white>amount<rose>: /money pay <name> <amount>");

        try {
            amount = arguments.get("amount").getDoubleValue();
        } catch(NumberFormatException e) {
            throw new InvalidUsage("Invalid <white>amount<rose>, must be double.");
        }

        if(Double.isInfinite(amount) || Double.isNaN(amount))
            throw new InvalidUsage("Invalid <white>amount<rose>, must be double.");

        if(amount < 0.1)
            throw new InvalidUsage("Invalid <white>amount<rose>, cannot be less than 0.1");

        if(Common.matches(from.getName(), name)) {
            template.set(Template.Node.PAYMENT_SELF);
            Messaging.send(sender, template.parse());
            return false;
        }

        if(!Accounts.exists(name)) {
            template.set(Template.Node.ERROR_ACCOUNT);
            template.add("name", name);

            Messaging.send(sender, tag + template.parse());
            return false;
        }

        Account holder = new Account(from.getName());
        Holdings holdings = holder.getHoldings();

        if(holdings.getBalance() < amount) {
            template.set(Template.Node.ERROR_FUNDS);
            Messaging.send(sender, tag + template.parse());
            return false;
        }

        Account account = new Account(name);
        holdings.subtract(amount);
        account.getHoldings().add(amount);

        template.set(Template.Node.PAYMENT_TO);
        template.add("name", name);
        template.add("amount", iConomy.format(amount));
        Messaging.send(sender, tag + template.parse());

        Player to = iConomy.Server.getPlayer(name);

        if(to != null) {
            template.set(Template.Node.PAYMENT_FROM);
            template.add("name", from.getName());
            template.add("amount", iConomy.format(amount));

            Messaging.send(to, tag + template.parse());
        }

        return false;
    }
}
