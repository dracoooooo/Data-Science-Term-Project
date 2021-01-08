/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.servicemix.kernel.main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Statements {

    private String lockTableName = "SERVICEMIX_LOCK";
    private String clusterName = "smx4";
    private String lockCreateStatement;
    private String lockPopulateStatement;
    private String lockUpdateStatement;

    public Statements(String tableName, String clusterName) {
        this.lockTableName = tableName; 
        this.clusterName = clusterName;
        this.lockCreateStatement="create table " + lockTableName + " (TIME bigint, CLUSTER varchar(20))";
        this.lockPopulateStatement="insert into " + lockTableName + " (TIME, CLUSTER) values (1, '" + clusterName + "')";
    }

    public String testLockTableStatus() {
        String test = "SELECT * FROM " + lockTableName + " FOR UPDATE";
        return test;
    }

    public String getLockUpdateStatement(long timeStamp) {
        lockUpdateStatement = "UPDATE " + lockTableName + 
                              " SET TIME=" + timeStamp + 
                              " WHERE CLUSTER = '" + clusterName + "'";
        return lockUpdateStatement;
    }

    /**
     * init - initialize db
     */
    public void init (Connection lockConnection) {
        Statement s = null;
        try {
            // Check to see if the table already exists. If it does, then don't
            // log warnings during startup.
            // Need to run the scripts anyways since they may contain ALTER
            // statements that upgrade a previous version
            // of the table
            boolean alreadyExists = false;
            ResultSet rs = null;
            try {
                rs = lockConnection.getMetaData().getTables(null, null, lockTableName, new String[] {"TABLE"});
                alreadyExists = rs.next();
            } catch (Throwable ignore) {
                System.err.println(ignore);
            } finally {
                close(rs);
            }
            if (alreadyExists) {
                return;
            }
            s = lockConnection.createStatement();
            String[] createStatments = {lockCreateStatement, lockPopulateStatement};
            for (int i = 0; i < createStatments.length; i++) {
                // This will fail usually since the tables will be
                // created already.
                try {
                    s.execute(createStatments[i]);
                } catch (SQLException e) {
                    System.err.println("Could not create JDBC tables; they could already exist."
                                 + " Failure was: " + createStatments[i] + " Message: " + e.getMessage()
                                 + " SQLState: " + e.getSQLState() + " Vendor code: " + e.getErrorCode());
                }
            }
            lockConnection.commit();
        } catch (Exception ignore) {
            System.err.println(ignore);
        } finally {
            try {
                s.close();
            } catch (Throwable e) {
                // ignore
            }
        }
    }

    private static void close(ResultSet rs) {
        try {
            rs.close();
        } catch (Throwable e) {
            // ignore
        }
    }

}
