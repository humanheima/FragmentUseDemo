package com.hm.fragmentusedemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hm.fragmentusedemo.fragment.BaseFragment;
import com.hm.fragmentusedemo.fragment.CarFragment;
import com.hm.fragmentusedemo.fragment.MusicFragment;
import com.hm.fragmentusedemo.fragment.SearchFragment;
import com.hm.fragmentusedemo.fragment.SettingFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.text_search)
    TextView textSearch;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.img_music)
    ImageView imgMusic;
    @BindView(R.id.text_music)
    TextView textMusic;
    @BindView(R.id.ll_music)
    LinearLayout llMusic;
    @BindView(R.id.img_car)
    ImageView imgCar;
    @BindView(R.id.text_car)
    TextView textCar;
    @BindView(R.id.ll_car)
    LinearLayout llCar;
    @BindView(R.id.img_setting)
    ImageView imgSetting;
    @BindView(R.id.text_setting)
    TextView textSetting;
    @BindView(R.id.ll_setting)
    LinearLayout llSetting;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    private SearchFragment searchFragment;
    private MusicFragment musicFragment;
    private CarFragment carFragment;
    private SettingFragment settingFragment;

    private BaseFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initListener();
    }

    private void initData() {
        if (searchFragment == null) {
            searchFragment = new SearchFragment();
            if (!searchFragment.isAdded()) {
                // 提交事务
                getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, searchFragment).commit();
                // 记录当前Fragment
                currentFragment = searchFragment;
            }
        }
    }

    private void initListener() {
        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSearchLayout();
            }
        });
        llMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMusicLayout();
            }
        });
        llCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCarLayout();
            }
        });
        llSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSettingLayout();
            }
        });
    }

    private void clickSearchLayout() {
        if (searchFragment == null) {
            searchFragment = new SearchFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), searchFragment);
    }

    private void clickMusicLayout() {
        if (musicFragment == null) {
            musicFragment = new MusicFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), musicFragment);
    }

    private void clickCarLayout() {
        if (carFragment == null) {
            carFragment = new CarFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), carFragment);
    }

    private void clickSettingLayout() {
        if (settingFragment == null) {
            settingFragment = new SettingFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), settingFragment);
    }


    /**
     * 添加或者显示 fragment
     *
     * @param transaction
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        if (currentFragment == fragment)
            return;
        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment).add(R.id.frame_layout, fragment).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).commit();
        }
        currentFragment.setUserVisibleHint(false);

        currentFragment = (BaseFragment) fragment;

        currentFragment.setUserVisibleHint(true);
    }
}
