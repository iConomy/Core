package com.iCo6.handlers;

import java.util.LinkedHashMap;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser.Argument;
import com.iCo6.command.exceptions.InvalidUsage;

import com.iCo6.iConomy;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;

import com.iCo6.util.Messaging;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Money extends Handler {

    private Accounts Accounts = new Accounts();

    public Money(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        String name = arguments.get("name").getStringValue();

        if(name.equals("0")) {
            if(isConsole(sender)) {
                Messaging.send(sender, "`rCannot check money on non-living organism.");
                return false;
            }

            Player player = (Player) sender;

            if(player == null)
                return false;

            Account account = new Account(player.getName());
            account.getHoldings().showBalance(null);
            
            return false;
        }

        if(!Accounts.exists(name)) {
            Messaging.send(sender, "`rAccount for " + name + " does not exist!");
            return false;
        }

        Account account = new Account(name);
        account.getHoldings().showBalance(sender);
        return false;
    }
}
