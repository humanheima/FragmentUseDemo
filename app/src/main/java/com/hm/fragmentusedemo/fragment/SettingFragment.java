package com.hm.fragmentusedemo.fragment;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hm.fragmentusedemo.R;
import com.hm.fragmentusedemo.widget.TfEditView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment {


    private static final String TAG = "SettingFragment";
    public static final int DICUSS_CAN_HIDE_TIME = 500;
    @BindView(R.id.btn_pop_window)
    Button btnPopWindow;
    Unbinder unbinder;
    private PopupWindow popupWindow;
    private View view;
    private TextView textCancel;
    private TextView textSend;
    private TfEditView editText;
    private InputMethodManager imm;
    private long openInputTime;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG, "setUserVisibleHint isVisibleToUser =" + isVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged hidden =" + hidden);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
        unbinder.unbind();
    }

    @OnClick(R.id.btn_pop_window)
    public void onViewClicked() {
        if (popupWindow == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.pop_layout, null);
            textCancel = (TextView) view.findViewById(R.id.text_cancel);
            textSend = (TextView) view.findViewById(R.id.text_send);
            editText = (TfEditView) view.findViewById(R.id.edit_text);
            editText.setOnFinishComposingListener(new TfEditView.OnFinishComposingListener() {
                @Override
                public void finishComposing() {
                    if (popupWindow != null && popupWindow.isShowing() && openInputTime != 0 && (System.currentTimeMillis() - openInputTime > DICUSS_CAN_HIDE_TIME)) {
                        popupWindow.dismiss();
                    }
                }
            });
            textCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.setText("");
                    hideKeyBoard();
                    popupWindow.dismiss();
                }
            });
            textSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.setText("");
                    hideKeyBoard();
                    popupWindow.dismiss();
                }
            });
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        }
        popupWindow.showAtLocation(btnPopWindow, Gravity.BOTTOM, 0, 0);
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.requestFocus();
        imm.toggleSoftInput(0, 0);
        openInputTime = System.currentTimeMillis();
    }

    private void hideKeyBoard() {
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, 0);
        }
    }
}
