
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

import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;

import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchManager {
    public static final int INDEX_ID = 0;
    public static final int INDEX_GROUP_ID = 1;
    public static final int INDEX_DATE = 2;
    public static final int INDEX_WORD = 3;

    /**
     * 指定した単語が含まれているメッセージを抽出し、リプライメッセージを作成します
     *
     * @param token   返信用トークン
     * @param id      検索の始点ID
     * @param groupId 検索対象の送信者ID
     * @param word    検索する単語
     * @param date    検索する日付
     * @return 検索結果のテキストメッセージ
     * @throws SQLException
     * @throws URISyntaxException
     */
    public ReplyMessage createReply(String token, long id, String groupId, String word, String date) throws SQLException, URISyntaxException {
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.init();

        ArrayList<DatabaseRecord> databaseRecords;
        if (DatabaseManager.isDate(date)) {
            databaseRecords = databaseManager.queryByDateAndWord(id, groupId, Date.valueOf(date), word, 5);
        } else {
            databaseRecords = databaseManager.queryByWord(id, groupId, word, 5);
        }

        ArrayList<Message> messages = new ArrayList<>();
        if (databaseRecords.size() > 0) {
            if (word.length() > 0) {
                messages.add(new TextMessage(word + "で検索しました。会話を再現します。"));
            } else {
                messages.add(new TextMessage("会話を再現します。"));
            }

            for (DatabaseRecord databaseRecord : databaseRecords) {
                if (messages.size() == 4) {
                    ArrayList<String> data = new ArrayList<>();
                    data.add(SearchManager.INDEX_ID, "" + databaseRecord.getId());
                    data.add(SearchManager.INDEX_GROUP_ID, groupId);
                    data.add(SearchManager.INDEX_DATE, date);
                    data.add(SearchManager.INDEX_WORD, word);

                    PostbackEventData postbackEventData = new PostbackEventData();
                    postbackEventData.setTarget("@search");
                    postbackEventData.setDataList(data);

                    ArrayList<Action> actions = new ArrayList<>();
                    actions.add(new PostbackAction("次", postbackEventData.toJson()));

                    ButtonsTemplate buttonsTemplate = new ButtonsTemplate(null, null, "さらに検索結果があります。", actions);
                    messages.add(new TemplateMessage("検索結果", buttonsTemplate));

                    break;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (DatabaseRecord previousDatabaseRecord : databaseManager.queryOfPreviousById(databaseRecord.getId(), groupId, 2)) {
                        stringBuilder.append( previousDatabaseRecord.getDate() + "\n---------------------\n" + previousDatabaseRecord.getText() + "\n" + "---------------------\n");
                    }
                    stringBuilder.append( databaseRecord.getText() + "\n" + "---------------------\n");
                    for (DatabaseRecord afterDatabaseRecord : databaseManager.queryOfAfterById(databaseRecord.getId(), groupId, 2)) {
                        stringBuilder.append( afterDatabaseRecord.getText() + "\n" + "---------------------");
                    }

                    messages.add(new TextMessage(stringBuilder.toString()));
                }
            }
        } else {
            messages.add(new TextMessage("検索結果なし"));
        }

        databaseManager.destroy();

        return new ReplyMessage(token, messages);
    }
}
