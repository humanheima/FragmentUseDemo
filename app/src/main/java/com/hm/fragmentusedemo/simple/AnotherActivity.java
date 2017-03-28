package com.hm.fragmentusedemo.simple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hm.fragmentusedemo.R;

import butterknife.ButterKnife;

public class AnotherActivity extends AppCompatActivity {

    private static final String TAG = "AnotherActivity";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    private String content;
    private RightFragment fragment;
    private String title;

    public static void launch(Context context, String title, String content) {
        Intent starter = new Intent(context, AnotherActivity.class);
        starter.putExtra(TITLE, title);
        starter.putExtra(CONTENT, content);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);
        ButterKnife.bind(this);
        title = getIntent().getStringExtra(TITLE);
        content = getIntent().getStringExtra(CONTENT);
        Log.e(TAG, "title=" + title + ",content=" + content);
        fragment = (RightFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_right);
        fragment.refresh(title, content);
    }
}
