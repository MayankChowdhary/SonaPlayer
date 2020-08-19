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

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import java.net.URLConnection;

/**
 * Exposes the functionality of the {@link MediaPlayer} and implements the {@link PlayerAdapter}
 * so that {@link MainActivity} can control music playback.
 */
public final class MediaPlayerAdapter extends PlayerAdapter {


    private final Context mContext;
    private  MediaPlayer mMediaPlayer;
    private  MediaPlayer xMediaPlayer;
    private  String mId;
    private  PlaybackInfoListener mPlaybackInfoListener;
    private  MediaMetadataCompat mCurrentMedia;
    private  int mState;
    private  boolean mCurrentMediaPlayedToCompletion;

    private   static String[] music_styles;
    private static int equalizerIndex = 0;
    private   static int loudGain=50;
    private   static short bassGain=500;
    private static short virtualGain=500;
    private   static short trableGain=0;
    private   static short m;
    private   static short bandRange;

    private static short reverb=0;
    private   static float left=1.0f;
    private static float right=1.0f;
    private  static  float tempo=1.0f;

    private static boolean isFirstMediaAcive=false;
    private boolean firstStart=true;

    private Handler crossFadeHandler = new Handler();

    private static float mFadeInVolume = 0.0f;
    private static float mFadeOutVolume = 1.0f;
    private static   double mCrossfadeDuration=15;
    private static double  manualCrossfadeDuration=0.4;
    static boolean alterEqMode=false;
    private static  double fadeTime=0.7;
    public static boolean isCrossfade=false;
    private double crossFadeCurrent;
    private static boolean masterCrossFade=true;
    private static boolean fadeInFadeOut=true;
    private EqualizerHelper equalizerHelper;
    private static SharedPreferences  currentState;
    // Work-around for a MediaPlayer bug related to the behavior of MediaPlayer.seekTo()
    // while not playing.

    private int mSeekWhileNotPlaying = -1;

    public MediaPlayerAdapter(Context context, PlaybackInfoListener listener) {
        super(context);
        mContext = context.getApplicationContext();
        mPlaybackInfoListener = listener;

        currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        equalizerIndex = currentState.getInt("Equalizer", 5);
        loudGain=currentState.getInt("LoudValue",0);
        bassGain=(short)currentState.getInt("BassValue",1000);
        virtualGain=(short)currentState.getInt("VirtualValue",1000);
        trableGain=(short)currentState.getInt("TrableValue",0);
        left=currentState.getFloat("left",1.0f);
        right=currentState.getFloat("right",1.0f);
       tempo=currentState.getFloat("tempo",1.0f);
        reverb=(short) currentState.getInt("reverb",0);
        masterCrossFade=currentState.getBoolean("mCrossFade",true);
        fadeInFadeOut=currentState.getBoolean("FadeInFadeOut",true);

        mCrossfadeDuration=(double) (currentState.getInt("crossfadeTime",15000))/1000;
        manualCrossfadeDuration=(double) (currentState.getInt("manualFadeTime",400))/1000;
        fadeTime=(double) (currentState.getInt("fadeTime",700))/1000;
        alterEqMode=currentState.getBoolean("alterEqMode",false);
        Log.d("alterEqMode", "MediaPlayerAdapter: alterEqMode= "+alterEqMode );

        initMediaPlayers();
    }

    /**
     * Once the {@link MediaPlayer} is released, it can't be used again, and another one has to be
     * created. In the onStop() method of the {@link MainActivity} the {@link MediaPlayer} is
     * released. Then in the onStart() of the {@link MainActivity} a new {@link MediaPlayer}
     * object has to be created. That's why this method is private, and called by load(int) and
     * not the constructor.
     */


    private void initMediaPlayers(){
        releaseAll();

        Log.d("Initialization", "initMediaPlayers:Invoked ");
        mMediaPlayer=new MediaPlayer();
        xMediaPlayer=new MediaPlayer();
        mMediaPlayer.reset();
        xMediaPlayer.reset();
        mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
        xMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        xMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        initializeMediaPlayerX();
        initializeMediaPlayer();
       equalizerHelper=new EqualizerHelper(mMediaPlayer.getAudioSessionId(),xMediaPlayer.getAudioSessionId(),true);


    }

    private void initializeMediaPlayer() {

        Log.d("MediaPlayer1", "initializeMediaPlayer...: ");

        isFirstMediaAcive=true;
        if(!masterCrossFade || SonaHeartService.mIsRepeat<2) {
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    firstStart = true;
                        mPlaybackInfoListener.onPlaybackCompleted();
                        mPlaybackInfoListener.onCompletion();
                        setNewState(PlaybackStateCompat.STATE_PAUSED);
                        Log.e("OnCompletionListener", "onCompletion:EndInvoked ");

                    if (SonaHeartService.mIsRepeat > 1)
                        mMediaPlayer.reset();
                }
            });

        }

        Log.d("MediaPlayer1", "preparingMediaPlayer...: ");


        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mFadeInVolume = 0.0f;
                     mFadeOutVolume = 1.0f;
                    Log.d("MediaPlayer is prepared", "M_AudioSessionID: "+mMediaPlayer.getAudioSessionId());

                    if(firstStart) {
                        if(alterEqMode) {
                            equalizerHelper.disableEq(isFirstMediaAcive);
                            equalizerHelper.EnableEq(isFirstMediaAcive);
                        }

                        startEqualizers();
                        play(false);
                        firstStart=false;
                    }
                    else {

                        if (masterCrossFade ){
                            startEqualizers();
                            mMediaPlayer.setVolume(0.0f, 0.0f);
                        onPlay(true);
                        crossFadeHandler.postDelayed(crossFadeRunnable, 1000);
                    }else {
                            startEqualizers();
                            onPlay(true);

                        }
                    }
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("OnErrorInvoked", "onError: MediaPlayerHandled" );
                    handleExtras(extra);
                    return true;
                }
            });

        Log.d("MediaPlayer1", "FinishingMediaPlayer...: ");

    }

    @Override
    public void setCompletionListener(boolean isEnable){

        if(isEnable) {
            if (isFirstMediaAcive && mMediaPlayer != null) {

                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        firstStart = true;
                        mPlaybackInfoListener.onPlaybackCompleted();
                        mPlaybackInfoListener.onCompletion();
                        setNewState(PlaybackStateCompat.STATE_PAUSED);
                        Log.e("OnCompletionListener", "onCompletion:EndInvoked ");


                        if (SonaHeartService.mIsRepeat > 1)
                            mMediaPlayer.reset();



                    }
                });

            } else if (xMediaPlayer != null) {

                xMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        firstStart = true;
                        mPlaybackInfoListener.onPlaybackCompleted();
                        mPlaybackInfoListener.onCompletion();
                        setNewState(PlaybackStateCompat.STATE_PAUSED);
                        Log.e("OnCompletionListenerX", "onCompletion:EndInvoked ");

                        if (SonaHeartService.mIsRepeat > 1)
                            xMediaPlayer.reset();

                    }
                });

            }
        }else {

            if(isFirstMediaAcive && mMediaPlayer!=null){
                mMediaPlayer.setOnCompletionListener(null);
            }else if(xMediaPlayer!=null) {
                xMediaPlayer.setOnCompletionListener(null);
            }
        }

    }


    private void initializeMediaPlayerX() {

        Log.d("MediaPlayer2", "initializeMediaPlayer...2: ");


        isFirstMediaAcive=false;
    if(!masterCrossFade || SonaHeartService.mIsRepeat<2) {
    xMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            firstStart = true;
                mPlaybackInfoListener.onPlaybackCompleted();
                mPlaybackInfoListener.onCompletion();
                setNewState(PlaybackStateCompat.STATE_PAUSED);
                Log.e("OnCompletionListenerX", "onCompletion:EndInvoked ");

            if (SonaHeartService.mIsRepeat > 1)
                xMediaPlayer.reset();

        }
    });
}
        Log.d("MediaPlayer2", "preparingMediaPlayer...2: ");

            xMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mFadeInVolume = 0.0f;
                    mFadeOutVolume = 1.0f;


                    Log.d("MediaPlayer is prepared", "X_AudioSessionID: " + xMediaPlayer.getAudioSessionId());

                    if(firstStart) {

                        if(alterEqMode) {
                            equalizerHelper.disableEq(isFirstMediaAcive);
                            equalizerHelper.EnableEq(isFirstMediaAcive);
                        }

                        startEqualizers();
                        play(false);
                        firstStart=false;
                    }
                    else {
                        if(masterCrossFade) {
                            startEqualizers();
                            xMediaPlayer.setVolume(0.0f, 0.0f);
                            onPlay(true);
                            crossFadeHandler.postDelayed(crossFadeRunnable, 1000);
                        }else {
                            startEqualizers();
                            onPlay(true);
                        }
                    }

                }
            });

            xMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    handleExtras(extra);
                    Log.e("OnErrorInvoked", "onError: MediaPlayerHandled");

                    return true;
                }
            });

        Log.d("MediaPlayer2", "FinishingMediaPlayer...2: ");

    }

    private void handleExtras(int extra) {
        switch(extra){

            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.e("MediaPlayerError", "onError: MEDIA ERROR UNKNOWN ");
                break;

            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.e("MediaPlayerError", "onError: MEDIA_ERROR_SERVER_DIED ");
                break;

            case MediaPlayer.MEDIA_ERROR_IO:
                mPlaybackInfoListener.onCompletion();
                // handle MEDIA_ERROR_IO
                Log.e("MediaPlayerError", "onError: MEDIA_ERROR_IO ");

                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                mPlaybackInfoListener.onCompletion();
                // handle MEDIA_ERROR_MALFORMED
                Log.e("MediaPlayerError", "onError: MEDIA_ERROR_MALFORMED ");

                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                mPlaybackInfoListener.onCompletion();
                // handle MEDIA_ERROR_UNSPECIFIED
                Log.e("MediaPlayerError", "onError: MEDIA_ERROR_UNSPECIFIED ");

                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                mPlaybackInfoListener.onCompletion();
                // handle MEDIA_ERROR_TIMED_OUT
                Log.e("MediaPlayerError", "onError: MEDIA_ERROR_TIMED_OUT ");
                break;
            default:
                Log.e("MediaPlayerError", "onError: ERROR_NOT_RECOGNIZED ");

        }
    }


    // Implements PlaybackControl.
    @Override
    public void playFromMedia(MediaMetadataCompat metadata) {
        if(mMediaPlayer==null || xMediaPlayer==null)
            initMediaPlayers();

        if(isCrossfade) {
            crossFadeCurrent = mCrossfadeDuration;
            isCrossfade=false;
            Log.e("CrossfadeCurrent", "run: "+crossFadeCurrent );
        }
        else {
            crossFadeCurrent = manualCrossfadeDuration;
        }

        mCurrentMedia = metadata;
        try {
            playFile(mCurrentMedia.getDescription().getMediaId());
        }catch (Throwable e){

             e.printStackTrace();
           Toast.makeText(ApplicationContextProvider.getContext(),"Failed to Play Media!",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public MediaMetadataCompat getCurrentMedia() {
        return mCurrentMedia;
    }

    @Override
    public void setCurrentMedia(MediaMetadataCompat metadataCompat) {
         mCurrentMedia=metadataCompat;
    }

    @Override
    public int getPosition() {
        try {
            if(isFirstMediaAcive)
            return mMediaPlayer.getCurrentPosition();
            else
                return xMediaPlayer.getCurrentPosition();

        }catch (Throwable e){
            Log.e("ExceptionRaised", "getPosition:In mediaPlayerAdapter ");
            return 0;
        }
    }


    @Override
    public int getDuration() {

        try {
            if (isFirstMediaAcive && mMediaPlayer!=null)
                return mMediaPlayer.getDuration();
            else if(!isFirstMediaAcive && xMediaPlayer!=null)
                return xMediaPlayer.getDuration();
        }catch (Throwable e){
            Log.e("ExceptionRaised", "getDuration: MediaPlayerAdapter");
        }

        return 0;
    }


    @Override
    public void setNewState(){
        setNewState(mState);

    }





    private void playFile(String id) {
        boolean mediaChanged = (mId == null || !id.equals(mId));
        if (mCurrentMediaPlayedToCompletion) {
            // Last audio file was played to completion, the resourceId hasn't changed, but the
            // player was released, so force a reload of the media file for playback.
            mediaChanged = true;
            mCurrentMediaPlayedToCompletion = false;
        }
        if (!mediaChanged) {
            if (!isPlaying()) {
                play(false);

                Log.e("MediaCheck", "playFile:MediaNotChanged Invoked" );

            }
            return;
        } else {
            Log.e("MediaCheck", "playFile:MediaIsChanged Invoked" );

            if(masterCrossFade ) {
                if (isFirstMediaAcive) {
                    xMediaPlayer.stop();
                   xMediaPlayer.reset();
                } else {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                }
            }else {
                xMediaPlayer.stop();
                xMediaPlayer.reset();
                mMediaPlayer.stop();
                mMediaPlayer.reset();

            }
        }

        mId = id;

        if(!isFirstMediaAcive) {
            initializeMediaPlayer();
        }else {
            initializeMediaPlayerX();
        }

        try {

               if(isVideoFile(MusicLibrary.videoFileName.get(id)))
               {
                   Uri contentUri = ContentUris.withAppendedId(
                           android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,Long.valueOf( id));

                   if(isFirstMediaAcive)
                   mMediaPlayer.setDataSource(ApplicationContextProvider.getContext(),contentUri);
                   else
                       xMediaPlayer.setDataSource(ApplicationContextProvider.getContext(),contentUri);


               }else {

                   Uri contentUri = ContentUris.withAppendedId(
                           android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.valueOf(id));
                   if(isFirstMediaAcive)
                   mMediaPlayer.setDataSource(ApplicationContextProvider.getContext(), contentUri);
                   else
                       xMediaPlayer.setDataSource(ApplicationContextProvider.getContext(), contentUri);

               }




        } catch (Exception e) {
            throw new RuntimeException("Failed to open file: " + mId, e);
        }

        try {

            if(isFirstMediaAcive)
            mMediaPlayer.prepare();
            else
                xMediaPlayer.prepare();



        } catch (Exception e) {
            throw new RuntimeException("Failed to open file: " + mId, e);
        }

      //  play();
    }

    @Override
    public void onStop() {
        // Regardless of whether or not the MediaPlayer has been created / started, the state must
        // be updated, so that MediaNotificationManager can take down the notification.


        try {

            SharedPreferences currentState = mContext.getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            if(isFirstMediaAcive)
            editor.putLong("currentPosition", mMediaPlayer.getCurrentPosition());
            else
                editor.putLong("currentPosition", xMediaPlayer.getCurrentPosition());
            editor.commit();
        }catch (Throwable e){

            Log.e("onStopException", "Exception Raised: "+e);
        }
        crossFadeHandler.removeCallbacks(crossFadeRunnable);
        releaseAll();
        isFirstMediaAcive=false;
        firstStart=true;
        setNewState(PlaybackStateCompat.STATE_STOPPED);

    }
    private void releaseAll() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;

        }

        if (xMediaPlayer != null) {
            xMediaPlayer.release();
            xMediaPlayer = null;

        }

        try {
            if(equalizerHelper!=null)
            equalizerHelper.releaseEQObjects();
        }catch (Throwable e){
            Log.e("ExceptionRaised", "releaseAll: "+e );
        }
        firstStart=true;
    }


    @Override
    public boolean isPlaying() {

        if(isFirstMediaAcive)
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
        else
            return xMediaPlayer != null && xMediaPlayer.isPlaying();

    }

    @Override
    protected void onPlay(boolean supressFade) {

        firstStart=false;
        if(isFirstMediaAcive) {
            if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                Log.d("OnPlayReady", "onPlayReady:SeekTime is: " + mMediaPlayer.getCurrentPosition());
                Log.d("MediaPlayerValues", "onPlay: supressFade: +"+supressFade+"FadeInFadeOut: +"+fadeInFadeOut);
               if(!supressFade && fadeInFadeOut)
               {
                   mFadeInVolume=0.0f;
                   getCurrentMediaPlayer().setVolume(0.0f,0.0f);
                   mMediaPlayer.start();
                   crossFadeHandler.postDelayed(FadeInRunnable,0);
               }else {
                   mMediaPlayer.start();
                   MainActivity.supressPlay=false;
               }
                setNewState(PlaybackStateCompat.STATE_PLAYING);
                Log.d("OnPlayInvoked", "onPlay:SeekTime is: " + mMediaPlayer.getCurrentPosition());
            }
        }else {

            if (xMediaPlayer != null && !xMediaPlayer.isPlaying()) {
                Log.d("OnPlayReady", "onPlayReady:SeekTime is: " + xMediaPlayer.getCurrentPosition());
                if(!supressFade && fadeInFadeOut)
                {
                    mFadeInVolume=0.0f;
                    getCurrentMediaPlayer().setVolume(0.0f,0.0f);
                    xMediaPlayer.start();
                    crossFadeHandler.postDelayed(FadeInRunnable,0);
                }else {
                    xMediaPlayer.start();
                    MainActivity.supressPlay=false;
                }
                setNewState(PlaybackStateCompat.STATE_PLAYING);
                Log.d("OnPlayInvoked", "onPlay:SeekTime is: " + xMediaPlayer.getCurrentPosition());

            }
        }
    }

@Override
public Bundle onEqCommand(String command,Bundle extras){
          try{
        switch (command) {

            case "Equalizer":

                Bundle bundleq = new Bundle();
                bundleq.putInt("EqIndex", equalizerIndex);
                bundleq.putStringArray("EqName", music_styles);
                bundleq.putShort("Eqnum", m);
                bundleq.putShort("eLavel", bandRange);
                Log.e("EqualizerCommand", "onCommandMediaPlayback:Invoked ELavel Sent:" + bandRange);
                return bundleq;

            case "Equalizerx":

                switchEq(extras.getInt("EqIndex"));
                return null;

            case "Loudness":

                loudGain = extras.getInt("LoudValue");
                setLoudnessEnhancerGain(extras.getInt("LoudValue"));

                return null;

            case "BassBoost":

                bassGain = extras.getShort("BassValue");
                setBassBoostStrength(extras.getShort("BassValue"));
                Log.d("BassBoost command", "BassValue: " + (extras.getShort("BassValue")));

                return null;

            case "Virtualizer":
                virtualGain = extras.getShort("VirtualValue");
                setVirtualizerStrength(extras.getShort("VirtualValue"));

                Log.d("Virtual command", "VirtualValue: " + (extras.getShort("VirtualValue")));

                return null;

            case "TrableBoost":

                trableGain = extras.getShort("TrableValue");
                trableBoost(extras.getShort("TrableValue"));

                return null;

            case "channelBalance":

                channelBalance(extras.getFloat("left"), extras.getFloat("right"));
                return null;

            case "Tempo":

                setTempo(extras.getFloat("tempo"));
                return null;

            case "Reverb":

                setPresetReverbStrength(extras.getShort("reverb"));

                Log.d("Reverb command", "ReverbValue: " + (extras.getShort("reverb")));

                return null;

            case "TempoX":
                SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                setTempo(currentState.getFloat("tempo", 1.0f));
                return null;

            case "CrossFade":
                masterCrossFade=extras.getBoolean("crossFading");
                if(masterCrossFade)
                {
                 setCompletionListener(false);
                }else
                {
                    setCompletionListener(true);
                }
                return null;

            case "FadingInOut":
                fadeInFadeOut=extras.getBoolean("Fading");
                return null;

            case "CrossfadeTime":
                mCrossfadeDuration=(double) (extras.getInt("crossfadeTime",15000))/1000;
                return null;

            case "ManualFadeTime":
                manualCrossfadeDuration=(double) (extras.getInt("manualFadeTime",400))/1000;
                return null;

            case "FadeTime":
                fadeTime=(double)(extras.getInt("fadeTime",700))/1000;
                return null;
            case "AlterEqMode":
                alterEqMode=extras.getBoolean("alterEqMode");
                return null;

        }

        }catch (Throwable e){

            Log.e("ExceptionRaised", "onEqCommand: "+e );
        }

        return null ;
}

    @Override
    protected void onPause(boolean supressFade) {
        mFadeOutVolume=1.0f;
        if(isFirstMediaAcive) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                if(!supressFade && fadeInFadeOut) {
                    crossFadeHandler.postDelayed(FadeOutRunnable, 0);
              } else {
                    mMediaPlayer.pause();
                    MainActivity.supressPlay=false;
                }
                if(xMediaPlayer.isPlaying()){
                    xMediaPlayer.stop();
                    xMediaPlayer.reset();
                    if(alterEqMode) {
                        equalizerHelper.disableEq(isFirstMediaAcive);
                        equalizerHelper.EnableEq(isFirstMediaAcive);
                        startEqualizers();
                    }else
                    channelBalance(left,right);
                }
                setNewState(PlaybackStateCompat.STATE_PAUSED);
                Log.d("OnPauseInvoked", "onPause:SeekTime is: " + mMediaPlayer.getCurrentPosition());

            }
        }else {
            if (xMediaPlayer != null && xMediaPlayer.isPlaying()) {
                if(!supressFade &&fadeInFadeOut)
                crossFadeHandler.postDelayed(FadeOutRunnable,0);
                else {
                    xMediaPlayer.pause();
                    MainActivity.supressPlay=false;

                }
                if(mMediaPlayer.isPlaying()){

                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    if(alterEqMode) {
                        equalizerHelper.disableEq(isFirstMediaAcive);
                        equalizerHelper.EnableEq(isFirstMediaAcive);
                        startEqualizers();
                    }else
                        channelBalance(left,right);
                }
                setNewState(PlaybackStateCompat.STATE_PAUSED);
                Log.d("OnPauseInvoked", "onPause:SeekTime is: " + xMediaPlayer.getCurrentPosition());
            }

        }

    }



    // This is the main reducer for the player state machine.
    private void setNewState(@PlaybackStateCompat.State int newPlayerState) {
        mState = newPlayerState;
        // Whether playback goes to completion, or whether it is stopped, the
        // mCurrentMediaPlayedToCompletion is set to true.
        if (mState == PlaybackStateCompat.STATE_STOPPED) {
            mCurrentMediaPlayedToCompletion = true;

        }else {
            mCurrentMediaPlayedToCompletion=false;
        }


        // Work around for MediaPlayer.getCurrentPosition() when it changes while not playing.
        final long reportPosition;
        if (mSeekWhileNotPlaying >= 0) {
            reportPosition = mSeekWhileNotPlaying;

            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                mSeekWhileNotPlaying = -1;
            }
        } else {

            if(isFirstMediaAcive)
                reportPosition = mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
            else
                reportPosition = xMediaPlayer == null ? 0 : xMediaPlayer.getCurrentPosition();


        }

        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(getAvailableActions());
        stateBuilder.setState(mState,
                              reportPosition,
                              1.0f,
                              SystemClock.elapsedRealtime());
        mPlaybackInfoListener.onPlaybackStateChange(stateBuilder.build());

        if(SonaHeartService.isActivityDestroyed && mState== PlaybackStateCompat.STATE_PLAYING){

            SharedPreferences  currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            setTempo(currentState.getFloat("tempo",1.0f));

        }

        if(SonaHeartService.isActivityDestroyed &&  SonaHeartService.isMediaButtonStopped && Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) {
            SonaHeartService.isMediaButtonStopped=false;
            Log.e("KillServiceInvoked", "onStop: ServiceKilled");
            ApplicationContextProvider.getContext().stopService(new Intent(ApplicationContextProvider.getContext(), SonaHeartService.class));
            //System.exit(0);

            if(!MainActivity.isActivityRunning) {
                Intent serviceIntent = new Intent(mContext, HeadsetTriggerService.class);
                mContext.startService(serviceIntent);
                Log.d("HeadSetTriggerService", "onDestroy: HeadsetWatchdogStarted");
            }

        }


    }

    /**
     * Set the current capabilities available on this session. Note: If a capability is not
     * listed in the bitmask of capabilities then the MediaSession will not handle it. For
     * example, if you don't want ACTION_STOP to be handled by the MediaSession, then don't
     * included it in the bitmask that's returned.
     */

    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                       | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                       | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                       | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        switch (mState) {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                           | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                           | PlaybackStateCompat.ACTION_PAUSE
                           | PlaybackStateCompat.ACTION_SEEK_TO
                            |PlaybackStateCompat.ACTION_FAST_FORWARD
                        |PlaybackStateCompat.ACTION_REWIND;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                           | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                           | PlaybackStateCompat.ACTION_PLAY_PAUSE
                           | PlaybackStateCompat.ACTION_STOP
                           | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

    @Override
    public void seekTo(long position) {

        Log.e("MesterSeekInvoked", "seekTo: MediaPlayerAdapter" );
        if(isFirstMediaAcive) {
            if (mMediaPlayer != null) {
                if (!mMediaPlayer.isPlaying()) {
                    mSeekWhileNotPlaying = (int) position;
                }

                if(xMediaPlayer!=null && xMediaPlayer.isPlaying()){
                    crossFadeHandler.removeCallbacks(crossFadeRunnable);
                    xMediaPlayer.stop();
                    xMediaPlayer.reset();
                    if(alterEqMode) {
                        equalizerHelper.disableEq(isFirstMediaAcive);
                        equalizerHelper.EnableEq(isFirstMediaAcive);
                        startEqualizers();
                    }else
                    channelBalance(left,right);

                }

                if(position<mMediaPlayer.getDuration())
                mMediaPlayer.seekTo((int) position);
                else
                    mMediaPlayer.seekTo(mMediaPlayer.getDuration()-1000);

                // Set the state (to the current state) because the position changed and should
                // be reported to clients.
                setNewState(mState);
            }
        }else {


            if (xMediaPlayer != null) {
                if (!xMediaPlayer.isPlaying()) {
                    mSeekWhileNotPlaying = (int) position;
                }
                if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
                    crossFadeHandler.removeCallbacks(crossFadeRunnable);
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    if(alterEqMode) {
                        equalizerHelper.disableEq(isFirstMediaAcive);
                        equalizerHelper.EnableEq(isFirstMediaAcive);
                        startEqualizers();
                    }else
                        channelBalance(left,right);
                }

                if(position<xMediaPlayer.getDuration())
                    xMediaPlayer.seekTo((int) position);
                else
                    xMediaPlayer.seekTo(xMediaPlayer.getDuration()-1000);

                // Set the state (to the current state) because the position changed and should
                // be reported to clients.
                setNewState(mState);

            }
        }
    }

    @Override
    public void setVolume(float volL,float volR) {
        if(isFirstMediaAcive) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setVolume(volL, volR);
            }
        }else {
                if (xMediaPlayer != null) {
                    xMediaPlayer.setVolume(volL, volR);
                }
            }
    }

    @Override
    public int getSessionId(){

        if(isFirstMediaAcive)
        return mMediaPlayer.getAudioSessionId();
        else
            return xMediaPlayer.getAudioSessionId();


    }


    private static boolean isVideoFile(String path) {

        try {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("video");
        }catch (Throwable e){
            Log.e("ExceptionInIsVideo", "isVideoFile: "+e );
            return false;
        }

    }

    /*private static String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = ApplicationContextProvider.getContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }*/




    public  void loadEqualizers() {

        m =  equalizerHelper.getEqualizer().getNumberOfPresets();
        bandRange= equalizerHelper.getEqualizer().getBandLevelRange()[1];
        music_styles = new String[m];
        for (int k = 0; k < m; k++) {
            music_styles[k] =  equalizerHelper.getEqualizer().getPresetName((short) k);
            Log.d("Equalizers", "Names: " + music_styles[k]);
        }


        Log.d("Trable lavel ", "UpperLimit: "+ equalizerHelper.getEqualizer().getBandLevelRange()[1]);
        Log.d("Trable lavel ", "LowerLimit: "+ equalizerHelper.getEqualizer().getBandLevelRange()[0]);



    }

    public void switchEq(int index) {

        try {
            if(isFirstMediaAcive) {
                equalizerHelper.getEqualizer().usePreset((short) index);
            }else {

                equalizerHelper.getEqualizer2().usePreset((short) index);

            }
            equalizerIndex = index;
            trableGain = 0;
            SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putInt("Equalizer", equalizerIndex);
            editor.putInt("TrableValue", 0);
            editor.commit();

        }catch (Throwable e){
            Log.e("EqualizerException", "Exception Raised in Switch Equalizer: "+e );


        }



    }

    public  void trableBoost(short val){

        try {
            if (val > 0) {
                if(isFirstMediaAcive) {
                    equalizerHelper.getEqualizer().setBandLevel((short) 4, val);
                    equalizerHelper.getEqualizer().setBandLevel((short) 3, val);
                }else {

                    equalizerHelper.getEqualizer2().setBandLevel((short) 4, val);
                    equalizerHelper.getEqualizer2().setBandLevel((short) 3, val);
                }
            } else {

                switchEq(equalizerIndex);
            }


            SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putInt("TrableValue", val);
            editor.commit();

        }catch (Throwable e){

            Log.e("TrableException", "Exception Raised in Trable: "+e );
        }

    }


    public void startEqualizers(){

            loadEqualizers();
            setBassBoostStrength(bassGain);
           setVirtualizerStrength(virtualGain);
            setPresetReverbStrength(reverb);
            setLoudnessEnhancerGain( loudGain);
            trableBoost(trableGain);
            channelBalance(left, right);

         //setTempo(tempo)

    }


    public  void setBassBoostStrength(short strength) {
            try {
                if (strength <= 1000) {
                    if(isFirstMediaAcive)
                        equalizerHelper.getBassBoost().setStrength(strength);
                    else
                        equalizerHelper.getBassBoost2().setStrength(strength);
                    SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putInt("BassValue",(int) strength);
                    editor.commit();
                }
            } catch (IllegalArgumentException e) {
                Log.e("BassBoosts", "Bassboost effect not supported");
            } catch (IllegalStateException e) {
                Log.e("BassBoosts", "Bassboost cannot get strength supported");
            } catch (UnsupportedOperationException e) {
                Log.e("BassBoosts", "Bassboost library not loaded");
            } catch (RuntimeException e) {
                Log.e("BassBoosts", "Bassboost effect not found");
            }
    }



    public  void setVirtualizerStrength(short strength) {
            try {
                if (strength <= 1000) {
                    if(isFirstMediaAcive)
                    equalizerHelper.getVirtualizer().setStrength(strength);
                    else
                        equalizerHelper.getVirtualizer2().setStrength(strength);

                    SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putInt("VirtualValue", (int)strength);
                    editor.commit();
                }
            } catch (IllegalArgumentException e) {
                Log.e("Virtualizers", "Virtualizers effect not supported");
            } catch (IllegalStateException e) {
                Log.e("Virtualizers", "Virtualizers cannot get strength supported");
            } catch (UnsupportedOperationException e) {
                Log.e("Virtualizers", "Virtualizers library not loaded");
            } catch (RuntimeException e) {
                Log.e("Virtualizers", "Virtualizers effect not found");
            }

    }



    public  void setLoudnessEnhancerGain(int gain) {

            try {
                if (gain <= 1000) {
                    if(isFirstMediaAcive)
                        equalizerHelper.getLoudnessEnhancer().setTargetGain(gain);
                    else
                        equalizerHelper.getLoudnessEnhancer2().setTargetGain(gain);

                    Log.d("LoudNess command", "Value:"+gain);
                    SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putInt("LoudValue", gain);
                    editor.commit();
                }
            } catch (IllegalArgumentException e) {
                Log.e("Loud", "Loud effect not supported");
            } catch (IllegalStateException e) {
                Log.e("Loud", "Loud cannot get gain supported");
            } catch (UnsupportedOperationException e) {
                Log.e("Loud", "Loud library not loaded");
            } catch (RuntimeException e) {
                Log.e("Loud", "Loud effect not found");
            }

    }


    public  void setPresetReverbStrength(short strength) {

            try {
                if(isFirstMediaAcive)
                    equalizerHelper.getReverb().setPreset(strength);
                else
                    equalizerHelper.getReverb2().setPreset(strength);

                   reverb = strength;
                    SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putInt("reverb", (int) strength);
                    editor.commit();

                Log.d("ReverbSet", "setPresetReverbStrength: "+strength);

            } catch (IllegalArgumentException e) {
                Log.e("Reverb", "Reverb effect not supported");
            } catch (IllegalStateException e) {
                Log.e("Reverb", "Reverb cannot get strength supported");
            } catch (UnsupportedOperationException e) {
                Log.e("Reverb", "Reverb library not loaded");
            } catch (RuntimeException e) {
                Log.e("Reverb", "Reverb effect not found");
            }
    }


    public void channelBalance(float leftx,float rightx){


            try {
                if(getCurrentMediaPlayer()!=null) {
                    getCurrentMediaPlayer().setVolume(leftx, rightx);
                    left = leftx;
                    right = rightx;
                    SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putFloat("left", leftx);
                    editor.putFloat("right", rightx);
                    editor.commit();
                    Log.d("MainChannelMethod", "channelBalance: Left: " + leftx + " right: " + rightx);
                }
            }catch (Throwable e){


                Log.e("ExceptionRaised", "channelBalance: MediaPlayerAdapter "+e );
            }

    }


    private  void setTempo(float speed){



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (getCurrentMediaPlayer() != null) {
                try {

                    if (tempo == 1.0f && speed == 1.0f) {

                        Log.d("PlaybackCurrent Speed==" + tempo, "DemandTempo: " + speed);
                        return;
                    }
                    Log.d("TempoChanged", "setTempo: " + speed);
                    getCurrentMediaPlayer().setPlaybackParams(getCurrentMediaPlayer().getPlaybackParams().setSpeed(speed));
                    tempo = speed;
                    SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putFloat("tempo", tempo);
                    editor.commit();

                } catch (Throwable e) {

                    Log.e("ExceptionRaised", "tempo: " + e);

                }
            }
        }
        }






    public Runnable crossFadeRunnable = new Runnable() {

        @Override
        public void run() {
            crossFadeHandler.removeCallbacksAndMessages(crossFadeRunnable);
            try {

                Log.e("CrossFadeHandler", "run: Invoked" );
                    if(mMediaPlayer!=null &&xMediaPlayer!=null) {
                        if(mMediaPlayer.isPlaying()&&xMediaPlayer.isPlaying()) {

                            getSecondMediaPlayer().setOnCompletionListener(null);
                            getCurrentMediaPlayer().setVolume(mFadeInVolume, mFadeInVolume);
                            getSecondMediaPlayer().setVolume(mFadeOutVolume, mFadeOutVolume);

                            mFadeInVolume = mFadeInVolume + (float) (1.0f / (((float) crossFadeCurrent) * 10.0f));
                            mFadeOutVolume = mFadeOutVolume - (float) (1.0f / (((float) crossFadeCurrent) * 10.0f));

                            Log.d("crossFadeInvoked", "run:mFadeInVolume: " + mFadeInVolume);
                            Log.d("crossFadeInvoked", "run:mFadeOutVolume: " + mFadeOutVolume);

                            if (mFadeOutVolume <= 0.01) {
                                getCurrentMediaPlayer().setVolume(1.0f, 1.0f);
                                if (isFirstMediaAcive) {
                                    xMediaPlayer.stop();
                                    xMediaPlayer.reset();
                                } else {
                                    mMediaPlayer.stop();
                                    mMediaPlayer.reset();
                                }
                                if(alterEqMode) {
                                    equalizerHelper.disableEq(isFirstMediaAcive);
                                    equalizerHelper.EnableEq(isFirstMediaAcive);
                                    startEqualizers();
                                }else
                                    channelBalance(left,right);

                                crossFadeHandler.removeCallbacks(crossFadeRunnable);

                            } else
                                crossFadeHandler.postDelayed(crossFadeRunnable, 100);
                        }else {
                            if (isFirstMediaAcive) {
                                xMediaPlayer.stop();
                                xMediaPlayer.reset();
                            } else {
                                mMediaPlayer.stop();
                                mMediaPlayer.reset();
                            }
                            if(alterEqMode) {
                                equalizerHelper.disableEq(isFirstMediaAcive);
                                equalizerHelper.EnableEq(isFirstMediaAcive);
                                startEqualizers();
                            }else
                                channelBalance(left,right);
                            crossFadeHandler.removeCallbacks(crossFadeRunnable);

                        }
                    }

            } catch (Exception e) {
                crossFadeHandler.removeCallbacks(crossFadeRunnable);
                e.printStackTrace();
            }
        }};


    public Runnable FadeInRunnable = new Runnable() {

        @Override
        public void run() {
            crossFadeHandler.removeCallbacksAndMessages(FadeInRunnable);
            try {
                if (getCurrentMediaPlayer() != null) {
                    getCurrentMediaPlayer().setVolume(mFadeInVolume, mFadeInVolume);
                    mFadeInVolume = mFadeInVolume + (float) (1.0f / (((float) fadeTime) * 10.0f));
                    Log.d("FadeInRunnable", "run: Volume=" + mFadeInVolume);
                    if (mFadeInVolume > 0.95) {
                        channelBalance(left, right);
                        MainActivity.supressPlay=false;
                        crossFadeHandler.removeCallbacksAndMessages(FadeInRunnable);
                    } else
                        crossFadeHandler.postDelayed(FadeInRunnable, 100);
                }
            }
         catch (Exception e) {
             MainActivity.supressPlay=false;
            crossFadeHandler.removeCallbacks(FadeInRunnable);
            e.printStackTrace();
        }
    }};

    public Runnable FadeOutRunnable = new Runnable() {

        @Override
        public void run() {
            crossFadeHandler.removeCallbacksAndMessages(FadeOutRunnable);
            try {

                getCurrentMediaPlayer().setVolume(mFadeOutVolume,mFadeOutVolume);
                mFadeOutVolume = mFadeOutVolume - (float) (1.0f / (((float) fadeTime) * 10.0f));
                Log.d("FadeOutRunnable", "run: Volume="+mFadeOutVolume);
                if(mFadeOutVolume <=0.09){
                    getCurrentMediaPlayer().setVolume(0.0f,0.0f);
                    getCurrentMediaPlayer().pause();
                    MainActivity.supressPlay=false;
                    crossFadeHandler.removeCallbacks(FadeOutRunnable);
                }else
                    crossFadeHandler.postDelayed(this,100);
            }
            catch (Exception e) {
                MainActivity.supressPlay=false;
                crossFadeHandler.removeCallbacks(FadeOutRunnable);
                e.printStackTrace();
            }
        }
    };




    private MediaPlayer getCurrentMediaPlayer(){

        if(isFirstMediaAcive)
            return mMediaPlayer;
        else
            return xMediaPlayer;

    }

    private MediaPlayer getSecondMediaPlayer(){

        if(isFirstMediaAcive)
            return xMediaPlayer;
        else
            return mMediaPlayer;

    }
}
