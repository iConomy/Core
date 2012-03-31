package com.iCo6;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Locale;

import com.nijikokun.bukkit.Permissions.Permissions;

import com.iCo6.Constants.Drivers;
import com.iCo6.command.Handler;
import com.iCo6.command.Parser;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.handlers.*;
import com.iCo6.IO.Database;
import com.iCo6.IO.Database.Type;
import com.iCo6.IO.exceptions.MissingDriver;
import com.iCo6.listeners.players;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;
import com.iCo6.system.Interest;
import com.iCo6.system.Queried;
import com.iCo6.util.Common;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import com.iCo6.util.Thrun;
import com.iCo6.util.org.apache.commons.dbutils.DbUtils;
import com.iCo6.util.org.apache.commons.dbutils.QueryRunner;
import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import com.iCo6.util.wget;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Timer;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.h2.jdbcx.JdbcConnectionPool;

/**
 * iConomy by Team iCo
 *
 * @copyright Copyright AniGaiku LLC (C) 2010-2012
 * @author Nijikokun <nijikokun@gmail.com>
 * @author SpaceManiac
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class iConomy extends JavaPlugin {
    public PluginDescriptionFile info;
    public PluginManager manager;

    private static Accounts Accounts = new Accounts();
    public Parser Commands = new Parser();
    public Permissions Permissions;
    private boolean testedPermissions = false;

    public static boolean TerminalSupport = false;
    public static File directory;
    public static Database Database;
    public static Server Server;
    public static Template Template;
    public static Timer Interest;

    private JdbcConnectionPool h2pool;

    public void onEnable() {
        final long startTime = System.nanoTime();
        final long endTime;

        try {
            // Localize locale to prevent issues.
            Locale.setDefault(Locale.US);

            // Server & Terminal Support
            Server = getServer();

            if(getServer().getServerName().equalsIgnoreCase("craftbukkit")) {
                TerminalSupport = ((CraftServer)getServer()).getReader().getTerminal().isANSISupported();
            }

            // Get general plugin information
            info = getDescription();

            // Plugin directory setup
            directory = getDataFolder();
            if(!directory.exists()) directory.mkdir();

            // Extract Files
            Common.extract("Config.yml", "Template.yml");

            // Setup Configuration
            Constants.load(new File(directory, "Config.yml"));

            // Setup Template
            Template = new Template(directory.getPath(), "Template.yml");

            // Upgrade Template to 6.0.9b
            LinkedHashMap<String, String> nodes = new LinkedHashMap<String, String>();
            nodes.put("top.opening", "<green>-----[ <white>Wealthiest Accounts <green>]-----");
            nodes.put("top.item", "<gray>+i. <green>+name <gray>- <white>+amount");

            try {
                Template.update(nodes);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            
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
            Commands.setHelp("money", new String[] { "", "Check your balance." });
            Commands.setHelp("money+", new String[] { " [name]", "Check others balance." });

            Commands.add("/money -h|?|help +command", new Help(this));
            Commands.setPermission("help", "iConomy.help");
            Commands.setHelp("help", new String[] { " (command)", "For Help & Information." });

            Commands.add("/money -t|top", new Top(this));
            Commands.setPermission("top", "iConomy.top");
            Commands.setHelp("top", new String[] { "", "View top economical accounts." });

            Commands.add("/money -p|pay +name +amount:empty", new Payment(this));
            Commands.setPermission("pay", "iConomy.payment");
            Commands.setHelp("pay", new String[] { " [name] [amount]", "Send money to others." });

            Commands.add("/money -c|create +name", new Create(this));
            Commands.setPermission("create", "iConomy.accounts.create");
            Commands.setHelp("create", new String[] { " [name]", "Create an account." });

            Commands.add("/money -r|remove +name", new Remove(this));
            Commands.setPermission("remove", "iConomy.accounts.remove");
            Commands.setHelp("remove", new String[] { " [name]", "Remove an account." });

            Commands.add("/money -g|give +name +amount:empty", new Give(this));
            Commands.setPermission("give", "iConomy.accounts.give");
            Commands.setHelp("give", new String[] { " [name] [amount]", "Give money." });

            Commands.add("/money -t|take +name +amount:empty", new Take(this));
            Commands.setPermission("take", "iConomy.accounts.take");
            Commands.setHelp("take", new String[] { " [name] [amount]", "Take money." });

            Commands.add("/money -s|set +name +amount:empty", new Set(this));
            Commands.setPermission("set", "iConomy.accounts.set");
            Commands.setHelp("set", new String[] { " [name] [amount]", "Set account balance." });

            Commands.add("/money -u|status +name +status:empty", new Status(this));
            Commands.setPermission("status", "iConomy.accounts.status");
            Commands.setPermission("status+", "iConomy.accounts.status.set");
            Commands.setHelp("status", new String[] { " [name] (status)", "Check/Set account status." });

            Commands.add("/money -x|purge", new Purge(this));
            Commands.setPermission("purge", "iConomy.accounts.purge");
            Commands.setHelp("purge", new String[] { "", "Purge all accounts with initial holdings." });

            Commands.add("/money -e|empty", new Empty(this));
            Commands.setPermission("empty", "iConomy.accounts.empty");
            Commands.setHelp("empty", new String[] { "", "Empty database of accounts." });

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
                if(Database.isSQL()) {
                    if(!Database.tableExists(Constants.Nodes.DatabaseTable.toString())) {
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
                } else {
                    this.onConversion();
                }
            } catch (MissingDriver ex) {
                System.out.println(ex.getMessage());
            }

            getServer().getPluginManager().registerEvents(new players(), this);
        } finally {
          endTime = System.nanoTime();
        }

        // Setup Interest
        if(Constants.Nodes.Interest.getBoolean()) {
            Thrun.init(new Runnable() {
                public void run() {
                    long time = Constants.Nodes.InterestTime.getLong() * 1000L;

                    Interest = new Timer();
                    Interest.scheduleAtFixedRate(new Interest(getDataFolder().getPath()), time, time);
                }
            });
        }

        if(Constants.Nodes.Purging.getBoolean()) {
            Thrun.init(new Runnable() {
                public void run() {
                    Queried.purgeDatabase();
                    System.out.println("[" + info.getName() + " - " + Constants.Nodes.CodeName.toString() + "] Purged accounts with default balance.");
                }
            });
        }

        final long duration = endTime - startTime;

        // Finish
        System.out.println("[" + info.getName() + " - " + Constants.Nodes.CodeName.toString() + "] Enabled (" + Common.readableProfile(duration) + ")");
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

            if(Interest != null) {
                Interest.cancel();
                Interest.purge();
                Interest = null;
            }
            
            TerminalSupport = false;
        } finally {
          endTime = System.nanoTime();
        }

        // Finish duration
        final long duration = endTime - startTime;

        // Output finished & time.
        System.out.println("[" + name + "] Disabled. (" + Common.readableProfile(duration) + ")");
    }

    public boolean onConversion() {
        if(!Constants.Nodes.Convert.getBoolean())
            return false;

        Thrun.init(new Runnable() {
            public void run() {
                String from = Constants.Nodes.ConvertFrom.toString();
                String table = Constants.Nodes.ConvertTable.toString();
                String username = Constants.Nodes.ConvertUsername.toString();
                String password = Constants.Nodes.ConvertPassword.toString();
                String url = Constants.Nodes.ConvertURL.toString();

                if(!Common.matches(from, "h2", "h2db", "h2sql", "mysql", "mysqldb"))
                    return;

                String driver = "", dsn = "";

                if(Common.matches(from, "sqlite", "h2", "h2sql", "h2db")) {
                    driver = "org.h2.Driver";
                    dsn = "jdbc:h2:" + directory + File.separator + table + ";AUTO_RECONNECT=TRUE";
                    username = "sa";
                    password = "sa";
                } else if (Common.matches(from, "mysql", "mysqldb")) {
                    driver = "com.mysql.jdbc.Driver";
                    dsn = url + "/" + table;
                }

                if(!DbUtils.loadDriver(driver)) {
                    System.out.println("Please make sure the " + from + " driver library jar exists.");

                    return;
                }

                Connection old = null;

                try {
                    old = (username.isEmpty() && password.isEmpty()) ? 
                    DriverManager.getConnection(url) :
                    DriverManager.getConnection(url, username, password);
                } catch (SQLException ex) {
                    System.out.println(ex);
                    return;
                }

                QueryRunner run = new QueryRunner();

                try {
                    try{
                        run.query(old, "SELECT * FROM " + table, new ResultSetHandler(){
                            public Object handle(ResultSet rs) throws SQLException {
                                Account current = null;
                                Boolean next = rs.next();

                                if(next)
                                    if(iConomy.Accounts.exists(rs.getString("username")))
                                        current = iConomy.Accounts.get(rs.getString("username"));
                                    else
                                        iConomy.Accounts.create(rs.getString("username"), rs.getDouble("balance"));

                                if(current != null)
                                    current.getHoldings().setBalance(rs.getDouble("balance"));

                                if(next)
                                    if(iConomy.Accounts.exists(rs.getString("username")))
                                        if(rs.getBoolean("hidden"))
                                            iConomy.Accounts.get(rs.getString("username")).setStatus(1);

                                return true;
                            }
                        });
                    } catch (SQLException ex) {
                        System.out.println("[iConomy] Error issueing SQL query: " + ex);
                    } finally {
                        DbUtils.close(old);
                    }
                } catch (SQLException ex) {
                    System.out.println("[iConomy] Database Error: " + ex);
                }

                System.out.println("[iConomy] Conversion complete. Please update your configuration, change convert to false!");
            }
        });

        return false;
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

            if(player == null) {
                System.out.println("[iConomy] Cannot execute command with false player");
                return false;
            }

            if(Commands.hasPermission(command)) {
                String node = Commands.getPermission(command);

                if(node == null)
                    return true;

                if(this.Permissions == null)
                    if(!this.testedPermissions) {
                        Plugin Perms = Server.getPluginManager().getPlugin("Permissions");
                        
                        if (Perms != null) {
                            if (Perms.isEnabled()) {
                                this.Permissions = ((Permissions)Perms);
                                System.out.println("[iConomy] hooked into Permissions.");
                            }
                        }

                        this.testedPermissions = true;
                    }
                
                if(this.Permissions != null) {
                    if(Permissions.Security.permission(player, node) || Permissions.Security.permission(player, node.toLowerCase()))
                        return true;

                    return false;
                } else {
                    try {
                        Permission perm = new Permission(node);
                        if(player.hasPermission(perm) || player.hasPermission(node) || player.hasPermission(node.toLowerCase()))
                            return true;

                        return false;
                    } catch(Exception e) {
                        return player.isOp();
                    }
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
