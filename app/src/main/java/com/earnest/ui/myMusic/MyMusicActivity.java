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
        }else if (myMusiclabel.equals("我的收藏")){
            myMusicList = MusicPlayerActivity.favoriateSongList;
        }else {

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

        //设置监听事件
        setUIControlsOnClick();
    }

    private void setUIControlsOnClick() {

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

}
