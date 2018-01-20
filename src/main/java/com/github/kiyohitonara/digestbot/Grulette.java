package com.github.kiyohitonara.digestbot;

import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;
import java.lang.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;



public class Grulette{  
	public void createFrame(String[] items,String dirName){
		
		for(int i=0;i<items.length;i++)
		{
			if(items[i].length() > 10)
			{
				items[i] = items[i].substring(0,9) + "...";
			}
		}

		Grulette roulette = new Grulette();
		roulette.makePicture(items,dirName);
		roulette.makeRoulette(items,dirName);
	}
	public void makePicture(String[] items,String dirName){
		boolean result;
		int width, height;
		String outname = dirName + "/template.jpg";
		BufferedImage img = null;
		width = 500;
		height = 700;
		img = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D out = img.createGraphics();
		out.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		out.setBackground(Color.WHITE);
		out.clearRect(0, 0, width, height);
		try{
			result = ImageIO.write( img, "jpg", new File(outname));
		}catch(IOException e){
		}
	}
	public void makeRoulette(String[] items,String dirName){
		String s1 = dirName + "/frame";
		String s2 = ".jpg";
		int number = 0;
		Random rnd = new Random();
        int r = rnd.nextInt(72);
        int iteration = 280+r;
        long flashStart = 0;
        int resultNum = -1;
		for(int i=0;i<iteration;i++){
			System.out.println(i + "回目");
			number = i;
			if( (number != 0 && number <37) || number == iteration-100 ||number == iteration-100+1)
			{
				makeFrames(items, dirName + "/template.jpg",s1+String.format("%03d", i)+s2,number,iteration,"rotate",flashStart);
			}
			else if(number>iteration-100+1)
			{
				makeFrames(items,s1+String.format("%03d", i-2)+s2,s1+String.format("%03d", i)+s2,number,iteration,"rotate",flashStart);
			}
			else if(number >= 37)
			{
				makeFrames(items,s1+String.format("%03d", i%36+1)+s2,s1+String.format("%03d", i)+s2,number,iteration,"rotate",flashStart);
			}
			else if(number == 0)
			{
				makeFrames(items, dirName + "/template.jpg",s1+String.format("%03d", i)+s2,number,iteration,"rotate",flashStart);
			}
		}
	}
	public void makeFrames(String[]items, String inputPath, String outputPath , int number, int iteration, String mode, long flashStart){
		boolean isWrite = false;
		int width = 0;
		int height = 0;
		BufferedImage sourceImage = null;
		BufferedImage outputImage = null ;
		try {
			File inputFile = new File(inputPath);
			sourceImage = ImageIO.read(inputFile);
			width = sourceImage.getWidth();
			height = sourceImage.getHeight();
			outputImage = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
			Graphics2D out = outputImage.createGraphics();
			out.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			AffineTransform at = new AffineTransform();
			double rotateNum = 0.0;
			int stopCount = iteration-100;

			if(number<37 && number != 0)
			{
				rotateNum = 10*(number-1) + 1;
				makeBoard(rotateNum, items, out, at, outputPath, outputImage,stopCount,number);
	
			}
			else if(number == stopCount || number == stopCount + 1)
			{
				rotateNum = (10*(stopCount-1))%360 + 1;
				makeBoard(rotateNum, items, out, at, outputPath, outputImage,stopCount,number);
			}
			else
			{
				out.setBackground(Color.WHITE);
				out.drawImage(sourceImage,at,null);
				ImageIO.write(outputImage, "jpeg", new File(outputPath));;
			}

			out.setBackground(Color.WHITE);
			out.clearRect(0, 0, width, height);

		} catch (IOException e) {
		}
	}
	public int makeBoard(double rotateNum,String[] items, Graphics2D out, AffineTransform at, String outputPath, BufferedImage outputImage,int stopCount,int number){
		boolean hit = false;
		//System.out.println(rotateNum);
		int resultNum = -1;
		try{
			out.setBackground(Color.WHITE);
			int width = 500;
			int height = 700;
			int length = items.length;
			out.clearRect(0, 0, width, height);
			at.setToRotation(Math.toRadians(rotateNum), 250, 320);
			out.setTransform(at);
		    out.setColor(Color.BLACK);
		    BasicStroke wideStroke = new BasicStroke(7.0f);
   			out.setStroke(wideStroke);
			out.setColor(Color.RED);
		    out.setColor(Color.BLACK);
    		FontMetrics fontMetrics = out.getFontMetrics();
			Arc2D.Double baseArc = new Arc2D.Double( 30, 100, 440, 440, 0, 360, Arc2D.PIE);
		    out.fill(baseArc);

		    Arc2D.Double[] arcs = new Arc2D.Double[length];
		    Random rndo = new Random();
			for(int k = 0; k<length; k++){
		    	if(k == length-1){
		    		if(k%4 == 0){
			    		out.setColor(Color.YELLOW);
			    	}else if(k%4 == 1){
			    		out.setColor(Color.YELLOW);
			    	}else if(k%4 == 2){
			    		out.setColor(Color.YELLOW);
			   		}else if(k%4 == 3){
			   			out.setColor(Color.GREEN);
			   		}
			    }else if(k%4 == 0){
			   		out.setColor(Color.RED);
			   	}else if(k%4 == 1){
			   		out.setColor(new Color(42, 197, 199));
			   	}else if(k%4 == 2){
		    		out.setColor(Color.YELLOW);
		    	}else if(k%4 == 3){
		    		out.setColor(Color.GREEN);
		    	}
			   	double positionTheta = 90.0-(360.0/length/2)+(360.0/length)*k;
			   	double arctheta = 360.0/length;
			   	arcs[k] = new Arc2D.Double( 50, 120, 400, 400, positionTheta, arctheta, Arc2D.PIE);   	
			    out.fill(arcs[k]);
			   	BasicStroke nomalStroke = new BasicStroke(6.0f);
	   			out.setStroke(nomalStroke);
	   			out.setColor(Color.WHITE);
		    	out.draw(arcs[k]);//太い?		    	
				at.setToRotation(Math.toRadians(rotateNum), 250, 320);
			   	out.setTransform(at);
			}
		    for(int m = 0; m<length; m++){	    	
			    at.setToRotation(Math.toRadians((int)(rotateNum)-360.0/length*m), 250, 320);
			   	out.setTransform(at);
			   	out.setColor(Color.BLACK);
			    Font font = new Font("ＭＳ ゴシック", Font.BOLD, 3);//ここは無関係
			    int i = 40;
    			font = new Font("ＭＳ ゴシック", Font.BOLD, 40);
    			out.setFont(font);
    			fontMetrics = out.getFontMetrics();


    			if(length == 1)
    			{
    				while(fontMetrics.stringWidth(items[m])>100){
				   		font = new Font("ＭＳ ゴシック", Font.BOLD, i);
	    				out.setFont(font);
	    				fontMetrics = out.getFontMetrics();
	    				i--;
	   				}
    			}
    			else
    			{
	    			while(fontMetrics.stringWidth(items[m])>160*Math.sqrt(2*(1-Math.cos(2*Math.PI/length)))){
				   		font = new Font("ＭＳ ゴシック", Font.BOLD, i);
	    				out.setFont(font);
	    				fontMetrics = out.getFontMetrics();
	    				i--;
	   				}
   				}

		    	out.drawString(items[m], 250-fontMetrics.stringWidth(items[m])/2, 180);
		    }
			out.setColor(Color.RED);
		    at.setToRotation(0, 250, 320);
		    out.setTransform(at);
    		Font font = new Font("ＭＳ ゴシック", Font.BOLD, 40);
    		out.setFont(font);
    		fontMetrics = out.getFontMetrics();
		    out.drawString("ぐるぐるルーレット♪♪",250-fontMetrics.stringWidth("ぐるぐるルーレット♪♪")/2, 60);
			

		    
			/*if(number%30>15 && number>=stopCount){
				out.setColor(Color.BLACK);
				out.draw(arcs[resultNum]);
			}else if(number%30<=15 && number>=stopCount){
				out.setColor(Color.WHITE);
				out.draw(arcs[resultNum]);
			}*/

		  	//きれいに現在のパターンで計算して上書き(kent)もっくんの処理はとりあえず残しとく
		   	/*if(rotateNum > 180)
		   	{
		   		resultNum = (int)(  ( ( (rotateNum) - 180.0)-180.0/length)/(360.0/length) );
		   	}
		   	else if(rotateNum <= 180)
		   	{
		   		resultNum = (int)(  ( ( (rotateNum) + 180.0)-180.0/length)/(360.0/length) );
		   	}*/

	
			at.setToRotation(0, 250, 320);
		   	out.setTransform(at);		    	
		

		    if(number>=stopCount){

		    	double big = Math.toRadians((10*(stopCount-1))%360 + 1);
			    double cosbig = Math.cos(Math.PI/2.0-big);
				double sinbig = Math.sin(Math.PI/2.0-big);
			    double x = (250+198*cosbig);
			    double y = (320+198*sinbig);
			    Rectangle2D.Double cir_ = new Rectangle2D.Double( x, y, 1, 1);
				out.setColor(Color.BLUE);
				out.fill(cir_);

			    for(int l = 0; l<length; l++){
			    	System.out.println("x=" + x + "とy=" + y);
			    	if(arcs[l].contains(cir_) == true){
						resultNum = l;
				   	break;
				   	}
			  	}

			  	at.setToRotation(Math.toRadians(rotateNum), 250, 320);
		  	 	out.setTransform(at);

		  	 	BasicStroke answerArcStroke = new BasicStroke(6.0f);
	   			out.setStroke(answerArcStroke);
				if(number%2 == 0){
					out.setColor(Color.BLACK);
					out.draw(arcs[resultNum]);
				}else if(number%2 == 1){
					out.setColor(Color.WHITE);
					out.draw(arcs[resultNum]);
				}

				at.setToRotation(0, 250, 320);
		   		out.setTransform(at);	

				out.setColor(Color.RED);
			   	int s = 40;
			 
			   	
			   	System.out.println(resultNum);
		    	while(fontMetrics.stringWidth(items[resultNum]+"で決定したのだ!!!")>500){
		    		font = new Font("ＭＳ ゴシック", Font.BOLD, s);
			    	out.setFont(font);
			    	fontMetrics = out.getFontMetrics();
			   		s--;
			   	}
				out.drawString(items[resultNum]+"で決定したのだ!!!", 250-fontMetrics.stringWidth(items[resultNum]+"で決定したのだ!!!")/2, 650);
			

			
			
		 	
		   	at.setToRotation(Math.toRadians(rotateNum), 250, 320);
		   	out.setTransform(at);
		   	BasicStroke widestStroke = new BasicStroke(4.0f);
	   		out.setStroke(widestStroke);
			at.setToRotation(0, 250, 320);
		    out.setTransform(at);
			}
			at.setToRotation(0, 250, 320);
		   	out.setTransform(at);	
			int[] pointX = {230,250,270};
		    int[] pointY = {550,500,550};
		    Polygon polygon = new Polygon(pointX,pointY , 3);
		    out.setColor(Color.RED);
		    out.fill(polygon);
		    out.setColor(Color.BLACK);
		    out.draw(polygon);
		    int[] pointXO = {228,250,272};
		    int[] pointYO = {550,498,550};
		    Polygon polygonOut = new Polygon(pointXO,pointYO , 3);
		    BasicStroke fineStroke = new BasicStroke(2.0f);
	   		out.setStroke(fineStroke);
			out.setColor(Color.WHITE);
		    out.draw(polygonOut);

		    Ellipse2D centerO = new Ellipse2D.Double( 230, 300, 40, 40);
		    out.setColor(Color.BLACK);
		    out.fill(centerO);
		    Ellipse2D centerI = new Ellipse2D.Double( 240, 310, 20, 20);
			out.setColor(Color.WHITE);
		    out.draw(centerI);
		    out.drawImage(null,at,null);
		    boolean isWrite = ImageIO.write(outputImage, "jpeg", new File(outputPath));

		}catch(IOException e){
		}
		return resultNum;
	}
	public void makeMp4(String imgdirName,String videodirName){
	    try{
	    	
	      Runtime runtime = Runtime.getRuntime();
	      System.out.println("動画作成成功ルートパス" + imgdirName + videodirName);

	      Process p = runtime.exec("ffmpeg -f image2 -r 30 -i " + imgdirName + "/frame%03d.jpg" + " -r 30 -vb 4000k -an -pix_fmt yuv420p " + videodirName);
	      printInputStream(p.getInputStream());
	      printInputStream(p.getErrorStream()); 
	      System.out.println(p.waitFor());   

	    }catch(Exception ex){
	    }
	}

    public static void printInputStream(InputStream is) throws IOException {
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    for (;;) {
	    String line = br.readLine();
	    if (line == null) break;
	    System.out.println(line);
		}
    }
  
}