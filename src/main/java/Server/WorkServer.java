package main.java.Server;

import com.google.gson.Gson;
import main.java.Utils.GsonUtils;
import main.java.bean.ClientUser;
import main.java.bean.Group;
import main.java.bean.ServerUser;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static main.java.Utils.Constants.*;

/**
 * @ClassName WorkServer
 * @Description TODO
 * @date 2021/6/14 11:06
 * @Version 1.0
 */

public class WorkServer extends Thread{
    private ServerUser workUser; //the user is connected

    private Socket socket;

    private ArrayList<ServerUser> users;

    private ArrayList<Group> groups;

    private BufferedReader reader;

    private PrintWriter writer;

    private boolean isLogOut = false;

    private long currentTime = 0;

    private Gson gson;

    public WorkServer(Socket socket, ArrayList users,ArrayList groups) {
        super();
        gson = new Gson();
        this.groups=groups;
        this.socket = socket; //bind socket
        this.users = users;   //get the common user resource
    }

    @Override
    public void run() {
        //todo server's work
        try {
            currentTime = new Date().getTime();
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            String readLine;
            while (true) {
                //heart check
                long newTime = new Date().getTime();
                if (newTime - currentTime > 2000) {
//                    logOut();
                } else {
                    currentTime = newTime;
                }
                readLine = reader.readLine();
                if (readLine == null)
                    logOut();
                handleMessage(readLine);
                sentMessageToClient();
                if (isLogOut) {
                    // kill the I/O stream
                    reader.close();
                    writer.close();
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            logOut();
        } catch (IOException e) {
            e.printStackTrace();
            logOut();
        }
    }
    /**
     * @Description:
     * @Authod: share
     * @Date: 2021/6/14 19:46
     * @param: []
     * @return: void
     **/
    private void logOut(){
        //
        if(workUser==null)
            return;
        System.out.println("用户 " + workUser.getUserName() + " 已经离线");
        //保留user但修改状态
        workUser.setStatus("offLine");
        for(ServerUser u:users){
            if(u.getUserName().equals(workUser.getUserName()))
                u.setStatus("offLine");
        }
        broadcast(getGroup(),COM_LOGOUT);
        isLogOut=true;
    }
///**
// * @Description:
//  * @param userName:
// * @param password:
// **/
//    private boolean creatUser(String userName,String password){
//            for (ServerUser user:users){
//                if(user.getUserName().equals(userName)){
//                    return false;
//                }
//            }
//            ServerUser newUser=new ServerUser(users.size(),userName,password);
//            newUser.setStatus("online");
//            users.add(newUser);
//            //把新用户添加进数据库
//            try {
//                UserDaoImpl.getInstance().add(newUser);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            workUser = newUser;
//            return true;
//    }
    private void handleMessage(String readLine){
        System.out.println("handle message" + readLine);
        Map<Integer,Object> gsonMap= GsonUtils.GsonToMap(readLine);
        Integer command=GsonUtils.Double2Integer((Double) gsonMap.get(COMMAND));
        HashMap map=new HashMap();
        String username,password;
        switch (command){
            case COM_GROUP:
                writer.println(getGroup());
                System.out.println(workUser.getUserName() + "请求获得在线用户详情");
                break;
            case COM_SIGNUP:
                username=(String) gsonMap.get(USERNAME);
                password=(String) gsonMap.get(PASSWORD);
                map.put(COMMAND,COM_RESULT);
                    currentTime=new Date().getTime();
                    //存储信息
                    map.put(COM_RESULT,SUCCESS);
                    map.put(COM_DESCRIPTION,"success");
                    writer.println(gson.toJson(map));
                    broadcast(getGroup(),COM_SIGNUP);
                    System.out.println("用户" + username + "注册上线了");
                break;
            case COM_LOGIN:
                username = (String) gsonMap.get(USERNAME);
                password = (String) gsonMap.get(PASSWORD);
                boolean find = false;
                for (ServerUser u : users) {
                    if (u.getUserName().equals(username)) {
                        if (!u.getPassword().equals(password)) {
                            map.put(COM_DESCRIPTION, "账号密码输入有误");
                            break;
                        }
                        if (u.getStatus().equals("online")) {
                            map.put(COM_DESCRIPTION, "该用户已经登录");
                            break;
                        }
                        currentTime = new Date().getTime();
                        map.put(COM_RESULT, SUCCESS);
                        map.put(COM_DESCRIPTION, username + "success");
                        u.setStatus("online");
                        writer.println(gson.toJson(map));
                        workUser = u;
                        broadcast(getGroup(), COM_SIGNUP);
                        find = true;
                        System.out.println("用户" + username + "上线了");
                        break;
                    }
                }
                if(!find){
                    map.put(COM_RESULT, FAILED);
                    if (!map.containsKey(COM_DESCRIPTION))
                        map.put(COM_DESCRIPTION, username + "未注册");
                    writer.println(gson.toJson(map)); //返回消息给服务器
                }
                break;
            case COM_CHATWITH:
                String receiver=(String) gsonMap.get(RECEIVER);
                map=new HashMap();
                map.put(COMMAND,COM_CHATWITH);
                map.put(SPEAKER,gsonMap.get(SPEAKER));
                map.put(RECEIVER,gsonMap.get(RECEIVER));
                map.put(CONTENT,gsonMap.get(CONTENT));
                map.put(TIME,getFormatDate());
                for( ServerUser u:users){
                    if(u.getUserName().equals(receiver)){
                        u.addMsg(gson.toJson(map));
                        break;
                    }
                }
                workUser.addMsg(gson.toJson(map));
                break;
            case COM_CHATALL:
                String groupname=(String) gsonMap.get(GROUPNAME);
                map = new HashMap();
                map.put(COMMAND, COM_CHATALL);
                map.put(GROUPNAME,groupname);
                map.put(SPEAKER, workUser.getUserName());
                map.put(TIME, getFormatDate());
                map.put(CONTENT, gsonMap.get(CONTENT));
                broadcast(gson.toJson(map), COM_MESSAGEALL);
                break;
            default:
                break;
        }
    }
/**
 * @return 时间格式字符串
 **/
    public String getFormatDate(){
        Date date=new Date();
        long times=date.getTime();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dataString=formatter.format(date);
        return dataString;
    }
/**
 * @Description:广播信息
  * @param message: 广播的信息
 * @param type: 包含"message", "logOUt", "signUp"类型
 **/

    private void broadcast(String message,int type){
        System.out.println(workUser.getUserName() + " 开始广播broadcast " + message);
        switch (type){
            case COM_MESSAGEALL:
                for (ServerUser u:users){
                    u.addMsg(message);
                }
                break;
            case COM_LOGOUT:
            case COM_SIGNUP:
                for (ServerUser u:users){
                    if(!u.getUserName().equals(workUser.getUserName())){
                        u.addMsg(message);
                    }
                }
                break;
        }
    }
/**
 * @Description:
 * @Authod: share
 * @Date: 2021/6/14 20:09


 **/
    private void sentMessageToClient(){
        String message;
        if(workUser!=null)
            while ((message=workUser.getMsg())!=null){
                writer.println(message);
                System.out.println(workUser.getUserName() + "的数据仓发送 message: " + message + "剩余 size" + workUser.session.size());
            }
    }
    
/**
 * @Description:
 * @Authod: share
 * @Date: 2021/6/14 20:08


 **/
    private String getGroup(){
        String[] userlist=new String[users.size()*2];
//        为什么乘二：一个格子放用户姓名，旁边格子放用户的状态
        int j=0;
        for (int i=0;i<users.size();i++,j++){
            userlist[j]=users.get(i).getUserName();
            userlist[++j]=users.get(i).getStatus();
        }
        HashMap map=new HashMap();
        map.put(COMMAND,COM_GROUP);
        map.put(COM_GROUP,userlist);
        return gson.toJson(map);
    }
}
