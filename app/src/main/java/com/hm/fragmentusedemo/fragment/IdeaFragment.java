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
public class IdeaFragment extends BaseFragment {

    @BindView(R.id.text_idea)
    TextView textIdea;
    private String tag = getClass().getSimpleName();


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(tag, "setUserVisibleHint isVisibleToUser =" + isVisibleToUser);
    }

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

    public IdeaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(tag, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_idea, container, false);
        ButterKnife.bind(this, view);
        isViewCreated = true;
        return view;
    }

    @Override
    protected void lazyLoadData() {
        if (isViewCreated) {
            Log.e(tag,"lazyLoadData");
            textIdea.setText("我的想法");
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
