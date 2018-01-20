package com.github.kiyohitonara.digestbot;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.*;

//rssの方で使う
import java.net.URL;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;


import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.*;
import java.lang.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.PushMessage;

import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.message.VideoMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.AudioMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.VideoMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;

import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class Weather{
	
    public String[] bringWeatherInfo(String pref, int areaNum) throws  Exception{


        String indexPref = pref;
        
        System.out.println("indexPref is"+indexPref);
        String xmlNum = "";

        switch(indexPref){
            case "北海道":
                xmlNum = "01";
                break;
            case "青森":
                xmlNum = "02";
                break;
            case "岩手":
                xmlNum = "03";
                break;
            case "宮城":
                xmlNum = "04";
                break;
            case "秋田":
                xmlNum = "05";
                break;
            case "山形":
                xmlNum = "06";
                break;
            case "福島":
                xmlNum = "07";
                break;
            case "茨城":
                xmlNum = "08";
                break;
            case "栃木":
                xmlNum = "09";
                break;
            case "群馬":
                xmlNum = "10";
                break;
            case "埼玉":
                xmlNum = "11";
                break;
            case "千葉":
                xmlNum = "12";
                break;
            case "東京":
                xmlNum = "13";
                break;
            case "神奈川":
                xmlNum = "14";
                break;
            case "新潟":
                xmlNum = "15";
                break;
            case "富山":
                xmlNum = "16";
                break;
            case "石川":
                xmlNum = "17";
                break;
            case "福井":
                xmlNum = "18";
                break;
            case "山梨":
                xmlNum = "19";
                break;
            case "長野":
                xmlNum = "20";
                break;
            case "岐阜":
                xmlNum = "21";
                break;
            case "静岡":
                xmlNum = "22";
                break;
            case "愛知":
                xmlNum = "23";
                break;
            case "三重":
                xmlNum = "24";
                break;
            case "滋賀":
                xmlNum = "25";
                break;
            case "京都":
                xmlNum = "26";
                break;
            case "大阪":
                xmlNum = "27";
                break;
            case "兵庫":
                xmlNum = "28";
                break;
            case "奈良":
                xmlNum = "29";
                break;
            case "和歌山":
                xmlNum = "30";
                break;
            case "鳥取":
                xmlNum = "31";
                break;
            case "島根":
                xmlNum = "22";
                break;
            case "岡山":
                xmlNum = "33";
                break;
            case "広島":
                xmlNum = "34";
                break;
            case "山口":
                xmlNum = "35";
                break;
            case "徳島":
                xmlNum = "36";
                break;
            case "香川":
                xmlNum = "37";
                break;
            case "愛媛":
                xmlNum = "38";
                break;
            case "高知":
                xmlNum = "39";
                break;
            case "福岡":
                xmlNum = "40";
                break;
            case "佐賀":
                xmlNum = "41";
                break;
            case "長崎":
                xmlNum = "42";
                break;
            case "熊本":
                xmlNum = "43";
                break;
            case "大分":
                xmlNum = "44";
                break;
            case "宮崎":
                xmlNum = "45";
                break;
            case "鹿児島":
                xmlNum = "46";
                break;
            case "沖縄":
                xmlNum = "47";
                break;
            default:
                System.out.println("一致する都道府県がありません");
        }

        
        

        try{

            Document document= DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("http://www.drk7.jp/weather/xml/"+xmlNum+".xml");
           

            Node rootElement = document.getDocumentElement();

            NodeList firstList = rootElement.getChildNodes();
            //firstList(0)...
            //Node title = firstList.item(1);
            //System.out.println(title.getTextContent());
            //Node desc = firstList.item(5);
            

            Node prefNode = firstList.item(13);
            //System.out.println(prefNode.getNodeName());//pref
            NodeList areaList = prefNode.getChildNodes();

            //System.out.println("areaListの個数は"+areaList.getLength());

            int a = 2+5*9;
            String weatherInfo[] = new String[a];//(県)地方からすべての地方の5日分の天気予報をかえす

            int j = 0;

            weatherInfo[j] = indexPref;
            j++;


            int l = areaNum*2-1;
        //for(int j = 0; j<(areaList.getLength()-1)/2; j++){

            Node area = areaList.item(l);
            NamedNodeMap attributes = area.getAttributes();
            Node id = attributes.getNamedItem("id");
            //System.out.println(id.getNodeName());//"id"
            //System.out.println(id.getNodeValue());//"東部","西部"<-二週目で入る
            String areaName = id.getNodeValue();

            

            System.out.println(areaName);

                weatherInfo[j] = areaName;
                j++;

            //areaAndNum.put(areaName, j);

            NodeList infoList = area.getChildNodes();


                int k = 3;
                


            for(int i = 0; i<5;i++){


                Node info = infoList.item(k);
                //System.out.println("kの値は");
                //System.out.println(k);


                NodeList detailList = info.getChildNodes();

                //Node weatherNode = detailList.item(0);
                NamedNodeMap weatherAttributes = info.getAttributes();
                Node dateId = weatherAttributes.getNamedItem("date");
                //System.out.println(dateId.getNodeValue());//"2016..."
                //System.out.println(dateId.getNodeName());//date
                String date = dateId.getNodeValue();

                System.out.println(date);

                weatherInfo[j] = date;
                j++;


                Node weathNode = detailList.item(1);

                String weather = weathNode.getTextContent();

                System.out.println(weather);



                weatherInfo[j] = weather;
                j++;


                Node tempratureNode;
                Node rainFallChanceNode;

                if(i<2){
                tempratureNode = detailList.item(9);

                rainFallChanceNode = detailList.item(11);

                }else{
                tempratureNode = detailList.item(5);
                rainFallChanceNode = detailList.item(7);
                }

                NodeList tempratureList = tempratureNode.getChildNodes();

                Node maxNode = tempratureList.item(1);
                Node minNode = tempratureList.item(3);

                
                String max = maxNode.getTextContent();
                String min = minNode.getTextContent();

                System.out.println(max);
                System.out.println(min);



        
                weatherInfo[j] = max;
                j++;

            
                weatherInfo[j] = min;
                j++;


                NodeList rainfallchanceList = rainFallChanceNode.getChildNodes();

                Node rainfallchance0006Node = rainfallchanceList.item(1);
                Node rainfallchance0612Node = rainfallchanceList.item(3);
                Node rainfallchance1218Node = rainfallchanceList.item(5);
                Node rainfallchance1824Node = rainfallchanceList.item(7);

                System.out.println(rainfallchance0006Node.getTextContent());//0
                System.out.println(rainfallchance0612Node.getTextContent());//40
                System.out.println(rainfallchance1218Node.getTextContent());//40
                System.out.println(rainfallchance1824Node.getTextContent());//50

                String rainfallchance0006 = rainfallchance0006Node.getTextContent();
                String rainfallchance0612 = rainfallchance0612Node.getTextContent();
                String rainfallchance1218 = rainfallchance1218Node.getTextContent();
                String rainfallchance1824 = rainfallchance1824Node.getTextContent();

                
                weatherInfo[j] = rainfallchance0006;
                j++;
               
                weatherInfo[j] = rainfallchance0612;
                j++;
    
                weatherInfo[j] = rainfallchance1218;
                j++;
           
                weatherInfo[j] = rainfallchance1824;
                j++;

                String imagePath = getWeatherImagePath(weather);

                weatherInfo[j] = imagePath;
                j++;

                k = k+2;
            }
            //l = l+2;
        //}

        return weatherInfo;//県名、地域名、予報、の順に並んだ配列

        }catch(Exception e){
            throw e;
        }
    }
    public ArrayList<String> getArea( String text) throws  Exception{
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

        String indexPref = "";

        for(String pref : arrPref){
            if (text.indexOf(pref) != -1) {
            indexPref = pref;
            }
        }

        System.out.println(indexPref);

        String rssNum = "";
        String xmlNum = "";

        switch(indexPref){
            case "北海道":
                xmlNum = "01";
                break;
            case "青森":
                xmlNum = "02";
                break;
            case "岩手":
                xmlNum = "03";
                break;
            case "宮城":
                xmlNum = "04";
                break;
            case "秋田":
                xmlNum = "05";
                break;
            case "山形":
                xmlNum = "06";
                break;
            case "福島":
                xmlNum = "07";
                break;
            case "茨城":
                xmlNum = "08";
                break;
            case "栃木":
                xmlNum = "09";
                break;
            case "群馬":
                xmlNum = "10";
                break;
            case "埼玉":
                xmlNum = "11";
                break;
            case "千葉":
                xmlNum = "12";
                break;
            case "東京":
                xmlNum = "13";
                break;
            case "神奈川":
                xmlNum = "14";
                break;
            case "新潟":
                xmlNum = "15";
                break;
            case "富山":
                xmlNum = "16";
                break;
            case "石川":
                xmlNum = "17";
                break;
            case "福井":
                xmlNum = "18";
                break;
            case "山梨":
                xmlNum = "19";
                break;
            case "長野":
                xmlNum = "20";
                break;
            case "岐阜":
                xmlNum = "21";
                break;
            case "静岡":
                xmlNum = "22";
                break;
            case "愛知":
                xmlNum = "23";
                break;
            case "三重":
                xmlNum = "24";
                break;
            case "滋賀":
                xmlNum = "25";
                break;
            case "京都":
                xmlNum = "26";
                break;
            case "大阪":
                xmlNum = "27";
                break;
            case "兵庫":
                xmlNum = "28";
                break;
            case "奈良":
                xmlNum = "29";
                break;
            case "和歌山":
                xmlNum = "30";
                break;
            case "鳥取":
                xmlNum = "31";
                break;
            case "島根":
                xmlNum = "32";
                break;
            case "岡山":
                xmlNum = "33";
                break;
            case "広島":
                xmlNum = "34";
                break;
            case "山口":
                xmlNum = "35";
                break;
            case "徳島":
                xmlNum = "36";
                break;
            case "香川":
                xmlNum = "37";
                break;
            case "愛媛":
                xmlNum = "38";
                break;
            case "高知":
                xmlNum = "39";
                break;
            case "福岡":
                xmlNum = "40";
                break;
            case "佐賀":
                xmlNum = "41";
                break;
            case "長崎":
                xmlNum = "42";
                break;
            case "熊本":
                xmlNum = "43";
                break;
            case "大分":
                xmlNum = "44";
                break;
            case "宮崎":
                xmlNum = "45";
                break;
            case "鹿児島":
                xmlNum = "46";
                break;
            case "沖縄":
                xmlNum = "47";
                break;
            default:
                System.out.println("一致する都道府県がありません");
        }

        
        

        try{

            ArrayList<String> arrArea = new ArrayList<String>();


            Document document= DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("http://www.drk7.jp/weather/xml/"+xmlNum+".xml");
           

            Node rootElement = document.getDocumentElement();

            NodeList firstList = rootElement.getChildNodes();
            //firstList(0)...
            //Node title = firstList.item(1);
            //System.out.println(title.getTextContent());
            //Node desc = firstList.item(5);
            

            Node pref = firstList.item(13);
            System.out.println(pref.getNodeName());//pref
            NamedNodeMap prefAttributes = pref.getAttributes();
            Node prefId = prefAttributes.getNamedItem("id");
            String prefName = prefId.getNodeValue();
            arrArea.add(prefName);//県あり

            NodeList areaList = pref.getChildNodes();

            //System.out.println("areaListの個数は"+areaList.getLength());

            int a = 5*7;
            int b = (areaList.getLength()-1)/2;
            String weatherInfo[][] = new String[b][a];//(県)地方からすべての地方の5日分の天気予報をかえす
            //HashMap<String,Integer> areaAndNum = new HashMap<String,Integer>();//県から地方(&番号)を返す
            
            int l = 1;
        for(int j = 0; j<(areaList.getLength()-1)/2; j++){

            Node area = areaList.item(l);
            NamedNodeMap attributes = area.getAttributes();
            Node areaId = attributes.getNamedItem("id");
            System.out.println(areaId.getNodeName());//"id"
            System.out.println(areaId.getNodeValue());//"東部","西部"<-二週目で入る
            String areaName = areaId.getNodeValue();

            arrArea.add(areaName);

            NodeList infoList = area.getChildNodes();


                int k = 3;


            for(int i = 0; i<5;i++){


                Node info = infoList.item(k);
                //System.out.println("kの値は");
                //System.out.println(k);


                NodeList detailList = info.getChildNodes();

                Node weatherNode = detailList.item(1);
                //System.out.println(weatherNode.getTextContent());

                String weather = weatherNode.getTextContent();


                int t = 0*(i+1);
                weatherInfo[j][t] = weather;



                Node tempratureNode;
                Node rainFallChanceNode;

                if(i<2){
                tempratureNode = detailList.item(9);

                rainFallChanceNode = detailList.item(11);

                }else{
                tempratureNode = detailList.item(5);
                rainFallChanceNode = detailList.item(7);
                }

                NodeList tempratureList = tempratureNode.getChildNodes();

                Node maxNode = tempratureList.item(1);
                Node minNode = tempratureList.item(3);

                
                String max = maxNode.getTextContent();
                String min = minNode.getTextContent();


                t = 1*(i+1);
                weatherInfo[j][t] = max;

                t = 2*(i+1);
                weatherInfo[j][t] = min;



                NodeList rainfallchanceList = rainFallChanceNode.getChildNodes();

                Node rainfallchance0006Node = rainfallchanceList.item(1);
                Node rainfallchance0612Node = rainfallchanceList.item(3);
                Node rainfallchance1218Node = rainfallchanceList.item(5);
                Node rainfallchance1824Node = rainfallchanceList.item(7);

                System.out.println(rainfallchance0006Node.getTextContent());//0
                System.out.println(rainfallchance0612Node.getTextContent());//40
                System.out.println(rainfallchance1218Node.getTextContent());//40
                System.out.println(rainfallchance1824Node.getTextContent());//50

                String rainfallchance0006 = rainfallchance0006Node.getTextContent();
                String rainfallchance0612 = rainfallchance0612Node.getTextContent();
                String rainfallchance1218 = rainfallchance1218Node.getTextContent();
                String rainfallchance1824 = rainfallchance1824Node.getTextContent();

                t = 3*(i+1);
                weatherInfo[j][t] = rainfallchance0006;

                t = 4*(i+1);
                weatherInfo[j][t] = rainfallchance0612;

                t = 5*(i+1);
                weatherInfo[j][t] = rainfallchance1218;

                t = 6*(i+1);
                weatherInfo[j][t] = rainfallchance1824;



                k = k+2;
            }
            l = l+2;
        }

        //System.out.println("indexPrefは"+indexPref);

        String prefImagePath = getPrefImagePath(indexPref);
        arrArea.add(prefImagePath);

        arrArea.add(indexPref);//県なし


        

        return arrArea;//せいぜい1~4コの地域名を返してるだけ

        }catch(Exception e){
            throw e;
        }
    }

    public String getWeatherImagePath(String weather){
        

        String path1 = "/static/img/";
        String path2 = "";

        if(weather.contains("晴れ") && weather.contains("くもり")){
            path2 = "cloudyAndSunny.jpeg";
        }else if(weather.contains("晴れ")){
            path2 = "sunny.jpeg";
        }else if(weather.contains("雪")){
            path2 = "cloudyAndSnowy.jpeg";
        }else if(weather.contains("雨")){
            path2 = "cloudyAndRainy.jpeg";
        }else if(weather.contains("くもり")){
            path2 = "cloudy.jpeg";
        }else if(weather.contains("風")){
            path2 = "wind.jpeg";
        }else if(weather.contains("雷")){
            path2 = "cloudyAndThunder.jpeg";
        }else{
            path2 = "cloud.jpeg";
        }

        String path = path1+path2;

        return path;
    }

    public String getPrefImagePath(String indexPref){
        

        String path1 = "/static/img/";
        String path2 = "pref_";
        String prefNum = "";

        switch(indexPref){
            case "北海道":
                prefNum = "01";
                break;
            case "青森":
                prefNum = "02";
                break;
            case "岩手":
                prefNum = "03";
                break;
            case "宮城":
                prefNum = "04";
                break;
            case "秋田":
                prefNum = "05";
                break;
            case "山形":
                prefNum = "06";
                break;
            case "福島":
                prefNum = "07";
                break;
            case "茨城":
                prefNum = "08";
                break;
            case "栃木":
                prefNum = "09";
                break;
            case "群馬":
                prefNum = "10";
                break;
            case "埼玉":
                prefNum = "11";
                break;
            case "千葉":
                prefNum = "12";
                break;
            case "東京":
                prefNum = "13";
                break;
            case "神奈川":
                prefNum = "14";
                break;
            case "新潟":
                prefNum = "15";
                break;
            case "富山":
                prefNum = "16";
                break;
            case "石川":
                prefNum = "17";
                break;
            case "福井":
                prefNum = "18";
                break;
            case "山梨":
                prefNum = "19";
                break;
            case "長野":
                prefNum = "20";
                break;
            case "岐阜":
                prefNum = "21";
                break;
            case "静岡":
                prefNum = "22";
                break;
            case "愛知":
                prefNum = "23";
                break;
            case "三重":
                prefNum = "24";
                break;
            case "滋賀":
                prefNum = "25";
                break;
            case "京都":
                prefNum = "26";
                break;
            case "大阪":
                prefNum = "27";
                break;
            case "兵庫":
                prefNum = "28";
                break;
            case "奈良":
                prefNum = "29";
                break;
            case "和歌山":
                prefNum = "30";
                break;
            case "鳥取":
                prefNum = "31";
                break;
            case "島根":
                prefNum = "32";
                break;
            case "岡山":
                prefNum = "33";
                break;
            case "広島":
                prefNum = "34";
                break;
            case "山口":
                prefNum = "35";
                break;
            case "徳島":
                prefNum = "36";
                break;
            case "香川":
                prefNum = "37";
                break;
            case "愛媛":
                prefNum = "38";
                break;
            case "高知":
                prefNum = "39";
                break;
            case "福岡":
                prefNum = "40";
                break;
            case "佐賀":
                prefNum = "41";
                break;
            case "長崎":
                prefNum = "42";
                break;
            case "熊本":
                prefNum = "43";
                break;
            case "大分":
                prefNum = "44";
                break;
            case "宮崎":
                prefNum = "45";
                break;
            case "鹿児島":
                prefNum = "46";
                break;
            case "沖縄":
                prefNum = "47";
                break;
            default:
                System.out.println("一致する都道府県がありません");
        }

        String path = path1+path2+prefNum+".jpeg";

        return path;
    }
}