package com.McDevelopers.sonaplayer;

import android.content.Context;
import android.os.Vibrator;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.ViewCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SnackbarHelper {

    public static void configSnackbar(Context context, Snackbar snack) {
        addMargins(snack);
        setRoundBordersBg(context, snack);
        ViewCompat.setElevation(snack.getView(), 6f);

        Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(10);
    }

    private static void addMargins(Snackbar snack) {

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams ) snack.getView().getLayoutParams();

        TextView mTextView =  snack.getView().findViewById(R.id.snackbar_text);

            mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            mTextView.setEllipsize(TextUtils.TruncateAt.END);
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);


        snack.getView().setPadding(0,0,0,0);
        snack.getView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        params.height=ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        params.width=ViewGroup.MarginLayoutParams.MATCH_PARENT;
        snack.getView().getLayoutParams();

        snack.getView().setLayoutParams(params);

    }

    private static void setRoundBordersBg(Context context, Snackbar snackbar) {
        snackbar.getView().setBackground(context.getDrawable(R.drawable.bg_snackbar));
    }

}