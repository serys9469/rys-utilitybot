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

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.List;

public class PostbackEventData {
    private String target;
    private List<String> dataList = null;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    public String toJson() {
        Moshi moshi = new Moshi.Builder()
                .build();
        JsonAdapter<PostbackEventData> jsonAdapter = moshi.adapter(PostbackEventData.class);

        return jsonAdapter.toJson(this);
    }

    public static PostbackEventData fromJson(String json) throws IOException {
        Moshi moshi = new Moshi.Builder()
                .build();
        JsonAdapter<PostbackEventData> jsonAdapter = moshi.adapter(PostbackEventData.class);

        return jsonAdapter.fromJson(json);
    }
}
