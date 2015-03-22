package com.iConomy.system;

import com.iConomy.Constants;
import com.iConomy.IO.mini.Arguments;
import com.iConomy.iConomy;
import com.iConomy.util.Common;
import com.iConomy.IO.mini.Mini;

public class Transactions {
    /**
     * Insert a balance transaction change or update.
     *
     * @param data Transaction class
     */
    public static void insert(Transaction data) {
        if (!Constants.Nodes.Logging.getBoolean())
            return;

        if (Common.matches(iConomy.Database.getType().toString(), "inventorydb", "minidb", "orbdb")) {
            Mini database = iConomy.Database.getTransactionDatabase();

            if(database == null)
                return;

            Arguments entry = new Arguments(data.time);
            entry.setValue("where", data.where);
            entry.setValue("from", data.from);
            entry.setValue("to", data.to);
            entry.setValue("from_balance", data.fromBalance);
            entry.setValue("to_balance", data.toBalance);
            entry.setValue("gain", data.gain);
            entry.setValue("loss", data.loss);
            entry.setValue("set", data.set);
            database.addIndex(entry);
            database.update();

            return;
        }
    }
}