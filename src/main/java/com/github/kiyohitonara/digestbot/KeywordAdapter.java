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

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.HashMap;
import java.util.Map;

/**
 * キーワード抽出機能用JSONコンバーター
 * レスポンスパラメータのkeywordsが可変パラメータ名の要素の配列を持っているため、それをKeywordクラスに変換しています。
 *
 * @author Kiyohito Nara
 */
public class KeywordAdapter {
    @ToJson
    public Map<String, Object> toJson(Keyword keyword) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(keyword.getWord(), keyword.getScore());

        return hashMap;
    }

    @FromJson
    public Keyword fromJson(Map<String, Object> json) {
        Keyword keyword = new Keyword();

        for (String key : json.keySet()) {
            keyword.setWord(key);
            keyword.setScore(Double.parseDouble(json.get(key).toString()));
        }

        return keyword;
    }
}
