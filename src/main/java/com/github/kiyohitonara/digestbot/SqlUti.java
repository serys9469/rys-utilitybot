package com.github.kiyohitonara.digestbot;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;

public class SqlUti {


    public Connection init() throws URISyntaxException, SQLException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", username, password);
    }

	public int flagSearch(String roomID) throws URISyntaxException, SQLException {

		int rowCount = 0;

		try{
			Class.forName("org.postgresql.Driver");
		}catch (ClassNotFoundException ex){
        	System.err.println("ClassNotFoundException.");
            ex.printStackTrace ();
        }
		Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        


        try {
            //-----------------
            // 接続
            //-----------------

            connection = init();

            //-----------------
            // SQLの発行
            //-----------------
            //ユーザー情報のテーブル
            statement = connection.prepareStatement("SELECT * FROM roulette WHERE room_id = ?",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.setString(1,roomID);


            resultSet = statement.executeQuery();
            resultSet.last();
			rowCount = resultSet.getRow();
			resultSet.beforeFirst();

            System.out.println("該当件数:" + rowCount);
            if(rowCount != 0)
            {
            	System.out.println("更新処理");
            	statement = connection.prepareStatement("UPDATE roulette SET roulette_flg = 0 WHERE room_id = ?");
            	statement.setString(1,roomID);
            	statement.executeUpdate();
            }
            	statement = connection.prepareStatement("INSERT INTO roulette (roulette_id,room_id, roulette_flg) values (nextval('roulette_id_seq'),?, 1)");
            	statement.setString(1,roomID);
            	statement.executeUpdate();

	        connection.close();
	        statement.close();
	        resultSet.close();

        } catch (SQLException e) {
            System.err.println("SQL failed.");
            e.printStackTrace ();
        }

        if(rowCount == 0){
			return 0;
		}
		else
		{
			return 1;
		}
	}

	public int insertItem(String roomID, String item_name) throws URISyntaxException, SQLException {
		try{
			Class.forName("org.postgresql.Driver");
		}catch (ClassNotFoundException ex){
        	System.err.println("ClassNotFoundException.");
            ex.printStackTrace ();
        }
        //仕様上0か1しかありえないので、フラグの方が適切かも
        int rowCount = 0;
		Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            //-----------------
            // 接続
            //-----------------
            connection = init();

            //-----------------
            // SQLの発行
            //-----------------
            //ユーザー情報のテーブル
            statement = connection.prepareStatement("SELECT * FROM roulette WHERE room_id = ? AND roulette_flg = 1",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.setString(1,roomID);


            resultSet = statement.executeQuery();
            resultSet.last();
			rowCount = resultSet.getRow();
			resultSet.beforeFirst();


            System.out.println("概要件数:" + rowCount);

            if(rowCount != 0)
            {
                resultSet.next();
            	int roulette_id = resultSet.getInt("roulette_id");
            	System.out.println("項目挿入処理");
            	statement = connection.prepareStatement("INSERT INTO roulette_item (roulette_item_id,roulette_id,item_name) values (nextval('roulette_item_id_seq'),?,?)");
            	statement.setInt(1,roulette_id);
            	statement.setString(2,item_name);
            	statement.executeUpdate();
            }
            

	        connection.close();
	        statement.close();
	        resultSet.close();

        } catch (SQLException e) {
            System.err.println("SQL failed.");
            e.printStackTrace ();
        }

        if(rowCount == 0){
			return 0;
		}
		else
		{
			return 1;
		}
	}
	public ArrayList<String> searchItems(String roomID) throws URISyntaxException, SQLException {
		try{
			Class.forName("org.postgresql.Driver");
		}catch (ClassNotFoundException ex){
        	System.err.println("ClassNotFoundException.");
            ex.printStackTrace ();
        }
        //仕様上0か1しかありえないので、フラグの方が適切かも
        int rowCount = 0;
        ArrayList<String> roulette_item = new ArrayList<String>();
		Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            //-----------------
            // 接続
            //-----------------
            connection = init();

            //-----------------
            // SQLの発行
            //-----------------
            //ユーザー情報のテーブル
            statement = connection.prepareStatement("SELECT * FROM roulette WHERE room_id = ? AND roulette_flg = 1",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.setString(1,roomID);


            resultSet = statement.executeQuery();
            resultSet.last();
			rowCount = resultSet.getRow();
			resultSet.beforeFirst();


            System.out.println("概要件数:" + rowCount);

            if(rowCount != 0)
            {
                resultSet.next();
            	int roulette_id = resultSet.getInt("roulette_id");
            	System.out.println("項目挿入処理");
            	statement = connection.prepareStatement("SELECT item_name FROM roulette_item WHERE roulette_id = ?");
            	statement.setInt(1,roulette_id);
            	resultSet = statement.executeQuery();
            	int i=0;
         		while(resultSet.next())
         		{
         			roulette_item.add(resultSet.getString("item_name"));
         			i++;
         		}
         		statement = connection.prepareStatement("UPDATE roulette SET roulette_flg = 0 WHERE roulette_id = ?");
            	statement.setInt(1,roulette_id);
            	statement.executeUpdate();
            }
            

	        connection.close();
	        statement.close();
	        resultSet.close();

        } catch (SQLException e) {
            System.err.println("SQL failed.");
            e.printStackTrace ();
        }
        return roulette_item;
	}


    public void conversation_change(String roomID, int conversation_flg) throws URISyntaxException, SQLException {

        int rowCount = 0;

        try{
            Class.forName("org.postgresql.Driver");
        }catch (ClassNotFoundException ex){
            System.err.println("ClassNotFoundException.");
            ex.printStackTrace ();
        }
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        


        try {
            //-----------------
            // 接続
            //-----------------

            connection = init();

            //-----------------
            // SQLの発行
            //-----------------
            //ユーザー情報のテーブル
            statement = connection.prepareStatement("SELECT * FROM conversation_ready WHERE room_id = ?",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.setString(1,roomID);


            resultSet = statement.executeQuery();
            resultSet.last();
            rowCount = resultSet.getRow();
            resultSet.beforeFirst();

            System.out.println("該当件数:" + rowCount);
            if(rowCount != 0)
            {
                if(conversation_flg == 1)
                {
                    statement = connection.prepareStatement("UPDATE conversation_ready SET conversation_flg = '1' WHERE room_id = ?");
                }
                else
                {
                    statement = connection.prepareStatement("UPDATE conversation_ready SET conversation_flg = '0' WHERE room_id = ?");
                }
                statement.setString(1,roomID);
                statement.executeUpdate();
            }
            else
            {
                if(conversation_flg == 1)
                {
                    statement = connection.prepareStatement("INSERT INTO conversation_ready (room_id, conversation_flg) values (?, '1')");
                }
                else
                {
                    statement = connection.prepareStatement("INSERT INTO conversation_ready (room_id, conversation_flg) values (?, '0')");
                }
                statement.setString(1,roomID);
                statement.executeUpdate();  
            }
            
            connection.close();
            statement.close();
            resultSet.close();

        } catch (SQLException e) {
            System.err.println("SQL failed.");
            e.printStackTrace ();
        }

        return;
    }

    public int search_conversationFlg(String roomID) throws URISyntaxException, SQLException {

        int rowCount = 0;
        int conversation_flg = 0;

        try{
            Class.forName("org.postgresql.Driver");
        }catch (ClassNotFoundException ex){
            System.err.println("ClassNotFoundException.");
            ex.printStackTrace ();
        }
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        


        try {
            //-----------------
            // 接続
            //-----------------

            connection = init();

            //-----------------
            // SQLの発行
            //-----------------
            //ユーザー情報のテーブル
            statement = connection.prepareStatement("SELECT * FROM conversation_ready WHERE room_id = ?",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.setString(1,roomID);


            resultSet = statement.executeQuery();
            resultSet.last();
            rowCount = resultSet.getRow();
            resultSet.beforeFirst();

            System.out.println("該当件数:" + rowCount);
            if(rowCount != 0)
            {
                resultSet.next();
                conversation_flg = resultSet.getInt("conversation_flg");
            }
            
            
            connection.close();
            statement.close();
            resultSet.close();

        } catch (SQLException e) {
            System.err.println("SQL failed.");
            e.printStackTrace ();
        }

        if(rowCount == 0)
        {
            return 0;
        }
        else
        {
            return conversation_flg;
        }
    }
}