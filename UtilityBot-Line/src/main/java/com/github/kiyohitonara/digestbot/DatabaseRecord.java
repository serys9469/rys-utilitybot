/*
 * Copyright 2017 Kiyohito Nara
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.kiyohitonara.digestbot;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseRecord {
    private long id;
    private String groupId;
    private Date date;
    private String text;

    public DatabaseRecord() {
    }

    public DatabaseRecord(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getLong(DatabaseManager.COLUMN_ID);
        this.groupId = resultSet.getString(DatabaseManager.COLUMN_GROUP_ID);
        this.date = resultSet.getDate(DatabaseManager.COLUMN_DATE);
        this.text = resultSet.getString(DatabaseManager.COLUMN_TEXT);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        return this.getId() + "/" + this.getGroupId() + "/" + this.getDate().toString() + "/" + this.getText();
    }

    public static DatabaseRecord valueOfDatabaseRecord(String s) throws IllegalArgumentException {
        String[] values = s.split("/", -1);

        DatabaseRecord databaseRecord = new DatabaseRecord();
        databaseRecord.setId(Long.parseLong(values[0]));
        databaseRecord.setGroupId(values[1]);
        databaseRecord.setDate(Date.valueOf(values[2]));
        databaseRecord.setText(values[3]);

        return databaseRecord;
    }
}
