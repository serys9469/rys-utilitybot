package com.github.kiyohitonara.digestbot;

import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.System.out;

/**
 * Created by hoshinoeiko on 2017/01/12.
 *
 */
@Configuration
public class Postgres {
    private static String filepath;
    public static int status;

    private static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", username, password);
    }


    //statement=1（「メッセージカード」と発言した時のfirst insert）
    public static void  insertTo(String insertuserid)throws SQLException{
        Connection conn = null;
        try{
            conn = getConnection();
            out.println("connection success!");

            Statement stmt = conn.createStatement(1004,1007);

            String sql="SELECT * FROM messagecard WHERE from_userid='"+insertuserid+"' AND status=1;";
            ResultSet rs=stmt.executeQuery(sql);
            rs.last();
            int rownum=rs.getRow();//status=1の行数を数えた
            rs.beforeFirst();
            rs.close();
            stmt.close();


            if(rownum>=1) {
                Statement stmt1 = conn.createStatement();
                String sql2= "UPDATE messagecard SET status=0 WHERE status=1 AND from_userid='"+insertuserid+"';";
                stmt1.executeUpdate(sql2);
                stmt1.close();
            }

            String sql3 = "insert into messagecard (cardid,templateid,textmessage,save_url,password,createtime,updatetime,sendtime,to_roomid,from_userid,status)" +
                    "values(nextval('cardid_seqe'),null,null,null,null,now(),null,null,null,'"+insertuserid+"',1);";
            Statement stmt2 = conn.createStatement();
            stmt2.executeUpdate(sql3);
            stmt2.close();


        }
        catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                out.println("SQLException:" + e.getMessage());
            }
        }

    }
    //statement=2　（カードの種類選択）
    public static void  Update_choosecard(String insertuserid,String inserttemplatename)throws SQLException{
        Connection conn = null;
        try{
            conn = getConnection();
            out.println("connection success!");

            /*他にSTATUS=2が存在していればSTATUS=0にする処理の記述が必要
            * EX)00002 この場合は２を削除しない
            *    00222 この場合、最新以外のSTATUS=2は削除
            *    02221 この場合、１以外は削除*/

            //template tablからID検索
            Statement stmt3 = conn.createStatement(1004,1007);
            String sql3 = "SELECT * FROM template WHERE templatename='"+inserttemplatename+"';";
            ResultSet rs3 =stmt3.executeQuery(sql3);
            rs3.next();
            int code = rs3.getInt("templateid");
            rs3.close();
            stmt3.close();
            //first choose
            String sql5 = "UPDATE messagecard SET status=2,templateid="+code+",updatetime=now() WHERE status=1 AND from_userid='"+insertuserid+"';";
            Statement stmt5 = conn.createStatement();
            stmt5.executeUpdate(sql5);
            stmt5.close();

        }
        catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                out.println("SQLException:" + e.getMessage());
            }
        }

    }
    //statement=3(送るメッセージの格納)
    public static void Update_textmessage(String insertuserid,String insertmessage)throws SQLException{
        Connection conn = null;
        try{
            conn = getConnection();
            out.println("connection success!");
          /*  Statement stmt = conn.createStatement(1004,1007);

            String sql="SELECT * FROM messagecard WHERE from_userid='"+insertuserid+"' AND status=3;";
            ResultSet rs=stmt.executeQuery(sql);
            rs.last();
            int rownum=rs.getRow();//status=1の行数を数えた
            rs.beforeFirst();
            rs.close();
            stmt.close();


            if(rownum>=1) {
                Statement stmt1 = conn.createStatement();
                String sql2= "UPDATE messagecard SET status=0 WHERE status=2 AND from_userid='"+insertuserid+"';";
                stmt1.executeUpdate(sql2);
                stmt1.close();
            }*/

            String sql3 ="UPDATE messagecard SET status=3,updatetime=now(),textmessage='"+insertmessage
                    +"' WHERE status=2 AND from_userid='"+insertuserid+"';";

            Statement stmt2 = conn.createStatement();
            stmt2.executeUpdate(sql3);
            stmt2.close();

        }
        catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                out.println("SQLException:" + e.getMessage());
            }
        }

    }
    //status=4(urlの格納)
    public static void Update_saveurl(String insertuserid,String inserturl){
        Connection conn = null;
        try{
            conn = getConnection();
            out.println("connection success!");

            Statement stmt =conn.createStatement();
            String sql="UPDATE messagecard SET save_url='"+inserturl
                    +"',status=4,updatetime=now() WHERE from_userid='"+insertuserid+"' AND status=3;";
            stmt.executeUpdate(sql);


        }
        catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();

                }
            } catch (SQLException e) {
                out.println("SQLException:" + e.getMessage());
            }
        }

    }

    public static void Update_sendtime(String insertuserid,Timestamp inserttimestamp){
        Connection conn = null;
        try{
            conn = getConnection();
            out.println("connection success!");

            Statement stmt =conn.createStatement();
            String sql="UPDATE messagecard SET sendtime='"+inserttimestamp
                    +"',status=5,updatetime=now() WHERE from_userid='"+insertuserid+"' AND status=4;";
            stmt.executeUpdate(sql);


        }
        catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();

                }
            } catch (SQLException e) {
                out.println("SQLException:" + e.getMessage());
            }
        }
    }

    public static void Update_password(String insertuserid,String insertpassword){
        Connection conn = null;
        try{
            conn = getConnection();
            out.println("connection success!");

            Statement stmt =conn.createStatement();
            String sql="UPDATE messagecard SET password='"+insertpassword
                    +"',status=6,updatetime=now() WHERE from_userid='"+insertuserid+"' AND status=5;";
            stmt.executeUpdate(sql);


        }
        catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();

                }
            } catch (SQLException e) {
                out.println("SQLException:" + e.getMessage());
            }
        }
    }

    public static void Update_settoroomid(String insertroomid,String insertpassword){
        Connection conn = null;
        try{
            conn = getConnection();
            out.println("connection success!");

            Statement stmt =conn.createStatement();
            String sql="UPDATE messagecard SET to_roomid='"+insertroomid+"',status=7,updatetime=now() WHERE status=6 AND password='"+insertpassword+"';";
            stmt.executeUpdate(sql);


        }
        catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();

                }
            } catch (SQLException e) {
                out.println("SQLException:" + e.getMessage());
            }
        }
    }
    public static ArrayList<ArrayList<String>> set_roomid_url = null;

    public static ArrayList<ArrayList<String>> search_sendtime(String insertTimeStamp){//Timestamp inserttimestamp) {
        Connection conn = null;

        ArrayList<ArrayList<String>> set_roomid_url = new ArrayList<>();

        try {
            conn = getConnection();

            Statement stmt = conn.createStatement(1004, 1007);
            // = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM messagecard WHERE sendtime='" + insertTimeStamp + "';";
            ResultSet rs = stmt.executeQuery(sql);

            ArrayList<String> list_url = new ArrayList<>();
            ArrayList<String> list_roomid = new ArrayList<>();
            if(rs.next()){
                rs.beforeFirst();
                while (rs.next()) {
                    list_roomid.add(rs.getString("to_roomid"));
                    list_url.add(rs.getString("save_url"));//getStringは一カラムにつき一回のみ?
                }
                set_roomid_url.add(list_roomid);
                set_roomid_url.add(list_url);
            }
            rs.close();
            stmt.close();

            //System.out.println(set_roomid_url);
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return set_roomid_url;

    }

    public static void setfilepath(String insertuserid){
        Connection conn = null;
        try{
            conn = getConnection();
            out.println("connection success!");

            //template tableからID検索
            Statement stmt3 = conn.createStatement(1004,1007);
            String sql = "SELECT * FROM messagecard WHERE status=3 AND from_userid='"+insertuserid+"';";
            ResultSet rs3 =stmt3.executeQuery(sql);
            rs3.next();
            int code = rs3.getInt("templateid");
            rs3.close();
            stmt3.close();

            String sql2 ="SELECT * FROM template WHERE templateid="+code+";";

            Statement stmt2 = conn.createStatement(1004,1007);
            ResultSet rs2  = stmt2.executeQuery(sql2);
            rs2.next();
            filepath = rs2.getString("path");

            out.println("setting:"+filepath);

            
            rs2.close();
            stmt2.close();

        }
        catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();

                }
            } catch (SQLException e) {
                out.println("SQLException:" + e.getMessage());
            }
        }

    }
    public static void refleshstatus(String insertuserid){
        Connection conn = null;
        ArrayList<Integer> tmpcardid=new ArrayList<Integer>();
        try{
            conn = getConnection();
            System.out.println("ステータスのリフレッシュ開始");

            String sql ="SELECT * FROM messagecard WHERE from_userID ='"+insertuserid+"' AND status!=0 AND status!=7";
            Statement stmt =conn.createStatement(1004,1007);

            ResultSet rs  = stmt.executeQuery(sql);
            while (rs.next()){
                tmpcardid.add(rs.getInt("cardid"));
            }
            rs.close();
            stmt.close();



            Collections.reverse(tmpcardid);
            String latestcardid = String.valueOf(tmpcardid.get(0));

            //first choose

            String sql1 = "UPDATE messagecard SET status=0,updatetime=now() WHERE status!=7 AND cardid!="+latestcardid+" AND from_userid='"+insertuserid+"';";
            Statement stmt1 = conn.createStatement();
            stmt1.executeUpdate(sql1);
            stmt1.close();

        }
        catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                out.println("SQLException:" + e.getMessage());
            }
        }

    }

    public static String getfilepath(){
        return filepath;
    }
    public  static int getstatus(){
        return status;
    }

    public static void  readyForMessage(String userid)throws SQLException{
        Connection conn = null;
        try{
            conn = getConnection();
            System.out.println("readyForMessageにはいりました");

            /*他にSTATUS=2が存在していればSTATUS=0にする処理の記述が必要
            * EX)00002 この場合は２を削除しない
            *    00222 この場合、最新以外のSTATUS=2は削除
            *    02221 この場合、１以外は削除*/

            //template tablからID検索
            /*Statement stmt3 = conn.createStatement(1004,1007);
            String sql3 = "SELECT * FROM messagecard WHERE from_userid='"+insertuserid+"';";
            ResultSet rs3 =stmt3.executeQuery(sql3);
            rs3.next();
            int code = rs3.getInt("templateid");
            rs3.close();
            stmt3.close();*/
            //first choose
            String sql5 = "UPDATE messagecard SET status=2,updatetime=now() WHERE status = 4 AND from_userid='"+userid+"';";
            Statement stmt5 = conn.createStatement();
            stmt5.executeUpdate(sql5);
            stmt5.close();

        }
        catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                out.println("SQLException:" + e.getMessage());
            }
        }

    }


    public static String getWhereCardIs(String userId)throws SQLException{
        Connection conn = null;
        String url = "";
        try{
            conn = getConnection();
            System.out.println("readyForMessageにはいりました");

            /*他にSTATUS=2が存在していればSTATUS=0にする処理の記述が必要
            * EX)00002 この場合は２を削除しない
            *    00222 この場合、最新以外のSTATUS=2は削除
            *    02221 この場合、１以外は削除*/

            //template tablからID検索
            Statement stmt3 = conn.createStatement(1004,1007);
            String sql3 = "SELECT * FROM messagecard WHERE status = 4 AND from_userid='"+userId+"';";
            ResultSet rs3 =stmt3.executeQuery(sql3);
            rs3.next();
            url = rs3.getString("save_url");
            rs3.close();
            stmt3.close();
            //first choose
            /*String sql5 = "UPDATE messagecard SET status=2,updatetime=now() WHERE status = 4 AND from_userid='"+userid+"';";
            Statement stmt5 = conn.createStatement();
            stmt5.executeUpdate(sql5);
            stmt5.close();*/


        }
        catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                out.println("SQLException:" + e.getMessage());
            }
        }

        return url;

    }


}

