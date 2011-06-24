package com.iConomy.system.events;

import org.bukkit.event.Event;

public class HoldingsSet extends Event {
    private final String account;
    private double balance;

    public HoldingsSet(String account, double balance) {
        super("ACCOUNT_UPDATE");
        this.account = account;
        this.balance = balance;
    }

    public String getAccountName() {
        return account;
    }

    public double getBalance() {
        return balance;
    }
}
