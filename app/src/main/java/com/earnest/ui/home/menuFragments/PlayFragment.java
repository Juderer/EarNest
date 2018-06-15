package com.earnest.ui.home.menuFragments;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.earnest.R;
import com.earnest.ui.adapter.BaseFragment;
import com.earnest.ui.myMusic.LocalMusicActivity;

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

        setUIControlsOnClick();
    }

    private void setUIControlsOnClick() {
        //Menu items
        rlLocalMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LocalMusicActivity.class));
            }
        });
        rlDownloadMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rlFavoriteMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rlRecentMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
