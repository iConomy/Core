package com.iConomy.handlers;

import com.iConomy.command.Handler;
import com.iConomy.command.Parser.Argument;
import com.iConomy.command.exceptions.InvalidUsage;
import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Accounts;
import com.iConomy.util.Messaging;
import com.iConomy.util.Template;

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
