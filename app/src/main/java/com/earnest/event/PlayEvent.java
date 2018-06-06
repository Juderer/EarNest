package com.earnest.event;

import com.earnest.model.entities.Song;

import java.util.List;

/**
 * Created by DELL on 2018/6/4.
 */

public class PlayEvent {
    public enum Action {
        PLAY,STOP,RESUME,NEXT,PREVIOUS,SEEK
    }

    private Action mAction;
    private Song mSong;
    private List<Song> mQueue;
    private int seekTo;

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

}
