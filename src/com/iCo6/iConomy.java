package com.iCo6;

import com.iCo6.Constants.Drivers;
import java.io.File;
import java.util.Locale;

import com.nijikokun.bukkit.Permissions.Permissions;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.handlers.*;
import com.iCo6.IO.Database;
import com.iCo6.IO.Database.Type;
import com.iCo6.IO.exceptions.MissingDriver;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;
import com.iCo6.system.Holdings;
import com.iCo6.util.Common;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;

import java.sql.Connection;
import java.sql.SQLException;

import com.iCo6.util.org.apache.commons.dbutils.DbUtils;
import com.iCo6.util.org.apache.commons.dbutils.QueryRunner;
import com.iCo6.util.wget;
import java.text.DecimalFormat;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class iConomy extends JavaPlugin {
    public PluginDescriptionFile info;
    public PluginManager manager;

    private static Accounts Accounts = new Accounts();
    private Parser Commands = new Parser();
    private Permissions Permissions;

    public static boolean TerminalSupport = false;
    public static File directory;
    public static Database Database;
    public static Server Server;
    public static Template Template;

    public void onEnable() {
        final long startTime = System.nanoTime();
        final long endTime;

        try {
            // Localize locale to prevent issues.
            Locale.setDefault(Locale.US);

            // Server & Terminal Support
            Server = getServer();
            TerminalSupport = ((CraftServer)getServer()).getReader().getTerminal().isANSISupported();

            // Get general plugin information
            info = getDescription();

            // Plugin directory setup
            directory = getDataFolder();
            if(!directory.exists()) directory.mkdir();

            // Extract Files
            Common.extract("Config.yml", "Template.yml");

            // Setup Configuration
            Constants.load(new Configuration(new File(directory, "Config.yml")));

            // Setup Template
            Template = new Template(directory.getPath(), "Template.yml");
            
            // Check Drivers if needed
            Type type = Database.getType(Constants.Nodes.DatabaseType.toString());
            if(!(type.equals(Type.InventoryDB) || type.equals(Type.MiniDB))) {
                Drivers driver = null;
                
                switch(type) {
                    case H2DB: driver = Constants.Drivers.H2; break;
                    case MySQL: driver = Constants.Drivers.MySQL; break;
                    case SQLite: driver = Constants.Drivers.SQLite; break;
                    case Postgre: driver = Constants.Drivers.Postgre; break;
                }

                if(driver != null)
                    if(!(new File("lib", driver.getFilename()).exists())) {
                        System.out.println("[iConomy] Downloading " + driver.getFilename() + "...");
                        wget.fetch(driver.getUrl(), driver.getFilename());
                        System.out.println("[iConomy] Finished Downloading.");
                    }
            }

            // Setup Commands
            Commands.add("/money +name", new Money(this));
            Commands.setPermission("money", "iConomy.holdings");
            Commands.setPermission("money+", "iConomy.holdings.others");

            Commands.add("/money -h|?|help", new Help(this));
            Commands.setPermission("help", "iConomy.help");

            Commands.add("/money -p|pay +name +amount:empty", new Payment(this));
            Commands.setPermission("pay", "iConomy.payment");

            Commands.add("/money -c|create +name", new Create(this));
            Commands.setPermission("create", "iConomy.accounts.create");

            Commands.add("/money -r|remove +name", new Remove(this));
            Commands.setPermission("remove", "iConomy.accounts.remove");

            Commands.add("/money -g|give +name +amount:empty", new Give(this));
            Commands.setPermission("give", "iConomy.accounts.give");

            Commands.add("/money -t|take +name +amount:empty", new Take(this));
            Commands.setPermission("take", "iConomy.accounts.take");

            Commands.add("/money -s|set +name +amount:empty", new Set(this));
            Commands.setPermission("set", "iConomy.accounts.set");

            Commands.add("/money -u|status +name +status:empty", new Status(this));
            Commands.setPermission("status", "iConomy.accounts.status");
            Commands.setPermission("status+", "iConomy.accounts.status.set");

            // Setup Database.
            try {
                Database = new Database(
                    Constants.Nodes.DatabaseType.toString(),
                    Constants.Nodes.DatabaseUrl.toString(),
                    Constants.Nodes.DatabaseUsername.toString(),
                    Constants.Nodes.DatabasePassword.toString()
                );

                // Check to see if it's a binary database, if so, check the database existance
                // If it doesn't exist, Create one.
                if(Database.getDatabase() == null && Database.getInventoryDatabase() == null)
                    if(!Database.tableExists(Constants.Nodes.DatabaseTable.toString())) {
                        System.out.println("should create table..");
                        String SQL = Common.resourceToString("SQL/Core/Create-Table-" + Database.getType().toString().toLowerCase() + ".sql");
                        SQL = String.format(SQL, Constants.Nodes.DatabaseTable.getValue());

                        try {
                            QueryRunner run = new QueryRunner();
                            Connection c = iConomy.Database.getConnection();

                            try{
                                run.update(c, SQL);
                            } catch (SQLException ex) {
                                System.out.println("[iConomy] Error creating database: " + ex);
                            } finally {
                                DbUtils.close(c);
                            }
                        } catch (SQLException ex) {
                            System.out.println("[iConomy] Database Error: " + ex);
                        }
                    }

            } catch (MissingDriver ex) {
                System.out.println(ex.getMessage());
            }

            // Test account creation / existance.
            String name = "Nijikokun";

            System.out.println(name + " exists? " + Accounts.exists(name));

            Account Nijikokun = Accounts.get(name);
            Holdings holdings = Nijikokun.getHoldings();

            System.out.println("Balance: " + holdings.getBalance());
        } finally {
          endTime = System.nanoTime();
        }

        final long duration = endTime - startTime;

        // Finish
        System.out.println("[" + info.getName() + "] Enabled (" + Common.readableProfile(duration) + ")");
    }

    public void onDisable() {
        String name = info.getName();
        System.out.println("[" + name + "] Closing general data...");

        // Start Time Logging
        final long startTime = System.nanoTime();
        final long endTime;

        // Disable Startup information to prevent
        // duplicate information on /reload
        try {
            info = null;
            Server = null;
            manager = null;
            Accounts = null;
            Commands = null;
            Database = null;
            Template = null;
            TerminalSupport = false;
        } finally {
          endTime = System.nanoTime();
        }

        // Finish duration
        final long duration = endTime - startTime;

        // Output finished & time.
        System.out.println("[" + name + "] Disabled. (" + Common.readableProfile(duration) + ")");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Handler handler = Commands.getHandler(command.getName());
        String split = "/" + command.getName().toLowerCase();

        for (int i = 0; i < args.length; i++) {
            split = split + " " + args[i];
        }

        Messaging.save(sender);
        Commands.save(split);
        Commands.parse();

        if(Commands.getHandler() != null)
            handler = Commands.getHandler();

        if(handler == null) return false;

        try {
            return handler.perform(sender, Commands.getArguments());
        } catch (InvalidUsage ex) {
            Messaging.send(sender, ex.getMessage());
            return false;
        }
    }

    public boolean hasPermissions(CommandSender sender, String command) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            if(Commands.hasPermission(command)) {
                String node = Commands.getPermission(command);

                if(this.Permissions != null)
                    return Permissions.Security.permission(player, node);
                else {
                    return player.isOp();
                }
            }
        }

        return true;
    }

    /**
     * Formats the holding balance in a human readable form with the currency attached:<br /><br />
     * 20000.53 = 20,000.53 Coin<br />
     * 20000.00 = 20,000 Coin
     *
     * @param account The name of the account you wish to be formatted
     * @return String
     */
    public static String format(String account) {
        return Accounts.get(account).getHoldings().toString();
    }

    /**
     * Formats the money in a human readable form with the currency attached:<br /><br />
     * 20000.53 = 20,000.53 Coin<br />
     * 20000.00 = 20,000 Coin
     *
     * @param amount double
     * @return String
     */
    public static String format(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String formatted = formatter.format(amount);

        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return Common.formatted(formatted, Constants.Nodes.Major.getStringList(), Constants.Nodes.Minor.getStringList());
    }
}
