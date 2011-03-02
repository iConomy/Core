package com.nijiko.coelho.iConomy.util;

import java.io.File;

import org.bukkit.util.config.Configuration;

public class Template {
	private Configuration tpl = null;
	
    public Template(String directory, String filename) {
        this.tpl = new Configuration(new File(directory, filename));
        this.tpl.load();
    }

    /**
     * Grab the raw template line by the key, and don't save anything.
     *
     * @param key The template key we wish to grab.
     *
     * @return <code>String</code> - Template line / string.
     */
    
    public String raw(String key) {
        return this.tpl.getString(key);
    }

    /**
     * Grab the raw template line and save data if no key existed.
     *
     * @param key The template key we are searching for.
     * @param line The line to be placed if no key was found.
     * 
     * @return
     */
    
    public String raw(String key, String line) {
        return this.tpl.getString(key, line);
    }

    public void save(String key, String line) {
    	this.tpl.setProperty(key, line);
    }
    
    public String color(String key) {
        return Messaging.parse(Messaging.colorize(this.raw(key)));
    }

    public String parse(String key, Object[] argument, Object[] points) {
        return Messaging.parse(Messaging.colorize(Messaging.argument(this.raw(key), argument, points)));
    }

    public String parse(String key, String line, Object[] argument, Object[] points) {
        return Messaging.parse(Messaging.colorize(Messaging.argument(this.raw(key, line), argument, points)));
    }
}
