/*
 * Copyright 2016 Kiyohito Nara
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

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;

/**
 * PostgreSQL操作用クラス
 *
 * @author Kiyohito Nara
 */
public class DatabaseManager {
    private Connection mConnection;

    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_GROUP_ID = "group_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TEXT = "text";

    /**
     * データベースと接続します
     * インスタンス生成後に必ず呼んでください
     *
     * @throws URISyntaxException
     * @throws SQLException
     */
    public void init() throws URISyntaxException, SQLException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

        mConnection = DriverManager.getConnection(dbUrl + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", username, password);
    }

    /**
     * データベースにデータを挿入します
     *
     * @param groupId メッセージの送信者ID
     * @param text    メッセージの内容
     * @throws SQLException
     */
    public void insert(String groupId, String text) throws SQLException {
        PreparedStatement preparedStatement = mConnection.prepareStatement("INSERT INTO messages (group_id, date, text) VALUES (?, now(), ?)");
        preparedStatement.setString(1, groupId);
        preparedStatement.setString(2, text);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    /**
     * データベースに保存されているメッセージを全件取得します
     *
     * @param id      メッセージのID
     * @param groupId メッセージの送信者ID
     * @param limit   取得件数
     * @throws SQLException
     */
    public ArrayList<DatabaseRecord> query(long id, String groupId, int limit) throws SQLException {
        PreparedStatement preparedStatement = mConnection.prepareStatement("SELECT * FROM messages WHERE _id >= ? AND group_id = ? LIMIT ?");
        preparedStatement.setLong(1, id);
        preparedStatement.setString(2, groupId);
        preparedStatement.setInt(3, limit);
        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<DatabaseRecord> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            arrayList.add(new DatabaseRecord(resultSet));
        }

        resultSet.close();
        preparedStatement.close();

        return arrayList;
    }

    /**
     * 指定した単語が含まれているメッセージを検索します
     *
     * @param id      メッセージのID
     * @param groupId メッセージの送信者ID
     * @param limit   取得件数
     * @return 指定した単語が含まれているメッセージ
     * @throws SQLException
     */
    public ArrayList<DatabaseRecord> queryOfPreviousById(long id, String groupId, int limit) throws SQLException {
        PreparedStatement preparedStatement = mConnection.prepareStatement("SELECT * FROM messages WHERE _id < ? AND group_id = ? ORDER BY _id DESC LIMIT ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        preparedStatement.setLong(1, id);
        preparedStatement.setString(2, groupId);
        preparedStatement.setInt(3, limit);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.afterLast();

        ArrayList<DatabaseRecord> arrayList = new ArrayList<>();
        while (resultSet.previous()) {
            arrayList.add(new DatabaseRecord(resultSet));
        }

        resultSet.close();
        preparedStatement.close();

        return arrayList;
    }

    /**
     * 指定した単語が含まれているメッセージを検索します
     *
     * @param id      メッセージのID
     * @param groupId メッセージの送信者ID
     * @param limit   取得件数
     * @return 指定した単語が含まれているメッセージ
     * @throws SQLException
     */
    public ArrayList<DatabaseRecord> queryOfAfterById(long id, String groupId, int limit) throws SQLException {
        PreparedStatement preparedStatement = mConnection.prepareStatement("SELECT * FROM messages WHERE _id > ? AND group_id = ? ORDER BY _id ASC LIMIT ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        preparedStatement.setLong(1, id);
        preparedStatement.setString(2, groupId);
        preparedStatement.setInt(3, limit);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.beforeFirst();

        ArrayList<DatabaseRecord> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            arrayList.add(new DatabaseRecord(resultSet));
        }

        resultSet.close();
        preparedStatement.close();

        return arrayList;
    }

    /**
     * 指定した単語が含まれているメッセージを検索します
     *
     * @param id      メッセージのID
     * @param groupId メッセージの送信者ID
     * @param word    検索する単語
     * @param limit   取得件数
     * @return 指定した単語が含まれているメッセージ
     * @throws SQLException
     */
    public ArrayList<DatabaseRecord> queryByWord(long id, String groupId, String word, int limit) throws SQLException {
        PreparedStatement preparedStatement = mConnection.prepareStatement("SELECT * FROM messages WHERE _id >= ? AND group_id = ? AND text LIKE ? LIMIT ?");
        preparedStatement.setLong(1, id);
        preparedStatement.setString(2, groupId);
        preparedStatement.setString(3, "%" + word + "%");
        preparedStatement.setInt(4, limit);
        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<DatabaseRecord> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            arrayList.add(new DatabaseRecord(resultSet));
        }

        resultSet.close();
        preparedStatement.close();

        return arrayList;
    }

    /**
     * 指定した日付に送信されたメッセージを検索します
     *
     * @param id      メッセージのID
     * @param groupId メッセージの送信者ID
     * @param date    検索する日時
     * @param limit   取得件数
     * @return 指定した日付に送信されたメッセージ
     * @throws SQLException
     */
    public ArrayList<DatabaseRecord> queryByDate(long id, String groupId, Date date, int limit) throws SQLException {
        PreparedStatement preparedStatement = mConnection.prepareStatement("SELECT * FROM messages WHERE _id >= ? AND group_id = ? AND date = ? LIMIT ?");
        preparedStatement.setLong(1, id);
        preparedStatement.setString(2, groupId);
        preparedStatement.setDate(3, date);
        preparedStatement.setInt(4, limit);
        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<DatabaseRecord> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            arrayList.add(new DatabaseRecord(resultSet));
        }

        return arrayList;
    }

    /**
     * 指定した日付に送信されたメッセージを検索します
     *
     * @param id      メッセージのID
     * @param groupId メッセージの送信者ID
     * @param date    検索する日時
     * @param limit   取得件数
     * @return 指定した日付に送信されたメッセージ
     * @throws SQLException
     */
    public ArrayList<DatabaseRecord> queryByDateAndWord(long id, String groupId, Date date, String word, int limit) throws SQLException {
        PreparedStatement preparedStatement = mConnection.prepareStatement("SELECT * FROM messages WHERE _id >= ? AND group_id = ? AND date = ? AND text LIKE ? LIMIT ?");
        preparedStatement.setLong(1, id);
        preparedStatement.setString(2, groupId);
        preparedStatement.setDate(3, date);
        preparedStatement.setString(4, "%" + word + "%");
        preparedStatement.setInt(5, limit);
        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<DatabaseRecord> arrayList = new ArrayList<>();
        while (resultSet.next()) {
            arrayList.add(new DatabaseRecord(resultSet));
        }

        return arrayList;
    }

    /**
     * データベースに保存されているメッセージをログに出力します
     *
     * @param groupId メッセージの送信者ID
     * @param limit   出力件数
     * @throws SQLException
     */
    public void printLog(String groupId, int limit) throws SQLException {
        PreparedStatement preparedStatement = mConnection.prepareStatement("SELECT * FROM messages WHERE group_id = ? ORDER BY _id ASC LIMIT ?");
        preparedStatement.setString(1, groupId);
        preparedStatement.setInt(2, limit);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            System.out.println(resultSet.getLong(COLUMN_ID) + ", " + resultSet.getString(COLUMN_GROUP_ID) + ", " + resultSet.getDate(COLUMN_DATE).toString() + ", " + resultSet.getString(COLUMN_TEXT));
        }

        resultSet.close();
        preparedStatement.close();
    }

    /**
     * データベースを切断します
     * インスタンス破棄時に必ず呼んでください
     *
     * @throws SQLException
     */
    public void destroy() throws SQLException {
        mConnection.close();
    }

    public static String trimAnnotation(String annotation, String s) {
        int length = annotation.length();

        return s.substring(length).trim();
    }

    public static boolean isDate(String s) {
        try {
            Date.valueOf(s);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isDatabaseRecord(String s) {
        try {
            DatabaseRecord.valueOfDatabaseRecord(s);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
