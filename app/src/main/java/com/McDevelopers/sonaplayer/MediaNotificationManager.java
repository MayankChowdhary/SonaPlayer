/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.McDevelopers.sonaplayer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.concurrent.TimeUnit;



/**
 * Keeps track of a notification and updates it automatically for a given MediaSession. This is
 * required so that the music service don't get killed during playback.
 */
public class MediaNotificationManager {

    public static final int NOTIFICATION_ID = 412;


    private static final String TAG = MediaNotificationManager.class.getSimpleName();
    private static final String CHANNEL_ID = "com.mcdevelopers.sonaplayer.channel";

    private  final SonaHeartService mService;

    private final NotificationCompat.Action mPlayAction;
    private final NotificationCompat.Action mPauseAction;
    private final NotificationCompat.Action mNextAction;
    private final NotificationCompat.Action mPrevAction;
    private final NotificationCompat.Action mStopAction;
    private  final NotificationManager mNotificationManager;
    private static MediaMetadataCompat mCurrentMetadata ;
     private  static boolean isPlaying;
     private static int playDisc2=0;
     private static int playDisc=0;
    private static   Bitmap artwork;
    private int majorColor= -15263969;
   private int minorColor= -1;
   private int middleColor= -1;






    public MediaNotificationManager(SonaHeartService service) {
        mService = service;

        mNotificationManager =
                (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);

        mPlayAction =
                new NotificationCompat.Action(
                        R.drawable.m_played,
                        mService.getString(R.string.label_play),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                mService,
                                PlaybackStateCompat.ACTION_PLAY));
        mPauseAction =
                new NotificationCompat.Action(
                        R.drawable.m_paused,
                        mService.getString(R.string.label_pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                mService,
                                PlaybackStateCompat.ACTION_PAUSE));
        mNextAction =
                new NotificationCompat.Action(
                        R.drawable.m_nexted,
                        mService.getString(R.string.label_next),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                mService,
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
        mPrevAction =
                new NotificationCompat.Action(
                        R.drawable.m_prevd,
                        mService.getString(R.string.label_previous),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                mService,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        mStopAction =
                new NotificationCompat.Action(
                        R.drawable.m_stopped,
                        mService.getString(R.string.label_previous),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                mService,
                                PlaybackStateCompat.ACTION_STOP));



        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.

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

    public Notification getNotification(MediaMetadataCompat metadata,
                                        @NonNull PlaybackStateCompat state,
                                        MediaSessionCompat.Token token) {
        mCurrentMetadata=metadata;
        try {
            isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;
            MediaDescriptionCompat description = metadata.getDescription();
            NotificationCompat.Builder builder =
                    buildNotification(state, token, isPlaying, description);

            //  Log.d("Metadata Test",String.valueOf(metadata.getLong( MediaMetadataCompat.METADATA_KEY_DURATION)));
            return builder.build();
        }catch (Throwable e){
            e.printStackTrace();
        }
        return null;
    }

    private NotificationCompat.Builder buildNotification(@NonNull PlaybackStateCompat state,
                                                         MediaSessionCompat.Token token,
                                                         boolean isPlaying,
                                                         MediaDescriptionCompat description) {

        // Create the (mandatory) notification channel when running on Android Oreo.
        if (isAndroidOOrHigher()) {
            createChannel();
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(mService, CHANNEL_ID);

        SharedPreferences OreoNotification = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        boolean oreoStyle = OreoNotification.getBoolean("oreoStyle", false);
        boolean colorized=OreoNotification.getBoolean("colorized", true);

        if (oreoStyle) {

            builder.setStyle(
                    new MediaStyle()
                            .setMediaSession(token)
                            .setShowActionsInCompactView(0, 1, 2)
                            // For backwards compatibility with Android L and earlier.
                            .setShowCancelButton(true)
                            .setCancelButtonIntent(
                                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                                            mService,
                                            PlaybackStateCompat.ACTION_STOP)))
                    .setColor(ContextCompat.getColor(mService, R.color.BlueGrey))
                    // Pending intent that is fired when user clicks on notification.
                    .setContentIntent(createContentIntent())
                    // Title - Usually Song name.
                    .setContentTitle( mCurrentMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)+" - "+mCurrentMetadata.getString("FILENAME"))
                    .setSubText(mService.getListMeta())
                    // Subtitle - Usually Albums name.
                    //.setContentText(mCurrentMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
                    .setContentText(mService.getNextSong())
                    .setShowWhen(false)
                    // When notification is deleted (when playback is paused and notification can be
                    // deleted) fire MediaButtonPendingIntent with ACTION_STOP.
                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                            mService, PlaybackStateCompat.ACTION_STOP))
                    // Show controls on lock screen even when user hides sensitive content.
                    .setColorized(true)
                    .setOngoing(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            artwork = description.getIconBitmap();



            if (isPlaying) {

                switch (playDisc) {



                    case 0:
                        builder.setSmallIcon(R.drawable.heartw);

                        if (artwork == null)
                            artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskk);

                        playDisc = playDisc + 1;

                        break;

                    case 1:
                        builder.setSmallIcon(R.drawable.heartx);

                        if (artwork == null)
                            artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskkg);
                        playDisc = playDisc + 1;

                        break;

                    case 2:
                        builder.setSmallIcon(R.drawable.hearty);

                        if (artwork == null)
                            artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskkz);

                        playDisc = playDisc + 1;

                        break;

                    case 3:
                        builder.setSmallIcon(R.drawable.heartz);

                        if (artwork == null)
                            artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskky);


                        playDisc = 0;
                        break;

                    default:
                        builder.setSmallIcon(R.drawable.heartw);

                        if (artwork == null)
                            artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskk);

                }

            } else {
                builder.setSmallIcon(R.drawable.disk_pause);

                if (artwork == null)
                    artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskk);


            }


            builder.setLargeIcon(artwork);


            // If skip to next action is enabled.
            if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
                builder.addAction(mPrevAction);
            }

            builder.addAction(isPlaying ? mPauseAction : mPlayAction);

            // If skip to prev action is enabled.
            if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
                builder.addAction(mNextAction);
            }

            builder.addAction(mStopAction);

        } else {


            RemoteViews views;
            RemoteViews bigViews;

            int notificationAction;
            PendingIntent play_pauseAction;


            if (isPlaying) {

                notificationAction =  R.drawable.mpausex;
                play_pauseAction = MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_PAUSE);


            } else {

                notificationAction =  R.drawable.mplayx;
                play_pauseAction = MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_PLAY);
            }

                views = new RemoteViews(mService.getPackageName(),
                        R.layout.status_bar);

                bigViews = new RemoteViews(mService.getPackageName(), R.layout.status_bar_expanded);

                bigViews.setImageViewResource(R.id.status_bar_play,
                        notificationAction);

            if(colorized) {
                    String major = mCurrentMetadata.getString("MajorColor");
                    String middle = mCurrentMetadata.getString("MiddleColor");
                    String minor = mCurrentMetadata.getString("MinorColor");
                    if(major!=null && middle !=null) {
                            majorColor = Integer.valueOf(major);
                            minorColor = Integer.valueOf(minor);
                            middleColor = Integer.valueOf(middle);
                    }
            }else {
                majorColor=-15263969;
                minorColor= -1;
               middleColor= -1;
            }

            views.setImageViewResource(R.id.status_bar_playPause, notificationAction);
                views.setTextViewText(R.id.status_bar_track_name,  mCurrentMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)+" - "+mCurrentMetadata.getString("FILENAME"));
                bigViews.setTextViewText(R.id.status_bar_track_name,  mCurrentMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)+" - "+mCurrentMetadata.getString("FILENAME"));
            bigViews.setTextViewText(R.id.status_bar_artist_name,  mService.getNextSong());
            views.setTextViewText(R.id.status_bar_artist_name,  mService.getNextSong());
            views.setTextViewText(R.id.status_bar_metadata, ((mCurrentMetadata.getString("SAMPLERATE")) + "  ") + (mCurrentMetadata.getString("BITRATE")) + "  " + (mCurrentMetadata.getString("SONGFORMAT")) + "  " + mCurrentMetadata.getString("CHANNEL"));
                bigViews.setTextViewText(R.id.status_bar_album_name, ((mCurrentMetadata.getString("SAMPLERATE")) + " ") + (mCurrentMetadata.getString("BITRATE")) + " " + (mCurrentMetadata.getString("SONGFORMAT")) + " " + mCurrentMetadata.getString("CHANNEL"));
                bigViews.setTextViewText(R.id.track_num, mService.getListMeta());

                bigViews.setTextViewText(R.id.end_time, timeConvert(mService.getmPlayback().getDuration()));


                bigViews.setTextViewText(R.id.time_start, timeConvert(mService.getmPlayback().getPosition()));
                bigViews.setProgressBar(R.id.progressBar, mService.getmPlayback().getDuration(), mService.getmPlayback().getPosition(), false);


                views.setOnClickPendingIntent(R.id.status_bar_playPause, play_pauseAction);
                bigViews.setOnClickPendingIntent(R.id.status_bar_play, play_pauseAction);

                views.setOnClickPendingIntent(R.id.status_bar_next, MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
                bigViews.setOnClickPendingIntent(R.id.status_bar_next, MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

                views.setOnClickPendingIntent(R.id.status_bar_previous, MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
                bigViews.setOnClickPendingIntent(R.id.status_bar_prev, MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
                bigViews.setOnClickPendingIntent(R.id.status_bar_ff, MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_FAST_FORWARD));
                bigViews.setOnClickPendingIntent(R.id.status_bar_rev, MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_REWIND));

                bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_STOP));
            views.setOnClickPendingIntent(R.id.status_bar_stop, MediaButtonReceiver.buildMediaButtonPendingIntent(
                    mService,
                    PlaybackStateCompat.ACTION_STOP));


                builder.setCustomContentView(views);
                builder.setCustomBigContentView(bigViews);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                builder.setShowWhen(false);
                builder.setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService, PlaybackStateCompat.ACTION_STOP));
                builder.setContentIntent(createContentIntent());
                builder.setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService, PlaybackStateCompat.ACTION_STOP));
                builder.setPriority(NotificationCompat.PRIORITY_MAX);
                builder.setOngoing(true);

                artwork = description.getIconBitmap();

           // Log.d("Notification loop", "buildNotification Starting: If Statement:IsPlaying: "+isPlaying);

                if (isPlaying) {

                 //   Log.d("Notification switch", "buildNotification Entered: SwitchValue: "+playDisc2);

                    switch (playDisc2) {

                        case 0:
                            builder.setSmallIcon(R.drawable.heartw);

                            if (artwork == null) {

                                if (colorized) {
                                    artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskk);
                                } else {
                                    artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.disc_mono);
                                    majorColor = -15263969;
                                    middleColor = -1;
                                    minorColor = -1;
                                }
                            }


                            playDisc2++;

                            break;

                        case 1:
                            builder.setSmallIcon(R.drawable.heartx);

                            if (artwork == null) {
                                if(colorized) {
                                    artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskkg);

                                }else {

                                    artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.disc_mono1);
                                    majorColor = -15263969;
                                    middleColor = -1;
                                    minorColor = -1;
                                }
                            }
                            playDisc2++;

                            break;

                        case 2:
                            builder.setSmallIcon(R.drawable.hearty);

                            if (artwork == null) {
                                if(colorized){
                                artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskkz);
                                }else {
                                    artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.disc_mono2);
                                    majorColor = -15263969;
                                    middleColor = -1;
                                    minorColor = -1;
                                }
                            }
                            playDisc2++;

                            break;

                        case 3:
                            builder.setSmallIcon(R.drawable.heartz);

                            if (artwork == null) {
                                if(colorized) {
                                    artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskky);
                                }else {
                                    artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.disc_mono3);
                                    majorColor = -15263969;
                                    middleColor = -1;
                                    minorColor = -1;
                                }
                            }
                            playDisc2 = 0;
                            break;

                        default:
                            playDisc2=0;
                            builder.setSmallIcon(R.drawable.heartw);
                            if (artwork == null) {
                                if(colorized){
                                artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskk);
                                }else {

                                    artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.disc_mono);
                                    majorColor = -15263969;
                                    middleColor = -1;
                                    minorColor = -1;
                                }
                            }
                    }

                } else {
                    builder.setSmallIcon(R.drawable.disk_pause);

                    if (artwork == null) {
                        if(colorized){
                        artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.diskk);
                        //majorColor =  -16777216;
                       // middleColor =  -3100672;
                       // minorColor=-3100672;
                        }else {
                            artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.disc_mono);
                            majorColor = -15263969;
                            middleColor = -1;
                            minorColor = -1;
                        }
                    }

                }

                bigViews.setImageViewBitmap(R.id.status_bar_album_art, artwork);
                views.setImageViewBitmap(R.id.status_bar_album_art, artwork);
            bigViews.setInt(R.id.notificationbg, "setBackgroundColor", majorColor);
            bigViews.setInt(R.id.status_bar_prev,"setColorFilter",middleColor);
            bigViews.setInt(R.id.status_bar_rev,"setColorFilter",middleColor);
            bigViews.setInt(R.id.status_bar_play,"setColorFilter",middleColor);
            bigViews.setInt(R.id.status_bar_ff,"setColorFilter",middleColor);
            bigViews.setInt(R.id.status_bar_next,"setColorFilter",middleColor);
            bigViews.setInt(R.id.status_bar_collapse,"setColorFilter",middleColor);
            bigViews.setInt(R.id.status_bar_track_name,"setTextColor",middleColor);
            bigViews.setInt(R.id.status_bar_artist_name,"setTextColor",middleColor);
            bigViews.setInt(R.id.status_bar_album_name,"setTextColor",middleColor);
            bigViews.setInt(R.id.track_num,"setTextColor",minorColor);
            bigViews.setInt(R.id.time_start,"setTextColor",middleColor);
            bigViews.setInt(R.id.end_time,"setTextColor",middleColor);

            if(description.getIconBitmap()==null) {
                bigViews.setInt(R.id.status_bar_album_art, "setColorFilter", middleColor);
                views.setInt(R.id.status_bar_album_art, "setColorFilter", middleColor);
            }else{
                bigViews.setInt(R.id.status_bar_album_art, "setColorFilter", 0);
                views.setInt(R.id.status_bar_album_art, "setColorFilter", 0);
            }


            views.setInt(R.id.notificationbg_small, "setBackgroundColor", majorColor);
            views.setInt(R.id.status_bar_previous,"setColorFilter",middleColor);
            views.setInt(R.id.status_bar_playPause,"setColorFilter",middleColor);
            views.setInt(R.id.status_bar_next,"setColorFilter",middleColor);
            views.setInt(R.id.status_bar_stop,"setColorFilter",middleColor);
            views.setInt(R.id.status_bar_track_name,"setTextColor",middleColor);
            views.setInt(R.id.status_bar_artist_name,"setTextColor",middleColor);
            views.setInt(R.id.status_bar_metadata,"setTextColor",middleColor);



        }



            //Toast.makeText(getApplicationContext(), "Exception raised:" + e, Toast.LENGTH_LONG).show();

        return builder;
    }


    // Does nothing on versions of Android earlier than O.
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
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

    private boolean isAndroidOOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mService, MainActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                mService, 0, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }


    @SuppressLint("DefaultLocale")
    private String timeConvert(int duration) {

        if(duration<3600000) {

            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }else {

            return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                    TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

        }

    }



}
