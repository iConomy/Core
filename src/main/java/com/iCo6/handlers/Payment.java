package com.iCo6.handlers;

import java.util.LinkedHashMap;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser.Argument;
import com.iCo6.command.exceptions.InvalidUsage;

import com.iCo6.iConomy;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;
import com.iCo6.system.Holdings;
import com.iCo6.system.events.MoneyTransactionEvent;
import com.iCo6.util.Common;

import com.iCo6.util.Messaging;
import com.iCo6.util.Template;

import org.bukkit.Bukkit;
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
        //String tag = template.color(Template.Node.TAG_MONEY);
        Double amount;

        if(name.equals("0"))
            throw new InvalidUsage("Missing <white>name<rose>: /money pay <name> <amount>");

        if(arguments.get("amount").getStringValue().equals("empty"))
            throw new InvalidUsage("Missing <white>amount<rose>: /money pay <name> <amount>");

        try {
            String amountString = arguments.get("amount").getStringValue();
            amountString = amountString.replace(",", ".");
            amount = Double.parseDouble(amountString);
        } catch(NumberFormatException e) {
            throw new InvalidUsage("Invalid <white>amount<rose>, must be double.");
        }

        if(Double.isInfinite(amount) || Double.isNaN(amount))
            throw new InvalidUsage("Invalid <white>amount<rose>, must be double.");

        if(amount < 0.1)
            throw new InvalidUsage("Invalid <white>amount<rose>, cannot be less than 0.1");

        String reason = arguments.get("reason").getStringValue();
        MoneyTransactionEvent event = null;
        if(Common.matches(from.getName(), name)) {
            event = new MoneyTransactionEvent(from, null, amount, reason, MoneyTransactionEvent.State.SELF_PAYMENT);
            event.setCancelled(true);
            Bukkit.getPluginManager().callEvent(event);
        } else if(!Accounts.exists(name)) {
            event = new MoneyTransactionEvent(from, name, amount, reason, MoneyTransactionEvent.State.ERROR_ACCOUNT);
            event.setCancelled(true);
            Bukkit.getPluginManager().callEvent(event);
        }

        Account holder = new Account(from.getName());
        Holdings holdings = holder.getHoldings();

        if(holdings.getBalance() < amount && event == null) {
            event = new MoneyTransactionEvent(from, name, amount, reason, MoneyTransactionEvent.State.NO_MONEY);
            event.setCancelled(true);
            Bukkit.getPluginManager().callEvent(event);
            //template.set(Template.Node.ERROR_FUNDS);
            //Messaging.send(sender, tag + template.parse());
            //return false;
        } else if (event == null){
            event = new MoneyTransactionEvent(from, name, amount, reason, MoneyTransactionEvent.State.COMPLETE);
            Bukkit.getPluginManager().callEvent(event);
        }

        if(event.isCancelled()) return false;

        Account account = new Account(name);
        holdings.subtract(amount);
        account.getHoldings().add(amount);

        return false;
    }
}
