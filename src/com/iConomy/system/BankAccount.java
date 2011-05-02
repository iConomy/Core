/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iConomy.system;

import com.iConomy.iConomy;
import com.iConomy.util.Constants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Nijikokun
 */
public class BankAccount {
    private String BankName;
    private int BankId;
    private String AccountName;

    public BankAccount(String BankName, int BankId, String AccountName) {
        this.BankName = BankName;
        this.BankId = BankId;
        this.AccountName = AccountName;
    }

    public String getBankName() {
        return this.BankName;
    }

    public int getBankId() {
        return this.BankId;
    }

    public void getAccountName(String AccountName) {
        this.AccountName = AccountName;
    }

    public Holdings getHoldings() {
        return new Holdings(this.BankId, this.AccountName, true);
    }

    public void remove() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            conn = iConomy.getiCoDatabase().getConnection();
            ps = conn.prepareStatement("DELETE FROM " + Constants.SQLTable + "_BankRelations WHERE bank_id = ? AND account_name = ?");
            ps.setInt(1, BankId);
            ps.setString(2, AccountName);
            ps.executeUpdate();
        } catch (Exception e) {
            return;
        } finally {
            if(ps != null)
                try { ps.close(); } catch (SQLException ex) { }

            if(conn != null)
                try { conn.close(); } catch (SQLException ex) { }
        }

        return;
    }
}