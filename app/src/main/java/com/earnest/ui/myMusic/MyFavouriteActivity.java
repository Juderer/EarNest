package com.earnest.ui.myMusic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.earnest.R;
import com.earnest.ui.home.MainActivity;
import com.earnest.ui.musicPlayer.RemarkActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFavouriteActivity extends AppCompatActivity {

    private ImageView iv_myFavourite_back;
    private ImageView iv_myFavourite_share;
    private ImageView iv_myFavourite_musicListImg;
    private ImageView iv_myFavourite_playAll;
    private TextView tv_myFavourite_playAll;
    private ListView lv_myFavourite_musicList;
    private ImageButton imgbtn_item_my_favourite_action;

    private int play_status = 0;  //暂停

    //弹窗
    private AlertDialog.Builder myMusicBuilder;
    private AlertDialog myMusicAlertDialog;
    private RelativeLayout myMusicMoreActionPlayNext;
    private RelativeLayout myMusicMoreActionCollect;
    private RelativeLayout myMusicMoreActionRemark;
    private RelativeLayout myMusicMoreActionShare;
    private RelativeLayout myMusicMoreActionDelete;

    //测试
    private String[] num = {"1","2","3"};
    private  String[] musicName={"林俊杰","林俊杰","林俊杰"};
    private String[] musicSinger = {"林俊杰","林俊杰","林俊杰"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourite);

        initUIControls();

         /* 歌曲列表适配 */
        List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        for(int i=0;i<num.length;i++){
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("tvMyFavouriteNum", num[i]);
            listItem.put("tvMyFavouriteMusicName",musicName[i]);
            listItem.put("tvMyFavouriteMusicSinger",musicSinger[i]);
            listItems.add(listItem);
        }
        SimpleAdapter simleAdapter = new SimpleAdapter(this, listItems, R.layout.item_my_favourite_list ,
                new String[]{"tvMyFavouriteNum","tvMyFavouriteMusicName","tvMyFavouriteMusicSinger"},
                new int[]{R.id.tv_item_my_favourite_num, R.id.tv_item_my_favourite_musicName, R.id.tv_item_my_favourite_musicSinger}){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final int p = position;
                final View view = super.getView(position, convertView, parent);

               /* 更多操作 */
                imgbtn_item_my_favourite_action = (ImageButton) view.findViewById(R.id.imgbtn_item_my_favourite_action);
                imgbtn_item_my_favourite_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showMoreAction();
                    }
                });
                return view;
            }
        };

        lv_myFavourite_musicList.setAdapter(simleAdapter);
    }

    private void initUIControls(){
        iv_myFavourite_back = (ImageView) findViewById(R.id.iv_myFavourite_back);
        iv_myFavourite_share = (ImageView) findViewById(R.id.iv_myFavourite_share);
        iv_myFavourite_musicListImg = (ImageView) findViewById(R.id.iv_myFavourite_musicListImg);
        iv_myFavourite_playAll = (ImageView) findViewById(R.id.iv_myFavourite_playAll);
        tv_myFavourite_playAll = (TextView) findViewById(R.id.tv_myFavourite_playAll);
        lv_myFavourite_musicList = (ListView) findViewById(R.id.lv_myFavourite_musicList);

        /*设置专辑图片的背景图片透明度*/
        iv_myFavourite_musicListImg.getBackground().setAlpha(35);

        //设置监听事件
        setUIControlsOnClick();
    }

    private void setUIControlsOnClick(){

        /*返回*/
        iv_myFavourite_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*分享*/
        iv_myFavourite_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*全部播放*/
        tv_myFavourite_playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(play_status == 0){
                    tv_myFavourite_playAll.setText("暂停播放");
                    iv_myFavourite_playAll.setImageResource(R.drawable.ic_my_music_play_all_play);
                    play_status = 1;
                }else if(play_status == 1){
                    tv_myFavourite_playAll.setText("全部播放");
                    iv_myFavourite_playAll.setImageResource(R.drawable.ic_my_music_play_all_pause);
                    play_status = 0;
                }
            }
        });
    }

    /* 更多操作弹窗 */
    protected void showMoreAction(){

        Context context = MyFavouriteActivity.this;
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
