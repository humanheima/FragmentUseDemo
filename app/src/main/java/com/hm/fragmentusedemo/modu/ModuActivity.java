package com.hm.fragmentusedemo.modu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hm.fragmentusedemo.R;
import com.hm.fragmentusedemo.fragment.CarFragment;
import com.hm.fragmentusedemo.fragment.MusicFragment;
import com.hm.fragmentusedemo.fragment.SettingFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * fake 魔都行囊主界面
 */
public class ModuActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = ModuActivity.class.getSimpleName();
    private static final int HOME = 0;
    private static final int MUSIC = 1;
    private static final int CAR = 2;
    private static final int SETTING = 3;

    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.ll_music)
    LinearLayout llMusic;
    @BindView(R.id.ll_car)
    LinearLayout llCar;
    @BindView(R.id.ll_setting)
    LinearLayout llSetting;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindViews({R.id.img_search, R.id.img_music, R.id.img_car, R.id.img_setting})
    List<ImageView> imgTabMap;
    @BindViews({R.id.text_search, R.id.text_music, R.id.text_car, R.id.text_setting})
    List<TextView> textTabList;
    private SparseIntArray sparseArrayNormal = new SparseIntArray(4);
    private SparseIntArray sparseArrayPressed = new SparseIntArray(4);
    private SparseArray<Fragment> fragmentTabMap = new SparseArray<>(4);
    private int preFrag = -1;
    private int nowFrag = 0;
    private View activityRootView;

    public static void launch(Context context) {
        Intent intent = new Intent(context, ModuActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initIcon();
        initListener();
        changeTab();
        activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    private void initIcon() {
        sparseArrayNormal.put(HOME, R.drawable.ic_main);
        sparseArrayNormal.put(MUSIC, R.drawable.ic_music);
        sparseArrayNormal.put(CAR, R.drawable.ic_car);
        sparseArrayNormal.put(SETTING, R.drawable.ic_setting);

        sparseArrayPressed.put(HOME, R.drawable.ic_main_pressed);
        sparseArrayPressed.put(MUSIC, R.drawable.ic_music_pressed);
        sparseArrayPressed.put(CAR, R.drawable.ic_car_pressed);
        sparseArrayPressed.put(SETTING, R.drawable.ic_setting_pressed);
    }

    private void initListener() {
        llSearch.setOnClickListener(new TabClickListener(HOME));
        llMusic.setOnClickListener(new TabClickListener(MUSIC));
        llCar.setOnClickListener(new TabClickListener(CAR));
        llSetting.setOnClickListener(new TabClickListener(SETTING));
    }

    private void changeTab() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (preFrag != -1) {
            imgTabMap.get(preFrag).setImageResource(sparseArrayNormal.get(preFrag));
            textTabList.get(preFrag).setTextColor(getResources().getColor(R.color.colorPrimary));
            fragmentTransaction.hide(fragmentTabMap.get(preFrag));
        }
        imgTabMap.get(nowFrag).setImageResource(sparseArrayPressed.get(nowFrag));
        textTabList.get(nowFrag).setTextColor(getResources().getColor(R.color.colorAccent));

        if (fragmentTabMap.get(nowFrag) == null) {
            switch (nowFrag) {
                case HOME:
                    HomeFragment homeFragment = new HomeFragment();
                    fragmentTabMap.put(HOME, homeFragment);
                    fragmentTransaction.add(R.id.frame_layout, homeFragment);
                    break;
                case MUSIC:
                    MusicFragment musicFragment = new MusicFragment();
                    fragmentTabMap.put(MUSIC, musicFragment);
                    fragmentTransaction.add(R.id.frame_layout, musicFragment);
                    break;
                case CAR:
                    CarFragment carFragment = new CarFragment();
                    fragmentTabMap.put(CAR, carFragment);
                    fragmentTransaction.add(R.id.frame_layout, carFragment);
                    break;
                case SETTING:
                    SettingFragment settingFragment = new SettingFragment();
                    fragmentTabMap.put(SETTING, settingFragment);
                    fragmentTransaction.add(R.id.frame_layout, settingFragment);
                    break;
                default:
                    break;
            }
        } else {
            fragmentTransaction.show(fragmentTabMap.get(nowFrag));
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onGlobalLayout() {
        if (isKeyboardShown(activityRootView)) {
            Log.e(TAG, "软键盘弹起");
            llBottom.setVisibility(View.GONE);
        } else {
            llBottom.postDelayed(new Runnable() {
                @Override
                public void run() {
                    llBottom.setVisibility(View.VISIBLE);
                }
            }, 300);
            Log.e(TAG, "软键盘弹起关闭");
        }
    }

    private boolean isKeyboardShown(View rootView) {
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        //设定一个认为是软键盘弹起的阈值
        final int softKeyboardHeight = (int) (100 * dm.density);
        //得到屏幕可见区域的大小
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        int heightDiff = dm.heightPixels - r.bottom;
        Log.e(TAG, "isKeyboardShown: " + dm.heightPixels + "," + r.bottom);
        return heightDiff > softKeyboardHeight;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    private class TabClickListener implements View.OnClickListener {

        private int position;

        public TabClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (position != nowFrag) {
                preFrag = nowFrag;
                nowFrag = position;
                changeTab();
            }
        }
    }
}
