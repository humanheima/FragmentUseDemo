package com.hm.fragmentusedemo.viewpager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.fragmentusedemo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewPagerPagerAdapterActivity extends AppCompatActivity implements View.OnClickListener {

    public int currentType = 0;// 指示当前是哪个界面
    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.message_image)
    ImageView messageImage;
    @BindView(R.id.message_text)
    TextView messageText;
    @BindView(R.id.message_layout)
    RelativeLayout messageLayout;
    @BindView(R.id.contacts_image)
    ImageView contactsImage;
    @BindView(R.id.contacts_text)
    TextView contactsText;
    @BindView(R.id.contacts_layout)
    RelativeLayout contactsLayout;
    @BindView(R.id.news_image)
    ImageView newsImage;
    @BindView(R.id.news_text)
    TextView newsText;
    @BindView(R.id.news_layout)
    RelativeLayout newsLayout;
    @BindView(R.id.setting_image)
    ImageView settingImage;
    @BindView(R.id.setting_text)
    TextView settingText;
    @BindView(R.id.setting_layout)
    RelativeLayout settingLayout;
    @BindView(R.id.btnRemove)
    Button btnRemove;
    private MyFragmentPagerAdapter adapter;
    private List<Fragment> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_pager_adapter);
        ButterKnife.bind(this);
        initViews();//找到控件id并且添加监听
        showBottomButton();//滑动界面时高亮对应的底部的（按钮）
    }

    private void initViews() {
        list.add(new Fragment1());
        list.add(new Fragment2());
        list.add(new Fragment3());
        list.add(new Fragment4());
        btnRemove = findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(this);
        messageLayout.setOnClickListener(this);
        contactsLayout.setOnClickListener(this);
        newsLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);

        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), list);
        viewPager = this.findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        //viewPager.setOffscreenPageLimit(4);

        //给ViewPager添加页面切换监听事件
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentType = position;
                viewPager.setCurrentItem(currentType);
                showBottomButton();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRemove:
                if (list.size() > 0) {
                    //删除当前的Fragment
                    adapter.destroyItem(viewPager, viewPager.getCurrentItem(), list.get(viewPager.getCurrentItem()));
                    list.remove(viewPager.getCurrentItem());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "fragment已经删光了", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.message_layout:
                currentType = 0;
                viewPager.setCurrentItem(currentType);
                showBottomButton();
                break;
            case R.id.contacts_layout:
                currentType = 1;
                viewPager.setCurrentItem(currentType);
                showBottomButton();
                break;
            case R.id.news_layout:
                currentType = 2;
                viewPager.setCurrentItem(currentType);
                showBottomButton();
                break;
            case R.id.setting_layout:
                currentType = 3;
                viewPager.setCurrentItem(currentType);
                showBottomButton();
                break;
            default:
                break;
        }
    }

    protected void showBottomButton() {
        clearSelection();
        switch (currentType) {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                messageImage.setImageResource(R.drawable.message_selected);
                messageText.setTextColor(Color.WHITE);
                break;
            case 1:
                // 当点击了联系人tab时，改变控件的图片和文字颜色
                contactsImage.setImageResource(R.drawable.contacts_selected);
                contactsText.setTextColor(Color.WHITE);
                break;
            case 2:
                // 当点击了动态tab时，改变控件的图片和文字颜色
                newsImage.setImageResource(R.drawable.news_selected);
                newsText.setTextColor(Color.WHITE);
                break;
            case 3:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                settingImage.setImageResource(R.drawable.setting_selected);
                settingText.setTextColor(Color.WHITE);
                break;
            default:
                break;
        }
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        messageImage.setImageResource(R.drawable.message_unselected);
        messageText.setTextColor(Color.parseColor("#82858b"));
        contactsImage.setImageResource(R.drawable.contacts_unselected);
        contactsText.setTextColor(Color.parseColor("#82858b"));
        newsImage.setImageResource(R.drawable.news_unselected);
        newsText.setTextColor(Color.parseColor("#82858b"));
        settingImage.setImageResource(R.drawable.setting_unselected);
        settingText.setTextColor(Color.parseColor("#82858b"));
    }

    /**
     * 使用FragmentPagerAdapter适配器的时候，他会持久化数据，即使Fragment被系统销毁，数据也是会保存在内存中的
     * 所以不能使用它来加载大量的数据
     * 使用FragmentStatePagerAdapter适配器的时候，他会销毁Fragment仅仅保存Fragment的引用，
     * 适合加载一些大量的数据 ，
     */
    public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> list;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

    }

}
