package com.hm.fragmentusedemo.lazyload;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.hm.fragmentusedemo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class SecondLazyFragment extends BaseLazyFragment {

    public static final String ID = "ID";
    protected String TAG = getClass().getSimpleName();
    @BindView(R.id.ptr_rv)
    PullToRefreshRecyclerView pullToRefreshRecyclerView;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private int id = -1;

    private List<NotifyBean> data;
    private NotifyAdapter adapter;

    public SecondLazyFragment() {
        // Required empty public constructor
    }

    public static SecondLazyFragment newInstance(int id) {
        SecondLazyFragment fragment = new SecondLazyFragment();
        Bundle args = new Bundle();
        args.putInt(ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ID);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_lazy;
    }

    @Override
    protected void init(View view) {
        Log.e(TAG, "onCreateView init id=" + id);
        tvTitle.setText("fragment id is " + id);
        data = new ArrayList<>();
        rv = pullToRefreshRecyclerView.getRefreshableView();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotifyAdapter(data);
        rv.setAdapter(adapter);
        pullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                getData();
            }
        });
    }

    @Override
    protected void lazyLoadData() {
        super.lazyLoadData();
        Log.e(TAG, "lazyLoadData id=" + id);
        getData();
    }

    private void getData() {
        pullToRefreshRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshRecyclerView.onRefreshComplete();
                if (page <= 3) {
                    List<NotifyBean> tempList = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        NotifyBean notifyBean = new NotifyBean(System.currentTimeMillis() / 1000, "page" + page + "i=" + i);
                        tempList.add(notifyBean);
                    }
                    data.addAll(tempList);
                    page++;
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), getString(R.string.load_all), Toast.LENGTH_SHORT).show();
                    pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
            }
        }, 500);
    }
}
