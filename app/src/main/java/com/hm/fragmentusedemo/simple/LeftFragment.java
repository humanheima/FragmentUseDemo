package com.hm.fragmentusedemo.simple;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hm.fragmentusedemo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeftFragment extends Fragment {

    private static final String TAG = "LeftFragment";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<News> newsList;

    private boolean isTwoPane = false;
    private RvAdapter adapter;

    public LeftFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_left, container, false);
        ButterKnife.bind(this, view);
        newsList = new ArrayList<>();
        initNews();
        setAdapter();
        return view;
    }

    private void initNews() {
        for (int i = 0; i < 30; i++) {
            News news = new News("titile" + i, "content" + i);
            newsList.add(news);
        }
    }

    private void setAdapter() {
        if (adapter == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new RvAdapter();
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity().findViewById(R.id.frame_content_layout) != null) {
            isTwoPane = true;
        } else {
            isTwoPane = false;
        }
    }

    class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final News news = newsList.get(position);
            if (isTwoPane) {
                if (position == 0) {
                    RightFragment rightFragment = (RightFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_right);
                    rightFragment.refresh(news.getTitle(), news.getContent());
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isTwoPane) {
                        RightFragment rightFragment = (RightFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_right);
                        rightFragment.refresh(news.getTitle(), news.getContent());
                    } else {
                        Log.e(TAG, news.getTitle() + "," + news.getContent());
                        AnotherActivity.launch(getContext(), news.getTitle(), news.getContent());
                    }
                }
            });
            holder.textView.setText(news.getTitle());
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(android.R.id.text1);
            }
        }

    }

}
