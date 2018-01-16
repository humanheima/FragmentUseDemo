package com.hm.fragmentusedemo.simple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hm.fragmentusedemo.R;

/**
 * http://www.jianshu.com/p/52daa5ff5130 死磕Fragment生命周期
 */
public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
    }
}
