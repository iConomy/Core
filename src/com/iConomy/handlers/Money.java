package com.iConomy.handlers;

import com.iConomy.Constants;
import com.iConomy.command.Handler;
import com.iConomy.command.Parser.Argument;
import com.iConomy.command.exceptions.InvalidUsage;
import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Accounts;
import com.iConomy.util.Messaging;
import com.iConomy.util.Template;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

public class Money extends Handler {

    private Accounts Accounts = new Accounts();

    public Money(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(Constants.Nodes.useHoldingsPermission.getBoolean())
            if(!hasPermissions(sender, "money"))
                throw new InvalidUsage("You do not have permission to do that.");

        String name = arguments.get("name").getStringValue();
        String tag = template.color(Template.Node.TAG_MONEY);

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

        if(!hasPermissions(sender, "money+"))
            throw new InvalidUsage("You do not have permission to do that.");

        if(!Accounts.exists(name)) {
            template.set(Template.Node.ERROR_ACCOUNT);
            template.add("name", name);

            Messaging.send(sender, tag + template.parse());
            return false;
        }

        Account account = new Account(name);
        account.getHoldings().showBalance(sender);
        return false;
    }
}
