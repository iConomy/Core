package com.iConomy;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.config.Configuration;

public class Constants {
    public static enum Drivers {
        H2("http://mirror.nexua.org/Dependencies/h2.jar"),
        MySQL("http://mirror.nexua.org/Dependencies/mysql-connector-java-bin.jar"),
        SQLite("http://mirror.nexua.org/Dependencies/sqlite-jdbc.jar"),
        Postgre("http://mirror.nexua.org/Dependencies/postgresql.jdbc4.jar");

        String url;

        private Drivers(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return url;
        }
    }

    public static enum Nodes {
        Minor("System.Default.Currency.Minor", new ArrayList<String>()),
        Major("System.Default.Currency.Major", new ArrayList<String>()),

        MultiWorld("System.Default.Account.MultiWorld", false),
        Balance("System.Default.Account.Holdings", 30.0),

        AllowMinor("System.Formatting.Minor", false),
        isSplit("System.Formatting.Seperate", false),
        isSingle("System.Formatting.Single", false),

        Logging("System.Logging.Enabled", false),

        Interest("System.Interest.Enabled", false),
        InterestOnline("System.Interest.Online", false),
        InterestTimer("System.Interest.Interval", 60),
        InterestPercentage("System.Interest.Amount.Percentage", 0.0),
        InterestCutoff("System.Interest.Amount.Cutoff", 0.0),
        InterestMin("System.Interest.Amount.Maximum", 1.0),
        InterestMax("System.Interest.Amount.Minimum", 2.0),

        DatabaseType("System.Database.Type", "MiniDB"),
        DatabaseTable("System.Database.Table", "iConomy"),
        DatabaseUrl("System.Database.URL", "mysql:\\\\localhost:3306\\iConomy"),
        DatabaseUsername("System.Database.Username", "root"),
        DatabasePassword("System.Database.Password", ""),
        DatabaseMajorItem("System.Database.MajorItem", 0),
        DatabaseMinorItem("System.Database.MinorItem", 0);

        String node;
        Object value;

        private Nodes(String node, Object value) {
            this.node = node;
            this.value = value;
        }

        public String getNode() {
            return node;
        }

        public Object getValue() {
            return this.value;
        }

        public Boolean getBoolean() {
            return (Boolean) value;
        }

        public Integer getInteger() {
            return (Integer) value;
        }

        public Double getDouble() {
            return (Double) value;
        }

        public Long getLong() {
            return (Long) value;
        }

        public List<String> getStringList() {
            return (List<String>) value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public static void load(Configuration config) {
        config.load();

        for(Nodes n: Nodes.values())
            if(config.getProperty(n.getNode()) != null)
                n.setValue(config.getProperty(n.getNode()));
    }
}
