package com.iCo6.util;

import java.io.File;
import java.util.LinkedHashMap;

import org.bukkit.util.config.Configuration;

public class Template {
    private Configuration tpl = null;
    private LinkedHashMap<String, Object> arguments;
    private String currentKey;

    public Template(String directory, String filename) {
        this.tpl = new Configuration(new File(directory, filename));
        this.tpl.load();
        this.arguments = new LinkedHashMap<String, Object>();
    }

    public void upgrade() {
        LinkedHashMap<String, String> nodes = new LinkedHashMap<String, String>();

        if(!nodes.isEmpty()) {
            System.out.println(" - Upgrading Template.yml");
            int count = 1;

            for(String node : nodes.keySet()) {
                System.out.println("   Adding node [" + node + "] #" + count + " of " + nodes.size());
                this.tpl.setProperty(node, nodes.get(node));
                count++;
            }

            this.tpl.save();
            System.out.println(" + Messages Upgrade Complete.");
        }
    }

    public String raw(String key) {
        return this.tpl.getString(key);
    }

    public String raw(Node key) {
        return this.tpl.getString(key.getKey());
    }

    public String raw(String key, String line) {
        return this.tpl.getString(key, line);
    }

    public void save(String key, String line) {
        this.tpl.setProperty(key, line);
        this.tpl.save();
    }

    public Template set(String key) {
        this.currentKey = key;

        return this;
    }

    public Template set(Node node) {
        this.currentKey = node.getKey();
        return this;
    }

    public Template add(String key, Object value) {
        this.arguments.put(key, value);
        return this;
    }

    public String color() {
        if(this.currentKey != null)
            return Messaging.parse(Messaging.colorize(this.raw(this.currentKey)));

        return null;
    }

    public String parse() {
        if(this.currentKey != null)
            return Messaging.parse(Messaging.colorize(Messaging.argument(this.raw(this.currentKey), this.arguments)));

        return null;
    }

    public String color(String key) {
        return Messaging.parse(Messaging.colorize(this.raw(key)));
    }

    public String color(Node key) {
        return Messaging.parse(Messaging.colorize(this.raw(key.getKey())));
    }

    public String parse(String key, Object[] argument, Object[] points) {
        return Messaging.parse(Messaging.colorize(Messaging.argument(this.raw(key), argument, points)));
    }

    public String parse(String key, String line, Object[] argument, Object[] points) {
        return Messaging.parse(Messaging.colorize(Messaging.argument(this.raw(key, line), argument, points)));
    }

    public String parseRaw() {
        if(this.currentKey != null && !this.arguments.isEmpty())
            return Messaging.argument(this.raw(this.currentKey), this.arguments);

        return null;
    }

    public static enum Node {
        TAG_MONEY("tag.money"),

        PERSONAL_BALANCE("personal.balance"),
        PERSONAL_RESET("personal.reset"),
        PERSONAL_RANK("personal.rank"),
        PERSONAL_SET("personal.set"),
        PERSONAL_DEBIT("personal.debit"),
        PERSONAL_CREDIT("personal.credit"),
        PERSONAL_STATUS("personal.status"),

        PLAYER_BALANCE("player.balance"),
        PLAYER_RANK("player.rank"),
        PLAYER_RESET("player.reset"),
        PLAYER_SET("player.set"),
        PLAYER_CREDIT("player.credit"),
        PLAYER_DEBIT("player.debit"),
        PLAYER_STATUS("player.status"),

        PAYMENT_SELF("payment.self"),
        PAYMENT_TO("payment.to"),
        PAYMENT_FROM("payment.from"),

        STATISTICS_OPENING("statistics.opening"),
        STATISTICS_TOTAL("statistics.total"),
        STATISTICS_AVERAGE("statistics.average"),
        STATISTICS_ACCOUNTS("statistics.accounts"),

        INTEREST_ANNOUNCEMENT("interest.announcement"),

        ACCOUNTS_EMPTY("accounts.empty"),
        ACCOUNTS_PURGE("accounts.purge"),
        ACCOUNTS_CREATE("accounts.create"),
        ACCOUNTS_REMOVE("accounts.remove"),
        ACCOUNTS_STATUS("accounts.status"),

        TOP_OPENING("top.opening"),
        TOP_EMPTY("top.empty"),
        TOP_LINE("top.line"),

        ERROR_ONLINE("error.online"),
        ERROR_EXISTS("error.exists"),
        ERROR_ACCOUNT("error.account"),
        ERROR_FUNDS("error.funds"),
        ERROR_CREATE("error.create"),
        ;

        private String key;

        Node(String key) {
            this.key = key;
        }

        String getKey() {
            return this.key;
        }

        @Override
        public String toString() {
            return this.key;
        }
    }
}
