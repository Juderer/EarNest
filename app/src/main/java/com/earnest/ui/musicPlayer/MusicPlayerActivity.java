package com.earnest.ui.musicPlayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.earnest.R;

public class MusicPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        getSupportActionBar().hide();//隐藏标题栏
    }
}
