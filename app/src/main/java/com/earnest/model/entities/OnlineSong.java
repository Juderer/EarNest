package com.earnest.model.entities;

/**
 * Created by Administrator on 2018/6/19.
 */

public class OnlineSong extends Song {
    private String id;
    private String albumPicUrl;
    private String audioUrl;

    public OnlineSong(String fileName, String title, int duration, String singer,
                      String album, String year, String type, long size, String fileUrl,
                      String id, String albumPicUrl, String audioUrl) {
        super(fileName, title, duration, singer, album, year, type, size, fileUrl);
        this.id = id;
        this.albumPicUrl = albumPicUrl;
        this.audioUrl = audioUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumPicUrl() {
        return albumPicUrl;
    }

    public void setAlbumPicUrl(String albumPicUrl) {
        this.albumPicUrl = albumPicUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
