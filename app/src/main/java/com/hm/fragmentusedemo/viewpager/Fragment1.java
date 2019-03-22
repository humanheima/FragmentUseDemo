package com.hm.fragmentusedemo.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.fragmentusedemo.R;
import com.hm.fragmentusedemo.utils.Images;

public class Fragment1 extends Fragment {

    private static final String TAG = "Fragment1";
    private final String image_path = Images.imageUrls[0];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f1, null);
        Log.d(TAG, "onCreateView: ");
        TextView textView = view.findViewById(R.id.f1textview);
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(getActivity(), "ok", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}