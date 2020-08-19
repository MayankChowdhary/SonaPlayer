package com.McDevelopers.sonaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.util.Log;

public class SonaBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context contex, Intent intent){
        Log.d("BootBroadcastReceived", "onReceive: Invoked");
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(
                        ApplicationContextProvider.getContext(),
                        new Intent(ApplicationContextProvider.getContext(), HeadsetTriggerService.class));
            }else {
                Intent serviceIntent = new Intent(contex, HeadsetTriggerService.class);
                contex.startService(serviceIntent);
            }
                //Toast.makeText(contex, "Sona Boot Receiver invoked", Toast.LENGTH_SHORT).show();
            }
       // this.abortBroadcast();

    }

}
