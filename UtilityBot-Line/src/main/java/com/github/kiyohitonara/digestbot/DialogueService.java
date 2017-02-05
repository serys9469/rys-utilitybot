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

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Map;

/**
 * 対話機能用POSTインターフェース
 *
 * @author Kiyohito Nara
 */
public interface DialogueService {
    @POST("/dialogue/v1/dialogue")
    Call<DialogueResponse> getResponse(@Query("APIKEY") String apiKey, @Body Map<String, String> request);
}
