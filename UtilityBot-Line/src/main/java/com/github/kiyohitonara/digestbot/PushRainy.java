/*package com.github.kiyohitonara.digestbot;


import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.model.Multicast;
import retrofit2.Response;
//import okhttp3.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class PushRainy {
    public void push() throws IOException {
        TextMessage textMessage = new TextMessage("今日は雨が降りそう！傘を持って出かけよう");

        PushMessage pushMessage = new PushMessage(
                "Uad908f552876b82d5b6b707ef78b5bf7",
                textMessage
        );

        Set<String> to = new HashSet<>();
        WeatherDatabaseAccess weatherDatabaseAccess = new WeatherDatabaseAccess();
        ArrayList<String> userIDList = weatherDatabaseAccess.getSingleIDs();
        for(String iD : userIDList) {
            System.out.println(iD);
            Collections.addAll(to, iD);
        }
        Multicast multicast = new Multicast(
                to,
                textMessage
        );

        String channelAccessToken = System.getenv("LINE_BOT_CHANNEL_TOKEN");
        Response<BotApiResponse> response =
                LineMessagingServiceBuilder
                        .create(channelAccessToken)
                        .build()
                        .multicast(multicast)//.pushMessage(pushMessage)
                        .execute();
    }
}
*/