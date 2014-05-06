package com.iCo6.system;

import java.util.UUID;

import com.iCo6.iConomy;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Account {
    public String name;
    public UUID uuid;

    public Account(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public Account(String name, Boolean create, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }
    
    public Account(String name) {
    	this.name = name;
    	this.uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    public void showHoldings(boolean console) {
        if(console)
            return;

        Player player = iConomy.Server.getPlayer(name);
        if(iConomy.Server.getPlayer(name) == null)
            return;

        String tag = iConomy.Template.color(Template.Node.TAG_MONEY);

        Template template = iConomy.Template;
        template.set(Template.Node.PERSONAL_BALANCE);
        template.add("balance", getHoldings().getBalance());

        Messaging.send(player, tag + template.parse());
    }

    public Holdings getHoldings() {
        return new Holdings(this.name);
    }

    public Integer getStatus() {
        return Queried.getStatus(this.name, this.uuid);
    }

    public void setStatus(int status) {
        Queried.setStatus(this.name, status);
    }
    
    public void updateName(String newName) {
    	Queried.updateName(this.name, newName);
    }

    public boolean remove() {
        return Queried.removeAccount(this.name);
    }

    @Override
    public String toString() {
        String tag = iConomy.Template.raw(Template.Node.TAG_MONEY);

        Template template = iConomy.Template;
        template.set(Template.Node.PLAYER_BALANCE);
        template.add("name", name);
        template.add("balance", getHoldings().getBalance());

        return tag + template.parseRaw();
    }
}
