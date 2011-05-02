package com.iConomy.util;

import org.bukkit.entity.Player;

import com.iConomy.iConomy;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.yaml.snakeyaml.Yaml;

public class Misc {

    /**
     * Checks text against two variables, if it equals at least one returns true.
     *
     * @param text The text that we were provided with.
     * @param against The first variable that needs to be checked against
     * @param or The second variable that it could possibly be.
     *
     * @return <code>Boolean</code> - True or false based on text.
     */
    public static boolean is(String text, String[] is) {
        for (String s : is) {
            if (text.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSelf(CommandSender sender, String name) {
        return (sender instanceof Player) ? (((Player)sender).getName().equalsIgnoreCase(name)) ? true : false : false;
    }

    public static int plural(Double amount) {
        if(amount != 1 || amount != -1) {
            return 1;
        } else {
            return 0;
        }
    }
    
    public static int plural(Integer amount) {
        if(amount != 1 || amount != -1) {
            return 1;
        } else {
            return 0;
        }
    }

    public static String BankCurrency(int which, String denom) {
        String[] denoms = denom.split(",");

        return denoms[which];
    }

    public static String formatted(String amount, List<String> maj, List<String> min) {
        String formatted = "";
        String famount = amount.replace(",", "");

        if(Constants.FormatMinor) {
            String[] pieces = null;
            String[] fpieces = null;

            if(amount.contains(".")) {
                pieces = amount.split("\\.");
                fpieces = new String[] { pieces[0].replace(",", ""), pieces[1] };
            } else {
                pieces = new String[] { amount, "0" };
                fpieces = new String[] { amount.replace(",", ""), "0" };
            }

            if(Constants.FormatSeperated) {
                String major = maj.get(plural(Integer.valueOf(fpieces[0])));
                String minor = min.get(plural(Integer.valueOf(fpieces[1])));

                if(pieces[1].startsWith("0") && !pieces[1].equals("0")) pieces[1] = pieces[1].substring(1, pieces[1].length());
                if(pieces[0].startsWith("0") && !pieces[0].equals("0")) pieces[0] = pieces[0].substring(1, pieces[0].length());

                if(Integer.valueOf(fpieces[1]) != 0 && Integer.valueOf(fpieces[0]) != 0) {
                    formatted = pieces[0] + " " + major + ", " + pieces[1] + " " + minor;
                } else if(Integer.valueOf(fpieces[0]) != 0) {
                    formatted = pieces[0] + " " + major;
                } else {
                    formatted = pieces[1] + " " + minor;
                }
            } else {
                String currency = "";

                if(Double.valueOf(famount) < 1 || Double.valueOf(famount) > -1) {
                    currency = min.get(plural(Integer.valueOf(fpieces[1])));
                } else {
                    currency = maj.get(1);
                }

                formatted = amount + " " + currency;
            }
        } else {
                int plural = plural(Double.valueOf(famount));
                String currency = maj.get(plural);

                formatted = amount + " " + currency;
        }

        return formatted;
    }

    /**
     * Get the player from the server (matched)
     */
    public static Player playerMatch(String name) {
        Player[] online = iConomy.getBukkitServer().getOnlinePlayers();
        Player lastPlayer = null;

        for (Player player : online) {
            String playerName = player.getName();

            if (playerName.equalsIgnoreCase(name)) {
                lastPlayer = player;
                break;
            }

            if (playerName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
                if (lastPlayer != null) {
                    return null;
                }

                lastPlayer = player;
            }
        }

        return lastPlayer;
    }
}
