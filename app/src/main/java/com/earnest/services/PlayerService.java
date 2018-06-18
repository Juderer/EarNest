package com.earnest.services;

import android.app.Service;
import android.content.Intent;

import android.os.IBinder;


import com.earnest.R;
import com.earnest.event.PlayEvent;
import com.earnest.manager.MusicPlayerManager;
import com.earnest.model.entities.Song;
import com.earnest.utils.MusicUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hr on 2018/6/5.
 * 音乐播放service
 */

public class PlayerService extends Service {

    List<Song> mSongList=new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
//        mSongList= MusicUtils.getLocalMusicData(this);
//        MusicPlayerManager.getPlayer().setQueue(mSongList,0);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //接收EventBus post过来的PlayEvent
    @Subscribe
    public void onEvent(PlayEvent playEvent) {
        switch (playEvent.getAction()) {
            case PLAY:
                MusicPlayerManager.getPlayer().setQueue(playEvent.getQueue(), playEvent.getMusicIndex());
                break;
            case STOP:
                MusicPlayerManager.getPlayer().pause();
                break;
            case RESUME:
                MusicPlayerManager.getPlayer().seekTo(playEvent.getSeekTo());
                MusicPlayerManager.getPlayer().resume();
                break;
            case NEXT:
                MusicPlayerManager.getPlayer().next();
                break;
            case PREVIOUS:
                MusicPlayerManager.getPlayer().previous();
                break;
            case SEEK:
                MusicPlayerManager.getPlayer().seekTo(playEvent.getSeekTo());
                break;
        }
    }


}