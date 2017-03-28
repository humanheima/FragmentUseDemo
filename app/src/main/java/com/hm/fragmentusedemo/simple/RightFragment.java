package com.hm.fragmentusedemo.simple;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hm.fragmentusedemo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RightFragment extends Fragment {

    private static final String TAG = "RightFragment";
    TextView textTitle;
    TextView textContent;


    public RightFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right, container, false);
        textTitle = (TextView) view.findViewById(R.id.text_title);
        textContent = (TextView) view.findViewById(R.id.text_content);
        return view;
    }

    public void refresh(String title, String content) {
        textTitle.setText(title);
        textContent.setText(content);
        Log.e(TAG, title);
        Log.e(TAG, content);
    }

}
