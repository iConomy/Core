package com.iConomy.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Bank;
import com.iConomy.system.BankAccount;
import com.iConomy.system.Holdings;
import com.iConomy.util.Constants;
import com.iConomy.util.Messaging;
import com.iConomy.util.Misc;
import com.iConomy.util.Template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerJoinEvent;
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
        this.Template = new Template(directory, "Template.yml");
    }

    /**
     * Help documentation for iConomy all in one method.
     *
     * Allows us to easily utilize all throughout the class without having multiple
     * instances of the same help lines.
     */
    private void getMoneyHelp(CommandSender player) {
        Messaging.send("&e ");
        Messaging.send("&f iConomy (&c" + Constants.Codename + "&f)");
        Messaging.send("&e ");
        Messaging.send("&f [] Required, () Optional");
        Messaging.send(" ");
        Messaging.send("`G  /money &e Check your balance");
        Messaging.send("`G  /money `g? &e For help & Information");

        if (iConomy.hasPermissions(player, "iConomy.rank")) {
            Messaging.send("`G  /money `grank `G(`wplayer`G) &e Rank on the topcharts.   ");
        }
        
        if (iConomy.hasPermissions(player, "iConomy.list")) {
            Messaging.send("`G  /money `gtop `G(`wamount`G) &e Richest players listing.  ");
        }

        if (iConomy.hasPermissions(player, "iConomy.payment")) {
            Messaging.send("`G  /money `gpay `G[`wplayer`G] [`wamount`G] &e Send money to a player.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.grant")) {
            Messaging.send("`G  /money `ggrant `G[`wplayer`G] [`wamount`G] &e Give money.");
            Messaging.send("`G  /money `ggrant `G[`wplayer`G] -[`wamount`G] &e Take money.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.set")) {
            Messaging.send("`G  /money `gset `G[`wplayer`G] [`wamount`G] &e Sets a players balance.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.hide")) {
            Messaging.send("`G  /money `ghide `G[`wplayer`G] `wtrue`G/`wfalse &e Hide or show an account.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.account.create")) {
            Messaging.send("`G  /money `gcreate `G[`wplayer`G] &e Create player account.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.account.remove")) {
            Messaging.send("`G  /money `gremove `G[`wplayer`G] &e Remove player account.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.reset")) {
            Messaging.send("`G  /money `greset `G[`wplayer`G] &e Reset player account.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.purge")) {
            Messaging.send("`G  /money `gpurge &e Remove all accounts with inital holdings.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.empty")) {
            Messaging.send("`G  /money `gempty &e Empties database.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.stats")) {
            Messaging.send("`G  /money `gstats &e Check all economic stats.");
        }

        Messaging.send(" ");
    }

    /**
     * Help documentation for iConomy all in one method.
     *
     * Allows us to easily utilize all throughout the class without having multiple
     * instances of the same help lines.
     */
    private void getBankHelp(CommandSender player) {
        Messaging.send("&e ");
        Messaging.send("&f iConomy (&c" + Constants.Codename + "&f)");
        Messaging.send("&e ");
        Messaging.send("&f [] Required, () Optional");
        Messaging.send(" ");
        Messaging.send("`G  /bank &e Check your bank accounts");
        Messaging.send("`G  /bank `g? &e For help & Information");

        if (iConomy.hasPermissions(player, "iConomy.bank.list")) {
            Messaging.send("`G  /bank `glist `G(`w#`G) &e Paged list of banks.");
        }

        if (iConomy.hasPermissions(player, "iConomy.bank.main")) {
            Messaging.send("`G  /bank `gmain &e View your main bank.");
        }

        if (iConomy.hasPermissions(player, "iConomy.bank.main.view")) {
            Messaging.send("`G  /bank `gmain `G[`waccount`G] &e View an accounts main bank.");
        }

        if (iConomy.hasPermissions(player, "iConomy.bank.main.set")) {
            Messaging.send("`G  /bank `gmain set `G[`wbank`G] &e Set your main bank.");
        }

        if (iConomy.hasPermissions(player, "iConomy.bank.join")) {
            Messaging.send("`G  /bank `gjoin `G[`wbank`G] &e Create an account with a bank.");
        }
        
        if (iConomy.hasPermissions(player, "iConomy.bank.leave")) {
            Messaging.send("`G  /bank `gleave `G[`wbank`G] &e Close an account with a bank.");
        }

        if (iConomy.hasPermissions(player, "iConomy.bank.transfer")) {
            Messaging.send("`G  /bank `gsend `G[`wto`G] `r[`wamount`r] &e Send money to another players bank.");
        }

        if (iConomy.hasPermissions(player, "iConomy.bank.transfer.multiple")) {
            Messaging.send("`G  /bank `G[`wfrom-bank`G] `gsend `G[`wto`G] `G[`wamount`G]");
        }

        if (iConomy.hasPermissions(player, "iConomy.bank.transfer.multiple")) {
            Messaging.send("`G  /bank `G[`wfrom-bank`G] `gsend `G[`wto-bank`G] `G[`wto`G] `G[`wamount`G]");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.bank.create")) {
            Messaging.send("`G  /bank `gcreate `G[`wbank`G] &e Create a bank.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.bank.remove")) {
            Messaging.send("`G  /bank `gremove `G[`wbank`G] &e Close a bank.");
        }

        if (iConomy.hasPermissions(player, "iConomy.admin.bank.set")) {
            Messaging.send("`G  /bank `G[`wbank`G] `gset `G[`wkey`G] `G[`wvalue`G] &e Create a bank.");
            Messaging.send("`y   Keys: `Yname`y, `Yinitial`y, `Ymajor`y, `Yminor`y, `Yfee");
        }

        Messaging.send(" ");
    }

    public boolean setHidden(String name, boolean hidden) {
        return iConomy.getAccount(name).setHidden(hidden);
    }

    /**
     * Account Creation
     */
    public void createAccount(String name) {
        iConomy.getAccount(name);
        Messaging.send(Template.color("tag.money") + Template.parse("accounts.create", new String[]{ "+name,+n" }, new String[]{ name }));
    }

    public void createBank(CommandSender sender, String bank) {
        if(iConomy.Banks.exists(bank)) {
            Messaging.send(sender, Template.color("error.bank.exists"));
            return;
        }

        Bank Bank = iConomy.Banks.create(bank);

        if(Bank == null) {
            Messaging.send(sender, Template.parse("error.bank.couldnt", new String[]{ "+bank,+b,+name,+n" }, new String[]{ bank }));
        } else {
            Messaging.send(sender, Template.parse("banks.create", new String[]{ "+bank,+b,+name,+n" }, new String[]{ bank }));
        }

        return;
    }

    public void createBank(CommandSender sender, String bank, Double initial, Double fee) {
        if(iConomy.Banks.exists(bank)) {
            Messaging.send(sender, Template.color("error.bank.exists"));
            return;
        }

        Bank Bank = iConomy.Banks.create(bank);

        if(Bank == null) {
            Messaging.send(sender, Template.parse("error.bank.couldnt", new String[]{ "+bank,+b,+name,+n" }, new String[]{ bank }));
        } else {
            Bank.setInitialHoldings(initial);
            Bank.setFee(fee);

            Messaging.send(sender, Template.parse("banks.create", new String[]{ "+bank,+b,+name,+n" }, new String[]{ bank }));
        }

        return;
    }

    public void setBankValue(CommandSender sender, String bank, String key, Object value) {
        if(!iConomy.Banks.exists(bank)) {
            Messaging.send(sender, Template.parse("error.bank.doesnt", new String[]{ "+bank,+b,+name,+n" }, new String[]{ bank }));
            return;
        }
        
        Bank Bank = iConomy.getBank(bank);

        if(key.equals("initial")) {
            Double initial = Double.valueOf(value.toString());
            Bank.setInitialHoldings(initial);
        } else if(key.equals("major")) {
            if(!value.toString().contains(",")) {
                Messaging.send(sender, "`rMajor value is missing seperator between single and plural.");
                Messaging.send(sender, "`r  Ex: `s/bank [`Sname`s] set `Smajor`s Dollar`S,`sDollars");
                return;
            }

            String[] line = value.toString().split(",");

            if(line[0].isEmpty() || line.length < 1 || line[1].isEmpty()) {
                Messaging.send(sender, "`rMinor value is missing a single `Ror`r plural.");
                Messaging.send(sender, "`r  Ex: `s/bank [`Sname`s] set `Smajor`s Dollar`S,`sDollars");
                return;
            }

            Bank.setMajor(line[0], line[1]);
        } else if(key.equals("minor")) {
            if(!value.toString().contains(",")) {
                Messaging.send(sender, "`rMinor value is missing seperator between single and plural.");
                Messaging.send(sender, "`r  Ex: `s/bank [`Sname`s] set `Sminor`s Coin`S,`sCoins");
                return;
            }

            String[] line = value.toString().split(",");

            if(line[0].isEmpty() || line.length < 1 || line[1].isEmpty()) {
                Messaging.send(sender, "`rMinor value is missing a single `Ror`r plural.");
                Messaging.send(sender, "`r  Ex: `s/bank [`Sname`s] set `Sminor`s Coin`S,`sCoins");
                return;
            }

            Bank.setMinor(line[0], line[1]);
        } else if(key.equals("fee")) {
            Double fee = Double.valueOf(value.toString());
            Bank.setFee(fee);
        } else if(key.equals("name")) {
            Bank.setName(value.toString());
        }

        Messaging.send(sender,
            Template.color("tag.bank") + Template.parse("bank.set",
                new String[] { "+bank,+name,+n,+b", "+key,+k", "+value,+val,+v" },
                new Object[] { bank, key, value }
            )
        );
    }

    public void createBankAccount(CommandSender sender, String name, String player) {
        Bank bank = iConomy.getBank(name);

        if(!iConomy.hasAccount(player)) {
            Messaging.send(sender, Template.color("error.bank.account.none"));
            return;
        }

        Account account = iConomy.getAccount(player);

        if(bank == null) {
            Messaging.send(sender, Template.parse("error.bank.doesnt", new String[] { "+bank,+name,+b,+n" }, new String[] { name }));
            return;
        }

        int count = iConomy.Banks.count(player);

        if(count > 1 && !Constants.BankingMultiple || !iConomy.hasPermissions(sender, "iConomy.bank.join.multiple")) {
            Messaging.send(sender, Template.color("error.bank.account.maxed"));
            return;
        }

        if(bank != null) {
            double fee = bank.getFee();
            if(fee > account.getHoldings().balance()) {
                Messaging.send(sender, Template.color("error.bank.account.funds"));
                return;
            }

            if(bank.createAccount(player)){
                account.getHoldings().subtract(fee);

                Messaging.send(sender, Template.color("tag.bank") + Template.parse("accounts.bank.create",
                    new String[] { "+bank,+b", "+name,+n" },
                    new String[] { name, player })
                );

                if(count == 0) {
                    iConomy.getAccount(player).setMainBank(bank.getId());
                }

                return;
            } else {
                Messaging.send(sender, Template.color("error.bank.account.failed"));
            }
        } else {
            Messaging.send(sender, Template.color("error.bank.account.none"));
        }
    }
    
    /**
     * Account Removal
     */
    public void removeAccount(String name) {
        iConomy.Accounts.remove(name);
        Messaging.send(Template.color("tag.money") + Template.parse("accounts.remove", new String[]{ "+name,+n" }, new String[]{ name }));
    }

    public void removeBankAccount(CommandSender sender, String name, String player) {
        Bank bank = iConomy.getBank(name);

        if(!iConomy.hasAccount(player)) {
            Messaging.send(Template.color("error.bank.account.none"));
            return;
        }

        if(bank == null) {
            Messaging.send(Template.parse("error.bank.doesnt", new String[] { "+bank,+name,+b,+n" }, new String[] { name }));
            return;
        }

        if(!bank.hasAccount(player)) {
            Messaging.send(Template.parse("error.bank.account.doesnt", new String[] { "+name,+n" }, new String[] { player }));
            return;
        }

        bank.removeAccount(player);

        Messaging.send(Template.color("tag.bank") + Template.parse("accounts.bank.remove",
            new String[] { "+bank,+b", "+name,+n" },
            new String[] { name, player })
        );
    }

    /**
     * Show list of banks
     *
     * @param player
     * @param name
     */
    public void showBankList(CommandSender player, int current) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        LinkedList<Bank> banks = new LinkedList<Bank>();
        int total = iConomy.Banks.count();
        int perPage = 7;
        int page = (current <= 0) ? 1 : current;
        int totalPages = (int) (((total % perPage) == 0) ? total / perPage : Math.floor(total / perPage) + 1);
        int start = (page-1) * perPage;
        String entry = (Constants.BankFee != 0.0) ? (Constants.FormatMinor) ? "list.banks.all-entry" : "list.banks.fee-major-entry" : (Constants.FormatMinor) ? "list.banks.entry" : "list.banks.major-entry";

        page = (page > totalPages) ? totalPages : page;

        if(total == -1){
            Messaging.send(player, Template.parse("list.banks.opening", new String[]{ "+amount,+a", "+total,+t" }, new Object[]{ 0, 0 }));
            Messaging.send(player, Template.color("list.banks.empty"));
            return;
        }

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("SELECT name FROM " + Constants.SQLTable + "_Banks ORDER BY name ASC LIMIT ?, ?");
            ps.setInt(1, start);
            ps.setInt(2, perPage);
            rs = ps.executeQuery();

            while(rs.next()) {
                banks.add(new Bank(rs.getString("name")));
            }
        } catch (Exception e) {
            System.out.println("[iConomy] Error while listing banks: " + e.getMessage()); return;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        if(banks.isEmpty()) {
            Messaging.send(player, Template.parse("list.banks.opening", new String[]{ "+amount,+a", "+total,+t" }, new Object[]{ 0, 0 }));
            Messaging.send(player, Template.color("list.banks.empty"));
            return;
        }

        Messaging.send(player, Template.parse("list.banks.opening", new String[]{ "+amount,+a", "+total,+t" }, new Object[]{ page, totalPages }));

        for(Bank bank : banks) {
            if(bank == null) continue;

            String major = bank.getMajor().get(1);
            String minor = bank.getMinor().get(1);
            
            Messaging.send(player, Template.parse(
                entry,
                new String[]{ "+name,+bank,+b,+n", "+fee,+f", "+initial,+holdings,+i,+h", "+major", "+minor" },
                new Object[]{ bank.getName(), iConomy.format(bank.getFee()), iConomy.format(bank.getInitialHoldings()), major, minor }
            ));
        }
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
            Messaging.send(viewing, Template.color("tag.money") + Template.parse("personal.balance", new String[]{"+balance,+b"}, new String[]{ iConomy.format(name) }));
        } else {
            Messaging.send(viewing, Template.color("tag.money") + Template.parse("player.balance", new String[]{"+balance,+b", "+name,+n"}, new String[]{ iConomy.format(name), name }));
        }
    }

    public void showBankAccounts(CommandSender player, String name) {
        List<BankAccount> Accounts = null;
        boolean self = Misc.isSelf(player, name);

        if(!iConomy.hasAccount(name)) {
            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{ name }));
            return;
        }

        Accounts = iConomy.getAccount(name).getBankAccounts();

        if(Accounts == null || Accounts.isEmpty()) {
            Messaging.send(Template.color("error.bank.account.none"));
            return;
        }

        for(BankAccount account : Accounts) {
            if(account == null) { continue; }

            Messaging.send(
                player,
                Template.color("tag.bank") +
                Template.parse((self) ? "personal.bank.balance" : "player.bank.balance", new String[]{ "+balance,+holdings,+h", "+bank,+b", "+name,+n" }, new String[]{ account.getHoldings().toString(), account.getBankName(), name })
            );
        }
    }

    public void showBankAccount(CommandSender player, String bank, String name) {
        Bank Bank = null;
        BankAccount account = null;
        Holdings holdings = null;
        boolean self = Misc.isSelf(player, name);

        if(!iConomy.Banks.exists(bank)) {
            Messaging.send(player, Template.parse("error.bank.doesnt", new String[]{ "+bank,+name,+n,+b" }, new String[]{ bank }));
            return;
        }

        Bank = iConomy.getBank(bank);

        if(Bank == null) {
            Messaging.send(player, Template.parse("error.bank.doesnt", new String[]{ "+bank,+name,+n,+b" }, new String[]{ bank }));
            return;
        }

        if(!Bank.hasAccount(name)) {
            Messaging.send(player, Template.parse("error.bank.account.doesnt", new String[]{ "+bank,+b", "+name,+n" }, new String[]{ bank, name }));
            return;
        }

        account = Bank.getAccount(name);

        if(account == null) {
            Messaging.send(player, Template.parse("error.bank.account.doesnt", new String[]{ "+bank,+b", "+name,+n" }, new String[]{ bank, name }));
            return;
        }

        holdings = account.getHoldings();

        Messaging.send(
            player,
            Template.color("tag.bank") +
            Template.parse((self) ? "personal.bank.balance" : "player.bank.balance", new String[]{ "+balance,+holdings,+h", "+bank,+b", "+name,+n" }, new String[]{ holdings.toString(), account.getBankName(), name })
        );
    }
    
    public void showBankWithdrawal(CommandSender player, String bank, String name, double amount) {
        if(!iConomy.hasAccount(name)) {
            Messaging.send(Template.color("error.bank.account.none"));
            return;
        }
        
        Bank Bank = null;
        Account Account = iConomy.getAccount(name);
        BankAccount account = null;
        Holdings holdings = null;
        Holdings held = null;
        boolean self = Misc.isSelf(player, name);

        if(!iConomy.Banks.exists(bank)) {
            Messaging.send(player, Template.parse("error.bank.doesnt", new String[]{ "+bank,+name,+n,+b" }, new String[]{ bank }));
            return;
        }

        Bank = iConomy.getBank(bank);

        if(Bank == null) {
            Messaging.send(player, Template.parse("error.bank.doesnt", new String[]{ "+bank,+name,+n,+b" }, new String[]{ bank }));
            return;
        }

        if(!Bank.hasAccount(name)) {
            Messaging.send(player, Template.parse("error.bank.account.doesnt", new String[]{ "+bank,+b", "+name,+n" }, new String[]{ bank, name }));
            return;
        }

        account = Bank.getAccount(name);

        if(account == null) {
            Messaging.send(player, Template.parse("error.bank.account.doesnt", new String[]{ "+bank,+b", "+name,+n" }, new String[]{ bank, name }));
            return;
        }

        held = Account.getHoldings();
        holdings = account.getHoldings();
        Double onHand = held.balance();
        Double balance = holdings.balance();

        if (balance < 0.0 || !holdings.hasEnough(amount)) {
            if (player != null) {
                Messaging.send(player, Template.color("error.bank.account.funds"));
            }
        } else {
            holdings.subtract(amount);
            held.add(amount);

            onHand = held.balance();
            balance = holdings.balance();

            iConomy.getTransactions().insert("[Bank] " + bank, name, balance, onHand, 0.0, 0.0, amount);
            iConomy.getTransactions().insert(name, "[Bank] " + bank, onHand, balance, 0.0, amount, 0.0);

            if (player != null) {
                Messaging.send(
                    player,
                    Template.color("tag.bank") + 
                    Template.parse( "personal.bank.withdraw",
                    new String[]{ "+bank,+b,+name,+n", "+amount,+a" },
                    new String[]{ bank, iConomy.format(amount) })
                );

                showBalance(name, player, true);
                showBankAccount(player, bank, name);
            }
        }
    }

    public void showBankDeposit(CommandSender player, String bank, String name, double amount) {
        if(!iConomy.hasAccount(name)) {
            Messaging.send(Template.color("error.bank.account.none"));
            return;
        }

        Bank Bank = null;
        Account Account = iConomy.getAccount(name);
        BankAccount account = null;
        Holdings holdings = null;
        Holdings held = null;
        boolean self = Misc.isSelf(player, name);

        if(!iConomy.Banks.exists(bank)) {
            Messaging.send(player, Template.parse("error.bank.doesnt", new String[]{ "+bank,+name,+n,+b" }, new String[]{ bank }));
            return;
        }

        Bank = iConomy.getBank(bank);

        if(Bank == null) {
            Messaging.send(player, Template.parse("error.bank.doesnt", new String[]{ "+bank,+name,+n,+b" }, new String[]{ bank }));
            return;
        }

        if(!Bank.hasAccount(name)) {
            Messaging.send(player, Template.parse("error.bank.account.doesnt", new String[]{ "+bank,+b", "+name,+n" }, new String[]{ bank, name }));
            return;
        }

        account = Bank.getAccount(name);

        if(account == null) {
            Messaging.send(player, Template.parse("error.bank.account.doesnt", new String[]{ "+bank,+b", "+name,+n" }, new String[]{ bank, name }));
            return;
        }

        held = Account.getHoldings();
        holdings = account.getHoldings();
        Double onHand = held.balance();
        Double balance = holdings.balance();

        if (onHand < 0.0 || !held.hasEnough(amount)) {
            if (player != null) {
                Messaging.send(player, Template.color("error.funds"));
            }
        } else {
            held.subtract(amount);
            holdings.add(amount);

            onHand = held.balance();
            balance = holdings.balance();

            iConomy.getTransactions().insert(name, "[Bank] " + bank, onHand, balance, 0.0, 0.0, amount);
            iConomy.getTransactions().insert("[Bank] " + bank, name, balance, onHand, 0.0, amount, 0.0);

            if (player != null) {
                Messaging.send(
                    player,
                    Template.color("tag.bank") +
                    Template.parse( "personal.bank.deposit",
                    new String[]{ "+bank,+b,+name,+n", "+amount,+a" },
                    new String[]{ bank, iConomy.format(amount) })
                );

                showBalance(name, player, true);
                showBankAccount(player, bank, name);
            }
        }
    }

    public void showBankTransaction(CommandSender player, String from, String to, double amount) {
        if(from.toLowerCase().equalsIgnoreCase(to.toLowerCase())) {
            Messaging.send(player, Template.color("payment.self"));
            return;
        }

        if(!iConomy.hasAccount(from) || !iConomy.hasAccount(to)) {
            Messaging.send(player, Template.color("error.bank.account.none"));
            return;
        }

        Bank Bank = null;
        Bank from_bank = iConomy.getAccount(from).getMainBank();
        Bank to_bank = iConomy.getAccount(to).getMainBank();
        BankAccount from_account = iConomy.getAccount(from).getMainBankAccount();
        BankAccount to_account = iConomy.getAccount(to).getMainBankAccount();
        String from_bank_name = from_account.getBankName();
        String to_bank_name = to_account.getBankName();
        Holdings from_holdings = null;
        Holdings to_holdings = null;

        if(from_bank == null || from_account == null) {
            Messaging.send(player, Template.parse("error.bank.account.doesnt", new String[]{ "+name,+n" }, new String[]{ from }));
            return;
        }

        if(to_bank == null || from_account == null) {
            Messaging.send(player, Template.parse("error.bank.account.doesnt", new String[]{ "+name,+n" }, new String[]{ to }));
            return;
        }

        from_holdings = from_account.getHoldings();
        to_holdings = to_account.getHoldings();

        if (from_holdings.balance() < 0.0 || !from_holdings.hasEnough(amount)) {
            if (player != null) {
                Messaging.send(player, Template.color("error.bank.account.funds"));
            }
        } else {
            from_holdings.subtract(amount);
            to_holdings.add(amount);

            Double from_current = from_holdings.balance();
            Double to_current = to_holdings.balance();

            iConomy.getTransactions().insert(from, to, to_current, from_current, 0.0, 0.0, amount);
            iConomy.getTransactions().insert(to, from, from_current, to_current, 0.0, amount, 0.0);

            if (player != null) {
                Messaging.send(
                    player,
                    Template.color("tag.bank") +
                    Template.parse( (from.equalsIgnoreCase(to)) ? "personal.bank.transfer" : "personal.bank.between",
                    new String[]{ "+bank,+b", "+bankAlt,+ba,+bA", "+name,+n", "+amount,+a" },
                    new String[]{ from_bank_name, to_bank_name, to, iConomy.format(amount) })
                );

                showBankAccount(player, from_bank_name, from);
            }

            if(!from.equalsIgnoreCase(to)) {
                Player playerTo = iConomy.getBukkitServer().getPlayer(to);

                if(playerTo != null) {
                    Messaging.send(
                        player,
                        Template.color("tag.bank") +
                        Template.parse( "personal.bank.recieved",
                        new String[]{ "+bank,+b", "+amount,+a" },
                        new String[]{ to_bank_name, iConomy.format(amount) })
                    );
                    showBankAccount(playerTo, to_bank_name, to);
                }
            }
        }
    }

    public void showBankTransfer(CommandSender player, String from, String from_bank, String to, String to_bank, double amount) {
        Bank Bank = null;
        Bank fBank = iConomy.getBank(from_bank);
        Bank tBank = iConomy.getBank(to_bank);
        Holdings from_holdings = null;
        Holdings to_holdings = null;

        if(fBank == null) {
            Messaging.send(player, Template.parse("error.bank.doesnt", new String[]{ "+bank,+name,+n,+b" }, new String[]{ from_bank }));
            return;
        }

        if(tBank == null) {
            Messaging.send(player, Template.parse("error.bank.doesnt", new String[]{ "+bank,+name,+n,+b" }, new String[]{ to_bank }));
            return;
        }

        BankAccount fAccount = iConomy.getAccount(from).getMainBankAccount();
        BankAccount tAccount = iConomy.getAccount(to).getMainBankAccount();

        if(fAccount == null) {
            Messaging.send(player, Template.parse("error.bank.account.doesnt", new String[]{ "+name,+n" }, new String[]{ from }));
            return;
        }

        if(tAccount == null) {
            Messaging.send(player, Template.parse("error.bank.account.doesnt", new String[]{ "+name,+n" }, new String[]{ to }));
            return;
        }

        String from_bank_name = fAccount.getBankName();
        String to_bank_name = tAccount.getBankName();
        from_holdings = fAccount.getHoldings();
        to_holdings = tAccount.getHoldings();

        if (from_holdings.balance() < 0.0 || !from_holdings.hasEnough(amount)) {
            if (player != null) {
                Messaging.send(player, Template.color("error.bank.account.funds"));
            }
        } else {
            from_holdings.subtract(amount);
            to_holdings.add(amount);

            Double from_current = from_holdings.balance();
            Double to_current = to_holdings.balance();

            iConomy.getTransactions().insert(from, to, to_current, from_current, 0.0, 0.0, amount);
            iConomy.getTransactions().insert(to, from, from_current, to_current, 0.0, amount, 0.0);

            if (player != null) {
                Messaging.send(
                    player,
                    Template.color("tag.bank") +
                    Template.parse( (from.equalsIgnoreCase(to)) ? "personal.bank.transfer" : "personal.bank.between",
                    new String[]{ "+bank,+b", "+bankAlt,+ba,+bA", "+name,+n", "+amount,+a" },
                    new String[]{ from_bank_name, to_bank_name, to, iConomy.format(amount) })
                );

                showBankAccount(player, from_bank_name, from);
            }

            if(!from.equalsIgnoreCase(to)) {
                Player playerTo = iConomy.getBukkitServer().getPlayer(to);

                if(playerTo != null) {
                    Messaging.send(
                        player,
                        Template.color("tag.bank") +
                        Template.parse( "personal.bank.recieved",
                        new String[]{ "+bank,+b", "+amount,+a" },
                        new String[]{ to_bank_name, iConomy.format(amount) })
                    );
                    
                    showBankAccount(playerTo, to_bank_name, to);
                }
            }
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

        if(paymentFrom != null) {
            from = paymentFrom.getName();
        }

        if(paymentTo != null) {
            to = paymentTo.getName();
        }

        Holdings From = iConomy.getAccount(from).getHoldings();
        Holdings To = iConomy.getAccount(to).getHoldings();

        if (from.equals(to)) {
            if (paymentFrom != null) {
                Messaging.send(paymentFrom, Template.color("payment.self"));
            }
        } else if (amount < 0.0 || !From.hasEnough(amount)) {
            if (paymentFrom != null) {
                Messaging.send(paymentFrom, Template.color("error.funds"));
            }
        } else {
            From.subtract(amount);
            To.add(amount);

            Double balanceFrom = From.balance();
            Double balanceTo = To.balance();

            iConomy.getTransactions().insert(from, to, balanceFrom, balanceTo, 0.0, 0.0, amount);
            iConomy.getTransactions().insert(to, from, balanceTo, balanceFrom, 0.0, amount, 0.0);

            if (paymentFrom != null) {
                Messaging.send(
                        paymentFrom,
                        Template.color("tag.money") + Template.parse(
                        "payment.to",
                        new String[]{"+name,+n", "+amount,+a"},
                        new String[]{to, iConomy.format(amount)}));

                showBalance(from, paymentFrom, true);
            }

            if (paymentTo != null) {
                Messaging.send(
                        paymentTo,
                        Template.color("tag.money") + Template.parse(
                        "payment.from",
                        new String[]{"+name,+n", "+amount,+a"},
                        new String[]{from, iConomy.format(amount)}));

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

        if(player != null) {
            account = player.getName();
        }

        // Get account
        Account Account = iConomy.getAccount(account);

        // Log Transaction
        iConomy.getTransactions().insert(account, "[System]", 0.0, 0.0, 0.0, 0.0, Account.getHoldings().balance());

        // Reset
        Account.getHoldings().reset();

        if (player != null) {
            Messaging.send(player, Template.color("personal.reset"));
        }

        if (controller != null) {
            Messaging.send(
                Template.parse(
                    "player.reset",
                    new String[]{ "+name,+n" },
                    new String[]{ account }
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

        if(online != null) {
            name = online.getName();
        }

        Account account = iConomy.getAccount(name);

        if(account != null) {
            Holdings holdings = account.getHoldings();
            holdings.add(amount);

            Double balance = holdings.balance();

            if (amount < 0.0) {
                iConomy.getTransactions().insert("[System]", name, 0.0, balance, 0.0, 0.0, amount);
            } else {
                iConomy.getTransactions().insert("[System]", name, 0.0, balance, 0.0, amount, 0.0);
            }

            if (online != null) {
                Messaging.send(online,
                    Template.color("tag.money") + Template.parse(
                        (amount < 0.0) ? "personal.debit" : "personal.credit",
                        new String[]{"+by", "+amount,+a"},
                        new String[]{(console) ? "console" : controller.getName(), iConomy.format(((amount < 0.0) ? amount * -1 : amount))}
                    )
                );

                showBalance(name, online, true);
            }

            if (controller != null) {
                Messaging.send(
                    Template.color("tag.money") + Template.parse(
                        (amount < 0.0) ? "player.debit" : "player.credit",
                        new String[]{"+name,+n", "+amount,+a"},
                        new String[]{ name, iConomy.format(((amount < 0.0) ? amount * -1 : amount)) }
                    )
                );
            }

            if (console) {
                System.out.println("Player " + account.getName() + "'s account had " + ((amount < 0.0) ? "negative " : "") + iConomy.format(((amount < 0.0) ? amount * -1 : amount)) + " grant to it.");
            } else {
                System.out.println(Messaging.bracketize("iConomy") + "Player " + account.getName() + "'s account had " + ((amount < 0.0) ? "negative " : "") + iConomy.format(((amount < 0.0) ? amount * -1 : amount)) + " grant to it by " + controller.getName() + ".");
            }
        }
    }

    /**
     * Show the actual setting of the new balance of an account.
     *
     * @param account
     * @param controller If set to null, won't display messages.
     * @param amount
     * @param console Is it sent via console?
     */
    public void showSet(String name, Player controller, double amount, boolean console) {
        Player online = iConomy.getBukkitServer().getPlayer(name);

        if(online != null) {
            name = online.getName();
        }

        Account account = iConomy.getAccount(name);

        if(account != null) {
            Holdings holdings = account.getHoldings();
            holdings.set(amount);

            Double balance = holdings.balance();

            // Log Transaction
            iConomy.getTransactions().insert("[System]", name, 0.0, balance, amount, 0.0, 0.0);

            if (online != null) {
                Messaging.send(online,
                    Template.color("tag.money") + Template.parse(
                        "personal.set",
                        new String[]{"+by", "+amount,+a"},
                        new String[]{(console) ? "Console" : controller.getName(), iConomy.format(amount) }
                    )
                );

                showBalance(name, online, true);
            }

            if (controller != null) {
                Messaging.send(
                    Template.color("tag.money") + Template.parse(
                        "player.set",
                        new String[]{ "+name,+n", "+amount,+a" },
                        new String[]{ name, iConomy.format(amount) }
                    )
                );
            }

            if (console) {
                System.out.println("Player " + account + "'s account had " + iConomy.format(amount) + " set to it.");
            } else {
                System.out.println(Messaging.bracketize("iConomy") + "Player " + account + "'s account had " + iConomy.format(amount) + " set to it by " + controller.getName() + ".");
            }
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
        Account account = iConomy.getAccount(player);

        if (account != null) {
            int rank = account.getRank();
            boolean isPlayer = (viewing instanceof Player);
            boolean isSelf = (isPlayer) ? ((((Player)viewing).getName().equalsIgnoreCase(player)) ? true : false) : false;

            Messaging.send(
                viewing,
                Template.color("tag.money") + Template.parse(
                    ((isSelf) ? "personal.rank" : "player.rank"),
                    new Object[]{ "+name,+n", "+rank,+r" },
                    new Object[]{ player, rank }
                )
            );
        } else {
            Messaging.send(
                viewing,
                Template.parse(
                    "error.account",
                    new Object[]{ "+name,+n" },
                    new Object[]{ player }
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
        LinkedHashMap<String, Double> Ranking = iConomy.Accounts.ranking(amount);
        int count = 1;

        Messaging.send(
                viewing,
                Template.parse(
                "top.opening",
                new Object[]{ "+amount,+a" },
                new Object[]{ amount }
            )
        );

        if(Ranking == null || Ranking.isEmpty()) {
            Messaging.send(viewing, Template.color("top.empty"));

            return;
        }

        for (String account : Ranking.keySet()) {
            Double balance = Ranking.get(account);

            Messaging.send(
                viewing,
                Template.parse(
                    "top.line",
                    new String[]{"+i,+number", "+player,+name,+n", "+balance,+b"},
                    new Object[]{count, account, iConomy.format(balance)}
                )
            );

            count++;
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
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (iConomy.getAccount(player.getName()) == null) {
            System.out.println("[iConomy] Error creating / grabbing account for: " + player.getName());
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

        if (split[0].equalsIgnoreCase("bank") && Constants.Banking) {
            switch (split.length) {
                case 1:
                    if(isPlayer) {
                        showBankAccounts(sender, player.getName());
                    } else {
                        Messaging.send("`RCannot show bank list without organism.");
                    }

                    return;

                case 2:

                    if (Misc.is(split[1], new String[]{ "list", "-l" }) && Constants.BankingMultiple) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.list")) {
                            return;
                        }

                        showBankList(sender, 0); return;
                    }

                    if (Misc.is(split[1], new String[]{ "main", "-m" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.main")) {
                            return;
                        }

                        if(isPlayer) {
                            Account account = iConomy.getAccount(player.getName());

                            if(account != null) {
                                Bank bank = account.getMainBank();

                                if(bank != null)
                                    showBankAccount(sender, bank.getName(), player.getName());
                                else {
                                    Messaging.send(Template.color("error.bank.account.none"));
                                }
                            }
                        } else {
                            Messaging.send("`RCannot show main bank without organism.");
                        }

                        return;
                    }

                    if (Misc.is(split[1], new String[]{ 
                        "help", "?", "create", "-c", "remove", "-r", "set", "-s",
                        "send", "->", "deposit", "-d", "join", "-j", "leave", "-l"
                    })) {
                        getBankHelp(player); return;
                    } else {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.access")) {
                            return;
                        }

                        Player online = iConomy.getBukkitServer().getPlayer(split[1]);

                        if(online != null) {
                            split[1] = online.getName();
                        }

                        showBankAccounts(sender, split[1]);
                    }

                    return;

                case 3:

                    if (Misc.is(split[1], new String[]{ "list", "-l" }) && Constants.BankingMultiple) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.list")) {
                            return;
                        }

                        int page = 0;

                        try { page = Integer.parseInt(split[2]); } catch(NumberFormatException e) { }

                        showBankList(sender, page); return;
                    }

                    if (Misc.is(split[1], new String[]{ "main", "-m" }) && Constants.BankingMultiple) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.main.view")) {
                            return;
                        }

                        Player check = Misc.playerMatch(split[2]);
                        String name = "";

                        if(check != null) {
                            name = check.getName();
                        } else {
                            name = split[2];
                        }

                        if(name == null ? "" != null : !name.equals("")) {
                            Account account = iConomy.getAccount(name);

                            if(account != null) {
                                Bank bank = account.getMainBank();

                                if(bank != null)
                                    showBankAccount(sender, bank.getName(), name);
                                else {
                                    Messaging.send(Template.color("error.bank.account.none"));
                                }
                            }
                        } else {
                            Messaging.send("`RPlayer name was empty or invalid.");
                        }

                        return;
                    }

                    if (Misc.is(split[1], new String[]{ "create", "-c" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.bank.create")) {
                            return;
                        }

                        createBank(sender, split[2]); 
                        return;
                    }

                    if (Misc.is(split[1], new String[]{ "join", "-j" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.join") && isPlayer) {
                            return;
                        }

                        createBankAccount(sender, split[2], player.getName());
                        return;
                    }

                    if (Misc.is(split[1], new String[]{ "leave", "-l" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.leave") && isPlayer) {
                            return;
                        }

                        removeBankAccount(sender, split[2], player.getName());
                        return;
                    }

                    if (Misc.is(split[1], new String[]{ "deposit", "-d" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.deposit") && isPlayer) {
                            return;
                        }

                        Double amount = 0.0;
                        String name = player.getName();
                        String bank = iConomy.getAccount(name).getMainBankAccount().getBankName();

                        if(bank == null || bank.isEmpty()) {
                            Messaging.send(Template.color("error.bank.account.none"));
                            return;
                        }

                        try {
                            amount = Double.parseDouble(split[2]);

                            if (amount < 0.01) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            Messaging.send("`rInvalid amount` `f" + amount);
                            Messaging.send("`rUsage: `w/bank `r[`wbank-name`r] `Rdeposit `r[`wamount`r]");

                            return;
                        }

                        showBankDeposit(sender, bank, name, amount);
                        return;
                    }

                    if (Misc.is(split[1], new String[]{ "withdraw", "-w" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.withdraw") && isPlayer) {
                            return;
                        }

                        Double amount = 0.0;
                        String name = player.getName();
                        String bank = iConomy.getAccount(name).getMainBankAccount().getBankName();

                        if(bank == null || bank.isEmpty()) {
                            Messaging.send(Template.color("error.bank.account.none"));
                            return;
                        }

                        try {
                            amount = Double.parseDouble(split[2]);

                            if (amount < 0.01) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            Messaging.send("`rInvalid amount` `f" + amount);
                            Messaging.send("`rUsage: `w/bank `r[`wbank-name`r] `Rdeposit `r[`wamount`r]");

                            return;
                        }

                        showBankWithdrawal(sender, bank, name, amount);
                        return;
                    }

                    return;

                case 4:
                    if (Misc.is(split[1], new String[]{ "send", "->" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.transfer") || !isPlayer) {
                            return;
                        }

                        String name = "";
                        double amount = 0.0;

                        if (iConomy.hasAccount(split[2])) {
                            name = split[2];
                        } else {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{split[2]})); return;
                        }

                        try {
                            amount = Double.parseDouble(split[3]);

                            if (amount < 0.01) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            Messaging.send("&cInvalid amount: &f" + amount);
                            Messaging.send("&cUsage: `w/bank `Rsend `r[`waccount`r] `r[`wamount`r]"); return;
                        }

                        showBankTransaction(sender, player.getName(), name, amount);
                        return;
                    }

                    if (Misc.is(split[2], new String[]{ "deposit", "-d" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.deposit") && isPlayer) {
                            return;
                        }

                        Double amount = 0.0;
                        String bank = split[1];
                        String name = player.getName();

                        try {
                            amount = Double.parseDouble(split[3]);

                            if (amount < 0.01) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            Messaging.send("`rInvalid amount` `f" + amount);
                            Messaging.send("`rUsage: `w/bank `r[`wbank-name`r] `Rdeposit `r[`wamount`r]");

                            return;
                        }

                        showBankDeposit(sender, bank, name, amount);
                        return;
                    }

                    if (Misc.is(split[2], new String[]{ "withdraw", "-w" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.withdraw") && isPlayer) {
                            return;
                        }

                        Double amount = 0.0;
                        String bank = split[1];
                        String name = player.getName();

                        try {
                            amount = Double.parseDouble(split[3]);

                            if (amount < 0.01) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            Messaging.send("`rInvalid amount` `f" + amount);
                            Messaging.send("`rUsage: `w/bank `r[`wbank-name`r] `Rdeposit `r[`wamount`r]");

                            return;
                        }

                        showBankWithdrawal(sender, bank, name, amount);
                        return;
                    }

                    if (Misc.is(split[1], new String[]{ "main", "-m" }) && Constants.BankingMultiple) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.main.change") && isPlayer) {
                            return;
                        }

                        if(Misc.is(split[2], new String[]{ "set" })) {
                            String name = player.getName();

                            Account account = iConomy.getAccount(player.getName());
                            Bank bank = iConomy.getBank(split[3]);

                            if(bank == null) {
                                Messaging.send(Template.parse("error.bank.doesnt", new String[] { "+bank,+name,+b,+n" }, new String[] { split[3] }));
                                return;
                            }

                            if(!bank.hasAccount(name)) {
                                Messaging.send(Template.parse("error.bank.account.doesnt", new String[] { "+name,+n" }, new String[] { name }));
                                return;
                            }

                            account.setMainBank(bank.getId());
                            Messaging.send(Template.parse("personal.bank.change", new String[] { "+bank,+name,+b,+n" }, new String[] { split[3] }));
                        } else {
                            Messaging.send("`RInvalid key given possible keys:");
                            Messaging.send("`r  set");
                        }

                        return;
                    }

                case 5:
                    if (Misc.is(split[2], new String[]{ "set", "-s" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.admin.set") && isPlayer) {
                            return;
                        }

                        if(Misc.is(split[3], new String[]{ "name", "initial", "major", "minor", "fee" })) {
                            setBankValue(sender, split[1], split[3].toLowerCase(), split[4]);
                        } else {
                            Messaging.send("`RInvalid key given possible keys:");
                            Messaging.send("`r  name`R, `rinitial`R, `rmajor`R, `rminor`R, `rfee");
                        }

                        return;
                    }
                    
                    if (Misc.is(split[2], new String[]{"send", "->"})) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.transfer.multiple") || !isPlayer) {
                            return;
                        }

                        String name = "";
                        String bank = split[1];
                        String tBank = "";
                        double amount = 0.0;

                        if (iConomy.hasAccount(split[3])) {
                            name = split[3];
                            tBank = iConomy.getAccount(name).getMainBankAccount().getBankName();
                        } else {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{split[3]}));
                            return;
                        }

                        try {
                            amount = Double.parseDouble(split[4]);

                            if (amount < 0.01) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            Messaging.send("&cInvalid amount: &f" + amount);
                            Messaging.send("&cUsage: `w/bank`r[`wfrom-bank`r] `Rsend `r[`wto-account`r] `r[`wamount`r]");
                            return;
                        }

                        showBankTransfer(sender, player.getName(), bank, tBank, name, amount);
                        return;
                    }

                case 6:
                    if (Misc.is(split[2], new String[]{"send", "->"})) {
                        if (!iConomy.hasPermissions(sender, "iConomy.bank.transfer.multiple") || !isPlayer) {
                            return;
                        }

                        String name = "";
                        String bank = split[1];
                        String tBank = split[3];
                        name = split[4];
                        double amount = 0.0;

                        try {
                            amount = Double.parseDouble(split[5]);

                            if (amount < 0.01) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            Messaging.send("&cInvalid amount: &f" + amount);
                            Messaging.send("&cUsage: `w/bank`r[`wfrom-bank`r] `Rsend `r[`wto-bank`r] `r[`wto-account`r] `r[`wamount`r]");
                            return;
                        }

                        showBankTransfer(sender, player.getName(), bank, tBank, name, amount);
                        return;
                    }
            }
        }

        if (split[0].equalsIgnoreCase("money")) {
            switch (split.length) {
                case 1:
                    if(isPlayer)
                        showBalance(player.getName(), player, true);
                    else {
                        Messaging.send("`RCannot show balance without organism.");
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

                    if (Misc.is(split[1], new String[]{ "empty", "-e" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.empty")) {
                            return;
                        }

                        iConomy.Accounts.emptyDatabase();

                        Messaging.send(Template.color("accounts.empty"));
                        return;
                    }

                    if (Misc.is(split[1], new String[]{ "purge", "-pf" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.purge")) {
                            return;
                        }

                        iConomy.Accounts.purge();
                        Messaging.send(Template.color("accounts.purge"));
                        return;
                    }

                    if (Misc.is(split[1], new String[]{ "stats", "-s" })) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.stats")) {
                            return;
                        }

                        Collection<Double> accountHoldings = iConomy.Accounts.values();
                        Collection<Double> bankHoldings = null;
                        Collection<Double> totalHoldings = accountHoldings;

                        double TCOH = 0;
                        int accounts = accountHoldings.size();
                        int bankAccounts = 0;
                        int totalAccounts = accounts;

                        if(Constants.Banking) {
                            bankHoldings = iConomy.Banks.values();
                            bankAccounts = (bankHoldings != null) ? bankHoldings.size() : 0;
                            
                            if(bankHoldings != null) {
                                totalHoldings.addAll(bankHoldings);
                                totalAccounts += bankAccounts;
                            }
                        }

                        for (Object o : totalHoldings.toArray()) {
                            TCOH += (Double)o;
                        }

                        Messaging.send(Template.color("statistics.opening"));

                        Messaging.send(Template.parse("statistics.total",
                                new String[]{ "+currency,+c", "+amount,+money,+a,+m" },
                                new Object[]{ Constants.Major.get(1), iConomy.format(TCOH) }
                        ));

                        Messaging.send(Template.parse("statistics.average",
                                new String[]{ "+currency,+c", "+amount,+money,+a,+m" },
                                new Object[]{ Constants.Major.get(1), iConomy.format(TCOH / totalAccounts) }
                        ));

                        Messaging.send(Template.parse("statistics.accounts",
                                new String[]{ "+currency,+c", "+amount,+accounts,+a" },
                                new Object[]{ Constants.Major.get(1), accounts }
                        ));

                        if(Constants.Banking) {
                            Messaging.send(Template.parse("statistics.bank-accounts",
                                    new String[]{ "+currency,+c", "+amount,+accounts,+a" },
                                    new Object[]{ Constants.Major.get(1), bankAccounts }
                            ));
                        }

                        return;
                    }

                    if (Misc.is(split[1], new String[]{
                        "help", "?", "grant", "-g", "reset", "-x",
                        "set", "-s", "pay", "-p", "create", "-c",
                        "remove", "-v", "hide", "-h" })) {
                        getMoneyHelp(player); return;
                    } else {
                        if (!iConomy.hasPermissions(sender, "iConomy.access")) {
                            return;
                        }

                        Player online = iConomy.getBukkitServer().getPlayer(split[1]);

                        if(online != null) {
                            split[1] = online.getName();
                        }

                        if (iConomy.hasAccount(split[1])) {
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

                        if (iConomy.hasAccount(split[2])) {
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
                            int top = Integer.parseInt(split[2]);
                            showTop(sender, top < 0 ? 5 : ((top > 100) ? 100 : top));
                        } catch (Exception e) {
                            showTop(sender, 5);
                        }

                        return;
                    }

                    if (Misc.is(split[1], new String[]{"create", "-c"})) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.account.create")) {
                            return;
                        }

                        if (!iConomy.hasAccount(split[2])) {
                            createAccount(split[2]);
                        } else {
                            Messaging.send(Template.parse("error.exists", new String[]{"+name,+n"}, new String[]{split[2]}));
                        }

                        return;
                    }

                    if (Misc.is(split[1], new String[]{"remove", "-v"})) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.account.remove")) {
                            return;
                        }

                        if (iConomy.hasAccount(split[2])) {
                            removeAccount(split[2]);
                        } else {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{split[2]}));
                        }

                        return;
                    }

                    if (Misc.is(split[1], new String[]{"reset", "-x"})) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.reset")) {
                            return;
                        }

                        if (iConomy.hasAccount(split[2])) {
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

                        if (iConomy.hasAccount(split[2])) {
                            name = split[2];
                        } else {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{split[2]})); return;
                        }

                        try {
                            amount = Double.parseDouble(split[3]);

                            if (amount < 0.01) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            Messaging.send("&cInvalid amount: &f" + amount);
                            Messaging.send("&cUsage: &f/money &c[&f-p&c|&fpay&c] <&fplayer&c> &c<&famount&c>"); return;
                        }

                        showPayment(player.getName(), name, amount);
                        return;
                    }

                    if (Misc.is(split[1], new String[]{"grant", "-g"})) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.grant")) {
                            return;
                        }

                        ArrayList<String> accounts = new ArrayList<String>();
                        boolean console = (isPlayer) ? false : true;
                        double amount = 0.0;

                        if(split[2].startsWith("g:")) {
                            if(iConomy.getPermissions() == null) {
                                Messaging.send(Messaging.colorize("<rose>Sorry, you need permissions to use this feature.")); return;
                            }

                            if(iConomy.getBukkitServer().getOnlinePlayers().length < 1) {
                                Messaging.send(Template.color("error.online")); return;
                            }

                            String group = split[2].substring(2);

                            for(Player p : iConomy.getBukkitServer().getOnlinePlayers()) {
                                if(iConomy.getPermissions().inGroup(p.getWorld().getName(), p.getName(), group)) {
                                    accounts.add(p.getName());
                                }
                            }
                        } else {
                            Player check = Misc.playerMatch(split[2]);
                            String name = "";

                            if(check != null) {
                                name = check.getName();
                            } else {
                                name = split[2];
                            }

                            if (iConomy.hasAccount(name)) {
                                accounts.add(name);
                            } else {
                                Messaging.send(Template.parse("error.account", new String[]{ "+name,+n" }, new String[]{ name })); return;
                            }
                        }

                        try {
                            amount = Double.parseDouble(split[3]);
                        } catch (NumberFormatException e) {
                            Messaging.send("&cInvalid amount: &f" + split[3]);
                            Messaging.send("&cUsage: &f/money &c[&f-g&c|&fgrant&c] <&fplayer&c> (&f-&c)&c<&famount&c>"); return;
                        }

                        if(accounts.size() < 1 || accounts.isEmpty()) {
                            Messaging.send(Template.color("<rose>Grant Query returned 0 accounts to alter.")); return;
                        }

                        for(String name : accounts) {
                            showGrant(name, player, amount, console);
                        }

                        return;
                    }

                    if (Misc.is(split[1], new String[]{"hide", "-h"})) {
                        if (!iConomy.hasPermissions(sender, "iConomy.admin.hide")) {
                            return;
                        }

                        String name = "";
                        Player check = Misc.playerMatch(split[2]);
                        boolean hidden = false;

                        if(check != null) {
                            name = check.getName();
                        } else {
                            name = split[2];
                        }

                        if (!iConomy.hasAccount(name)) {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{ split[2] })); return;
                        }

                        if (Misc.is(split[3], new String[]{"true", "t", "-t", "yes", "da", "-d"})) {
                            hidden = true;
                        }

                        if(!setHidden(name, hidden)) {
                            Messaging.send(Template.parse("error.account", new String[]{ "+name,+n" }, new String[]{ name }));
                        } else {
                            Messaging.send(Template.parse("accounts.status", new String[]{ "+status,+s" }, new String[]{ (hidden) ? "hidden" : "visible" }));
                        }

                        return;
                    }

                    if (Misc.is(split[1], new String[]{"set", "-s"})) {
                        if (!iConomy.hasPermissions(player, "iConomy.admin.set")) {
                            return;
                        }

                        String name = "";
                        double amount = 0.0;

                        Player check = Misc.playerMatch(split[2]);

                        if(check != null) {
                            name = check.getName();
                        } else {
                            name = split[2];
                        }

                        if (!iConomy.hasAccount(name)) {
                            Messaging.send(Template.parse("error.account", new String[]{"+name,+n"}, new String[]{split[2]})); return;
                        }

                        try {
                            amount = Double.parseDouble(split[3]);
                        } catch (NumberFormatException e) {
                            Messaging.send("&cInvalid amount: &f" + split[3]);
                            Messaging.send("&cUsage: &f/money &c[&f-g&c|&fgrant&c] <&fplayer&c> (&f-&c)&c<&famount&c>"); return;
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

            getMoneyHelp(player);
        }

        return;
    }
}
