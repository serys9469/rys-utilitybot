
package com.github.kiyohitonara.digestbot;

import java.sql.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class WeatherDatabaseAccess {

    private Connection conn;

    public void init() {
        try {
            URI dbUri = new URI(System.getenv("DATABASE_URL"));

            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

            conn = DriverManager.getConnection(dbUrl + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", username, password);
            System.out.println("データベースに接続しました。");
        }catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void insert(String roomID){
        try {
            Statement statement = conn.createStatement();
            String sql = "insert into weather_ready(room_id,single,ready_flg) values('"+roomID+"','0','0');";
            statement.executeUpdate(sql);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void updateSingle(String userID){
        try {
            Statement statement = conn.createStatement();
            String sql = "update weather_ready set single = '1' where room_id = '"+userID+"';";
            statement.executeUpdate(sql);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean searchID(String roomID){

        boolean exist = false;
        try{
            Statement statement = conn.createStatement();
            String sql = "select ready_flg from weather_ready where room_id = '"+roomID+"';";
            ResultSet result = statement.executeQuery(sql);
            if(result.next()){
                exist = true;
            }else{
                exist = false;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return exist;
    }

    public ArrayList<String> getSingleIDs(){

        ArrayList<String> singleIDs = new ArrayList<>();

        try{
            Statement statement = conn.createStatement();
            String sql = "select room_id from weather_ready where single = '1';";
            ResultSet result = statement.executeQuery(sql);
            while(result.next()){
                singleIDs.add(result.getString("room_id"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return singleIDs;
    }

    public int returnFlg(String roomID) {
        int readyFlg = 0;
        try{
            Statement statement = conn.createStatement();
            String sql = "select ready_flg from weather_ready where room_id = '"+roomID+"';";
            ResultSet result = statement.executeQuery(sql);
            while(result.next()){
                readyFlg = result.getInt("ready_flg");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        System.out.println("リターンフラグの値は:"+readyFlg);
        return readyFlg;
    }

    public void putFlg(String roomID, String mode){
        int flg = -1;
        switch (mode){
            case "on": flg = 1;
                break;
            case "off": flg = 0;
        }
        try {
            Statement statement = conn.createStatement();
            String sql = "update weather_ready set ready_flg = '"+flg+"' where room_id = '"+roomID+"';";
            statement.executeUpdate(sql);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void destroy() {
        try {
            conn.close();
            System.out.println("データベースとの接続を切断しました。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
