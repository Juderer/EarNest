package com.earnest.model.entities;

/**
 * Created by HP on 2018/6/2.
 */

public class Item_Song {

    public String num;
    public String title;
    public String singer;

    public String getNum() {
        return num;
    }

    public void setNum(String num){ this.num = num; };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public Item_Song() {
        super();
    }

    public Item_Song(String num, String title, String singer) {
        super();
        this.num = num;
        this.title = title;
        this.singer = singer;
    }

    @Override
    public String toString() {
        return "Song [num=" + num + ", title=" + title + ", singer=" + singer + "]";
    }


}
