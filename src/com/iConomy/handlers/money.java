package com.iConomy.handlers;

import java.util.LinkedHashMap;

import com.iConomy.command.Handler;
import com.iConomy.command.Parser.Argument;
import com.iConomy.command.exceptions.InvalidUsage;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Accounts;

import com.iConomy.util.Messaging;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class money extends Handler {

    private Accounts Accounts = new Accounts();

    public money(iConomy plugin) {
        super(plugin);
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
