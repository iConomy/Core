package com.nijiko.coelho.iConomy.system;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.util.Constants;

public class Transactions {


    public Transactions() { }

	public void load() throws Exception {
        if (Constants.Log_Data) {
            if (Constants.Database_Type.equalsIgnoreCase("flatfile")) {
                System.out.println("[iConomy] Can't log transactions using flatfile.");
            } else {
                DatabaseMetaData dbm = iConomy.getDatabase().getConnection().getMetaData();
                ResultSet rs = dbm.getTables(null, null, Constants.SQL_Table + "_Transactions", null);

                if(!rs.next()) {
                    System.out.println("[iConomy] Creating logging database.. [" + Constants.SQL_Table + "_Transactions]");

                    if(Constants.Database_Type.equalsIgnoreCase("mysql")) {
                        iConomy.getDatabase().executeQuery("CREATE TABLE " + Constants.SQL_Table + "_Transactions (`id` INT(255) NOT NULL AUTO_INCREMENT, `account_from` TEXT NOT NULL, `account_to` TEXT NOT NULL, `account_from_balance` DECIMAL(65, 2) NOT NULL, `account_to_balance` DECIMAL(65, 2) NOT NULL, `timestamp` TEXT NOT NULL, `set` DECIMAL(65, 2) NOT NULL, `gain` DECIMAL(65, 2) NOT NULL, `loss` DECIMAL(65, 2) NOT NULL, PRIMARY KEY (`id`))");
                    } else if(Constants.Database_Type.equalsIgnoreCase("sqlite")) {
                        iConomy.getDatabase().executeQuery("CREATE TABLE '" + Constants.SQL_Table + "_Transactions' ('id' INT ( 255 ) PRIMARY KEY , 'account_from' TEXT , 'account_to' TEXT , 'account_from_balance' DECIMAL ( 65 , 2 ), 'account_to_balance' DECIMAL ( 65 , 2 ), 'timestamp' TEXT , 'set' DECIMAL ( 65 , 2 ), 'gain' DECIMAL ( 65 , 2 ), 'loss' DECIMAL ( 65 , 2 ));");
                    }

                    System.out.println("[iConomy] Database Created.");
                }

                System.out.println("[iConomy] Logging enabled.");
            }
        } else {
            System.out.println("[iConomy] Logging is currently disabled.");
        }
	}

    /**
     * Inserts data into transaction without using seperate methods, direct method.
     *
     * @param from
     * @param to
     * @param gain
     * @param loss
     */
    public void insert(String from, String to, double from_balance, double to_balance, double set, double gain, double loss) {
        if(!Constants.Log_Data) {
            return;
        }

        long timestamp = System.currentTimeMillis()/1000;

        iConomy.getDatabase().executeQuery(
            "INSERT INTO `" + Constants.SQL_Table + "_Transactions`(account_from, account_to, account_from_balance, account_to_balance, `timestamp`, `set`, gain, loss) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            new Object[] { from, to, from_balance, to_balance, timestamp, set, gain, loss }
        );
    }
}
