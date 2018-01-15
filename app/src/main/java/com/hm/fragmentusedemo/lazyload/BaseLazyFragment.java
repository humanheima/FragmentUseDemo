package com.hm.fragmentusedemo.lazyload;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseLazyFragment extends Fragment {

    protected String TAG = getClass().getSimpleName();
    protected int page = 1;

    protected boolean isViewCreated;
    protected boolean isLoadDataCompleted;
    /**
     * 定义 成员变量 rootView，当Fragment第一次可见的时候，会调用 onCreateView 创建rootView，
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
        if (isVisibleToUser && isViewCreated && !isLoadDataCompleted) {
            lazyLoadData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayout(), container, false);
            ButterKnife.bind(this, rootView);
            init(rootView);
            isViewCreated = true;
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            lazyLoadData();
        }
    }

    protected abstract int getLayout();

    protected abstract void init(View view);

    protected void lazyLoadData() {
        isLoadDataCompleted = true;
    }

}
