package com.earnest.services;

import android.app.Service;
import android.content.Intent;

import android.os.IBinder;


import com.earnest.R;
import com.earnest.event.PlayEvent;
import com.earnest.manager.MusicPlayerManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Created by hr on 2018/6/5.
 * 音乐播放service
 */

public class PlayerService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
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
                MusicPlayerManager.getPlayer().setQueue(playEvent.getQueue(), 0);
                break;
            case STOP:
                MusicPlayerManager.getPlayer().pause();
                break;
            case RESUME:
                MusicPlayerManager.getPlayer().resume();
                break;
            case NEXT:
                MusicPlayerManager.getPlayer().next();
                break;
            case PREVIOUS:
                MusicPlayerManager.getPlayer().previous();
                break;
        }
    }


}