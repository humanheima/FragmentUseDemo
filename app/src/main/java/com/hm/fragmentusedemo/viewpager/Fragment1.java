package com.hm.fragmentusedemo.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.fragmentusedemo.R;
import com.hm.fragmentusedemo.utils.Images;

public class Fragment1 extends SimpleBaseFragment {


    private final String image_path = Images.imageUrls[0];
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        if (rootView == null) {
            Log.e(TAG, "onCreateView: rootView==null");
            rootView = inflater.inflate(R.layout.f1, null);

            TextView textView = rootView.findViewById(R.id.f1textview);
            textView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Toast.makeText(getActivity(), "ok", Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Log.e(TAG, "onCreateView: rootView!=null");
        }

        return rootView;
    }

}