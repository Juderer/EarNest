package com.earnest.ui.musicPlayer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.earnest.R;

public class MusicPlayerActivity extends AppCompatActivity {

    //UI控件声明

    //标题栏
    ImageView ivBack;
    TextView tvTitle;
    TextView tvArtist;
    ImageView ivShare;

    //中部
    ImageView ivCD;
    //动画
    private ObjectAnimator discAnimation;
    private boolean isPlaying = false;

    //功能栏
    ImageView ivFavoriate;
    ImageView ivDownload;
    ImageView ivReview;
    ImageView ivMore;

    //滚动条
    TextView tvProgress;
    SeekBar seek_bar;
    TextView tvDuration;

    //控制栏
    ImageView ivPlayMode;
    ImageView ivPlayLast;
    ImageView ivPlay;
    ImageView ivPlayNext;
    ImageView ivPlayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        //getSupportActionBar().hide();//隐藏标题栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        initUIControls();
        setAnimations();
    }

    /////初始化UI

    private void initUIControls() {
        //标题栏
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvArtist = (TextView) findViewById(R.id.tvArtist);
        ivShare = (ImageView) findViewById(R.id.ivShare);
        //中部
        ivCD = (ImageView) findViewById(R.id.ivCD);
        //功能栏
        ivFavoriate = (ImageView) findViewById(R.id.ivFavoriate);
        ivDownload = (ImageView) findViewById(R.id.ivDownload);
        ivReview = (ImageView) findViewById(R.id.ivReview);
        ivMore = (ImageView) findViewById(R.id.ivMore);
        //滚动条
        tvProgress = (TextView) findViewById(R.id.tvProgress);
        seek_bar = (SeekBar) findViewById(R.id.seek_bar);
        tvDuration = (TextView) findViewById(R.id.tvDuration);
        //控制栏
        ivPlayMode = (ImageView) findViewById(R.id.ivPlayMode);
        ivPlayLast = (ImageView) findViewById(R.id.ivPlayLast);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivPlayNext = (ImageView) findViewById(R.id.ivPlayNext);
        ivPlayList = (ImageView) findViewById(R.id.ivPlayList);

        //图片初始化
        if(isPlaying) {
            ivPlay.setImageResource(R.drawable.ic_pause);
        } else {
            ivPlay.setImageResource(R.drawable.ic_play);
        }

        //设置监听事件
        setUIControlsOnClick();
    }

    private void setUIControlsOnClick() {
        //标题栏
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //中部
        ivCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //功能栏
        ivFavoriate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //滚动条
        //控制栏
        ivPlayMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivPlayLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMusic();
            }
        });
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPlaying) {
                    playMusic();
                } else {
                    pauseMusic();
                }
            }
        });
        ivPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMusic();
            }
        });
        ivPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //动画设置
    private void setAnimations() {
        discAnimation = ObjectAnimator.ofFloat(ivCD, "rotation", 0, 360);
        discAnimation.setDuration(20000);
        discAnimation.setInterpolator(new LinearInterpolator());
        discAnimation.setRepeatCount(ValueAnimator.INFINITE);
    }

    //音乐控制方法，所有控制方法写在如下函数中
    //播放音乐
    private void playMusic() {
        discAnimation.start();
        ivPlay.setImageResource(R.drawable.ic_pause);
        isPlaying = true;
    }

    //暂停音乐
    private void pauseMusic() {
        if (discAnimation != null && discAnimation.isRunning()) {
            discAnimation.cancel();
            float valueAvatar = (float) discAnimation.getAnimatedValue();
            discAnimation.setFloatValues(valueAvatar, 360f + valueAvatar);
        }
        ivPlay.setImageResource(R.drawable.ic_play);
        isPlaying = false;
    }

    //切换音乐
    private void switchMusic() {
        discAnimation.end();
        playMusic();
    }
}
