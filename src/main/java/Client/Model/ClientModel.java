package main.java.Client.Model;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.Client.chatroom.MainView;
import main.java.Dao.DbUtils;
import main.java.Utils.GsonUtils;
import main.java.bean.ClientUser;
import main.java.bean.Group;
import main.java.bean.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

import main.java.Utils.Constants.*;

import static main.java.Utils.Constants.*;
//不懂为什么要分ClientUser和ClientModel
/**
 * @ClassName ClientModel
 * @Description TODO
 * @date 2021/6/14 10:03
 * @Version 1.0
 */

public class ClientModel {
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket client;
    private final int port = 8888;
    private String IP;
    private boolean isConnect = false;                               //连接标志
    private boolean chatChange = false;
    private String chatUser = "[group]";
    public static String thisUser;
    private Gson gson;
    public LinkedHashMap<String, ArrayList<Message>> userSession;   //用户消息队列存储用
    private Thread keepalive = new Thread(new KeepAliveWatchDog());
    private Thread keepreceive = new Thread(new ReceiveWatchDog());
    public static boolean flag=false;
    public static ObservableList<ClientUser> userList;
    public static ObservableList<ClientUser> friendlist;
    public static ObservableList<Group> grouplist;
//还有群聊列表待补充
    public static HashMap<String,ArrayList<Message>> chatRecorderSum;//人名：消息
    public static HashMap<String,ArrayList<Message>> groupChatRecord;//群名：消息
    private ObservableList<Message> chatRecoder;
    private ClientModel() throws SQLException {
        gson = new Gson();
        this.thisUser=thisUser;
        ClientUser user=new ClientUser();
        user.setUserName("[group]");
        user.setStatus("");
        userSession=new LinkedHashMap<>();
        userSession.put("[group]", new ArrayList<>());

        chatRecoder=FXCollections.observableArrayList();
    }
    private static ClientModel instance;

    class KeepAliveWatchDog implements Runnable{

        @Override
        public void run() {
            HashMap<Integer,Integer> map=new HashMap<>();
            map.put(COMMAND,COM_KEEP);
            try{
                System.out.println("keep alive start" + Thread.currentThread());
                //心跳检测
                while (isConnect){
                    Thread.sleep(500);
                    writer.println(gson.toJson(map));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
class ReceiveWatchDog implements Runnable{
//收包检测
    @Override
    public void run() {
        try{
            System.out.println(" Receieve start" + Thread.currentThread());
            String message;
            while (isConnect){
                message=reader.readLine();
                handleMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    public static ClientModel getInstance(String thisUser) {
        try{
        if (instance == null) {
            synchronized (ClientModel.class) {
                if (instance == null) {
                    instance = new ClientModel();
                }
            }
        }
            return instance;
        }catch (SQLException e){
            System.out.println("连接数据库失败");
        }
        return null;
    }
    public void setChatUser(String chatUser){
        if(!this.chatUser.equals(chatUser)){
            chatChange=true;
            this.chatUser=chatUser;
        }
    }
    public String getChatUser(){
        return chatUser;
    }

    public ObservableList<Message> getChatRecoder(){
        return chatRecoder;
    }

//    public HashMap<String,ArrayList<Message>> getChatRecorderSum(){
//        return chatRecorderSum;
//    }
    public String getThisUser(){
        return thisUser;
    }
    //断开连接
    public void disConnect() {
        isConnect=false;
        keepalive.stop();
        keepreceive.stop();
        if(writer!=null){
            writer.close();
            writer=null;
        }
        if(client!=null){
            try{
                client.close();
                client=null;
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * @Description: 对信息进行备份，包括聊天记录和好友列表


     **/
    public void backup(){
//        备份聊天记录，先把该用户的记录全部删除，再重新写入
        DbUtils.deleteAllChatRecordByName(thisUser);
        Set<Map.Entry<String,ArrayList<Message>>> s=chatRecorderSum.entrySet();
        for(Map.Entry<String,ArrayList<Message>> set:s){
            String chatUser=set.getKey();
            ArrayList<Message> messages=set.getValue();
            for(Message m:messages){
                DbUtils.insert_chatRecord(thisUser,chatUser,m);
            }
        }
//        备份好友列表，先把该用户所有好友都删除，再重新写入
        DbUtils.deleteAllFriendsByName(thisUser);
        for(ClientUser c:friendlist){
            DbUtils.insert_friendlist(thisUser,c.getUserName());
        }
//        群聊不保存

    }
    private void handleMessage(String message){
        Map<Integer,Object> gsonMap= GsonUtils.GsonToMap(message);
        System.out.println(gsonMap);
        Integer command=GsonUtils.Double2Integer((Double) gsonMap.get(COMMAND));
        Message m;
        switch (command){
            case COM_GROUP:
                HashSet<String> recorder=new HashSet<>();
                for (ClientUser u:userList){
                    if(u.isNotify()){
                        recorder.add(u.getUserName());
                    }
                }
                ArrayList<String> userData=(ArrayList<String>)gsonMap.get(COM_GROUP);
                userList.remove(1,userList.size());
                int onlineUserNum=0;
                for(int i=0;i<userData.size();i++){
                    ClientUser user=new ClientUser();
                    user.setUserName(userData.get(i));
                    user.setStatus(userData.get(++i));
                    if(user.getStatus().equals("online"))
                        onlineUserNum++;
                    if(recorder.contains(user.getStatus()+"(*)")){
                        user.setNotify(true);
                        user.setStatus(user.getStatus()+"(*)");
                    }
                    userList.add(user);
                    userSession.put(user.getUserName(),new ArrayList<>());
                }
                int finalOnlineUserNum=onlineUserNum;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        MainView.getInstance().getLabUserCoumter().setText("服务器在线人数为" + finalOnlineUserNum);
                    }
                });
                break;
            case COM_CHATALL:
//                客户端已经收到了群聊包怎么
//
                //我做的
                m=new Message();
//                String receiverMember=(String) gsonMap.get(RECEIVER);//某一个群成员
//                String speakerGroup=(String) gsonMap.get(SPEAKER);//群名
                m.setTimer((String) gsonMap.get(TIME));
                m.setSpeaker((String) gsonMap.get(SPEAKER));
                m.setContent((String) gsonMap.get(CONTENT));
                String group=(String) gsonMap.get(GROUPNAME);
                try{
//                    if(thisUser.equals(receiverMember)){
//                        groupChatRecord.get(speakerGroup).add(m);
//                    }else if(thisUser.equals(speakerGroup)){
////                        这个应该不可能发生
//                        groupChatRecord.get(receiverMember).add(m);
//                    }
//                    如果自己在群聊，则收下这个包，否则什么都不做
                    for(Group g:grouplist){
                        if(g.getGroupName().equals(group)){
                            if(groupChatRecord.get(group)!=null){
                                groupChatRecord.get(group).add(m);
                            }else {
                                groupChatRecord.put(group,new ArrayList<>());
                                groupChatRecord.get(group).add(m);
                            }
                            break;
                        }
                    }
                }catch (Exception e){
                    System.out.println(thisUser);
                    System.out.println(group);
                    System.out.println(m.getContent());
                }
                break;
            case COM_CHATWITH:
                String speaker = (String) gsonMap.get(SPEAKER);
                String receiver = (String) gsonMap.get(RECEIVER);
                String time = (String) gsonMap.get(TIME);
                String content = (String) gsonMap.get(CONTENT);
                m = new Message();
                m.setSpeaker(speaker);
                m.setContent(content);
                m.setTimer(time);
                if (thisUser.equals(receiver)) {
                    if (!chatUser.equals(speaker)) {
                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i).getUserName().equals(speaker)) {
                                ClientUser user = userList.get(i);
                                if (!user.isNotify()) {
                                    //user.setStatus(userList.get(i).getStatus() + "(*)");
                                    user.setNotify(true);
                                }
                                userList.remove(i);
                                userList.add(i, user);
                                break;
                            }
                        }
                        System.out.println("标记未读");
                    }else{
                        chatRecoder.add(m);
                    }
                    userSession.get(speaker).add(m);

                }else{
                    if(chatUser.equals(receiver))
                        chatRecoder.add(m);
                    userSession.get(receiver).add(m);
                }
                //break;
                //我做的
                try{
                    if(thisUser.equals(receiver)){
                        chatRecorderSum.get(speaker).add(m);
                    }else if(thisUser.equals(speaker)){
                        chatRecorderSum.get(receiver).add(m);
                    }
                }catch (Exception e){
                    System.out.println(thisUser);
                    System.out.println(speaker);
                    System.out.println(m.getContent());
                }

                break;
            default:
                break;
        }
        flag=true;
        System.out.println("服务器发来消息" + message + "消息结束");

    }
    public ArrayList<Message> getChatRecorderSum(String name){
        return chatRecorderSum.get(name);
    }
    public ArrayList<Message> getGroupchatRecorder(String name){
        return groupChatRecord.get(name);
    }
    //这里的message必须已经转换成了json字符串
    public void sentMessage(String message){
        writer.println(message);
    }

    public boolean CheckLogin(String username,String password,StringBuffer buf,int type){
        Map<Integer, Object> map;
        try {
            //针对多次尝试登录
            if (client == null || client.isClosed()) {
                client = new Socket(IP, port);
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer = new PrintWriter(client.getOutputStream(), true);
            }
            map = new HashMap<>();
            if (type == 0)
                map.put(COMMAND, COM_LOGIN);
            else
                map.put(COMMAND, COM_SIGNUP);
            map.put(USERNAME, username);
            map.put(PASSWORD, password);
            writer.println(gson.toJson(map));
            String strLine = reader.readLine(); //readline是线程阻塞的
            System.out.println(strLine);
            map = GsonUtils.GsonToMap(strLine);
            Integer result = GsonUtils.Double2Integer((Double) map.get(COM_RESULT));
            if (result == SUCCESS) {
                isConnect = true;
                //request group
                map.clear();
                map.put(COMMAND, COM_GROUP);
                writer.println(gson.toJson(map));
//                初始化静态变量
                thisUser = username;
                ClientModel.userList= DbUtils.find_personToClient();
                ClientModel.friendlist= DbUtils.find_friendlist(ClientModel.thisUser);
                ClientModel.grouplist=DbUtils.find_GroupPerson(thisUser);
                ClientModel.chatRecorderSum=DbUtils.find_chatRecord(ClientModel.thisUser);
                ClientModel.groupChatRecord=DbUtils.find_groupChatRecord();
                keepalive.start();
                keepreceive.start();
                return true;
            } else {
                String description = (String) map.get(COM_DESCRIPTION);
                buf.append(description);
                return false;
            }
        } catch (ConnectException e) {
            buf.append(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            buf.append(e.toString());
        }
        return false;
    }

}
