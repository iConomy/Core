package com.nijiko.coelho.iConomy.events;

import org.bukkit.event.Event;

public class AccountResetEvent extends Event {
    private final String account;
    private boolean cancelled = false;

    public AccountResetEvent(String account) {
        super("ACCOUNT_RESET");
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

