package com.nijiko.coelho.iConomy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.sql.PreparedStatement;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Timer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.nijiko.coelho.iConomy.entity.Players;
import com.nijiko.coelho.iConomy.net.Database;
import com.nijiko.coelho.iConomy.system.Bank;
import com.nijiko.coelho.iConomy.system.Interest;
import com.nijiko.coelho.iConomy.util.Constants;
import com.nijiko.coelho.iConomy.system.Transactions;
import com.nijiko.coelho.iConomy.util.Downloader;
import com.nijiko.coelho.iConomy.util.FileManager;
import com.nijiko.coelho.iConomy.util.Misc;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.event.server.PluginEnableEvent;

/**
 * iConomy by Team iCo
 *
 * @copyright     Copyright AniGaiku LLC (C) 2010-2011
 * @author          Nijikokun <nijikokun@gmail.com>
 * @author          Coelho <robertcoelho@live.com>
 * @author       ShadowDrakken <shadowdrakken@gmail.com>
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
    private static Server Server = null;
    private static Bank Bank = null;
    private static Database Database = null;
    private static Transactions Transactions = null;
    private static PermissionHandler Permissions = null;
    private static Players playerListener = null;
    private static Timer Interest_Timer = null;

    @Override
    public void onEnable() {
        Locale.setDefault(Locale.US);

        // Get the server
        Server = getServer();

        // Lib Directory
        (new File("lib" + File.separator)).mkdir();
        (new File("lib" + File.separator)).setWritable(true);
        (new File("lib" + File.separator)).setExecutable(true);

        // Plugin Directory
        getDataFolder().mkdir();
        getDataFolder().setWritable(true);
        getDataFolder().setExecutable(true);

        // Setup the path.
        Constants.Plugin_Directory = getDataFolder().getPath();

        // Grab plugin details
        PluginDescriptionFile pdfFile = this.getDescription();

        // Versioning File
        FileManager file = new FileManager(getDataFolder().getPath(), "VERSION", false);

        // Default Files
        extractDefaultFile("iConomy.yml");
        extractDefaultFile("Messages.yml");

        // Configuration
        try {
            Constants.load(new Configuration(new File(getDataFolder(), "iConomy.yml")));
        } catch (Exception e) {
            Server.getPluginManager().disablePlugin(this);
            System.out.println("[iConomy] Failed to retrieve configuration from directory.");
            System.out.println("[iConomy] Please back up your current settings and let iConomy recreate it.");
            return;
        }

        if(Misc.is(Constants.Database_Type, new String[] { "sqlite", "h2", "h2sql", "h2db" })) {
            if(!(new File("lib" + File.separator, "h2.jar").exists())) {
                Downloader.install(Constants.H2_Jar_Location, "h2.jar");
            }
        } else {
            if(!(new File("lib" + File.separator, "mysql-connector-java-bin.jar").exists())) {
                Downloader.install(Constants.MySQL_Jar_Location, "mysql-connector-java-bin.jar");
            }
        }

        // Load the database
        try {
            Database = new Database();
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to connect to database: " + e);
            Server.getPluginManager().disablePlugin(this);
            return;

        }

        // File Logger
        try {
            Transactions = new Transactions();
            Transactions.load();
        } catch (Exception e) {
            System.out.println("[iConomy] Could not load transaction logger: ");
            e.printStackTrace();
        }

        // Check version details before the system loads
        update(file, Double.valueOf(pdfFile.getVersion()));

        // Load the bank system
        try {
            Bank = new Bank();
            Bank.load();
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to load database: " + e);
            Server.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            if (Constants.Interest) {
                Interest_Timer = new Timer();
                Interest_Timer.scheduleAtFixedRate(new Interest(getDataFolder().getPath()),
                        Constants.Interest_Interval * 1000L, Constants.Interest_Interval * 1000L);
            }
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to start interest system: " + e);
            Server.getPluginManager().disablePlugin(this);
            return;
        }

        // Initializing Listeners
        playerListener = new Players(getDataFolder().getPath());

        // Event Registration
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, new Listener(this), Priority.Monitor, this);
        
        // Console Detail
        System.out.println("[iConomy] v" + pdfFile.getVersion() + " (" + Constants.Codename + ") loaded.");
        System.out.println("[iConomy] Developed by: " + pdfFile.getAuthors());
    }

    @Override
    public void onDisable() {
        try {
            if(Misc.is(Constants.Database_Type, new String[] { "sqlite", "h2", "h2sql", "h2db" })) {
                Database.connectionPool().dispose();
            }
            
            System.out.println("[iConomy] Plugin disabled.");
        } catch (Exception e) {
            System.out.println("[iConomy] Plugin disabled.");
        } finally {
            if (Interest_Timer != null) {
                Interest_Timer.cancel();
            }

            Server = null;
            Bank = null;
            Database = null;
            Permissions = null;
            Transactions = null;
            playerListener = null;
            Interest_Timer = null;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            String[] split = new String[args.length + 1];
            split[0] = cmd.getName().toLowerCase();

            for (int i = 0; i < args.length; i++) {
                split[i + 1] = args[i];
            }

            playerListener.onPlayerCommand(sender, split);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void update(FileManager file, double version) {
        if (file.exists()) {
            file.read();

            try {
                double current = Double.parseDouble(file.getSource());
                LinkedList<String> MySQL = new LinkedList<String>();
                LinkedList<String> GENERIC = new LinkedList<String>();
                LinkedList<String> SQL = new LinkedList<String>();

                if(current != version) {
                    if(current < 4.62) {
                        MySQL.add("ALTER TABLE " + Constants.SQL_Table + " ADD UNIQUE(username(32));");
                        GENERIC.add("ALTER TABLE " + Constants.SQL_Table + " ADD UNIQUE(username);");
                    }

                    if(current < 4.61) {
                        MySQL.add("ALTER TABLE " + Constants.SQL_Table + " ADD hidden boolean DEFAULT '0';");
                        GENERIC.add("ALTER TABLE " + Constants.SQL_Table + " ADD HIDDEN BOOLEAN DEFAULT '0';");
                    }

                    if(!MySQL.isEmpty() && !GENERIC.isEmpty()) {
                        Connection conn = null;
                        ResultSet rs = null;
                        Statement stmt = null;

                        try {
                            conn = iConomy.getLocalDatabase().getConnection();
                            stmt = null;

                            System.out.println(" - Updating " + Constants.Database_Type + " Database for latest iConomy");

                            int i = 1;
                            SQL = (Constants.Database_Type.equalsIgnoreCase("mysql")) ? MySQL : GENERIC;

                            for (String Query : SQL) {
                                stmt = conn.createStatement();
                                stmt.execute(Query);

                                System.out.println("   Executing SQL Query #" + i + " of " + (SQL.size()));
                                ++i;
                            }

                            file.write(version);

                            System.out.println(" + Database Update Complete.");
                        } catch (SQLException e) {
                            System.out.println("[iConomy] Error updating database: " + e);
                        } finally {
                            if(stmt != null)
                                try { stmt.close(); } catch (SQLException ex) { }

                            if(rs != null)
                                try { rs.close(); } catch (SQLException ex) { }

                            iConomy.getLocalDatabase().close(conn);
                        }
                    }
                } else {
                    file.write(version);
                }
            } catch (Exception e) {
                System.out.println("[iConomy] Error on version check: ");
                e.printStackTrace();
                file.delete();
            }
        } else {
            if (!Constants.Database_Type.equalsIgnoreCase("flatfile")) {
                String[] SQL = {};

                String[] MySQL = {
                    "DROP TABLE " + Constants.SQL_Table + ";",
                    "RENAME TABLE ibalances TO " + Constants.SQL_Table + ";",
                    "ALTER TABLE " + Constants.SQL_Table + " CHANGE  player  username TEXT NOT NULL, CHANGE balance balance DECIMAL(65, 2) NOT NULL;"
                };

                String[] SQLite = {
                    "DROP TABLE " + Constants.SQL_Table + ";",
                    "CREATE TABLE '" + Constants.SQL_Table + "' ('id' INT ( 10 ) PRIMARY KEY , 'username' TEXT , 'balance' DECIMAL ( 65 , 2 ));",
                    "INSERT INTO " + Constants.SQL_Table + "(id, username, balance) SELECT id, player, balance FROM ibalances;",
                    "DROP TABLE ibalances;"
                };

                Connection conn = null;
                ResultSet rs = null;
                PreparedStatement ps = null;

                try {
                    conn = iConomy.getLocalDatabase().getConnection();
                    DatabaseMetaData dbm = conn.getMetaData();
                    rs = dbm.getTables(null, null, "ibalances", null);
                    ps = null;

                    if (rs.next()) {
                        System.out.println(" - Updating " + Constants.Database_Type + " Database for latest iConomy");

                        int i = 1;
                        SQL = (Constants.Database_Type.equalsIgnoreCase("mysql")) ? MySQL : SQLite;

                        for (String Query : SQL) {
                            ps = conn.prepareStatement(Query);
                            ps.executeQuery(Query);

                            System.out.println("   Executing SQL Query #" + i + " of " + (SQL.length));
                            ++i;
                        }

                        System.out.println(" + Database Update Complete.");
                    }

                    file.write(version);
                } catch (SQLException e) {
                    System.out.println("[iConomy] Error updating database: " + e);
                } finally {
                    if(ps != null)
                        try { ps.close(); } catch (SQLException ex) { }

                    if(rs != null)
                        try { rs.close(); } catch (SQLException ex) { }

                    if(conn != null)
                        iConomy.getLocalDatabase().close(conn);
                }
            }

            file.create();
            file.write(version);
        }
    }

    private void extractDefaultFile(String name) {
        File actual = new File(getDataFolder(), name);
        if (!actual.exists()) {
            InputStream input = this.getClass().getResourceAsStream("/default/" + name);
            if (input != null) {
                FileOutputStream output = null;

                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;

                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }

                    System.out.println("[iConomy] Default setup file written: " + name);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    } catch (Exception e) { }
                    try {
                        if (output != null) {
                            output.close();
                        }
                    } catch (Exception e) { }
                }
            }
        }
    }

    /**
     * Grab the bank to modify and access bank accounts.
     *
     * @return Bank
     */
    public static Bank getBank() {
        return Bank;
    }

    /**
     * Grabs Database controller.
     *
     * @return iDatabase
     */
    public static Database getLocalDatabase() {
        return Database;
    }

    /**
     * Grabs Transaction Log Controller.
     *
     * Used to log transactions between a player and anything. Such as the
     * system or another player or just enviroment.
     *
     * @return T
     */
    public static Transactions getTransactions() {
        return Transactions;
    }

    public static PermissionHandler getPermissions() {
        return Permissions;
    }

    public static boolean hasPermissions(CommandSender sender, String node) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            if(Permissions != null)
                return Permissions.permission(player, node);
            else {
                return player.isOp();
            }
        }

        return true;
    }

    public static void setPermissions(PermissionHandler ph) {
        Permissions = ph;
    }

    public static Server getBukkitServer() {
        return Server;
    }

    private class Listener extends ServerListener {

        private iConomy plugin;

        public Listener(iConomy thisPlugin) {
            this.plugin = thisPlugin;
        }

        @Override
        public void onPluginEnable(PluginEnableEvent event) {
            if (plugin.Permissions == null) {
                Plugin Permissions = plugin.getServer().getPluginManager().getPlugin("Permissions");

                if (Permissions != null) {
                    if (Permissions.isEnabled()) {
                        plugin.Permissions = (((Permissions)Permissions).getHandler());
                        System.out.println("[iConomy] hooked into Permissions.");
                    }
                }
            }
        }
    }
}
