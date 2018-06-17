package com.earnest.ui.musicPlayer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.earnest.R;
import com.earnest.event.PlayEvent;
import com.earnest.model.entities.Song;
import com.earnest.ui.utils.DisplayUtil;
import com.earnest.ui.utils.FastBlurUtil;
import com.earnest.ui.widget.BackgourndAnimationLinearLayout;
import com.earnest.ui.widget.DiscView;
import com.earnest.utils.MusicUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MusicPlayerActivity extends AppCompatActivity implements DiscView.IPlayInfo{
    //hr:event声明和list声明
    PlayEvent playEvent;
    private List<Song> queue;


    //UI控件声明
    //重写的LinearLayout，实现动画
    private BackgourndAnimationLinearLayout mRootLayout;

    //标题栏
    ImageView ivBack;
    TextView tvTitle;
    TextView tvArtist;
    ImageView ivShare;

    //中部
    ImageView ivCD;
    DiscView mDisc;
    //动画
    private ObjectAnimator discAnimation;

    //hr:定义三种播放状态
    private static final int IDLE = 0;
    private static final int PAUSE = 1;
    private static final int START = 2;
    private int currState = IDLE;
    //private boolean isPlaying = false;

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

        //根据音乐图片制作毛玻璃背景效果，并通过一个单独的线程进行切换显示
        try2UpdateMusicPicBackground(R.drawable.timg);

        //hr:导入本地音乐数据
        queue = new ArrayList<>();
        queue=MusicUtils.getLocalMusicData(this);

    }

    /////初始化UI

    private void initUIControls() {
        //背景
        mRootLayout = (BackgourndAnimationLinearLayout) findViewById(R.id.rootLayout);
        //标题栏
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvArtist = (TextView) findViewById(R.id.tvArtist);
        ivShare = (ImageView) findViewById(R.id.ivShare);
        //中部
        //ivCD = (ImageView) findViewById(R.id.ivCD);
        mDisc = (DiscView) findViewById(R.id.discview);
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
        //hr:if(isPlaying) {
        if(true) {
            ivPlay.setImageResource(R.drawable.ic_play);
        } else {
            ivPlay.setImageResource(R.drawable.ic_pause);
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
        /*//中部
        ivCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        mDisc.setPlayInfoListener(this);
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

        /*评论*/
        ivReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MusicPlayerActivity.this,RemarkActivity.class));
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
                //hr:event播放控制
                playEvent = new PlayEvent();
                playEvent.setAction(PlayEvent.Action.PREVIOUS);
                playEvent.setQueue(queue);
                EventBus.getDefault().post(playEvent);
                switch (currState){
                    case IDLE:
                        currState=PAUSE;
                        break;
                    case START:
                        currState=PAUSE;
                        break;
                    case PAUSE:
                        break;
                }
            }
        });

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!isPlaying) {
//                    playMusic();
//                    //hr:event播放控制
//                    playEvent = new PlayEvent();
//                    playEvent.setAction(PlayEvent.Action.PLAY);
//                    playEvent.setQueue(queue);
//                    EventBus.getDefault().post(playEvent);
//                } else {
//                    pauseMusic();
//                    //hr:event播放控制
//                    playEvent = new PlayEvent();
//                    playEvent.setAction(PlayEvent.Action.STOP);
//                    EventBus.getDefault().post(playEvent);
//                }

                //hr:修改播放状态选择
                switch (currState) {
            case IDLE:
                playMusic();
                //hr:event播放控制
                playEvent = new PlayEvent();
                playEvent.setAction(PlayEvent.Action.PLAY);
                playEvent.setQueue(queue);
                EventBus.getDefault().post(playEvent);
                currState =PAUSE;
                break;
            case PAUSE:
                pauseMusic();
                //hr:event播放控制
                playEvent = new PlayEvent();
                playEvent.setAction(PlayEvent.Action.STOP);
                EventBus.getDefault().post(playEvent);
                currState = START;
                break;
            case START:
                playMusic();
                //hr:event播放控制
                playEvent = new PlayEvent();
                playEvent.setAction(PlayEvent.Action.RESUME);
                playEvent.setQueue(queue);
                EventBus.getDefault().post(playEvent);
                currState = PAUSE;
        }

            }
        });
        ivPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMusic();
                //hr:event播放控制
                playEvent = new PlayEvent();
                playEvent.setAction(PlayEvent.Action.NEXT);
                EventBus.getDefault().post(playEvent);
                switch (currState){
                    case IDLE:
                        currState=PAUSE;
                        break;
                    case START:
                        currState=PAUSE;
                        break;
                    case PAUSE:
                        break;
                }

            }
        });
        ivPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //动画设置
    private void setAnimations() {/*
        discAnimation = ObjectAnimator.ofFloat(ivCD, "rotation", 0, 360);
        discAnimation.setDuration(20000);
        discAnimation.setInterpolator(new LinearInterpolator());
        discAnimation.setRepeatCount(ValueAnimator.INFINITE);*/
    }

    //音乐控制方法，所有动画控制方法写在如下函数中
    //播放音乐
    private void playMusic() {
        //discAnimation.start();
        ivPlay.setImageResource(R.drawable.ic_pause);
        //isPlaying = true;
    }

    //暂停音乐
    private void pauseMusic() {/*
        if (discAnimation != null && discAnimation.isRunning()) {
            discAnimation.cancel();
            float valueAvatar = (float) discAnimation.getAnimatedValue();
            discAnimation.setFloatValues(valueAvatar, 360f + valueAvatar);
        }*/
        ivPlay.setImageResource(R.drawable.ic_play);
        //isPlaying = false;
    }

    //切换音乐
    private void switchMusic() {
        //discAnimation.end();
        playMusic();
        //根据音乐图片制作毛玻璃背景效果，并通过一个单独的线程进行切换显示
        try2UpdateMusicPicBackground(R.drawable.timg);
    }

    //背景图片处理，以下请勿更改
    private void try2UpdateMusicPicBackground(final int musicPicRes) {
        if (mRootLayout.isNeed2UpdateBackground(musicPicRes)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Drawable foregroundDrawable = getForegroundDrawable(musicPicRes);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRootLayout.setForeground(foregroundDrawable);
                            mRootLayout.beginAnimation();
                        }
                    });
                }
            }).start();
        }
    }

    private Drawable getForegroundDrawable(int musicPicRes) {
        /*得到屏幕的宽高比，以便按比例切割图片一部分*/
        final float widthHeightSize = (float) (DisplayUtil.getScreenWidth(MusicPlayerActivity.this)
                * 1.0 / DisplayUtil.getScreenHeight(this) * 1.0);

        Bitmap bitmap = getForegroundBitmap(musicPicRes);
        int cropBitmapWidth = (int) (widthHeightSize * bitmap.getHeight());
        int cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);

        /*切割部分图片*/
        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth,
                bitmap.getHeight());
        /*缩小图片*/
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth() / 50, bitmap
                .getHeight() / 50, false);
        /*模糊化*/
        final Bitmap blurBitmap = FastBlurUtil.doBlur(scaleBitmap, 8, true);

        final Drawable foregroundDrawable = new BitmapDrawable(blurBitmap);
        /*加入灰色遮罩层，避免图片过亮影响其他控件*/
        foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        return foregroundDrawable;
    }

    private Bitmap getForegroundBitmap(int musicPicRes) {
        int screenWidth = DisplayUtil.getScreenWidth(this);
        int screenHeight = DisplayUtil.getScreenHeight(this);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(), musicPicRes, options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        if (imageWidth < screenWidth && imageHeight < screenHeight) {
            return BitmapFactory.decodeResource(getResources(), musicPicRes);
        }

        int sample = 2;
        int sampleX = imageWidth / DisplayUtil.getScreenWidth(this);
        int sampleY = imageHeight / DisplayUtil.getScreenHeight(this);

        if (sampleX > sampleY && sampleY > 1) {
            sample = sampleX;
        } else if (sampleY > sampleX && sampleX > 1) {
            sample = sampleY;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = sample;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return BitmapFactory.decodeResource(getResources(), musicPicRes, options);
    }

    @Override
    public void onMusicInfoChanged(String musicName, String musicAuthor) {
        tvTitle.setText(musicName);
        tvArtist.setText(musicAuthor);
    }

    @Override
    public void onMusicPicChanged(int musicPicRes) {
        try2UpdateMusicPicBackground(musicPicRes);
    }

    @Override
    public void onMusicChanged(DiscView.MusicChangedStatus musicChangedStatus) {/*
        switch (musicChangedStatus) {
            case PLAY:{
                play();
                break;
            }
            case PAUSE:{
                pause();
                break;
            }
            case NEXT:{
                next();
                break;
            }
            case LAST:{
                last();
                break;
            }
            case STOP:{
                stop();
                break;
            }
        }*/
    }/*
    private void play() {
        //optMusic(MusicService.ACTION_OPT_MUSIC_PLAY);
        startUpdateSeekBarProgress();
    }

    private void pause() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PAUSE);
        stopUpdateSeekBarProgree();
    }

    private void stop() {
        stopUpdateSeekBarProgree();
        mIvPlayOrPause.setImageResource(R.drawable.ic_play);
        mTvMusicDuration.setText(duration2Time(0));
        mTvTotalMusicDuration.setText(duration2Time(0));
        mSeekBar.setProgress(0);
    }

    private void next() {
        mRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_NEXT);
            }
        }, DURATION_NEEDLE_ANIAMTOR);
        stopUpdateSeekBarProgree();
        mTvMusicDuration.setText(duration2Time(0));
        mTvTotalMusicDuration.setText(duration2Time(0));
    }

    private void last() {
        mRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_LAST);
            }
        }, DURATION_NEEDLE_ANIAMTOR);
        stopUpdateSeekBarProgree();
        mTvMusicDuration.setText(duration2Time(0));
        mTvTotalMusicDuration.setText(duration2Time(0));
    }

    private void complete(boolean isOver) {
        if (isOver) {
            mDisc.stop();
        } else {
            mDisc.next();
        }
    }*/
}
