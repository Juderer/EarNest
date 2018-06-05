package com.earnest.ui.myMusic;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
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
                new int[]{R.id.tv_localMusicListItemMusicName, R.id.tv_localMusicListItemMusicSinger});

        lvLocalMusicList.setAdapter(simleAdapter);

        /* 本地音乐返回 */
        imgbtnLocalMusicBack = (ImageButton) findViewById(R.id.imgbtn_localMusicBack);
        imgbtnLocalMusicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* 其他操作 */
        imgbtnLocalMusicListItemAction = (ImageButton)findViewById(R.id.imgbtn_localMusicListItemAction);
        /*ibLocalMusicListItemAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreAction();
            }
        });*/
    }

    protected void showMoreAction(){

        Context context = LocalMusicActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.local_music_more_action, null);

        localMusicBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.BottomAlertDialog));
        localMusicBuilder.setView(layout);
        localMusicAlertDialog = localMusicBuilder.create();
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
