package main.java.Server;

/**
 * @ClassName Localserver
 * @Description 采用worker-master架构，由master进行事件分发，
 * 由worker具体管理单个用户的消息请求
 * @date 2021/6/14 11:05
 * @Version 1.0
 */

public class Localserver {
    public static void main(String[] args) {
        MasterServer masterServer=new MasterServer();
        masterServer.start();
    }
}
