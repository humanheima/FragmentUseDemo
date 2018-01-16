package com.hm.fragmentusedemo.lazyload;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.hm.fragmentusedemo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LazyLoadActivity extends AppCompatActivity {

    @BindView(R.id.view_page)
    ViewPager viewPage;
    private List<Fragment> fragments;

    public static void launch(Context context) {
        Intent intent = new Intent(context, LazyLoadActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lazy_load);
        ButterKnife.bind(this);
        fragments = new ArrayList<>();
        fragments.add(FirstLazyFragment.newInstance((1)));
        fragments.add(FirstLazyFragment.newInstance((2)));
        fragments.add(FirstLazyFragment.newInstance((3)));
        fragments.add(FirstLazyFragment.newInstance((4)));
        viewPage.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
    }
}


