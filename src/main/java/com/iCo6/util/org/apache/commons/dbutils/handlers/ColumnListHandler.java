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

/*
 * <code>ResultSetHandler</code> implementation that converts one
 * <code>ResultSet</code> column into a <code>List</code> of
 * <code>Object</code>s. This class is thread safe.
 * 
 * @see org.apache.commons.dbutils.ResultSetHandler
 * @since DbUtils 1.1
 */
public class ColumnListHandler extends AbstractListHandler<Object> {

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
     * Creates a new instance of ColumnListHandler.  The first column of each
     * row will be returned from <code>handle()</code>.
     */
    public ColumnListHandler() {
        this(1, null);
    }

    /* 
     * Creates a new instance of ColumnListHandler.
     * 
     * @param columnIndex The index of the column to retrieve from the 
     * <code>ResultSet</code>.
     */
    public ColumnListHandler(int columnIndex) {
        this(columnIndex, null);
    }

    /* 
     * Creates a new instance of ColumnListHandler.
     * 
     * @param columnName The name of the column to retrieve from the 
     * <code>ResultSet</code>.
     */
    public ColumnListHandler(String columnName) {
        this(1, columnName);
    }

    /* Private Helper
     * @param columnIndex The index of the column to retrieve from the 
     * <code>ResultSet</code>.
     * @param columnName The name of the column to retrieve from the 
     * <code>ResultSet</code>.
     */
    private ColumnListHandler(int columnIndex, String columnName) {
        super();
        this.columnIndex = columnIndex;
        this.columnName = columnName;        
    }
    
    /*
     * Returns one <code>ResultSet</code> column value as <code>Object</code>.
     * @param rs <code>ResultSet</code> to process.
     * @return <code>Object</code>, never <code>null</code>.
     * 
     * @throws SQLException if a database access error occurs
     * 
     * @see org.apache.commons.dbutils.handlers.AbstractListHandler#handle(ResultSet)
     */
   protected Object handleRow(ResultSet rs) throws SQLException {
        if (this.columnName == null) {
            return rs.getObject(this.columnIndex);
        } else {
            return rs.getObject(this.columnName);
        }
   }

}
