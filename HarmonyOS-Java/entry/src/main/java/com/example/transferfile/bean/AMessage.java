package com.example.transferfile.bean;

public class AMessage {

    private String userName;

    private String message;

    private String type;

    public AMessage(String name, String message) {
        this.userName = name;
        this.message = message;
    }

    public AMessage(String name, String message,String type) {
        this.userName = name;
        this.message = message;
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
