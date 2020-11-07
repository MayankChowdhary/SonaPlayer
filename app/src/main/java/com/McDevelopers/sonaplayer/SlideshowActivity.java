package com.McDevelopers.sonaplayer;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SlideshowActivity extends AppCompatActivity {

    int UI_OPTIONS = View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;


  private   ImageView slideView;
  private int currentArtIndex;
    private int slideArtIndex;
    Bitmap albumArt;
    public   static ArrayList<String> ArtList = new ArrayList<>();
    private Handler albumArtChangerHandler =new Handler(Objects.requireNonNull(Looper.myLooper()));
    Animation zoomin, zoomout;
    private String startId;
    private static boolean handlerIsRunning=true;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        getWindow().getDecorView().setSystemUiVisibility(UI_OPTIONS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        ArtList=MainActivity.albumArtList;

        Intent i = getIntent();

        startId=ArtList.get(i.getIntExtra("artIndex",0));
        currentArtIndex=i.getIntExtra("artIndex",0);
        Log.e("receivedArtIndex", "randomArt: "+currentArtIndex);

        zoomin = AnimationUtils.loadAnimation(this, R.anim.zoom_in_default);
        zoomout = AnimationUtils.loadAnimation(this, R.anim.zoom_out_default);
        slideView=findViewById(R.id.slideshow_view);

        slideView.setImageBitmap(loadScaledBitmap());

        //changeAlbumArt();
        startAnimListener();
        slideView.startAnimation(zoomin);
        albumArtChangerHandler.postDelayed(mAlbumArtChanger,10000);
        changeAlbumArt(true);

        slideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(20);
                changeAlbumArt(false);
                zoomout.cancel();
                slideView.startAnimation(zoomin);
                if(handlerIsRunning) {
                    albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                    albumArtChangerHandler.postDelayed(mAlbumArtChanger, 10000);
                }


            }

        });

        slideView.setLongClickable(true);
        slideView.setHapticFeedbackEnabled(false);
        slideView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(20);

                if(handlerIsRunning){
                    albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                    handlerIsRunning=false;
                    SonaToast.setToast(getApplicationContext(),"*    Repeat Single    *",0);
                }else {
                    albumArtChangerHandler.postDelayed(mAlbumArtChanger,1000);
                    handlerIsRunning=true;
                    SonaToast.setToast(getApplicationContext(),"*     Repeat All     *",0);
                }


                return true;
            }});


    }



    /*private void changeAlbumArt(){

        try {
            slideView.setImageBitmap(loadScaledBitmap(false));


        } catch (Throwable e) {

            albumArt =BitmapFactory.decodeResource(getResources(), R.drawable.loading_art);

        }

    }*/
        Bitmap preloadBitmap;
    private void changeAlbumArt(boolean onlyLoad){

        try {

            if(!onlyLoad && preloadBitmap!=null){
                slideView.setImageBitmap(preloadBitmap);
            }
            slideArtIndex=currentArtIndex;

            GlideApp
                    .with(slideView.getContext())
                    .asBitmap()
                    .load(new File(Objects.requireNonNull(randomArt())))
                    .dontAnimate()
                    .error(R.drawable.main_art)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .apply(new RequestOptions())
                    .into(new CustomTarget<Bitmap>(1500,1500) {
                        @Override
                        public void onResourceReady(@NotNull Bitmap resource, Transition<? super Bitmap> transition) {
                            preloadBitmap=resource;
                            Log.d("GlideImageReady", "onResourceReady:Invoked ");

                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Remove the Drawable provided in onResourceReady from any Views and ensure
                            // no references to it remain.
                        }
                    });


            Log.d("ChangingAlbumArt", "changeAlbumArt: Invoked");
        } catch (Throwable e) {

            albumArt =null;

            Log.e("ExceptionRaised", "changeAlbumArt: "+e );
        }

    }

    private  String randomArt() {

        int max = ArtList.size();
      Random  rand = new Random();
        int newAlbumIndex=currentArtIndex;

        try {
            if(max>1) {

                if(max>50){
                    while (newAlbumIndex == currentArtIndex) {
                        newAlbumIndex = rand.nextInt(max);
                    }}
                else {

                    newAlbumIndex = rand.nextInt(max);
                }
                String newAlbum = ArtList.get(newAlbumIndex);
                currentArtIndex=newAlbumIndex;
                return newAlbum;
            }else
                currentArtIndex=-1;
            return null;
        }catch (Throwable e){

            Log.e("ExceptionRaised", "randomArt: "+e );
            currentArtIndex=-1;
            return null;
        }

    }


    Runnable mAlbumArtChanger = new Runnable() {
        @Override
        public void run() {

            albumArtChangerHandler.removeCallbacksAndMessages(null);

            if( ArtList.size()!=0) {

                changeAlbumArt(false);
                zoomout.cancel();
                slideView.startAnimation(zoomin);
                albumArtChangerHandler.postDelayed(mAlbumArtChanger,10000);

            }}};


    public  void startAnimListener(){

        zoomin.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

                    slideView.startAnimation(zoomout);

            }
        });

        zoomout.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

                    slideView.startAnimation(zoomin);

            }
        });


    }


    private Bitmap loadScaledBitmap(){

        Uri imageUri;

        imageUri = Uri.fromFile(new File(startId));

        ContentResolver resolver = getApplicationContext().getContentResolver();
        InputStream is;
        try {
            is = resolver.openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            Log.e("ExceptionRaised", "Image not found.", e);
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opts);

        // scale the image
        float maxSideLength = 2000;
        float scaleFactor = Math.min(maxSideLength / opts.outWidth, maxSideLength / opts.outHeight);
        // do not upscale!
        if (scaleFactor < 1) {
            opts.inDensity = 10000;
            opts.inTargetDensity = (int) ((float) opts.inDensity * scaleFactor);
        }
        opts.inJustDecodeBounds = false;

        try {
            assert is != null;
            is.close();
        } catch (IOException e) {
            // ignore
        }
        try {
            is = resolver.openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            Log.e("ExceptionRaised", "Image not found.", e);
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
        try {
            Objects.requireNonNull(is).close();
        } catch (IOException e) {
            // ignore
        }

        return bitmap;


    }

        @Override
        public void onBackPressed(){
            Intent intent = new Intent();
            intent.putExtra("artIndex", slideArtIndex);
            Log.e("slideSendArtIndex", "onBackPressed: "+slideArtIndex );
            Log.d("destroyInvoked", "onDestroy:slideshow ");
            setResult(RESULT_OK, intent);
            super.onBackPressed();

        }
    @Override
    protected void onDestroy(){

        handlerIsRunning=true;
        slideView.clearAnimation();
        albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);

        super.onDestroy();
    }

    @Override
    protected void onResume() {

        getWindow().getDecorView().setSystemUiVisibility(UI_OPTIONS);
        slideView.startAnimation(zoomin);
        albumArtChangerHandler.postDelayed(mAlbumArtChanger,10000);
        super.onResume();
    }

    @Override
    protected void onPause(){
       overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        slideView.clearAnimation();
        albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);


        super.onPause();
    }
}
