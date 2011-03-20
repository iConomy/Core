package com.nijiko.coelho.iConomy.util;

import java.io.File;

import org.bukkit.util.config.Configuration;

public class Constants {
    // Code name
    public static final String Codename = "Elektra";

    // Nodes
    private static String[] nodes = new String[] {
        "System.Logging.Enabled:false",
        "System.Currency:Coin",
        "System.Initial_Balance:45.0",
        "System.Interest.Enabled:false",
        "System.Interest.IntervalSeconds:60",
        "System.Interest.FlatRate:0.0",
        "System.Interest.MinimumPerInterval:1",
        "System.Interest.MaximumPerInterval:2",
        "System.Database.Type:SQLite",
        "System.Database.MySQL.Hostname:localhost",
        "System.Database.MySQL.Port:3306",
        "System.Database.MySQL.Username:root",
        "System.Database.MySQL.Password:none",
        "System.Database.Name:minecraft",
        "System.Database.Table:iConomy",
    };

    // Files and Directories
    public static File Configuration;
    public static String Plugin_Directory;
    public static String SQLite_Jar_Location = "http://mirror.anigaiku.com/Dependencies/sqlitejdbc-v056.jar";
    public static String MySQL_Jar_Location = "http://mirror.anigaiku.com/Dependencies/mysql-connector-java-bin.jar";


    // System Data
    public static String Currency = "Coin";
    public static double Initial_Balance = 45.0;

    // System Logging
    public static boolean Log_Data = false;

    // System Interest
    public static boolean Interest = false;
    public static double Interest_FlatRate = 0.0;
    public static int Interest_Interval = 60;
    public static double Interest_Min_Interval = 1;
    public static double Interest_Max_Interval = 2;

    // Database Type
    public static String Database_Type = "SQLite";

    // Relational SQL Generics
    public static String SQL_Hostname = "localhost";
    public static String SQL_Port = "3306";
    public static String SQL_Username = "root";
    public static String SQL_Password = "";

    // SQL Generics
    public static String SQL_Database = "minecraft";
    public static String SQL_Table = "iConomy";

    public static void load(Configuration config) {
        config.load();

        // System Configuration
        Currency = config.getString("System.Currency", Currency);
        Initial_Balance = config.getDouble("System.Initial_Balance", Initial_Balance);

        // System Logging
        Log_Data = config.getBoolean("System.Logging.Enabled", Log_Data);

        // System Interest
        Interest = config.getBoolean("System.Interest.Enabled", Interest);
        Interest_FlatRate = config.getDouble("System.Interest.FlatRate", Interest_FlatRate);
        Interest_Interval = config.getInt("System.Interest.IntervalSeconds", Interest_Interval);
        Interest_Min_Interval = config.getDouble("System.Interest.MinimumPerInterval", Interest_Min_Interval);
        Interest_Max_Interval = config.getDouble("System.Interest.MaximumPerInterval", Interest_Max_Interval);

        // Database Configuration
        Database_Type = config.getString("System.Database.Type", Database_Type);

        // MySQL
        SQL_Hostname = config.getString("System.Database.MySQL.Hostname", SQL_Hostname);
        SQL_Port = config.getString("System.Database.MySQL.Port", SQL_Port);
        SQL_Username = config.getString("System.Database.MySQL.Username", SQL_Username);
        SQL_Password = config.getString("System.Database.MySQL.Password", SQL_Password);

        // SQLite
        SQL_Database = config.getString("System.Database.Name", SQL_Database);
        SQL_Table = config.getString("System.Database.Table", SQL_Table);

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