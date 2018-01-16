package com.hm.fragmentusedemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hm.fragmentusedemo.R;
import com.hm.fragmentusedemo.lazyload.LazyLoadActivity;
import com.hm.fragmentusedemo.modu.ModuActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    void launchLazyActivity(View view) {
        LazyLoadActivity.launch(this);
    }

    void launchMainActivity(View view) {
        ModuActivity.launch(this);
    }
}
