package com.hm.fragmentusedemo.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hm.fragmentusedemo.R;
import com.hm.fragmentusedemo.adapter.MainTabAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment {

    private String tag = getClass().getSimpleName();
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private MainTabAdapter adapter;
    private List<Fragment> fragmentList;

    private BaseFragment sinceFragment;
    private BaseFragment LifeFragment;
    private BaseFragment learningFragment;
    private BaseFragment bussinessFragment;
    private BaseFragment ideaFragment;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(tag, "setUserVisibleHint isVisibleToUser =" + isVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(tag, "onHiddenChanged hidden =" + hidden);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(tag, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(tag, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        fragmentList = new ArrayList<>();
        initTabLayout();
        initAdapter();
        return view;
    }

    private void initTabLayout() {
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        //设置tab上文字的颜色，第一个参数表示没有选中状态下的文字颜色，第二个参数表示选中后的文字颜色
        tabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#0ddcff"));
        //设置tab选中的底部的指示条的颜色
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#0ddcff"));
    }

    private void initAdapter() {
        sinceFragment = new SinceFragment();
        LifeFragment = new LifeFragment();
        learningFragment = new LearningFragment();
        bussinessFragment = new BussinessFragment();
        ideaFragment = new IdeaFragment();

        fragmentList.add(sinceFragment);
        fragmentList.add(LifeFragment);
        fragmentList.add(learningFragment);
        fragmentList.add(bussinessFragment);
        fragmentList.add(ideaFragment);

        adapter = new MainTabAdapter(getChildFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        //和tabLayout关联
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(tag, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(tag, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(tag, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(tag, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(tag, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(tag, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(tag, "onDetach");
    }
}
