package com.iCo6.system;

import com.iCo6.Constants;
import java.util.List;

public class Accounts {
    public Accounts() { }

    public boolean exists(String name) {
        return Queried.hasAccount(name);
    }

    public Account get(String name) {
        if(!Queried.hasAccount(name))
            this.create(name);

        return new Account(name);
    }

    public List<Account> getTopAccounts(int amount) {
        return Queried.topAccounts(amount);
    }

    public boolean create(String name) {
        return create(name, Constants.Nodes.Balance.getDouble());
    }

    public boolean create(String name, Double balance) {
        return create(name, balance, 0);
    }

    public boolean create(String name, Double balance, Integer status) {
        if(!Queried.hasAccount(name))
            return Queried.createAccount(name, balance, status);

        return false;
    }

    public boolean remove(String... name) {
        Boolean success = false;

        for(String n: name)
            if(Queried.hasAccount(n))
                success = Queried.removeAccount(n);

        return success;
    }

    public void purge() {
        Queried.purgeDatabase();
    }

    public void empty() {
        Queried.emptyDatabase();
    }
}