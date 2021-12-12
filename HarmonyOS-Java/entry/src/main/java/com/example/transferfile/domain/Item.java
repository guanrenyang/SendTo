package com.example.transferfile.domain;

import ohos.agp.components.Text;

public class Item {
    private String text;
    private int img;
    private String btname;

    public  Item(int img, Text text){}


    public Item(int img,String text) {
        this.text = text;
        this.img = img;
    }
    public Item(int img,String text,String btname) {
        this.text = text;
        this.img = img;
        this.btname = btname;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getImg() {
        return img;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getBtname() {
        return btname;
    }

    public void setBtname(String btname) {
        this.btname = btname;
    }
}
