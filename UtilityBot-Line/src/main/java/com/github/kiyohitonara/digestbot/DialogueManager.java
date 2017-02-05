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
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.squareup.moshi.Moshi;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * docomoの雑談対話API(https://dev.smt.docomo.ne.jp/?p=docs.api.page&api_name=dialogue&p_name=api_usage_scenario)を使用した、対話機能用クラス
 *
 * @author Kiyohito Nara
 */
public class DialogueManager {
    /**
     * 受信したメッセージに対する対話を取得し、リプライメッセージを作成します
     *
     * @param token   返信用トークン
     * @param id      メッセージのID(使いません)
     * @param groupId メッセージの送信者ID(使いません)
     * @param message 受信したメッセージ内容
     * @return 対話メッセージ
     * @throws IOException
     */
    public ReplyMessage createReply(String token, int id, String groupId, String message) throws IOException {
        DialogueResponse dialogueResponse = getResponse(message);
        TextMessage textMessage = new TextMessage(dialogueResponse.getUtt());

        return new ReplyMessage(token, textMessage);
    }

    /**
     * リクエスト先にPOSTし、レスポンスを受け取ります
     *
     * @param utt ユーザの発話(255文字以下)
     * @return レスポンスパラメータ(JSON)
     * @throws IOException
     */
    private DialogueResponse getResponse(String utt) throws IOException {
        Moshi moshi = new Moshi.Builder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.apigw.smt.docomo.ne.jp")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();
        DialogueService service = retrofit.create(DialogueService.class);

        HashMap<String, String> request = createRequest(utt);
        String apiKey = System.getenv("DOCOMO_API_KEY");

        return service.getResponse(apiKey, request).execute().body();
    }

    /**
     * POSTするパラメータを作成します
     *
     * @param utt ユーザの発話(255文字以下)
     * @return リクエストパラメータ(JSON)
     */
    private HashMap<String, String> createRequest(String utt) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("utt", utt);
        //hashMap.put("context", "");
        //hashMap.put("nickname", "");
        //hashMap.put("nickname_y", "");
        //hashMap.put("sex", "");
        //hashMap.put("bloodtype", "");
        //hashMap.put("birthdateY", "");
        //hashMap.put("birthdateM", "");
        //hashMap.put("birthdateD", "");
        //hashMap.put("age", "");
        //hashMap.put("constellations", "");
        //hashMap.put("place", "");
        //hashMap.put("mode", "");
        //hashMap.put("t", "10");

        return hashMap;
    }
}
