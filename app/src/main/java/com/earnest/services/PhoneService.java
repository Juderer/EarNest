package com.earnest.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.earnest.event.PlayEvent;
import com.earnest.manager.MusicPlayerManager;

import org.greenrobot.eventbus.EventBus;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static com.earnest.event.PlayEvent.Action;
import static com.earnest.event.PlayEvent.Action.RESUME;
import static com.earnest.event.PlayEvent.Action.STOP;

/**
 * Created by yzp on 2018/6/10.
 * 电话接入处理service
 */
public class PhoneService extends Service{
    private boolean isChangeToPause = false;
    private TelephonyManager tm;    //电话管理器
    private MyListener listener;    //监听器对象
    //private AudioManager ams = null;    //音频管理器

    PlayEvent playEvent=new PlayEvent();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 服务创建的时候调用的方法
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //后台监听电话的呼叫状态
        //得到电话管理器
        tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        //initAudio();
    }

//    //微信，QQ通话监听
//    private void initAudio(){
//        ams = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        ams.getMode();//这里getmode返回值为3时代表，接通qq或者微信电话
//        ams.requestAudioFocus( mAudioFocusListener,1,1);
//    }

    private class MyListener extends PhoneStateListener{
        //当电话的呼叫状态发生变化的时候调用的方法

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.d("yzp111", "state" + state);
            try {
                switch (state){
                    case TelephonyManager.CALL_STATE_IDLE:  //空闲状态
                        //继续播放音乐
                        playEvent.setAction(RESUME);
                       int  currPlayPosition=MusicPlayerManager.getPlayer().getCurrentPosition();
                        playEvent.setSeekTo(currPlayPosition);
                        EventBus.getDefault().post(playEvent);
                        Log.v("PhoneService","空闲状态");
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:   //响铃状态
                        //暂停播放音乐
                        playEvent.setAction(STOP);
                        EventBus.getDefault().post(playEvent);
                        Log.v("PhoneService","响铃状态");
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:   //通话状态
                        //暂停播放音乐
                        playEvent.setAction(STOP);
                        EventBus.getDefault().post(playEvent);
                        Log.v("PhoneService","通话状态");
                        break;
                    default:
                        break;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

//    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
//        @Override
//        public void onAudioFocusChange(int focusChange) {
//            Log.d("yzp111","focusChange----------" + focusChange);
//
//            if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
//                //暂时失去AudioFocus，可以很快再次获取AudioFocus，可以不释放播放资源,只需暂停播放
//                    //暂停音乐
////            }else if (focusChange == AUDIOFOCUS_GAIN){     //获取了AudioFocus，如果当前处于播放暂停状态，并且这个暂停状态不是用户手动点击的暂停，才会继续播放
////                if (AudioPlayerService.status)
//            }else if (focusChange == AUDIOFOCUS_LOSS){
//                // 会长时间的失去AudioFoucs,就不在监听远程播放
//                if ()
//            }
//        }
//    };

    /**
     * 服务销毁的时候调用的方法
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消电话的监听
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        listener = null;
        //ams.abandonAudioFocusRequest(mAudioFocusListener);
    }
}
