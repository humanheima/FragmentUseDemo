package com.hm.fragmentusedemo.testloop;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.hm.fragmentusedemo.R;
import com.hm.fragmentusedemo.adapter.CycleFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dumingwei on 2017/6/28.
 */
public class HomeFragment extends MyBaseFragment {

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private List<Fragment> fragments;

    private CycleFragmentAdapter adapter;

    @Override
    protected int bindLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        fragments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fragments.add(NewsListFragment.newInstance("title" + i));
        }
        adapter = new CycleFragmentAdapter(getActivity().getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(fragments.size()*100);
    }

}
