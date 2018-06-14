package com.earnest.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.earnest.R;
import com.earnest.model.entities.Item_Song;

import com.earnest.services.PlayerService;
import com.earnest.ui.musicPlayer.MusicPlayerActivity;
import com.earnest.ui.myMusic.LocalMusicActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int REQUEST_READ_PHONE_STATE  = 0;
    Button btn;

    //ToorBar,ViewPager,Fragment
    ViewPager viewPager;

    //底部音乐栏部分
    ImageView ivBottomPlay;  //底栏播放暂停按钮
    boolean isChanged = false; //暂停状态
    int playMode = 0; //顺序播放

    /* 底部音乐列表*/
    private View home_bottomMusicPlayer;
    private ImageView ivBottomPlayerList;
    public List<Item_Song> list = new ArrayList<Item_Song>();
    private AlertDialog.Builder bottomListBuilder;
    private AlertDialog bottomAlertDialog;
    ImageView iv_bottomPlayerMode;


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


        // 进入MainActivity后，需要结束StartActivity
        // 进入MainActivity的唯一路径就是从StartActivity中进入
        //StartActivity.instance.finish();

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocalMusicActivity.class));
            }
        });

        initUIControls();
    }

    /////初始化UI

    private void initUIControls() {
        //ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //底部播放栏部分
        home_bottomMusicPlayer = (View)findViewById(R.id.home_bottomMusicPlayer);
        ivBottomPlay = (ImageView) findViewById(R.id.iv_bottomPlayerPlay);
        ivBottomPlayerList = (ImageView) findViewById(R.id.iv_bottomPlayerList);

        //设置监听事件
        setUIControlsOnClick();
    }

    private void setUIControlsOnClick() {

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
                if (isChanged) {
                    //点击播放
                    ivBottomPlay.setImageResource(R.drawable.bottomplayerpause);
                } else {
                    //点击暂停
                    ivBottomPlay.setImageResource(R.drawable.bottomplayerplay);
                }
                isChanged = !isChanged;
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

    //底部音乐列表初始化数据
    private ArrayList<Item_Song> initData() {
        ArrayList<Item_Song> slist = new ArrayList<Item_Song>();
        Item_Song s;
        for (int i = 0; i < 10; i++) {
            s = new Item_Song();
            s.setNum(String.valueOf(i + 1));
            s.setTitle("醉赤壁");
            s.setSinger("林俊杰");
            slist.add(s);
        }
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
        private List<Item_Song> mlist = new ArrayList<Item_Song>();
        private Context mContext;

        public BottomMusicListAdapter(Context context, List<Item_Song> list) {
            this.mContext = context;
            this.mlist = list;
        }

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Item_Song getItem(int position) {
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

            songs.num.setText(list.get(position).num);
            songs.musicname.setText(list.get(position).title);
            songs.singer.setText(list.get(position).singer);

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


}