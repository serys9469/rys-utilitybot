package com.github.kiyohitonara.digestbot;

import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapAction;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

/**
 * 占い配信API 原宿占い館塔里木(タリム)監修(http://jugemkey.jp/api/waf/api.php)を使用した、占い機能用クラス
 *
 * @author Eiko Hoshino
 */
public class FortuneManager {
    private static final String[] CONSTELLATIONS = {"牡羊座", "牡牛座", "双子座", "蟹座", "獅子座", "乙女座", "天秤座", "蠍座", "射手座", "山羊座", "水瓶座", "魚座"};

    /**
     * 星座選択のImageMapを作成し、リプライメッセージを作成します
     *
     * @param token 返信用トークン
     * @return 星座選択のImageMapを含むメッセージ
     */
    public ReplyMessage createSelectReply(String token) {
        ArrayList<ImagemapAction> actions = new ArrayList<>();
        actions.add(new MessageImagemapAction("->牡羊座", new ImagemapArea(0, 0, 250, 200)));
        actions.add(new MessageImagemapAction("->牡牛座", new ImagemapArea(250, 0, 275, 200)));
        actions.add(new MessageImagemapAction("->双子座", new ImagemapArea(525, 0, 290, 200)));
        actions.add(new MessageImagemapAction("->蟹座", new ImagemapArea(815, 0, 225, 200)));
        actions.add(new MessageImagemapAction("->獅子座", new ImagemapArea(0, 200, 250, 225)));
        actions.add(new MessageImagemapAction("->乙女座", new ImagemapArea(250, 200, 275, 225)));
        actions.add(new MessageImagemapAction("->天秤座", new ImagemapArea(525, 200, 290, 225)));
        actions.add(new MessageImagemapAction("->蠍座", new ImagemapArea(815, 200, 225, 225)));
        actions.add(new MessageImagemapAction("->射手座", new ImagemapArea(0, 425, 250, 206)));
        actions.add(new MessageImagemapAction("->山羊座", new ImagemapArea(250, 425, 275, 206)));
        actions.add(new MessageImagemapAction("->水瓶座", new ImagemapArea(525, 425, 290, 206)));
        actions.add(new MessageImagemapAction("->魚座", new ImagemapArea(815, 425, 225, 206)));

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(new TextMessage("あなたの星座を選んでください。"));
        messages.add(new ImagemapMessage(DigestBotController.createUri("/static/seizamap"), "This is alt text", new ImagemapBaseSize(631, 1040), actions));

        return new ReplyMessage(token, messages);
    }

    /**
     * 運勢を取得し、リプライメッセージを作成します
     *
     * @param token 返信用トークン
     * @param text  たぶん星座
     * @return 運勢のテキストメッセージ
     * @throws IOException
     */
    public ReplyMessage createResultReply(String token, String text) throws IOException {
        String result = getResponse().body().string();
        String parse = result.replaceFirst(getJST(), "data");

        Moshi moshi = new Moshi.Builder()
                .build();
        JsonAdapter<Fortune> jsonAdapter = moshi.adapter(Fortune.class);

        Fortune fortune = jsonAdapter.fromJson(parse);
        int constellationId = getConstellationId(text);
        Data data = fortune.getHoroscope().getData().get(constellationId);
        String content = data.getContent();
        String item = data.getItem();
        String money = Integer.toString(data.getMoney());
        String total = Integer.toString(data.getTotal());
        String job = Integer.toString(data.getJob());
        String color = data.getColor();
        String day = data.getDay();
        String love = Integer.toString(data.getLove());
        String rank = Integer.toString(data.getRank());
        String sign = data.getSign();

        TextMessage resulthoro = new TextMessage("今日の" + sign + "の運勢は…\n\n" + content + "\nラッキーアイテム:" + item + "\nラッキーカラー:" + color
                + "\n金運:" + money + "\n恋愛:" + love + "\n仕事:" + job + "\n総合運:" + total + "\n順位" + rank
        );

        return new ReplyMessage(token, resulthoro);
    }

    /**
     * リクエスト先にPOSTし、レスポンスを受け取ります
     *
     * @return レスポンス
     * @throws IOException
     */
    private Response getResponse() throws IOException {
        Request request = new Request.Builder()
                .url("http://api.jugemkey.jp/api/horoscope/free/" + getJST())
                .build();

        return new OkHttpClient().newCall(request).execute();
    }

    /**
     * 日本時間を取得します
     *
     * @return 日本時間
     */
    private String getJST() {
        TimeZone tz = TimeZone.getTimeZone("JST");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        simpleDateFormat.setTimeZone(tz);
        Date dates = new Date();

        return simpleDateFormat.format(dates);
    }

    /**
     * 星座のIDを返します
     *
     * @param constellation 星座
     * @return 星座のID
     */
    private int getConstellationId(String constellation) {
        return Arrays.asList(CONSTELLATIONS).indexOf(constellation);
    }
}
