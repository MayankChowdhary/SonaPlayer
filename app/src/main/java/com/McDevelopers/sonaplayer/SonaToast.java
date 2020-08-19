package com.McDevelopers.sonaplayer;

import android.content.Context;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class SonaToast {

    public static void setToast(Context context,String message,int length){

        Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(50);
        Toast toast =Toast.makeText(context, message, length);
        View view= toast.getView();
        //view.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_dark));
        view.setBackgroundResource(R.drawable.toast_background);
        view.setAlpha((float)1);
        view.setPadding(25,20,25,20);
        toast.setGravity(Gravity.CENTER,10,310);
        toast.show();
    }
}
