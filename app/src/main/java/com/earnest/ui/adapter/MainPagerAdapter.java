package com.earnest.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Administrator on 2018/6/15.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    private BaseFragment[] mFragments;

    public MainPagerAdapter(FragmentManager fm, BaseFragment[] fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        if (mFragments == null) return 0;
        return mFragments.length;
    }
}
