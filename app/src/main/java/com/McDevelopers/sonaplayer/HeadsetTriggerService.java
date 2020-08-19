package com.McDevelopers.sonaplayer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;
import android.media.session.MediaSession;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class HeadsetTriggerService extends Service {
    private final IBinder iBinder = new LocalBinder();
    private static PowerManager.WakeLock wakeLock;
    SharedPreferences currentState;
    private static boolean autoPlay = false;
    private static MediaSession ms;
    private static boolean wakeFlag = false;
    private static int criticlBattery = 20;
    private static boolean isPowerEnabled = true;
    private AutoPlayNotification mMediaNotificationManager;
    private static boolean NotiFlag=false;
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind done");
        return iBinder;
    }

    public class LocalBinder extends Binder {

        public HeadsetTriggerService getService() {
            return HeadsetTriggerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        autoPlay = currentState.getBoolean("autoPlay", false);
        NotiFlag=currentState.getBoolean("notiFlag", false);
        mMediaNotificationManager = new AutoPlayNotification(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            launchNotification();
        }else if(NotiFlag) {
            launchNotification();
        }
        registerMediaButton();

        if (autoPlay) {
            registerheadSetPlugReciever();
            registerBluetoothReceiver();
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || NotiFlag)
            stopForeground(true);
            else
                stopSelf();

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wakeFlag = currentState.getBoolean("wakeFlag", false);
        criticlBattery = currentState.getInt("sleepBatteryLevel", 30);
        isPowerEnabled = currentState.getBoolean("powerFlag", true);

        Log.d("HeadsetTriggerService", "onStartCommand: Invoked");
        try {
            if (wakeFlag) {
                PowerManager mgr = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StandardLock:HeadsetTriggerLock");
                wakeLock.acquire();
                if (isPowerEnabled) {
                    registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                }
            }
        } catch (Throwable e) {
            Log.e("ExceptionRaised", "onCreate:Received " + e);
        }
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn()) {
            SharedPreferences.Editor editor = currentState.edit();
            editor.putBoolean("Headset", true);
            editor.commit();
        }
        return START_STICKY;
    }


    private BroadcastReceiver headSetPlugRecievers = new BroadcastReceiver() {
        @SuppressLint("ApplySharedPref")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                Log.d("HeadsetTriggerService", "onReceive: Invoked");

                int state = intent.getIntExtra("state", -1);
                SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                boolean headset = currentState.getBoolean("Headset", true);

                if (headset) {
                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putBoolean("Headset", false);
                    editor.commit();
                } else if (state == 1) {
                    Log.d("HeadsetTriggerService", "onReceive: MediaPlayCommandSent");

                  Vibration.Companion.vibrate(100);
                    Intent serviceIntent = new Intent(getApplicationContext(), SonaHeartService.class);
                    serviceIntent.putExtra("isFromHeadset", true);
                    getApplicationContext().startService(serviceIntent);

                }

            }

        }
    };

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action!=null) {
                switch (action) {
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        Vibration.Companion.vibrate(100);
                        Intent serviceIntent = new Intent(getApplicationContext(), SonaHeartService.class);
                        serviceIntent.putExtra("isFromHeadset", true);
                        getApplicationContext().startService(serviceIntent);
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        break;
                }
            }else {

                Log.e("BluetoothReceiverNull", "onReceive: NullActionReceived");
            }
        }
    };

    private void registerMediaButton() {

        ms = new MediaSession(getApplicationContext(), getPackageName());
        ms.setActive(true);
        ms.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(@NonNull Intent mediaButtonIntent) {
                Log.e("HeadsetMediaButton", "Invoked");
                final ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90);
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);

                if (Intent.ACTION_MEDIA_BUTTON.equals(mediaButtonIntent.getAction())) {
                    KeyEvent event = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    if (event != null) {
                        int action = event.getAction();
                        if (action == KeyEvent.ACTION_DOWN) {
                            Log.e("HeadsetMediaButton", "Starting SonaHeartService");

                            Intent serviceIntent = new Intent(getApplicationContext(), SonaHeartService.class);
                            serviceIntent.putExtra("isFromHeadset", true);
                            getApplicationContext().startService(serviceIntent);

                            //Toast.makeText(getApplicationContext(),"Start Service Invoked",Toast.LENGTH_SHORT).show();

                        }
                    }
                }
                return true;
            }
        });
        ms.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS);
        // you can button by receiver after terminating your app
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 2, mediaButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        ms.setMediaButtonReceiver(pendingIntent);

        // play dummy audio
        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 48000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);
        at.play();
        // a little sleep
        at.stop();
        at.release();
    }

    @Override
    public void onDestroy() {
        if (wakeFlag && wakeLock.isHeld())
            wakeLock.release();
        if (isPowerEnabled && wakeFlag)
            unregisterReceiver(mBatInfoReceiver);
        ms.setActive(false);
        ms.release();
        ms = null;
        if (autoPlay) {
            unregisterReceiver(headSetPlugRecievers);
            unregisterReceiver(mBluetoothReceiver);
        }
          if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || NotiFlag)
            stopForeground(true);
            else
               stopSelf();

        super.onDestroy();
    }


    private void registerheadSetPlugReciever() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(headSetPlugRecievers, intentFilter);
    }

    private void registerBluetoothReceiver(){

        IntentFilter filterBluetooth = new IntentFilter();
        filterBluetooth.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filterBluetooth.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBluetoothReceiver, filterBluetooth);
    }


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @SuppressLint("WakelockTimeout")
        @Override
        public void onReceive(Context arg0, Intent intent) {
            //this will give you battery current status

            try {
                int level = intent.getIntExtra("level", 0);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                if (level <= criticlBattery && status != BatteryManager.BATTERY_STATUS_CHARGING) {

                    Log.d("BatterySignalLocked", "onReceive:BatteryLevel: " + level);
                    if (wakeFlag && wakeLock.isHeld())
                        wakeLock.release();
                } else if (level > criticlBattery) {
                    if (wakeFlag && !wakeLock.isHeld()) {
                        try {
                            PowerManager mgr = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                            wakeLock = Objects.requireNonNull(mgr).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BatteryLoop:HeadsetTriggerLock");
                            wakeLock.acquire();
                        } catch (Throwable e) {

                            Log.e("ExceptionRaised", "onReceive: HeadsetTriggerPowerBroadcast");
                        }
                    }
                }
                Log.e("BatterySignalInvoked", "onReceive:BatteryLevel: " + level);
            } catch (Exception e) {
                Log.v(TAG, "Battery Info Error");
            }
        }
    };


        private void launchNotification() {
            Notification notification = mMediaNotificationManager.getNotification();

            ContextCompat.startForegroundService(
                    HeadsetTriggerService.this,
                    new Intent(HeadsetTriggerService.this, HeadsetTriggerService.class));

            notification.priority = Notification.PRIORITY_MAX;
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notification.visibility = Notification.VISIBILITY_PUBLIC;
            notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
            notification.flags = Notification.FLAG_NO_CLEAR;
            startForeground(AutoPlayNotification.NOTIFICATION_ID, notification);

        }
}
