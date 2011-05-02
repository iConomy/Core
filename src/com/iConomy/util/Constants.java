package com.iConomy.util;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.util.config.Configuration;

public class Constants {
    // Code name
    public static final String Codename = "Eruanna";

    // Nodes
    private static String[] nodes = new String[] {
        "System.Banking.Enabled:false",
        "System.Logging.Enabled:false",
        "System.Interest.Enabled:false",
        "System.Interest.Announce.Enabled:false",

        "System.Default.Account.Holdings:30.0",
        "System.Default.Currency.Major:[ 'Dollar', 'Dollars' ]",
        "System.Default.Currency.Minor:[ 'Coin', 'Coins' ]",

        "System.Default.Bank.Name:iConomy",
        "System.Default.Bank.Account.Fee:30.0",
        "System.Default.Bank.Account.Holdings:30.0",
        "System.Default.Bank.Currency.Major:[ 'Dollar', 'Dollars' ]",
        "System.Default.Bank.Currency.Minor:[ 'Coin', 'Coins' ]",

        "System.Banking.Accounts.Multiple:true",

        "System.Formatting.Minor:true",
        "System.Formatting.Seperate:false",

        "System.Interest.Online:true",
        "System.Interest.Interval.Seconds:60",
        "System.Interest.Amount.Cutoff:0.0",
        "System.Interest.Amount.On:Players",
        "System.Interest.Amount.Percent:0.0",
        "System.Interest.Amount.Minimum:1",
        "System.Interest.Amount.Maximum:2",

        "System.Database.Type:H2SQL",
        "System.Database.Settings.Name:minecraft",
        "System.Database.Settings.Table:iConomy",

        "System.Database.Settings.MySQL.Hostname:localhost",
        "System.Database.Settings.MySQL.Port:3306",
        "System.Database.Settings.MySQL.Username:root",
        "System.Database.Settings.MySQL.Password:none",
    };

    // Files and Directories
    public static File Configuration;
    public static String Plugin_Directory;
    public static String H2_Jar_Location = "http://mirror.nexua.org/Dependencies/h2.jar";
    public static String MySQL_Jar_Location = "http://mirror.anigaiku.com/Dependencies/mysql-connector-java-bin.jar";

    // iConomy basics
    public static List<String> Major = new LinkedList<String>();
    public static List<String> Minor = new LinkedList<String>();
    public static double Holdings = 30.0;

    // iConomy Bank
    public static boolean Banking = false;
    public static boolean BankingMultiple = true;
    public static String BankName = "iConomy";
    public static List<String> BankMajor = new LinkedList<String>();
    public static List<String> BankMinor = new LinkedList<String>();
    public static double BankHoldings = 30.0;
    public static double BankFee = 20.0;

    // System formatting
    public static boolean FormatMinor = false;
    public static boolean FormatSeperated = false;

    // System Logging
    public static boolean Logging = false;

    // System Interest
    public static int InterestSeconds = 60;
    public static boolean Interest = false;
    public static boolean InterestAnn = false;
    public static boolean InterestOnline = false;
    public static String InterestType = "Players";
    public static double InterestCutoff = 0.0;
    public static double InterestPercentage = 0.0;
    public static double InterestMin = 1;
    public static double InterestMax = 2;

    // Database Type
    public static String DatabaseType = "H2SQL";

    // Relational SQL Generics
    public static String SQLHostname = "localhost";
    public static String SQLPort = "3306";
    public static String SQLUsername = "root";
    public static String SQLPassword = "";

    // SQL Generics
    public static String SQLDatabase = "minecraft";
    public static String SQLTable = "iConomy";

    public static void load(Configuration config) {
        config.load();

        Major.add("Dollar"); Major.add("Dollars"); BankMajor.add("Dollar"); BankMajor.add("Dollars");
        Minor.add("Coin"); Minor.add("Coins"); BankMinor.add("Coin"); BankMinor.add("Coins");

        // System Configuration
        Major = config.getStringList("System.Default.Currency.Major", Major);
        Minor = config.getStringList("System.Default.Currency.Minor", Minor);
        Holdings = config.getDouble("System.Default.Account.Holdings", Holdings);

        // System Bank
        Banking = config.getBoolean("System.Banking.Enabled", Banking);
        BankingMultiple = config.getBoolean("System.Banking.Accounts.Multiple", BankingMultiple);
        BankName = config.getString("System.Default.Bank.Name", BankName);
        BankMajor = config.getStringList("System.Default.Bank.Currency.Major", BankMajor);
        BankMinor = config.getStringList("System.Default.Bank.Currency.Minor", BankMinor);
        BankHoldings = config.getDouble("System.Default.Bank.Account.Holdings", BankHoldings);
        BankFee = config.getDouble("System.Default.Bank.Account.Fee", BankFee);

        // System Logging
        Logging = config.getBoolean("System.Logging.Enabled", Logging);

        // Formatting
        FormatMinor = config.getBoolean("System.Formatting.Minor", FormatMinor);
        FormatSeperated = config.getBoolean("System.Formatting.Seperate", FormatSeperated);

        // System Interest
        Interest = config.getBoolean("System.Interest.Enabled", Interest);
        InterestOnline = config.getBoolean("System.Interest.Online", InterestOnline);
        InterestType = config.getString("System.Interest.Amount.On", InterestType);
        InterestAnn = config.getBoolean("System.Interest.Announce.Enabled", InterestAnn);
        InterestSeconds = config.getInt("System.Interest.Interval.Seconds", InterestSeconds);
        InterestPercentage = config.getDouble("System.Interest.Amount.Percent", InterestPercentage);
        InterestCutoff = config.getDouble("System.Interest.Amount.Cutoff", InterestCutoff);
        InterestMin = config.getDouble("System.Interest.Amount.Minimum", InterestMin);
        InterestMax = config.getDouble("System.Interest.Amount.Maximum", InterestMax);

        // Database Configuration
        DatabaseType = config.getString("System.Database.Type", DatabaseType);

        // Generic
        SQLDatabase = config.getString("System.Database.Settings.Name", SQLDatabase);
        SQLTable = config.getString("System.Database.Settings.Table", SQLTable);

        // MySQL
        SQLHostname = config.getString("System.Database.Settings.MySQL.Hostname", SQLHostname);
        SQLPort = config.getString("System.Database.Settings.MySQL.Port", SQLPort);
        SQLUsername = config.getString("System.Database.Settings.MySQL.Username", SQLUsername);
        SQLPassword = config.getString("System.Database.Settings.MySQL.Password", SQLPassword);

        int i = 0;

        for(String node : nodes) {
            if(config.getProperty(node.split(":")[0]) == null) {
                i++;
            }
        }

        if(i != 0) {
            System.out.println("[iConomy] Configuration Integrity Start:");

            for(String node : nodes) {
                if(config.getProperty(node.split(":")[0]) == null) {
                    System.out.println("    - "+ node.split(":")[0] +" is null or missing, Defaulting to: " + node.split(":")[1]);
                }
            }

            System.out.println("[iConomy] Configuration Integrity End.");
        }
    }
}