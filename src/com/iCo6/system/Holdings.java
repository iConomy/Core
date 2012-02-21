package com.iCo6.system;

import com.iCo6.Constants;
import com.iCo6.iConomy;
import com.iCo6.system.events.HoldingsUpdate;
import com.iCo6.util.Common;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import java.text.DecimalFormat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Holdings {
    private String name;
    private Queried database = new Queried();

    public Holdings(String name) {
        this.name = name;
    }

    public Double getBalance() {
        return Queried.getBalance(this.name);
    }

    public void setBalance(double balance) {
        Queried.setBalance(this.name, balance);
    }

    public void showBalance(CommandSender to) {
        if(to != null) {
            String tag = iConomy.Template.raw(Template.Node.TAG_MONEY);

            Template template = iConomy.Template;
            template.set(Template.Node.PLAYER_BALANCE);
            template.add("name", name);
            template.add("balance", toString());

            Messaging.send(to, tag + template.parse());
            return;
        }

        Player player = iConomy.Server.getPlayer(name);
        String tag = iConomy.Template.color(Template.Node.TAG_MONEY);

        if(iConomy.Server.getPlayer(name) == null)
            return;

        Template template = iConomy.Template;
        template.set(Template.Node.PERSONAL_BALANCE);
        template.add("balance", toString());
        Messaging.send(player, tag + template.parse());
    }

    public void add(double amount) {
        double balance = this.getBalance();
        double ending = (balance + amount);

        this.math(amount, balance, ending);
    }

    public void subtract(double amount) {
        double balance = this.getBalance();
        double ending = (balance - amount);

        this.math(amount, balance, ending);
    }

    public void divide(double amount) {
        double balance = this.getBalance();
        double ending = (balance / amount);

        this.math(amount, balance, ending);
    }

    public void multiply(double amount) {
        double balance = this.getBalance();
        double ending = (balance * amount);

        this.math(amount, balance, ending);
    }

    public boolean isNegative() {
        return this.getBalance() < 0.0;
    }

    public boolean hasEnough(double amount) {
        return amount <= this.getBalance();
    }

    public boolean hasOver(double amount) {
        return amount < this.getBalance();
    }

    public boolean hasUnder(double amount) {
        return amount > this.getBalance();
    }

    private void math(double amount, double balance, double ending) {
        HoldingsUpdate Event = new HoldingsUpdate(this.name, balance, ending, amount);
        iConomy.Server.getPluginManager().callEvent(Event);

        if(!Event.isCancelled())
            setBalance(ending);
    }

    @Override
    public String toString() {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        Double balance = this.getBalance();
        String formatted = formatter.format(balance);

        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return Common.formatted(formatted, Constants.Nodes.Major.getStringList(), Constants.Nodes.Minor.getStringList());
    }
}