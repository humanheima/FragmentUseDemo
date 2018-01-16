package com.hm.fragmentusedemo.modu;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.hm.fragmentusedemo.R;
import com.hm.fragmentusedemo.lazyload.BaseLazyFragment;
import com.hm.fragmentusedemo.lazyload.NotifyAdapter;
import com.hm.fragmentusedemo.lazyload.NotifyBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dumingwei on 2017/6/28.
 */
public class NewsListFragment extends BaseLazyFragment {

    @BindView(R.id.text_view)
    TextView textView;
    @BindView(R.id.ptr_rv)
    PullToRefreshRecyclerView pullToRefreshRecyclerView;
    RecyclerView rv;

    private int page = 1;
    private ArrayList<NotifyBean> data;
    private NotifyAdapter adapter;

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
        data = new ArrayList<>();
        textView.setText(bundle.getString("title"));
        rv = pullToRefreshRecyclerView.getRefreshableView();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotifyAdapter(data);
        rv.setAdapter(adapter);
    }

    @Override
    protected void bindEvent() {
        pullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pullToRefreshRecyclerView.onRefreshComplete();
                pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
                data.clear();
                page = 1;
                List<NotifyBean> tempList = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    NotifyBean notifyBean = new NotifyBean(System.currentTimeMillis() / 1000, "page" + page + "i=" + i);
                    tempList.add(notifyBean);
                }
                data.addAll(tempList);
                page++;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pullToRefreshRecyclerView.onRefreshComplete();
                if (page <= 3) {
                    List<NotifyBean> tempList = new ArrayList<>();
                    int start = page * 10;
                    for (int i = start; i < start + 10; i++) {
                        NotifyBean notifyBean = new NotifyBean(System.currentTimeMillis() / 1000, "page" + page + "i=" + i);
                        tempList.add(notifyBean);
                    }
                    data.addAll(tempList);
                    page++;
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), getString(R.string.load_all), Toast.LENGTH_SHORT).show();
                    pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            }
        });
    }

    @Override
    protected void lazyLoadData() {
        super.lazyLoadData();
        pullToRefreshRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshRecyclerView.setRefreshing();
            }
        }, 300);
    }
}
