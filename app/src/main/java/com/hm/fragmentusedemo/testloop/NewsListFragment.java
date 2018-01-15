package com.hm.fragmentusedemo.testloop;

import android.os.Bundle;
import android.widget.TextView;

import com.hm.fragmentusedemo.R;

import butterknife.BindView;

/**
 * Created by dumingwei on 2017/6/28.
 */
public class NewsListFragment extends MyBaseFragment {

    @BindView(R.id.text_view)
    TextView textView;

    public static NewsListFragment newInstance(String title) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_newslist;
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        textView.setText(bundle.getString("title"));
    }

}
