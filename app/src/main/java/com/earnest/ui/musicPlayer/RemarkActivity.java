package com.earnest.ui.musicPlayer;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.earnest.R;
import com.earnest.ui.home.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemarkActivity extends AppCompatActivity {

    private ImageView imgbtn_remarkBack;
    private RelativeLayout re_currentMusicInfo;
    private Button btn_remark_send;
    private ListView lv_remark;
    private ImageView iv_remark_like;
    private int like_status = 0; //未点赞

    //测试
    /*private int[] remarkHeadImg={R.drawable.timg,R.drawable.timg,R.drawable.timg};*/
    private  String[] remarkUserName={"林俊杰","林俊杰","林俊杰"};
    private String[] likeNum = {"100","100","100"};
    private String[] remarkContent = {"这首歌太好听了！！！！！！！","这首歌太好听了！！！！！！！","这首歌太好听了！！！！！！！"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);

        initUIControls();

         /* 歌曲列表适配 */
        List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        for(int i=0;i<remarkUserName.length;i++){
            Map<String,Object> listItem = new HashMap<String,Object>();
            //listItem.put("imgRemarkHeadImg",remarkHeadImg[i]);
            listItem.put("tvRemarkUserName", remarkUserName[i]);
            listItem.put("tvLikeNum",likeNum[i]);
            listItem.put("tvRemarkContent",remarkContent[i]);
            listItems.add(listItem);
        }
        SimpleAdapter simleAdapter = new SimpleAdapter(this, listItems, R.layout.item_remark ,
                new String[]{"tvRemarkUserName","tvLikeNum","tvRemarkContent"},
                new int[]{R.id.tv_remark_userName, R.id.tv_like_num, R.id.tv_remark_content}){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final int p = position;
                final View view = super.getView(position, convertView, parent);

               /* 点赞操作 */
                iv_remark_like = (ImageView) view.findViewById(R.id.iv_remark_like);
                iv_remark_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(like_status == 0){
                            iv_remark_like.setImageResource(R.drawable.ic_remark_like);
                            like_status = 1;
                        }else if(like_status == 1){
                            iv_remark_like.setImageResource(R.drawable.ic_remark_unlike);
                            like_status = 0;
                        }

                    }
                });
                return view;
            }
        };

        lv_remark.setAdapter(simleAdapter);
    }

    private void initUIControls(){
        lv_remark = (ListView)findViewById(R.id.lv_remark);
        imgbtn_remarkBack = (ImageView) findViewById(R.id.imgbtn_remarkBack);
        re_currentMusicInfo = (RelativeLayout) findViewById(R.id.re_currentMusicInfo);
        btn_remark_send = (Button) findViewById(R.id.btn_remark_send);

        //设置监听事件
        setUIControlsOnClick();
    }

    private void setUIControlsOnClick(){

        /*返回到主页--我的音乐*/
        imgbtn_remarkBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RemarkActivity.this, MainActivity.class));
            }
        });

        /* 回到播放页面 */
        re_currentMusicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*发表评论*/
        btn_remark_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
