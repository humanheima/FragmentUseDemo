package com.hm.fragmentusedemo.lazyload;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.fragmentusedemo.R;
import com.hm.fragmentusedemo.utils.DateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 19658 on 2017/5/25.
 */

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.ViewHolder> {


    private List<NotifyBean> data;

    public NotifyAdapter(List<NotifyBean> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notify, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String title = data.get(position).getTitle();
        holder.tvNotify.setText(title);
        holder.tvDate.setText(DateUtils.getYmdHm(data.get(position).getAdd_time()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(), title, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_notify)
        TextView tvNotify;
        @BindView(R.id.tv_date)
        TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
