package com.earnest.ui.search;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.earnest.R;
import com.earnest.model.entities.Item_Song;
import com.earnest.ui.home.MainActivity;
import com.earnest.ui.musicPlayer.MusicPlayerActivity;
import com.earnest.ui.myMusic.MyMusicActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {


    private ImageView iv_search_history_delete;
    private ListView lv_search_history;
    private ImageView iv_search_search;
    private TextView tv_search_cancel;

    //历史记录 - - 测试
    private String[] historyMusicNames={"醉赤壁","醉赤壁","醉赤壁"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initUIControls();

         /* 历史搜索歌曲列表适配 */
        List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        for(int i=0;i<historyMusicNames.length;i++){
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("tvSearchHistoryMusicName", historyMusicNames[i]);
            listItems.add(listItem);
        }
        SimpleAdapter simleAdapter = new SimpleAdapter(this, listItems, R.layout.item_search_history ,
                new String[]{"tvSearchHistoryMusicName"},
                new int[]{R.id.tv_search_history_musicName}){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final int p = position;
                final View view = super.getView(position, convertView, parent);

                //点击删除此条历史记录
                iv_search_history_delete = (ImageView) view.findViewById(R.id.iv_search_history_delete);
                iv_search_history_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                return view;
            }
        };
        lv_search_history.setAdapter(simleAdapter);


    }

    private void initUIControls(){
        lv_search_history = (ListView)findViewById(R.id.lv_search_history);
        iv_search_search = (ImageView)findViewById(R.id.iv_search_search);
        tv_search_cancel = (TextView)findViewById(R.id.tv_search_cancel);

        //设置监听事件
        setUIControlsOnClick();
    }

    private void setUIControlsOnClick() {

        /*点击搜索图标*/
        iv_search_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this,SearchResultActivity.class));
            }
        });

        /*点击取消*/
        tv_search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this,MainActivity.class));
            }
        });
    }

}
