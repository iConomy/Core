package com.nijiko.coelho.iConomy.entity;

import java.util.ArrayList;
import java.util.Collection;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;
import com.nijiko.coelho.iConomy.util.Constants;
import com.nijiko.coelho.iConomy.util.Messaging;
import com.nijiko.coelho.iConomy.util.Misc;
import com.nijiko.coelho.iConomy.util.Template;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handles the command usage and account creation upon a
 * player joining the server.
 *
 * @author Nijikokun
 */
public class Players extends PlayerListener {

    private Template Template = null;

    /**
     * Initialize the class as well as the template for various
     * messages throughout the commands.
     *
     * @param directory
     */
    public Players(String directory) {
        Template = new Template(directory, "Messages.yml");
    }

    /**
     * Help documentation for iConomy all in one method.
     *
     * Allows us to easily utilize all throughout the class without having multiple
     * instances of the same help lines.
     */
    private void showHelp(CommandSender player) {
        Messaging.send("&e----------------------------------------------------");
        Messaging.send("&f iConomy (&c" + Constants.Codename + "&f)           ");
        Messaging.send("&e----------------------------------------------------");
        Messaging.send("&f [] Required, () Optional                            ");
        Messaging.send("&e----------------------------------------------------");
        Messaging.send("&f/money &6-&e Check your balance                     ");
        Messaging.send("&f/money ? &6-&e For help & Information               ");

        if (iConomy.hasPermissions(player, "iConomy.rank")) {
            Messaging.send("&f/money rank (player) &6-&e Rank on the topcharts.   ");
        }
        
        if (iConomy.hasPermissions(player, "iConomy.list")) {
            Messaging.send("&f/money top (amount) &6-&e Richest players listing.  ");
        }

        if (iConomy.hasPermissions(player, "iConomy.payment")) {
            Messaging.send("&f/money pay [player] [amount] &6-&e Send money to a player.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.grant")) {
            Messaging.send("&f/money grant [player] [amount] &6-&e Give money.");
            Messaging.send("&f/money grant [player] -[amount] &6-&e Take money.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.set")) {
            Messaging.send("&f/money set [player] [amount] &6-&e Sets a players balance.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.reset")) {
            Messaging.send("&f/money reset [player] &6-&e Reset player account.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.stats")) {
            Messaging.send("&f/money stats  &6-&e Check all economic stats.");
        }

        Messaging.send("&e----------------------------------------------------");
    }

    /**
     * Shows the balance to the requesting player.
     *
     * @param name The name of the player we are viewing
     * @param viewing The player who is viewing the account
     * @param mine Is it the player who is trying to view?
     */
    public void showBalance(String name, CommandSender viewing, boolean mine) {
        if (mine) {
            Messaging.send(viewing, Template.color("tag") + Template.parse("personal.balance", new String[]{"+balance,+b"}, new String[]{iConomy.getBank().format(((Player)viewing).getName())}));
        } else {
            Messaging.send(viewing, Template.color("tag") + Template.parse("player.balance", new String[]{"+balance,+b", "+name,+n"}, new String[]{iConomy.getBank().format(name), name}));
        }
    }

    /**
     * Reset a players account easily.
     *
     * @param resetting The player being reset. Cannot be null.
     * @param by The player resetting the account. Cannot be null.
     * @param notify Do we want to show the updates to each player?
     */
    public void showPayment(String from, String to, double amount) {
        Player paymentFrom = iConomy.getBukkitServer().getPlayer(from);
        Player paymentTo = iConomy.getBukkitServer().getPlayer(to);
        Account balanceFrom = iConomy.getBank().getAccount(from);
        Account balanceTo = iConomy.getBank().getAccount(to);

        if (from.equals(to)) {
            if (paymentFrom != null) {
                Messaging.send(paymentFrom, Template.color("payment.self"));
            }
        } else if (amount < 0.0 || !balanceFrom.hasEnough(amount)) {
            if (paymentFrom != null) {
                Messaging.send(paymentFrom, Template.color("error.funds"));
            }
        } else {
            balanceFrom.subtract(amount);
            balanceFrom.save();
            balanceTo.add(amount);
            balanceTo.save();

            iConomy.getTransactions().insert(from, to, balanceFrom.getBalance(), balanceTo.getBalance(), 0.0, 0.0, amount);
            iConomy.getTransactions().insert(to, from, balanceTo.getBalance(), balanceFrom.getBalance(), 0.0, amount, 0.0);

            if (paymentFrom != null) {
                Messaging.send(
                        paymentFrom,
                        Template.color("tag") + Template.parse(
                        "payment.to",
                        new String[]{"+name,+n", "+amount,+a"},
                        new String[]{to, iConomy.getBank().format(amount)}));

                showBalance(from, paymentFrom, true);
            }

            if (paymentTo != null) {
                Messaging.send(
                        paymentTo,
                        Template.color("tag") + Template.parse(
                        "payment.from",
                        new String[]{"+name,+n", "+amount,+a"},
                        new String[]{from, iConomy.getBank().format(amount)}));

                showBalance(to, paymentTo, true);
            }
        }
    }

    /**
     * Reset a players account, accessable via Console & In-Game
     *
     * @param account The account we are resetting.
     * @param controller If set to null, won't display messages.
     * @param console Is it sent via console?
     */
    public void showReset(String account, Player controller, boolean console) {
        Player player = iConomy.getBukkitServer().getPlayer(account);

        // Log Transaction
        iConomy.getTransactions().insert(account, "[System]", 0.0, 0.0, 0.0, 0.0, iConomy.getBank().getAccount(account).getBalance());

        // Reset
        iConomy.getBank().resetAccount(account);

        if (player != null) {
            Messaging.send(player, Template.color("personal.reset"));
        }

        if (controller != null) {
            Messaging.send(
                Template.parse(
                    "player.reset",
                    new String[]{"+name,+n"},
                    new String[]{account}
                )
            );
        }

        if (console) {
            System.out.println("Player " + account + "'s account has been reset.");
        } else {
            System.out.println(Messaging.bracketize("iConomy") + "Player " + account + "'s account has been reset by " + controller.getName() + ".");
        }
    }

    /**
     *
     * @param account
     * @param controller If set to null, won't display messages.
     * @param amount
     * @param console Is it sent via console?
     */
    public void showGrant(String name, Player controller, double amount, boolean console) {
        Player online = iConomy.getBukkitServer().getPlayer(name);
        Account account = iConomy.getBank().getAccount(name);
        account.add(amount);
        account.save();

        // Log Transaction
        if (amount < 0.0) {
            iConomy.getTransactions().insert("[System]", name, 0.0, account.getBalance(), 0.0, 0.0, amount);
        } else {
            iConomy.getTransactions().insert("[System]", name, 0.0, account.getBalance(), 0.0, amount, 0.0);
        }

        if (online != null) {
            Messaging.send(online,
                Template.color("tag") + Template.parse(
                    (amount < 0.0) ? "personal.debit" : "personal.credit",
                    new String[]{"+by", "+amount,+a"},
                    new String[]{(console) ? "console" : controller.getName(), iConomy.getBank().format(((amount < 0.0) ? amount * -1 : amount))}
                )
            );

            showBalance(name, online, true);
        }

        if (controller != null) {
            Messaging.send(
                Template.color("tag") + Template.parse(
                    (amount < 0.0) ? "player.debit" : "player.credit",
                    new String[]{"+name,+n", "+amount,+a"},
                    new String[]{name, iConomy.getBank().format(amount)}
                )
            );
        }

        if (console) {
            System.out.println("Player " + account.getName() + "'s account had " + amount + " grant to it.");
        } else {
            System.out.println(Messaging.bracketize("iConomy") + "Player " + account.getName() + "'s account had " + amount + " grant to it by " + controller.getName() + ".");
        }
    }

    /**
     *
     * @param account
     * @param controller If set to null, won't display messages.
     * @param amount
     * @param console Is it sent via console?
     */
    public void showSet(String name, Player controller, double amount, boolean console) {
        Player online = iConomy.getBukkitServer().getPlayer(name);
        Account account = iConomy.getBank().getAccount(name);
        account.setBalance(amount);
        account.save();

        // Log Transaction
        iConomy.getTransactions().insert("[System]", name, 0.0, account.getBalance(), amount, 0.0, 0.0);

        if (online != null) {
            Messaging.send(online,
                Template.color("tag") + Template.parse(
                    "personal.set",
                    new String[]{"+by", "+amount,+a"},
                    new String[]{(console) ? "Console" : controller.getName(), iConomy.getBank().format(amount)}
                )
            );

            showBalance(name, online, true);
        }

        if (controller != null) {
            Messaging.send(
                Template.color("tag") + Template.parse(
                    "player.set",
                    new String[]{ "+name,+n", "+amount,+a" },
                    new String[]{ name, iConomy.getBank().format(amount) }
                )
            );
        }

        if (console) {
            System.out.println("Player " + account + "'s account had " + amount + " set to it.");
        } else {
            System.out.println(Messaging.bracketize("iConomy") + "Player " + account + "'s account had " + amount + " set to it by " + controller.getName() + ".");
        }
    }

    /**
     * Parses and outputs personal rank.
     *
     * Grabs rankings via the bank system and outputs the data,
     * using the template variables, to the given player stated
     * in the method.
     *
     * @param viewing
     * @param player
     */
    public void showRank(CommandSender viewing, String player) {
        if (iConomy.getBank().hasAccount(player)) {
            int rank = iConomy.getBank().getAccountRank(player);
            boolean isPlayer = (viewing instanceof Player);
            boolean isSelf = (isPlayer) ? ((((Player)viewing).getName().equalsIgnoreCase(player)) ? true : false) : false;

            Messaging.send(
                viewing,
                Template.color("tag") + Template.parse(
                    ((isSelf) ? "personal.rank" : "player.rank"),
                    new Object[]{"+name,+n", "+rank,+r"},
                    new Object[]{player, rank}
                )
            );
        } else {
            Messaging.send(
                viewing,
                Template.parse(
                    "error.account",
                    new Object[]{"+name,+n"},
                    new Object[]{player}
                )
            );
        }
    }

    /**
     * Top ranking users by cash flow.
     *
     * Grabs the top amount of players and outputs the data, using the template
     * system, to the given viewing player.
     *
     * @param viewing
     * @param amount
     */
    public void showTop(CommandSender viewing, int amount) {
        ArrayList<String> als = iConomy.getBank().getAccountRanks(amount);

        Messaging.send(
                viewing,
                Template.parse(
                "top.opening",
                new Object[]{"+amount,+a"},
                new Object[]{als.size()}
            )
        );

        for (int i = 0; i < als.size(); i++) {
            int current = i + 1;

            Account account = iConomy.getBank().getAccount(als.get(i));

            Messaging.send(
                viewing,
                Template.parse(
                    "top.line",
                    new String[]{"+i,+number", "+player,+name,+n", "+balance,+b"},
                    new Object[]{current, account.getName(), iConomy.getBank().format(account.getBalance())}
                )
            );
        }
    }

    /**
     * Commands sent from in game to us.
     *
     * @param player The player who sent the command.
     * @param split The input line split by spaces.
     * @return <code>boolean</code> - True denotes that the command existed, false the command doesn't.
     */
    @Override
    public void onPlayerJoin(PlayerEvent event) {
        Player player = event.getPlayer();

        if (!iConomy.getBank().hasAccount(player.getName())) {
            iConomy.getBank().addAccount(player.getName());
        }
    }

    /**
     * Commands sent from in-game are parsed and evaluated here.
     *
     * @param player
     * @param split
     */
    public void onPlayerCommand(CommandSender sender, String[] split) {
        Messaging.save(sender);
        boolean isPlayer = (sender instanceof Player);
        Player player = (sender instanceof Player) ? (Player)sender : null;

        if (split[0].equalsIgnoreCase("money")) {
            switch (split.length) {
                case 1:
                    if(isPlayer)
                        showBalance("", player, true);
                    else {
                        Messaging.send("&7Cannot show balance without organism.");
                    }

                    return;

                case 2:

                    if (Misc.is(split[1], new String[]{ "rank", "-r" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.rank") || !isPlayer) {
                            return;
                        }

                        showRank(player, player.getName());
                        return;
                    }

                    if (Misc.is(split[1], new String[]{ "top", "-t" })) {
                        if (!iConomy.hasPermissions(player, "iConomy.list")) {
                            return;
                        }

                        showTop(sender, 5);
                        return;
                    }

                    if (Misc.is(split[1], new String[]{ "stats", "-s" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.stats")) {
                            return;
                        }

                        Collection<Double> money = iConomy.getBank().getAccounts().values();
                        double totalMoney = 0;
                        int totalPlayers = money.size();

                        for (Object o : money.toArray()) {
                            totalMoney += (Double)o;
                        }

                        Messaging.send(Template.color("statistics.opening"));

                        Messaging.send(Template.parse("statistics.total",
                                new String[]{"+currency,+c", "+amount,+money,+a,+m"},
                                new Object[]{iConomy.getBank().getCurrency(), iConomy.getBank().format(totalMoney)}
                        ));

                        Messaging.send(Template.parse("statistics.average",
                                new String[]{"+currency,+c", "+amount,+money,+a,+m"},
                                new Object[]{iConomy.getBank().getCurrency(), iConomy.getBank().format(totalMoney / totalPlayers)}
                        ));

                        Messaging.send(Template.parse("statistics.accounts",
                                new String[]{"+currency,+c", "+amount,+accounts,+a"},
                                new Object[]{iConomy.getBank().getCurrency(), totalPlayers}
                        ));

                        return;
                    }

                    if (Misc.is(split[1], new String[]{ "help", "?", "grant", "-g", "reset", "-x", "set", "-s", "pay", "-p" })) {
                        showHelp(player); return;
                    } else {
                        if (!iConomy.hasPermissions(sender, "iConomy.access")) {
                            return;
                        }

                        if (iConomy.getBank().hasAccount(split[1])) {
                            showBalance(split[1], sender, false);
                        } else {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{split[1]}));
                        }

                        return;
                    }

                case 3:

                    if (Misc.is(split[1], new String[]{"rank", "-r"})) {
                        if (!iConomy.hasPermissions(sender, "iConomy.rank")) {
                            return;
                        }

                        if (iConomy.getBank().hasAccount(split[2])) {
                            showRank(sender, split[2]);
                        } else {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{split[2]}));
                        }

                        return;
                    }

                    if (Misc.is(split[1], new String[]{"top", "-t"})) {
                        if (!iConomy.hasPermissions(player, "iConomy.list")) {
                            return;
                        }

                        try {
                            showTop(sender, Integer.parseInt(split[2]) < 0 ? 5 : Integer.parseInt(split[2]));
                        } catch (Exception e) {
                            showTop(sender, 5);
                        }

                        return;
                    }

                    if (Misc.is(split[1], new String[]{"reset", "-x"})) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.reset")) {
                            return;
                        }

                        if (iConomy.getBank().hasAccount(split[2])) {
                            if(isPlayer)
                                showReset(split[2], player, false);
                            else {
                                showReset(split[2], null, true);
                            }
                        } else {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{split[2]}));
                        }

                        return;
                    }

                    break;

                case 4:

                    if (Misc.is(split[1], new String[]{"pay", "-p"})) {
                        if (!iConomy.hasPermissions(sender, "iConomy.payment") || !isPlayer) {
                            return;
                        }

                        String name = "";
                        double amount = 0.0;

                        if (iConomy.getBank().hasAccount(split[2])) {
                            name = split[2];
                        } else {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{split[2]}));
                            return;
                        }

                        try {
                            amount = Double.parseDouble(split[3]);

                            if (amount < 0.01) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            Messaging.send("&cInvalid amount: &f" + amount);
                            Messaging.send("&cUsage: &f/money &c[&f-p&c|&fpay&c] <&fplayer&c> &c<&famount&c>");
                            return;
                        }

                        showPayment(player.getName(), name, amount);
                        return;
                    }

                    if (Misc.is(split[1], new String[]{"grant", "-g"})) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.grant")) {
                            return;
                        }

                        String name = "";
                        double amount = 0.0;

                        if (iConomy.getBank().hasAccount(split[2])) {
                            name = split[2];
                        } else {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{split[2]}));
                        }

                        try {
                            amount = Double.parseDouble(split[3]);
                        } catch (NumberFormatException e) {
                            Messaging.send("&cInvalid amount: &f" + split[3]);
                            Messaging.send("&cUsage: &f/money &c[&f-g&c|&fgrant&c] <&fplayer&c> (&f-&c)&c<&famount&c>");
                        }

                        if(isPlayer)
                            showGrant(name, player, amount, false);
                        else {
                            showGrant(name, null, amount, true);
                        }

                        return;
                    }

                    if (Misc.is(split[1], new String[]{"set", "-s"})) {
                        if (!iConomy.hasPermissions(player, "iConomy.admin.set")) {
                            return;
                        }

                        String name = "";
                        double amount = 0.0;

                        if (iConomy.getBank().hasAccount(split[2])) {
                            name = split[2];
                        } else {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{split[2]}));
                        }

                        try {
                            amount = Double.parseDouble(split[3]);
                        } catch (NumberFormatException e) {
                            Messaging.send("&cInvalid amount: &f" + split[3]);
                            Messaging.send("&cUsage: &f/money &c[&f-g&c|&fgrant&c] <&fplayer&c> (&f-&c)&c<&famount&c>");
                        }

                        if(isPlayer)
                            showSet(name, player, amount, false);
                        else {
                            showSet(name, null, amount, true);
                        }

                        return;
                    }

                    break;
            }

            showHelp(player);
        }

        return;
    }
}
