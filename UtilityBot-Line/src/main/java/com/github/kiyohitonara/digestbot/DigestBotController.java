/*
 * Copyright 2016 Kiyohito Nara
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

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.VideoMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;





import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import retrofit2.Retrofit;


import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.lang.System.*;
import java.util.regex.Pattern;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static java.lang.System.out;


@LineMessageHandler
public class DigestBotController {
    @Autowired
    private LineMessagingService mLineMessagingService;

    @EventMapping
    public void handleFollowEvent(FollowEvent event) {
        String token = event.getReplyToken();
        TemplateMessage templateMessage = createHelp();
        this.reply(
            token,
            Arrays.asList(new TextMessage("はじめまして!多機能型ロボットのりんロボです♪\nそれぞれの機能の使い方は以下の通りです。"),
                new TextMessage("※PC版Lineには対応しておりません。\nお手数ですがスマホ版のLineからご利用ください。"),
                templateMessage)
        );
    }

    @EventMapping
    public void handleJoinEvent(JoinEvent event) {
        String token = event.getReplyToken();
        TemplateMessage templateMessage = createHelp();
        this.reply(
            token,
            Arrays.asList(new TextMessage("はじめまして!多機能型ロボットのりんロボです♪\nそれぞれの機能の使い方は以下の通りです。"),
                new TextMessage("※PC版Lineには対応しておりません。\nお手数ですがスマホ版のLineからご利用ください。"),
                templateMessage)
        );
    }


    @EventMapping
    public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
        LocationMessageContent locationMessage = event.getMessage();
        String replyToken = event.getReplyToken();
        String senderID = event.getSource().getSenderId();
        String rawLocation = locationMessage.getAddress();

        String arrPref[] = {"北海道",
            "青森","岩手","宮城","秋田","山形","福島",
            "茨城","栃木","群馬","埼玉","千葉","東京","神奈川",
            "新潟","富山","石川","福井","山梨","長野","岐阜",
            "静岡","愛知","三重","滋賀","京都","大阪","兵庫",
            "奈良","和歌山","鳥取","島根","岡山","広島","山口",
            "徳島","香川","愛媛","高知","福岡","佐賀","長崎",
            "熊本","大分","宮崎","鹿児島"
            ,"沖縄"
        };

        String indexPref = null;

        for(String pref : arrPref){
            if (rawLocation.contains(pref)) {
                indexPref = pref;
                break;
            }
        }

        if(indexPref != null){
            int readyFlg = 0;
            //ここで見るのはまずいと思う by kent
            WeatherDatabaseAccess weatherDatabaseAccess = new WeatherDatabaseAccess();
            weatherDatabaseAccess.init();
            if(weatherDatabaseAccess.searchID(senderID)) {
                readyFlg = weatherDatabaseAccess.returnFlg(senderID);
                if (readyFlg == 1) {
                    replyWeatherAreas(replyToken, indexPref, senderID);//managerを作って対応
                }
            }
            weatherDatabaseAccess.destroy();
            /*int flg = 0;
            //データベースを見に行く処理
            //WeatherManager weatherManager = new WeatherManager();
            //文字のときと同じ
            flg =1;
            if(flg == 1){
                //専用のmanagerを作ってそれを呼び出すべき(bad_coding_point)
                replyWeatherAreas(event.getReplyToken(), indexPref);
            }*/
        }else{
            this.reply(replyToken, new TextMessage("ごめんなさい。海外の天気はわからないんだ。"));
        }
    }

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws IOException {
        String token = event.getReplyToken();
        String groupId = event.getSource().getSenderId();
        String userID = event.getSource().getUserId();
        String text = event.getMessage().getText();

        String arrPref[] = {"北海道",
                "青森","岩手","宮城","秋田","山形","福島",
                "茨城","栃木","群馬","埼玉","千葉","東京","神奈川",
                "新潟","富山","石川","福井","山梨","長野","岐阜",
                "静岡","愛知","三重","滋賀","京都","大阪","兵庫",
                "奈良","和歌山","鳥取","島根","岡山","広島","山口",
                "徳島","香川","愛媛","高知","福岡","佐賀","長崎",
                "熊本","大分","宮崎","鹿児島"
                ,"沖縄"
            };
        String indexPref = null;
        for(String pref : arrPref){
            if (text.contains(pref)) {
            indexPref = pref;
            break;
            }
        }

        try {


            if(text.equals("ヘルプ")){
                this.reply(token, createHelp()); 
            } else if (text.contains("占いの使い方")){
                this.reply(token, new TextMessage("「占い」と発言してください。")); 
            } else if (text.contains("天気予報の使い方")){
                this.reply(token, new TextMessage("「天気予報」と発言してください。")); 
            } else if (text.contains("辞典の使い方")){
                this.reply(token, new TextMessage("調べたい言葉を「〇〇とは？」という形で送信してください。")); 
            } else if (text.contains("ルーレットの使い方")){
                this.reply(token, new TextMessage("「ルーレット」と発言してください。")); 
            } else if (text.contains("りんロボと会話を楽しむ使い方")){
                this.reply(token, new TextMessage("トークルームやグループトーク内ではみなさんの会話のじゃまにならないよう謹んでおります。\n会話に参加させていただけるなら「りんロボ」とお呼びください。\n許可を頂いたら一生懸命言葉を返します。\n日本語難しい...")); 
            } else if (text.contains("メッセージカードの使い方") || text.contains("トーク内検索の使い方") || text.contains("トーク内重要KeyWord調査の使い方") || text.contains("未実装")){
                this.reply(token, new TextMessage("現在、調整中です。\nもうしばらくお待ちください。")); 
            } else if (text.contains("使い方を忘れたら")){
                this.reply(token, new TextMessage("「ヘルプ」と発言してください。再び使い方を表示いたします。")); 
            } else if(text.startsWith("。")){
                //一致した
                SqlUti sqlinstance  = new SqlUti();

                //ルーレット中か、ルーレット中でないか(ルーレット中なら項目として追加)
                int rouletteInFlg = sqlinstance.insertItem(groupId,text.substring(1));

                if(rouletteInFlg == 1){
                    ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                        null,null,"「" + text.substring(1) + "」をルーレット項目に追加したよ♪\nこれでルーレットするならボタンを押してね！\nまだ項目を追加するなら、ボタンを押さずに続けてね!",
                        Arrays.asList(new MessageAction("ルーレットスタート！", "ルーレットスタート！"))
                    );
                    TemplateMessage templateMessage = new TemplateMessage("「" + text.substring(1) + "」をルーレット項目に追加したよ♪", buttonsTemplate);
                    this.reply(token, templateMessage);
                }

            } else if (text.contains("ルーレットスタート")){

                SqlUti sqlinstance  = new SqlUti();
                //ルーレット中ならItemを返却
                ArrayList<String> rouletteItem = sqlinstance.searchItems(groupId);
                Collections.shuffle(rouletteItem);
                
                if(rouletteItem.size() != 0)
                {

                    if(rouletteItem.size() >= 16)
                    {
                        for (int i = 16 ; i < rouletteItem.size() ; i++){
                          rouletteItem.remove(i);
                        }
                        this.reply(token, new TextMessage("項目が16個以上選択されているのでランダムに15個選んでルーレットを作成します。\nルーレット作成中です！しばらく待ってね")); 
                    }
                    else
                    {
                        this.reply(token, new TextMessage("ルーレット作成中です！しばらく待ってね")); 
                    }
                    
                    String[] itemArray = rouletteItem.toArray(new String[rouletteItem.size()]);;

                    String dirName = LocalDateTime.now().toString() + '-' + UUID.randomUUID().toString();

                    Path newvideodir = Paths.get("/tmp/video/" + dirName);
                    Path newimgdir = Paths.get("/tmp/img/" + dirName);
                    Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
                    FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(perms);
                    Path newvideopath = Files.createDirectories(newvideodir, attrs);
                    //log.info("Got text message from {}: {}", replyToken, "ビデオパス:" + newvideopath.toString());
                    Path newimgpath = Files.createDirectories(newimgdir, attrs);
                    //log.info("Got text message from {}: {}", replyToken, "画像パス:" + newimgpath.toString());


                    Grulette oneroulette = new Grulette();
                    oneroulette.createFrame(itemArray,newimgpath.toString());
                    oneroulette.makeMp4(newimgpath.toString(),"/tmp/video/" + dirName + ".mp4");

                    //Files.copy(Paths.get(newvideopath.toString() + "/roulette.mp4"), Paths.get(createUri("/static/video/roulette.mp4")));
                    //Files.copy(Paths.get(newimgpath.toString() + "/frame001.jpg"), Paths.get(createUri("/static/img/chap.jpg")));
                    //のちのちs3対応

                    UploadObjectSingleOperation upinstant = new UploadObjectSingleOperation();
                    //upinstant.uploadFileName = "/tmp/video/" + dirName + ".mp4";
                    upinstant.upfile(dirName + ".mp4");

                    this.push(groupId, Arrays.asList(
                        new TextMessage("さぁ何が出るかな？"),
                        new VideoMessage(System.getenv("S3_URL") + dirName + ".mp4",createUri("/static/img/hatena.jpg"))
                        )
                    ); 
                }
                else
                {
                    this.reply(token, new TextMessage("既にルーレットは作成開始しました。")); 
                }

            }else if (text.contains("ルーレット") && !text.contains("スタート")) {
                SqlUti sqlinstance  = new SqlUti();
                int rouletteFlg = sqlinstance.flagSearch(groupId);
                if(rouletteFlg == 0)
                {
                    //そのトークルームで初めて
                    this.reply(
                        token,
                        Arrays.asList(new TextMessage("ルーレット項目にしたい言葉を「。」を先頭につけて教えてね!"),
                            new TextMessage("↓↓こんな感じで♪"),
                            new ImageMessage(createUri("/static/img/explain.png"),createUri("/static/img/explain.png")))
                    );

                }
                else if(rouletteFlg == 1)
                {
                    this.reply(token, new TextMessage("ルーレット項目にしたい言葉を「。」を先頭につけて教えてね!\n(例)\n。焼肉\nとか\n。カラオケ\nみたいに!")); 
                }
            } else if(text.contains("りんロボ") || text.contains("りんろぼ") || text.contains("リンロボ")){
                Source source = event.getSource();
                if (source instanceof GroupSource || source instanceof RoomSource)
                {
                    ConfirmTemplate confirmTemplate = new ConfirmTemplate(
                        "会話モードを切り替えますか？",
                        new PostbackAction("ON","7,1","->会話モードをONにする。"),
                        new PostbackAction("OFF","7,2","->会話モードをOFFにする。")
                    );
                    TemplateMessage templateMessage = new TemplateMessage("会話モードを切り替え\n※こちらの機能はPC版Lineに対応しておりません。", confirmTemplate);
                    this.reply(token, templateMessage);   
                } 
                else
                {
                    this.reply(token, new TextMessage("呼びましたか？")); 
                }
                
            } else if (text.contains("てんき") || text.contains("天気")) {
                WeatherDatabaseAccess weatherDatabaseAccess = new WeatherDatabaseAccess();
                weatherDatabaseAccess.init();
                ArrayList<String> userIDList = weatherDatabaseAccess.getSingleIDs();
                System.out.println(userIDList);
                if(!(weatherDatabaseAccess.searchID(groupId))) {
                    weatherDatabaseAccess.insert(groupId);
                    if(userID != null){
                        weatherDatabaseAccess.updateSingle(userID);
                    }
                }
                weatherDatabaseAccess.putFlg(groupId, "on");

                String weatherIndexMessage = "天気が知りたい時は、都道府県名をコメントするか、位置情報を送ってね！\n例)　神奈川県";
                this.reply(token, new TextMessage(weatherIndexMessage));
                //120秒たったらflg = 0 に戻す
                Thread.sleep(120000);//別のセッションだとしても120秒後に切られてしまう（しょうがない？）
                weatherDatabaseAccess.putFlg(groupId, "off");
                weatherDatabaseAccess.destroy();

            } else if(indexPref != null){
                int readyFlg = 0;
                //ここで見るのはまずいと思う by kent
                WeatherDatabaseAccess weatherDatabaseAccess = new WeatherDatabaseAccess();
                weatherDatabaseAccess.init();
                if(weatherDatabaseAccess.searchID(groupId)) {
                    readyFlg = weatherDatabaseAccess.returnFlg(groupId);
                    if (readyFlg == 1) {
                        replyWeatherAreas(token, indexPref,groupId);//managerを作って対応
                    }
                }
                weatherDatabaseAccess.destroy();


            } else if (text.endsWith("とは？")){
                //専用のmanagerを作ってそれを呼び出すべき(bad_coding_point)
                WordSearch wordSearch = new WordSearch();
                ArrayList<ArrayList<String>> list = wordSearch.getXMLWordsList(text.substring(0,text.length()-3),"0");
                ArrayList<CarouselColumn> carouselList = new ArrayList<CarouselColumn>();
                int countWords = 0;
                System.out.println(list.size());
                for(countWords = 0;countWords<list.size();countWords++)
                {
                    ArrayList<String> sub = list.get(countWords);

                    carouselList.add(
                            new CarouselColumn(null,"この言葉ですか？",sub.get(1).trim(), Arrays.asList(
                            new PostbackAction("この言葉の意味を聞く",
                                                    "1,"+sub.get(0).trim(),"->"+sub.get(1).trim()+"の意味を取得"),
                            new PostbackAction("次の候補を表示する",
                                                    "2,"+text.substring(0,text.length()-3)+",0","->次の候補を見る")
                    )));
                }

                if(countWords != 0)
                {
                    TemplateMessage templateMessage = new TemplateMessage("検索結果", new CarouselTemplate(carouselList));
                    this.reply(token, templateMessage);
                }
                else
                {
                    this.reply(token, new TextMessage("すみません。私には分からない言葉です。")); 
                }
            } else if (text.equals("占い")) {
                //専用のmanagerを作ってそれを呼び出すべき(bad_coding_point)
                handleTextContent(token, event, text);
            } else if(text.startsWith("->")){
                //専用のmanagerを作ってそれを呼び出すべき(bad_coding_point)
                if(text.endsWith("座"))
                {
                    fortune(trimAnnotation("->",text),token);
                }
            }  else if(event.getMessage().getText().equals("メッセージカード")) {
                //カードの種類を選択する処理
                // 送るもの：message[使用するテンプレートを選んでください],imagemap

                if (event.getSource().getUserId() == null) {
                    //グループトークでの場合なにもしない
                } else {

                    handleTextContent(token, event, text);
                    Postgres.refleshstatus(event.getSource().getUserId());
                }
            }else if (event.getMessage().getText().equals("yellow")||event.getMessage().getText().equals("pink")
                    ||event.getMessage().getText().equals("purple") ||event.getMessage().getText().equals("green")
                    ||event.getMessage().getText().equals("blue")||event.getMessage().getText().equals("colorful")){
                Postgres.Update_choosecard(event.getSource().getUserId(),event.getMessage().getText());
                //if(checkstatement(event.getSource().getUserId(),2)==1)
                replyText(token,"カードに書き込みたい文章を「」で囲って記入してください\n"+"(例)「ありがとう」");
                Postgres.refleshstatus(groupId);
            } else if(event.getMessage().getText().contains("「")&&event.getMessage().getText().contains("」")){
                String userId = event.getSource().getUserId();

                try {
                    Postgres.refleshstatus(event.getSource().getUserId());

                    //「」でくくられたメッセージの中身を抽出
                    String strl = event.getMessage().getText();
                    String willToWriteforcard =strl.substring(1, strl.indexOf("」"));
                    //out.println(willToWriteforcard);
                    Postgres.Update_textmessage(userId,willToWriteforcard);//データベースにメッセージを格納

                    //JAVAクラスを利用し,IMAGEに書き込む,/TMPに一時保存
                    Postgres.setfilepath(userId);
                    String getfilepath=Postgres.getfilepath();

                    out.println("filepathは:"+getfilepath);//日本語の時null

                    //S3からテンプレートを取得後、CreateMessagecard(未完成)で画像作成
                    //UploadObjectSingleOperation.getimage(createUri("/static/tempformessagecard/"+getfilepath));
                    CreateMessagecard.write(willToWriteforcard,userId,"/app/src/main/resources/static/tempformessagecard/"+getfilepath);

                    //アップロード後、トークルームアップロードした画像を送信し、そのURLをDBに格納
                    UploadObjectSingleOperation.upload(userId,getfilepath);
                    String receveURL = UploadObjectSingleOperation.getimageURL();
                    out.println("URLは:"+receveURL);
                    Postgres.Update_saveurl(userId,receveURL);



                    //String imageUrl = createUri(receveURL);

                    ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                            receveURL,
                            "こんな感じでよろしいですか？",
                            "次の操作を選んでください<-nullにする",
                            Arrays.asList(
                                    new PostbackAction("メッセージを変更する",
                                            "4,",
                                            "はい、メッセージを変更します"),
                                    new PostbackAction("続けて、投下する日時を設定する",
                                            "5,")
                            ));

                    TemplateMessage templateMessage = new TemplateMessage("次の操作を選んでください", buttonsTemplate);
                    this.reply(event.getReplyToken(), templateMessage);


                    // hoge.Update_password(userId,madepassword);
                }catch(NullPointerException e){
                    e.printStackTrace();
                    out.println("NullPointerException is occured");
                    replyText(event.getReplyToken(),"NullPointerExceptionエラーが起こりました。最初からやり直してください");
                }catch(Throwable e){
                    e.printStackTrace();
                    out.println("Can't expecting error is occured");
                    replyText(event.getReplyToken(),"Throwableエラーが起こりました。最初からやり直してください");
                }
            }

            else if(event.getMessage().getText().contains("月")&&event.getMessage().getText().contains("日")
                    &&event.getMessage().getText().contains("時")){
                Postgres.refleshstatus(event.getSource().getUserId());

                String gettext =event.getMessage().getText();
                int monthindex= gettext.lastIndexOf("月");
                int dateindex = gettext.lastIndexOf("日");
                int hourindex = gettext.lastIndexOf("時");
                String numberstr= gettext.substring(monthindex-2,monthindex)
                        +gettext.substring(dateindex-2,dateindex)
                        +gettext.substring(hourindex-2,hourindex);


                out.println("ゲットした数字の表示:"+numberstr);
                String numbertoharfstr=fullWidthNumberToHalfWidthNumber(numberstr);
                String monthstr= numbertoharfstr.substring(0,2);
                String datestr =numbertoharfstr.substring(2,4);
                String timestr  =numbertoharfstr.substring(4,6);
                out.println(monthstr+datestr+timestr);
                Timestamp willsendtime = createsendtime(
                        Integer.parseInt(monthstr),Integer.parseInt(datestr),Integer.parseInt(timestr));
                out.println(willsendtime);

                //パスワード作成
                String madepassword =makePassWord();

                Postgres.Update_sendtime(event.getSource().getUserId(),willsendtime);
                this.reply(event.getReplyToken(),Arrays.asList(
                        new TextMessage(willsendtime+"に送信します。"),
                        new TextMessage("送信したいトークルームで以下のパスワードを入力してください。(パスワードは24時間以内に入力してください。)"),
                        new TextMessage(madepassword)
                ));

                Postgres.Update_password(event.getSource().getUserId(),madepassword);
            }
            else if(event.getMessage().getText().length()==16&&isHankakuOnly(event.getMessage().getText())){
                //Postgres.refleshstatus(event.getSource().getUserId());
                //String groopid = event.getSource().getSenderId();
                out.println("GROOOOOOPID:"+groupId);
                Postgres.Update_settoroomid(groupId,event.getMessage().getText());

            }


            else if (text.startsWith(System.getenv("ANNOTATION_SEARCH"))) {
                String word = trimAnnotation(System.getenv("ANNOTATION_SEARCH"), text);
                SearchManager searchManager = new SearchManager();
                ReplyMessage replyMessage = searchManager.createReply(token, 0, groupId, word, "");

                mLineMessagingService.replyMessage(replyMessage).execute();
            } else if (text.startsWith(System.getenv("ANNOTATION_KEYWORD"))) {
                String date = trimAnnotation(System.getenv("ANNOTATION_KEYWORD"), text);
                KeywordManager keywordManager = new KeywordManager();
                ReplyMessage replyMessage = keywordManager.createReply(token, 0, groupId, date);

                mLineMessagingService.replyMessage(replyMessage).execute();
            } else if (text.startsWith(System.getenv("ANNOTATION_LOG"))) {
                DatabaseManager databaseManager = new DatabaseManager();
                databaseManager.init();
                databaseManager.printLog(groupId, 20);
                databaseManager.destroy();
            } else {

                Source source = event.getSource();
                if (source instanceof GroupSource || source instanceof RoomSource)
                {
                    SqlUti sqlinstance  = new SqlUti();

                    if (sqlinstance.search_conversationFlg(groupId)== 1)
                    {
                        DialogueManager dialogueManager = new DialogueManager();
                        ReplyMessage replyMessage = dialogueManager.createReply(token, 0, groupId, text);
                        mLineMessagingService.replyMessage(replyMessage).execute();
                    }
                }
                else
                {
                    DialogueManager dialogueManager = new DialogueManager();
                    ReplyMessage replyMessage = dialogueManager.createReply(token, 0, groupId, text);
                    mLineMessagingService.replyMessage(replyMessage).execute();                
                }
                DatabaseManager databaseManager = new DatabaseManager();
                databaseManager.init();
                databaseManager.insert(groupId, text);
                databaseManager.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventMapping
    public void handlePostbackEvent(PostbackEvent event) {
        String token = event.getReplyToken();
        String roomId = event.getSource().getSenderId();
        String data = event.getPostbackContent().getData();
        String[] postBackData = data.split(",", 0);


        try {
            //,区切りで、一番目の数字でmode指定
            //1=言葉意味検索処理
            //2=言葉、次の5件取得
            //3以降自由に
            //modeでケース分け
            //専用のmanagerを作ってそれを呼び出すべき(bad_coding_point)

            //jsonに統一すべき(bad_coding_point)
            switch (postBackData[0]) {

                case "1": {
                    WordSearch wordSearch = new WordSearch();
                    String meanText = wordSearch.getXMLWordMean(postBackData[1]);
                    this.reply(token, new TextMessage(meanText.trim()));
                    break;
                }

                case "2": {
                    int pageindex = Integer.parseInt(postBackData[2]);
                    pageindex++;
                    WordSearch wordSearch = new WordSearch();
                    ArrayList<ArrayList<String>> list = wordSearch.getXMLWordsList(postBackData[1], String.valueOf(pageindex));
                    ArrayList<CarouselColumn> carouselList = new ArrayList<CarouselColumn>();
                    int countWords = 0;
                    System.out.println(list.size());
                    for (countWords = 0; countWords < list.size(); countWords++) {
                        ArrayList<String> sub = list.get(countWords);
                        carouselList.add(
                                new CarouselColumn(null, "この言葉ですか？", sub.get(1).trim(), Arrays.asList(
                                        new PostbackAction("この言葉の意味を聞く",
                                                "1," + sub.get(0).trim(), "->" + sub.get(1).trim() + "の意味を取得する"),
                                        new PostbackAction("次の候補を表示する",
                                                "2," + postBackData[1] + "," + String.valueOf(pageindex), "->次の候補を見る")
                                )));
                    }
                    if (countWords != 0) {
                        TemplateMessage templateMessage = new TemplateMessage("検索結果", new CarouselTemplate(carouselList));
                        this.reply(token, templateMessage);
                    } else {
                        this.reply(token, new TextMessage("すみません。これ以上の言葉は分かりません。"));
                    }
                    break;
                }

                case "3": {

                    int readyFlg = 0;
                    String senderId = postBackData[3];//senderId
                    WeatherDatabaseAccess weatherDatabaseAccess = new WeatherDatabaseAccess();
                    weatherDatabaseAccess.init();
                    if(weatherDatabaseAccess.searchID(senderId)) {
                        readyFlg = weatherDatabaseAccess.returnFlg(senderId);
                        if (readyFlg == 1) {
                            String prefName = postBackData[1];//県あり
                            String prefName_KenNashi = postBackData[2];//県なし

                            int areaNum = Integer.parseInt(postBackData[4]);//地域に対応するタグの番号
                            Weather aWth = new Weather();
                            String arrWeatherInfo[] = aWth.bringWeatherInfo(prefName_KenNashi, areaNum);
                            String prefectureName = arrWeatherInfo[0];
                            String areaName = arrWeatherInfo[1];
                            String arrSentences[] = new String[5];
                            String arrDate[] = new String[5];

                            String arrImage[] = new String[5];//新たに
                            String arrWeather[] = new String[5];//あらたに


                            for(int i = 0; i<5; i++){
                                int temp = 0;
                                temp = 2+9*i;
                                String date = arrWeatherInfo[temp];
                                arrDate[i] = date;
                                temp = 3+9*i;

                                String weather = arrWeatherInfo[temp];
                                arrWeather[i] = weather;

                                temp = 4+9*i;
                                String max = arrWeatherInfo[temp];
                                temp = 5+9*i;
                                String min = arrWeatherInfo[temp];
                                temp = 6+9*i;
                                String rain_1 = arrWeatherInfo[temp];
                                temp = 7+9*i;
                                String rain_2 = arrWeatherInfo[temp];
                                temp = 8+9*i;
                                String rain_3 = arrWeatherInfo[temp];
                                temp = 9+9*i;
                                String rain_4 = arrWeatherInfo[temp];

                                arrSentences[i] = "天気:"
                                        +weather
                                        +"\n気温:"
                                        +max
                                        +"/"
                                        +min
                                        +"°C   降水確率-朝:"
                                        +rain_1
                                        +"%昼:"
                                        +rain_2
                                        +"%夕:"
                                        +rain_3
                                        +"%夜:"
                                        +rain_4
                                        +"%";

                                temp = 10+9*i;
                                arrImage[i] = createUri(arrWeatherInfo[temp]);

                                System.out.println(arrSentences[i]);
                            }

                            //カルーセル
                            String imageUrl = createUri("/static/img/sun.jpg");
                            CarouselTemplate carouselTemplate = new CarouselTemplate(
                                    Arrays.asList(
                                            new CarouselColumn(arrImage[0], arrDate[0]+" "+prefectureName+"の天気", arrSentences[0], Arrays.asList(

                                                    new PostbackAction("BOTくんのひとこと",
                                                            "6,0,"+arrWeather[0])
                                            )),new CarouselColumn(arrImage[1], arrDate[1]+" "+prefectureName+"の天気", arrSentences[1], Arrays.asList(

                                                    new PostbackAction("BOTくんのひとこと",
                                                            "6,1,"+arrWeather[1])
                                            )),new CarouselColumn(arrImage[2], arrDate[2]+" "+prefectureName+"の天気", arrSentences[2], Arrays.asList(

                                                    new PostbackAction("BOTくんのひとこと",
                                                            "6,2,"+arrWeather[2])
                                            )),new CarouselColumn(arrImage[3], arrDate[3]+" "+prefectureName+"の天気", arrSentences[3], Arrays.asList(

                                                    new PostbackAction("BOTくんのひとこと",
                                                            "6,3,"+arrWeather[3])
                                            )),new CarouselColumn(arrImage[4], arrDate[4]+" "+prefectureName+"の天気", arrSentences[4], Arrays.asList(

                                                    new PostbackAction("BOTくんのひとこと",
                                                            "6,4,"+arrWeather[4])
                                            ))
                                    )
                            );
                            TemplateMessage templateMessage = new TemplateMessage("天気予報が送られました", carouselTemplate);
                            this.reply(token, templateMessage);
                        }
                    }
                    weatherDatabaseAccess.putFlg(senderId, "off");//一度5日分の天気を表示したら反応しないようにする
                    System.out.println("5日分表示したのでready_flgを0にしました");
                    weatherDatabaseAccess.destroy();

                    break;
                }
                case "4": {
                    Postgres.refleshstatus(event.getSource().getUserId());
                    //データベースに接続してフラグを2にする

                    try {
                        Postgres.readyForMessage(event.getSource().getUserId());
                        this.reply(token, new TextMessage("メッセージを変更します。カードに書き込みたい文章を「」で囲って記入してください"));

                    } catch (NullPointerException e) {
                        out.println("NullPointerException is occured");
                        replyText(event.getReplyToken(), "NullPointerExceptionエラーが起こりました。最初からやり直してください");
                    } catch (Throwable e) {
                        out.println("Can't expecting error is occured");
                        replyText(event.getReplyToken(), "Throwableエラーが起こりました。最初からやり直してください");
                    }

                    break;

                }

                case "5": {

                    Postgres.refleshstatus(event.getSource().getUserId()); //userIdで検索して最新のやつ以外を0にする

                    //String userId = event.getSource().getUserId();
                    Postgres hoge = new Postgres();
                    UploadObjectSingleOperation uploadtoS3 = new UploadObjectSingleOperation();
                    try {
                        String cardUrl = Postgres.getWhereCardIs(event.getSource().getUserId());
                        this.reply(event.getReplyToken(), Arrays.asList(
                                new ImageMessage(cardUrl, cardUrl),
                                new TextMessage("送信したい月日時を全角で記入して下さい\n(例)１２月２４日２２時")
                        ));
                        //this.reply(token, new TextMessage("メッセージを変更します。カードに書き込みたい文章を「」で囲って記入してください"));

                    } catch (NullPointerException e) {
                        out.println("NullPointerException is occured");
                        replyText(event.getReplyToken(), "NullPointerExceptionエラーが起こりました。最初からやり直してください");
                    } catch (Throwable e) {
                        out.println("Can't expecting error is occured");
                        replyText(event.getReplyToken(), "Throwableエラーが起こりました。最初からやり直してください");
                    }

                    break;

                }

                case "6": {
                    try{
                        String userTaped = postBackData[2];
                        this.reply(token, new TextMessage(userTaped+"の日には、いいことが起こる気がするんです！")); 
                    }catch(Exception e){  
                    }   

                    break;
                }

                case "7": {
                    try 
                    {
                        String onOff = postBackData[1];
                        out.println(onOff);
                        if(onOff.contains("1"))
                        {//on
                            SqlUti sqlinstance  = new SqlUti();
                            sqlinstance.conversation_change(roomId,1);
                            this.reply(token, new TextMessage("ありがとうございます！\nガンガン話させてもらいます！")); 
                        }
                        else if(onOff.contains("2"))
                        {//off
                            SqlUti sqlinstance  = new SqlUti();
                            sqlinstance.conversation_change(roomId,0);
                            this.reply(token, new TextMessage("空気をよみます...\n私はだだのロボットですから...")); 
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                default: {
                    PostbackEventData postbackEventData = PostbackEventData.fromJson(data);

                    List<String> dataList = postbackEventData.getDataList();
                    long id = Long.parseLong(dataList.get(SearchManager.INDEX_ID));
                    String groupId = dataList.get(SearchManager.INDEX_GROUP_ID);
                    String date = dataList.get(SearchManager.INDEX_DATE);
                    String word = dataList.get(SearchManager.INDEX_WORD);

                    SearchManager searchManager = new SearchManager();
                    ReplyMessage replyMessage = searchManager.createReply(token, id, groupId, word, date);

                    mLineMessagingService.replyMessage(replyMessage).execute();

                }
            }
     
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {

    }


    //push通知は動作確認していない(1/18)
    private void push(@NonNull String to, @NonNull Message message) {
        push(to, Collections.singletonList(message));
    }

    private void push(@NonNull String to, @NonNull List<Message> messages) {
        try {
            mLineMessagingService.pushMessage(new PushMessage(to, messages)).execute();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
   
    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
            mLineMessagingService.replyMessage(new ReplyMessage(replyToken, messages)).execute();

        } catch (IOException e) {
         e.printStackTrace();
        }
    }

    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    //専用のmanagerを作るべき(bad_coding_point)
    private void handleTextContent(String replyToken, Event event,String text)throws IOException{
            switch (text) {
                case "メッセージカード":{
                    String userId = event.getSource().getUserId();
                    Postgres hoge=new Postgres();
                    try {
                        out.println("try insert");
                        hoge.insertTo(userId);
                        out.println("finish insert");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    this.reply(replyToken,Arrays.asList(new TextMessage("好きなテンプレートを選んでください。"),
                            new ImagemapMessage(
                                    createUri("/static/choosetemp"),
                                    "メッセージカード",
                                    new ImagemapBaseSize(500, 1040),
                                    Arrays.asList(
                                            new MessageImagemapAction("yellow", new ImagemapArea(0, 0, 350, 250)),
                                            new MessageImagemapAction("pink", new ImagemapArea(350, 0, 340, 250)),
                                            new MessageImagemapAction("purple", new ImagemapArea(690,0, 350, 250)),
                                            new MessageImagemapAction("green", new ImagemapArea(0,250, 350, 250)),
                                            new MessageImagemapAction("blue", new ImagemapArea(350,250, 340, 250)),
                                            new MessageImagemapAction("colorful", new ImagemapArea(690,250, 350, 250))
                                    )
                            )
                    ));
                }
                case "占い" :{
                    this.reply(replyToken,  Arrays.asList( new TextMessage("あなたの星座を選んでください。"),
                            new ImagemapMessage(
                            createUri("/static/seizamap1"),
                            "This is alt text",
                            new ImagemapBaseSize(631, 1040),
                            Arrays.asList(
                                    new MessageImagemapAction("->牡羊座", new ImagemapArea(0, 0, 250, 200)),
                                    new MessageImagemapAction("->牡牛座", new ImagemapArea(265, 0, 250, 200)),
                                    new MessageImagemapAction("->双子座", new ImagemapArea(525, 0, 250, 200)),
                                    new MessageImagemapAction("->蟹座", new ImagemapArea(785, 0, 250, 200)),
                                    new MessageImagemapAction("->獅子座", new ImagemapArea(0, 215, 250, 200)),
                                    new MessageImagemapAction("->乙女座", new ImagemapArea(265, 215, 250, 200)),
                                    new MessageImagemapAction("->天秤座", new ImagemapArea(525, 215, 250, 200)),
                                    new MessageImagemapAction("->蠍座", new ImagemapArea(785, 215, 250, 200)),
                                    new MessageImagemapAction("->射手座", new ImagemapArea(0, 430, 250, 200)),
                                    new MessageImagemapAction("->山羊座", new ImagemapArea(265, 430, 250, 200)),
                                    new MessageImagemapAction("->水瓶座", new ImagemapArea(525, 430, 250, 200)),
                                    new MessageImagemapAction("->魚座", new ImagemapArea(785, 430, 250, 200))
                                )
                    )));
                }
            }
    }

    //専用のmanagerを作るべき(bad_coding_point)
    private void fortune(String text,String token) {

        Request request = new Request.Builder()
                .url("http://api.jugemkey.jp/api/horoscope/free/" +getJST())
                .build();

        try {
            Response response = new OkHttpClient().newCall(request).execute();

            String result = response.body().string();
            String parse = result.replaceFirst(getJST(), "data");

            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<Fortune> jsonAdapter = moshi.adapter(Fortune.class);
            Fortune fortune = jsonAdapter.fromJson(parse);

            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("牡羊座");
            arrayList.add("牡牛座");
            arrayList.add("双子座");
            arrayList.add("蟹座");
            arrayList.add("獅子座");
            arrayList.add("乙女座");
            arrayList.add("天秤座");
            arrayList.add("蠍座");
            arrayList.add("射手座");
            arrayList.add("山羊座");
            arrayList.add("水瓶座");
            arrayList.add("魚座");

            int i = arrayList.indexOf(text);

          //  System.out.println(fortune.getHoroscope().getData().get(i).getItem());

            String content =fortune.getHoroscope().getData().get(i).getContent();
            String item =fortune.getHoroscope().getData().get(i).getItem();
            String money =Integer.toString(fortune.getHoroscope().getData().get(i).getMoney());
            String total =Integer.toString(fortune.getHoroscope().getData().get(i).getTotal());
            String job =Integer.toString(fortune.getHoroscope().getData().get(i).getJob());
            String color =fortune.getHoroscope().getData().get(i).getColor();
           // String day =fortune.getHoroscope().getData().get(i).getDay();
            String love =Integer.toString(fortune.getHoroscope().getData().get(i).getLove());
            String rank =Integer.toString(fortune.getHoroscope().getData().get(i).getRank());
            String sign =fortune.getHoroscope().getData().get(i).getSign();

            //ArrayList<Message> resulthoro=new ArrayList<Message>();
              //  resulthoro.add(new TextMessage(content));
                //resulthoro.add(new TextMessage(item));

            TextMessage resulthoro =new TextMessage("今日の"+sign+"の運勢は…\n\n"+content+"\nラッキーアイテム:"+item+"\nラッキーカラー:"+color
                    +"\n金運:"+money+"\n恋愛:"+love+"\n仕事:"+job+"\n総合運:"+total+"\n順位"+rank
            );

            mLineMessagingService.replyMessage(new ReplyMessage(token,resulthoro)).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //専用のmanagerを作るべき(bad_coding_point)
    public void replyWeatherAreas(String replyToken, String indexPref, String SenderId){
        try{
                Weather wth = new Weather();
                ArrayList<String> arrArea = wth.getArea(indexPref);

                for(int aa = 0; aa<arrArea.size(); aa++){
                    System.out.println(arrArea.get(aa));
                }//権あり　地域たち 県なし 



                //データベースに入れる




                String imageUrl = createUri(arrArea.get(arrArea.size() - 2));//ここをarrAreaのarrArea.size()-2番目にする

                //別メソッドに置き換える

                switch(arrArea.size()-3){//地域数による場合分け

                    case 1:
                        CarouselTemplate carouselTemplate1 = 
                            new CarouselTemplate(Arrays.asList(new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(1),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",1")
                        ))));
                        TemplateMessage templateMessage1 = new TemplateMessage("天気予報を表示したい地域を選んでください", carouselTemplate1);
                        this.reply( replyToken, templateMessage1);

                        break;


                    case 2:
                        CarouselTemplate carouselTemplate2 = 
                            new CarouselTemplate(Arrays.asList(new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(1),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",1"),
                                            new PostbackAction(arrArea.get(2),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",2")//県付き/なし
                        ))));
                        TemplateMessage templateMessage2 = new TemplateMessage("天気予報を表示したい地域を選んでください", carouselTemplate2);
                        this.reply( replyToken, templateMessage2);

                        break;

                    case 3:
                        CarouselTemplate carouselTemplate3 = 
                            new CarouselTemplate(Arrays.asList(new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(1),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",1"),
                                            new PostbackAction(arrArea.get(2),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",2"),
                                            new PostbackAction(arrArea.get(3),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",3")
                        ))));
                        TemplateMessage templateMessage3 = new TemplateMessage("天気予報を表示したい地域を選んでください", carouselTemplate3);
                        this.reply( replyToken, templateMessage3);

                        break;

                    case 4:
                        CarouselTemplate carouselTemplate4 = 
                            new CarouselTemplate(Arrays.asList(new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(1),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",1"),
                                            new PostbackAction(arrArea.get(2),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",2")
                                                                )),
                                                            new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(3),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",3"),
                                            new PostbackAction(arrArea.get(4),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",4")
                                                                ))
                            ));
                        TemplateMessage templateMessage4 = new TemplateMessage("天気予報を表示したい地域を選んでください", carouselTemplate4);
                        this.reply( replyToken, templateMessage4);

                        break;

                    case 7://沖縄
                        CarouselTemplate carouselTemplate5 = 
                            new CarouselTemplate(Arrays.asList(new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(1),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",1"),
                                            new PostbackAction(arrArea.get(3),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",3")
                                                                )),
                                                            new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(4),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",4"),
                                            new PostbackAction(arrArea.get(5),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",5")
                                                                )),
                                                            new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(5),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",6"),
                                            new PostbackAction(arrArea.get(6),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",7")
                                                                ))
                            ));
                        TemplateMessage templateMessage5 = new TemplateMessage("天気予報を表示したい地域を選んでください", carouselTemplate5);
                        this.reply( replyToken, templateMessage5);

                        break;
                        
                    case 16://北海道
                        CarouselTemplate carouselTemplate6 = 
                            new CarouselTemplate(Arrays.asList(new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(1),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",1"),
                                            new PostbackAction(arrArea.get(3),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",3"),
                                            new PostbackAction(arrArea.get(4),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",4")
                                                                )),
                                                            new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(5),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",5"),
                                            new PostbackAction(arrArea.get(6),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",6"),
                                            new PostbackAction(arrArea.get(7),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",7")
                                                                )),
                                                            new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(8),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",8"),
                                            new PostbackAction(arrArea.get(9),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",9"),
                                            new PostbackAction(arrArea.get(10),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",10")
                                                                )),
                                                            new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(11),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",11"),
                                            new PostbackAction(arrArea.get(12),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",12"),
                                            new PostbackAction(arrArea.get(13),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",13")
                                                                )),
                                                            new CarouselColumn(imageUrl, arrArea.get(0)+"の天気","天気予報を表示したい地域を選んでください", Arrays.asList(
                                            new PostbackAction(arrArea.get(14),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",14"),
                                            new PostbackAction(arrArea.get(15),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",15"),
                                            new PostbackAction(arrArea.get(16),"3,"+arrArea.get(0)+","+arrArea.get(arrArea.size() - 1)+","+SenderId+",16")
                                                            ))
                            ));
                        TemplateMessage templateMessage6 = new TemplateMessage("天気予報を表示したい地域を選んでください", carouselTemplate6);
                        this.reply( replyToken, templateMessage6);

                        break;


                }

                

                
                }catch(Exception e){

                }
    }



    //ヘルプのテンプレート作成
    public static TemplateMessage createHelp() {

         CarouselTemplate carouselTemplate = new CarouselTemplate(
            Arrays.asList(
                new CarouselColumn(null, null, "りんロボの機能使い方", Arrays.asList(
                    new MessageAction("占いの使い方","占いの使い方"),
                    new MessageAction("天気予報の使い方","天気予報の使い方"),
                    new MessageAction("辞典の使い方","辞典の使い方")
                )), 
                new CarouselColumn(null, null, "りんロボの機能使い方", Arrays.asList(
                    new MessageAction("ルーレットの使い方","ルーレットの使い方"),
                    new MessageAction("りんロボと会話を楽しむ使い方","りんロボと会話を楽しむ使い方"),
                    new MessageAction("メッセージカードの使い方","メッセージカードの使い方")
                    
                )),
                new CarouselColumn(null, null, "りんロボの機能使い方", Arrays.asList(
                    new MessageAction("トーク内検索の使い方","トーク内検索の使い方"),
                    new MessageAction("トーク内重要KeyWord調査の使い方","トーク内重要KeyWord調査の使い方"),
                    new MessageAction("使い方を忘れたら?","使い方を忘れたら？")
                ))
            )
        );
        return new TemplateMessage("りんロボの使い方\n※こちらの機能はPC版Lineに対応しておりません。", carouselTemplate);
    }


    public static String createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path).build()
                .toUriString();
    }


//時間を扱うクラスとしてまとめるべき
    private static String getJST() {

        TimeZone tz = TimeZone.getTimeZone("JST");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        simpleDateFormat.setTimeZone(tz);
        Date dates =new Date();
       return simpleDateFormat.format(dates);

    }
    public static String getJSTtime() {

        TimeZone tz = TimeZone.getTimeZone("JST");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-HH-mm-ss");

        simpleDateFormat.setTimeZone(tz);
        Date dates =new Date();
        return simpleDateFormat.format(dates);

    }
    private static Timestamp createsendtime(int month,int date,int hour){
        LocalDateTime assigndate =
                LocalDateTime.now()
                        .withMonth(month)
                        .withDayOfMonth(date)
                        .withHour(hour)
                        .truncatedTo(ChronoUnit.HOURS);
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(assigndate, zone);

        Instant instant = zonedDateTime.toInstant();
        Date dates = Date.from(instant);
        Timestamp willsendtime = new Timestamp(dates.getTime());
        return willsendtime;
    }
    //生成するパスワード
    public static String makePassWord(){

        //パスワード桁数
        int length = 16;
        //記号使用有無　ここでは記号使用なしとした
        boolean useSign = false;
        //アルファベット大文字小文字のスタイル(normal/lowerCase/upperCase)
        String style = "normal";

        //生成処理
        StringBuilder result = new StringBuilder();
        //パスワードに使用する文字を格納
        StringBuilder source = new StringBuilder();
        //数字
        for (int i = 0x30; i < 0x3A; i++) {
            source.append((char) i);
        }
        //記号
        if (useSign) {
            for (int i = 0x21; i < 0x30; i++) {
                source.append((char) i);
            }
        }
        //アルファベット小文字
        switch (style) {
            case "lowerCase":
                break;
            default:
                for (int i = 0x41; i < 0x5b; i++) {
                    source.append((char) i);
                }
                break;
        }
        //アルファベット大文字
        switch (style) {
            case "upperCase":
                break;
            default:
                for (int i = 0x61; i < 0x7b; i++) {
                    source.append((char) i);
                }
                break;
        }

        int sourceLength = source.length();
        Random random = new Random();
        while (result.length() < length) {
            result.append(source.charAt(Math.abs(random.nextInt()) % sourceLength));
        }
        //標準出力
        System.out.format("生成結果=%1$s", result);

        return new String(result);


    }

    /**
     * 文字列に含まれる全角数字を半角数字に変換します。
     */
    public static String fullWidthNumberToHalfWidthNumber(String str) {
        if (str == null){
            throw new IllegalArgumentException();
        }
        StringBuffer sb = new StringBuffer(str);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ('０' <= c && c <= '９') {
                sb.setCharAt(i, (char) (c - '０' + '0'));
            }
        }
        return sb.toString();
    }
    /**
     * 指定した文字列が半角文字のみか判断する
     */
    public boolean isHankakuOnly(String source) {
        if (source == null || source.equals("")) {
            return true;
        }
        String regText = "[ -~｡-ﾟ]+";
        Pattern pattern = Pattern.compile(regText);
        return pattern.matcher(source).matches();
    }





    private String trimAnnotation(String annotation, String s) {
        int length = annotation.length();

        return s.substring(length).trim();
    }

}