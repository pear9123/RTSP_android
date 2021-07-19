package com.bae.message.ipcam.ui.home;

public class CardItem {
    private int _id;
    private String title;
    private String contents;
    private String ip;
    private String port;
    private String id;
    private String pw;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    @Override
    public String toString() {
        return "CardItem{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
