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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;


import static android.content.Context.AUDIO_SERVICE;

/**
 * Abstract player implementation that handles playing music with proper handling of headphones
 * and audio focus.
 */
public abstract class PlayerAdapter {

    private static final float MEDIA_VOLUME_DEFAULT = 1.0f;
    private static final float MEDIA_VOLUME_DUCK = 0.3f;
    private   static float left=1.0f;
    private static float right=1.0f;
    private static  boolean pFocus_pause=true;
    private static boolean tFocus_pause=true;
    private static boolean can_duck=true;

    private static final IntentFilter AUDIO_NOISY_INTENT_FILTER =
            new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private boolean mAudioNoisyReceiverRegistered = false;
    private final BroadcastReceiver mAudioNoisyReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                        if (isPlaying()) {
                            pause(true);
                        }
                    }
                }
            };

    private final Context mApplicationContext;
    private final AudioManager mAudioManager;
    private final AudioFocusHelper mAudioFocusHelper;
    private AudioManager audioManager;
    private int music_vol_level=8;
    private int phone_vol_level=8;
    private boolean mPlayOnAudioFocus = false;

    public PlayerAdapter(@NonNull Context context) {
        mApplicationContext = context.getApplicationContext();
        mAudioManager = (AudioManager) mApplicationContext.getSystemService(AUDIO_SERVICE);
        mAudioFocusHelper = new AudioFocusHelper();
        audioManager = (AudioManager) ApplicationContextProvider.getContext().getSystemService(AUDIO_SERVICE);
        phone_vol_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

    }

    public abstract void playFromMedia(MediaMetadataCompat metadata);

    public abstract MediaMetadataCompat getCurrentMedia();
    public abstract void setCurrentMedia(MediaMetadataCompat metadataCompat);

    public abstract boolean isPlaying();
    public abstract void setCompletionListener(boolean isEnable);
    public abstract int getSessionId();
    public abstract Bundle onEqCommand(String command, Bundle extras);

    public final void play(boolean supressFade) {

            if (mAudioFocusHelper.requestAudioFocus()) {
                registerAudioNoisyReceiver();
                onPlay(supressFade);
            }

    }

    /**
     * Called when media is ready to be played and indicates the app has audio focus.
     */
    protected abstract void onPlay(boolean supressFade);

    public final void pause(boolean supressFade) {
        if (!mPlayOnAudioFocus) {
            mAudioFocusHelper.abandonAudioFocus();
        }

        unregisterAudioNoisyReceiver();
        onPause(supressFade);
        Log.e("playerAdapterOnPause", "pause:Invoked " );
    }

    /**
     * Called when media must be paused.
     */
    protected abstract void onPause(boolean supressFade);

    public final void stop() {
        mAudioFocusHelper.abandonAudioFocus();
        unregisterAudioNoisyReceiver();
        onStop();


    }

    /**
     * Called when the media must be stopped. The player should clean up resources at this
     * point.
     */
    protected abstract void onStop();

    public abstract void seekTo(long position);
    public abstract int getPosition();
    public abstract int getDuration();
    public abstract void setNewState();


    public abstract void setVolume(float leftx,float rightx);



    private void registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            mApplicationContext.registerReceiver(mAudioNoisyReceiver, AUDIO_NOISY_INTENT_FILTER);
            mAudioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            mApplicationContext.unregisterReceiver(mAudioNoisyReceiver);
            mAudioNoisyReceiverRegistered = false;
        }
    }

    /**
     * Helper class for managing audio focus related tasks.
     */
    private final class AudioFocusHelper
            implements AudioManager.OnAudioFocusChangeListener {

        private boolean requestAudioFocus() {
            final int result = mAudioManager.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }

        private void abandonAudioFocus() {
            mAudioManager.abandonAudioFocus(this);
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            SharedPreferences  currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            left=currentState.getFloat("left",1.0f);
            right=currentState.getFloat("right",1.0f);
            pFocus_pause=currentState.getBoolean("pFocusPause",true);
            tFocus_pause=currentState.getBoolean("tFocusPause",true);
            can_duck=currentState.getBoolean("canDuck",true);

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mPlayOnAudioFocus && !isPlaying()) {
                        audioManager = (AudioManager) ApplicationContextProvider.getContext().getSystemService(AUDIO_SERVICE);
                        phone_vol_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, music_vol_level, 0);
                        play(false);
                    } else if (isPlaying()) {
                        setVolume(left,right);
                        //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, music_vol_level, 0);
                    }
                    mPlayOnAudioFocus = false;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                    if(can_duck)
                   setVolume(MEDIA_VOLUME_DUCK,MEDIA_VOLUME_DUCK);

                    Log.e("AudioFocusCANDuck", "onAudioDuckChange: Invoked: val:"+can_duck );

                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (isPlaying() && tFocus_pause) {
                        audioManager = (AudioManager) ApplicationContextProvider.getContext().getSystemService(AUDIO_SERVICE);
                        music_vol_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        mPlayOnAudioFocus = true;
                        pause(true);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, phone_vol_level, 0);
                        Log.e("AudioFocusLossTrans", "onAudioFocusChange: Invoked" );
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (isPlaying()) {
                        audioManager = (AudioManager) ApplicationContextProvider.getContext().getSystemService(AUDIO_SERVICE);
                        music_vol_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                        if(pFocus_pause) {
                            mPlayOnAudioFocus = true;
                            pause(true);
                        }
                        else {
                            mPlayOnAudioFocus = false;
                            mAudioManager.abandonAudioFocus(this);
                            pause(true);
                            if(isHeadsetOn(ApplicationContextProvider.getContext())){
                                SharedPreferences.Editor editor = currentState.edit();
                                editor.putInt("earVolLevel", mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                                editor.commit();
                            }else {
                                SharedPreferences.Editor editor = currentState.edit();
                                editor.putInt("volLevel", mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                                editor.commit();
                            }
                        }
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, phone_vol_level, 0);
                        currentState=ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = currentState.edit();
                        editor.putInt("volLevel", music_vol_level);
                        editor.commit();
                        Log.e("AudioFocusLossPerma", "onAudioFocusChange: Invoked" );
                    }

                   // mAudioManager.abandonAudioFocus(this);
                  //  mPlayOnAudioFocus = false;
                  //  stop();
                    break;
            }
        }
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
}
