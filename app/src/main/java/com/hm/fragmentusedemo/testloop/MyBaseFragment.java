package com.hm.fragmentusedemo.testloop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dumingwei on 2017/6/28.
 */
public abstract class MyBaseFragment extends Fragment {

    private static final String TAG = "MyBaseFragment";
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "MyBaseFragment onCreateView: ");
        View rootView = LayoutInflater.from(container.getContext()).inflate(bindLayout(), null);
        unbinder = ButterKnife.bind(this, rootView);

        initData();
        bindEvent();

        return rootView;
    }

    /**
     * 绑定布局文件
     *
     * @return
     */
    protected abstract int bindLayout();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 绑定控件事件
     */
    protected void bindEvent() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

}
