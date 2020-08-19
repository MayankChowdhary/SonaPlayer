package com.McDevelopers.sonaplayer;

import android.content.Context;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class CustomEditText extends AppCompatEditText {

    private KeyImeChange keyImeChangeListener;


    public CustomEditText( Context context )
    {
        super( context );
    }

    public CustomEditText( Context context, AttributeSet attribute_set )
    {
        super( context, attribute_set );
    }

    public CustomEditText( Context context, AttributeSet attribute_set, int def_style_attribute )
    {
        super( context, attribute_set, def_style_attribute );
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