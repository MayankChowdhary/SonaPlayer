package com.McDevelopers.sonaplayer.AlbumListActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.McDevelopers.sonaplayer.Animations;
import com.McDevelopers.sonaplayer.ApplicationContextProvider;
import com.McDevelopers.sonaplayer.BuildConfig;
import com.McDevelopers.sonaplayer.GetPathFromUri;
import com.McDevelopers.sonaplayer.GlideApp;
import com.McDevelopers.sonaplayer.InfoTabDialog;
import com.McDevelopers.sonaplayer.MusicLibrary;
import com.McDevelopers.sonaplayer.R;
import com.McDevelopers.sonaplayer.SonaToast;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.signature.ObjectKey;
import com.roger.catloadinglibrary.CatLoadingView;
import com.roger.catloadinglibrary.GraduallyTextView;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.TagOptionSingleton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

import static com.McDevelopers.sonaplayer.ApplicationContextProvider.getContext;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public  class AlbumListActivity extends AppCompatActivity implements InfoTabDialog.InfoTabResult,MusicLibrary.albumUpdateListener {

    private ExpandableAlbumAdapter mAdapter;
    private  RecyclerView recyclerView;
    private VerticalRecyclerViewFastScroller fastScroller;
    static List<AlbumCategoryExpanded> albumzCategoryExpanded= Collections.emptyList();
    private SearchView searchView;
    private  TextView listHeader;
    private ImageButton upDown;
    private ImageButton upButton;
    private  CatLoadingView catLoadingView;
    private Handler mUpdateProgressHandler = new Handler();
    private RelativeLayout no_music_layout;

    private static boolean checkFlags=false;
    private ConstraintLayout parentLayout;
    private RelativeLayout optionDialog;
    private CheckBox markAllChecker;
    TextView markCountText;
    Button closeDialogBtn;
    LinearLayout sendBtnLayout;
    LinearLayout deleteBtnLayout;
    LinearLayout playlistBtnLayout;
    LinearLayout queueBtnLayout;
    private InputMethodManager imm;
    private static Uri sdCardUri;
    AlertDialog.Builder builder=null;
    AlertDialog alertDialog=null;
    ProgressDialog progressDialog;
    private boolean supressUpdate=false;
    SharedPreferences currentState;
    private PopupWindow mPopupWindow;
    private boolean deleteSingleFlag=false;
    private  String deletePath=null;
    private String deleteMediaId=null;

    private static boolean TagEditFlag=false;
    private InfoTabDialog infoTabDialog;

    private static Bundle bundleMetaTemp;
    private static String songDataTemp;
    private static boolean isVideoFileTemp;
    private static String mediaIdTemp;
    private static Bundle bundleTagsTemp;
    private static Bundle bundleRestoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        getWindow().setBackgroundDrawable(null);
        albumzCategoryExpanded=AlbumLibrary.albumxCategoryExpand;
        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        String UriRaw=currentState.getString("SDCardUri",null);
        if(!TextUtils.isEmpty(UriRaw)) {
            sdCardUri = Uri.parse(UriRaw);
            Log.d("MediaListActivity", "onCreate: SdcardUri:" + UriRaw);
        }

        // activity = this;
        listHeader=findViewById(R.id.album_list_header);
        no_music_layout=findViewById(R.id.no_mucic_view);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_nested);
        fastScroller = findViewById(R.id.fast_scroller_album);
        upDown=findViewById(R.id.layout_up_down);
        upButton=findViewById(R.id.layout_up);

        checkFlags=false;
        supressUpdate=false;

        if(!MusicLibrary.isAlbumUpdated) {
            initAdapter(AlbumLibrary.albumxDummyData);
            recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
            fastScroller.setRecyclerView(recyclerView);
            recyclerView.setVisibility(View.INVISIBLE);
            catLoadingView = new CatLoadingView();
            catLoadingView.setCanceledOnTouchOutside(false);
            catLoadingView.setCancelable(false);
            catLoadingView.show(getSupportFragmentManager(), "");
            listHeader.setText("Albums ( N/A )");
           initCatLoadingView();


        }else if(albumzCategoryExpanded.isEmpty()) {

           initAdapter(AlbumLibrary.albumxDummyData);
            recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
            fastScroller.setRecyclerView(recyclerView);
            listHeader.setText("Albums ( N/A )");


        }else {
            initAdapter(albumzCategoryExpanded);
            recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
            fastScroller.setRecyclerView(recyclerView);
        }

        getWindow().setBackgroundDrawable(null);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setupSearch();


            upDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(20);

                    if(albumzCategoryExpanded.size()!=0 && mAdapter!=null) {

                        for (int i = recyclerView.getAdapter().getItemCount() - 1; i >= 0; i--) {
                            try {
                                if (!mAdapter.isGroupExpanded(i)) {
                                    continue;
                                }
                                mAdapter.toggleGroup(i);
                                Log.d("ToatlItemCount", "Loop1: " + recyclerView.getAdapter().getItemCount());
                            } catch (Throwable e) {
                                Log.e("ExceptionRaised", "onClick: " + e);
                            }
                        }

                        for (int i = mAdapter.getGroups().size() - 1; i >= 0; i--) {
                            if (mAdapter.isGroupExpanded(i)) {
                                continue;
                            }
                            mAdapter.toggleGroup(i);
                            Log.d("ToatlItemCount", "Loop2: " + recyclerView.getAdapter().getItemCount());

                        }
                    }

                }
            });


            upButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(20);

                    if(albumzCategoryExpanded.size()!=0 && mAdapter!=null) {
                        for (int i = recyclerView.getAdapter().getItemCount() - 1; i >= 0; i--) {
                            try {
                                if (!mAdapter.isGroupExpanded(i)) {
                                    continue;
                                }
                                mAdapter.toggleGroup(i);
                                Log.d("ToatlItemCount", "Loop3: " + recyclerView.getAdapter().getItemCount());

                            } catch (Throwable e) {
                                Log.e("ExceptionRaised", "onClick: " + e);
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }
            });

    }


    @Override
    public  void onPause(){
        super.onPause();

    }

    @Override
    public void onResume(){
        super.onResume();
        MusicLibrary.registerAlbumUpdateListener(this);

    }

    @Override
    public void onStart(){

        super.onStart();
    }

    @Override
    public void onBackPressed() {

        if(checkFlags)
            removeMarkDialog();
        else
            super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

    @Override
    public  void onDestroy() {
        MusicLibrary.registerAlbumUpdateListener(null);
        recyclerView.setAdapter(null);
        mAdapter=null;
        recyclerView=null;
        alertDialog=null;
        builder=null;
        Runtime.getRuntime().gc();
        System.gc();
        super.onDestroy();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAdapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    sdCardUri = data.getData();
                    if(sdCardUri!=null) {
                        final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(sdCardUri, takeFlags);

                        Log.d("SDcardUri", "onActivityResult: Uri:  " + sdCardUri.toString());
                        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = currentState.edit();
                        editor.putString("SDCardUri", sdCardUri.toString());
                        editor.putBoolean("canReadSD", true);
                        editor.commit();
                        if (TagEditFlag) {
                            SonaToast sonaToast = new SonaToast();
                            sonaToast.setToast(getApplicationContext(), "Permission Granted", 0);
                            TagEditFlag = false;

                        } else{
                            if (!deleteSingleFlag)
                            showProgressDialog();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                if (deleteSingleFlag) {
                                    deleteItem(deletePath, deleteMediaId);
                                } else {
                                    proceedToDelete();
                                }
                            }
                        }, 500);

                    }
                    }
                }
                break;

            case 2:

                if (resultCode == RESULT_OK && data!=null) {

                    Log.e("UriReceived", "onActivityResult: AlbumUri: "+data.getData().toString() );
                    Uri selectedImage = data.getData();
                    GetPathFromUri getPathFromUri=new GetPathFromUri();
                    String selectedImageX= getPathFromUri.GetPathFromDocUri(getContext(),selectedImage);
                    Log.d("UriToPath", "onActivityResult: "+selectedImageX);
                    performCrop( Uri.fromFile(new File(selectedImageX)),selectedImageX);

                }
                break;

            case 3:
                if (resultCode == RESULT_OK && data!=null) {

                    try {

                        if(infoTabDialog!=null)
                            infoTabDialog.dismiss();
                        Uri uri = data.getData();
                        Log.e("CroppedUriReceived", "onActivityResult:Uri: " + uri.toString());
                        Bitmap selectedBitmap = decodeUriAsBitmap(uri);
                        inflateInfoWindow(getApplicationContext(), bundleMetaTemp, songDataTemp, isVideoFileTemp, false, mediaIdTemp, bundleTagsTemp, true, selectedBitmap, bundleRestoreText);

                    }catch (Throwable e){
                        SonaToast sonaToast=new SonaToast();
                        sonaToast.setToast(getApplicationContext(),"Something Went Wrong",0);
                        inflateInfoWindow(getApplicationContext(),bundleMetaTemp,songDataTemp,isVideoFileTemp,false,mediaIdTemp,bundleTagsTemp,true,null,bundleRestoreText);

                    }

                }
                break;
        }
    }

    private Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            new File(uri.getPath()).delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
    private void setupSearch() {
         searchView = findViewById(R.id.album_search);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listHeader.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(),"SearchView Clicked",Toast.LENGTH_LONG).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processQuery(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processQuery(newText);
                return false;
            }

        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                listHeader.setVisibility(View.VISIBLE);
             initAdapter(albumzCategoryExpanded);
                if(checkFlags){
                    markCountText.setText(mAdapter.getMarkCount());
                }
                return false;
            }
        });



    }

    private  void processQuery(String query) {
        // in real app you'd have it instantiated just once
        List<AlbumCategoryExpanded> result = new ArrayList<>();
        List<Albums> songTemp;

   try {
       // case insensitive search
       for (AlbumCategoryExpanded song : albumzCategoryExpanded) {

           songTemp = song.getSongName();

           if (song.getName().toLowerCase().contains(query.toLowerCase())) {
               result.add(song);
           } else {
               for (Albums songz : songTemp) {
                   if (songz.getName().toLowerCase().contains(query.toLowerCase())) {
                       result.add(song);

                       Log.d("SearchingChilds", "processQuery: Iinvoked");
                       break;
                   }


               }
           }

           initAdapter(result);
           if (checkFlags) {
               markCountText.setText(mAdapter.getMarkCount());
           }
           if (result.size() == 0) {

               fastScroller.setEnabled(false);
           } else {

               fastScroller.setEnabled(true);
           }
       }
   }catch (Throwable e){

       if (!searchView.isIconified()) {
           searchView.onActionViewCollapsed();
       }
   }


    }

    @Override
    public void albumUpdateEvent(){

        if(albumzCategoryExpanded.size()==0) {
            catLoadingView.onStop();
            catLoadingView.dismissAllowingStateLoss();
            catLoadingView.onDestroy();
            catLoadingView = null;
            no_music_layout.setVisibility(View.VISIBLE);
            listHeader.setText("Albums ( N/A )");

        } else {

            catLoadingView.onStop();
            catLoadingView.dismissAllowingStateLoss();
            catLoadingView.onDestroy();
            catLoadingView = null;
            no_music_layout.setVisibility(View.GONE);
            initAdapter(albumzCategoryExpanded);

        }

    }

    private void initCatLoadingView(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!MusicLibrary.isAlbumUpdated ) {
                    try {
                        GraduallyTextView mGraduallyTextView = catLoadingView.getDialog().getWindow().getDecorView().findViewById(com.roger.catloadinglibrary.R.id.graduallyTextView);
                        final ViewGroup.MarginLayoutParams lpt = (ViewGroup.MarginLayoutParams) mGraduallyTextView.getLayoutParams();
                        lpt.width =  220;
                        lpt.height = 100;
                        //  lpt.leftMargin = 80;
                        //lpt.bottomMargin = 20;
                        mGraduallyTextView.setLayoutParams(lpt);
                        Log.d("CatLoadingMargin", "run: MarginSetInvoked ");
                    }catch(Throwable e){

                        Log.e("ExceptionRaised!", "run: AlbumListActivity");
                    }
                }
            }
        }, 500);
    }



  private void initAdapter(final List<AlbumCategoryExpanded> albumCategoryData){

      recyclerView.setAdapter(null);
      mAdapter=null;

      mAdapter = new ExpandableAlbumAdapter(albumCategoryData);
      recyclerView.setAdapter(mAdapter);
      recyclerView.setLayoutManager(new LinearLayoutManager(AlbumListActivity.this));
      recyclerView.setHasFixedSize(true);
      recyclerView.setItemViewCacheSize(50);
      recyclerView.setDrawingCacheEnabled(true);
      recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);



      if ( !MusicLibrary.isAlbumUpdated) {
          fastScroller.setEnabled(false);
          recyclerView.setVisibility(View.INVISIBLE);
          listHeader.setText("Albums ( N/A )");
      }else if (!albumzCategoryExpanded.isEmpty() ){
          no_music_layout.setVisibility(View.GONE);
          recyclerView.setVisibility(View.VISIBLE);
          fastScroller.setEnabled(true);
          listHeader.setText("Albums ( "+albumCategoryData.size()+" )");
      }else {
          fastScroller.setEnabled(false);
          recyclerView.setVisibility(View.INVISIBLE);
          no_music_layout.setVisibility(View.VISIBLE);
          listHeader.setText("Albums ( N/A )");

      }


      mAdapter.setOnGroupLongClickListenerX(new ExpandableAlbumAdapter.OnGroupLongClickListenerX() {
          @Override
          public void OnGroupLongClick(ExpandableGroup group, int flatPosition) {

              Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
              vb.vibrate(50);
              if(!checkFlags) {
                  checkFlags=true;
                  mAdapter.setCheckFlag(true);
                  inflateMarkDialog();
              }

              mAdapter.setMarkGroup(group);

                  if(markAllChecker.isChecked()) {
                      markAllChecker.setChecked(false);
                  }
                  markCountText.setText(mAdapter.getMarkCount());

              Log.d("GroupLongClick", "OnGroupLongClick:On AlbumListActivity ");
          }
      });


      mAdapter.setOnItemClickListener(new ExpandableAlbumAdapter.OnItemClickListener() {
          @Override
          public void OnItemClick(ExpandableGroup group, int position) {

              AlbumCategoryExpanded albumCategoryExpanded = (AlbumCategoryExpanded) group;
              List<Albums> albums = albumCategoryExpanded.getSongName();

              if(!checkFlags) {
                  imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                  imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                  Intent intent = new Intent();
                  intent.putExtra("mediaId", albums.get(position).getMediaIds());
                  setResult(RESULT_OK, intent);
                  finish();
              }else {

                      mAdapter.setMarkBox(group,position);
                      markCountText.setText(mAdapter.getMarkCount());

                  if(markAllChecker.isChecked()) {
                      markAllChecker.setChecked(false);
                  }
              }


          }
      });

      mAdapter.setOnItemLongClickListener(new ExpandableAlbumAdapter.OnItemLongClickListener() {
          @Override
          public void OnItemLongClick(ExpandableGroup group, int position) {
              Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
              vb.vibrate(50);
            //  AlbumCategoryExpanded albumCategoryExpanded = (AlbumCategoryExpanded) group;
             // List<Albums> albums = albumCategoryExpanded.getSongName();


              if(!checkFlags) {
                  checkFlags=true;
                  mAdapter.setCheckFlag(true);
                  inflateMarkDialog();
              }

                  mAdapter.setMarkBox(group, position);
                  markCountText.setText(mAdapter.getMarkCount());

                  if (markAllChecker.isChecked()) {
                      markAllChecker.setChecked(false);
                  }

          }
      });


      mAdapter.setOnOptionClickListener(new ExpandableAlbumAdapter.OnOptionClickListener() {
          @Override
          public void OnOptionClick(ExpandableGroup group, int position) {

              if(checkFlags)
                  removeMarkDialog();

              Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
              if(vb!=null)
                  vb.vibrate(30);

              AlbumCategoryExpanded albumCategoryExpanded = (AlbumCategoryExpanded) group;
              List<Albums> albums = albumCategoryExpanded.getSongName();
              String title=albums.get(position).getName();
              String subTitle=albumCategoryExpanded.getName()+" - "+albums.get(position).getArtist();
              String meta=timeConvert(albums.get(position).getDuration())+" | "+(getSongFormat(albums.get(position).getData())+" | "+(getStringSizeLengthFile(albums.get(position).getData())));

              inflateOptionMenu(getApplicationContext(),title,subTitle,meta,albums.get(position).getData(),albums.get(position).getAlbumIds(),position,albums.get(position).getMediaIds(),group);

          }
      });

  }



    private void inflateMarkDialog(){

        parentLayout = findViewById(R.id.album_list_parent);
        View v = getLayoutInflater().inflate(R.layout.list_option_dialog, parentLayout, false);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        //TransitionManager.beginDelayedTransition(parentLayout);
        v.setVisibility(View.INVISIBLE);
        parentLayout.addView(v, lp);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parentLayout);
        constraintSet.connect(v.getId(), ConstraintSet.END, parentLayout.getId(), ConstraintSet.END, 25);
        constraintSet.connect(v.getId(), ConstraintSet.START, parentLayout.getId(), ConstraintSet.START, 0);
        constraintSet.connect(v.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM, 15);
        constraintSet.applyTo(parentLayout);

        Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_up);
        ViewGroup hiddenPanel = (ViewGroup)findViewById(R.id.list_option_parent);
        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.VISIBLE);
        initMarkDialog();
    }

    private void initMarkDialog(){
        markAllChecker=findViewById(R.id.mark_all);
        markCountText=findViewById(R.id.mark_count);
        closeDialogBtn=findViewById(R.id.close_list_option);
        sendBtnLayout=findViewById(R.id.option_send_parent);
        deleteBtnLayout=findViewById(R.id.option_delete_parent);
        playlistBtnLayout=findViewById(R.id.add_playlist_parent);
        queueBtnLayout=findViewById(R.id.add_queue_parent);

        markCountText.setText(mAdapter.getMarkCount());

        markAllChecker.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(50);
                // you might keep a reference to the CheckBox to avoid this class cast
                boolean checked = ((CheckBox)v).isChecked();
                mAdapter.setMarkAll(checked);
                    markCountText.setText(mAdapter.getMarkCount());
                    if(checked)
                    upDown.performClick();
                    else
                        upButton.performClick();
            }

        });

        markAllChecker.setLongClickable(true);
        markAllChecker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(50);
                // you might keep a reference to the CheckBox to avoid this class cast
                markAllChecker.setChecked(false);

                mAdapter.setMarkAll(false);
                    markCountText.setText(mAdapter.getMarkCount());
                    upButton.performClick();

                return true;
            }});

        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(50);
                removeMarkDialog();
            }
        });

        playlistBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(50);
                SonaToast sonaToast=new SonaToast();
                sonaToast.setToast(getApplicationContext(),"Currently Not Available",0);
            }
        });

        queueBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(50);

                SonaToast sonaToast=new SonaToast();
                sonaToast.setToast(getApplicationContext(),"Currently Not Available",0);
            }
        });


        sendBtnLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(mAdapter.getmarkedNumCount()>0)
                    shareMedia();
                else {
                    Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(20);
                    SonaToast sonaToast=new SonaToast();
                    sonaToast.setToast(getApplicationContext(),"Please Select media First",0);
                }

            }});

        deleteBtnLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mAdapter.getmarkedNumCount()==0){
                    Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(20);
                    SonaToast sonaToast=new SonaToast();
                    sonaToast.setToast(getApplicationContext(),"Please Select media First",0);
                    return;
                }
                Log.e("DeleteButtonClicked", "onClick: Invoked");
                AlertDialog.Builder builder1 = new AlertDialog.Builder(new ContextThemeWrapper(AlbumListActivity.this, R.style.myDialog));
                builder1.setTitle("Confirm Delete ?");
                builder1.setMessage("         Selected: "+mAdapter.getmarkedNumCount());
                builder1.setCancelable(true);
                builder1.setIcon(R.drawable.delete_icons);
                builder1.setPositiveButton(
                        "DELETE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                showProgressDialog();
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        proceedToDelete();
                                    }}, 500);

                            }
                        });

                builder1.setNegativeButton(
                        "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog  alertDialog1 = builder1.create();
                alertDialog1.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                alertDialog1.show();
            }});
    }

    private void removeMarkDialog(){
        checkFlags=false;
        optionDialog=findViewById(R.id.list_option_parent);
        slideDown(optionDialog);
        parentLayout.removeView(optionDialog);
        markAllChecker=null;
        markCountText=null;
        closeDialogBtn=null;
        mAdapter.setCheckFlag(false);
        mAdapter.notifyDataSetChanged();

    }



    private void shareMedia(){
        HashMap<String, String> slectedItems =mAdapter.getMarkedFileList();
            Log.d("Entered In adapter", "shareMedia: ListStyle 1 Invoked ");
        if(slectedItems==null || slectedItems.size()==0) {
            Log.e("Empty Media List", "shareMedia: Returned!!" );
            SonaToast sonaToast=new SonaToast();
            sonaToast.setToast(getApplicationContext(),"Operation Failed! Something Went Wrong",1);
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("*/*");
        ArrayList<Uri> uriFileList = new ArrayList<Uri>();
        Uri uri=null;
        for(Map.Entry<String, String> entry : slectedItems.entrySet() ) {
            String path=entry.getValue();
            File file = new File(path);
            Log.d("FilePacked", "path:"+path);
            uri = FileProvider.getUriForFile(getApplicationContext(), AlbumListActivity.this.getPackageName(), file);
            AlbumListActivity.this.grantUriPermission(AlbumListActivity.this.getPackageManager().toString(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uriFileList.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriFileList);
        AlbumListActivity.this.startActivity(Intent.createChooser(intent, "Share Media via"));


    }


    private void getSdCardUri(){

        if(builder==null) {
            builder = new AlertDialog.Builder(AlbumListActivity.this);
            builder.setTitle("Need SDCard Write Permission");
            builder.setMessage("Open File Manager > Choose SDCard > Tap SELECT to Grant Permission");
            builder.setPositiveButton("Open File Manager", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION|Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                    startActivityForResult(intent, 1);
                }
            });

            alertDialog = builder.create();
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            alertDialog.show();
        }else {

            if(!alertDialog.isShowing()){
                //if its visibility is not showing then show here
                alertDialog.show();
            }
        }
    }

    private void proceedToDelete(){

        if(deleteSingleFlag) {
            deleteSingleFlag = false;
            deleteMediaId = null;
            deletePath = null;
        }

        HashMap<String, String> slectedItems =mAdapter.getMarkedFileList();

        if(slectedItems==null || slectedItems.size()==0) {
            Log.e("Empty Media List", "shareMedia: Returned!!" );
            SonaToast sonaToast=new SonaToast();
            sonaToast.setToast(getApplicationContext(),"Operation Failed! Something Went Wrong",1);
            return;
        }

        for(Map.Entry<String, String> entry : slectedItems.entrySet() ) {
            String path =entry.getValue();
            String mediaId=entry.getKey();
            File file = new File(path);
            boolean isSuccess = file.delete();
            if(isSuccess){
                long longMediaId = Long.valueOf(mediaId);

                Uri mediaContentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        longMediaId);
                getContentResolver().delete(mediaContentUri, null, null);

            } else {
                Log.d("FilePacked", "path:" + path);
                if (!deleteMedia(sdCardUri, file, mediaId)) {
                    supressUpdate = true;
                    break;
                }
            }
            supressUpdate=false;
        }

        if(!supressUpdate)
            updateLibrary();
        else
            supressUpdate=false;


    }

    private boolean deleteMedia(Uri sdCardUri ,File file, String mediaId){
        DocumentFile documentFile=null;
        try {


            if( sdCardUri!=null)
                documentFile = DocumentFile.fromTreeUri(this, sdCardUri);



            String[] parts = (file.getPath()).split("\\/");

            // findFile method will search documentFile for the first file
            // with the expected `DisplayName`

            // We skip first three items because we are already on it.(sdCardUri = /storage/extSdCard)
            for (int i = 3; i < parts.length; i++) {
                if (documentFile != null) {
                    documentFile = documentFile.findFile(parts[i]);
                }
            }

            if (documentFile == null) {

                // Toast.makeText(getApplicationContext(),"Operation Failed! Please Select Correct SDCard",Toast.LENGTH_LONG);
                progressDialog.dismiss();
                getSdCardUri();
                // File not found on tree search
                // User selected a wrong directory as the sd-card
                // Here must inform user about how to get the correct sd-card
                // and invoke file chooser dialog again.

                return false;

            } else {

                if (documentFile.delete()) {// if delete file succeed
                    // Remove information related to your media from ContentResolver,
                    // which documentFile.delete() didn't do the trick for me.
                    // Must do it otherwise you will end up with showing an empty
                    // ImageView if you are getting your URLs from MediaStore.
                    //

                    long longMediaId = Long.valueOf(mediaId);

                    Uri mediaContentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            longMediaId);
                    getContentResolver().delete(mediaContentUri, null, null);

                    if(deleteSingleFlag) {
                        deleteSingleFlag = false;
                        deleteMediaId = null;
                        deletePath = null;
                    }
                }


            }
        }catch (Throwable e) {
            e.printStackTrace();
            progressDialog.dismiss();
            getSdCardUri();
            return false;
            // Log.e("Exception Raised", "deleteMedia: "+e);
        }
        return true;
    }

    private void showProgressDialog(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Deleting Media Files");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
            progressDialog.show();

    }
    public void slideDown(View view){
        try {
            TranslateAnimation animate = new TranslateAnimation(
                    0,                 // fromXDelta
                    0,                 // toXDelta
                    0,                 // fromYDelta
                    view.getHeight()); // toYDelta
            animate.setDuration(400);
            animate.setFillAfter(true);
            view.startAnimation(animate);
            view.setVisibility(View.GONE);
        }catch (Throwable e){

            Log.e("slideDownException", "slideDown:  "+e);
        }
    }


    private void updateLibrary(){
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
        }
        listHeader.setText("Albums ( N/A )");
        listHeader.setVisibility(View.VISIBLE);

        progressDialog.setMessage("Updating Library...");
        sendBroadcast(new Intent("refreshLibrary"));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                removeMarkDialog();

                if (!searchView.isIconified()) {
                    searchView.onActionViewCollapsed();
                }
                progressDialog.dismiss();
                initAdapter(AlbumLibrary.albumxDummyData);
                alertDialog=null;
                builder=null;
                Runtime.getRuntime().gc();
                recyclerView.setVisibility(View.INVISIBLE);
                catLoadingView = new CatLoadingView();
                catLoadingView.setCanceledOnTouchOutside(false);
                catLoadingView.setCancelable(false);
                catLoadingView.show(getSupportFragmentManager(), "");
               initCatLoadingView();

                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(20);
                SonaToast.setToast(getApplicationContext(),"Media Files Deleted",1);
            }}, 2000);

    }



    private  void  inflateOptionMenu(final Context context, String title, String subtitle, String meta, final String songData,final String imageId, final int position, final String mediaId,final ExpandableGroup group) {

        Typeface currentTypeface=getTypeface();

        parentLayout = findViewById(R.id.album_list_parent);
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup_window_layout,null);
       final TextView headerTitle=customView.findViewById(R.id.popup_header_title);
       final TextView headerSubTitle=customView.findViewById(R.id.popup_header_subtitle);
        TextView headerMeta=customView.findViewById(R.id.popup_header_meta);
        TextView popup_delete=customView.findViewById(R.id.popup_delete_text);
        TextView popup_send=customView.findViewById(R.id.popup_send_text);
        TextView popup_mark=customView.findViewById(R.id.popup_mark_text);
        TextView popup_info=customView.findViewById(R.id.popup_info_text);
        TextView popup_ringtone=customView.findViewById(R.id.popup_ringtone_text);
        ImageView headerIcon=customView.findViewById(R.id.popup_header_icon);

        headerTitle.setTypeface(currentTypeface);
        headerSubTitle.setTypeface(currentTypeface);
        headerMeta.setTypeface(currentTypeface);
        popup_delete.setTypeface(currentTypeface);
        popup_send.setTypeface(currentTypeface);
        popup_mark.setTypeface(currentTypeface);
        popup_info.setTypeface(currentTypeface);
        popup_ringtone.setTypeface(currentTypeface);

        headerTitle.setText(title);
        headerSubTitle.setText(subtitle);
        headerMeta.setText(meta);

        headerTitle.setSelected(false);
        headerSubTitle.setSelected(false);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                headerSubTitle.setSelected(true);
                headerTitle.setSelected(true);
            }
        }, 2000);
        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        mPopupWindow.setElevation(5.0f);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.dialog_animation);
        mPopupWindow.showAtLocation(parentLayout, Gravity.CENTER,0,0);

         DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
            GlideApp
                    .with(headerIcon.getContext())
                    .asBitmap()
                    .load(songData)
                    .transition(withCrossFade(factory))
                    .signature(new ObjectKey(new File(songData).lastModified()))
                    .placeholder(R.drawable.artist)
                    .error(R.drawable.artist)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(headerIcon);




        popup_mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                performMark(group,position);
            }
        });

        popup_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(20);
                mPopupWindow.dismiss();
                sendItem(songData);
            }
        });

        popup_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(20);
                mPopupWindow.dismiss();
                deleteItem(songData,mediaId);
            }
        });

        popup_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builderR = new AlertDialog.Builder(new ContextThemeWrapper(AlbumListActivity.this, R.style.myDialog));
                builderR.setMessage("Confirm set as ringtone?");
                builderR.setCancelable(true);

                builderR.setPositiveButton(
                        "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                mPopupWindow.dismiss();
                                Uri ringUri = Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + mediaId);
                                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, ringUri);

                                SonaToast sonaToast = new SonaToast();
                                sonaToast.setToast(getApplicationContext(), "Ringtone Set", 0);

                            }
                        });

                builderR.setNegativeButton(
                        "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });


                AlertDialog alertR = builderR.create();
                alertR.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                alertR.show();
                }

        });

        popup_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mediaTags =getSongList(mediaId);
                Bundle bundleMeta = getInfoBundleMeta(songData,false);
                Bundle bundleTags = getInfoBundleTags(songData,imageId,mediaTags[0],mediaTags[1],mediaTags[2],mediaTags[3],mediaTags[4]);
                Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(20);
                inflateInfoWindow(getApplicationContext(),bundleMeta,songData,false,true,mediaId,bundleTags,false,null,null);
                mPopupWindow.dismiss();



            }
        });
    }

    private void inflateInfoWindow(Context context, final Bundle bundleMeta, final String songPath, final boolean isVideoFile, boolean isFirstTab, String mediaId, Bundle bundleTags, boolean isScrollEnd, Bitmap albumCropped, Bundle bundleRestoreText){
        TagEditFlag=false;

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        FragmentManager fm = getSupportFragmentManager();
        infoTabDialog = InfoTabDialog.newInstance("Info Tag Dialog",context,bundleMeta,songPath,isVideoFile,isFirstTab,imm,mediaId,bundleTags,isScrollEnd,albumCropped,bundleRestoreText);
        infoTabDialog.show(fm, "Sona Fonts");

    }

    private Typeface getTypeface(){
        Typeface face;
        if(!ApplicationContextProvider.systemFont) {
            face = Typeface.createFromAsset(getAssets(),
                    ApplicationContextProvider.getFontPath());
        }else {

            face=Typeface.DEFAULT;
        }

        return face;
    }

    private static Uri getAlbumUri(String mediaID) {

        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

        Log.d("AlbumArtUri", ContentUris.withAppendedId(sArtworkUri, Long.parseLong(mediaID)).toString());
        return ContentUris.withAppendedId(sArtworkUri, Long.parseLong(mediaID));

    }



    private void performMark(ExpandableGroup group, int position) {

        Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(50);
        if (!checkFlags) {
            checkFlags = true;
                mAdapter.setCheckFlag(true);

            inflateMarkDialog();
        }

        mAdapter.setMarkBox(group,position);
        markCountText.setText(mAdapter.getMarkCount());

    }

    private void sendItem( String path){

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("*/*");
        Uri uri=null;
        File file = new File(path);
        Log.d("FilePacked", "path:"+path);
        uri = FileProvider.getUriForFile(getApplicationContext(), AlbumListActivity.this.getPackageName(), file);
        AlbumListActivity.this.grantUriPermission(AlbumListActivity.this.getPackageManager().toString(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        AlbumListActivity.this.startActivity(Intent.createChooser(intent, "Share Media via"));
    }

    private void deleteItem(final String path,final String mediaId) {

        Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(20);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(new ContextThemeWrapper(AlbumListActivity.this, R.style.myDialog));
        builder1.setTitle("Confirm Delete ?");
        builder1.setMessage("         Selected: " + 1);
        builder1.setCancelable(true);
        builder1.setIcon(R.drawable.delete_icons);
        builder1.setPositiveButton(
                "DELETE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        showProgressDialog();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {


                                File file = new File(path);
                                boolean isSuccess = file.delete();
                                if(isSuccess){
                                    long longMediaId = Long.parseLong(mediaId);

                                    Uri mediaContentUri = ContentUris.withAppendedId(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            longMediaId);
                                    getContentResolver().delete(mediaContentUri, null, null);

                                    updateLibrary();

                                } else {
                                    deleteSingleFlag=true;
                                    deletePath=path;
                                    deleteMediaId=mediaId;
                                    Log.d("FilePacked", "path:" + path);
                                    if (deleteMedia(sdCardUri, file, mediaId)) {
                                        updateLibrary();
                                    }
                                }
                            }
                        }, 500);

                    }
                });

        builder1.setNegativeButton(
                "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog1 = builder1.create();
        alertDialog1.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog1.show();
    }

    private static String getSongFormat(String data){

        return  ((data).substring((data.lastIndexOf(".") + 1)).toUpperCase());
    }

    @SuppressLint("DefaultLocale")
    private String timeConvert(long durationX) {
        int duration=(int)durationX;

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

    public  String getStringSizeLengthFile(String data) {


        File file = new File(data);

// Get the number of bytes in the file
        long size = file.length();
        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;


        if(size < sizeMo)
            return df.format(size / sizeKb)+ " KB";
        else if(size < sizeGo)
            return df.format(size / sizeMo) + " MB";
        else if(size < sizeTerra)
            return df.format(size / sizeGo) + " GB";

        return "";
    }

    private  String[] getSongList(String mediaId){

        long mediaIdL=Long.parseLong(mediaId);
        String[] mediaStoreTags=new String[8];

        String selection = "is_music != 0";
        selection = selection + " and _id = " + mediaIdL;

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DISPLAY_NAME,

        };

        Cursor cursor = null;

        try {
            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor =ApplicationContextProvider.getContext().getContentResolver().query(uri, projection, selection, null, null);
            if (cursor != null) {
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    mediaStoreTags[0]=cursor.getString(0);
                    mediaStoreTags[1]=cursor.getString(1);
                    mediaStoreTags[2]=cursor.getString(2);
                    mediaStoreTags[3]=cursor.getString(3);
                    mediaStoreTags[4]=cursor.getString(4);
                    mediaStoreTags[5]=cursor.getString(5);
                    mediaStoreTags[6]=cursor.getString(6);
                    mediaStoreTags[7]=cursor.getString(7);

                    Log.d("MediaStore Retrieved", "Title:"+cursor.getString(0));
                    Log.d("MediaStore Retrieved", "Album:"+cursor.getString(1));
                    Log.d("MediaStore Retrieved", "Artist:"+cursor.getString(2));
                    Log.d("MediaStore Retrieved", "Filename:"+cursor.getString(7));

                    cursor.moveToNext();
                }

            }



        } catch (Exception e) {
            Log.e("Media", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return mediaStoreTags;

    }

    private void performCrop(Uri picUri,String artPath) {
        try {
            final File IMAGE_FILE_LOCATION =new File(getContext().getCacheDir(),"cropTempImage.jpg");
            Uri tmpUri = FileProvider.getUriForFile(getContext(), "com.McDevelopers.sonaplayer", IMAGE_FILE_LOCATION);

            File imageFile= new File(picUri.getPath());
            Uri uriToImage = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, imageFile);

            Intent cropIntent = ShareCompat.IntentBuilder.from(AlbumListActivity.this)
                    .setStream(uriToImage)
                    .getIntent();
            cropIntent.setAction("com.android.camera.action.CROP");
            // Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(uriToImage, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("noFaceDetection",true);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("scaleUpIfNeeded", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 768);
            cropIntent.putExtra("outputY", 768);
            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // start the activity - we handle returning in onActivityResult
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(cropIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, tmpUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri);
            startActivityForResult(cropIntent, 3);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfd){
            anfd.printStackTrace();
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            SonaToast.setToast(getApplicationContext(),errorMessage,1);
            Bitmap selectedBitmap=loadScaledBitmap(artPath);
            inflateInfoWindow(getApplicationContext(), bundleMetaTemp, songDataTemp, isVideoFileTemp, false, mediaIdTemp, bundleTagsTemp, true, selectedBitmap, bundleRestoreText);
        }
        catch (Exception anfe) {
            // display an error message
            anfe.printStackTrace();
            String errorMessage = "Gallery not supported";
            SonaToast.setToast(getApplicationContext(),errorMessage,1);
        }

    }

    private Bitmap loadScaledBitmap(String artPath){

        Uri imageUri;
        imageUri = Uri.fromFile(new File(artPath));
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
        float maxSideLength = 1000;
        float scaleFactor = Math.min(maxSideLength / opts.outWidth, maxSideLength / opts.outHeight);
        // do not upscale!
        if (scaleFactor < 1) {
            opts.inDensity = 10000;
            opts.inTargetDensity = (int) ((float) opts.inDensity * scaleFactor);
        }
        opts.inJustDecodeBounds = false;

        try {
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
            is.close();
        } catch (IOException e) {
            // ignore
        }

        return bitmap;


    }

    @Override
    public void onSaveTagResult(final String mediaNewId, final Bundle newTagsBundle){
        SonaToast sonaToast=new SonaToast();
        sonaToast.setToast(getApplicationContext(),"Updating...",1);
        Log.e("renameID", "onSaveTagResult: ID:"+mediaNewId );

        new Handler().postDelayed(new Runnable() {
            public void run() {

                String filename = null;
                if(newTagsBundle!=null) {

                    Bitmap bmp;
                    try {
                        //Write file
                        bmp = newTagsBundle.getParcelable("IMAGE");
                        filename = "albumBitmap.png";
                        FileOutputStream stream = AlbumListActivity.this.openFileOutput(filename, Context.MODE_PRIVATE);
                        if (bmp != null){
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            bmp.recycle();
                        }
                        //Cleanup
                        stream.close();

                        //Pop intent
                        Log.d("BitmapWrittenToStream", "run:Invoked ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        Bitmap value = null;
                        value = newTagsBundle.getParcelable("IMAGE");
                        if (value != null) {
                            newTagsBundle.remove("IMAGE");
                        }
                    }catch (Exception e){


                        Log.e("ExceptionRaised", "run: "+e );
                        e.printStackTrace();
                    }

                }
                Intent intent = new Intent("saveTagResult");
                intent.putExtra("renameId", mediaNewId);
                intent.putExtra("albumImage", filename);
                intent.putExtra("BUNDLE",newTagsBundle);

                sendBroadcast(intent);
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!searchView.isIconified()) {
                    searchView.onActionViewCollapsed();
                }
                listHeader.setText("Albums ( N/A )");
                listHeader.setVisibility(View.VISIBLE);

                        initAdapter(AlbumLibrary.albumxDummyData);
                        alertDialog=null;
                        builder=null;
                        Runtime.getRuntime().gc();
                        recyclerView.setVisibility(View.INVISIBLE);
                        catLoadingView = new CatLoadingView();
                        catLoadingView.setCanceledOnTouchOutside(false);
                        catLoadingView.setCancelable(false);
                        catLoadingView.show(getSupportFragmentManager(), "");
                        initCatLoadingView();



            }}, 2500);
    }
    @Override
    public  void onSdCardUriResult(){
        TagEditFlag=true;
        getSdCardUri();

    }

    @Override
    public void onChooseAlbumArt(Bundle bundleMetaTemp, String songDataTemp, boolean isVideoFileTemp,String mediaId, Bundle bundleTagsTemp,Bundle restoreText){
        this.bundleMetaTemp=bundleMetaTemp;
        this.songDataTemp=songDataTemp;
        this.isVideoFileTemp=isVideoFileTemp;
        this.mediaIdTemp=mediaId;
        this.bundleTagsTemp=bundleTagsTemp;
        this.bundleRestoreText=restoreText;


        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 2);
    }

    private Bundle getInfoBundleMeta( String songPath,boolean isVideoFile){

        try {
            Bundle bundleData = new Bundle();
            MediaFormat mf ;
            float samplerate =48;
            String duration;
            int framerate=24;
            String channel_text = "Stereo";
            try {
                MediaExtractor mediaExtractor = new MediaExtractor();
                mediaExtractor.setDataSource(songPath);

                if(isVideoFile) {
                    mf = mediaExtractor.getTrackFormat(0);
                    framerate=mf.getInteger(MediaFormat.KEY_FRAME_RATE);
                    mf = mediaExtractor.getTrackFormat(1);
                }
                else {
                    mf = mediaExtractor.getTrackFormat(0);
                }
                samplerate = (float) mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                samplerate = (samplerate / 1000);

                int channel = mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

                if (channel < 2)
                    channel_text = "Mono";
                else
                    channel_text = "Stereo";

            } catch (Throwable e) {


                e.printStackTrace();
            }

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();

            mmr.setDataSource(songPath);
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String fileName = songPath.substring(songPath.lastIndexOf("/") + 1);

            try {
                if (isVideoFile) {
                    Bitmap frame = mmr.getFrameAtTime();
                    int width = frame.getWidth();
                    int height = frame.getHeight();
                    bundleData.putString("RESOLUTION", width + "x" + height);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            bundleData.putString("FILENAME", fileName);
            bundleData.putString("FRAMERATE",framerate+"FPS");
            bundleData.putString("BITRATE",getBitRate(songPath,isVideoFile) + "Kbps");
            bundleData.putString("SAMPLERATE", samplerate + "KHz");
            bundleData.putString("FORMAT", ((songPath).substring((songPath.lastIndexOf(".") + 1)).toUpperCase()));
            bundleData.putString("CHANNEL", channel_text);
            bundleData.putString("SIZE", getStringSizeLengthFile(songPath));
            bundleData.putInt("DURATION", Integer.valueOf(duration));

            mmr.release();
            return bundleData;
        }catch (Throwable e){
            e.printStackTrace();
            return null;
        }

    }

    private  String getBitRate(String data,boolean isVideoFile) {

        int Bitrate=0;

        if(!isVideoFile) {
            try {
                TagOptionSingleton.getInstance().setAndroid(true);
                AudioFile f = AudioFileIO.read(new File(data));
                AudioHeader audioHeader = f.getAudioHeader();
                Bitrate = (int)audioHeader.getBitRateAsNumber();

                Log.d("JAudioTaggerBitrateMeta", "getBitRate: SetByJAudioTagger");
            } catch (Throwable e) {

                e.printStackTrace();
            }
        }

        if(isVideoFile || Bitrate==0) {

            try {

                MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                metadataRetriever.setDataSource(data);
                Bitrate = Integer.parseInt(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
                Bitrate = Bitrate / 1000;
                Log.d("Bitrate:", String.valueOf(Bitrate));

            } catch (Throwable e) {
                Bitrate = 128;
                Log.d("BitRate Exception: " + e, String.valueOf(Bitrate));
            }
        }

        return String.valueOf(Bitrate);

    }

    private Bundle getInfoBundleTags(String songPath, String imageId,String titleMedia,String albumMedia,String artistMedia,String composerMedia,String yearMedia){

        String genre;
        try {

            MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
            mRetriever.setDataSource(songPath);

            if (titleMedia == null || TextUtils.isEmpty(titleMedia))
                titleMedia = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            if (albumMedia == null || TextUtils.isEmpty(albumMedia))
                albumMedia = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

            if (artistMedia == null || TextUtils.isEmpty(artistMedia))
                artistMedia = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            if (composerMedia == null || TextUtils.isEmpty(composerMedia))
                composerMedia = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);

            if (yearMedia == null || TextUtils.isEmpty(yearMedia))
                yearMedia = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);

            genre = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);


        }catch (Throwable e){

            e.printStackTrace();
            return null;
        }

        Bundle bundleTags = new Bundle();
        bundleTags.putString("TITLE",titleMedia);
        bundleTags.putString("ALBUM",albumMedia);
        bundleTags.putString("ARTIST",artistMedia);
        bundleTags.putString("COMPOSER",composerMedia);
        bundleTags.putString("YEAR",yearMedia);
        bundleTags.putString("GENRE",genre);
        bundleTags.putString("ALBUM_ID",imageId);
        return bundleTags;
    }
}


