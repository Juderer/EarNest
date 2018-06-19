package com.earnest.ui.home.menuFragments;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.earnest.R;
import com.earnest.ui.adapter.BaseFragment;
import com.earnest.ui.myMusic.MyFavouriteActivity;
import com.earnest.ui.myMusic.MyMusicActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends BaseFragment {

    private View view;

    //Menu items
    RelativeLayout rlLocalMusic;
    RelativeLayout rlDownloadMusic;
    RelativeLayout rlFavoriteMusic;
    RelativeLayout rlRecentMusic;
    LinearLayout llMySongListsMyFavourite;

    public PlayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_play, container, false);

        initUIControls();
        // Inflate the layout for this fragment
        return view;
    }

    private void initUIControls() {
        //Menu items
        rlLocalMusic = (RelativeLayout) view.findViewById(R.id.rlLocalMusic);
        rlDownloadMusic = (RelativeLayout) view.findViewById(R.id.rlDownloadMusic);
        rlFavoriteMusic = (RelativeLayout) view.findViewById(R.id.rlFavoriteMusic);
        rlRecentMusic = (RelativeLayout) view.findViewById(R.id.rlRecentMusic);
        llMySongListsMyFavourite = (LinearLayout)view.findViewById(R.id.ll_mySongLists_myFavourite);

        setUIControlsOnClick();
    }

    private void setUIControlsOnClick() {
        //Menu items

        /* 本地音乐 */
        rlLocalMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentlocalmusic = new Intent(getActivity(), MyMusicActivity.class);
                intentlocalmusic.putExtra("label","本地音乐");
                startActivity(intentlocalmusic);
            }
        });

        /* 下载管理 */
        rlDownloadMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /* 我的收藏 */
        rlFavoriteMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //View view1 = View.inflate(getActivity().getApplicationContext(),R.layout.activity_my_music, null);
                Intent intentFovourite = new Intent(getActivity(), MyMusicActivity.class);
                intentFovourite.putExtra("label","我的收藏");
                startActivity(intentFovourite);
            }
        });

        /* 最近播放 */
        rlRecentMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRecentMusic = new Intent(getActivity(), MyMusicActivity.class);
                intentRecentMusic.putExtra("label","最近播放");
                intentRecentMusic.putExtra("delete",1);
                startActivity(intentRecentMusic);
            }
        });

        /*我喜欢 -- 歌单*/
        llMySongListsMyFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMyFavouriteMusicList = new Intent(getActivity(), MyFavouriteActivity.class);
                startActivity(intentMyFavouriteMusicList);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
