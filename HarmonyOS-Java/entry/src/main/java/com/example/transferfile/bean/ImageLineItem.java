package com.example.transferfile.bean;

import ohos.utils.net.Uri;

public class ImageLineItem {
    private int index;
    private Uri[] uris;

    public ImageLineItem(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Uri[] getUris() {
        return uris;
    }

    public void setUris(Uri[] uris) {
        this.uris = uris;
    }
}
