package com.hm.fragmentusedemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.hm.fragmentusedemo.fragment.BaseFragment;

import java.util.List;

public class CycleFragmentAdapter extends FragmentPagerAdapter {

    private static final String TAG = "CycleFragmentAdapter";
    private List<BaseFragment> mList;


    public CycleFragmentAdapter(FragmentManager fm, List<BaseFragment> list) {
        super(fm);
        mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        Log.e(TAG, "getItem position=" + position);
        position = position % mList.size();
        Log.e(TAG, "getItem position % mList.size()=" + position);
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position = position % mList.size();
        Log.e(TAG, "instantiateItem position % mList.size()=" + position);
        return super.instantiateItem(container, position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Log.e(TAG, "setPrimaryItem position=" + position);
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.e(TAG, "destroyItem position=" + position);
        position = position % mList.size();
        Log.e(TAG, "destroyItem position % mList.size()=" + position);
        super.destroyItem(container, position, object);
    }

}