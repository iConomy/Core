package com.iCo6.IO.mini;

import com.iCo6.IO.mini.file.Manager;
import java.io.File;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
    Copyright (c) 2011, Nijiko Yonskai (@nijikokun) <nijikokun@gmail.com>
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

        1. Redistributions of source code must retain the above copyright
            notice, this list of conditions and the following disclaimer.

        2. Redistributions in binary form must reproduce the above copyright
            notice, this list of conditions and the following disclaimer in the
            documentation and/or other materials provided with the distribution.

        3. Neither the name of Nijiko Yonskai nor the
            names of its contributors may be used to endorse or promote products
            derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
    ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
    WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL Nijiko Yonskai BE LIABLE FOR ANY
    DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
    LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
    ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * MiNiDB (MiNimalistic Database)
 *
 * @version 0.2
 * @copyright 2011
 * @author Nijikokun (@nijikokun) <nijikokun@gmail.com>
 */
public class Mini {
    private String folder;
    private String database;
    private String source;
    private boolean changed = false;
    private boolean caseSensitive = false;
    private Manager Database;
    private LinkedHashMap<String, Arguments> Indexes;
    private LinkedHashMap<String, Arguments> pushedIndexes;

    /**
     * Initialize a new MiNiDB, creates database if it does not exist.
     *
     * @param folder Location of database file.
     * @param database Database File Name, any extension can be used.
     */
    public Mini(String folder, String database) {
        this.database = database;
        this.folder = folder;
        this.Database = new Manager(this.folder, this.database, true);
        this.read();
    }
    
    /**
     * Initialize a new Minidb, creates if it does not exist, and also states
     * whether or not the keys are case sensitive.
     * 
     * @param folder
     * @param database
     * @param caseSensitive
     */
    public Mini(String folder, String database, boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        this.database = database;
        this.folder = folder;
        this.Database = new Manager(this.folder, this.database, true);
        this.read();
    }

    /**
     * Create a new minidb using a file variable.
     * Filename is the database name, path is the folder path.
     *
     * @param data
     */
    public Mini(File data) {
        this.database = data.getName();
        this.folder = data.getPath();
        this.Database = new Manager(this.folder, this.database, true);
        this.read();
    }

    /**
     * Create a new minidb using a file variable.
     * Filename is the database name, path is the folder path.
     * Allows to let you set keys case sensitive.
     *
     * @param data
     * @param caseSensitive
     */
    public Mini(File data, boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        this.database = data.getName();
        this.folder = data.getPath();
        this.Database = new Manager(this.folder, this.database, true);
        this.read();
    }

    public static void main(String[] args) {
        Mini mini = new Mini(".", "mini");
        Arguments Row;

        Double amount = mini.getArguments("Nijikokun").getDouble("money");
        mini.setArgument("Nijikokun", "money", (amount + 2), true);

        System.out.println(mini.getArguments("Nijikokun").getDouble("money"));
        System.out.println(mini.getIndices().toString());
    }

    private String[] trim(String[] values) {
        for (int i = 0, length = values.length; i < length; i++)
            if (values[i] != null)
                values[i] = values[i].trim();

        return values;
    }

    private void read() {
        this.read(true);
    }

    private void read(boolean pushed) {
        this.Database = new Manager(this.folder, this.database, true);
        this.Database.removeDuplicates();
        this.Database.read();

        this.Indexes = new LinkedHashMap<String, Arguments>();

        if(pushed)
            this.pushedIndexes = new LinkedHashMap<String, Arguments>();

        for(String line: this.Database.getLines()) {
            if(line.trim().isEmpty())
                continue;

            String[] parsed = trim(line.trim().split(Dict.SPACER));

            if(parsed[0].contains(Dict.ARGUMENT_SPLIT) || parsed[0].isEmpty())
                continue;

            Arguments entry = new Arguments(parseIndice(parsed[0]));
            for(String item: parsed) {
                if(!item.contains(Dict.ARGUMENT_SPLIT)) continue;
                String[] map = trim(item.split(Dict.ARGUMENT_SPLIT, 2));
                String key = map[0], value = map[1];

                if(key == null) 
                    continue;

                entry.setValue(key, value);
            }

            this.Indexes.put(parseIndice(parsed[0]), entry);
        }
    }

    /**
     * Checks to see if the index given exists in the database.
     *
     * @param index (<code>String</code>) Index value or name
     * @return
     */
    public boolean hasIndex(Object index) {
        return this.Indexes.containsKey(parseIndice(index));
    }

    /**
     * Grab the complete list of indices with arguments and all.
     *
     * @return LinkedHashMap<String, Arguments>
     */
    public LinkedHashMap<String, Arguments> getIndices() {
        return this.Indexes;
    }

    /**
     * Creates a new index value-map in the current database or updates a pre-existing index
     * with new Arguments.
     *
     * @param entry Arguments value-mapped entries with Entry index for the initializer.
     */
    public void addIndex(Arguments entry) {
        this.addIndex(entry.getKey(), entry);
    }

    /**
     * Creates a new index value-map in the current database or updates a pre-existing index
     * with new Arguments.
     *
     * @param index Entry index in database, used to grab value-mapped data later on.
     * @param entry Arguments value-mapped entries with Entry index for the initializer.
     */
    public void addIndex(Object index, Arguments entry) {
        this.pushedIndexes.put(parseIndice(index), entry);
        this.changed = true;
    }

    /**
     * Change an Indices without altering the arguments or recursive copying.<br />
     * Updates the database immediately upon alter.
     *
     * @param original (<code>String</code>) Old index value.
     * @param updated (<code>String</code>) New index value.
     * @return boolean - true or false depending on the existence of the original index.
     */
    public boolean alterIndex(Object original, String updated) {
        return this.alterIndex(original, updated, true);
    }

    /**
     * Change an Indices without altering the arguments or recursive copying.<br />
     * Allows the updating of database to be halted until explicitly given.
     *
     * @param original (<code>String</code>) Old index value.
     * @param updated (<code>String</code>) New index value.
     * @param update (<code>Boolean</code>) Update database after alter or wait until update()
     * @return boolean - true or false depending on the existence of the original index.
     */
    public boolean alterIndex(Object original, String updated, boolean update) {
        if(!hasIndex(original) || hasIndex(updated)) return false;

        Arguments data = this.Indexes.get(parseIndice(original));
        this.removeIndex(original);
        this.addIndex(updated, data);

        if(update) this.update();

        return true;
    }

    /**
     * Remove an index from the database, along with it's entries.
     *
     * @param key (<code>String</code>) Index value or name.
     */
    public void removeIndex(Object key) {
        this.Database.remove(this.Indexes.get(parseIndice(key)).toString());
        this.read(false);
    }

    /**
     * Grab value-mapping from an Index.
     *
     * @param key Database line Index used when setting the value-map
     * @return Arguments
     */
    public Arguments getArguments(Object key) {
        return this.Indexes.get(parseIndice(key));
    }

    /**
     * Add / Set a value map to an index, without updating the database.
     *
     * @param index (<code>String</code>) Index value or name
     * @param key (<code>String</code>) Key of the value map
     * @param value (<code>String</code>) Value of the value map
     */
    public void setArgument(String index, Object key, Object value) {
        this.setArgument(index, key, String.valueOf(value), false);
    }

    /**
     * Add / Set a value map to an index.<br /><br />
     *
     * This method allows the passing of the save boolean which will let you
     * determine whether or not to update the database immediately after adding
     * a new value map entry to an index.
     *
     * @param index (<code>String</code>) Index value or name
     * @param key (<code>String</code>) Key of the value map
     * @param value (<code>String</code>) Value of the value map
     * @param save (<code>Boolean</code>) Update database immediately after alter
     */
    public void setArgument(Object index, Object key, String value, boolean save) {
        if(!hasIndex(index)) return;
        this.changed = true;

        Arguments original = this.Indexes.get(parseIndice(index)).copy();
        original.setValue(parseIndice(key), value);

        this.pushedIndexes.put(parseIndice(index), original);
        if(save) this.update();
    }

    public void setArgument(Object index, Object key, Object value, boolean save) {
        String formatted = "";

        if(value instanceof int[]) {
            for(int v: (int[])value)
                formatted = v + ",";
        } else if(value instanceof String[]) {
            for(String v: (String[])value)
                formatted = v + ",";
        } else if(value instanceof Double[]) {
            for(Double v: (Double[])value)
                formatted = v + ",";
        } else if(value instanceof Boolean[]) {
            for(Boolean v: (Boolean[])value)
                formatted = v + ",";
        } else if(value instanceof Long[]) {
            for(Long v: (Long[])value)
                formatted = v + ",";
        } else if(value instanceof Float[]) {
            for(Float v: (Float[])value)
                formatted = v + ",";
        } else if(value instanceof Byte[]) {
            for(Byte v: (Byte[])value)
                formatted = v + ",";
        } else if(value instanceof char[]) {
            for(char v: (char[])value)
                formatted = v + ",";
        } else if(value instanceof ArrayList) {
            ArrayList data = (ArrayList)value;
            for(Object v: data)
                formatted = v + ",";
        }

        if(formatted.length() > 1)
            formatted.substring(0, formatted.length()-2);
        else {
            formatted = String.valueOf(value);
        }

        this.setArgument(parseIndice(index), parseIndice(key), formatted, save);
    }

    /**
     * Update pushes the new indices into the file, while removing the old index attributes
     */
    public void update() {
        if(!this.changed)
            return;

        LinkedList<String> lines = new LinkedList<String>();

        for(String key: this.pushedIndexes.keySet()) {
            if(this.Indexes.containsKey(key)) {
                if(!this.Indexes.get(key).toString().equals(this.pushedIndexes.get(key).toString())) {
                    this.Database.remove(this.Indexes.get(key).toString());
                }
            }
        }

        this.read(false);

        for(String key: this.pushedIndexes.keySet()) {
            if(this.Indexes.containsKey(key)) {
                if(!this.Indexes.get(key).toString().equals(this.pushedIndexes.get(key).toString())) {
                    this.Indexes.put(key, this.pushedIndexes.get(key));
                    this.Database.append(this.Indexes.get(key).toString());
                }
            } else {
                this.Database.append(this.pushedIndexes.get(key).toString());
            }
        }

        this.pushedIndexes.clear();
        this.read();
    }

    private String parseIndice(Object key) {
        if(this.caseSensitive)
            return String.valueOf(key);

        return String.valueOf(key).toLowerCase();
    }
}
