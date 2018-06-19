package com.earnest.ui.musicPlayer;

import android.os.AsyncTask;
import android.widget.ListView;

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

/**
 * Created by Administrator on 2018/6/19.
 */

public class OnlineMusic {
    private static OnlineMusic instance;

    public static  OnlineMusic getInstance() {
        if(instance == null) {
            instance = new OnlineMusic();
        }
        return  instance;
    }

    public List<Map<String, Object>> getNetMusicList() {
        return netMusicList;
    }

    public Map<String, SoftReference<String>> getPicUrlMap() {
        return picUrlMap;
    }



    //音乐搜索接口
    public static final String CLOUD_MUSIC_API_PREFIX = "http://s.music.163.com/search/get/?";
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
                    //获取歌曲名字
                    String title = song.getString("name");
                    //获取歌词演唱者
                    String artist = song.getJSONArray("artists")
                            .getJSONObject(0).getString("name");
                    //获取歌曲专辑图片的url
                    String albumPicUrl = song.getJSONObject("album").getString(
                            "picUrl");
                    //获取歌曲音频的url
                    String audioUrl = song.getString("audio");

                    //保存音乐信息的Map
                    Map<String, Object> item = new HashMap<>();
                    item.put("title", title);
                    item.put("artist", artist);
                    item.put("picUrl", albumPicUrl);
                    picUrlMap.put(title + artist, new SoftReference<String>(
                            albumPicUrl));
                    item.put("audio", audioUrl);
                    //将一条歌曲信息存入list中
                    netMusicList.add(item);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
