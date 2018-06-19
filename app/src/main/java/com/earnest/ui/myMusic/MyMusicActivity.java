package com.earnest.ui.myMusic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.earnest.R;
import com.earnest.event.MessageEvent;
import com.earnest.event.PlayEvent;
import com.earnest.manager.MusicPlayerManager;
import com.earnest.model.entities.Item_Song;
import com.earnest.model.entities.Song;
import com.earnest.services.PlayerService;
import com.earnest.ui.musicPlayer.MusicPlayerActivity;
import com.earnest.utils.MusicUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyMusicActivity extends AppCompatActivity {

    //hr:变量声明
    private List<Song> myMusicList;
    //hr:定义三种播放状态
    //hr:定义三种播放状态
    private static final int IDLE = 0;
    private static final int PAUSE = 1;
    private static final int START = 2;
    private int currState = IDLE;

    PlayEvent playEvent;
    private ListView lvMyMusicList;
    private ImageButton imgbtnMyMusicListItemAction;
    private AlertDialog.Builder myMusicBuilder;
    private AlertDialog myMusicAlertDialog;
    private ImageButton imgbtnMyMusicBack;
    private RelativeLayout myMusicMoreActionPlayNext;
    private RelativeLayout myMusicMoreActionCollect;
    private RelativeLayout myMusicMoreActionRemark;
    private RelativeLayout myMusicMoreActionShare;
    private RelativeLayout myMusicMoreActionDelete;
    private ImageView iv_myMusicPlayAll;
    private TextView tv_myMusicPlayAll;
    private boolean isPlayAll = false;  //播放全部暂停状态

    /* 复用activity */
    TextView tv_myMusicHeadLabel;
    Button btn_RecentMusic_deleteAll;

    ///底部音乐栏部分
    private ImageView ivBottomPlay;  //底栏播放暂停按钮
    private boolean isChanged = false; //暂停状态
    private int playMode = 0; //顺序播放

    /* 底部音乐列表*/
    private View my_music_bottomMusicPlayer;
    private ImageView ivBottomPlayerList;
    private List<Song> list = new ArrayList<>();
    private AlertDialog.Builder bottomListBuilder;
    private AlertDialog bottomAlertDialog;
    private ImageView iv_bottomPlayerMode;
    private ImageView iv_bottomPlayerDeleteAll;

    Handler mHandler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean b = supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_music);

        /*初始化控件*/
        initUIControls();

        /*复用*/
        String myMusiclabel = getIntent().getStringExtra("label");
        int recentDelete = getIntent().getIntExtra("delete",0);
        tv_myMusicHeadLabel.setText(myMusiclabel);
        if(recentDelete == 1){
            btn_RecentMusic_deleteAll.setVisibility(View.VISIBLE);
        }

        myMusicList=new ArrayList<>();
        if (myMusiclabel.equals("本地音乐")){
            /* 歌曲列表适配 */
            myMusicList= MusicUtils.getLocalMusicData(this);
        }else if (myMusiclabel.equals("我的收藏")) {
            myMusicList = MusicPlayerActivity.favoriateSongList;
        }else if (myMusiclabel.equals("最近播放")) {
            myMusicList = PlayerService.recentPlayedSongList;
        }

        final List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        for(int i=0;i<myMusicList.size();i++){
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("tvMyMusicName", myMusicList.get(i).getTitle());
            listItem.put("tvMyMusicSinger",myMusicList.get(i).getSinger());
            listItems.add(listItem);
            //mHandler.sendEmptyMessage(10);
        }

        final SimpleAdapter simleAdapter = new SimpleAdapter(this, listItems, R.layout.item_my_music_list ,
                new String[]{"tvMyMusicName","tvMyMusicSinger"},
                new int[]{R.id.tv_myMusicListItemMusicName, R.id.tv_myMusicListItemMusicSinger}){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final int p = position;
                final View view = super.getView(position, convertView, parent);


                /* 点击显示其他操作 */
                imgbtnMyMusicListItemAction = (ImageButton)view.findViewById(R.id.imgbtn_myMusicListItemAction);
                imgbtnMyMusicListItemAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showMoreAction();
                    }
                });
                return view;
            }
        };

         lvMyMusicList.setAdapter(simleAdapter);
         //simleAdapter.notifyDataSetChanged();
         //
         lvMyMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int positon, long l) {
                 //在这里面就是执行点击后要进行的操作
                 //返回listview的下标
                 int currpisition = positon;
                 //把消息psot出去
                 playEvent = new PlayEvent();
                 playEvent.setAction(PlayEvent.Action.PLAY);
                 playEvent.setQueue(myMusicList);
                 playEvent.setMusicIndex(currpisition);
                 EventBus.getDefault().post(playEvent);
             }
         });


        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if(msg.what==10){
                    simleAdapter.notifyDataSetChanged();
                }
            }

        };


        /* 返回 */
        imgbtnMyMusicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* 最近播放清空列表按钮*/
        btn_RecentMusic_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerService.recentPlayedSongList.clear();
                myMusicList.clear();
                finish();
            }
        });

        /* 播放全部 */
        tv_myMusicPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isPlayAll == false){     //点击播放
                    iv_myMusicPlayAll.setImageResource(R.drawable.ic_my_music_play_all_play);
                    tv_myMusicPlayAll.setText("暂停播放");
                    isPlayAll = true;
                }else if(isPlayAll == true){   //点击暂停
                    iv_myMusicPlayAll.setImageResource(R.drawable.ic_my_music_play_all_pause);
                    tv_myMusicPlayAll.setText("播放全部");
                    isPlayAll = false;
                }

            }
        });
    }


    private void initUIControls(){
        tv_myMusicHeadLabel = (TextView)findViewById(R.id.tv_myMusicHeadLabel);
        lvMyMusicList = (ListView) findViewById(R.id.lv_myMusicList);
        imgbtnMyMusicBack = (ImageButton) findViewById(R.id.imgbtn_myMusicBack);
        btn_RecentMusic_deleteAll = (Button)findViewById(R.id.btn_RecentMusic_deleteAll);
        iv_myMusicPlayAll = (ImageView)findViewById(R.id.iv_myMusicPlayAll);
        tv_myMusicPlayAll = (TextView)findViewById(R.id.tv_myMusicPlayAll);

        //底部播放栏部分
       // my_music_bottomMusicPlayer = (View)findViewById(R.id.my_music_bottomMusicPlayer);
       // ivBottomPlay = (ImageView)my_music_bottomMusicPlayer.findViewById(R.id.iv_bottomPlayerPlay);
       // ivBottomPlayerList = (ImageView) my_music_bottomMusicPlayer.findViewById(R.id.iv_bottomPlayerList);

        //设置监听事件
        setUIControlsOnClick();
    }

    private void setUIControlsOnClick() {
        //底部播放栏部分
        /* 点击底部播放器转换至播放界面 */
//        my_music_bottomMusicPlayer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MyMusicActivity.this,MusicPlayerActivity.class));
//            }
//        });

//        /* 底栏播放按钮 */
//        ivBottomPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isChanged) {
//                    //点击播放
//                    ivBottomPlay.setImageResource(R.drawable.bottomplayerpause);
//                } else {
//                    //点击暂停
//                    ivBottomPlay.setImageResource(R.drawable.bottomplayerplay);
//                }
//                isChanged = !isChanged;
//            }
//        });
//
//        /* 底栏歌曲列表按钮*/
//        ivBottomPlayerList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showBottomMusicList();
//            }
//        });
    }

    protected void showMoreAction(){

        Context context = MyMusicActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.my_music_more_action, null);

        myMusicBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.BottomAlertDialog));
        myMusicBuilder.setView(layout);
        myMusicAlertDialog = myMusicBuilder.create();


        /* 下一首播放*/
        myMusicMoreActionPlayNext = (RelativeLayout)layout.findViewById(R.id.myMusicMoreActionPlayNext);
        myMusicMoreActionPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /* 收藏 */
        myMusicMoreActionCollect = (RelativeLayout)layout.findViewById(R.id.myMusicMoreActionCollect);
        myMusicMoreActionCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*评论*/
        myMusicMoreActionRemark = (RelativeLayout)layout.findViewById(R.id.myMusicMoreActionRemark);
        myMusicMoreActionRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*分享*/
        myMusicMoreActionShare = (RelativeLayout)layout.findViewById(R.id.myMusicMoreActionShare);
        myMusicMoreActionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*删除*/
        myMusicMoreActionDelete = (RelativeLayout)layout.findViewById(R.id.myMusicMoreActionDelete);
        myMusicMoreActionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /* 更多操作弹框显示 */
        myMusicAlertDialog.show();

        //设置大小、位置
        WindowManager.LayoutParams layoutParams = myMusicAlertDialog.getWindow().getAttributes();
        myMusicAlertDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        myMusicAlertDialog.getWindow().setAttributes(layoutParams);
        myMusicAlertDialog.getWindow().setGravity(Gravity.BOTTOM);
        myMusicAlertDialog.getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
    }

    //底部音乐列表初始化数据
    private List<Song> initData() {
        List<Song> slist = new ArrayList<>();
        slist=MusicUtils.getLocalMusicData(this);

        return slist;
    }

    /* 底部歌曲列表显示*/
    protected void showBottomMusicList() {
        //list .addAll(staticLocalMusicList);
        Log.d("90",String.valueOf(list.size()));
        Context context = MyMusicActivity.this;
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
            BottomMusicListAdapter.Songs songs = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.item_bottom_music_list_item, null);

                songs = new BottomMusicListAdapter.Songs();
                songs.num = (TextView) convertView.findViewById(R.id.tv_bottomMusicListItemNumber);
                songs.musicname = (TextView) convertView.findViewById(R.id.tv_bottomMusicListItemMusicName);
                songs.singer = (TextView) convertView.findViewById(R.id.tv_bottomMusicListItemMusicSinger);
                songs.delete = (ImageView) convertView.findViewById(R.id.iv_bottomMusicListItemDelete);

                convertView.setTag(songs);
            } else {
                songs = (BottomMusicListAdapter.Songs) convertView.getTag();
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

}
