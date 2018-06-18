package com.earnest.ui.myMusic;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.earnest.R;
import com.earnest.model.entities.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hr on 2018/6/12.
 * 本地音乐listview的adapter
 */

public class LocalMusicAdapter extends BaseAdapter{
    private Context context;
    private Activity mActivity;
    private List<Song> list=new ArrayList<>();
    private ListView mListView;
    public LocalMusicAdapter(MyMusicActivity localMusicActivity) {
        this.mActivity=localMusicActivity;
        this.context = localMusicActivity;
        //this.list= list;

    }

    //addItemSelf()做两件事：（1）在调用线程中更新数据。（2）在UI线程中notifyDataSetChanged()。
    public void addItemSelf (List<Song> list) {
        this.list=list;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("TestListView" , "run TestAdapter.notifyDataSetChanged() ..." );
                LocalMusicAdapter.this.notifyDataSetChanged();
            }
        });
    }

    public void setListView(ListView listView){
        mListView=listView;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            //引入布局
            view = View.inflate(context, R.layout.item_my_music_list, null);
            //实例化对象
            holder.song = (TextView) view.findViewById(R.id.tv_myMusicListItemMusicName);
            holder.singer = (TextView) view.findViewById(R.id.tv_myMusicListItemMusicSinger);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //给控件赋值
        holder.song.setText(list.get(i).getTitle());
        holder.singer.setText(list.get(i).getSinger());
        //时间需要转换一下
//        int duration = list.get(i).duration;
//        String time = MusicUtils.formatTime(duration);
//        holder.duration.setText(time);
        return view;
    }
    class ViewHolder{
        TextView song;//歌曲名
        TextView singer;//歌手

    }

    public void addList(List list){
        this.list.addAll(list);
    }






}
