package com.McDevelopers.sonaplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.RemoteViews;

public  class  AutoPlayNotification {

    public static final int NOTIFICATION_ID = 415;


    private static final String TAG = AutoPlayNotification.class.getSimpleName();
    private static final String CHANNEL_ID = "com.mcdevelopers.sonaplayer.channel";
    private  final NotificationManager mNotificationManager;

    private final     HeadsetTriggerService mService;

    public AutoPlayNotification(HeadsetTriggerService service){

        this.mService=service;
        mNotificationManager = (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        if(mNotificationManager!=null)
            mNotificationManager.cancelAll();


    }
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");

        mNotificationManager.cancel(NOTIFICATION_ID);
        mNotificationManager.cancelAll();
    }
    public NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    public   Notification getNotification() {
        try {
            NotificationCompat.Builder builder =
                    buildNotification();
            return builder.build();
        }catch (Throwable e){
            e.printStackTrace();
        }
        return null;
    }


    private NotificationCompat.Builder buildNotification() {

        // Create the (mandatory) notification channel when running on Android Oreo.
        if (isAndroidOOrHigher()) {
            createChannel();
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mService, CHANNEL_ID);
      PendingIntent  mPlayAction = MediaButtonReceiver.buildMediaButtonPendingIntent(
                mService,
                PlaybackStateCompat.ACTION_PLAY);
        RemoteViews views;
        views = new RemoteViews(mService.getPackageName(),
                R.layout.autoplay_notification);
        views.setOnClickPendingIntent(R.id.autoplay_button, mPlayAction);
        builder.setCustomContentView(views);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setShowWhen(false);
        builder.setContentIntent(createContentIntent());
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setOngoing(true);

        builder.setSmallIcon(R.drawable.autostarticon);

        return builder;
    }

    private  boolean isAndroidOOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private  void createChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = "Sona Music";
            // The user-visible description of the channel.
            // String description = "MediaSession and MediaPlayer";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Configure the notification channel.
            mChannel.setShowBadge(false);
            // mChannel.setSound(null,null);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(mChannel);
            Log.d(TAG, "createChannel: New channel created");
        } else {
            Log.d(TAG, "createChannel: Existing channel reused");
        }
    }


    private  PendingIntent createContentIntent() {
        Intent openUI = new Intent(mService, MainActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                mService, 0, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
