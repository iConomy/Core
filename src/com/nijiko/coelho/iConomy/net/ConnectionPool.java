/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nijiko.coelho.iConomy.net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Nijikokun
 */
public class ConnectionPool extends DatabasePool<Connection> {

    private String dsn, usr, pwd;

    public ConnectionPool(String driver, String dsn, String usr, String pwd) {
        super();
        
        try {
            Class.forName(driver).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.dsn = dsn;
        this.usr = usr;
        this.pwd = pwd;
    }

    @Override
    protected Connection create() {
        try {
            if(usr.equalsIgnoreCase("") && pwd.equalsIgnoreCase(""))
                return (DriverManager.getConnection(dsn));
            else {
                return (DriverManager.getConnection(dsn, usr, pwd));
            }
        } catch (SQLException e) {
            System.out.println("[iConomy] Could not create connection: " + e);
            return (null);
        }
    }

    @Override
    public void expire(Connection o) {
        try {
            ((Connection) o).close();
        } catch (SQLException e) {
            System.out.println("[iConomy] Could not expire connection: " + e);
        }
    }

    @Override
    public boolean validate(Connection o) {
        try {
            return (!((Connection) o).isClosed());
        } catch (SQLException e) {
            System.out.println("[iConomy] Could not validate connection: " + e);
            return (false);
        }
    }
}
