package com.McDevelopers.sonaplayer;

import android.content.Context;
import com.google.android.material.textfield.TextInputEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class CustomTextInputEditText extends TextInputEditText {

    private KeyImeChange keyImeChangeListener;

    public CustomTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setKeyImeChangeListener(KeyImeChange listener){
        keyImeChangeListener = listener;
    }

    public interface KeyImeChange {
        public void onKeyIme(int keyCode, KeyEvent event);
    }

    @Override
    public boolean onKeyPreIme (int keyCode, KeyEvent event){
        if(keyImeChangeListener != null){
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int startSelection = getSelectionStart();
        int endSelection = getSelectionEnd();
        if (startSelection != endSelection) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                final CharSequence text = getText();
                setText(null);
                setText(text);
            }
        }
        return super.dispatchTouchEvent(event);
    }


}