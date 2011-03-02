package com.nijiko.coelho.iConomy.util;

import java.io.File;

import org.bukkit.util.config.Configuration;

public class Constants {
	
	// Code name
	public static final String Codename = "Kristen";

	// Files and Directories
	public static File Configuration;
	public static String Plugin_Directory;

	// System Data
	public static String Currency = "Coin";
	public static double Initial_Balance = 45.0;
	
	// System Logging
	public static boolean Log_Data = false;
	
	// System Interest
	public static boolean Interest = false;
	public static int Interest_Interval = 60;
	public static double Interest_Min_Interval = 1;
	public static double Interest_Max_Interval = 2;

	// Database Type
	public static String Database_Type = "MySQL";

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

		if(config.getProperty("System.Logging.Enabled") == null
				|| config.getProperty("System.Currency") == null
				|| config.getProperty("System.Initial_Balance") == null
				|| config.getProperty("System.Interest.Enabled") == null
				|| config.getProperty("System.Interest.IntervalSeconds") == null
				|| config.getProperty("System.Interest.MinimumPerInterval") == null
				|| config.getProperty("System.Interest.MaximumPerInterval") == null
				|| config.getProperty("System.Database.Type") == null
				|| config.getProperty("System.Database.MySQL.Hostname") == null
				|| config.getProperty("System.Database.MySQL.Port") == null
				|| config.getProperty("System.Database.MySQL.Username") == null
				|| config.getProperty("System.Database.MySQL.Password") == null
				|| config.getProperty("System.Database.Name") == null
				|| config.getProperty("System.Database.Table") == null) {
			System.out.println("[iConomy] Certain nodes in the properties are missing.");
			System.out.println("[iConomy] Please backup your current iConomy.yml and let us recreate it.");
		}
	}
}
