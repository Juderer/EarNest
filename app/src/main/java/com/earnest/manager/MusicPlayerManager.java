package com.earnest.manager;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.earnest.event.MessageEvent;
import com.earnest.model.entities.Song;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hr on 2018/6/5.
 * 播放控制类
 */

public class MusicPlayerManager implements MediaPlayer.OnCompletionListener {
    private MessageEvent mMessageEvent=new MessageEvent();

    private static MusicPlayerManager player = new MusicPlayerManager();

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private List<Song> mQueue;
    private int mQueueIndex;
    private PlayMode mPlayMode;

    private enum PlayMode {
        LOOP, RANDOM, REPEAT
    }

    public static MusicPlayerManager getPlayer() {
        return player;
    }

    public static void setPlayer(MusicPlayerManager player) {
        MusicPlayerManager.player = player;
    }

    public MusicPlayerManager() {

        mMediaPlayer= new ManagedMediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);

        mQueue = new ArrayList<>();
        mQueueIndex = 0;

        mPlayMode = PlayMode.LOOP;
    }

    public MediaPlayer getMediaPlayer(){
        return mMediaPlayer;
    }


    public void setQueue(List<Song> queue, int index) {
        mQueue = queue;
        mQueueIndex = index;
        Log.d("hr01-1",String.valueOf(mQueueIndex));
        play(getNowPlaying());
    }

    //hr:618
    public List<Song> getQueue() {
        return mQueue;
    }

    public void play(Song song) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(song.getFileUrl());
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                    Log.d("hr01-3",String.valueOf(mQueueIndex));
                    EventBus.getDefault().post(mMessageEvent);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void resume() {
        mMediaPlayer.start();
    }

    public void next() {
        play(getNextSong());
        //EventBus.getDefault().post(mMessageEvent);
    }

    public void previous() {
        play(getPreviousSong());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    private Song getNowPlaying() {
        if (mQueue.isEmpty()) {
            return null;
        }
        //Log.d("hr01-2",String.valueOf(mQueueIndex));
        return mQueue.get(mQueueIndex);
    }

    private Song getNextSong() {
        if (mQueue.isEmpty()) {
            return null;
        }
        switch (mPlayMode) {
            case LOOP:
                return mQueue.get(getNextIndex());
            case RANDOM:
                return mQueue.get(getRandomIndex());
            case REPEAT:
                return mQueue.get(mQueueIndex);
        }
        return null;
    }

    private Song getPreviousSong() {
        if (mQueue.isEmpty()) {
            return null;
        }
        switch (mPlayMode) {
            case LOOP:
                return mQueue.get(getPreviousIndex());
            case RANDOM:
                return mQueue.get(getRandomIndex());
            case REPEAT:
                return mQueue.get(mQueueIndex);
        }
        return null;
    }

    //获取当前播放歌曲index
    public int getCurrentMusicIndex(){
        return mQueueIndex;
    }

    //hr:获取当前进度
    public int getCurrentPosition() {
        if (getNowPlaying() != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (getNowPlaying() != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(PlayMode playMode) {
        mPlayMode = playMode;
    }

    private int getNextIndex() {
        mQueueIndex = (mQueueIndex + 1) % mQueue.size();
        return mQueueIndex;
    }

    private int getPreviousIndex() {
        if((mQueueIndex-1)>=0){
            mQueueIndex--;
            Log.d("ju",String.valueOf(mQueueIndex));

        }else{
            mQueueIndex = mQueue.size()-1;

        }

        return mQueueIndex;
    }

    private int getRandomIndex() {
        mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
        return mQueueIndex;
    }

    private void release() {
        mMediaPlayer.release();
        mMediaPlayer = null;
        mContext = null;
    }

    public void seekTo(int mec){
        mMediaPlayer.seekTo(mec);
    }

}
