package com.iConomy.handlers;

import java.util.LinkedHashMap;

import com.iConomy.command.Handler;
import com.iConomy.command.Parser.Argument;
import com.iConomy.command.exceptions.InvalidUsage;

import com.iConomy.iConomy;

import com.iConomy.util.Messaging;

import org.bukkit.command.CommandSender;

public class Give extends Handler {

    public Give(iConomy plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, LinkedHashMap<String, Argument> arguments) throws InvalidUsage {
        if(isConsole(sender))
            Messaging.send(sender, "`rCannot check money on non-living organism.");

        System.out.println();

        return false;
    }
}
