package com.earnest.utils;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.earnest.model.entities.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hr on 2018/6/5.
 * 音乐工具类
 */


public class MusicUtils {

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<Song> getLocalMusicData(final Context context) {
        final List<Song> list = new ArrayList<Song>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 媒体库查询语句（写一个工具类MusicUtils）
                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                        null, MediaStore.Audio.AudioColumns.IS_MUSIC);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        Song song = new Song();
                        song.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                        song.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                        song.setFileUrl(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                        song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                        song.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                        if (song.getSize() > 1000 * 800) {
                            // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                            if (song.getTitle().contains("-")) {
                                String[] str = song.getTitle().split("-");
                                song.setSinger(str[0]);
                                song.setTitle(str[1]);
                            }
                            list.add(song);
                        }
                    }
                    // 释放资源
                    cursor.close();

                }
            }
        }).start();

        return list;
    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;

        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }

    }
}