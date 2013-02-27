package com.iCo6;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class Constants {
    public static enum Drivers {
        H2("http://mirror.nexua.org/Dependencies/h2.jar", "h2.jar"),
        MySQL("http://mirror.nexua.org/Dependencies/mysql-connector-java-bin.jar", "mysql.jar"),
        SQLite("http://mirror.nexua.org/Dependencies/sqlite-jdbc.jar", "sqlite.jar"),
        Postgre("http://mirror.nexua.org/Dependencies/postgresql.jdbc4.jar", "postgresql.jar");

        String url;
        String filename;

        private Drivers(String url, String filename) {
            this.url = url;
            this.filename = filename;
        }

        public String getFilename() {
            return filename;
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
        CodeName("", "Celty"),

        useHoldingsPermission("System.Permissions.Use.Holdings", false),

        Minor("System.Default.Currency.Minor", new ArrayList<String>()),
        Major("System.Default.Currency.Major", new ArrayList<String>()),

        MultiWorld("System.Default.Account.MultiWorld", false),
        Balance("System.Default.Account.Holdings", 30.0),

        AllowMinor("System.Formatting.Minor", false),
        isSplit("System.Formatting.Seperate", false),
        isSingle("System.Formatting.Single", false),

        Logging("System.Logging.Enabled", false),
        Purging("System.Purging.Enabled", true),

        Interest("System.Interest.Enabled", false),
        InterestOnline("System.Interest.Online", false),
        InterestTime("System.Interest.Interval.Seconds", 60),
        InterestPercentage("System.Interest.Amount.Percentage", 0.0),
        InterestCutoff("System.Interest.Amount.Cutoff", 0.0),
        InterestMin("System.Interest.Amount.Maximum", 1.0),
        InterestMax("System.Interest.Amount.Minimum", 2.0),

        DatabaseType("System.Database.Type", "MiniDB"),
        DatabaseTable("System.Database.Table", "iConomy"),
        DatabaseUrl("System.Database.URL", "mysql:\\\\localhost:3306\\iConomy"),
        DatabaseUsername("System.Database.Username", "root"),
        DatabasePassword("System.Database.Password", ""),
        DatabaseMajorItem("System.Database.MajorItem", 266),
        DatabaseMinorItem("System.Database.MinorItem", 265),

        Convert("System.Database.Conversion.Enabled", false),
        ConvertFrom("System.Database.Conversion.Type", "H2DB"),
        ConvertTable("System.Database.Conversion.Table", "iConomy"),
        ConvertURL("System.Database.Conversion.URL", "mysql:\\\\localhost:3306\\iConomy"),
        ConvertUsername("System.Database.Conversion.Username", "root"),
        ConvertPassword("System.Database.Conversion.Password", ""),
        ConvertAll("System.Database.Conversion.All", true);

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
            if(value instanceof Double)
                return ((Double) value).intValue();

            return (Integer) value;
        }

        public Double getDouble() {
            if(value instanceof Integer)
                return (double) ((Integer) value).intValue();

            return (Double) value;
        }

        public Long getLong() {
            if(value instanceof Integer)
                return ((Integer) value).longValue();

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

    public static void load(File configuration) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configuration);

        for(Nodes n: Nodes.values())
            if(!n.getNode().isEmpty())
                if(config.get(n.getNode()) != null)
                    n.setValue(config.get(n.getNode()));
    }
}
