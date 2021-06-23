package main.java.Dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.Client.Model.ClientModel;
import main.java.bean.ClientUser;
import main.java.bean.Group;
import main.java.bean.Message;
import main.java.bean.ServerUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @ClassName DB
 * @Description TODO
 * @date 2021/6/14 23:32
 * @Version 1.0
 */

public class DbUtils
{
    static {
        getInstance();
    }

    private static DaoModel userDao;
    public static DaoModel getInstance(){
        if(userDao==null){
            synchronized (DbUtils.class){
                if(userDao==null){
                    userDao=new DaoModel();
                }
            }
        }
        return userDao;
    }
    /**
     * @Description: 查找个人信息
     * 返回所有个人信息，包括姓名与密码
     **/
    public static ArrayList<ServerUser> find_person() throws SQLException {
        ResultSet resultSet=DaoModel.find("select name,password from person");
        //把resultSet转换成ArrayList<Server>
        ArrayList<ServerUser> s=new ArrayList<ServerUser>();
        while (resultSet.next()){
            System.out.println(resultSet.getString("name")+","+resultSet.getString("password"));
            s.add(new ServerUser(0, resultSet.getString("name"), resultSet.getString("password")));
        }
        return s;
    }
    public static ObservableList<ClientUser> find_personToClient() {
        try {
            ResultSet resultSet=DaoModel.find("select name from person");
            ObservableList<ClientUser> s = FXCollections.observableArrayList();
            while (resultSet.next()){
                ClientUser c=new ClientUser();
                c.setUserName(resultSet.getString("name"));
                s.add(c);
            }
            return s;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    /**
     * @Description: 查找所有的群返回给grouplist

     **/
//    public static ObservableList<Group> find_Group(){
//        try{
//            boolean flag=false;
//            ResultSet resultSet=DaoModel.find("select * from groupchat");
//            ObservableList<Group> o=FXCollections.observableArrayList();
//            while (resultSet.next()){
//                if(o.size()!=0){
//                    for(Group group:o){
//                        if(group.getGroupName().equals(resultSet.getString("groupname")))
//                        {
//                            flag=true;
////                            加人
//                            ClientUser c=new ClientUser();
//                            group.addMember(c);
//                            c.setUserName(resultSet.getString("groupmember"));
//                            break;
//                        }
//
//                    }
//                    if(flag==false){
//                            //如果该群没建
//                            Group group=new Group(resultSet.getString("groupname"));
//                            //拉人
//                            ClientUser c=new ClientUser();
//                            c.setUserName(resultSet.getString("groupmember"));
//                            group.addMember(c);
//                            o.add(group);
//
//                    }
//                }
//                else {
//                    //如果该群没建
//                    Group group=new Group(resultSet.getString("groupname"));
//                    //拉人
//                    ClientUser c=new ClientUser();
//                    c.setUserName(resultSet.getString("groupmember"));
//                    group.addMember(c);
//                    o.add(group);
//                }
//
//            }
//            return o;
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        return null;
//    }
    /**
     * @Description: 查找群名，包含成员。

     **/
    public static ObservableList<Group> find_Group(){
        try{
            ResultSet resultSet=DaoModel.find("select distinct groupName from groupchat");
            ObservableList<Group> o=FXCollections.observableArrayList();
            while (resultSet.next()){
                o.add(new Group(resultSet.getString("groupName")));
            }
            for(Group group:o){
                String sql="select groupmember from groupChat where groupName=?";
                resultSet=DaoModel.find(sql,group.getGroupName());
                while (resultSet.next()){
                    ClientUser c=new ClientUser();
                    c.setUserName(resultSet.getString("groupMember"));
                    group.addMember(c);
                }
            }
            return o;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    /**
     * @Description: 找到某个人在的群，并返回群列表
     * @param name:

    	 * @return javafx.collections.ObservableList<main.java.bean.Group>
     **/
    public static ObservableList<Group> find_GroupPerson(String name){
        try{
            ResultSet resultSet=DaoModel.find("select distinct groupName from groupchat where groupMember=?",name);
            ObservableList<Group> o=FXCollections.observableArrayList();
            while (resultSet.next()){
                o.add(new Group(resultSet.getString("groupName")));
            }
            for(Group group:o){
                String sql="select groupmember from groupChat where groupName=?";
                resultSet=DaoModel.find(sql,group.getGroupName());
                while (resultSet.next()){
                    ClientUser c=new ClientUser();
                    c.setUserName(resultSet.getString("groupMember"));
                    group.addMember(c);
                }
            }
            return o;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    public static ArrayList<Group> find_GroupToArrayList(){
        try{
            ResultSet resultSet=DaoModel.find("select distinct groupName from groupchat");
            ArrayList<Group> o=new ArrayList<>();
            while (resultSet.next()){
                o.add(new Group(resultSet.getString("groupName")));
            }
            for(Group group:o){
                String sql="select groupmember from groupChat where groupName=?";
                resultSet=DaoModel.find(sql,group.getGroupName());
                while (resultSet.next()){
                    ClientUser c=new ClientUser();
                    c.setUserName(resultSet.getString("groupMember"));
                    group.addMember(c);
                }
            }
            return o;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    public static HashMap<String,ArrayList<Message>> find_groupChatRecord(){
//        HashMap<String,ArrayList<Message>>: 群名：消息列表
        try{
            String sql="select distinct groupName from groupchatRecord";
            ResultSet resultSet=DaoModel.find(sql);
            HashMap<String,ArrayList<Message>> h=new HashMap<>();
            while (resultSet.next()){
                h.put(resultSet.getString("groupName"),new ArrayList<>());
            }
            Set<String> s=h.keySet();

//                有多少个群就循环多少次
            for(String groupname:s){
                sql="select * from groupChatRecord where groupName=? order by time";
                resultSet=DaoModel.find(sql,groupname);
                while (resultSet.next()){
                    Message m=new Message();
                    m.setSpeaker(resultSet.getString("speaker"));
                    m.setContent(resultSet.getString("content"));
                    m.setTimer(resultSet.getString("time"));
//                Group group=h.get(resultSet.getString("groupName"));
                    h.get(groupname).add(m);
                }
            }
            return h;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * @Description: 查找个人所有聊天记录
     * 转换成


     **/
    public static HashMap<String,ArrayList<Message>> find_chatRecord(String name) {
        //按时间排序
        try{
        String sql="SELECT * FROM chatrecord WHERE thisUser=? ORDER BY time";
        ResultSet resultSet=DaoModel.find(sql,name);
        HashMap<String,ArrayList<Message>> h=new HashMap<>();
        //新改了
        for(ClientUser c:ClientModel.userList){
            h.put(c.getUserName(),new ArrayList<>());
        }
        while (resultSet.next()){
            //有多少条记录就有多少条消息
            Message m=new Message();
            String chatUser;

            chatUser=resultSet.getString("chatUser");

            m.setSpeaker(resultSet.getString("speaker"));
            m.setContent(resultSet.getString("content"));
            m.setTimer(resultSet.getString("time"));
//            h.put(chatUser,new ArrayList<>());

            h.get(chatUser).add(m);

        }
        return h;
        } catch (SQLException throwables) {

        }
        return null;
    }
    /**
     * @Description: 查找个人的好友
     * 教训：遍历resultSet的时候只能一条语句遍历一个元组，不懂为什么

     **/

    public static ObservableList<ClientUser> find_friendlist(String name) {
        try {
            ObservableList<ClientUser> userList = FXCollections.observableArrayList();
            String sql = "SELECT friname FROM friendlist WHERE myname=?";
            ResultSet resultSet = DaoModel.find(sql, name);
            while (resultSet.next()) {
//                System.out.println("name1" + resultSet.getString(1) + ",name2" + resultSet.getString(2));
                ClientUser c = new ClientUser();
                c.setUserName(resultSet.getString("friname"));
                userList.add(c);
            }
            return userList;
        } catch (SQLException e) {
            System.out.println("错了");
        }
        return null;
    }
    /**
     * @Description: 新增个人聊天记录
     * 实质就是把chatRecordSum写回数据库


     **/
    public static void insert_chatRecord(String myName,String friName,Message m){
        String tableName="chatrecord";
        try{
            DaoModel.insert(tableName,myName,friName,m.getSpeaker(),m.getContent(),m.getTimer());
        }catch (SQLException e){
            System.out.println("插入聊天记录出错");
        }
    }
    public static void insert_groupchatRecord(String group,Message m){
        String tableName="groupchatRecord";
        try{
            DaoModel.insert(tableName,group,m.getSpeaker(),m.getContent(),m.getTimer());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    /**
     * @Description: 新增好友
     * 把userlist写回数据库


     **/
    public static void insert_friendlist(String myName,String friName){
        String tableName="friendlist";
        try{
            DaoModel.insert(tableName,friName,myName);
        }catch (SQLException e){
            System.out.println("插入聊天记录失败");
        }
    }
    public static void insert_group(String group,ArrayList<ClientUser> clientUsers){
        String tableName="groupchat";
        try{
            for(ClientUser c:clientUsers){
                DaoModel.insert(tableName,group,c.getUserName());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    /**
     * @Description: 删除个人聊天记录


     **/
    public static void delete_chatRecord(String myName,String friName,Message m){
        String tableName="chatrecord";
        String condition="thisuser=? and chatuser =? and speaker=? and content=? and time=?";
        try{
            DaoModel.delete(tableName,condition,myName,friName,m.getSpeaker(),m.getContent(),m.getTimer());
        } catch (SQLException throwables) {
            System.out.println("删除聊天记录出错");
        }
    }
    /**
     * @Description: 删除好友


     **/
    public static void delete_friendlist(String myName,String friName){
        String tableName="friendlist";
        String condition="friname=? and myname=?";
        try{
            DaoModel.delete(tableName,condition,friName,myName);
        } catch (SQLException throwables) {
            System.out.println("删除好友出错");
        }
    }
    public static void deleteAllChatRecordByName(String myName){
        String tableName="chatrecord";
        String condition="thisuser=?";
        try {
            DaoModel.delete(tableName,condition,myName);
        } catch (SQLException throwables) {
            System.out.println("删除该用户所有聊天数据失败");
        }

    }
    public static void deleteAllFriendsByName(String myName){
        String tableName="friendlist";
        String condition="myname=?";
        try{
            DaoModel.delete(tableName,condition,myName);
        } catch (SQLException throwables) {
            System.out.println("删除该用户所有好友失败");
        }
    }
    /**
     * @Description: 修改好友列表


     **/
    public static void update_fiendlist(){
    }

}
