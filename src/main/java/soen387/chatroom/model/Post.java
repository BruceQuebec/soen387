package soen387.chatroom.model;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Post {
    private int pid;
    private String title;
    private String content;
    private int userId;
    private String username;
    private List<String> hashTags;
    private List<Attachment> attachments;
    private Time createdTime;
    private Time lastModifiedTime;

    public Post(String title, String content, int userId, String username) {
        this.pid = pid;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.hashTags = new ArrayList<>();
        this.attachments = new ArrayList<>();
    }

    public int getPid() { return pid; }

    public void setPid(int pid) { this.pid = pid; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getHashTags() { return hashTags; }

    public void setHashTags(List<String> hashTags) { this.hashTags = hashTags; }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Time getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Time createdTime) {
        this.createdTime = createdTime;
    }

    public Time getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Time lastModifiedTime) { this.lastModifiedTime = lastModifiedTime; }

    public String timeFormat(Time timeObj){
        return new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(timeObj);
    }
}
