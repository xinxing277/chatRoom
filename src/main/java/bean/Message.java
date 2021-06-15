package main.java.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @ClassName Message
 * @Description TODO
 * @date 2021/6/14 10:09
 * @Version 1.0
 */

public class Message implements Serializable {
    private String content = null;
    private String speaker = null;
    private String timer = null;

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
