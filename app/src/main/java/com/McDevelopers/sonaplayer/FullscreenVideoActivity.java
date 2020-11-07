package com.McDevelopers.sonaplayer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenVideoActivity extends AppCompatActivity implements UniversalVideoView.VideoViewCallback{

    private static final String TAG = "MainActivity";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    private static  Uri VIDEO_URL ;
    private static String Title="";
    private static  boolean isPlaying=false;
    static Equalizer mEqualizer=null;
    static String[] music_styles;
    static int equalizerIndex = 0;
    static int loudGain=50;
    static short bassGain=500;
    static short virtualGain=500;
    static short trableGain=0;
    static short m;
    static short bandRange;
    private static BassBoost bassBoost = null;
    private static Virtualizer virtualizer = null;
    private static LoudnessEnhancer loudnessEnhancer = null;

    private AudioManager mAudioManager;

    static int cachedWidth;
    static int cachedHeight;

    static boolean repeatABFlag;
    static int repeatStartTime;
    static int repeatEndTime;

    private boolean fistseek=true;
    private static PresetReverb presetReverb = null;

    private  float left=1f ;
    private  float right=1f ;
    private float tempo=1.0f;
    private static short reverb=0;
    final private static int brightnessSystem=MainActivity.brightnessSystem;
    private static int brightnessCurrent=-1;
    SharedPreferences   currentState;
    SharedPreferences.Editor editor;

    ConstraintLayout videoParent;

    private SeekBar vSeekbar;
    private  RotateLayout rotateLayout;
    private SeekBar bSeekbar;
    private  RotateLayout rotateLayoutB;
    private Handler vLongTouchHandlers =new Handler();
    private Handler mRepeatABHandler =new Handler();


   static UniversalVideoView mVideoView;
   UniversalMediaController mMediaController;

    FrameLayout mVideoLayout;

    private int mSeekPosition;
   // private int cachedHeight;
    private boolean isFullscreen=false;


    int UI_OPTIONS = View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_video);
        androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.hide();
        Intent i = getIntent();
       final SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        equalizerIndex = currentState.getInt("Equalizer", 5);
        loudGain=currentState.getInt("LoudValue",0);
        bassGain=(short)currentState.getInt("BassValue",1000);
        virtualGain=(short)currentState.getInt("VirtualValue",1000);
        trableGain=(short)currentState.getInt("TrableValue",0);
        left=currentState.getFloat("left",1.0f);
        right=currentState.getFloat("right",1.0f);
        tempo=currentState.getFloat("tempo",1.0f);
        reverb=(short) currentState.getInt("reverb",0);
        brightnessCurrent=currentState.getInt("brightness",-1);

        vSeekbar=findViewById(R.id.video_full_seekBar);
        rotateLayout=findViewById(R.id.rotate_layout_seekbar);
        bSeekbar=findViewById(R.id.video_brightfull_seekBar);
        rotateLayoutB=findViewById(R.id.rotate_layout_brightness);
        videoParent=findViewById(R.id.video_parents);
        Log.d(TAG, "fullScreenEqualizers: EQindex:"+equalizerIndex+" LoudGain: "+loudGain+" BassGain: "+bassGain+" VirtualGain: "+virtualGain);


        getWindow().getDecorView().setSystemUiVisibility(UI_OPTIONS);
        String URI = i.getStringExtra("VideoUri");
        VIDEO_URL= Uri.parse(URI);
        Title= i.getStringExtra("Title");
        mSeekPosition=i.getIntExtra("seekPos",0);
        repeatABFlag=i.getBooleanExtra("repeatABflag",false);
        repeatStartTime=i.getIntExtra("repeatStartTime",0);
        repeatEndTime=i.getIntExtra("repeatEndTime",0);

        mVideoLayout = findViewById(R.id.video_layout);


        mVideoView =  findViewById(R.id.videoViewFullscreen);
        mMediaController =  findViewById(R.id.media_controller);
        //mVideoView.setMediaController(mMediaController);
        clearEqualizers();
        setVideoAreaSize();
        mVideoView.setVideoViewCallback(this);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {


                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                        if(!isFullscreen) {
                            mVideoView.setMediaController(mMediaController);
                            mMediaController.setLayoutParams(new FrameLayout.LayoutParams(mVideoView.getWidth(), mVideoView.getHeight()));
                            cachedWidth = mVideoView.getWidth();
                            cachedHeight = mVideoView.getHeight();

                            Log.e("VideoHeightInvoked", "Prepare-onVideoSizeChanged:Height= " + cachedHeight);

                            if (mVideoView.getHeight() < 600) {
                                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((mVideoView.getHeight() - 100), ViewGroup.LayoutParams.WRAP_CONTENT);
                                vSeekbar.setLayoutParams(lp);
                                bSeekbar.setLayoutParams(lp);
                            } else {

                                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((500), ViewGroup.LayoutParams.WRAP_CONTENT);
                                vSeekbar.setLayoutParams(lp);
                                bSeekbar.setLayoutParams(lp);
                            }


                        }

                    }
                });



                if(mEqualizer==null)
                    startEqualizers(equalizerIndex,mp.getAudioSessionId(),mp);


                    mp.setVolume(left, right);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && tempo!=1.0f ) {

                    mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(tempo));
                }

                videoParent.setBackgroundColor(getResources().getColor(R.color.black));

                if (isFullscreen) {

                    ConstraintLayout.LayoutParams lpv =
                            new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                                    ConstraintLayout.LayoutParams.MATCH_PARENT);
                    mVideoLayout.setLayoutParams(lpv);


                    mVideoView.setMediaController(mMediaController);
                    mMediaController.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER;
                    mVideoView.setLayoutParams(params);

                    rotateLayout.setAngle(90);
                    rotateLayoutB.setAngle(90);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT);
                    vSeekbar.setLayoutParams(lp);
                    bSeekbar.setLayoutParams(lp);
                    videoParent.setBackgroundColor(getResources().getColor(R.color.black));


                    Log.e("OnPrepareIvoked", "onPrepare:FullScreen ");


                }

                if(repeatABFlag) {
                    SonaToast sonaToast = new SonaToast();
                    sonaToast.setToast(getApplicationContext(), "RepeatAB Enabled", 0);
                }
            }

        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion ");

                if(repeatABFlag){
                    mRepeatABHandler.removeCallbacks(mRepeatABRunnable);
                    repeatABFlag=false;
                }

                clearEqualizers();
                Intent intent = new Intent();
                intent.putExtra("seekPos", mp.getDuration());
                intent.putExtra("isCompleted",true);
                intent.putExtra("repeatABflag",false);
                Log.d("SeekPosSent", "onDestroy:seekpos= "+ mVideoView.getCurrentPosition());
                setResult(RESULT_OK, intent);

                SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putLong("currentPosition",0);
                editor.commit();
                finish();

            }
        });

        vSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    if(!fistseek)
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    else
                        fistseek=false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mMediaController.hide();
                vSeekbar.setAlpha(1f);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                vLongTouchHandlers.postDelayed(mLongTouchRuns,2000);


            }
        });

        int brightmax =currentState.getInt("maxbright",-1);
        if(brightmax!=-1){
            bSeekbar.setMax(brightmax);
            Log.d("MaxSavedBrightness", "VideoActivity: MaxBrightnessSet: "+brightmax);
        }
        bSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(FullscreenVideoActivity.this)) {
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                            brightnessCurrent=progress;
                            SharedPreferences.Editor editor = currentState.edit();
                            editor.putInt("brightness", brightnessCurrent);
                            editor.commit();
                        }
                    }else {
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                        brightnessCurrent=progress;
                        SharedPreferences.Editor editor = currentState.edit();
                        editor.putInt("brightness", brightnessCurrent);
                        editor.commit();

                    }
                } catch (Exception e) {
                    bSeekbar.setMax(progress-1);
                    Log.e("MaxNewBrightness", "onProgressChanged: MaxBrightnessChanged"+ (progress-1));
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putInt("maxbright", progress-1);
                    editor.commit();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mMediaController.hide();
                bSeekbar.setAlpha(1f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(FullscreenVideoActivity.this)) {
                        openAndroidPermissionsMenu();
                    }
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                vLongTouchHandlers.postDelayed(mLongTouchRuns,2000);


            }
        });







        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        vSeekbar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        vSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        bSeekbar.setProgress(Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0));

        editor = currentState.edit();
        editor.putBoolean("fullscreen",true);
        editor.commit();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        Log.d("FullscreenActivity", "onPause: Invoked");
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(FullscreenVideoActivity.this)) {
                brightnessCurrent= Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putInt("brightness", brightnessCurrent);
                editor.commit();
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessSystem);
            }
        }else {
            brightnessCurrent= Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putInt("brightness", brightnessCurrent);
            editor.commit();
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessSystem);

        }

        if(mVideoView!=null)
        isPlaying=mVideoView.isPlaying();

        Log.d(TAG, "onPause ");
        if (mVideoView != null && mVideoView.isPlaying()) {
            mSeekPosition = mVideoView.getCurrentPosition();
            SharedPreferences.Editor editor = currentState.edit();
            editor.putLong("currentPosition",mSeekPosition);
            editor.commit();
            Log.d(TAG, "onPause mSeekPosition=" + mSeekPosition);
            mVideoView.pause();
            mVideoView.stopPlayback();
            clearEqualizers();
        }else if(mVideoView!=null && !mVideoView.isPlaying()){

            mSeekPosition = mVideoView.getCurrentPosition();
            SharedPreferences.Editor editor = currentState.edit();
            editor.putLong("currentPosition",mSeekPosition);
            editor.commit();
            mVideoView.pause();
            mVideoView.stopPlayback();
            clearEqualizers();
        }

            currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            editor = currentState.edit();
            editor.putBoolean("fullscreen",false);
            editor.commit();
            Intent serviceIntent = new Intent(getApplicationContext(), HeadsetTriggerService.class);
            getApplicationContext().startService(serviceIntent);
            Log.d("HeadSetTriggerService", "onFullscreenVideoActivity: HeadsetWatchdogStarted");


        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(UI_OPTIONS);

        if( SonaHeartService.isServiceRunning ) {
            Log.e("KillServiceInvoked", "onStop: ServiceKilled");
            sendBroadcast(new Intent("stopMediaService"));
        }
        stopService(new Intent(getBaseContext(), HeadsetTriggerService.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(FullscreenVideoActivity.this)) {
                if(brightnessCurrent>-1) {
                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessCurrent);
                }
            }
        }else {
            if(brightnessCurrent>-1) {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessCurrent);
            }
        }

       if(isPlaying){
           mVideoView.setVideoURI(VIDEO_URL);
           mVideoView.start();
         mVideoView.seekTo(mSeekPosition);

           Log.e("OnResumeActivity", "onResume:VideoPlaybackStarted " );

       }else{

           mVideoView.setVideoURI(VIDEO_URL);
           mVideoView.seekTo(mSeekPosition);

           if (isFullscreen) {

               ConstraintLayout.LayoutParams lpv =
                       new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                               ConstraintLayout.LayoutParams.MATCH_PARENT);
               mVideoLayout.setLayoutParams(lpv);


               mVideoView.setMediaController(mMediaController);
               mMediaController.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

               FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
               params.gravity = Gravity.CENTER;
               mVideoView.setLayoutParams(params);

               rotateLayout.setAngle(90);
               rotateLayoutB.setAngle(90);
               ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT);
               vSeekbar.setLayoutParams(lp);
               bSeekbar.setLayoutParams(lp);
               videoParent.setBackgroundColor(getResources().getColor(R.color.black));

           }
       }

        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        editor = currentState.edit();
        editor.putBoolean("fullscreen",true);
        editor.commit();

        Log.d("HeadSetTriggerService", "onCommand: HeadsetWatchdogKilled");



    }


    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {

                mVideoView.setVideoURI(VIDEO_URL);
                mVideoView.requestFocus();
                if (mSeekPosition > 0) {
                    mVideoView.seekTo(mSeekPosition);
                }
                mVideoView.start();

                mMediaController.setTitle(Title);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState Position=" + mVideoView.getCurrentPosition());
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
        Log.d(TAG, "onRestoreInstanceState Position=" + mSeekPosition);
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {

            ConstraintLayout.LayoutParams lpv =
                    new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.MATCH_PARENT);
            mVideoLayout.setLayoutParams(lpv);


            mVideoView.setMediaController(mMediaController);
            mMediaController.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            mVideoView.setLayoutParams(params);

            rotateLayout.setAngle(90);
            rotateLayoutB.setAngle(90);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT);
            vSeekbar.setLayoutParams(lp);
            bSeekbar.setLayoutParams(lp);
            videoParent.setBackgroundColor(getResources().getColor(R.color.black));




        } else {
            ConstraintLayout.LayoutParams lpv =
                    new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT);
            mVideoLayout.setLayoutParams(lpv);

            ConstraintSet constraintSet=new ConstraintSet();
            constraintSet.clone(videoParent);
            constraintSet.connect(mVideoLayout.getId(), ConstraintSet.TOP, videoParent.getId(), ConstraintSet.TOP,0);
            constraintSet.connect(mVideoLayout.getId(), ConstraintSet.BOTTOM, videoParent.getId(), ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(videoParent);


            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            mVideoView.setLayoutParams(params);


            rotateLayout.setAngle(90);
            rotateLayoutB.setAngle(90);

            mVideoView.setMediaController(mMediaController);
            mMediaController.setLayoutParams(new FrameLayout.LayoutParams(cachedWidth, cachedHeight));


            if(cachedHeight<600) {
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((cachedHeight-100), ViewGroup.LayoutParams.WRAP_CONTENT);
                vSeekbar.setLayoutParams(lp);
                bSeekbar.setLayoutParams(lp);
            }else {

                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((500), ViewGroup.LayoutParams.WRAP_CONTENT);
                vSeekbar.setLayoutParams(lp);
                bSeekbar.setLayoutParams(lp);
            }

            videoParent.setBackgroundColor(getResources().getColor(R.color.black));



        }


    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        if(repeatABFlag){

            mRepeatABHandler.removeCallbacks(mRepeatABRunnable);
        }
                Log.d(TAG, "onPause UniversalVideoView callback");
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {

        isPlaying=true;
        vLongTouchHandlers.postDelayed(mLongTouchRuns,2000);
        videoParent.setBackgroundColor(getResources().getColor(R.color.black));

        if(repeatABFlag){
            mVideoView.seekTo(repeatStartTime);
            mRepeatABHandler.postDelayed(mRepeatABRunnable,1000);
        }
        Log.d(TAG, "onStart UniversalVideoView callback ID= "+mediaPlayer.getAudioSessionId());
        if( SonaHeartService.isServiceRunning ) {
            Log.e("KillServiceInvoked", "onStop: ServiceKilled");
            sendBroadcast(new Intent("stopMediaService"));
        }

    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {

        Log.d(TAG, "onBufferingStart UniversalVideoView callback");
    }




    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingEnd UniversalVideoView callback");


    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);
        } else {
           mSeekPosition= mVideoView.getCurrentPosition();
           mVideoView.stopPlayback();
            mRepeatABHandler.removeCallbacks(mRepeatABRunnable);
            clearEqualizers();
             currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
             editor = currentState.edit();
               editor.putBoolean("fullscreen",false);
            editor.commit();

            Intent intent = new Intent();
            intent.putExtra("seekPos", mSeekPosition);
            intent.putExtra("bright",brightnessCurrent);
            intent.putExtra("repeatABflag",repeatABFlag);
            intent.putExtra("repeatStartTime",repeatStartTime);
            intent.putExtra("repeatEndTime",repeatEndTime);
            Log.d("SeekPosSent", "onDestroy:seekpos= "+ mSeekPosition);
            setResult(RESULT_OK, intent);

            finish();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        clearEqualizers();
        mRepeatABHandler.removeCallbacks(mRepeatABRunnable);
        fistseek=true;
        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        editor = currentState.edit();
        editor.putBoolean("fullscreen",false);
        editor.commit();
        Log.d("FullScreenActivity", "onDestroy:Invoked! ");



    }




    public  void loadEqualizers(int num,int audioId) {


        EndEqualizers();

        mEqualizer = new Equalizer(1000, audioId);
        m = mEqualizer.getNumberOfPresets();
        bandRange=mEqualizer.getBandLevelRange()[1];
        music_styles = new String[m];
        for (int k = 0; k < m; k++) {
            music_styles[k] = mEqualizer.getPresetName((short) k);
            Log.d("Equalizers", "Names: " + music_styles[k]);
        }
        mEqualizer.usePreset((short) num);

        Log.d("EqualizerSet", "EqualizerIndex: "+num+" AudioId:"+audioId);



        mEqualizer.setEnabled(true);
        equalizerIndex = num;


        Log.d("Trable lavel ", "UpperLimit: "+mEqualizer.getBandLevelRange()[1]);
        Log.d("Trable lavel ", "LowerLimit: "+mEqualizer.getBandLevelRange()[0]);



    }

    public void switchEq(int index) {

        try {
            mEqualizer.usePreset((short) index);
            //mEqualizer.setEnabled(true);
            equalizerIndex = index;
            trableGain = 0;
            SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
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
                mEqualizer.setBandLevel((short) 4, val);
                mEqualizer.setBandLevel((short) 3, val);
            } else {

                switchEq(equalizerIndex);
            }


            SharedPreferences currentState = this.getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putInt("TrableValue", val);
            editor.commit();

        }catch (Throwable e){

            Log.e("TrableException", "Exception Raised in Trable: "+e );
        }

    }


    public void startEqualizers(int num,int audioId,MediaPlayer mediaPlayer){


        loadEqualizers(num,audioId);
        initBass(audioId,bassGain);
        initVirtualizer(audioId,virtualGain);
        initLoudnessEnhancer(audioId,loudGain);
        trableBoost(trableGain);
        initReverb(reverb ,mediaPlayer);


    }
    public void clearEqualizers(){
        EndEqualizers();
        EndBass();
        EndVirtual();
        EndLoudnessEnhancer();
        EndReverb();


    }

    public  void initBass(int audioID,int bassVal) {
        EndBass();

        try {

            bassBoost = new BassBoost(1000, audioID);
            short savestr = (short) bassVal;
            if (savestr > 0) {
                setBassBoostStrength(savestr);
                bassBoost.setEnabled(true);

            } else {
                setBassBoostStrength((short) 0);
                bassBoost.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void EndBass() {
        if (bassBoost != null) {
            bassBoost.release();
            bassBoost = null;

        }

    }

    public  void EndEqualizers(){
        if(mEqualizer!=null){
            mEqualizer.release();
            mEqualizer=null;}


    }

    public  void setBassBoostStrength(short strength) {
        if (bassBoost != null && bassBoost.getStrengthSupported() &&  strength >= 0) {
            try {
                if (strength <= 1000) {
                    bassBoost.setStrength(strength);
                    Log.d("BassBoostSet", "BassBoostStrength: "+strength);
                    SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
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
    }





    public  void initVirtualizer(int audioID,short strength) {
        EndVirtual();
        try {
            virtualizer = new Virtualizer(1000, audioID);
            short str =  strength;
            if (str > 0) {
                setVirtualizerStrength(str);
                virtualizer.setEnabled(true);
            }else {
                setVirtualizerStrength((short) 0);
                virtualizer.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void setVirtualizerStrength(short strength) {
        if (virtualizer != null && virtualizer.getStrengthSupported() && strength >= 0) {
            try {
                if (strength <= 1000) {
                    virtualizer.setStrength(strength);

                    Log.d("virtualizerSet", "setVirtualizerStrength: "+strength);
                    SharedPreferences currentState = this.getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
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

    }

    public static void EndVirtual() {
        if (virtualizer != null) {
            virtualizer.release();
            virtualizer = null;
        }
    }






    public  void initLoudnessEnhancer(int audioID,int loud) {
        EndLoudnessEnhancer();
        try {
            loudnessEnhancer = new LoudnessEnhancer(audioID);
            if (loud > 0) {
                setLoudnessEnhancerGain(loud);
                loudnessEnhancer.setEnabled(true);
            }else {
                setLoudnessEnhancerGain(0);
                loudnessEnhancer.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void setLoudnessEnhancerGain(int gain) {
        if (loudnessEnhancer != null && gain >= 0) {
            try {
                if (gain <= 1000) {
                    loudnessEnhancer.setTargetGain(gain);
                    Log.d("LoudNess command", "Value:"+gain);
                    SharedPreferences currentState = this.getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
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

    }

    public static void EndLoudnessEnhancer() {
        if (loudnessEnhancer != null) {
            loudnessEnhancer.release();
            loudnessEnhancer = null;
        }
    }

    public static void initReverb( short strength,MediaPlayer mediaPlayer) {
        EndReverb();
        try {
            if(strength!=0 ) {
                presetReverb = new PresetReverb(1, mediaPlayer.getAudioSessionId());
                presetReverb.setPreset(strength);
                presetReverb.setEnabled(true);
               // mediaPlayer.attachAuxEffect(presetReverb.getId());
               // mediaPlayer.setAuxEffectSendLevel(1.0f);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void EndReverb() {
        if (presetReverb != null) {
            presetReverb.setEnabled(false);
            presetReverb.release();
            presetReverb = null;
        }
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        Log.d("VolumeKeyPressed", "dispatchKeyEvent:Main: ");

        switch (keyCode) {

            case KeyEvent.KEYCODE_HEADSETHOOK:

                if (action == KeyEvent.ACTION_DOWN) {

                    if(mVideoView.isPlaying()){

                        mVideoView.pause();
                    }else {

                        mVideoView.start();
                    }
                }

                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {

                    vSeekbar.setAlpha(1f);
                    vLongTouchHandlers.postDelayed(mLongTouchRuns,2000);

                    if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) < mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
                        vSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1);
                }

                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {

                    vSeekbar.setAlpha(1f);
                    vLongTouchHandlers.postDelayed(mLongTouchRuns,2000);

                    if (vSeekbar.getProgress() > 0)
                        vSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 1);

                }
                return true;

            default:
                Log.d("VolumeKeyPressed", "dispatchKeyEvent:CaseDefault: ");
                return super.dispatchKeyEvent(event);


        }

    }




    final Runnable mLongTouchRuns = new Runnable() {
        @Override
        public void run() {


            vLongTouchHandlers.removeCallbacksAndMessages(null);

            vSeekbar.setAlpha(0.1f);
            bSeekbar.setAlpha(0.1f);

        }

    };

    final Runnable mRepeatABRunnable = new Runnable() {
        @Override
        public void run() {
            mRepeatABHandler.removeCallbacksAndMessages(null);

            if(mVideoView.getCurrentPosition()>=repeatEndTime){
                mVideoView.seekTo(repeatStartTime);

            }

            mRepeatABHandler.postDelayed(this,1000);

        }

    };

    private void openAndroidPermissionsMenu() {
        AlertDialog.Builder builder=new AlertDialog.Builder(FullscreenVideoActivity.this);
        builder.setTitle("Need Special Permission");
        builder.setMessage("Sona Player needs Write Setting permissions to control Brightness");
        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });
        builder.show();
    }

}
