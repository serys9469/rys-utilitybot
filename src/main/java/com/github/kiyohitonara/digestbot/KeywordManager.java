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
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.squareup.moshi.Moshi;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * gooラボのキーワード抽出API(https://labs.goo.ne.jp/api/jp/keyword-extraction/)を使用した、キーワード抽出機能用クラス
 *
 * @author Kiyohito Nara
 */
public class KeywordManager {
    /**
     * 指定した日時の会話内容からキーワードを抽出し、リプライメッセージを作成します
     *
     * @param token   返信用トークン
     * @param id      抽出の始点ID
     * @param groupId 抽出対象の送信者ID
     * @param date    抽出する日付
     * @return 抽出結果のテキストメッセージ
     * @throws IOException
     * @throws SQLException
     * @throws URISyntaxException
     */
    public ReplyMessage createReply(String token, long id, String groupId, String date) throws IOException, SQLException, URISyntaxException {
        String body = createParameter(id, groupId, date);
        KeywordResponse keywordResponse = getResponse(body);

        ArrayList<CarouselColumn> carouselColumns = new ArrayList<>();
        for (Keyword keyword : keywordResponse.getKeywords()) {
            String word = keyword.getWord();

            ArrayList<String> data = new ArrayList<>();
            data.add(SearchManager.INDEX_ID, "0");
            data.add(SearchManager.INDEX_GROUP_ID, groupId);
            data.add(SearchManager.INDEX_DATE, date);
            data.add(SearchManager.INDEX_WORD, word);

            PostbackEventData postbackEventData = new PostbackEventData();
            postbackEventData.setTarget("@search");
            postbackEventData.setDataList(data);

            ArrayList<Action> actions = new ArrayList<>();
            actions.add(new PostbackAction("検索", postbackEventData.toJson()));

            String history = createHistory(groupId, word);
            carouselColumns.add(new CarouselColumn(null, word, history, actions));
        }

        CarouselTemplate carouselTemplate = new CarouselTemplate(carouselColumns);

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(new TextMessage("キーワードはこの5つの気がします。"));
        messages.add(new TemplateMessage("キーワード", carouselTemplate));

        return new ReplyMessage(token, messages);
    }

    /**
     * APIにPOSTするパラメータを作成します。
     *
     * @param id      抽出の始点ID
     * @param groupId 抽出対象の送信者ID
     * @param date    抽出する日付
     * @return パラメータ(body)
     * @throws SQLException
     * @throws URISyntaxException
     */
    private String createParameter(long id, String groupId, String date) throws SQLException, URISyntaxException {
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.init();

        ArrayList<DatabaseRecord> databaseRecords;
        if (DatabaseManager.isDate(date)) {
            databaseRecords = databaseManager.queryByDate(id, groupId, Date.valueOf(date), 100);
        } else {
            databaseRecords = databaseManager.query(id, groupId, 100);
        }

        databaseManager.destroy();

        StringBuilder stringBuilder = new StringBuilder();
        for (DatabaseRecord databaseRecord : databaseRecords) {
            stringBuilder.append(databaseRecord.getText());
            stringBuilder.append("。");
        }

        return stringBuilder.toString();
    }

    /**
     * キーワードの含まれたメッセージを抽出します。
     *
     * @param groupId 抽出対象の送信者ID
     * @param word    キーワード
     * @return パラメータ(body)
     * @throws SQLException
     * @throws URISyntaxException
     */
    private String createHistory(String groupId, String word) throws SQLException, URISyntaxException {
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.init();

        ArrayList<DatabaseRecord> databaseRecords = databaseManager.queryByWord(0, groupId, word, 1);

        databaseManager.destroy();

        StringBuilder stringBuilder = new StringBuilder();
        for (DatabaseRecord databaseRecord : databaseRecords) {
            String result = databaseRecord.getText();

            if (result.length() < 60) {
                stringBuilder.append(databaseRecord.getText());
            }
        }
        if (stringBuilder.length() == 0) {
            stringBuilder.append("プレビューできません");
        }

        return stringBuilder.toString();
    }

    /**
     * リクエスト先にPOSTし、レスポンスを受け取ります
     *
     * @param body 解析対象本文
     * @return レスポンスパラメータ(JSON)
     * @throws IOException
     */
    private KeywordResponse getResponse(String body) throws IOException {
        Moshi moshi = new Moshi.Builder()
                .add(new KeywordAdapter())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://labs.goo.ne.jp")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();
        KeywordService service = retrofit.create(KeywordService.class);

        HashMap<String, String> request = createRequestParameter("", body);

        return service.getResponse(request).execute().body();
    }

    /**
     * POSTするリクエストパラメータを作成します
     *
     * @param title 解析対象タイトル
     * @param body  解析対象本文
     * @return リクエストパラメータ(JSON)
     */
    private HashMap<String, String> createRequestParameter(String title, String body) {
        String apiKey = System.getenv("GOO_API_KEY");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("app_id", apiKey);
        hashMap.put("title", title);
        hashMap.put("body", body);
        hashMap.put("max_num", String.valueOf(5));
        hashMap.put("focus", "PSN");

        return hashMap;
    }
}
