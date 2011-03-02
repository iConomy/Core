package com.nijiko.coelho.iConomy.system;

import java.sql.ResultSet;
import java.text.DecimalFormat;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.util.Constants;

public class Account {
	
	private String name;
	private double balance;

	public Account(String name, double balance) {
		this.name = name;
		this.balance = balance;
	}

	public String getName() {
		return name;
	}

	public double getBalance() {
		return this.balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void resetBalance() {
		this.setBalance(Constants.Initial_Balance);
		this.save();
	}

	public boolean hasEnough(double amount) {
		return amount <= this.balance;
	}

	public boolean hasOver(double amount) {
		return amount < this.balance;
	}

	public boolean isNegative() {
		return this.balance < 0.0;
	}

	public void add(double amount) {
		this.balance = this.balance + amount;
	} 

	public void multiply(double amount) {
		this.balance = this.balance * amount;
	} 

	public void divide(double amount) {
		this.balance = this.balance / amount;
	}

	public void subtract(double amount) {
		this.balance = this.balance - amount;
	}

	public void remove() {
		try {
			ResultSet rs = iConomy.getDatabase().resultQuery(
					"SELECT * FROM `" + Constants.SQL_Table + "` WHERE username = ?",
					new Object[]{ this.name }
			);
			if(rs.next()) {
				iConomy.getDatabase().executeQuery(
					"DELETE FROM `" + Constants.SQL_Table + "` WHERE username = ?",
					new Object[]{ this.name }
				);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			ResultSet rs = iConomy.getDatabase().resultQuery(
					"SELECT * FROM `" + Constants.SQL_Table + "` WHERE username = ?",
					new Object[]{ this.name }
			);

			if(!rs.next()) {
				iConomy.getDatabase().executeQuery(
						"INSERT INTO `" + Constants.SQL_Table + "`(username, balance) VALUES (?, ?)",
						new Object[] { this.name, this.balance }
				);
			} else {
				iConomy.getDatabase().executeQuery(
						"UPDATE `" + Constants.SQL_Table + "` SET balance = ? WHERE username = ?",
						new Object[] { this.balance, this.name }
				);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		DecimalFormat formatter = new DecimalFormat("#,##0.##");
		String formatted = formatter.format(this.balance);

		if(formatted.endsWith("."))
			formatted = formatted.substring(0, formatted.length()-1);

		return formatted;
	}
}

