package main.java.bean;

/**
 * @ClassName ClientUser
 * @Description TODO
 * @date 2021/6/14 9:59
 * @Version 1.0
 */

public class ClientUser {
    private String userName;
    private String status;
    private boolean notify;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
