package main.java.Dao;


import javafx.collections.ObservableList;
import main.java.bean.ClientUser;
import main.java.bean.Group;
import main.java.bean.Message;
import main.java.bean.ServerUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @ClassName DBtest
 * @Description TODO
 * @date 2021/6/17 10:33
 * @Version 1.0
 */

public class DBtest {
    public static void main(String[] args) throws SQLException {
        DbUtils db=new DbUtils();
//        ObservableList<ClientUser> a=DbUtils.find_friendlist("3");
//        DbUtils.insert_friendlist("2","3");
//        DbUtils.insert_friendlist("3","2");
//        Message m=new Message();
//        m.setSpeaker("2");
//        m.setContent("吃了吗");
//        m.setTimer("2021-06-18 12:42:29");
//        DbUtils.insert_chatRecord("2","3",m);
//        DbUtils.delete_friendlist("2","3");
//        DbUtils.delete_chatRecord("2","3",m);
//        DbUtils.deleteAllChatRecordByName("1");
//        DbUtils.deleteAllFriendsByName("1");
//        ObservableList<Group>g=DbUtils.find_Group();
//        HashMap<String,ArrayList<Message>> h=DbUtils.find_groupChatRecord();
        ClientUser c=new ClientUser();
        c.setUserName("3");
        ArrayList<ClientUser> a=new ArrayList<>();
        a.add(c);
        DbUtils.insert_group("hhh",a);
        System.out.println("success");

    }
}
