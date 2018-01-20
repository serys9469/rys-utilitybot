package com.github.kiyohitonara.digestbot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WordSearch{
	
    public ArrayList<ArrayList<String>> getXMLWordsList(String word, String pageindex) throws  Exception{
    	
  		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList <String>>();


    	try{
    		org.w3c.dom.Document document= DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("http://public.dejizo.jp/NetDicV09.asmx/SearchDicItemLite?Dic=wpedia&Word=" + URLEncoder.encode(word, "UTF-8") + "&Scope=HEADWORD&Match=STARTWITH&Merge=AND&Prof=XHTML&PageSize=5&PageIndex="+pageindex);
    		NodeList title_list = document.getElementsByTagName("Title");
    		NodeList item_id_list = document.getElementsByTagName("ItemID");
    		for(int i = 0;i<title_list.getLength();i++)
    		{

    			ArrayList<String> sub = new ArrayList<String>();

    			//Node now_title = title_list.item(i);
    			Node now_item_id_list = item_id_list.item(i);
    			Node now_title_list = title_list.item(i);
    			sub.add(now_item_id_list.getTextContent());
    			sub.add(now_title_list.getTextContent());
    			System.out.println("id:" + now_item_id_list.getTextContent());
    			System.out.println("title:" + now_title_list.getTextContent());
    			list.add(sub);
    			

    		}

    	}catch(Exception e){
            throw e;
        }

        return list;   
    }


    public String getXMLWordMean(String item_id) throws  Exception{
    	
    	String meanText = "";

    	System.out.println("item_id:"+item_id);

    	try{
    		org.jsoup.nodes.Document document = Jsoup.connect("http://public.dejizo.jp/NetDicV09.asmx/GetDicItemLite?Dic=wpedia&Item="+item_id+"&Loc=&Prof=XHTML").get();

    		Elements elements =  document.select(".NetDicBody > div > *");
			
			for(Element element : elements) {

				if(element.tagName() == "h2")break;
				if(element.tagName() == "div")meanText += element.text();
				System.out.println(element.outerHtml());
			}
			
			if(meanText == "")
			{
				Elements elementsDetail =  document.select(".NetDicBody > div");
				for(Element element : elementsDetail) {

					meanText += element.text();
					System.out.println(element.outerHtml());
				}
				
				meanText += "で検索してみてね";

			}
				
			System.out.println("mean:" + meanText);
    		

    	}catch(Exception e){
            throw e;
        }

        return meanText;   
    }
}