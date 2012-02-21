package com.iCo6.system.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HoldingsSet extends Event {
    private final String account;
    private double balance;

    public HoldingsSet(String account, double balance) {
        this.account = account;
        this.balance = balance;
    }

    public String getAccountName() {
        return account;
    }

    public double getBalance() {
        return balance;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
