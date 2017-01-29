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
package com.iCo6.util.org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;

/*
 * <code>ResultSetHandler</code> implementation that converts one
 * <code>ResultSet</code> column into an Object. This class is thread safe.
 * 
 * @see org.apache.commons.dbutils.ResultSetHandler
 */
public class ScalarHandler implements ResultSetHandler<Object> {

    /*
     * The column number to retrieve.
     */
    private final int columnIndex;

    /*
     * The column name to retrieve.  Either columnName or columnIndex
     * will be used but never both.
     */
    private final String columnName;

    /* 
     * Creates a new instance of ScalarHandler.  The first column will
     * be returned from <code>handle()</code>.
     */
    public ScalarHandler() {
        this(1, null);
    }

    /* 
     * Creates a new instance of ScalarHandler.
     * 
     * @param columnIndex The index of the column to retrieve from the 
     * <code>ResultSet</code>.
     */
    public ScalarHandler(int columnIndex) {
        this(columnIndex, null);
    }

    /* 
     * Creates a new instance of ScalarHandler.
     * 
     * @param columnName The name of the column to retrieve from the 
     * <code>ResultSet</code>.
     */
    public ScalarHandler(String columnName) {
        this(1, columnName);
    }

    /* Helper constructor
     * @param columnIndex The index of the column to retrieve from the 
     * <code>ResultSet</code>.
     * @param columnName The name of the column to retrieve from the 
     * <code>ResultSet</code>.
     */
    private ScalarHandler(int columnIndex, String columnName){
        this.columnIndex = columnIndex;
        this.columnName = columnName;        
    }

    /*
     * Returns one <code>ResultSet</code> column as an object via the
     * <code>ResultSet.getObject()</code> method that performs type 
     * conversions.
     * @param rs <code>ResultSet</code> to process.
     * @return The column or <code>null</code> if there are no rows in
     * the <code>ResultSet</code>.
     * 
     * @throws SQLException if a database access error occurs
     * 
     * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet rs) throws SQLException {

        if (rs.next()) {
            if (this.columnName == null) {
                return rs.getObject(this.columnIndex);
            } else {
                return rs.getObject(this.columnName);
            }

        } else {
            return null;
        }
    }
}
