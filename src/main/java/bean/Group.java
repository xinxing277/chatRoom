package main.java.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @ClassName Group
 * @Description TODO
 * @date 2021/6/22 10:47
 * @Version 1.0
 */

public class Group implements Serializable {
    private String groupName;
    private ArrayList<ClientUser> member;
    public Group(String groupName) {
        this.groupName = groupName;
        this.member = new ArrayList<>();
    }
    public void setGroupName(String name){
        groupName=name;
    }
    public void addMember(ClientUser c){
        member.add(c);
    }
    public String getGroupName(){
        return groupName;
    }
    public ArrayList<ClientUser> getMember(){
        return member;
    }

}
