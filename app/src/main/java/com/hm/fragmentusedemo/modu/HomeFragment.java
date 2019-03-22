package com.hm.fragmentusedemo.modu;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hm.fragmentusedemo.R;
import com.hm.fragmentusedemo.activity.KotlinTestActivity;

import butterknife.BindView;

/**
 * Created by dumingwei on 2017/6/28.
 */
public class HomeFragment extends MyBaseFragment {

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.tvHomeFragment)
    TextView tvHomeFragment;
    @Override
    protected int bindLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
       /* List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fragments.add(NewsListFragment.newInstance("title" + i));
        }
        CycleFragmentAdapter adapter = new CycleFragmentAdapter(getActivity().getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(fragments.size()*100);*/
        tvHomeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), KotlinTestActivity.class);
                startActivity(intent);
            }
        });
    }

}
