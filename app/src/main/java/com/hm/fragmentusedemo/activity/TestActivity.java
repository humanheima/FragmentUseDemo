package com.hm.fragmentusedemo.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.hm.fragmentusedemo.R;

public class TestActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "TestActivity";
    private View activityRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (isKeyboardShown(activityRootView)) {
            Log.e(TAG, "软键盘弹起");
        } else {
            Log.e(TAG, "软键盘关闭");
        }
    }

    private boolean isKeyboardShown(View rootView) {
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        //设定一个认为是软键盘弹起的阈值
        final int softKeyboardHeight = (int) (100 * dm.density);
        //得到屏幕可见区域的大小
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        int heightDiff = dm.heightPixels - r.bottom;
        Log.e(TAG, "isKeyboardShown: " + dm.heightPixels + "," + r.bottom);
        return heightDiff > softKeyboardHeight;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }
}
