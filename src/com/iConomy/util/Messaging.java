package com.iConomy.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.iConomy.iConomy;
import org.bukkit.ChatColor;

/**
 * Copyright (C) 2011  Nijikokun <nijikokun@gmail.com>
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
public class Messaging {

    private static CommandSender sender = null;

    /**
     * Converts a list of arguments into points.
     *
     * @param original The original string necessary to convert inside of.
     * @param arguments The list of arguments, multiple arguments are seperated by commas for a single point.
     * @param points The point used to alter the argument.
     *
     * @return <code>String</code> - The parsed string after converting arguments to variables (points)
     */
    public static String argument(String original, Object[] arguments, Object[] points) {
        for (int i = 0; i < arguments.length; i++) {
            if (String.valueOf(arguments[i]).contains(",")) {
                for (String arg : String.valueOf(arguments[i]).split(",")) {
                    original = original.replace(arg, String.valueOf(points[i]));
                }
            } else {
                original = original.replace(String.valueOf(arguments[i]), String.valueOf(points[i]));
            }
        }

        return original;
    }

    /**
     * Parses the original string against color specific codes. This one converts &[code] to §[code]
     * <br /><br />
     * Example:
     * <blockquote><pre>
     * Messaging.parse("Hello &2world!"); // returns: Hello §2world!
     * </pre></blockquote>
     *
     * @param original The original string used for conversions.
     *
     * @return <code>String</code> - The parsed string after conversion.
     */
    public static String parse(String original) {
        original = colorize(original);
        return original.replaceAll("(&([a-z0-9]))", "\u00A7$2").replace("&&", "&");

    }

    /**
     * Converts color codes into the simoleon code. Sort of a HTML format color code tag and `[code]
     * <p>
     * Color codes allowed: black, navy, green, teal, red, purple, gold, silver, gray, blue, lime, aqua, rose, pink, yellow, white.</p>
     * Example:
     * <blockquote<pre>
     * Messaging.colorize("Hello &lt;green>world!"); // returns: Hello $world!
     * </pre></blockquote>
     *
     * @param original Original string to be parsed against group of color names.
     *
     * @return <code>String</code> - The parsed string after conversion.
     */
    public static String colorize(String original) {
        original = original.replace("`r", ChatColor.RED.toString());
        original = original.replace("`R", ChatColor.DARK_RED.toString());
        original = original.replace("`y", ChatColor.YELLOW.toString());
        original = original.replace("`Y", ChatColor.GOLD.toString());
        original = original.replace("`g", ChatColor.GREEN.toString());
        original = original.replace("`G", ChatColor.DARK_GREEN.toString());
        original = original.replace("`a", ChatColor.AQUA.toString());
        original = original.replace("`A", ChatColor.DARK_AQUA.toString());
        original = original.replace("`b", ChatColor.BLUE.toString());
        original = original.replace("`B", ChatColor.DARK_BLUE.toString());
        original = original.replace("`p", ChatColor.LIGHT_PURPLE.toString());
        original = original.replace("`P", ChatColor.DARK_PURPLE.toString());
        original = original.replace("`k", ChatColor.BLACK.toString());
        original = original.replace("`s", ChatColor.GRAY.toString());
        original = original.replace("`S", ChatColor.DARK_GRAY.toString());
        original = original.replace("`w", ChatColor.WHITE.toString());
        return original.replace("<black>", "\u00A70").replace("<navy>", "\u00A71").replace("<green>", "\u00A72").replace("<teal>", "\u00A73").replace("<red>", "\u00A74").replace("<purple>", "\u00A75").replace("<gold>", "\u00A76").replace("<silver>", "\u00A77").replace("<gray>", "\u00A78").replace("<blue>", "\u00A79").replace("<lime>", "\u00A7a").replace("<aqua>", "\u00A7b").replace("<rose>", "\u00A7c").replace("<pink>", "\u00A7d").replace("<yellow>", "\u00A7e").replace("<white>", "\u00A7f");
    }

    /**
     * Helper function to assist with making brackets. Why? Dunno, lazy.
     *
     * @param message The message inside of brackets.
     *
     * @return <code>String</code> - The message inside [brackets]
     */
    public static String bracketize(String message) {
        return "[" + message + "]";
    }

    /**
     * Save the player to be sent messages later. Ease of use sending messages.
     * <br /><br />
     * Example:
     * <blockquote><pre>
     * Messaging.save(player);
     * Messaging.send("This will go to the player saved.");
     * </pre></blockquote>
     *
     * @param player The player we wish to save for later.
     */
    public static void save(Player player) {
        Messaging.sender = player;
    }

    /**
     * Save the entity to be sent messages later. Ease of use sending messages.
     * <br /><br />
     * Example:
     * <blockquote><pre>
     * Messaging.save(sender);
     * Messaging.voice("This will go to the entity saved.");
     * </pre></blockquote>
     *
     * @param player The player we wish to save for later.
     */
    public static void save(CommandSender sender) {
        Messaging.sender = sender;
    }

    /**
     * Sends a message to a specific player.
     * <br /><br />
     * Example:
     * <blockquote><pre>
     * Messaging.send(player, "This will go to the player saved.");
     * </pre></blockquote>
     *
     * @param player Player we are sending the message to.
     * @param message The message to be sent.
     */
    public static void send(Player player, String message) {
        player.sendMessage(parse(message));
    }

    /**
     * Sends a message to an entity
     * <br /><br />
     * Example:
     * <blockquote><pre>
     * Messaging.send(sender, "This will go to the entity specified.");
     * </pre></blockquote>
     *
     * @param sender Entity we are sending the message to.
     * @param message The message to be sent.
     */
    public static void send(CommandSender sender, String message) {
        sender.sendMessage(parse(message));
    }

    /**
     * Sends a message to the stored entity.
     *
     * @param message The message to be sent.
     * @see Messaging#save(CommandSender)
     */
    public static void send(String message) {
        if (Messaging.sender != null) {
            sender.sendMessage(parse(message));
        }
    }

    /**
     * Broadcast a message to every player online.
     *
     * @param message - The message to be sent.
     */
    public static void broadcast(String message) {
        for (Player p : iConomy.getBukkitServer().getOnlinePlayers()) {
            p.sendMessage(parse(message));
        }
    }
}
