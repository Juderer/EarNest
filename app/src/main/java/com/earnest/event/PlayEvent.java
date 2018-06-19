package com.earnest.event;

import android.net.Uri;

import com.earnest.model.entities.Song;

import java.util.List;

/**
 * Created by DELL on 2018/6/4.
 */

public class PlayEvent {
    public enum Action {
        PLAY,STOP,RESUME,NEXT,PREVIOUS,SEEK
    }

    public enum TestNet{
        NET,NOTNET
    }

    private Action mAction;
    private TestNet mTestNet=TestNet.NOTNET;
    private Uri netSongUri;
    private Song mSong;
    private List<Song> mQueue;
    private int seekTo=0;
    private int musicIndex=0;

    public Song getSong(){
        return mSong;
    }

    public void setSong(Song song){
        mSong=song;
    }

    public Action getAction(){
        return mAction;
    }

    public void setAction(Action action){
        mAction = action;
    }

    public TestNet getTestNet(){
        return mTestNet;
    }

    public void setTestNet(TestNet testNet){
        mTestNet = testNet;
    }



    public List<Song> getQueue() {
        return mQueue;
    }

    public void setQueue(List<Song> queue) {
        mQueue = queue;
    }

    public int getSeekTo() {
        return seekTo;
    }

    public void setSeekTo(int seekTo) {
        this.seekTo = seekTo;
    }

    public int getMusicIndex() {
        return musicIndex;
    }

    public void setMusicIndex(int musicIndex) {
        this.musicIndex = musicIndex;
    }

    public Uri getNetSongUri(){return netSongUri;}

   public void setNetSongUri(Uri uri){
       this.netSongUri=uri;
   }



}
