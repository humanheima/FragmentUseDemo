package com.hm.fragmentusedemo.lazyload;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseLazyFragment extends Fragment {

    protected String TAG = getClass().getSimpleName();
    protected int page = 1;

    //标记布局是否已经初始化完毕
    protected boolean isViewCreated;
    //标记是否已经加载过数据
    protected boolean isLoadDataCompleted;
    /**
     * 声明 成员变量 rootView，当Fragment第一次可见的时候，会调用 onCreateView 创建rootView，
     * 当Fragment切换为不可见并调用了 onDestroyView 销毁视图的时候，
     * 这个rootView并不会被销毁。当Fragment 重新切换回来并调用onCreateView创建视图的时候，
     * 直接返回rootView.
     */
    protected View rootView;
    protected RecyclerView rv;

    public BaseLazyFragment() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //如果当前Fragment可见，onCreateView 已经调用完毕，并且没有加载过数据，则加载数据
        if (isVisibleToUser && isViewCreated && !isLoadDataCompleted) {
            lazyLoadData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(bindLayout(), container, false);
            ButterKnife.bind(this, rootView);
            initData();
            bindEvent();
            isViewCreated = true;
        }
        return rootView;
    }

    /**
     * 在这里要调用一次lazyLoadData ，因为ViewPage展示第一页的时候
     * setUserVisibleHint 先于 onCreateView调用，这时候 isViewCreated 为false，不会加载数据
     * onActivityCreated 在 onCreateView 之后调用，这时候视图已经初始化完毕 isViewCreated 为 true，
     * 加载数据
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            lazyLoadData();
        }
    }

    //获取布局文件
    protected abstract int bindLayout();

    //初始化一些view和相关数据
    protected abstract void initData();

    //绑定事件
    protected void bindEvent() {

    }

    //加载数据
    protected void lazyLoadData() {
        isLoadDataCompleted = true;
    }

}
