package com.hm.fragmentusedemo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hm.fragmentusedemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SinceFragment extends BaseFragment {

    @BindView(R.id.text_since)
    TextView textSince;

    private String tag = getClass().getSimpleName();

    public SinceFragment() {
        // Required empty public constructor
    }

    /**
     * 这个方法会被 viewPager 的 adapter触发
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(tag, "setUserVisibleHint isVisibleToUser =" + isVisibleToUser);
    }

    /**
     * 这个方法会在fragmentTransaction hide 或者 show 的时候触发
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(tag, "onHiddenChanged hidden =" + hidden);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(tag, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(tag, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(tag, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_since, container, false);
        ButterKnife.bind(this, view);
        isViewCreated = true;
        return view;
    }

    @Override
    protected void lazyLoadData() {
        if (isViewCreated) {
            Log.e(tag, "lazyLoadData");
            textSince.setText("我的科技界面");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(tag, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(tag, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(tag, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(tag, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(tag, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(tag, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(tag, "onDetach");
    }

}
