package com.earnest.ui.myMusic;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.earnest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalMusicActivity extends AppCompatActivity {

    private ListView lvLocalMusicList;
    private ImageButton imgbtnLocalMusicListItemAction;
    private AlertDialog.Builder localMusicBuilder;
    private AlertDialog localMusicAlertDialog;
    private ImageButton imgbtnLocalMusicBack;
    private RelativeLayout localMusicMoreActionPlayNext;
    private RelativeLayout localMusicMoreActionCollect;
    private RelativeLayout localMusicMoreActionRemark;
    private RelativeLayout localMusicMoreActionShare;
    private RelativeLayout localMusicMoreActionDelete;

    //测试
    private String[] localMusicNames={"醉赤壁","醉赤壁","醉赤壁"};
    private  String[] localMusicSingers={"林俊杰","林俊杰","林俊杰"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_local_music);

        lvLocalMusicList = (ListView) findViewById(R.id.lv_localMusicList);

        List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        for(int i=0;i<localMusicNames.length;i++){
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("tvLocalMusicName", localMusicNames[i]);
            listItem.put("tvLocalMusicSinger",localMusicSingers[i]);
            listItems.add(listItem);
        }
        SimpleAdapter simleAdapter = new SimpleAdapter(this, listItems, R.layout.item_local_music_list ,
                new String[]{"tvLocalMusicName","tvLocalMusicSinger"},
                new int[]{R.id.tv_localMusicListItemMusicName, R.id.tv_localMusicListItemMusicSinger}){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final int p = position;
                final View view = super.getView(position, convertView, parent);

               /* 点击显示其他操作 */
                imgbtnLocalMusicListItemAction = (ImageButton)view.findViewById(R.id.imgbtn_localMusicListItemAction);
                imgbtnLocalMusicListItemAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showMoreAction();
                    }
                });
                return view;
            }
        };

        lvLocalMusicList.setAdapter(simleAdapter);

        /* 本地音乐返回 */
        imgbtnLocalMusicBack = (ImageButton) findViewById(R.id.imgbtn_localMusicBack);
        imgbtnLocalMusicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    protected void showMoreAction(){

        Context context = LocalMusicActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.local_music_more_action, null);

        localMusicBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.BottomAlertDialog));
        localMusicBuilder.setView(layout);
        localMusicAlertDialog = localMusicBuilder.create();


        /* 下一首播放*/
        localMusicMoreActionPlayNext = (RelativeLayout)layout.findViewById(R.id.localMusicMoreActionPlayNext);
        localMusicMoreActionPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /* 收藏 */
        localMusicMoreActionCollect = (RelativeLayout)layout.findViewById(R.id.localMusicMoreActionCollect);
        localMusicMoreActionCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*评论*/
        localMusicMoreActionRemark = (RelativeLayout)layout.findViewById(R.id.localMusicMoreActionRemark);
        localMusicMoreActionRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*分享*/
        localMusicMoreActionShare = (RelativeLayout)layout.findViewById(R.id.localMusicMoreActionShare);
        localMusicMoreActionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*删除*/
        localMusicMoreActionDelete = (RelativeLayout)layout.findViewById(R.id.localMusicMoreActionDelete);
        localMusicMoreActionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /* 更多操作弹框显示 */
        localMusicAlertDialog.show();

        //设置大小、位置
        WindowManager.LayoutParams layoutParams = localMusicAlertDialog.getWindow().getAttributes();
        localMusicAlertDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        localMusicAlertDialog.getWindow().setAttributes(layoutParams);
        localMusicAlertDialog.getWindow().setGravity(Gravity.BOTTOM);
        localMusicAlertDialog.getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
    }
}
