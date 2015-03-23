package com.iCo.handlers;

import com.iCo.command.Handler;
import com.iCo.command.Parser.Argument;
import com.iCo.command.exceptions.InvalidUsage;
import com.iCo.iConomy;
import com.iCo.system.Account;
import com.iCo.system.Accounts;
import com.iCo.util.Messaging;
import com.iCo.util.Template;

import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.command.CommandSender;

public class Top extends Handler {
    private Accounts Accounts = new Accounts();

    public Top(iConomy plugin) {
        super(plugin, plugin.Template);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(!hasPermissions(sender, "top"))
            throw new InvalidUsage("You do not have permission to do that.");

        template.set(Template.Node.TOP_OPENING);
        Messaging.send(sender, template.parse());

        template.set(Template.Node.TOP_ITEM);
        List<Account> top = Accounts.getTopAccounts(5);
        for (int i = 0; i < top.size(); i++) {
            Account account = top.get(i);
            template.add("i", i + 1);
            template.add("name", account.name);
            template.add("amount", account.getHoldings().toString());
            Messaging.send(sender, template.parse());
        }

        return false;
    }
}
