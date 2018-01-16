package com.hm.fragmentusedemo.modu;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.hm.fragmentusedemo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dumingwei on 2017/6/28.
 */
public class HomeFragment extends MyBaseFragment {

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @Override
    protected int bindLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fragments.add(NewsListFragment.newInstance("title" + i));
        }
        CycleFragmentAdapter adapter = new CycleFragmentAdapter(getActivity().getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(fragments.size()*100);
    }

}
