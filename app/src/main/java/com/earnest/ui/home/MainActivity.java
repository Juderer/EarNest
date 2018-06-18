package com.earnest.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.earnest.R;
import com.earnest.event.MessageEvent;
import com.earnest.event.PlayEvent;
import com.earnest.manager.MusicPlayerManager;
import com.earnest.model.entities.Item_Song;

import com.earnest.model.entities.Song;
import com.earnest.services.PlayerService;
import com.earnest.ui.adapter.BaseFragment;
import com.earnest.ui.adapter.MainPagerAdapter;
import com.earnest.ui.home.menuFragments.FindFragment;
import com.earnest.ui.home.menuFragments.PlayFragment;
import com.earnest.ui.home.menuFragments.VideoFragment;
import com.earnest.ui.musicPlayer.MusicPlayerActivity;
import com.earnest.ui.search.SearchActivity;
import com.earnest.utils.MusicUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int REQUEST_READ_PHONE_STATE  = 0;

    //顶部标题栏
    ImageView ivMenuMy;
    ImageView ivMenuPlay;
    ImageView ivMenuFind;
    ImageView ivMenuVideo;
    ImageView ivMenuSearch;

    //ToorBar,ViewPager,Fragment
    ViewPager viewPager;
    BaseFragment[] fragments;
    private final static int pages = 3;
    private final static int PlayFragmentPosition = 0;
    private final static int FindFragmentPosition = 1;
    private final static int VideoFragmentPosition = 2;

    //底部音乐栏部分
    private ImageView ivBottomPlay;  //底栏播放暂停按钮
    //hr:定义三种播放状态
    private static final int IDLE = 0;
    private static final int PAUSE = 1;
    private static final int START = 2;
    private int currState = IDLE;
    private int playMode = 0; //顺序播放

    //hr:底部音乐栏信息
    private TextView tv_bottomPlayerMusicName;
    private TextView tv_bottomPlayerLyrics;//歌词？

    //hr:playevent声明
    PlayEvent playEvent;
    List<Song> queue;

    /* 底部音乐列表*/
    private View home_bottomMusicPlayer;
    private ImageView ivBottomPlayerList;
    private List<Song> list = new ArrayList<>();
    private AlertDialog.Builder bottomListBuilder;
    private AlertDialog bottomAlertDialog;
    private ImageView iv_bottomPlayerMode;
    private ImageView iv_bottomPlayerDeleteAll;

    /*个人设置*/
    private PopupWindow popPersonalSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //hr:动态申请权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE} , 1);
        }

        //hr:开始服务
        startService(new Intent(this, PlayerService.class));
        //hr:订阅传过来的MessageEvent以改变音乐信息
        EventBus.getDefault().register(this);


        // 进入MainActivity后，需要结束StartActivity
        // 进入MainActivity的唯一路径就是从StartActivity中进入
        //StartActivity.instance.finish();

        initUIControls();



        //hr:导入音乐列表 这里为本地音乐数据,在初识列表里没有音乐时使用
        queue = new ArrayList<>();
        queue= MusicUtils.getLocalMusicData(this);

    }

    /////初始化UI
    private void initUIControls() {
        //Fragment
        fragments = new BaseFragment[pages];
        fragments[PlayFragmentPosition] = new PlayFragment();
        fragments[FindFragmentPosition] = new FindFragment();
        fragments[VideoFragmentPosition] = new VideoFragment();
        //ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        inflateViewPager();

        //顶部标题栏
        ivMenuMy = (ImageView) findViewById(R.id.ivMenuMy);
        ivMenuPlay = (ImageView) findViewById(R.id.ivMenuPlay);
        ivMenuFind = (ImageView) findViewById(R.id.ivMenuFind);
        ivMenuVideo = (ImageView) findViewById(R.id.ivMenuVideo);
        ivMenuSearch = (ImageView) findViewById(R.id.ivMenuSearch);

        //底部播放栏部分
        home_bottomMusicPlayer = (View)findViewById(R.id.home_bottomMusicPlayer);
        ivBottomPlay = (ImageView) findViewById(R.id.iv_bottomPlayerPlay);
        ivBottomPlayerList = (ImageView) findViewById(R.id.iv_bottomPlayerList);

        tv_bottomPlayerMusicName=(TextView)findViewById(R.id.tv_bottomPlayerMusicName);
        tv_bottomPlayerLyrics=(TextView)findViewById(R.id.tv_bottomPlayerLyrics);


        //设置监听事件
        setUIControlsOnClick();
    }

    private void setUIControlsOnClick() {
        //顶部标题栏

        /*个人设置*/
        ivMenuMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPopupWindow();
                //popPersonalSetting.showAtLocation(findViewById(R.id.), Gravity.CENTER, 0, 0);
                popPersonalSetting.showAtLocation(getLayoutInflater().inflate(R.layout.activity_main, null), Gravity.LEFT, 0, 500);
            }
        });

        /*我的*/
        ivMenuPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPageSelection(PlayFragmentPosition);
            }
        });

        /*发现主页*/
        ivMenuFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPageSelection(FindFragmentPosition);
            }
        });

        /*视频*/
        ivMenuVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPageSelection(VideoFragmentPosition);
            }
        });

        /* 搜索 */
        ivMenuSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        //底部播放栏部分
        /* 点击底部播放器转换至播放界面 */
        home_bottomMusicPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MusicPlayerActivity.class));
            }
        });

        /* 底栏播放按钮 */
        ivBottomPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currState) {
                    case IDLE:
                        Toast.makeText(getApplicationContext(),"未指定歌曲，从本地音乐第一首开始放起",Toast.LENGTH_LONG).show();
                        ivBottomPlay.setImageResource(R.drawable.bottomplayerplay);
                        //hr:event播放控制
                        playEvent = new PlayEvent();
                        playEvent.setAction(PlayEvent.Action.PLAY);
                        playEvent.setQueue(queue);
                        EventBus.getDefault().post(playEvent);
                        currState =PAUSE;
                        break;
                    case PAUSE:
                        ivBottomPlay.setImageResource(R.drawable.bottomplayerpause);
                        //hr:event播放控制
                        playEvent.setAction(PlayEvent.Action.STOP);
                        EventBus.getDefault().post(playEvent);
                        currState = START;
                        break;
                    case START:
                        ivBottomPlay.setImageResource(R.drawable.bottomplayerplay);
                        //hr:event播放控制
                        playEvent.setAction(PlayEvent.Action.RESUME);
                        EventBus.getDefault().post(playEvent);
                        currState = PAUSE;
                        break;
                }
            }
        });
        /* 底栏歌曲列表按钮*/
        ivBottomPlayerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomMusicList();
            }
        });
    }

    //Viewpager与Fragment绑定，设置
    private void inflateViewPager() {
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPageSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //
    private void setPageSelection(int position) {
        viewPager.setCurrentItem(position);
        switch (position) {
            case PlayFragmentPosition:
                ivMenuPlay.setImageResource(R.drawable.ic_menu_play);
                ivMenuFind.setImageResource(R.drawable.ic_menu_find_nofocus);
                ivMenuVideo.setImageResource(R.drawable.ic_menu_video_nofocus);
                break;
            case FindFragmentPosition:
                ivMenuPlay.setImageResource(R.drawable.ic_menu_play_nofocus);
                ivMenuFind.setImageResource(R.drawable.ic_menu_find);
                ivMenuVideo.setImageResource(R.drawable.ic_menu_video_nofocus);
                break;
            case VideoFragmentPosition:
                ivMenuPlay.setImageResource(R.drawable.ic_menu_play_nofocus);
                ivMenuFind.setImageResource(R.drawable.ic_menu_find_nofocus);
                ivMenuVideo.setImageResource(R.drawable.ic_menu_video);
                break;
                default:
                    break;
        }

    }

    //底部音乐列表初始化数据
    private List<Song> initData() {
        List<Song> slist = new ArrayList<>();
//        Item_Song s;
//        for (int i = 0; i < 10; i++) {
//            s = new Item_Song();
//            s.setNum(String.valueOf(i + 1));
//            s.setTitle("醉赤壁");
//            s.setSinger("林俊杰");
//            slist.add(s);
//        }
        slist=MusicUtils.getLocalMusicData(this);
        return slist;
    }

    /* 底部歌曲列表显示*/
    protected void showBottomMusicList() {
        list = initData();
        Context context = MainActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.bottom_music_list, null);
        ListView bottomListView = (ListView) layout.findViewById(R.id.lv_bottomMusicListview);
        BottomMusicListAdapter adapter = new BottomMusicListAdapter(context, list);
        bottomListView.setAdapter(adapter);

        //点击列表中某一歌曲--播放歌曲
        bottomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long id) {

                //在这里面就是执行点击后要进行的操作
                //返回listview的下标
                int currpisition = positon;
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
        iv_bottomPlayerMode = (ImageView)layout.findViewById(R.id.iv_bottomPlayerMode);
        iv_bottomPlayerMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playMode == 0){
                    iv_bottomPlayerMode.setImageResource(R.drawable.bottom_music_list_play_mode_shuffle);
                    //MusicPlayerManager.getPlayer().setPlayMode();
                    playMode = 1;
                }else if(playMode == 1){
                    iv_bottomPlayerMode.setImageResource(R.drawable.bottom_music_list_play_mode_loop);
                    playMode = 2;
                }else if(playMode == 2){
                    iv_bottomPlayerMode.setImageResource(R.drawable.bottom_music_list_play_mode_list);
                    playMode = 0;
                }
            }
        });

        /* 清空列表*/
        iv_bottomPlayerDeleteAll = (ImageView)layout.findViewById(R.id.iv_bottomPlayerDeleteAll);
        iv_bottomPlayerDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        /*  关闭歌曲列表 */
        Button btnBottomMusicListClose = (Button)layout.findViewById(R.id.btn_bottomMusicListClose);
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
            Songs songs = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.item_bottom_music_list_item, null);

                songs = new Songs();
                songs.num = (TextView) convertView.findViewById(R.id.tv_bottomMusicListItemNumber);
                songs.musicname = (TextView) convertView.findViewById(R.id.tv_bottomMusicListItemMusicName);
                songs.singer = (TextView) convertView.findViewById(R.id.tv_bottomMusicListItemMusicSinger);
                songs.delete = (ImageView) convertView.findViewById(R.id.iv_bottomMusicListItemDelete);

                convertView.setTag(songs);
            } else {
                songs = (Songs) convertView.getTag();
            }

            songs.num.setText(String.valueOf(position+1));
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

    private void requestReadPhonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //在这里面处理需要权限的代码
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_READ_PHONE_STATE);

        }
    }

    /* 初始化个人设置弹窗 */
    protected void initPopupWindow(){
        View popupWindowView = getLayoutInflater().inflate(R.layout.pop_personal_setting, null);
        //内容，高度，宽度
        popPersonalSetting = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        //动画效果
        popPersonalSetting.setAnimationStyle(R.style.AnimationLeftFade);

        //点击其他地方消失
        popupWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if( popPersonalSetting != null && popPersonalSetting.isShowing()){
                    popPersonalSetting.dismiss();
                    popPersonalSetting = null;
                }
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });

        RelativeLayout re_personalsetting_letter = (RelativeLayout)popupWindowView.findViewById(R.id.re_personalsetting_letter);
        RelativeLayout re_personalsetting_help = (RelativeLayout)popupWindowView.findViewById(R.id.re_personalsetting_help);
        RelativeLayout re_personalsetting_about = (RelativeLayout)popupWindowView.findViewById(R.id.re_personalsetting_about);
        RelativeLayout re_personalsetting_setting = (RelativeLayout)popupWindowView.findViewById(R.id.re_personalsetting_setting);
        RelativeLayout re_personalsetting_exit = (RelativeLayout)popupWindowView.findViewById(R.id.re_personalsetting_exit);

        /*我的消息*/
        re_personalsetting_letter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*帮助*/
        re_personalsetting_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*关于耳窝*/
        re_personalsetting_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*设置*/
        re_personalsetting_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*退出*/
        re_personalsetting_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /*** 获取个人设置弹窗实例*/
    private void getPopupWindow() {

        if (null != popPersonalSetting) {
            popPersonalSetting.dismiss();
            return;
        } else {
            initPopupWindow();
        }
    }

    //hr:接收MessageEvent 解决获取不到MusicPlayermanager queue
    @Subscribe
    public void onEvent(MessageEvent mMessageEvent) {
        int i=MusicPlayerManager.getPlayer().getCurrentMusicIndex();
        Log.d("56",String.valueOf(i));
        Song song= MusicPlayerManager.getPlayer().getQueue().get(i);
        tv_bottomPlayerMusicName.setText(song.getTitle());
        boolean j=MusicPlayerManager.getPlayer().getMediaPlayer().isPlaying();
        if (MusicPlayerManager.getPlayer().getMediaPlayer().isPlaying()) {
            ivBottomPlay.setImageResource(R.drawable.bottomplayerplay);
            currState=PAUSE;
        } else {
            ivBottomPlay.setImageResource(R.drawable.bottomplayerpause);
            currState=START;
        }
    }

    //hr:想要在返回这个界面时获取新的歌曲信息
    @Override
    public void onResume() {
        super.onResume();
        if(  (MusicPlayerManager.getPlayer().getQueue())!=null&&!(MusicPlayerManager.getPlayer().getQueue()).isEmpty()  ){
            int i=MusicPlayerManager.getPlayer().getCurrentMusicIndex();
            Song song= MusicPlayerManager.getPlayer().getQueue().get(i);
            tv_bottomPlayerMusicName.setText(song.getTitle());
        }

        if (MusicPlayerManager.getPlayer().getMediaPlayer().isPlaying()) {
            ivBottomPlay.setImageResource(R.drawable.bottomplayerplay);
            currState=PAUSE;
        } else {
            ivBottomPlay.setImageResource(R.drawable.bottomplayerpause);
            //处理未播放歌曲时第一次点击底部播放按钮
            if(currState==IDLE){
            }else{
                currState=START;
            }
        }

    }



}