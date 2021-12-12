package com.example.transferfile.domain;

public class Itemoftransfer {
    private String text;
    private int img;
    private int abr;
    private String progresstxt;

    public Itemoftransfer(String text, int img,String progresstxt) {
        this.text = text;
        this.img = img;
        this.progresstxt = progresstxt;
    }
    public Itemoftransfer(String text, int img) {
        this.text = text;
        this.img = img;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImg(){return img;}
    public void setImg(){
        this.img=img;
    }

    public int getAbr(){return abr;}
    private void setAbr(){
        this.abr=abr;
    }


    public String getProgresstxt() {
        return progresstxt;
    }

    public void setProgresstxt(String progresstxt) {
        this.progresstxt = progresstxt;
    }
}
