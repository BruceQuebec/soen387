package soen387.chatroom.model;

import org.joda.time.DateTime;

public class Attachment {
    int pid;
    String type;
    String name;
    double size;
    byte[] content;
    DateTime uptime;
    DateTime mtime;

    public Attachment(String type, String name, double size, byte[] content){
        this.type = type;
        this.name = name;
        this.size = size;
        this.content = content;
        uptime = DateTime.now();
        mtime = DateTime.now();
    }

    public int getPid() { return pid; }

    public void setPid(int pid) { this.pid = pid; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public double getSize() { return size; }

    public void setSize(double size) { this.size = size; }

    public byte[] getContent() { return content; }

    public void setContent(byte[] content) { this.content = content; }

    public DateTime getUptime() { return uptime; }

    public void setUptime(DateTime uptime) { this.uptime = uptime; }

    public DateTime getMtime() { return mtime; }

    public void setMtime(DateTime mtime) { this.mtime = mtime; }
}
