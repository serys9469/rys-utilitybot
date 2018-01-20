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

/**
 * 対話機能用レスポンスモデルクラス
 *
 * @author Kiyohito Nara
 */
public class DialogueResponse {
    private String utt;
    private String yomi;
    private String mode;
    private String da;
    private String context;

    public DialogueResponse(String utt, String yomi, String mode, String da, String context) {
        this.utt = utt;
        this.yomi = yomi;
        this.mode = mode;
        this.da = da;
        this.context = context;
    }

    public String getUtt() {
        return utt;
    }

    public void setUtt(String utt) {
        this.utt = utt;
    }

    public String getYomi() {
        return yomi;
    }

    public void setYomi(String yomi) {
        this.yomi = yomi;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDa() {
        return da;
    }

    public void setDa(String da) {
        this.da = da;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
