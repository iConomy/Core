package com.nijiko.coelho.iConomy.system;

import java.sql.ResultSet;
import java.text.DecimalFormat;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.util.Constants;

public class Account {

    private String name;
    private double balance;
    private boolean altered = false;
    private boolean exists = false;

    public Account(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.exists = true;
    }

    public boolean exists() {
        return this.exists;
    }

    public boolean isAltered() {
        return this.altered;
    }

    private void setExists(boolean exists) {
        this.exists = exists;
    }

    private void setAltered(boolean altered) {
        this.altered = altered;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        if(Constants.Database_Cache) {
            return this.balance;
        }

        try {
            ResultSet rs = iConomy.getDatabase().resultQuery(
                    "SELECT balance FROM `" + Constants.SQL_Table + "` WHERE username = ?",
                    new Object[]{ this.name }
            );
            
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (Exception e) {
            System.out.println("[iConomy] Failed to grab player balance: " + e);
        }

        return 0.0;
    }

    public void setBalance(double balance) {
        if(Constants.Database_Cache) {
            this.balance = balance;
            this.setAltered(true);
        }

        try {
            ResultSet rs = iConomy.getDatabase().resultQuery(
                    "SELECT * FROM `" + Constants.SQL_Table + "` WHERE username = ?",
                    new Object[]{ this.name }
            );

            if (!rs.next()) {
                iConomy.getDatabase().executeQuery(
                        "INSERT INTO `" + Constants.SQL_Table + "`(username, balance) VALUES (?, ?)",
                        new Object[]{ this.name, balance }
                );

                this.setExists(true);
            } else {
                iConomy.getDatabase().executeQuery(
                        "UPDATE `" + Constants.SQL_Table + "` SET balance = ? WHERE username = ?",
                        new Object[]{ balance, this.name }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetBalance() {
        this.setBalance(Constants.Initial_Balance);
        this.setAltered(true);
        this.save();
    }

    public boolean hasEnough(double amount) {
        return amount <= this.getBalance();
    }

    public boolean hasOver(double amount) {
        return amount < this.getBalance();
    }

    public boolean isNegative() {
        return this.getBalance() < 0.0;
    }

    public void add(double amount) {
        if(Constants.Database_Cache) {
            this.balance = this.balance + amount;
            this.setAltered(true);
        }

        this.setBalance(this.getBalance() + amount);
    }

    public void multiply(double amount) {
        if(Constants.Database_Cache) {
            this.balance = this.balance * amount;
            this.setAltered(true);
        }

        this.setBalance(this.getBalance() * amount);
    }

    public void divide(double amount) {
        if(Constants.Database_Cache) {
            this.balance = this.balance / amount;
            this.setAltered(true);
        }

        this.setBalance(this.getBalance() / amount);
    }

    public void subtract(double amount) {
        if(Constants.Database_Cache) {
            this.balance = this.balance - amount;
            this.setAltered(true);
        }

        this.setBalance(this.getBalance() / amount);
    }

    public void remove() {
        try {
            ResultSet rs = iConomy.getDatabase().resultQuery(
                    "SELECT * FROM `" + Constants.SQL_Table + "` WHERE username = ?",
                    new Object[]{ this.name }
            );
            if (rs.next()) {
                iConomy.getDatabase().executeQuery(
                        "DELETE FROM `" + Constants.SQL_Table + "` WHERE username = ?",
                        new Object[]{ this.name }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setExists(false);
    }

    public void save() {
        if(Constants.Database_Cache) {
            try {
                ResultSet rs = iConomy.getDatabase().resultQuery(
                        "SELECT * FROM `" + Constants.SQL_Table + "` WHERE username = ?",
                        new Object[]{ this.name }
                );

                if (!rs.next()) {
                    iConomy.getDatabase().executeQuery(
                            "INSERT INTO `" + Constants.SQL_Table + "`(username, balance) VALUES (?, ?)",
                            new Object[]{ this.name, this.balance }
                    );

                    this.setExists(true);
                } else {
                    iConomy.getDatabase().executeQuery(
                            "UPDATE `" + Constants.SQL_Table + "` SET balance = ? WHERE username = ?",
                            new Object[]{ this.balance, this.name }
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.setAltered(false);
        }
    }

    @Override
    public String toString() {
        DecimalFormat formatter = new DecimalFormat("#,##0.##");
        String formatted = formatter.format(this.getBalance());

        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted;
    }
}
