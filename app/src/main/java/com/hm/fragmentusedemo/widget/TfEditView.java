package com.hm.fragmentusedemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

/**
 * Created by xunao on 16/8/17.
 */
public class TfEditView extends EditText {

    private OnFinishComposingListener mFinishComposingListener;

    public TfEditView(Context context) {
        super(context);
    }

    public TfEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TfEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnFinishComposingListener(OnFinishComposingListener listener) {
        this.mFinishComposingListener = listener;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new MyInputConnection(super.onCreateInputConnection(outAttrs), false);
    }

    /**
     * 软键盘关闭监听
     */
    public class MyInputConnection extends InputConnectionWrapper {

        public MyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean finishComposingText() {
            boolean finishComposing = super.finishComposingText();
            if (mFinishComposingListener != null) {
                mFinishComposingListener.finishComposing();
            }
            return finishComposing;
        }
    }

    /**
     * Created by xunao on 16/8/23.
     * 输入框软键盘关闭监听
     */
    public interface OnFinishComposingListener {
        void finishComposing();
    }
}
