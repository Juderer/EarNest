package com.earnest.ui.musicPlayer;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.earnest.R;
import com.earnest.event.MessageEvent;
import com.earnest.event.PlayEvent;
import com.earnest.manager.MusicPlayerManager;
import com.earnest.model.WechatShare;
import com.earnest.model.entities.Song;
import com.earnest.ui.utils.DisplayUtil;
import com.earnest.ui.utils.FastBlurUtil;
import com.earnest.ui.widget.BackgourndAnimationLinearLayout;
import com.earnest.ui.widget.DiscView;
import com.earnest.utils.MusicUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import static com.earnest.ui.home.MainActivity.currState;
import static com.earnest.ui.search.SearchResultActivity.currNetMusicArtist;
import static com.earnest.ui.search.SearchResultActivity.currNetMusicName;

import static com.earnest.ui.widget.DiscView.DURATION_NEEDLE_ANIAMTOR;


public class MusicPlayerActivity extends AppCompatActivity implements DiscView.IPlayInfo{

    //zsl: 微信分享
    private WechatShare wechatShare;

    //收藏歌单
    public static List<Song> favoriateSongList = new ArrayList<>();


    //hr:event声明和list声明
    PlayEvent playEvent;
    private List<Song> queue;
    private List<Song> list;
    //当前音乐索引
    private int currPosition;
    //当前音乐进度位置
    private int playPositon;

    private int playMode = 0; //顺序播放

    //音乐列表
    private View home_bottomMusicPlayer;
    private ImageView ivBottomPlayerList;
    private AlertDialog bottomAlertDialog;
    private AlertDialog.Builder bottomListBuilder;
    private ImageView iv_bottomPlayerMode;
    private ImageView iv_bottomPlayerDeleteAll;

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
   // private int currState = IDLE;


    //功能栏
    ImageView ivFavoriate;
    ImageView ivDownload;
    ImageView ivReview;
    ImageView ivMore;

    //滚动条
    TextView tvProgress;
    SeekBar seek_bar;
    TextView tvDuration;
    //hr:进度条相关
    private Timer timer;
    private boolean isSeekBarChanging;//互斥变量 防止进度条与定时器冲突
    private static final int UPDATE_TIME = 0x1;//更新播放事件的标记

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

        //hr:注册eventbus订阅
        EventBus.getDefault().register(this);

        //hr:导入本地音乐数据
        queue = new ArrayList<>();
        queue = MusicUtils.getLocalMusicData(this);


        initUIControls();

        //hr:绑定handler
        myHandler = new MyHandler(this);

        //根据音乐图片制作毛玻璃背景效果，并通过一个单独的线程进行切换显示
//        String str = ImgDownload.dir + "123.jpg";
//        try2UpdateMusicPicBackground(str);


    }

    /////初始化UI

    private void initUIControls() {
        //zsl: 微信分享
        wechatShare = WechatShare.getInstance(this);

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
        mDisc.setSongList(queue);
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
        if (true) {
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
                boolean result = true;
                // 微信分享
                if (wechatShare.isSupportWX()) {
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_wechat_share);
                    result = wechatShare.sharePic(bmp, SendMessageToWX.Req.WXSceneTimeline);
                } else {
                    Toast.makeText(MusicPlayerActivity.this, "手机上微信版本不支持分享到朋友圈", Toast.LENGTH_SHORT).show();
                }
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
        //收藏音乐操作
        ivFavoriate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivFavoriate.setImageResource(R.drawable.ic_favorite_yes);
                Song song = MusicPlayerManager.getPlayer().getQueue().get(MusicPlayerManager.getPlayer().getCurrentMusicIndex());
                System.out.println(song.getTitle());
                if (favoriateSongList.size() == 0) {
                    favoriateSongList.add(song);
                } else {
                    if (!favoriateSongList.contains(song))
                        favoriateSongList.add(song);
                }

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
                startActivity(new Intent(MusicPlayerActivity.this, RemarkActivity.class));
            }
        });
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //滚动条
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = false;
                MusicPlayerManager.getPlayer().seekTo(seekBar.getProgress());//???直接调用
            }
        });
        //控制栏
        ivPlayMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playMode == 0) {
                    ivPlayMode.setImageResource(R.drawable.ic_play_mode_shuffle);
                    //MusicPlayerManager.getPlayer().setPlayMode();
                    playMode = 1;
                } else if (playMode == 1) {
                    ivPlayMode.setImageResource(R.drawable.ic_play_mode_loop);
                    playMode = 2;
                } else if (playMode == 2) {
                    ivPlayMode.setImageResource(R.drawable.ic_play_mode_list);
                    playMode = 0;
                }
            }
        });
        ivPlayLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDisc.last();
            }
        });

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDisc.playOrPause();
            }
        });



        ivPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDisc.next();
            }
        });

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if ((MusicPlayerManager.getPlayer().getQueue()) != null && !(MusicPlayerManager.getPlayer().getQueue()).isEmpty()) {
                    if (!isSeekBarChanging) {
                        seek_bar.setProgress(MusicPlayerManager.getPlayer().getCurrentPosition());
                        //Handler用于更新已经播放时间
                        Message msg = myHandler.obtainMessage(UPDATE_TIME);//用于更新已经播放时间
                        msg.arg1 = seek_bar.getProgress();//用于更新已经播放时间
                        myHandler.sendMessage(msg);//用于更新已经播放时间

                    }
                }
            }
        }, 0, 50);

        ivPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomMusicList();
            }
        });
    }

    /* 底部歌曲列表显示*/
    protected void showBottomMusicList() {
        list = queue;
        Context context = MusicPlayerActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.bottom_music_list, null);
        ListView bottomListView = (ListView) layout.findViewById(R.id.lv_bottomMusicListview);
        MusicPlayerActivity.BottomMusicListAdapter adapter = new MusicPlayerActivity.BottomMusicListAdapter(context, list);
        bottomListView.setAdapter(adapter);

        //点击列表中某一歌曲--播放歌曲
        bottomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long id) {

                //在这里面就是执行点击后要进行的操作
                //返回listview的下标
                int currpisition = positon;
                Log.d("hr01", String.valueOf(currpisition));
                //把消息psot出去
                playEvent = new PlayEvent();
                playEvent.setAction(PlayEvent.Action.PLAY);
                playEvent.setQueue(list);
                playEvent.setMusicIndex(currpisition);
                EventBus.getDefault().post(playEvent);
            }
        });

        bottomListBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.BottomAlertDialog));
        bottomListBuilder.setView(layout);
        bottomAlertDialog = bottomListBuilder.create();

        /* 更换播放模式 */
        iv_bottomPlayerMode = (ImageView) layout.findViewById(R.id.iv_bottomPlayerMode);
        iv_bottomPlayerMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playMode == 0) {
                    iv_bottomPlayerMode.setImageResource(R.drawable.bottom_music_list_play_mode_shuffle);
                    //MusicPlayerManager.getPlayer().setPlayMode();
                    playMode = 1;
                } else if (playMode == 1) {
                    iv_bottomPlayerMode.setImageResource(R.drawable.bottom_music_list_play_mode_loop);
                    playMode = 2;
                } else if (playMode == 2) {
                    iv_bottomPlayerMode.setImageResource(R.drawable.bottom_music_list_play_mode_list);
                    playMode = 0;
                }
            }
        });

        /* 清空列表*/
        iv_bottomPlayerDeleteAll = (ImageView) layout.findViewById(R.id.iv_bottomPlayerDeleteAll);
        iv_bottomPlayerDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        /*  关闭歌曲列表 */
        Button btnBottomMusicListClose = (Button) layout.findViewById(R.id.btn_bottomMusicListClose);
        btnBottomMusicListClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomAlertDialog.dismiss();
            }
        });


        //显示
        bottomAlertDialog.show();

        //设置大小、位置
        WindowManager.LayoutParams layoutParams = bottomAlertDialog.getWindow().getAttributes();
        bottomAlertDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        bottomAlertDialog.getWindow().setAttributes(layoutParams);
        bottomAlertDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomAlertDialog.getWindow().setWindowAnimations(R.style.BottomDialogAnimation);

    }

    //底部音乐列表的适配器
    class BottomMusicListAdapter extends BaseAdapter {
        private List<Song> mlist = new ArrayList<>();
        private Context mContext;

        public BottomMusicListAdapter(Context context, List<Song> list) {
            this.mContext = context;
            this.mlist = list;
        }

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Song getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MusicPlayerActivity.BottomMusicListAdapter.Songs songs = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.item_bottom_music_list_item, null);

                songs = new MusicPlayerActivity.BottomMusicListAdapter.Songs();
                songs.num = (TextView) convertView.findViewById(R.id.tv_bottomMusicListItemNumber);
                songs.musicname = (TextView) convertView.findViewById(R.id.tv_bottomMusicListItemMusicName);
                songs.singer = (TextView) convertView.findViewById(R.id.tv_bottomMusicListItemMusicSinger);
                songs.delete = (ImageView) convertView.findViewById(R.id.iv_bottomMusicListItemDelete);

                convertView.setTag(songs);
            } else {
                songs = (MusicPlayerActivity.BottomMusicListAdapter.Songs) convertView.getTag();
            }

            songs.num.setText(String.valueOf(position + 1));
            songs.musicname.setText(list.get(position).getTitle());
            songs.singer.setText(list.get(position).getSinger());

            //点击回收箱按钮从列表中删除歌曲
            songs.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return convertView;
        }

        class Songs {
            TextView num;
            TextView musicname;
            TextView singer;
            ImageView delete;
        }
    }

    //背景图片处理，以下请勿更改
    private void try2UpdateMusicPicBackground(final String musicPicRes) {
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

    private Drawable getForegroundDrawable(String musicPicRes) {
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

    private Bitmap getForegroundBitmap(String musicPicRes) {
        int screenWidth = DisplayUtil.getScreenWidth(this);
        int screenHeight = DisplayUtil.getScreenHeight(this);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //BitmapFactory.decodeResource(getResources(), musicPicRes, options);
        BitmapFactory.decodeFile(musicPicRes, options);

        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        if (imageWidth < screenWidth && imageHeight < screenHeight) {
            //return BitmapFactory.decodeResource(getResources(), musicPicRes);
            return BitmapFactory.decodeFile(musicPicRes);
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

        //return BitmapFactory.decodeResource(getResources(), musicPicRes, options);
        return BitmapFactory.decodeFile(musicPicRes, options);
    }

    //hr:接收过来的MessageEvent 解决获取不到MusicPlayermanager queue
    @Subscribe
    public void onEvent(MessageEvent mMessageEvent) {
        if ((MusicPlayerManager.getPlayer().getQueue()) != null && !(MusicPlayerManager.getPlayer().getQueue()).isEmpty()) {
            int i = MusicPlayerManager.getPlayer().getQueue().size();
            currPosition = MusicPlayerManager.getPlayer().getCurrentMusicIndex();
            Song song = MusicPlayerManager.getPlayer().getQueue().get(currPosition);
            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getSinger());
            tvDuration.setText(MusicUtils.formatTime(song.getDuration()));
            seek_bar.setProgress(MusicPlayerManager.getPlayer().getCurrentPosition());//设置当前进度为0
            seek_bar.setMax((int) song.getDuration());//设置进度条最大值为MP3总时间

            if (MusicPlayerManager.getPlayer().getMediaPlayer().isPlaying()) {
                ivPlay.setImageResource(R.drawable.ic_pause);
                currState = PAUSE;
            } else {
                ivPlay.setImageResource(R.drawable.ic_play);
                currState = START;
            }
        } else {
            ivPlay.setImageResource(R.drawable.ic_play);
            currState = IDLE;
        }

    }

    //hr:每次进入界面获取一次音乐信息
    @Override
    public void onResume() {
        super.onResume();
        if ((MusicPlayerManager.getPlayer().getQueue()) != null && !(MusicPlayerManager.getPlayer().getQueue()).isEmpty()) {
            int i = MusicPlayerManager.getPlayer().getCurrentMusicIndex();
            Song song = MusicPlayerManager.getPlayer().getQueue().get(i);
            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getSinger());
            tvDuration.setText(MusicUtils.formatTime(song.getDuration()));
            seek_bar.setProgress(MusicPlayerManager.getPlayer().getCurrentPosition());//设置当前进度为0
            seek_bar.setMax((int) song.getDuration());//设置进度条最大值为MP3总时间
            seek_bar.setMax((int)song.getDuration());//设置进度条最大值为MP3总时间


        }else {

            if (MusicPlayerManager.getPlayer().getMediaPlayer().isPlaying()) {
                tvTitle.setText(currNetMusicName);
                tvArtist.setText(currNetMusicArtist);

            } else {
                if(currState==IDLE){
                }else {
                    currState=START;
                }
            }
        }

        if (MusicPlayerManager.getPlayer().getMediaPlayer().isPlaying()) {
            ivPlay.setImageResource(R.drawable.ic_pause);
            currState = PAUSE;
            Log.d("09","  currState = PAUSE;");
        } else {
            if (currState == IDLE) {
            } else {
                ivPlay.setImageResource(R.drawable.ic_play);
                currState = START;
            }
        }





    }

    //hr:用于更新进度条时间
    private static MyHandler myHandler;

    static class MyHandler extends Handler {
        private MusicPlayerActivity mMusicPlayerActivity;

        public MyHandler(MusicPlayerActivity mMusicPlayerActivity) {
            this.mMusicPlayerActivity = mMusicPlayerActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mMusicPlayerActivity != null) {
                switch (msg.what) {
                    case UPDATE_TIME://更新时间(已经播放时间)
                        mMusicPlayerActivity.tvProgress.setText(MusicUtils.formatTime(msg.arg1));
                        break;
                }
            }
        }
    }

    ///////////////////////DiscView/////////////////////
    @Override
    public void onMusicInfoChanged(String musicName, String musicAuthor) {
        tvTitle.setText(musicName);
        tvArtist.setText(musicAuthor);
    }

    @Override
    public void onMusicPicChanged(int musicPicRes) {
        //try2UpdateMusicPicBackground(musicPicRes);
    }

    @Override
    public void onMusicChanged(DiscView.MusicChangedStatus musicChangedStatus) {
        switch (musicChangedStatus) {
            case PLAY: {
                Log.d("07","play()");
                play();
                break;
            }
            case PAUSE: {
                Log.d("07","pause(");
                pause();
                break;
            }
            case NEXT: {
                next();
                Log.d("07","next();");
                break;
            }
            case LAST: {
                Log.d("07"," last();");
                last();
                break;
            }
            case STOP: {
                Log.d("07","stop();");
                stop();
                break;
            }
        }
    }

    private void play() {
        //optMusic(MusicService.ACTION_OPT_MUSIC_PLAY);
//        startUpdateSeekBarProgress();
        //hr:修改播放状态选择
        if(currState == IDLE) {
            Toast.makeText(getApplicationContext(), "未指定歌曲，从本地音乐第一首开始放起", Toast.LENGTH_LONG).show();
            ivPlay.setImageResource(R.drawable.ic_pause);
            currState = PAUSE;
            //hr:event播放控制
            playEvent = new PlayEvent();
            playEvent.setAction(PlayEvent.Action.PLAY);
            playEvent.setQueue(queue);
            EventBus.getDefault().post(playEvent);
        } else if (currState == START) {
            ivPlay.setImageResource(R.drawable.ic_pause);
            currState = PAUSE;
            if ((MusicPlayerManager.getPlayer().getQueue()) != null && !(MusicPlayerManager.getPlayer().getQueue()).isEmpty()) {
                //hr:event播放控制
                playEvent = new PlayEvent();
                playEvent.setAction(PlayEvent.Action.RESUME);
                playEvent.setQueue(queue);
                playEvent.setSeekTo(playPositon);
                EventBus.getDefault().post(playEvent);
            } else {
                playEvent = new PlayEvent();
                playEvent.setAction(PlayEvent.Action.RESUME);
                playEvent.setTestNet(PlayEvent.TestNet.NET);
                EventBus.getDefault().post(playEvent);
            }
        }
    }

    private void pause() {
//        stopUpdateSeekBarProgree();
        //hr:修改播放状态选择
        if(currState == PAUSE) {
            ivPlay.setImageResource(R.drawable.ic_play);
            currState = START;
            Log.d("10","currState = START;");
            if ((MusicPlayerManager.getPlayer().getQueue()) != null && !(MusicPlayerManager.getPlayer().getQueue()).isEmpty()) {
                //hr:event播放控制
                playEvent = new PlayEvent();
                playEvent.setAction(PlayEvent.Action.STOP);
                EventBus.getDefault().post(playEvent);
                //进度条相关
                //playPositon = MusicPlayerManager.getPlayer().getCurrentPosition();
                timer.purge();
            } else {
                playEvent = new PlayEvent();
                playEvent.setAction(PlayEvent.Action.STOP);
                EventBus.getDefault().post(playEvent);
            }
        }
    }

    private void stop() {
//        stopUpdateSeekBarProgree();
//        mIvPlayOrPause.setImageResource(R.drawable.ic_play);
//        mTvMusicDuration.setText(duration2Time(0));
//        mTvTotalMusicDuration.setText(duration2Time(0));
//        mSeekBar.setProgress(0);
    }

    private void next() {
        mRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
//                optMusic(MusicService.ACTION_OPT_MUSIC_NEXT);
            }
        }, DURATION_NEEDLE_ANIAMTOR);
//        stopUpdateSeekBarProgree();
//        mTvMusicDuration.setText(duration2Time(0));
//        mTvTotalMusicDuration.setText(duration2Time(0));
        if ((MusicPlayerManager.getPlayer().getQueue()) != null && !(MusicPlayerManager.getPlayer().getQueue()).isEmpty()) {
            //hr:event播放控制
            ivPlay.setImageResource(R.drawable.ic_pause);
            //currState = PAUSE;
            //根据音乐图片制作毛玻璃背景效果，并通过一个单独的线程进行切换显示
            //try2UpdateMusicPicBackground(R.drawable.timg);

            playEvent = new PlayEvent();
            playEvent.setAction(PlayEvent.Action.NEXT);
            EventBus.getDefault().post(playEvent);
            switch (currState) {
                case IDLE:
                    currState = PAUSE;
                    break;
                case START:
                    currState = PAUSE;
                    break;
                case PAUSE:
                    currState = START;
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), "当前为试听歌曲，无法跳转下一首，请先在列表中选择歌曲", Toast.LENGTH_LONG).show();
        }
    }

    private void last() {
        mRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
//                optMusic(MusicService.ACTION_OPT_MUSIC_LAST);
            }
        }, DURATION_NEEDLE_ANIAMTOR);
//        stopUpdateSeekBarProgree();
//        mTvMusicDuration.setText(duration2Time(0));
//        mTvTotalMusicDuration.setText(duration2Time(0));
        if ((MusicPlayerManager.getPlayer().getQueue()) != null && !(MusicPlayerManager.getPlayer().getQueue()).isEmpty()) {
            ivPlay.setImageResource(R.drawable.ic_pause);
            currState = PAUSE;
            //根据音乐图片制作毛玻璃背景效果，并通过一个单独的线程进行切换显示
            //try2UpdateMusicPicBackground(R.drawable.timg);

            //hr:event播放控制
            playEvent = new PlayEvent();
            playEvent.setAction(PlayEvent.Action.PREVIOUS);
            playEvent.setQueue(queue);
            EventBus.getDefault().post(playEvent);
            switch (currState) {
                case IDLE:
                    currState = PAUSE;
                    break;
                case START:
                    currState = PAUSE;
                    break;
                case PAUSE:
                    currState = START;
                    break;
            }
        } else {
            Log.d("rr", "yi");
            Toast.makeText(getApplicationContext(), "当前为试听歌曲，无法跳转上一首，请先在列表中选择歌曲", Toast.LENGTH_LONG).show();
        }
    }

    private void complete(boolean isOver) {
        if (isOver) {
            mDisc.stop();
        } else {
            mDisc.next();
        }
    }

}
