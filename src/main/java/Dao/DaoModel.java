package main.java.Dao;

import main.java.bean.ClientUser;

import java.sql.*;

/**
 * @ClassName UserDaoImpl
 * @Description TODO
 * @date 2021/6/15 7:20
 * @Version 1.0
 */

/**
 * 笔记
 * 数据库查询无非是静态查询与动态查询
 * 静态查询：没有未知参数，查询语句即使转换成字符串又从字符串直接转换仍然可以进行查询
 * 动态查询：sql语句含有未知参数
 * 查询步骤：
 * 1.创建数据库连接
 * 2.根据连接和sql语句创建statement
 * 3.设置参数（静态可忽略）
 * 4.执行
 **/
public class DaoModel {
    //Database information
    private final static String url = "jdbc:mysql://localhost:3306/chatroom?useSSL=false&characterEncoding=utf-8&serverTimezone=GMT";
    private final static String driver = "com.mysql.cj.jdbc.Driver";
    private final static String userName = "root";
    private final static String password = "12345";
    private static Connection connection;
    private static ResultSet resultSet;
    private static Statement statement;//静态查询
    private static PreparedStatement preparedStatement;//动态查询
/**
 * @Description: 连接数据库
 **/
    public static void connect(){
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @Description: 查询个人信息person表，在登陆验证需要
    	 * @return java.sql.ResultSet 返回所有人的账号以及密码
     **/
    public static ResultSet find(String sql) throws SQLException {
        connect();
        statement=connection.createStatement();
        resultSet=statement.executeQuery(sql);
        return resultSet;
    }
    /**
     * @Description: 查询该用户的聊天信息


    	 * @return java.sql.ResultSet
     **/
    public static ResultSet find(String sql,String...name) throws SQLException {
        connect();
        preparedStatement=connection.prepareStatement(sql);
        for(int i=1;i<=name.length;i++){
            preparedStatement.setString(i,name[i-1]);
        }
        resultSet=preparedStatement.executeQuery();
        return resultSet;
    }
//    /**
//     * @Description: 查询该用户的朋友
//
//
//    	 * @return java.sql.ResultSet
//     **/
//    public ResultSet find_friendlist(){
//        return null;
//    }
    /**
     * 该方法插入个数据  例如insert(表名,要插入的数据(String数组的形式))
     *
     * @param tableName
     * @param data
     * @throws SQLException
     */
    public static void insert(String tableName, String... data) throws SQLException {
        connect();
        String pre = "";
        for (int i = 0; i < data.length; i++) {
            if (i != data.length - 1)
                pre += "?,";
            else
                pre += "?";
        }
        String Sql = "INSERT INTO " + tableName + " VALUES(" + pre + ")";
        preparedStatement = connection.prepareStatement(Sql);
        for (int i = 1; i <= data.length; i++) {
            preparedStatement.setString(i, data[i - 1]);
        }
        preparedStatement.executeUpdate();
    }
//    public static void insert(String tableName,String... data) throws SQLException{
//        connect();
//        String
//    }
//    /**
//     * @Description: 新增好友
//
//     **/
//    public void insert_friendlist(){}
    /**
     * @Description: 删除信息
     * @param tableName: 表名
     * @param condition: 条件
     * @param data: 信息
     *            举例：delete(表名,删除时的条件(例如"id = ? AND name = ?"),传入问号代表的值)
     **/
    public static void delete(String tableName, String condition, String... data) throws SQLException {
        connect();
        String Sql = "DELETE FROM " + tableName + " WHERE " + condition;
        preparedStatement = connection.prepareStatement(Sql);
        for (int i = 1; i <= data.length; i++) {
            preparedStatement.setString(i, data[i - 1]);
        }
        preparedStatement.executeUpdate();
    }
    /**
     * @Description: 修改好友信息
     * 其余信息跟删除差不多
     **/
    public static void update(String tableName, String target, String condition, String data[]) throws SQLException {
        connect();
        String Sql = "UPDATE " + tableName + " SET " + target + " WHERE " + condition;
        preparedStatement = connection.prepareStatement(Sql);
        for (int i = 1; i <= data.length; i++) {
            preparedStatement.setString(i, data[i - 1]);

        }
        preparedStatement.executeUpdate();
    }
    public static void disconnect() throws SQLException {
        try{
            connection.close();
            statement.close();
            preparedStatement.close();
        }catch (Exception e){}

    }
    public Connection getConnection(){return connection;}

}
