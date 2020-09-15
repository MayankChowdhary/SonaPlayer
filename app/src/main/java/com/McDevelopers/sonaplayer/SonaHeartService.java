
package com.McDevelopers.sonaplayer;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import androidx.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import androidx.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

@SuppressLint({"SetTextI18n","ApplySharedPref","WakelockTimeout"})
public class SonaHeartService extends MediaBrowserServiceCompat implements MusicLibrary.postMetaUpdateListener {

    private static final String TAG = SonaHeartService.class.getSimpleName();
    TextToSpeech textToSpeechSystem;
    private MediaSessionCompat mSession;
    public  PlayerAdapter mPlayback;
    private MediaNotificationManager mMediaNotificationManager;
    private boolean mServiceInStartedState;
    private  static List<MediaSessionCompat.QueueItem> mPlaylist = new ArrayList<>();

    private static MediaMetadataCompat mPreparedMedia;
    private Handler mUpdateNextHandler = new Handler();
    public static int playlistSize;
    static int d = 0;
    private static int mQueueIndex = -1;
    private static int shuffleIndex = 0;
    private MediaBrowserHelper mMediaBrowserHelper;
    private static boolean startup = true;
    private static long startSeek = 0;
    private static boolean mIsShuffle = true;
    public static int mIsRepeat = 2;
    SharedPreferences currentState;
    public BigComputationTask task;
    CountDownTimer cTimer = null;
    static long timerSync = 0;
    private static int volLevel = 0;
    private static int earVolLevel=0;
    public static boolean isMediaButtonStopped = false;
    public static boolean isActivityDestroyed = true;
    private static boolean next = false;

    public static  boolean isServiceRunning=false;

    static int maxVolume;
     static int percentVol ;
    static Deque<Integer> songdDeque = new ArrayDeque<>();
    private static AudioManager mAudioManager;
    private static  int mCrossfadeDuration=15000;
    private Handler startCrossFadeHandler = new Handler();
    private static String nextSongId;
    private static boolean isNextVideo;
    private static boolean masterCrossFade=true;
    private static boolean autoResume=false;
    private static boolean headset=true;
    private static PowerManager.WakeLock wakeLock;
    private static  int criticlBattery=20;
    private static boolean isPowerEnabled=true;
    private static boolean isWakeLockEnabled=false;
    private static boolean firstPower=true;
    private static boolean isAutoSleep=false;
    private static int autoSleepBattery=30;
    private static boolean supressStop=false;
    public static boolean isQueueActive=false;

    @Override
    public void onCreate() {
        super.onCreate();
        startup = true;
        firstPower=true;
        Log.d("StartUp Variable", "onStartCommand: value startup and shuffleStart is:" + startup);
        SharedPreferences currentIndex = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        mQueueIndex = currentIndex.getInt("currentIndex", -1);
        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        mIsShuffle = currentState.getBoolean("shuffle", true);
        mIsRepeat = currentState.getInt("repeat", 2);
        volLevel = currentState.getInt("volLevel", (int) (maxVolume * 0.5f));
        earVolLevel = currentState.getInt("earVolLevel", (int) (maxVolume * 0.4f));
        masterCrossFade = currentState.getBoolean("mCrossFade", true);
        autoResume = currentState.getBoolean("autoResume", true);
        mCrossfadeDuration=currentState.getInt("crossfadeTime",15000);
      criticlBattery=currentState.getInt("batteryLevel",20);
        isPowerEnabled=currentState.getBoolean("powerFlag",true);
        isWakeLockEnabled=currentState.getBoolean("playerWakeLock",false);
        isAutoSleep=currentState.getBoolean("isAutoSleep",false);
        autoSleepBattery=currentState.getInt("sleepBatteryLevel",30);
        isQueueActive=currentState.getBoolean("QueueFlag",false);

        Log.d("LastVideoTime", "onCreate:Got is: "+currentIndex.getLong("currentPosition", 0));

        SharedPreferences.Editor editor;
        editor = currentState.edit();
        editor.putBoolean("fullscreen", false);
        editor.commit();


        try {

            if(isWakeLockEnabled) {
                PowerManager mgr = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                if (mgr != null) {
                    wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ServiceLock:SonaHeartLock");
                    wakeLock.acquire();
                }

            }

            mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (mAudioManager != null) {
                maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            }
            percentVol = (int) (maxVolume * 0.4f);
        } catch (Throwable e) {
            maxVolume = 15;
            percentVol = (int) (maxVolume * 0.4f);
        }




        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mSession = new MediaSessionCompat(getApplicationContext(), "Tag", mediaButtonReceiver, null);
        // Create a new MediaSession.
        mSession = new MediaSessionCompat(this, "SonaHeartService");
        MediaSessionCallback mCallback = new MediaSessionCallback();
        mSession.setCallback(mCallback);
        mSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);


        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, mediaButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSession.setMediaButtonReceiver(pendingIntent);
        setSessionToken(mSession.getSessionToken());
        mSession.setSessionActivity(PendingIntent.getActivity(this, 1,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        mMediaNotificationManager = new MediaNotificationManager(this);
        mPlayback = new MediaPlayerAdapter(this, new MediaPlayerListener());

        MusicLibrary.registerMetaUpdateListener(this);

    }

    public void startShuffles() {

        Random rand = new Random();
        int newSong = mQueueIndex;
        while (newSong == mQueueIndex) {
            newSong = rand.nextInt(mPlaylist.size());
        }
        shuffleIndex = newSong;

        Log.d("StartShuffleInvoked:", "nextSongIs: " + mPlaylist.get(shuffleIndex).getDescription().getTitle());

    }


    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        MusicLibrary.registerMetaUpdateListener(this);
        if(wakeLock!=null && isWakeLockEnabled && !wakeLock.isHeld()){
            wakeLock.acquire();
            Log.d("PowerWakeLock", "onStartCommand: Aquired");
        }

        isServiceRunning=true;
        supressStop=false;
        Log.e("SonaHeartService", "onStartCommand: Service Starting..." );
        autoResume=currentState.getBoolean("autoResume",true);

        if (mMediaBrowserHelper == null) {
            mMediaBrowserHelper = new SonaHeartService.MediaBrowserConnection(ApplicationContextProvider.getContext());
            mMediaBrowserHelper.onStart();
        }
        MediaButtonReceiver.handleIntent(mSession, startIntent);

            try {
                if (startIntent.getBooleanExtra("isFromHeadset", false)) {
                    mMediaNotificationManager = new MediaNotificationManager(this);
                    mPlayback = new MediaPlayerAdapter(this, new MediaPlayerListener());
                    if(isHeadsetOn(getApplicationContext())) {
                        earVolLevel = currentState.getInt("earVolLevel", (int) (maxVolume * 0.4f));
                        if ( startup && earVolLevel > (maxVolume * 0.4f)) {
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, percentVol, 0);
                        } else {
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, earVolLevel, 0);
                        }
                    }else {
                        volLevel = currentState.getInt("volLevel", (int) (maxVolume * 0.5f));
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volLevel, 0);
                    }

                    mSession.getController().getTransportControls().play();

                    Log.d("HeadsetReceiver", "onStartCommand: Started Safely");


                }
                stopService(new Intent(getBaseContext(), HeadsetTriggerService.class));


            }catch (Throwable e) {

                e.printStackTrace();
                Log.e("ExceptionOnReciever", "onStartCommand:Restarting Sona Heart Engine "+e );
                mMediaBrowserHelper = new SonaHeartService.MediaBrowserConnection(ApplicationContextProvider.getContext());
                mMediaBrowserHelper.onStart();
                mSession.getController().getTransportControls().play();
        }

        if(autoResume) {
            headset=true;
            IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
            registerReceiver(headSetPlugRecieversX, intentFilter);
            registerBluetoothReceiver();

            Log.e("HeadsetReceiver", "onStart:Creating Headset Reciever" );

        }

        if (isPowerEnabled || isAutoSleep) {
            registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
        IntentFilter stopIntent = new IntentFilter("stopMediaService");
        registerReceiver(mStopMediaService, stopIntent);

        return super.onStartCommand(startIntent, flags, startId);
        //return START_STICKY;
    }

    private BroadcastReceiver headSetPlugRecieversX=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state=intent.getIntExtra("state",-1);
                if (Objects.equals(intent.getAction(), Intent.ACTION_HEADSET_PLUG)) {

                    if(headset){
                        headset=false;
                    }else if (state == 1 && isServiceRunning) {
                        mSession.getController().getTransportControls().play();
                        Log.e("HeadsetCommand", "onReceive:HeadsetAutoPlay MediaPlay Invoked");

                }
            }
        }
        };

    private void registerBluetoothReceiver(){

        IntentFilter filterBluetooth = new IntentFilter();
        filterBluetooth.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filterBluetooth.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBluetoothReceiver, filterBluetooth);
    }
    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action!=null) {
                switch (action) {
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                       Vibration.Companion.vibrate(100);
                        mSession.getController().getTransportControls().play();
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        mSession.getController().getTransportControls().pause();
                        break;
                }
            }else {

                Log.e("BluetoothReceiverNull", "onReceive: NullActionReceived");
            }
        }
    };
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        MusicLibrary.clearLibrary();
        Log.e("SonaHeartService", "onTaskRemoved:ActivityDestroyed " );

        // stopSelf();
    }


    @Override
    public void onDestroy() {
        startup = true;
        if (task != null) {
            task.cancel(true);
            task = null;
        }
       if(wakeLock!=null && wakeLock.isHeld())
            wakeLock.release();

        isServiceRunning=false;
        mSession.getController().getTransportControls().stop();
        mMediaNotificationManager.onDestroy();
        mSession.release();
        super.onDestroy();
        Log.d(TAG, "onDestroy: MediaPlayerAdapter stopped, and MediaSession released");
    }

    @Override
    public void metaUpdateEvent(MediaMetadataCompat mediaMetadataCompat){

        mPreparedMedia=mediaMetadataCompat;
        mSession.setMetadata(mPreparedMedia);
        mPlayback.setCurrentMedia(mPreparedMedia);

        if(mPreparedMedia.getString("VIDEO").equals("true")&& MainActivity.isActivityRunning)
            return;

            updateNotificationState(mSession.getController().getPlaybackState());
       // Toast.makeText(getApplicationContext(),"MetaUpdateCallbackReceived",Toast.LENGTH_LONG).show();
    }




    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                 int clientUid,
                                 Bundle rootHints) {
        return new BrowserRoot(MusicLibrary.getRoot(), null);
    }

    @Override
    public void onLoadChildren(
            @NonNull final String parentMediaId,
            @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d("OnLoadChildren", "onLoadChildren: Result= ");
        if(!MainActivity.isActivityRunning) {
            scanMedia(false, null, false);
            Log.d("RootScanning", "onAddQueueItem:Invoked ");

        }
        result.sendResult(MusicLibrary.getMediaItems());

    }

    // MediaSession Callback: Transport Controls -> MediaPlayerAdapter
    public class MediaSessionCallback extends MediaSessionCompat.Callback {


        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
           //mPlaylist.add(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mQueueIndex == -1) ? 0 : mQueueIndex;
            playlistSize = mPlaylist.size();
            // mSession.setQueue(mPlaylist);

        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            mPlaylist.remove(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mPlaylist.isEmpty()) ? -1 : mQueueIndex;
            mSession.setQueue(mPlaylist);
        }

        @Override
        public void onPrepare() {
            if ( mPlaylist.isEmpty()) {
                if(!MusicLibrary.masterPlaylist.isEmpty()){
                    mPlaylist=MusicLibrary.masterPlaylist;
                }else {
                    return;
                }
            }
            Log.d("OnPrepareService", "onPrepareMedia:ListSize= "+mPlaylist.size());
            if (task != null) {
                task.cancel(true);
                task = null;
            }
            try {
                if (mQueueIndex < mPlaylist.size() && mQueueIndex != -1) {

                    String mediaId = mPlaylist.get(mQueueIndex).getDescription().getMediaId();
                    mPreparedMedia = MusicLibrary.getMetadata(mediaId);
                    mSession.setMetadata(mPreparedMedia);
                } else {
                    mQueueIndex = 0;
                    startSeek = 0;
                    String mediaId = mPlaylist.get(mQueueIndex).getDescription().getMediaId();
                    mPreparedMedia = MusicLibrary.getMetadata(mediaId);
                    mSession.setMetadata(mPreparedMedia);
                    SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putLong("currentPosition", 0);
                    editor.putInt("currentIndex", 0);
                    editor.commit();

                }

            } catch (Throwable e) {

                Log.d("ExceptionRaised", "onPrepare: " + e);
                e.printStackTrace();
                mQueueIndex = 0;
                startSeek = 0;
                SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putLong("currentPosition", 0);
                editor.putInt("currentIndex", 0);
                editor.commit();
               // onPrepare();

            }

            if (!mSession.isActive()) {
                mSession.setActive(true);

            }

            if (mIsShuffle && mPlaylist.size() > 2)
                startShuffles();
        }


        @Override
        public boolean onMediaButtonEvent(final Intent mediaButtonIntent) {
            Log.d("MediaButtonReciever", "onMediaButtonEvent: "+((KeyEvent)mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)).getKeyCode());
            if (Intent.ACTION_MEDIA_BUTTON.equals(mediaButtonIntent.getAction())) {
                KeyEvent event = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                SharedPreferences currentIndex = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                boolean fullscreen = currentIndex.getBoolean("fullscreen", false);

                mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

                if (fullscreen ) {
                    return true;
                }else if(MainActivity.isActivityRunning&& !MainActivity.videoViewDisabled) {

                    return true;
                }


                if (event != null) {

                    int action = event.getAction();
                    if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT && action == KeyEvent.ACTION_DOWN ) {

                        mSession.getController().getTransportControls().skipToNext();
                    }

                    if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS && action == KeyEvent.ACTION_DOWN) {
                        mSession.getController().getTransportControls().skipToPrevious();
                    }
                    if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD && action == KeyEvent.ACTION_DOWN ) {
                        mSession.getController().getTransportControls().fastForward();

                    }

                    if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_REWIND && action == KeyEvent.ACTION_DOWN) {
                        mSession.getController().getTransportControls().rewind();
                    }

                    if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY) {

                        if(isHeadsetOn(getApplicationContext())) {
                            earVolLevel = currentState.getInt("earVolLevel", (int) (maxVolume * 0.4f));
                            if ( startup && earVolLevel > (maxVolume * 0.4f)) {
                                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, percentVol, 0);
                            } else {
                                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, earVolLevel, 0);
                            }
                        }else {
                            volLevel = currentState.getInt("volLevel", (int) (maxVolume * 0.5f));
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volLevel, 0);
                        }

                        mSession.getController().getTransportControls().play();



                    }

                    if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                        if(isHeadsetOn(getApplicationContext())){
                            earVolLevel=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            SharedPreferences.Editor editor = currentState.edit();
                            editor.putInt("earVolLevel", earVolLevel);
                            editor.commit();
                        }else {
                            volLevel = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            SharedPreferences.Editor editor = currentState.edit();
                            editor.putInt("volLevel", volLevel);
                            editor.commit();
                        }

                            mSession.getController().getTransportControls().pause();

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Log.d("Notification", "runOnPause: UpdateNotificationInvoked");

                                        updateNotificationState(mSession.getController().getPlaybackState());


                                }
                            }, 400);
                    }

                    if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_STOP) {

                        Log.e("MediaStopButton", "onMediaButtonEvent: StopButtonInvoked" );
                        isMediaButtonStopped = true;
                        if(isHeadsetOn(getApplicationContext())){
                            earVolLevel=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            SharedPreferences.Editor editor = currentState.edit();
                            editor.putInt("earVolLevel", earVolLevel);
                            editor.commit();
                        }else {
                            volLevel = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            SharedPreferences.Editor editor = currentState.edit();
                            editor.putInt("volLevel", volLevel);
                            editor.commit();
                        }
                        mSession.getController().getTransportControls().stop();

                    }

                    if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE && event.getKeyCode() != KeyEvent.KEYCODE_MEDIA_PLAY && event.getKeyCode() != KeyEvent.KEYCODE_MEDIA_PAUSE && action == KeyEvent.ACTION_DOWN) {
                        if(mPlayback.isPlaying()){
                            if(isHeadsetOn(getApplicationContext())){
                                earVolLevel=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                SharedPreferences.Editor editor = currentState.edit();
                                editor.putInt("earVolLevel", earVolLevel);
                                editor.commit();
                            }else {
                                volLevel = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                SharedPreferences.Editor editor = currentState.edit();
                                editor.putInt("volLevel", volLevel);
                                editor.commit();
                            }

                            mSession.getController().getTransportControls().pause();

                        }else {

                            if(isHeadsetOn(getApplicationContext())) {
                                earVolLevel = currentState.getInt("earVolLevel", (int) (maxVolume * 0.4f));
                                if ( startup && earVolLevel > (maxVolume * 0.4f)) {
                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, percentVol, 0);
                                } else {
                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, earVolLevel, 0);
                                }
                            }else {
                                volLevel = currentState.getInt("volLevel", (int) (maxVolume * 0.5f));
                                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volLevel, 0);
                            }

                            mSession.getController().getTransportControls().play();

                        }
                        Log.d("MediaPlayPauseInvoked", "onMediaButtonEvent:PlayPauseInvoked! ");
                        //super.onMediaButtonEvent(mediaButtonIntent);
                    }

                    if (action == KeyEvent.ACTION_DOWN && event.getKeyCode() != KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE && event.getKeyCode() != KeyEvent.KEYCODE_MEDIA_NEXT && event.getKeyCode() != KeyEvent.KEYCODE_MEDIA_PREVIOUS && event.getKeyCode() != KeyEvent.KEYCODE_MEDIA_FAST_FORWARD && event.getKeyCode() != KeyEvent.KEYCODE_MEDIA_REWIND && event.getKeyCode() != KeyEvent.KEYCODE_MEDIA_PLAY && event.getKeyCode() != KeyEvent.KEYCODE_MEDIA_PAUSE && event.getKeyCode() != KeyEvent.KEYCODE_MEDIA_STOP) {

                        d++;
                        final ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90);
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);


                        final Handler handler = new Handler();
                        Runnable r = new Runnable() {

                            @Override
                            public void run() {


                                if (d == 1) {


                                    if (mPlayback.isPlaying()) {
                                        if(isHeadsetOn(getApplicationContext())){
                                            earVolLevel=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                            SharedPreferences.Editor editor = currentState.edit();
                                            editor.putInt("earVolLevel", earVolLevel);
                                            editor.commit();
                                        }else {
                                            volLevel = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                            SharedPreferences.Editor editor = currentState.edit();
                                            editor.putInt("volLevel", volLevel);
                                            editor.commit();
                                        }
                                        mSession.getController().getTransportControls().pause();
                                        d = 0;

                                    } else {

                                        if(isHeadsetOn(getApplicationContext())) {
                                            earVolLevel = currentState.getInt("earVolLevel", (int) (maxVolume * 0.4f));
                                            if ( startup && earVolLevel > (maxVolume * 0.4f)) {
                                                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, percentVol, 0);
                                            } else {
                                                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, earVolLevel, 0);
                                            }
                                        }else {
                                            volLevel = currentState.getInt("volLevel", (int) (maxVolume * 0.5f));
                                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volLevel, 0);
                                        }
                                            mSession.getController().getTransportControls().play();

                                        d = 0;
                                    }


                                }


                                handler.removeCallbacksAndMessages(null);
                                toneGenerator.stopTone();
                                toneGenerator.release();


                            }


                        };
                        final Handler zhandler = new Handler();
                        Runnable zr = new Runnable() {

                            @Override
                            public void run() {

                                if (d == 2) {

                                    mSession.getController().getTransportControls().skipToNext();
                                    d = 0;
                                }


                                handler.removeCallbacksAndMessages(null);
                                toneGenerator.stopTone();
                                toneGenerator.release();


                            }


                        };

                        final Handler xhandler = new Handler();
                        Runnable xr = new Runnable() {

                            @Override
                            public void run() {


                                if (d == 3) {

                                    mSession.getController().getTransportControls().skipToPrevious();
                                }
                                if (d > 3) {

                                    mSession.getController().getTransportControls().fastForward();
                                }


                                d = 0;
                                handler.removeCallbacksAndMessages(null);
                                toneGenerator.stopTone();
                                toneGenerator.release();


                            }


                        };


                        if (d == 1) {
                            handler.postDelayed(r, 300);

                        }
                        if (d == 2) {
                            zhandler.postDelayed(zr, 300);

                        }

                        if (d == 3) {
                            xhandler.postDelayed(xr, 300);

                        }


                    }

                }

            }

            return true;


        }

        @Override
        public void onCustomAction(String action, Bundle args){


        }

        @Override
        public void onPlay() {
            isServiceRunning=true;

            if (!isReadyToPlay()) {
                // Nothing to play.
                return;
            }
            if (mPreparedMedia == null) {
                onPrepare();
            }
            mPlayback.playFromMedia(mPreparedMedia);

            if (startup) {

                SharedPreferences currentIndex = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                startSeek = currentIndex.getLong("currentPosition", 0);
                Log.d("LastVideoTime", "onPlay:Got is: "+startSeek);

                if (startSeek < mPlayback.getDuration())
                    onSeekTo(startSeek);
                mPlayback.setNewState();
                startup = false;
                Log.e("StartSeek Invoked", "onPlay: StartSeek:" + startSeek);
            }

            if (task == null) {
                task = new BigComputationTask();
                task.execute();
            }

                if (mIsShuffle && mPlaylist.size() > 2) {
                    nextSongId=mPlaylist.get(shuffleIndex).getDescription().getMediaId();
                }else if(mPlaylist.size()>0) {
                    nextSongId=mPlaylist.get((mQueueIndex + 1) % mPlaylist.size()).getDescription().getMediaId();
                }

            isNextVideo=isVideoFile(MusicLibrary.videoFileName.get(nextSongId));
            mUpdateNextHandler.postDelayed(mUpdateNext, 4000);
            if(masterCrossFade) {
                startCrossFadeHandler.postDelayed(startCrossFadeRunnable, 1000);
            }
            Log.d(TAG, "onPlayFromMediaId: MediaSession active");
        }

        @Override
        public void onPause() {
            if (task != null) {
                task.cancel(true);
                task = null;
            }
            startCrossFadeHandler.removeCallbacks(startCrossFadeRunnable);
            mPlayback.pause(false);
            next = false;
            mUpdateNextHandler.removeCallbacks(mUpdateNext);
        }


        @Override
        public void onStop() {
            Log.e("SonaHeartService", "onStartCommand: Service Stopping..." );
            mPlayback.pause(true);
            isServiceRunning=false;
            mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            if(isHeadsetOn(getApplicationContext())){
                earVolLevel=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putInt("earVolLevel", earVolLevel);
                editor.commit();
            }else {
                volLevel = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putInt("volLevel", volLevel);
                editor.commit();
            }
            if(!MainActivity.isActivityRunning)
            cancelTimer();


            if (task != null) {
                task.cancel(true);
                task = null;
            }

            mUpdateNextHandler.removeCallbacks(mUpdateNext);
            startCrossFadeHandler.removeCallbacks(startCrossFadeRunnable);
            next = false;

            SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putInt("currentIndex", mQueueIndex);
            editor.putBoolean("QueueFlag",isQueueActive);
            editor.commit();

            autoResume=currentState.getBoolean("autoResume",true);
            try {
                if (autoResume) {
                    unregisterReceiver(headSetPlugRecieversX);
                    unregisterReceiver(mBluetoothReceiver);
                }
                if(isPowerEnabled || isAutoSleep)
                unregisterReceiver(mBatInfoReceiver);

                unregisterReceiver(mStopMediaService);
                Log.e("AutoResume", "onStop:Auto ResumeReceiver Destroyed ");
            }catch (Throwable e){
                Log.e("exceptionRaised", "onStop: "+e );
            }

            //autoPlay=currentState.getBoolean("autoPlay",false);
            if(!MainActivity.isActivityRunning && !supressStop ) {
                Intent serviceIntent = new Intent(getApplicationContext(), HeadsetTriggerService.class);
                getApplicationContext().startService(serviceIntent);
                Log.d("HeadSetTriggerService", "onDestroy: HeadsetWatchdogStarted");
            }
            supressStop=false;
            if(wakeLock!=null && wakeLock.isHeld())
                wakeLock.release();
            startup = true;
            mSession.setActive(false);
            mPlayback.stop();
        }

        @Override
        public void onSkipToNext() {
            startCrossFadeHandler.removeCallbacks(startCrossFadeRunnable);
          //  mPlayback.pause();
            Log.d("SkipToNext", "onSkipToNext: Invoked");

            next = false;
            startup = false;

            if (mIsShuffle && mPlaylist.size() > 2 && !isQueueActive) {
                if (songdDeque.isEmpty() || songdDeque.peekLast() != mQueueIndex) {
                    songdDeque.add(mQueueIndex);
                    if (songdDeque.size() >= 50) {
                        songdDeque.removeFirst();
                    }
                }
                mQueueIndex = shuffleIndex;
            }else {
                try {
                    mQueueIndex = (++mQueueIndex % mPlaylist.size());
                } catch (Throwable e) {
                    System.exit(0);
                }
            }


            if (task != null) {
                task.cancel(true);
                task = null;
            }
            mPreparedMedia = null;
            onPlay();
            SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putInt("currentIndex", mQueueIndex);
            editor.apply();
        }

        @Override
        public void onSkipToPrevious() {
            startCrossFadeHandler.removeCallbacks(startCrossFadeRunnable);
           // mPlayback.pause();
            next = false;
            startup = false;

            if (task != null) {
                task.cancel(true);
                task = null;
            }
            if (mIsShuffle && mPlaylist.size() > 2 && !isQueueActive) {

                if (!songdDeque.isEmpty()) {

                    mQueueIndex = songdDeque.pollLast();
                    Log.d("Song Deque", "Removed last element:" + mQueueIndex);
                    Log.d("SongDeque", "TotalSize: " + songdDeque.size());


                } else
                    mQueueIndex = shuffleIndex;

            } else {
                mQueueIndex = mQueueIndex > 0 ? mQueueIndex - 1 : mPlaylist.size() - 1;
            }

            mPreparedMedia = null;
            onPlay();
            SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putInt("currentIndex", mQueueIndex);
            editor.commit();
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
        }

        @Override
        public void onFastForward() {

            mPlayback.seekTo(mPlayback.getPosition() + 30000);
        }

        @Override
        public void onRewind() {
            mPlayback.seekTo(mPlayback.getPosition() - 30000);
        }


        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {

            if (mediaId.equals(mPreparedMedia.getDescription().getMediaId())) {
                return;
            }

            startCrossFadeHandler.removeCallbacks(startCrossFadeRunnable);
            //mPlayback.pause();
            next = false;
            startup = false;

            if (task != null) {
                task.cancel(true);
                task = null;
            }

            if (mIsShuffle) {
                if (songdDeque.isEmpty() || songdDeque.peekLast() != mQueueIndex) {
                    songdDeque.add(mQueueIndex);

                    Log.d("Song Deque", "Added new element:" + mQueueIndex);
                    Log.d("SongDeque", "TotalSize: " + songdDeque.size());


                    if (songdDeque.size() >= 50) {

                        Log.d("Deque Remove Invoked", "Removed Item Is " + songdDeque.peekFirst());
                        songdDeque.removeFirst();
                        Log.d("SongDeque", "Again Total Size: " + songdDeque.size());


                    }
                }
            }


            int searchListLength = mPlaylist.size();
            for (int i = 0; i < searchListLength; i++) {

                try {
                    if (Objects.requireNonNull(mPlaylist.get(i).getDescription().getMediaId()).equals(mediaId)) {

                        Log.d("onPlayFromMediaId:", "searchMediaId: " + mediaId);
                        Log.d("onPlayFromMediaId:", "ActualMediaId: " + mPlaylist.get(i).getDescription().getMediaId());
                        Log.d("onPlayFromMediaId:", "searchMediaIdPosition: " + i);

                        mQueueIndex = i;
                        mPreparedMedia = null;
                        onPlay();
                        SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = currentState.edit();
                        editor.putInt("currentIndex", mQueueIndex);
                        editor.commit();


                        break;
                    }

                } catch (Throwable e) {

                    Log.e("Exception Raised", "onPlayFromMediaId: " + e);
                }

            }

        }


        void onPlayFromTrackId(int trackId) {
            if (trackId==mQueueIndex)
                return;

            if(trackId<playlistSize && trackId>=0) {

                startCrossFadeHandler.postDelayed(startCrossFadeRunnable, 1000);
                startup = false;

                if (task != null) {
                    task.cancel(true);
                    task = null;
                }

                if (mIsShuffle) {
                    if (songdDeque.isEmpty() || songdDeque.peekLast() != mQueueIndex) {
                        songdDeque.add(mQueueIndex);

                        Log.d("Song Deque", "Added new element:" + mQueueIndex);
                        Log.d("SongDeque", "TotalSize: " + songdDeque.size());

                        if (songdDeque.size() >= 50) {

                            Log.d("Deque Remove Invoked", "Removed Item Is " + songdDeque.peekFirst());
                            songdDeque.removeFirst();
                            Log.d("SongDeque", "Again Total Size: " + songdDeque.size());


                        }
                    }
                }


                mQueueIndex = trackId;
                mPreparedMedia = null;
                onPlay();
                SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putInt("currentIndex", mQueueIndex);
                editor.commit();
            }


        }


        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);

            Log.d("OnCommandMusicService", "onCommand:Invoked " + command);
            switch (command) {

                case "oreoStyle":

                    SharedPreferences OreoNotification = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    boolean oreoStyle = OreoNotification.getBoolean("oreoStyle", false);

                    if (oreoStyle) {
                        OreoNotification = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = OreoNotification.edit();
                        editor.putBoolean("oreoStyle", false);
                        editor.commit();
                    } else {
                        OreoNotification = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = OreoNotification.edit();
                        editor.putBoolean("oreoStyle", true);
                        editor.commit();

                    }
                    updateNotificationState(mSession.getController().getPlaybackState());
                    break;


                case "ListMeta":

                    Bundle bundles = new Bundle();
                    bundles.putString("ListMeta", getListMeta());
                    cb.send(2, bundles);
                    mPlayback.setNewState();
                    break;

                case "shuffle":
                    if (mPlaylist.size() > 2)
                        startShuffles();
                    mIsShuffle = extras.getBoolean("shuffle");

                    break;

                case "nextSong":
                    Bundle bundlep = new Bundle();
                    if (mIsShuffle && mPlaylist.size() > 2 && !isQueueActive) {
                        try {
                            bundlep.putString("nextSong", (Objects.requireNonNull(mPlaylist.get(shuffleIndex).getDescription().getTitle()).toString() + " - " + (mPlaylist.get(shuffleIndex).getDescription().getDescription())));
                        } catch (Exception e) {
                            bundlep.putString("nextSong","N/A");
                            e.printStackTrace();
                        }
                        nextSongId=mPlaylist.get(shuffleIndex).getDescription().getMediaId();
                    }else if(mPlaylist.size()>0) {
                        try {
                            bundlep.putString("nextSong", (Objects.requireNonNull(mPlaylist.get((mQueueIndex + 1) % mPlaylist.size()).getDescription().getTitle()).toString() + " - " + (mPlaylist.get((mQueueIndex + 1) % mPlaylist.size()).getDescription().getDescription())));
                        } catch (Exception e) {
                            bundlep.putString("nextSong","N/A");
                            e.printStackTrace();
                        }
                        nextSongId=mPlaylist.get((mQueueIndex + 1) % mPlaylist.size()).getDescription().getMediaId();
                    }
                    cb.send(5, bundlep);
                    if(masterCrossFade &&  MainActivity.videoViewDisabled) {
                        isNextVideo = isVideoFile(MusicLibrary.videoFileName.get(nextSongId));
                        startCrossFadeHandler.postDelayed(startCrossFadeRunnable, 1000);
                    }
                    break;
                case "repeat":
                    mIsRepeat = extras.getInt("repeat");
                    if(masterCrossFade) {
                        if (mIsRepeat < 2) {
                            mPlayback.setCompletionListener(true);
                        } else {
                            mPlayback.setCompletionListener(false);
                            startCrossFadeHandler.postDelayed(startCrossFadeRunnable, 1000);
                        }
                    }else {
                        mPlayback.setCompletionListener(true);

                    }
                    break;

                case "SleepTimer":
                    if (timerSync != 0) {
                        cancelTimer();
                    }
                    startTimer(extras.getLong("time"));
                    break;
                case "SleepStop":
                    cancelTimer();
                    break;
                case "SleepSync":
                    Bundle bundlez = new Bundle();
                    bundlez.putLong("sleepVal", timerSync);
                    cb.send(3, bundlez);
                    break;
                case "Equalizer":

                    try {

                        cb.send(4, mPlayback.onEqCommand("Equalizer", null));
                    } catch (Throwable e) {

                        Log.d("ExceptionRaised", "onCommand:Service Equalizer");
                    }
                    break;
                case "Equalizerx":
                    Bundle bundlek = new Bundle();
                    bundlek.putInt("EqIndex", extras.getInt("EqIndex"));
                    mPlayback.onEqCommand("Equalizerx", bundlek);

                    break;

                case "Loudness":
                    Bundle bundleL = new Bundle();
                    bundleL.putInt("LoudValue", extras.getInt("LoudValue"));
                    mPlayback.onEqCommand("Loudness", bundleL);

                    break;

                case "BassBoost":
                    Bundle bundleB = new Bundle();
                    bundleB.putShort("BassValue", extras.getShort("BassValue"));
                    mPlayback.onEqCommand("BassBoost", bundleB);

                    break;

                case "Virtualizer":
                    Bundle bundleV = new Bundle();
                    bundleV.putShort("VirtualValue", extras.getShort("VirtualValue"));
                    mPlayback.onEqCommand("Virtualizer", bundleV);
                    break;

                case "TrableBoost":

                    Bundle bundleT = new Bundle();
                    bundleT.putShort("TrableValue", extras.getShort("TrableValue"));
                    mPlayback.onEqCommand("TrableBoost", bundleT);

                    break;


                case "scanMedia":
                    boolean rename;
                    String renameId;
                    boolean supressSeek=false;
                    try {
                      rename= extras.getBoolean("rename", false);
                       renameId = extras.getString("renameId", null);
                       supressSeek=extras.getBoolean("supressSeek",false);
                    }catch (Throwable e){

                        rename=false;
                        renameId=null;
                    }
                    scanMedia(rename,renameId,supressSeek);
                    Bundle bundleSM = new Bundle();
                    bundleSM.putString("videoCount", MusicLibrary.getVideoCount());
                    bundleSM.putString("trackCount", MusicLibrary.getTrackCount());
                    bundleSM.putString("audioCount", MusicLibrary.getAudioCount());
                    bundleSM.putString("ListMeta", getListMeta());
                    if (mPlaylist.size() > 2)
                        startShuffles();
                    cb.send(7, bundleSM);

                    break;

                case "scanVideoMedia":

                    scanMedia(false,null,false);
                    Bundle bundleSVM = new Bundle();
                    bundleSVM.putString("videoCount", MusicLibrary.getVideoCount());
                    bundleSVM.putString("trackCount", MusicLibrary.getTrackCount());
                    bundleSVM.putString("audioCount", MusicLibrary.getAudioCount());
                    bundleSVM.putString("ListMeta", getListMeta());
                    if (mPlaylist.size() > 2)
                        startShuffles();
                    cb.send(6, bundleSVM);
                    break;

                case "refreshMedia":

                    scanMedia(false,null,false);
                    if (mPlaylist.size() > 2)
                        startShuffles();
                    Bundle bundleRM = new Bundle();
                    bundleRM.putString("trackCount", MusicLibrary.getTrackCount());
                    cb.send(8, bundleRM);

                    break;

                case "channelBalance":

                    Bundle bundleCB = new Bundle();
                    bundleCB.putFloat("left", extras.getFloat("left"));
                    bundleCB.putFloat("right", extras.getFloat("right"));
                    mPlayback.onEqCommand("channelBalance", bundleCB);

                    break;

                case "Tempo":

                    Bundle bundleTMP = new Bundle();
                    bundleTMP.putFloat("tempo", extras.getFloat("tempo"));
                    mPlayback.onEqCommand("Tempo", bundleTMP);

                    break;

                case "Reverb":
                    Bundle bundleRV = new Bundle();
                    bundleRV.putShort("reverb", extras.getShort("reverb"));
                    mPlayback.onEqCommand("Reverb", bundleRV);
                    break;

                case "TempoX":
                    mPlayback.onEqCommand("TempoX", null);

                    break;
                case "scanAlbums":


                    Bundle bundleART = new Bundle();
                    bundleART.putInt("albumCount", scanAlbumArt());
                    cb.send(9, bundleART);

                    break;
                case "clearAlbums":
                    MusicLibrary.clearAlbumList();
                    break;

                case "CrossFade":
                    masterCrossFade=extras.getBoolean("crossFading");
                    Bundle bundleC = new Bundle();
                    bundleC.putBoolean("crossFading", masterCrossFade);
                    mPlayback.onEqCommand("CrossFade", bundleC);
                    if(!(mPreparedMedia.getString("VIDEO").equals("true")))
                    startCrossFadeHandler.postDelayed(startCrossFadeRunnable, 1000);
                    break;

                case "FadingInOut":

                    Bundle bundleF = new Bundle();
                    bundleF.putBoolean("Fading",extras.getBoolean("Fading") );
                    mPlayback.onEqCommand("FadingInOut", bundleF);
                    break;

                case "playTrackId":

                    int trackID=extras.getInt("trackId");
                    onPlayFromTrackId(trackID);
                    break;

                case "playTrackInfo":

                    int trackIDX=extras.getInt("trackId");

                    if(trackIDX<playlistSize && trackIDX>=0){
                        Bundle bundleTX = new Bundle();
                        try {
                            bundleTX.putString("Title", Objects.requireNonNull(mPlaylist.get(trackIDX).getDescription().getTitle()).toString());
                            bundleTX.putString("FileName",(Objects.requireNonNull(mPlaylist.get(trackIDX).getDescription().getDescription()).toString()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        cb.send(10, bundleTX);
                    }else {
                        Bundle bundleTX = new Bundle();
                        bundleTX.putString("Title", "Track Not Available!");
                        bundleTX.putString("FileName","Track Not Available!");
                        cb.send(10, bundleTX);
                    }
                    break;
                case "AutoResume":
                    autoResume=extras.getBoolean("autoResume");
                    if(autoResume){
                        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
                        registerReceiver(headSetPlugRecieversX, intentFilter);
                        registerBluetoothReceiver();
                        Log.e("HeadsetReceiver", "onCommand:Creating Headset Reciever" );
                    }else
                    {
                        try {
                            unregisterReceiver(headSetPlugRecieversX);
                            unregisterReceiver(mBluetoothReceiver);
                        }catch (Throwable e){
                            Log.d("ExceptionRaised", "onCommand: UnregisterReciever"+e);
                        }
                    }
                    break;

                case "CrossfadeTime":
                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    mCrossfadeDuration=currentState.getInt("crossfadeTime",15000);
                    Bundle bundleCT = new Bundle();
                    bundleCT.putInt("crossfadeTime", mCrossfadeDuration);
                    mPlayback.onEqCommand("CrossfadeTime", bundleCT);
                    break;

                case "ManualFadeTime":
                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                   int  manualFadeTime=currentState.getInt("manualFadeTime",500);
                    Bundle bundleMT = new Bundle();
                    bundleMT.putInt("manualFadeTime", manualFadeTime);
                    mPlayback.onEqCommand("ManualFadeTime", bundleMT);
                    break;

                case "FadeTime":
                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                   int fadeTime=currentState.getInt("fadeTime",1000);
                    Bundle bundleFT = new Bundle();
                    bundleFT.putInt("fadeTime",fadeTime);
                    mPlayback.onEqCommand("FadeTime", bundleFT);
                    break;
                    case "MainPaused":
                        startCrossFadeHandler.postDelayed(startCrossFadeRunnable, 0);
                        break;

                case "PowerSaver":
                    isPowerEnabled=currentState.getBoolean("powerFlag",true);
                    criticlBattery=currentState.getInt("batteryLevel",20);
                    isAutoSleep=currentState.getBoolean("isAutoSleep",false);
                    autoSleepBattery=currentState.getInt("sleepBatteryLevel",30);
                    Log.e("CriticalBatteryLevel", "onCommand:Value= "+criticlBattery);

                    try {
                        if (isPowerEnabled || isAutoSleep) {

                            registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                        } else {
                            unregisterReceiver(mBatInfoReceiver);
                        }
                    }catch (Throwable e){

                        e.printStackTrace();
                    }

                    break;

                case "WakeLock":
                    isWakeLockEnabled=extras.getBoolean("playerWakeLock");
                    if(isWakeLockEnabled){
                        PowerManager mgr = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                        if (mgr != null) {
                            wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SonaHeartService:SonaHeartLock");
                            wakeLock.acquire();
                        }

                    }else {
                        if( wakeLock!=null && wakeLock.isHeld())
                            wakeLock.release();
                    }
                    break;

                case "AlterEqMode":
                    Bundle bundleAE = new Bundle();
                    bundleAE.putBoolean("alterEqMode", extras.getBoolean("alterEqMode"));
                    mPlayback.onEqCommand("AlterEqMode", bundleAE);
                    break;

                case "QueueToggle":
                    boolean queueT=extras.getBoolean("QueueSwitch");
                    if(queueT && !isQueueActive)
                    {
                        mPlaylist=MusicLibrary.queueData;
                        playlistSize=mPlaylist.size();
                        mQueueIndex=-1;
                        mMediaBrowserHelper.getMediaController().getTransportControls().skipToNext();
                        Log.d("QueueActivated", "onCommand:Size: "+mPlaylist.size());
                    }else if(!queueT) {

                        mPlaylist=MusicLibrary.masterPlaylist;
                        playlistSize=mPlaylist.size();
                        Log.d("QueueDeactivated", "onCommand: Size: "+mPlaylist.size());
                    }else{
                        mPlaylist=MusicLibrary.queueData;
                        playlistSize=mPlaylist.size();
                    }
                    isQueueActive=queueT;
                    SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putBoolean("QueueFlag",isQueueActive);
                    editor.commit();

                default:

            }

        }

        private boolean isReadyToPlay() {
            return (!mPlaylist.isEmpty());
        }
    }

    // MediaPlayerAdapter Callback: MediaPlayerAdapter state -> SonaHeartService.
    public class MediaPlayerListener extends PlaybackInfoListener {

        private final ServiceManager mServiceManager;

        MediaPlayerListener() {
            mServiceManager = new ServiceManager();
        }

        @SuppressLint("SwitchIntDef")
        @Override
        public void onPlaybackStateChange(final PlaybackStateCompat state) {
            // Report the state to the MediaSession.
            try {
                mSession.setPlaybackState(state);
            } catch (Throwable e) {
                Log.e("ExceptionRaised!", "onPlaybackStateChange: " + e);
            }

            // Manage the started state of this service.
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mServiceManager.moveServiceToStartedState(state);
                    if(masterCrossFade) {
                        startCrossFadeHandler.postDelayed(startCrossFadeRunnable, 1000);
                    }
                    if (task == null) {
                        task = new BigComputationTask();
                        task.execute();
                    }

                    break;
                case PlaybackStateCompat.STATE_PAUSED:

                    mServiceManager.updateNotificationForPause(state);
                    startCrossFadeHandler.removeCallbacks(startCrossFadeRunnable);

                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    startCrossFadeHandler.removeCallbacks(startCrossFadeRunnable);
                    mServiceManager.moveServiceOutOfStartedState(state);
                    if (task != null) {
                        task.cancel(true);
                        task = null;
                    }
                    break;
            }


        }

        @Override
        public void onCompletion() {

            startCrossFadeHandler.removeCallbacks(startCrossFadeRunnable);
            if (mIsRepeat == 2) {
                mSession.getController().getTransportControls().skipToNext();
            } else if (mIsRepeat == 1) {

                mSession.getController().getTransportControls().prepare();
                mSession.getController().getTransportControls().play();

            } else if (mIsRepeat == 0) {

                mPreparedMedia = null;

            }

        }


        class ServiceManager {
            private void moveServiceToStartedState(PlaybackStateCompat state) {
                Notification notification =
                        mMediaNotificationManager.getNotification(
                                mPlayback.getCurrentMedia(), state, getSessionToken());

                if (!mServiceInStartedState) {
                    ContextCompat.startForegroundService(
                            SonaHeartService.this,
                            new Intent(SonaHeartService.this, SonaHeartService.class));
                    mServiceInStartedState = true;
                }

                notification.priority = Notification.PRIORITY_MAX;
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                notification.visibility = Notification.VISIBILITY_PUBLIC;
                notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
                notification.flags = Notification.FLAG_NO_CLEAR;
                startForeground(MediaNotificationManager.NOTIFICATION_ID, notification);

            }

            private void updateNotificationForPause(PlaybackStateCompat state) {
                // stopForeground(false);

                Notification notification =
                        mMediaNotificationManager.getNotification(
                                mPlayback.getCurrentMedia(), state, getSessionToken());

                if(notification!=null)
                mMediaNotificationManager.getNotificationManager().notify(MediaNotificationManager.NOTIFICATION_ID, notification);


            }

            private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
                Log.d("Destroying Notification", "moveServiceOutOfStartedState: "+state.toString());
                mMediaNotificationManager.onDestroy();
                stopForeground(true);
                stopSelf();
                mServiceInStartedState = false;
            }


        }

    }


    public PlayerAdapter getmPlayback() {

        return mPlayback;
    }


    public String getListMeta() {

        //  Log.d("ListMeta", "getListMeta: "+String.valueOf(mQueueIndex));
        return (mQueueIndex + 1) + "/" + playlistSize;


    }


    public String getNextSong() {

        if (next) {

            if (mIsShuffle && mPlaylist.size() > 2) {
                //Log.d("getNextSong", "next= " + next);
                try {

                    return "Next: " + Objects.requireNonNull(mPlaylist.get(shuffleIndex).getDescription().getTitle()).toString() + " - " + (mPlaylist.get(shuffleIndex).getDescription().getDescription());

                } catch (Throwable e) {


                    return "";
                }
            } else {
                //Log.d("getNextSong", "next= " + next);

                try {
                    return "Next: " + Objects.requireNonNull(mPlaylist.get((mQueueIndex + 1) % mPlaylist.size()).getDescription().getTitle()).toString() + " - " + (mPlaylist.get((mQueueIndex + 1) % mPlaylist.size()).getDescription().getDescription());
                } catch (Throwable e) {

                    return "";
                }

            }
        } else {


            try {

                return (Objects.requireNonNull(mSession.getController().getMetadata().getDescription().getSubtitle()).toString() + " - " + mSession.getController().getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST));


            } catch (Throwable e) {

                return "Loading...";
            }
        }


    }


    private void updateNotificationState(PlaybackStateCompat state) {

        try {
            Notification notification =
                    mMediaNotificationManager.getNotification(
                            mPlayback.getCurrentMedia(), state, getSessionToken());
            mMediaNotificationManager.getNotificationManager()
                    .notify(MediaNotificationManager.NOTIFICATION_ID, notification);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    private class MediaBrowserConnection extends MediaBrowserHelper {
        private MediaBrowserConnection(Context context) {
            super(context, SonaHeartService.class);
            Log.d("BrowserConnect", "MediaBrowserConnection: Invoked");
        }

        @Override
        protected void onConnected(@NonNull MediaControllerCompat mediaController) {
            Log.d("BrowserConnect", "onConnected:Invoked ");
        }

        @Override
        protected void onChildrenLoaded(@NonNull String parentId,
                                        @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);

            Log.d("BrowserConnection", "onChildrenLoaded:Invoked ");
            MediaControllerCompat controller = getMediaController();
           /* for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                controller.addQueueItem(mediaItem.getDescription());
            }*/
            controller.getTransportControls().prepare();
            controller.getTransportControls().play();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class BigComputationTask extends AsyncTask<Void, Void, Void> {
       /* private WeakReference<SonaHeartService> activityReference;

        // only retain a weak reference to the activity
        BigComputationTask(SonaHeartService context) {
            activityReference = new WeakReference<>(context);
        }*/

        @Override
        protected void onPreExecute() {
            // Runs on UI thread
            Log.d(TAG, "About to start...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                // Runs on the background thread
                while (!task.isCancelled() || mPlayback.isPlaying()) {
                    // Sets the progress indicator completion percentage

                    // Log.d("AsyncTask Running", "doInBackground: Notification Updated ");

                    updateNotificationState(mSession.getController().getPlaybackState());
                    Thread.sleep(1000);

                }


            } catch (Throwable e) {

                Log.e("Exception in AsyncTask", "doInBackground: AsyncTask Sleep Error:" + e);
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void res) {
            // Runs on the UI thread

            Log.d(TAG, "Big computation finished");

        }
    }


    //start timer function
    void startTimer(long time) {
        if(!isWakeLockEnabled || !wakeLock.isHeld()) {
            PowerManager mgr = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            if (mgr != null) {
                wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TimerWakeLock:SonaHeartLock");
                wakeLock.acquire();
            }

        }
        cTimer = new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {

                timerSync = millisUntilFinished;
            }

            public void onFinish() {
                if(isActivityDestroyed){
                    Calendar calendar = Calendar.getInstance();
                    Date date = new Date();
                    date.setTime(System.currentTimeMillis() + (300 * 60 * 1000));
                    calendar.setTime(date);

                    Intent intent = new Intent(SonaHeartService.this, HeadsetTriggerService.class);
                    PendingIntent pintent = PendingIntent.getService(SonaHeartService.this, 0, intent,  0);
                    AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    if (alarm != null) {
                        alarm.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), pintent);
                    }
                }

                timerSync = 0;
                mSession.getController().getTransportControls().stop();
                stopService(new Intent(SonaHeartService.this, SonaHeartService.class));
                System.exit(0);

            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {

        if (cTimer != null) {
            timerSync = 0;
            cTimer.cancel();
            cTimer=null;
            if(!isWakeLockEnabled && wakeLock!=null && wakeLock.isHeld())
                wakeLock.release();
        }

    }


    Runnable mUpdateNext = new Runnable() {
        @Override
        public void run() {
            mUpdateNextHandler.removeCallbacksAndMessages(null);
            next = !next;

            //Log.d("NextSongHandler", "run: next="+next);
            mUpdateNextHandler.postDelayed(this, 4000);
        }
    };


    public void scanMedia(boolean rename,String renameId,boolean supressSeek) {
        try {
            Log.e("renameID", "onScanMedia: ID:"+renameId );
            String currentId=null;
            long currentPos=mPlayback.getPosition();
            if(mPreparedMedia!=null)
                currentId = mPreparedMedia.getDescription().getMediaId();

        mPlaylist.clear();
        MusicLibrary.refreshMetadata();
            Log.d("CurrentQueueStatus", "scanMedia: "+isQueueActive);
            Log.d("CurrentQueueSize", "scanMedia: "+MusicLibrary.queueItems.size());
        if(isQueueActive && !MusicLibrary.queueData.isEmpty()){
            mPlaylist=MusicLibrary.queueData;
        }else if(isQueueActive) {
            isQueueActive=false;
            SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putBoolean("QueueFlag",isQueueActive);
            editor.commit();
            mPlaylist =MusicLibrary.masterPlaylist;
        }else {
            mPlaylist =MusicLibrary.masterPlaylist;

        }
        playlistSize = mPlaylist.size();
        boolean isMediaFound=false;

        if(!rename && currentId!=null) {
            for (int i = 0; i < playlistSize; i++) {

                if (Objects.equals(mPlaylist.get(i).getDescription().getMediaId(), currentId)) {
                    isMediaFound = true;
                    break;
                }
            }
            if (!isMediaFound) {

                mMediaBrowserHelper.getMediaController().getTransportControls().skipToNext();
            }

            Log.e("Entered In deletion", "scanMedia:Deletion Invoked " );
        }else if(rename) {
            mMediaBrowserHelper.getMediaController().getTransportControls().playFromMediaId(renameId,null);
            if(!supressSeek)
            mMediaBrowserHelper.getMediaController().getTransportControls().seekTo(currentPos);
            Log.e("Entered In rename", "scanMedia:Rename Invoked " );

        }

            }catch (Throwable e) {
            e.printStackTrace();
            Log.e("Exception Raised", "onPlayFromMediaId: " + e);

        }

        Log.d("scanMedia", "scanMedia:Successfully Invoked ");
    }

    public int scanAlbumArt() {
        int albumListSize = MusicLibrary.refresAlbums();
        Log.d("AlbumArtScanned", "scanAlbumArt: Size" + albumListSize);
        return albumListSize;
    }


    private boolean isHeadsetOn(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (am == null)
            return false;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return am.isWiredHeadsetOn() || am.isBluetoothScoOn() || am.isBluetoothA2dpOn();
        } else {
            AudioDeviceInfo[] devices = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

            for (AudioDeviceInfo device : devices) {
                if (device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        || device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        || device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                    return true;
                }
            }
        }
        return false;
    }



    public Runnable startCrossFadeRunnable = new Runnable() {

        @Override
        public void run() {
            startCrossFadeHandler.removeCallbacksAndMessages(startCrossFadeRunnable);
            //Check if we're in the last part of the current song.
                  // Log.d("CrossFadeHandler", "run: Entered Just And Checking" );
            try {
                if( isNextVideo && MainActivity.isActivityRunning || !MainActivity.videoViewDisabled || !masterCrossFade){

                    Log.d("CrossFadeHandler", "run: Entered Just And Returned" );
                    Log.d("isNextVideo", "run: isNextVideo= "+isNextVideo);
                    Log.d("isActivityRunning", "run: isActivityRunning= "+MainActivity.isActivityRunning);
                    Log.d("isMasterCrossfade", "run: isMasterCrossfade= "+masterCrossFade);

                    if(isNextVideo&&MainActivity.isActivityRunning){

                        mPlayback.setCompletionListener(true);
                    }
                        startCrossFadeHandler.removeCallbacks(startCrossFadeRunnable);

                }else {
                  // Log.d("CrossFadeHandler", "run: Entered Successfully in Else Phase" );
                   // Log.d("NextVideoVal", "run:isNextVideo: "+isNextVideo+"  isActivityRunning: "+MainActivity.isActivityRunning);
                       if (mIsRepeat>1 ) {
                       // Log.d("crossMediaCheck", "run: CrossMediaStatus: isPlaying"+mPlayback.isPlaying());
                        int currentTrackDuration = mPlayback.getDuration();
                       // Log.d("CrossFadeHandler", "run: Entered In Third Phase And Checking Time: "+mPlayback.getPosition() );
                        int currentTrackFadePosition = currentTrackDuration - (mCrossfadeDuration);
                        if (mPlayback.getPosition() >= currentTrackFadePosition) {
                          //  Log.d("CrossFadeHandler", "run: Invoked Successfully Completed" );
                            MediaPlayerAdapter.isCrossfade = true;
                            //Launch the next runnable that will handle the cross fade effect.
                                mSession.getController().getTransportControls().skipToNext();
                            startCrossFadeHandler.removeCallbacks(startCrossFadeRunnable);
                           // Log.e("OnStartCrossFade", "onStartCrossFadeRunnableInvoked:EndInvoked ");


                        } else {
                            startCrossFadeHandler.postDelayed(startCrossFadeRunnable, 1000);
                        }

                    } else {
                        startCrossFadeHandler.removeCallbacks(startCrossFadeRunnable);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };


    private static boolean isVideoFile(String path) {

        try {
            // Uri vedioUri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI + "/" + path);
            // String pathx = getRealPathFromURI(vedioUri);
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("video");
        }catch (Throwable e){
            Log.e("ExceptionInIsVideo", "isVideoFile: "+e );
            return false;
        }

    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            //this will give you battery current status

            try{
                int level = intent.getIntExtra("level", 0);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                if(isAutoSleep && level<=autoSleepBattery && status != BatteryManager.BATTERY_STATUS_CHARGING) {
                    Log.d("SleepModeLoop1", "onReceive: FirstLoopInvoked");

                    if(cTimer==null && wakeLock!=null && wakeLock.isHeld() && !mPlayback.isPlaying()) {
                        wakeLock.release();
                    }

                }else if(isAutoSleep && isWakeLockEnabled && level>autoSleepBattery && !wakeLock.isHeld()){

                    PowerManager mgr = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                    if (mgr != null) {
                        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AutoSleep:SonaHeartLock");
                        wakeLock.acquire();
                    }

                }

                if(firstPower && isPowerEnabled){

                    Log.d("FirstPower", "onReceive:Invoked ");
                    if(level<=criticlBattery && status != BatteryManager.BATTERY_STATUS_CHARGING){
                        //unregisterReceiver(mBatInfoReceiver);
                        Log.d("ReturnedPlug", "onReceive: Invoked");
                        return;

                    }else {

                        firstPower = false;
                    }
                }


                if(isPowerEnabled && level==criticlBattery+1 && status != BatteryManager.BATTERY_STATUS_CHARGING){

                    Toast.makeText(getApplicationContext(),"WARNING: Low Battery Sona Player Will be Terminated Soon!",Toast.LENGTH_LONG).show();
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {0, 300, 100, 300, 100, 300, 100};
                    if (v != null) {
                        v.vibrate(pattern, -1);
                    }

                    if(mPlayback.isPlaying()) {
                        mSession.getController().getTransportControls().pause();
                        textToSpeechSystem = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    // replace this Locale with whatever you want
                                    textToSpeechSystem.setLanguage(Locale.US);
                                    textToSpeechSystem.speak("WARNING: Low Battery Sona Player Will be Terminated Soon!", TextToSpeech.QUEUE_FLUSH, null, null);
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            mSession.getController().getTransportControls().play();
                                        }
                                    }, 2200);

                                }
                            }
                        });
                    }
                }

                if(isPowerEnabled && level<=criticlBattery && status != BatteryManager.BATTERY_STATUS_CHARGING){

                    Log.d("BatterySignalLocked", "onReceive:BatteryLevel: "+level);
                    Toast.makeText(getApplicationContext(),"BATTERY LOW: Sona Player Terminated!",Toast.LENGTH_LONG).show();
                    Intent intentX = new Intent(SonaHeartService.this, HeadsetTriggerService.class);
                    PendingIntent pintent = PendingIntent.getService(SonaHeartService.this, 0, intentX,  0);
                    AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    if (alarm != null) {
                        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+500, pintent);
                    }
                    if(!isActivityDestroyed)
                    sendBroadcast(new Intent("killActivity"));
                    mSession.getController().getTransportControls().stop();
                    stopSelf();

                }

                Log.e("BatterySignalInvoked", "onReceive:BatteryLevel: "+level);
                Log.e("SleepVal", "onReceive: sleepStatus:"+isAutoSleep+" sleepBatterylevel:"+autoSleepBattery);
            } catch (Exception e){
                Log.v(TAG, "Battery Info Error");
            }
        }
    };

    private BroadcastReceiver mStopMediaService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("StopBroadcastReceived", "onReceive: Invoked");
            supressStop=true;
            mSession.getController().getTransportControls().stop();

        }
    };
}



