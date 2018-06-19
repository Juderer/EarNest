package com.earnest.ui.search;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.earnest.R;
import com.earnest.event.PlayEvent;
import com.earnest.model.entities.Item_Song;
import com.earnest.ui.home.MainActivity;
import com.earnest.ui.musicPlayer.MusicPlayerActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultActivity extends AppCompatActivity {
    //hr:
    PlayEvent playEvent=new PlayEvent();

    private ListView lv_searchResult;
    private ImageView iv_searchResult_search;
    private EditText et_searchResult_input;
    private TextView tv_searchResult_cancel;

    /*更多操作*/
    private ImageView imgbtn_searchResultItemAction;
    private AlertDialog.Builder searchResultMusicBuilder;
    private AlertDialog searchResultMusicAlertDialog;
    private RelativeLayout searchResultMusicMoreActionPlayNext;
    private RelativeLayout searchResultMusicMoreActionCollect;
    private RelativeLayout searchResultMusicMoreActionRemark;
    private RelativeLayout searchResultMusicMoreActionShare;
    private RelativeLayout searchResultMusicMoreActionDownload;

    //搜索结果 - - 测试
    private String[] searchResultMusicNames={"醉赤壁","醉赤壁","醉赤壁"};
    private  String[] searchResultMusicSingers={"林俊杰","林俊杰","林俊杰"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        initUIControls();

        /* 歌曲列表适配 */
        List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        for(int i=0;i<searchResultMusicNames.length;i++){
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("title", searchResultMusicNames[i]);
            listItem.put("artist",searchResultMusicSingers[i]);
            listItems.add(listItem);
        }

        notifyDataSetChanged(listItems);
    }

    private void notifyDataSetChanged(List<Map<String,Object>> listItems) {
        SimpleAdapter simleAdapter = new SimpleAdapter(this, listItems, R.layout.item_search_result ,
                new String[]{"title","artist"},
                new int[]{R.id.tv_searchResultItemMusicName, R.id.tv_searchResultItemMusicSinger}){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final int p = position;
                final View view = super.getView(position, convertView, parent);

               /* 点击显示其他操作 */
                imgbtn_searchResultItemAction = (ImageButton)view.findViewById(R.id.imgbtn_searchResultItemAction);
                imgbtn_searchResultItemAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showMoreAction();
                    }
                });
                LinearLayout llItem = (LinearLayout) view.findViewById(R.id.llItem);
                llItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        List<Song> queue= MusicUtils.getLocalMusicData(getApplicationContext());
//                        PlayEvent playEvent = new PlayEvent();
//                        playEvent.setAction(PlayEvent.Action.PLAY);
//                        playEvent.setQueue(queue);
//                        EventBus.getDefault().post(playEvent);

                        netMusicList.get(p);
                        picUrlMap.get(p);
                        Uri uri1 = Uri.parse(netMusicList.get(p).get("audio").toString());
                        playEvent.setAction(PlayEvent.Action.PLAY);
                        playEvent.setTestNet(PlayEvent.TestNet.NET);
                        playEvent.setNetSongUri(uri1);
                        EventBus.getDefault().post(playEvent);
                        //tv_bottomPlayerMusicName.setText("正在缓冲...");

//                        MediaPlayer mediaPlayer = new MediaPlayer();
//                        try {
//                            mediaPlayer.setDataSource(getApplicationContext(), uri1);
//                            mediaPlayer.prepareAsync();
//                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//                                @Override
//                                public void onPrepared(MediaPlayer mp) {
//                                    // TODO Auto-generated method stub
//                                    Log.e("MusicReceiver", "a");
//                                    mp.start();
//                                }
//                            });
//                        } catch (Exception e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                            Toast.makeText(getApplicationContext(), "222", Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
                return view;
            }
        };

        lv_searchResult.setAdapter(simleAdapter);
    }

    private void initUIControls(){
        lv_searchResult = (ListView)findViewById(R.id.lv_searchResult);
        iv_searchResult_search = (ImageView)findViewById(R.id.iv_searchResult_search);
        et_searchResult_input = (EditText) findViewById(R.id.et_searchResult_input);
        tv_searchResult_cancel = (TextView)findViewById(R.id.tv_searchResult_cancel);

        //设置监听事件
        setUIControlsOnClick();
    }

    private void setUIControlsOnClick() {

        /*点击搜索图标*/
        iv_searchResult_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(SearchResultActivity.this,SearchResultActivity.class));
                searchOnlineMusic(et_searchResult_input.getText().toString());
                //searchOnlineMusic("学猫叫");
            }
        });

        /*点击取消*/
        tv_searchResult_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchResultActivity.this,MainActivity.class));
            }
        });
    }

    protected void showMoreAction(){

        Context context = SearchResultActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_search_result_more_action, null);

        searchResultMusicBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.BottomAlertDialog));
        searchResultMusicBuilder.setView(layout);
        searchResultMusicAlertDialog = searchResultMusicBuilder.create();


        /* 下一首播放*/
        searchResultMusicMoreActionPlayNext = (RelativeLayout)layout.findViewById(R.id.searchResultMusicMoreActionPlayNext);
        searchResultMusicMoreActionPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*收藏 */
        searchResultMusicMoreActionCollect = (RelativeLayout)layout.findViewById(R.id.searchResultMusicMoreActionCollect);
        searchResultMusicMoreActionCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*评论*/
        searchResultMusicMoreActionRemark = (RelativeLayout)layout.findViewById(R.id.searchResultMusicMoreActionRemark);
        searchResultMusicMoreActionRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*分享*/
        searchResultMusicMoreActionShare = (RelativeLayout)layout.findViewById(R.id.searchResultMusicMoreActionShare);
        searchResultMusicMoreActionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*下载*/
        searchResultMusicMoreActionDownload = (RelativeLayout)layout.findViewById(R.id.searchResultMusicMoreActionDownload);
        searchResultMusicMoreActionDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*更多操作弹框显示 */
        searchResultMusicAlertDialog.show();

        //设置大小、位置
        WindowManager.LayoutParams layoutParams = searchResultMusicAlertDialog.getWindow().getAttributes();
        searchResultMusicAlertDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        searchResultMusicAlertDialog.getWindow().setAttributes(layoutParams);
        searchResultMusicAlertDialog.getWindow().setGravity(Gravity.BOTTOM);
        searchResultMusicAlertDialog.getWindow().setWindowAnimations(R.style.BottomDialogAnimation);
    }

    ///////////////////////////////////////////////////////////////
    //音乐搜索接口
    public static final String CLOUD_MUSIC_API_PREFIX = "http://s.music.163.com/search/get/?";
    //真实地址前缀
    public static final String CLOUD_MUSIC_REAL_ADDRESS="http://music.163.com/song/media/outer/url?id=";
    //根据关键字搜索到的音乐列表
    //private ListView netMusicListView;
    //存放搜索到的音乐信息的list
    private List<Map<String, Object>> netMusicList = new ArrayList<>();
    //获取的json数据格式的网络响应赋值给searchResponse
    private String searchResponse = null;
    //private LayoutInflater inflater;
    //缓存专辑图片的下载链接，当下载完成并播放歌曲时，点击播放图标加载专辑图标时避免重新联网搜索，直接从缓存中获取
    private Map<String, SoftReference<String>> picUrlMap = new HashMap<String, SoftReference<String>>();


    public void searchOnlineMusic(String content) {
        String musicUrl = getRealUrl(content);
        new SearchMusicTask().execute(musicUrl);
    }

    //返回可以访问的网络资源
    private String getRealUrl(String query) {
        String key = null;
        try {
            key = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return CLOUD_MUSIC_API_PREFIX + "type=1&s='" + key
                + "'&limit=20&offset=0";
    }

    // 负责搜索音乐的异步任务，搜索完成后显示网络音乐列表
    private class SearchMusicTask extends AsyncTask<String, Void, Void> {

        public SearchMusicTask() {

        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(String... params) {
            String url = params[0];
            try {
                HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
                conn.setConnectTimeout(5000);
                //使用缓存提高处理效率
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                //网络响应赋值给成员变量searchResponse
                searchResponse = sb.toString();
                parseResponse();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //adapter数据更新后通知列表更新
            //netMusicListAdapter.notifyDataSetChanged();
            //musicList.setAdapter(netMusicListAdapter);
            notifyDataSetChanged(netMusicList);
        }

        //json解析网络响应
        private void parseResponse() {
            try {
                JSONObject response = new JSONObject(searchResponse);
                JSONObject result = response.getJSONObject("result");
                JSONArray songs = result.getJSONArray("songs");
                if (netMusicList.size() > 0) netMusicList.clear();
                for (int i = 0; i < songs.length(); i++) {
                    JSONObject song = songs.getJSONObject(i);
                    //获取歌曲id
                    String id = song.getString("id");
                    //获取歌曲名字
                    String title = song.getString("name");
                    //获取歌词演唱者
                    String artist = song.getJSONArray("artists")
                            .getJSONObject(0).getString("name");
                    //获取歌曲专辑图片的url
                    String albumPicUrl = song.getJSONObject("album").getString(
                            "picUrl");
                    //获取歌曲音频的url
                    String audioUrl = CLOUD_MUSIC_REAL_ADDRESS + id + ".mp3";

                    //保存音乐信息的Map
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", id);
                    item.put("title", title);
                    item.put("artist", artist);
                    item.put("picUrl", albumPicUrl);
                    picUrlMap.put(title + artist, new SoftReference<String>(
                            albumPicUrl));
                    item.put("audio", audioUrl);
                    //将一条歌曲信息存入list中
                    netMusicList.add(item);
                    Log.d("id:", "id:" + id);
                    Log.d("title:", "id:" + title);
                    Log.d("artist:", "id:" + artist);
                    Log.d("test:", "albumPicUrl:" + albumPicUrl);
                    Log.d("test:", "audio:" + audioUrl);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
