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

import com.squareup.moshi.Json;

import java.util.List;

/**
 * キーワード抽出機能用レスポンスモデルクラス
 *
 * @author Kiyohito Nara
 */
public class KeywordResponse {
    @Json(name = "request_id")
    private String requestId;
    private List<Keyword> keywords = null;

    public KeywordResponse(String requestId, List<Keyword> keywords) {
        this.requestId = requestId;
        this.keywords = keywords;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }
}
