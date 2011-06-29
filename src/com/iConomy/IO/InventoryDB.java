package com.iConomy.IO;

import com.iConomy.Constants;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

public class InventoryDB {
    
    private Server server;
    private File dataDir;
    
    public InventoryDB() {
        server = Bukkit.getServer();
        dataDir = new File(server.getWorlds().get(0).getName(), "players");
    }
    
    public List<String> getAllPlayers() {
        ArrayList<String> result = new ArrayList<String>();
        for (String file : dataDir.list()) {
            if (file.endsWith(".dat")) {
                result.add(file.substring(0, file.length() - 4));
            }
        }
        return result;
    }
    
    public boolean dataExists(String name) {
        return new File(dataDir, name + ".dat").exists();
    }
    
    public void setBalance(String name, double balance) {
        if (server.getPlayer(name) != null) {
            setBalance(server.getPlayer(name).getInventory().getContents(), balance);
        } else {
            // TODO: mess with NBT
        }
    }
    
    public double getBalance(String name) {
        if (server.getPlayer(name) != null) {
            return getBalance(server.getPlayer(name).getInventory().getContents());
        } else {
            // TODO: mess with NBT
            return 0;
        }
    }

    private void setBalance(ItemStack[] contents, double balance) {
        int major = Constants.Nodes.DatabaseMajorItem.getInteger();
        int minor = Constants.Nodes.DatabaseMinorItem.getInteger();
        
        // Remove all existing items
        for (int i = 0; i < contents.length; ++i) {
            ItemStack item = contents[i];
            if (item != null) {
                if (item.getTypeId() == major || item.getTypeId() == minor) {
                    contents[i] = null;
                }
            }
        }
        
        // Re-add balance to inventory
        for (int i = 0; i < contents.length; ++i) {
            if (contents[i] == null) {
                if (balance >= 1) {
                    int add = (int) balance;
                    if (add > Material.getMaterial(major).getMaxStackSize()) {
                        add = Material.getMaterial(major).getMaxStackSize();
                    }
                    contents[i] = new ItemStack(major, add);
                    balance -= add;
                } else if (balance > 0) {
                    int add = (int) ((balance - (int) balance) * 100);
                    if (add > Material.getMaterial(minor).getMaxStackSize()) {
                        add = Material.getMaterial(minor).getMaxStackSize();
                    }
                    contents[i] = new ItemStack(minor, add);
                    balance = 0;
                    break;
                }
            }
        }
    }
    
    private double getBalance(ItemStack[] contents) {
        double balance = 0;
        int major = Constants.Nodes.DatabaseMajorItem.getInteger();
        int minor = Constants.Nodes.DatabaseMinorItem.getInteger();
        
        for (ItemStack item : contents) {
            if (item != null) {
                if (item.getTypeId() == major) {
                    balance += item.getAmount();
                } else if (item.getTypeId() == minor) {
                    balance += 0.01 * item.getAmount();
                }
            }
        }
        
        return balance;
    }
    
}
