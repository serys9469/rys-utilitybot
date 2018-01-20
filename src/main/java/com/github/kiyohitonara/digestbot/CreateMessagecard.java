package com.github.kiyohitonara.digestbot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static java.lang.System.out;

/**
 * Created by hoshinoeiko on 2017/01/21.
 */
public class CreateMessagecard {
    public static void write(String message,String userID,String filepath) throws Exception{
        BufferedImage img = ImageIO.read(new File(filepath));
        Graphics2D gr = img.createGraphics();

        out.println(message+"+をメッセージにします");

        /*Font font = new Font("ＭＳ ゴシック", Font.BOLD, 40);
        gr.setFont(font);*/

        out.println("set image for createmessagecard!");
        gr.setColor(new Color(0,0,0));
        gr.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        gr.drawString(message, 300,300);

        gr.dispose();
        ImageIO.write(img, "jpg", new File("/tmp/"+userID+".jpg"));
    }
}
