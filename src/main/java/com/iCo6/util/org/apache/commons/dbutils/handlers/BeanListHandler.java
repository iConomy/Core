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
import java.util.List;

import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import com.iCo6.util.org.apache.commons.dbutils.RowProcessor;

/*
 * <code>ResultSetHandler</code> implementation that converts a
 * <code>ResultSet</code> into a <code>List</code> of beans. This class is 
 * thread safe.
 * 
 * @see org.apache.commons.dbutils.ResultSetHandler
 */
public class BeanListHandler<T> implements ResultSetHandler<List<T>> {

    /*
     * The Class of beans produced by this handler.
     */
    private final Class<T> type;

    /**
     * The RowProcessor implementation to use when converting rows 
     * into beans.
     */
    private final RowProcessor convert;

    /*
     * Creates a new instance of BeanListHandler.
     * 
     * @param type The Class that objects returned from <code>handle()</code>
     * are created from.
     */
    public BeanListHandler(Class<T> type) {
        this(type, ArrayHandler.ROW_PROCESSOR);
    }

    /*
     * Creates a new instance of BeanListHandler.
     * 
     * @param type The Class that objects returned from <code>handle()</code>
     * are created from.
     * @param convert The <code>RowProcessor</code> implementation 
     * to use when converting rows into beans.
     */
    public BeanListHandler(Class<T> type, RowProcessor convert) {
        this.type = type;
        this.convert = convert;
    }

    /*
     * Convert the whole <code>ResultSet</code> into a List of beans with
     * the <code>Class</code> given in the constructor.
     * 
     * @param rs The <code>ResultSet</code> to handle.
     * 
     * @return A List of beans, never <code>null</code>.
     * 
     * @throws SQLException if a database access error occurs
     * @see org.apache.commons.dbutils.RowProcessor#toBeanList(ResultSet, Class)
     */
    public List<T> handle(ResultSet rs) throws SQLException {
        return this.convert.toBeanList(rs, type);
    }
}
