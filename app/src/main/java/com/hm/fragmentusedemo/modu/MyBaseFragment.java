package com.hm.fragmentusedemo.modu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hm.fragmentusedemo.viewpager.SimpleBaseFragment;

import butterknife.ButterKnife;

/**
 * Created by dumingwei on 2017/6/28.
 */
public abstract class MyBaseFragment extends SimpleBaseFragment {

    protected View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        if (null == rootView) {
            Log.e(TAG, "onCreateView:==null ");
            rootView = LayoutInflater.from(container.getContext()).inflate(bindLayout(), null);
            ButterKnife.bind(this, rootView);
            initData();
            bindEvent();
        } else {
            Log.e(TAG, "onCreateView:!=null ");

        }
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

}
