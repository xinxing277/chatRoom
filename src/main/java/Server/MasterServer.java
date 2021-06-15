package main.java.Server;

import com.sun.corba.se.spi.activation.Server;
import main.java.bean.ServerUser;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * @ClassName MasterServer
 * @Description TODO
 * @date 2021/6/14 11:05
 * @Version 1.0
 */

public class MasterServer {
    private ArrayList<ServerUser> users;
    public ServerSocket masterServer;
    public WorkServer workServer;
    private int port=8888;
    public void start(){
        users=new ArrayList<ServerUser>();
        try{
            masterServer=new ServerSocket(port);
            try{
//                users = (ArrayList<ServerUser>) UserDaoImpl.getInstance().findAll();
                users.add(new ServerUser(1,"小红","123"));
                users.add(new ServerUser(2,"小明","456"));
                for(ServerUser u:users){
                    u.setStatus("offline");
                }
                System.out.println("get user"+users.size());

            } catch (Exception e) {
                System.out.println("userList init failed");
                e.printStackTrace();
            }
            System.out.println("server loading");
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true){
            try{
                workServer=new WorkServer(masterServer.accept(), users);
                workServer.start();
                System.out.println("workServer product");

            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
