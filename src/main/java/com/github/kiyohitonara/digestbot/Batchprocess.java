package com.github.kiyohitonara.digestbot;

import com.amazonaws.services.dynamodbv2.xspec.S;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.lang.System.*;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hoshinoeiko on 2017/01/21.
 */
@Service
public class Batchprocess {
    
    @Autowired
    private LineMessagingService mLineMessagingService;

    @Scheduled(cron = "* 0 * * * *")
    public void cron() {

        /*LocalDateTime assigndate = LocalDateTime.now()
                .withMinute(0)
                .withSecond(0);
                .withNano(0)
                .truncatedTo(ChronoUnit.HOURS);
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(assigndate, zone);

        Instant instant = zonedDateTime.toInstant();
        Date dates = Date.from(instant);
        Timestamp willsendtime = new Timestamp(dates.getTime());*/

        TimeZone timeZone_Japan = TimeZone.getTimeZone("Asia/Tokyo");
        ZoneId zoneId_Japan = timeZone_Japan.toZoneId();
        ZonedDateTime japanTime_Zone = ZonedDateTime.now(zoneId_Japan);
        LocalDateTime japanTime = japanTime_Zone.toLocalDateTime();

        String willsendtime = japanTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));
        //System.out.println("日本時間:"+willsendtime);

        Timestamp timeStamp_Japan = Timestamp.valueOf(japanTime);
        //System.out.println("timeStampの値は"+timeStamp_Japan);//ズレが生じる


        LocalDateTime japanTime_highAccuracy = japanTime.truncatedTo(ChronoUnit.HOURS);//時間以降切り捨て
        //System.out.println("精度直した方(LocalDateTime)は"+japanTime_highAccuracy);
        Timestamp timeStamp_Japan_highAccuracy = Timestamp.valueOf(japanTime_highAccuracy);
        //System.out.println("精度直した方(Timestamp)は"+timeStamp_Japan_highAccuracy);


        /*データベースに何度も接続しなおすことになるので、
        ArrayList<Arraylist<String>> listElements = Postgres.search_sendTime(willsendtime);
        を持ってきてそれを使ってループ処理とかする方がいい*/

        int listElements;
        if(Postgres.search_sendtime(willsendtime) != null){

            ArrayList<String> miniList = Postgres.search_sendtime(willsendtime).get(0);
            listElements =  miniList.size();

            for (int i = 0; i < listElements; i++) {
                // out.println(String.valueOf(listElements));
                String roomid=Postgres.search_sendtime(willsendtime).get(0).get(i);//roomid
                String url=Postgres.search_sendtime(willsendtime).get(1).get(i);//url
                System.out.println("ルームIDは:"+roomid);
                System.out.println("送る時間は:"+willsendtime);
                System.out.println("URLは:"+url);
                //PUSHで画像を送る処理の記述

                try {
                    mLineMessagingService.pushMessage(new PushMessage(roomid, Arrays.asList(new TextMessage("お手紙が届きました。"),new ImageMessage(url,url)))).execute();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }
    @Scheduled(cron = "* * 6 * * *")
    public void informRainy(){
        //Push機能は未完成
        /*PushRainy pushRainy = new PushRainy();
        try {
            pushRainy.push();
        }catch(IOException e){
            e.printStackTrace();
        }*/
    }
}
