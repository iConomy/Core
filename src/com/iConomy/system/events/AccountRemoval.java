package com.iConomy.system.events;

import org.bukkit.event.Event;

public class AccountRemoval extends Event {
    private final String account;
    private boolean cancelled = false;

    public AccountRemoval(String account) {
        super("ACCOUNT_REMOVE");
        this.account = account;
    }

    public String getAccountName() {
        return account;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
