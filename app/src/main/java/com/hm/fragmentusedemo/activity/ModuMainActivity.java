package com.hm.fragmentusedemo.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hm.fragmentusedemo.R;
import com.hm.fragmentusedemo.adapter.CycleFragmentAdapter;
import com.hm.fragmentusedemo.fragment.BaseFragment;
import com.hm.fragmentusedemo.fragment.BussinessFragment;
import com.hm.fragmentusedemo.fragment.IdeaFragment;
import com.hm.fragmentusedemo.fragment.LearningFragment;
import com.hm.fragmentusedemo.fragment.LifeFragment;
import com.hm.fragmentusedemo.fragment.SinceFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ModuMainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.image_title)
    ImageView imageTitle;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.text_menu)
    TextView textMenu;
    @BindView(R.id.left_drawer)
    ListView leftDrawer;
    @BindView(R.id.rl_menu)
    RelativeLayout rlMenu;
    private String tag = getClass().getSimpleName();
    private CycleFragmentAdapter adapter;
    private List<BaseFragment> fragmentList;

    private BaseFragment sinceFragment;
    private BaseFragment LifeFragment;
    private BaseFragment learningFragment;
    private BaseFragment bussinessFragment;
    private BaseFragment ideaFragment;
    private List<String> mPlanetTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modu_main);
        ButterKnife.bind(this);
        fragmentList = new ArrayList<>();
        mPlanetTitles = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            mPlanetTitles.add("title" + i);
        }
        leftDrawer.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mPlanetTitles));
        initAdapter();
        imageTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
            }
        });
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * 初始化Fragment的Adapter
     */
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
        adapter = new CycleFragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(fragmentList.size() * 1000);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }
}
