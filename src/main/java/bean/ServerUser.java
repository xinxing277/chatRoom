package main.java.bean;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @ClassName ServerUser
 * @Description ServerUser是在服务器端 客户的保存方式。
 * 也就是说，在客户端，客户是以ClientUser的方式存在，但在服务器，他是以ServerUser的方式存在
 * @date 2021/6/14 11:08
 * @Version 1.0
 */

public class ServerUser {
    private String userName;
    private String status;
    public Queue<String> session;
    public String password;
    public  int id;
    public int getId(){
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ServerUser(int id,String userName,String password) {
        super();
        this.userName = userName;
        this.id = id;
        this.password = password;
        //Ensure thread concurrent security
        session = new ConcurrentLinkedQueue();
    }

    public ServerUser() {
        super();
        new ServerUser(0, null,null);
    }

    public void addMsg(String message) {
        session.offer(message);
    }

    public String getMsg() {
        if (session.isEmpty())
            return null;
        return session.poll();
    }
}
