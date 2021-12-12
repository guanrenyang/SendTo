package com.example.transferfile.domain;

import ohos.agp.components.Text;

public class Itemoffile {
    private int img;
    private String text;

    public Itemoffile(int img, Text text){}


    public Itemoffile(int img, String text) {
        this.img = img;
        this.text = text;
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

}
