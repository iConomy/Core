/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iCo6.util.org.apache.commons.dbutils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <code>QueryLoader</code> is a registry for sets of queries so 
 * that multiple copies of the same queries aren't loaded into memory.
 * This implementation loads properties files filled with query name to 
 * SQL mappings.  This class is thread safe.
 */
public class QueryLoader {

    /**
     * The Singleton instance of this class.
     */
    private static final QueryLoader instance = new QueryLoader();

    /**
     * Return an instance of this class.
     * @return The Singleton instance.
     */
    public static QueryLoader instance() {
        return instance;
    }

    /**
     * Maps query set names to Maps of their queries.
     */
    private final Map<String,Map<String,String>> queries = new HashMap<String, Map<String,String>>();

    /**
     * QueryLoader constructor.
     */
    protected QueryLoader() {
        super();
    }

    /**
     * Loads a Map of query names to SQL values.  The Maps are cached so a 
     * subsequent request to load queries from the same path will return
     * the cached Map.
     * 
     * @param path The path that the ClassLoader will use to find the file. 
     * This is <strong>not</strong> a file system path.  If you had a jarred
     * Queries.properties file in the com.yourcorp.app.jdbc package you would 
     * pass "/com/yourcorp/app/jdbc/Queries.properties" to this method.
     * @throws IOException if a file access error occurs
     * @throws IllegalArgumentException if the ClassLoader can't find a file at
     * the given path.
     * @return Map of query names to SQL values
     */
    public synchronized Map<String,String> load(String path) throws IOException {

        Map<String,String> queryMap = (Map<String,String>) this.queries.get(path);

        if (queryMap == null) {
            queryMap = this.loadQueries(path);
            this.queries.put(path, queryMap);
        }

        return queryMap;
    }

    /**
     * Loads a set of named queries into a Map object.  This implementation
     * reads a properties file at the given path.
     * @param path The path that the ClassLoader will use to find the file.
     * @throws IOException if a file access error occurs
     * @throws IllegalArgumentException if the ClassLoader can't find a file at
     * the given path.
     * @since DbUtils 1.1
     * @return Map of query names to SQL values
     */
    @SuppressWarnings("unchecked")
    protected Map<String,String> loadQueries(String path) throws IOException {
        // Findbugs flags getClass().getResource as a bad practice; maybe we should change the API?
        InputStream in = getClass().getResourceAsStream(path);

        if (in == null) {
            throw new IllegalArgumentException(path + " not found.");
        }

        Properties props = new Properties();
        props.load(in);

        // Copy to HashMap for better performance
        return new HashMap(props);
    }
    
    /**
     * Removes the queries for the given path from the cache.
     * @param path The path that the queries were loaded from.
     */
    public synchronized void unload(String path){
        this.queries.remove(path);
    }

}
