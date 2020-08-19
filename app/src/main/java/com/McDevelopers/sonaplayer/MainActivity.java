package com.McDevelopers.sonaplayer;

import android.Manifest;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.McDevelopers.sonaplayer.PreferenceActivity.PreferenceActivity;
import com.McDevelopers.sonaplayer.QueueListActivity.QueueListActivity;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.McDevelopers.sonaplayer.ArtistListActivity.ArtistListActivity;
import com.McDevelopers.sonaplayer.Equalizer_View_Pager.ModelObject;
import com.McDevelopers.sonaplayer.FolderListActivity.FolderListActivity;
import com.McDevelopers.sonaplayer.MediaListActivity.MediaListActivity;
import com.McDevelopers.sonaplayer.AlbumListActivity.AlbumListActivity;
import com.McDevelopers.sonaplayer.RecentlyAddedActivity.RecentListActivity;
import com.McDevelopers.sonaplayer.musicviz.FFTFrame;
import com.McDevelopers.sonaplayer.musicviz.WaveFormFrame;
import com.McDevelopers.sonaplayer.musicviz.render.GLScene;
import com.McDevelopers.sonaplayer.musicviz.render.SceneController;
import com.McDevelopers.sonaplayer.musicviz.render.VisualizerRenderer;
import com.McDevelopers.sonaplayer.musicviz.scene.BasicSpectrumScene;
import com.McDevelopers.sonaplayer.musicviz.scene.ChlastScene;
import com.McDevelopers.sonaplayer.musicviz.scene.CircSpectrumScene;
import com.McDevelopers.sonaplayer.musicviz.scene.EnhancedSpectrumScene;
import com.McDevelopers.sonaplayer.musicviz.scene.InputSoundScene;
import com.McDevelopers.sonaplayer.musicviz.scene.OriginScene;
import com.McDevelopers.sonaplayer.musicviz.scene.RainbowSpectrumScene;
import com.McDevelopers.sonaplayer.musicviz.scene.Sa2WaveScene;
import com.McDevelopers.sonaplayer.musicviz.scene.WavesRemixScene;
import com.McDevelopers.sonaplayer.videolist.VideoListActivity;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.Transition;
import com.chibde.visualizer.BarVisualizer;
import com.chibde.visualizer.CircleBarVisualizer;
import com.chibde.visualizer.LineBarVisualizer;
import com.chibde.visualizer.LineVisualizer;
import com.cleveroad.androidmanimation.LoadingAnimationView;

import com.eftimoff.viewpagertransformers.ZoomOutSlideTransformer;
import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import me.bogerchan.niervisualizer.NierVisualizerManager;
import me.bogerchan.niervisualizer.renderer.IRenderer;
import me.bogerchan.niervisualizer.renderer.circle.CircleBarRenderer;
import me.bogerchan.niervisualizer.renderer.circle.CircleRenderer;

import me.bogerchan.niervisualizer.renderer.line.LineRenderer;
import me.kaelaela.verticalviewpager.VerticalViewPager;
import me.kaelaela.verticalviewpager.transforms.ZoomOutTransformer;

import static com.McDevelopers.sonaplayer.ApplicationContextProvider.getContext;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

@SuppressLint({"SetTextI18n","ApplySharedPref","ClickableViewAccessibility","InflateParams"})
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,Visualizer.OnDataCaptureListener,Sona_Font_Dialog.Sona_font_result,InfoTabDialog.InfoTabResult, MusicLibrary.postMetaUIUpdateListener{

   private MediaControllerCompat mediaController=null;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionRequired = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS};
  private static Typeface face;
  private static boolean videoFirstSeek=true;
    private static int volLevel = 0;
    static int maxVolume;
    private SharedPreferences currentState;
    private static boolean isPowerEnabled=true;
    public   static ArrayList<String> albumArtList = new ArrayList<>();
    private boolean sentToSetting = false;
    boolean isPlay = true;
    private  ViewPager EqViewPager=null;
    private boolean mIsPlaying;
    private MediaBrowserHelper mMediaBrowserHelper;
    private  SeekBar mSeekBarAudio;
    private ImageView albumImage;
    private ImageView mainAlbumArt;
    private static boolean albumAutoHide=true;
    private AudioTokenReceiver resultReceiver;
    boolean allgranted=false;
    private   Boolean longListener=false;
    private   Boolean longListenerNext=false;
    private   Boolean longListenerPrev=false;
    private static int visualizerIndex=0;
    private  static int clearVisualizerIndex=0;

    private static boolean customAlbum=false;

    private BarVisualizer barVisualizer;
    private CircleBarVisualizer circleBarVisualizer;
    private LineBarVisualizer lineBarVisualizer;
    private  LineVisualizer lineVisualizer;
    private LoadingAnimationView loadingAnimationView;
    Animation animationRight;
    private ConstraintLayout parentLayout;
    private TextSwitcher switcher;
    private TextSwitcher tagSwitcher;
    private TextSwitcher bitrateSwitcher;
    private TextSwitcher bitValueSwitcher;
    private TextSwitcher statusTextSwitcher;
    private TextSwitcher statusValueSwitcher;

    private TextSwitcher trackSwitcher;
    private TextSwitcher trackValueSwitcher;
    Animation zoomin, zoomout;
   private ImageButton PlayPause;
    static int density;
   private Croller volKnob;
    private Croller trableKnob;
    private Croller tempoKnob;
    private  TextView songName;
    private TextView EndTime;
    private  TextView StartTime;
    private Handler mSeekbarUpdateHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
    private Handler mUpdateTagsHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
    private Handler mUpdateTrackHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
    private Handler mUpdateMetaHandler =new Handler(Objects.requireNonNull(Looper.myLooper()));
    private Handler mSleepSyncHandler =new Handler(Objects.requireNonNull(Looper.myLooper()));
    private Handler viewHiderHandler =new Handler(Objects.requireNonNull(Looper.myLooper()));
    private Handler albumArtHiderHandler =new Handler(Objects.requireNonNull(Looper.myLooper()));
    private Handler mStatusUpdateHandler =new Handler(Objects.requireNonNull(Looper.myLooper()));
    private Handler vLongTouchHandler =new Handler(Objects.requireNonNull(Looper.myLooper()));
    private Handler albumArtChangerHandler =new Handler(Objects.requireNonNull(Looper.myLooper()));
    private Handler volDuckUpHandler =new Handler(Objects.requireNonNull(Looper.myLooper()));
    private int targetVolume=8;
    private boolean startup=true;
    private static Bitmap albumArt;
    private  boolean mIsStop=false;
    private boolean mIsShuffle=true;
    private int mIsRepeat=2;
    Animation animationLeft;
    private TextView repeatTimeA;
    private TextView repeatTimeB;
    private ImageButton repeatBtnA;
    private ImageButton repeatBtnB;
    private  boolean isRepeatAB=false;
    private  boolean isRepeatActive=false;
    private int repeatDurationA=-1;
    private int repeatDurationB=-1;
    private LinearLayout repeatParentA;
    private LinearLayout repeatParentB;
    private String[] strings = {"Unknown Album", "Unknown Artist", "Unknown", "Unknown Genre", "Unknown Year", "Unknown Filename", "Not Available"};
    private String[] stringTag = {"Album:", "Artist:", "Composer:", "Genre:", "Year:", "Filename:", "Next:"};
    private String[] stringStatusSwitcher = {"   Volume:", "Bass Boost:", "Virtualizer:", "Equalizer:", "Loudness:","   Treble: ","Balance:","     Tempo: ","   Reverb: ","FontStyle:"};
    private String[] stringValueSwitcher = {"N/A","N/A","N/A","N/A","N/A","N/A","N/A","N/A","N/A","N/A"};
    private String[] bitStrings={"Bitrate: ","Sample Rate: ","Format:   ","Channel:  ","Size:   "};
    private String[] vbitStrings={"Resolution: ","Frame Rate: ","Bitrate: ","Sample Rate: ","Format:   ","Channel:  ","Size:   "};
    private String[] bitValueStrings={"N/A","N/A","N/A","N/A","N/A"};
    private String[] vbitValueStrings={"N/A","N/A","N/A","N/A","N/A","N/A","N/A"};
    private  int bitCount=bitStrings.length;
    private int vbitCount=vbitStrings.length;
    private String[] stringTrackSwitcher = {"Track:", "Audio:", "Video:"};
    private String[] stringTrackValue = {"N/A", "N/A", "N/A"};
    private  int trackCount=stringTrackSwitcher.length;
    private int trackIndex=-1;

    static String[] reverb_styles={"None","Room","Scene","Hall","Studio","Stadium","Plate"};
    private int reverbIndex=0;

    private int bitIndex=-1;
    int messageCount = strings.length;
    int statusCount = stringStatusSwitcher.length;
    private int currentIndex=-1;
    private int statusIndex=-1;
    private boolean sleepflag=false;
    ConstraintLayout sleepLayout=null;
    LinearLayout equalizerLayout=null;
    LinearLayout equalizerPagerLayout=null;
    LinearLayout bassLayout=null;
    ConstraintLayout middleParent=null;
    private static VerticalViewPager viewPager;
    private static long sleepLong=0;
    private static  int duration;
    private TextView TimerSyncView;
    private SeekBar seekBar;
   private ImageButton EqualizerBtn;
    private ImageButton nextPage;
    private ImageButton prevPage;
    private Button sleepstop;
    private TextView sleepLabel;
    static String[] music_styles;
    static int equalizerIndex=0;
    static short eLavel=1500;
    static short m;
    static boolean equalizerFlag=false;
    static AudioManager mAudioManager;
    static boolean autoVol=false;
    public static   Uri videoUri;
    private  CustomVideoView videoView;
   public static boolean misVideoPlaying=false;
   private  static boolean supressOnPause=false;
   private  ConstraintLayout videoWrapper;
   private RelativeLayout statusLayout;
   private static boolean isTrackLayout=false;
   public static int videoseekPos=0;
    public static int audioSeekPos=0;
   private boolean misVideoPaused=false;
   public static  boolean isActivityRunning=false;
   public static String FullScreenTitle ;
   public static boolean touchReady=false;
   private  SeekBar videoSeekbar=null;
   private RelativeLayout seekbarWrapper;
   private RelativeLayout metaLayoutView;
   private boolean isFullScreen=false;
  private static boolean longTouch=false;
   private boolean fitwidth=true;
   private CustomTextInputEditText track_num_input;
   private TextInputLayout track_input_layout;
    private NierVisualizerManager visualizerManager;
    private SurfaceView surfaceView;
    LinearLayout channelLayout=null;
    private static VerticalViewPager reverbPager;
    private VisualizerRenderer mRender;
    private List<Pair<String, ? extends GLScene>> mSceneList;
    private Visualizer mVisualizer;
    private TextureView textureView;
    private static int visualID=-1;
    private  static  boolean videofinished=false;
    private  static  int fontIndex=0;
    public static boolean videoViewDisabled=true;
    private static  int currentArtIndex;

    private SeekBar volumeSeekbar;
    private SeekBar brightnessSeekbar;

    private  float left=1.0f ;
    private  float right=1.0f ;
    private int channelprogress=10;
    private float tempo=1.0f;

    private int tempoProgress=5;

    private static PresetReverb presetReverbV = null;

    private int leftRead;
    private int rightRead;

    private ImageButton sleepTimer;
    private ImageButton playList;
    private  ImageButton shuffle;
    private ImageButton repeat;
   private ImageButton next;
   private ImageButton prev;
    private FrameLayout track_goto_parent;
    private InputMethodManager imm;
    private static int SlideArtIndex;
    static Equalizer mEqualizerV=null;
    static int loudGain=50;
    static short bassGain=500;
    static short virtualGain=500;
    static short trableGain=0;
    static short bandRange;
    private static BassBoost bassBoostV = null;
    private static Virtualizer virtualizerV = null;
    private static LoudnessEnhancer loudnessEnhancerV = null;
    private static boolean fadeInFadeOut=true;

    private static  int videoWidth=0,videoHeight=0;

    private Handler volSeekHiderHandler =new Handler(Objects.requireNonNull(Looper.myLooper()));
    private ScaleGestureDetector mScaleGestureDetector;

    private static boolean scaleGesture=false;
    private static Animation myAnim;
    private static Animation shakeSmall;

     private static int percentVol;

    private static  int widthDelta=0;
    private static  int heightDelta=0;
    private static  int originalWidth;
    private static  int originalHeight;

    private static boolean slideMode=false;
    public static boolean supressPlay=false;
    private static int earVolLevel=0;

    private static Animation in;
    private static Animation out;
    private static Animation up;
    private static Animation down;
    private static Animation fadeIn;
    private static Animation fadeOut;
    public static int brightnessSystem=0;
    private static int brightnessCurrent=-1;
    private PopupWindow mPopupWindow;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder=null;
    AlertDialog alertDialog=null;
    private static Uri sdCardUri;
    String deletePath;
    String deleteMediaId;
   private static boolean TagEditFlag=false;
    private InfoTabDialog infoTabDialog;
    private static Bundle bundleMetaTemp;
    private static String songDataTemp;
    private static boolean isVideoFileTemp;
    private static String mediaIdTemp;
    private static Bundle bundleTagsTemp;
    private static Bundle bundleRestoreText;
    private static int enterAnim=6;
    private static int exitAnim=0;
    private boolean isQueueActive=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isActivityRunning=true;
        videoViewDisabled=true;
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        if(!ApplicationContextProvider.systemFont) {
            face = Typeface.createFromAsset(getAssets(),
                    ApplicationContextProvider.getFontPath());
        }else {

            face=Typeface.DEFAULT;
        }
        fontIndex=ApplicationContextProvider.getFontIndex();

        if(!ApplicationContextProvider.systemFont) {
            stringValueSwitcher[9] = ApplicationContextProvider.fontNameArray[fontIndex];
        }else {
            stringValueSwitcher[9] = "Default";

        }

        density=getResources().getDisplayMetrics().densityDpi;

        setContentView(R.layout.activity_main);
        parentLayout=findViewById(R.id.main_activity_parent);
        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        MusicLibrary.registerMetaUIUpdateListener(this);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if( mAudioManager != null)
        maxVolume= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        percentVol= (int) (maxVolume*0.4f);

        Log.d("MaxVolLevel", "onCreate: MaxVolLevel: "+maxVolume);
        volLevel = currentState.getInt("volLevel", (int) (maxVolume * 0.5f));
        earVolLevel = currentState.getInt("earVolLevel", (int)(maxVolume * 0.4f));
        targetVolume=currentState.getInt("volLevel", (int) (maxVolume * 0.5f));

        if(volLevel<5){
            volLevel= (int) (maxVolume * 0.5f);

        }
        if(earVolLevel<=4){

            earVolLevel= (int)(maxVolume * 0.4f);
        }
        Log.d("SpeakerVolLevel", "onCreate: SpeakerVolLevel: "+volLevel);
        Log.d("EarVolLevel", "onCreate: EarphoneVolLevel: "+earVolLevel);


        if(!SonaHeartService.isServiceRunning) {

            if(isHeadsetOn(getApplicationContext())) {
                if ( startup && earVolLevel > (maxVolume * 0.4f)) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, percentVol, 0);
                    Log.d("streamVolumeSet", "onCreate:earphone "+percentVol);
                } else {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, earVolLevel, 0);
                    Log.d("streamVolumeSet", "onCreate:earphone2 "+earVolLevel);

                }
            }else {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volLevel, 0);
                Log.d("streamVolumeSet", "onCreate:speaker "+volLevel);

            }
        }

      //Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        String UriRaw=currentState.getString("SDCardUri",null);
        if(!TextUtils.isEmpty(UriRaw)) {
            sdCardUri = Uri.parse(UriRaw);
            Log.d("MediaListActivity", "onCreate: SdcardUri:" + UriRaw);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        middleParent=findViewById(R.id.middleView);
        metaLayoutView=findViewById(R.id.metaLayoutView);
        inflateAnimation();
        ImageButton mainOption = findViewById(R.id.main_option_button);

        zoomin = AnimationUtils.loadAnimation(this, R.anim.zoom_in_default);
        zoomout = AnimationUtils.loadAnimation(this, R.anim.zoom_out_default);

        seekbarWrapper=findViewById(R.id.seekbar_wrapper);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
        shakeSmall = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_small);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        Menu menu = navigationView.getMenu();
        MenuItem tools = menu.findItem(R.id.nav_tools);
        MenuItem nav_help = menu.findItem(R.id.nav_help);
        MenuItem nav_send = menu.findItem(R.id.nav_share);
        MenuItem nav_about = menu.findItem(R.id.nav_about);
        SpannableString s = new SpannableString(tools.getTitle());
        SpannableString shelp = new SpannableString(nav_help.getTitle());
        SpannableString ssend = new SpannableString(nav_send.getTitle());
        SpannableString sabout = new SpannableString(nav_about.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.NavigationHead), 0, s.length(), 0);
        shelp.setSpan(new TextAppearanceSpan(this, R.style.NavigationTitle), 0, shelp.length(), 0);
        ssend.setSpan(new TextAppearanceSpan(this, R.style.NavigationTitle), 0, ssend.length(), 0);
        sabout.setSpan(new TextAppearanceSpan(this, R.style.NavigationTitle), 0, sabout.length(), 0);
        tools.setTitle(s);
        nav_about.setTitle(sabout);
        nav_help.setTitle(shelp);
        nav_send.setTitle(ssend);
        navigationView.setNavigationItemSelectedListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        mIsShuffle = currentState.getBoolean("shuffle", true);
        mIsRepeat = currentState.getInt("repeat", 2);
        fitwidth=currentState.getBoolean("isFillWidth",true);
        left=currentState.getFloat("left",1.0f);
        right=currentState.getFloat("right",1.0f);
        leftRead=currentState.getInt("leftRead",0);
        rightRead=currentState.getInt("rightRead",0);
        tempo=currentState.getFloat("tempo",1.0f);
        tempoProgress=currentState.getInt("tempoProgress",5);
        reverbIndex=currentState.getInt("reverb",0);
        albumAutoHide=currentState.getBoolean("albumAutoHide",true);
        customAlbum=currentState.getBoolean("customAlbum",false);
        equalizerIndex = currentState.getInt("Equalizer", 5);
        fadeInFadeOut=currentState.getBoolean("FadeInFadeOut",true);

        loudGain=currentState.getInt("LoudValue",0);
        bassGain=(short)currentState.getInt("BassValue",1000);
        virtualGain=(short)currentState.getInt("VirtualValue",1000);
        trableGain=(short)currentState.getInt("TrableValue",0);
        widthDelta=currentState.getInt("DeltaWidth",0);
        heightDelta=currentState.getInt("DeltaHeight",0);
        brightnessCurrent=currentState.getInt("brightness",-1);
        channelprogress=currentState.getInt("CP",10);
        boolean isScreenOn = currentState.getBoolean("keepScreenOn", true);
        isPowerEnabled=currentState.getBoolean("powerFlag",true);
        enterAnim=currentState.getInt("enterAnim",6);
        exitAnim=currentState.getInt("exitAnim",0);
        isQueueActive=currentState.getBoolean("QueueFlag",false);

        if(isPowerEnabled)
            registerReceiver(killActivity, new IntentFilter("killActivity"));

        registerReceiver(refreshLibrary, new IntentFilter("refreshLibrary"));
        registerReceiver(saveTagResult, new IntentFilter("saveTagResult"));
        registerReceiver(QueueToggle, new IntentFilter("QueueToggle"));


        if(isScreenOn){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }


        statusLayout=findViewById(R.id.currentStatusLayout);
        PlayPause = findViewById(R.id.PlayPause);
        PlayPause.setEnabled(false);
        PlayPause.setImageResource(R.drawable.btn_play);
        PlayPause.setLongClickable(true);
        PlayPause.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        "*    Music Stopped    *", Snackbar.LENGTH_SHORT);
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();
                PlayPause.setImageResource(R.drawable.matte_stop_big);

                mIsStop = true;
                isPlay = false;
                longListener = true;
                if(misVideoPlaying && videoView!=null) {
                    supressOnPause=true;
                    videoseekPos = 0;
                    videoView.pause();
                    videoSeekbar.setProgress(0);
                    misVideoPaused=true;
                }else {
                    mediaController.getTransportControls().pause();
                    mediaController.getTransportControls().seekTo(0);
                    mSeekBarAudio.setProgress(0);
                    mediaController.getTransportControls().stop();
                }
                StartTime.setText("00:00");


                return true;
            }
        });


        View.OnTouchListener handleTouch = new View.OnTouchListener() {
            @Override

            public boolean onTouch(View v, MotionEvent event) {

                //  v.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    // We're only interested in anything if our speak button is currently pressed.
                    if (longListener) {
                        //Toast.makeText(getApplicationContext(),"onTouchDown_Invoked",Toast.LENGTH_LONG).show();
                        PlayPause.setImageResource(R.drawable.btn_play);
                        longListener = false;
                        isPlay=false;
                    } else {
                        if(!supressPlay)
                        clickManual();
                        // Toast.makeText(getApplicationContext(),"onTouchDown_manualClick",Toast.LENGTH_LONG).show();
                    }

                }
                return false;

            }
        };

        PlayPause.setOnTouchListener(handleTouch);


        playList = findViewById(R.id.play_list);
        playList.setEnabled(false);


        playList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playList.startAnimation(myAnim);
                Vibration.Companion.vibrate(20);
                if(isQueueActive){
                    Intent intent = new Intent(MainActivity.this, QueueListActivity.class);
                    startActivityForResult(intent,1);
                    Animations.Animations(MainActivity.this,enterAnim);
                }else
                sendMessage();
            }
        });


        sleepTimer = findViewById(R.id.sleep_timer);
        sleepTimer.setEnabled(false);

        sleepTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);

                Vibration.Companion.vibrate(20);
                sleepTimer.startAnimation(myAnim);

                if (!sleepflag) {
                    if(videoView!=null){
                        if(misVideoPlaying){
                            videoView.pause();
                            clearEqualizers();
                            videoView=null;
                            inflateAudioSeekbar();
                        }}
                    if(equalizerFlag){
                        slideDown(equalizerPagerLayout);
                        viewPager.setAdapter(null);
                        reverbPager.setAdapter(null);
                        EqViewPager.setAdapter(null);
                        equalizerPagerLayout.setVisibility(View.GONE);
                        equalizerFlag=false;
                        nextPage.clearAnimation();
                        nextPage.setVisibility(View.GONE);
                        prevPage.clearAnimation();
                        prevPage.setVisibility(View.GONE);
                    }


                    clearVisualizer(clearVisualizerIndex);
                    inflateSleep();
                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    seekBar.setProgress(currentState.getInt("sleepSeek", 1));
                    sleepLong = currentState.getLong("sleepTime", 300000);

                   // sleepLayout.setVisibility(View.VISIBLE);
                    slideUp(sleepLayout);
                    if (TimerSyncView.getVisibility() == View.VISIBLE) {
                        sleepstop.setVisibility(View.VISIBLE);
                    } else {
                        sleepstop.setVisibility(View.INVISIBLE);
                    }
                    sleepflag = true;

                } else {


                    slideDown(sleepLayout);
                    sleepflag = false;
                    if(misVideoPlaying){

                        inflateVideoView();
                        videoView.seekTo((int)mediaController.getPlaybackState().getPosition());
                    }else {
                        inflateVisualizer();
                        startVisualizer();}

                }


            }
        });

        sleepTimer.setLongClickable(true);
        sleepTimer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                sleepTimer.startAnimation(myAnim);
                Vibration.Companion.vibrate(30);
                if (TimerSyncView.getVisibility() == View.VISIBLE) {

                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                    mediaController.sendCommand("SleepStop", null, resultReceiver);
                    TimerSyncView.setText("Running");
                    TimerSyncView.setVisibility(View.INVISIBLE);
                    mSleepSyncHandler.removeCallbacks(mSleepSync);
                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "Sleep Timer Stopped", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();


                } else {
                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    sleepLong = currentState.getLong("sleepTime", 300000);
                    if (sleepLong > 0) {
                        resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                        Bundle bundle = new Bundle();
                        bundle.putLong("time", sleepLong);
                        mediaController.sendCommand("SleepTimer", bundle, resultReceiver);
                        mSleepSyncHandler.postDelayed(mSleepSync, 1000);
                        TimerSyncView.setVisibility(View.VISIBLE);
                        Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                "Sleep Timer Running", Snackbar.LENGTH_SHORT);
                        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                        snack.show();
                    }
                }

                return true;
            }
        });

        playList.setLongClickable(true);
        playList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Intent intent = new Intent(MainActivity.this, VideoListActivity.class);
               // intent.putExtra("LIST", (Serializable) video_data);
                intent.putExtra("FOCUS", false);
                intent.putExtra("scrollId", mediaController.getMetadata().getDescription().getMediaId());
                startActivityForResult(intent, 1);
                Animations.Animations(MainActivity.this,enterAnim);


                return true;
            }
        });


        next = findViewById(R.id.next);
        next.setEnabled(false);

        next.setLongClickable(true);
        next.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibration.Companion.vibrate(50);

                longListenerNext=true;

            final Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper()));

                final Runnable r = new Runnable() {
                    public void run() {

                        if(longListenerNext) {

                            if (misVideoPlaying && videoView != null) {

                                Log.d("VideoSeekPos", "run: val "+videoView.getCurrentPosition());

                                int progress = videoSeekbar.getProgress()+5000;
                                videoView.seekTo(progress);
                                videoSeekbar.setProgress(progress);


                            } else {

                                mediaController.getTransportControls().seekTo(mediaController.getPlaybackState().getPosition()+5000);
                            }

                            handler.postDelayed(this, 200);
                        }
                    }
                };

                handler.postDelayed(r, 0);

                return true;
            }
        });



        View.OnTouchListener handleTouchNext = new View.OnTouchListener() {
            @Override

            public boolean onTouch(View v, MotionEvent event) {

                //  v.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    // We're only interested in anything if our speak button is currently pressed.
                    if (longListenerNext) {
                        //Toast.makeText(getApplicationContext(),"onTouchDown_Invoked",Toast.LENGTH_LONG).show();
                        longListenerNext = false;
                    } else {
                        mSeekbarUpdateHandler.removeCallbacks(mUpdateseekbar);
                        SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = currentState.edit();
                        editor.putLong("currentPosition", 0);
                        editor.commit();

                        if(misVideoPlaying && videoView!=null){
                            supressOnPause=true;
                            videoView.pause();
                            clearEqualizers();
                        }
                        mMediaBrowserHelper.getTransportControls().skipToNext();
                        // Toast.makeText(getApplicationContext(),"onTouchDown_manualClick",Toast.LENGTH_LONG).show();
                    }

                }
                return false;

            }
        };

        next.setOnTouchListener(handleTouchNext);

         prev = findViewById(R.id.prev);
        prev.setEnabled(false);

        prev.setLongClickable(true);
        prev.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibration.Companion.vibrate(50);

                longListenerPrev=true;

                final Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper()));

                final Runnable r = new Runnable() {
                    public void run() {

                        if(longListenerPrev) {

                            if (misVideoPlaying && videoView != null) {

                                int progress = videoSeekbar.getProgress()-5000;
                                if(progress<0)
                                    progress=0;

                                videoView.seekTo(progress);
                                videoSeekbar.setProgress(progress);
                            } else {
                                mediaController.getTransportControls().seekTo(mediaController.getPlaybackState().getPosition()-5000);
                            }

                            handler.postDelayed(this, 200);
                        }
                    }
                };

                handler.postDelayed(r, 0);

                return true;
            }
        });



        View.OnTouchListener handleTouchPrev = new View.OnTouchListener() {
            @Override

            public boolean onTouch(View v, MotionEvent event) {

                //  v.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    // We're only interested in anything if our speak button is currently pressed.
                    if (longListenerPrev) {
                        //Toast.makeText(getApplicationContext(),"onTouchDown_Invoked",Toast.LENGTH_LONG).show();
                        longListenerPrev = false;
                    } else {
                        mSeekbarUpdateHandler.removeCallbacks(mUpdateseekbar);
                        SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = currentState.edit();
                        editor.putLong("currentPosition", 0);
                        editor.commit();

                        if(misVideoPlaying && videoView!=null){
                            supressOnPause=true;
                            videoView.pause();
                            clearEqualizers();

                        }
                        mMediaBrowserHelper.getTransportControls().skipToPrevious();
                        // Toast.makeText(getApplicationContext(),"onTouchDown_manualClick",Toast.LENGTH_LONG).show();
                    }

                }
                return false;

            }
        };

        prev.setOnTouchListener(handleTouchPrev);



        shuffle = findViewById(R.id.shuffle);
        shuffle.setEnabled(false);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mIsShuffle) {
                    shuffle.setImageResource(R.drawable.shuffle_off);
                    mIsShuffle = false;
                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("shuffle", false);
                    mediaController.sendCommand("shuffle", bundle, resultReceiver);
                    mediaController.sendCommand("nextSong", null, resultReceiver);
                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "*    Shuffle Off    *", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putBoolean("shuffle", false);
                    editor.commit();
                } else {

                    shuffle.setImageResource(R.drawable.shuffle_on);
                    mIsShuffle = true;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("shuffle", true);
                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                    mediaController.sendCommand("shuffle", bundle, resultReceiver);
                    mediaController.sendCommand("nextSong", null, resultReceiver);
                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "*     Shuffle On    *", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putBoolean("shuffle", true);
                    editor.commit();
                }

            }
        });

        shuffle.setLongClickable(true);
        shuffle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(mIsShuffle) {

                    Bundle bundleS = new Bundle();
                    bundleS.putBoolean("shuffle", true);
                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                    mediaController.sendCommand("shuffle", bundleS, resultReceiver);
                    mediaController.sendCommand("nextSong", null, resultReceiver);
                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "Next Song Shuffled", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();

                }else {
                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "Enable Shuffle First!", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();

                }

                return true;
            }
        });

       repeat = findViewById(R.id.repeat);
        repeat.setEnabled(false);
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRepeatAB){
                    removeRepeatAB();
                    return;
                }

                if (mIsRepeat == 0) {

                    repeat.setImageResource(R.drawable.repeat_one);
                    mIsRepeat = 1;
                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                    Bundle bundle = new Bundle();
                    bundle.putInt("repeat", 1);
                    mediaController.sendCommand("repeat", bundle, resultReceiver);

                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "*    Repeat One   *", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putInt("repeat", 1);
                    editor.commit();
                } else if (mIsRepeat == 1) {

                    repeat.setImageResource(R.drawable.repeat_all);
                    mIsRepeat = 2;

                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                    Bundle bundle = new Bundle();
                    bundle.putInt("repeat", 2);
                    mediaController.sendCommand("repeat", bundle, resultReceiver);
                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "*     Repeat All    *", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putInt("repeat", 2);
                    editor.commit();

                } else if (mIsRepeat == 2) {

                    repeat.setImageResource(R.drawable.repeat_off);
                    mIsRepeat = 0;

                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                    Bundle bundle = new Bundle();
                    bundle.putInt("repeat", 0);
                    mediaController.sendCommand("repeat", bundle, resultReceiver);
                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "*     Repeat Off    *", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putInt("repeat", 0);
                    editor.commit();

                }
            }
        });

        repeat.setLongClickable(true);

        repeat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(!isRepeatAB) {
                    inflateRepeatAB();
                }else {
                    removeRepeatAB();
                }

            return true;
            }
        });

        if (mIsShuffle) {

            shuffle.setImageResource(R.drawable.shuffle_on);

        } else {
            shuffle.setImageResource(R.drawable.shuffle_off);
        }

        if (mIsRepeat == 0) {

            repeat.setImageResource(R.drawable.repeat_off);

        } else if (mIsRepeat == 1) {
            repeat.setImageResource(R.drawable.repeat_one);
        } else if (mIsRepeat == 2) {
            repeat.setImageResource(R.drawable.repeat_all);
        }


        mSeekBarAudio = findViewById(R.id.music_seekbar);
        mSeekBarAudio.setEnabled(false);
        songName = findViewById(R.id.song_name);
        EndTime = findViewById(R.id.end_time);
        StartTime = findViewById(R.id.startTime);
        albumImage = findViewById(R.id.album);

        switcher = findViewById(R.id.simpleTextSwitcher);
        tagSwitcher = findViewById(R.id.artistTextSwitcher);
        trackSwitcher=findViewById(R.id.track_switcher);
        trackValueSwitcher=findViewById(R.id.track_no_switcher);

        bitrateSwitcher = findViewById(R.id.bitrateSwitcher);
        bitValueSwitcher = findViewById(R.id.bitValueSwitcher);
        statusTextSwitcher=findViewById(R.id.statusTextSwitcher);
        statusValueSwitcher=findViewById(R.id.statusValueSwitcher);

        TimerSyncView = findViewById(R.id.timer_sync);
         EqualizerBtn =findViewById(R.id.equalizerbtn);
         EqualizerBtn.setHapticFeedbackEnabled(false);
         EqualizerBtn.setEnabled(false);
         nextPage=findViewById(R.id.next_page);
         prevPage=findViewById(R.id.prev_page);


       animationLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.right_anim);
       animationRight = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.left_anim);

        mSeekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if( fromUser) {

                    audioSeekPos=progress;
                    StartTime.setText(timeConvert(progress));


                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if(isRepeatActive){

                    isRepeatActive=false;
                    repeatTimeA.setText("00:00");
                    repeatDurationA=-1;
                    repeatTimeB.setText("00:00");
                    repeatDurationB=-1;
                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "  RepeatA-B Reset  ", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                }

                StartTime.setScaleX(1.1f);
                StartTime.setScaleY(1.1f);
                mSeekbarUpdateHandler.removeCallbacks(mUpdateseekbar);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(mediaController!=null) {
                    StartTime.setScaleX(1.0f);
                    StartTime.setScaleY(1.0f);
                    mediaController.getTransportControls().seekTo(audioSeekPos);
                    mSeekbarUpdateHandler.postDelayed(mUpdateseekbar, 0);
                }

            }
        });


       nextPage.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {

               Vibration.Companion.vibrate(20);

               if(EqViewPager!=null && EqViewPager.getVisibility()==View.VISIBLE){

               int currentPage= EqViewPager.getCurrentItem();

               switch (currentPage){

                   case 0: EqViewPager.setCurrentItem(1,true);
                       break;
                   case 1: EqViewPager.setCurrentItem(2,true);
                       break;
                   case 2: EqViewPager.setCurrentItem(0,true);
                       break;
               }

               }


               }
               });

        prevPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Vibration.Companion.vibrate(20);

                if(EqViewPager!=null && EqViewPager.getVisibility()==View.VISIBLE){

                    int currentPage= EqViewPager.getCurrentItem();

                    switch (currentPage){

                        case 0: EqViewPager.setCurrentItem(2,true);
                            break;
                        case 1: EqViewPager.setCurrentItem(0,true);
                            break;
                        case 2: EqViewPager.setCurrentItem(1,true);
                            break;
                    }

                }



            }
        });

        albumImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibration.Companion.vibrate(20);

                if(videoView==null && !misVideoPlaying && mainAlbumArt!=null){

                    if(mainAlbumArt.getVisibility()!=View.VISIBLE && albumArt!=null){

                        mainAlbumArt.setVisibility(View.VISIBLE);
                        albumAutoHide=false;

                        mainAlbumArt.startAnimation(zoomin);
                        startAnimListener();
                        albumArtChangerHandler.postDelayed(mAlbumArtChanger,20000);



                        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = currentState.edit();
                        editor.putBoolean("albumAutoHide", false);
                        editor.commit();

                        Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                "AlbumArt AutoHide off", Snackbar.LENGTH_SHORT);
                        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                        snack.show();


                    }else if(mainAlbumArt.getVisibility()==View.VISIBLE && albumArt!=null) {

                        zoomin.cancel();
                        zoomout.cancel();
                        mainAlbumArt.clearAnimation();
                        albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                        mainAlbumArt.setVisibility(View.GONE);
                        albumAutoHide=true;
                        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = currentState.edit();
                        editor.putBoolean("albumAutoHide", true);
                        editor.commit();

                        Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                "AlbumArt AutoHide On", Snackbar.LENGTH_SHORT);
                        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                        snack.show();

                    }else {

                        Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                "AlbumArt Unavailable!", Snackbar.LENGTH_SHORT);
                        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                        snack.show();

                    }

                }else {

                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "AlbumArt Unavailable!", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                }


            }
        });

        albumImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean isVideoFile = false;
                if (mediaController.getMetadata().getString("VIDEO").equals("true")) {
                    isVideoFile = true;
                }
                if(!isVideoFile) {
                    Bundle bundleMeta = getInfoBundleMeta();
                    Bundle bundleTags = getInfoBundleTags();
                    String mediaId = mediaController.getMetadata().getDescription().getMediaId();

                    String songPath = mediaController.getMetadata().getString("SONGPATH");
                    onChooseAlbumArt(bundleMeta, songPath, false, mediaId, bundleTags, null);
                }else {
                    Snackbar  snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "AlbumArt Unavailable", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                }

                return true;
            }
        });

        ((ViewGroup) findViewById(R.id.middleView)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);



        trackSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                 
                // create a TextView
                TextView t = new TextView(MainActivity.this);
                t.setTypeface(face);
                // set the gravity of text to top and center horizontal
                t.setGravity(Gravity.START);
                t.setSingleLine(true);
                t.setIncludeFontPadding(false);
                t.setLineSpacing(0f,0f);
                t.setEllipsize(TextUtils.TruncateAt.END);
                // set displayed text size
                if(density<=DisplayMetrics.DENSITY_HIGH) {
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
                }else if(density==DisplayMetrics.DENSITY_XHIGH) {

                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                }else if(density==DisplayMetrics.DENSITY_420){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
                }
                else if(density>=DisplayMetrics.DENSITY_XXHIGH){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                }
                t.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
                return t;
            }
        });

        trackValueSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                 
                // create a TextView
                TextView t = new TextView(MainActivity.this);
                t.setTypeface(face);
                // set the gravity of text to top and center horizontal
                t.setGravity(Gravity.START);
                t.setSingleLine(true);
                t.setEllipsize(TextUtils.TruncateAt.END);
                t.setIncludeFontPadding(false);
                t.setLineSpacing(0f,0f);
                // set displayed text size
                if(density<=DisplayMetrics.DENSITY_HIGH) {
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
                }else if(density==DisplayMetrics.DENSITY_XHIGH) {

                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                }else if(density==DisplayMetrics.DENSITY_420){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
                }
                else if(density>=DisplayMetrics.DENSITY_XXHIGH){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                }
                t.setTextColor(getResources().getColor(android.R.color.white));
                return t;
            }
        });







        switcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // create a TextView
                TextView t = new TextView(MainActivity.this);
                // set the gravity of text to top and center horizontal

                t.setGravity(Gravity.START);
                t.setTypeface(face);
                t.setSingleLine(true);
                t.setEllipsize(TextUtils.TruncateAt.END);
                t.setIncludeFontPadding(false);
                t.setLineSpacing(0f,0f);
                // set displayed text size
                if(density<=DisplayMetrics.DENSITY_HIGH) {
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                }else if(density==DisplayMetrics.DENSITY_420){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,22);
                }else {

                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,19);
                }
                t.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
                return t;
            }
        });

        tagSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                 
                // create a TextView
                TextView t = new TextView(MainActivity.this);
                t.setTypeface(face);
                t.setIncludeFontPadding(false);
                t.setLineSpacing(0f,0f);
                // set the gravity of text to top and center horizontal
                t.setGravity(Gravity.START);
                // set displayed text size
                if(density<=DisplayMetrics.DENSITY_HIGH) {
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                }else if(density==DisplayMetrics.DENSITY_420){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,22);
                }
                else {
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,19);
                }
                t.setTextColor(getResources().getColor(android.R.color.holo_purple));
                return t;
            }
        });

        bitrateSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                 
                // create a TextView
                TextView t = new TextView(MainActivity.this);
                t.setTypeface(face);
                // set the gravity of text to top and center horizontal
                t.setGravity(Gravity.START);
                t.setSingleLine(true);
                t.setIncludeFontPadding(false);
                t.setLineSpacing(0f,0f);
                // set displayed text size

                if(density<=DisplayMetrics.DENSITY_HIGH) {
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                }else if(density==DisplayMetrics.DENSITY_XHIGH) {

                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,19);
                }else if(density==DisplayMetrics.DENSITY_420){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,22);
                }
                else if(density>=DisplayMetrics.DENSITY_XXHIGH){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,19);
                }
                t.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                t.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                return t;
            }
        });

        bitValueSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // create a TextView
                TextView t = new TextView(MainActivity.this);
                int maxLength = 10;
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxLength);
                t.setFilters(fArray);
                t.setTypeface(face);
                t.setIncludeFontPadding(false);
                t.setLineSpacing(0f,0f);
                // set the gravity of text to top and center horizontal
                t.setGravity(Gravity.START);
                t.setSingleLine(true);
                // set displayed text size
                if(density<=DisplayMetrics.DENSITY_HIGH) {
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                }else if(density==DisplayMetrics.DENSITY_XHIGH) {

                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,19);
                }else if(density==DisplayMetrics.DENSITY_420){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,22);
                }
                else if(density>=DisplayMetrics.DENSITY_XXHIGH){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,19);
                }

                t.setEllipsize(TextUtils.TruncateAt.END);
                t.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                t.setTextColor(getResources().getColor(android.R.color.white));
                return t;
            }
        });

        statusValueSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                 
                // create a TextView
                TextView t = new TextView(MainActivity.this);
                // set the gravity of text to top and center horizontal
                t.setGravity(Gravity.START);
                t.setTypeface(face);
                t.setIncludeFontPadding(false);
                t.setLineSpacing(0f,0f);

                // set displayed text size
                if(density<=DisplayMetrics.DENSITY_HIGH) {
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
                }else if(density==DisplayMetrics.DENSITY_XHIGH) {

                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                }else if(density==DisplayMetrics.DENSITY_420){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
                }
                else if(density>=DisplayMetrics.DENSITY_XXHIGH){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                }
                t.setEllipsize(TextUtils.TruncateAt.END);
                t.setSingleLine(true);
                t.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                t.setTextColor(getResources().getColor(android.R.color.white));
                return t;
            }
        });

        statusTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                 
                // create a TextView
                TextView t = new TextView(MainActivity.this);
                t.setTypeface(face);
                // set the gravity of text to top and center horizontal
                t.setGravity(Gravity.START);
                t.setIncludeFontPadding(false);
                t.setLineSpacing(0f,0f);
                // set displayed text size
                if(density<=DisplayMetrics.DENSITY_HIGH) {
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
                }else if(density==DisplayMetrics.DENSITY_XHIGH) {

                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                }else if(density==DisplayMetrics.DENSITY_420){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
                }
                else if(density>=DisplayMetrics.DENSITY_XXHIGH){
                    t.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                }
                t.setEllipsize(TextUtils.TruncateAt.END);
                t.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
               // t.setTypeface(Typeface.DEFAULT,BOLD);
                t.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
                return t;
            }
        });

        switcher.setMeasureAllChildren(false);
         tagSwitcher.setMeasureAllChildren(false);
        bitValueSwitcher.setMeasureAllChildren(false);
        bitrateSwitcher.setMeasureAllChildren(false);
         statusTextSwitcher.setMeasureAllChildren(false);
        statusValueSwitcher.setMeasureAllChildren(false);
        trackValueSwitcher.setMeasureAllChildren(false);
        trackSwitcher.setMeasureAllChildren(false);



        // Declare in and out animations and load them using AnimationUtils class
         in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
         out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
         fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
         fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
         up = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
         down = AnimationUtils.loadAnimation(this, R.anim.slide_out_up);

        // set the animation type to TextSwitcher
        switcher.setInAnimation(up);
        switcher.setOutAnimation(down);
        tagSwitcher.setInAnimation(fadeIn);
        tagSwitcher.setOutAnimation(fadeOut);
        bitrateSwitcher.setInAnimation(fadeIn);
        bitrateSwitcher.setOutAnimation(fadeOut);
        bitValueSwitcher.setInAnimation(in);
        bitValueSwitcher.setOutAnimation(out);
        statusTextSwitcher.setInAnimation(up);
        statusTextSwitcher.setOutAnimation(down);
        statusValueSwitcher.setInAnimation(in);
        statusValueSwitcher.setOutAnimation(out);
        trackSwitcher.setInAnimation(fadeIn);
        trackSwitcher.setOutAnimation(fadeOut);
        trackValueSwitcher.setInAnimation(in);
        trackValueSwitcher.setOutAnimation(out);


        //text appear on start
        switcher.setCurrentText("Loading...");
        tagSwitcher.setCurrentText("Album:");
        bitrateSwitcher.setCurrentText("   Bitrate:");
        bitValueSwitcher.setCurrentText("Loading...");
        statusTextSwitcher.setCurrentText("Volume:");
        statusValueSwitcher.setCurrentText("N/A");
        trackSwitcher.setCurrentText("Track:");
        trackValueSwitcher.setCurrentText("N/A");



        final CoordinatorLayout snackBarLayout = findViewById (R.id.snackbarLayout);
        snackBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarLayout.startAnimation(shakeSmall);
                mUpdateTagsHandler.removeCallbacks(mUpdateTags);
                mUpdateMetaHandler.removeCallbacks(mUpdateMeta);
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                mUpdateTrackHandler.removeCallbacks(mUpdateTrack);

                Vibration.Companion.vibrate(20);
                try {
                    if (misVideoPlaying) {
                        bitIndex++;
                        if (bitIndex == vbitCount)
                            bitIndex = 0;
                        bitrateSwitcher.setText(vbitStrings[bitIndex]);
                        bitValueSwitcher.setText(vbitValueStrings[bitIndex]);
                    } else {
                        bitIndex++;
                        if (bitIndex == bitCount)
                            bitIndex = 0;
                        bitrateSwitcher.setText(bitStrings[bitIndex]);
                        bitValueSwitcher.setText(bitValueStrings[bitIndex]);
                    }

                    mUpdateTagsHandler.postDelayed(mUpdateTags, 3000);

                } catch (Throwable e) {

                    bitIndex=-1;
                    Log.e("ExceptionRaised", "CoordinatorOnClick: ExceptionInUpdateMetaRunnable"+e);
                }
            } });

        snackBarLayout.setLongClickable(true);
        snackBarLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Bundle bundleMeta = getInfoBundleMeta();
                Bundle bundleTags = getInfoBundleTags();
                String mediaId = mediaController.getMetadata().getDescription().getMediaId();
                boolean isVideoFile = false;
                if (mediaController.getMetadata().getString("VIDEO").equals("true")) {
                    isVideoFile = true;
                }
                String songPath = mediaController.getMetadata().getString("SONGPATH");
                Vibration.Companion.vibrate(20);
                inflateInfoWindow(getApplicationContext(),bundleMeta,songPath,isVideoFile,true,mediaId,bundleTags,false,null,null);
                return true;
            }
        });

        final RelativeLayout songPanel=findViewById(R.id.song_panel);
        songPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switcher.startAnimation(shakeSmall);
                tagSwitcher.startAnimation(shakeSmall);

                mUpdateTagsHandler.removeCallbacks(mUpdateTags);
                mUpdateMetaHandler.removeCallbacks(mUpdateMeta);
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                mUpdateTrackHandler.removeCallbacks(mUpdateTrack);
                Vibration.Companion.vibrate(20);

                ++currentIndex;

                if (currentIndex == messageCount)
                    currentIndex = 0;


                tagSwitcher.setText(stringTag[currentIndex]);
                switcher.setText(strings[currentIndex]);

                mUpdateTagsHandler.postDelayed(mUpdateTags,3000);

            }
        });

        songPanel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle bundleMeta = getInfoBundleMeta();
                Bundle bundleTags = getInfoBundleTags();
                String mediaId = mediaController.getMetadata().getDescription().getMediaId();
                boolean isVideoFile = false;
                if (mediaController.getMetadata().getString("VIDEO").equals("true")) {
                    isVideoFile = true;
                }
                String songPath = mediaController.getMetadata().getString("SONGPATH");
                Vibration.Companion.vibrate(20);
                inflateInfoWindow(getApplicationContext(),bundleMeta,songPath,isVideoFile,false,mediaId,bundleTags,false,null,null);
                return true;
            }
        });

        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statusLayout.startAnimation(shakeSmall);
                mUpdateTagsHandler.removeCallbacks(mUpdateTags);
                mUpdateMetaHandler.removeCallbacks(mUpdateMeta);
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                mUpdateTrackHandler.removeCallbacks(mUpdateTrack);
                Vibration.Companion.vibrate(20);
                ++trackIndex;

                if (trackIndex == trackCount)
                    trackIndex = 0;

                trackSwitcher.setText(stringTrackSwitcher[trackIndex]);
                trackValueSwitcher.setText(stringTrackValue[trackIndex]);

                ++statusIndex;

                if (statusIndex == statusCount)
                    statusIndex = 0;

                statusTextSwitcher.setText(stringStatusSwitcher[statusIndex]);
                statusValueSwitcher.setText(stringValueSwitcher[statusIndex]);

                mUpdateTagsHandler.postDelayed(mUpdateTags,3000);


            }
            });


        statusLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                statusLayout.startAnimation(shakeSmall);
                Vibration.Companion.vibrate(50);
                if(isTrackLayout){

                    removeTrackLayout();
                }else {
                    inflateTrackLayout();
                }
                return true;
            }
            });


        if (Build.VERSION.SDK_INT >= 23) {

            SharedPreferences permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionRequired[0]) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(MainActivity.this, permissionRequired[1]) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(MainActivity.this, permissionRequired[2]) != PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(MainActivity.this, permissionRequired[3]) != PackageManager.PERMISSION_GRANTED){

                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionRequired[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionRequired[2])
                        || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionRequired[3])) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Need Multiple Permissions");
                    builder.setMessage("Sona Player needs Storage and Phone Status Monitoring permissions.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(MainActivity.this, permissionRequired, PERMISSION_CALLBACK_CONSTANT);
                        }
                    });
                    builder.show();

                } else if (permissionStatus.getBoolean(permissionRequired[0], false)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Need Multiple Permissions");
                    builder.setMessage("Sona Player needs Storage and Phone Status Monitoring permissions");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            sentToSetting = true;
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Toast.makeText(getBaseContext(), "Go to permissions to Grant Storage and Media controls", Toast.LENGTH_LONG).show();

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {

                    ActivityCompat.requestPermissions(MainActivity.this, permissionRequired, PERMISSION_CALLBACK_CONSTANT);

                }
                // txtPermission.setText("Permission Required");
               SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(permissionRequired[0], true);
                editor.commit();

            } else {

                proceedAfterPermission();


            }
        } else {
            mMediaBrowserHelper = new MediaBrowserConnection(this);
            mMediaBrowserHelper.registerCallback(new MediaBrowserListener());
            mMediaBrowserHelper.onStart();

        }




        EqualizerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHiderHandler.removeCallbacks(mViewHider);

                if(sleepflag)
                {
                    sleepLayout.setVisibility(View.GONE);
                    sleepflag=false;
                }

                EqualizerBtn.startAnimation(myAnim);
                if (!equalizerFlag) {
                    albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);

                    if (videoView != null) {
                        if (misVideoPlaying) {
                            videoView.pause();
                            clearEqualizers();
                            videoView = null;
                            inflateAudioSeekbar();
                        }
                    }
                    clearVisualizer(clearVisualizerIndex);
                    inflateEquailizerPager(1);
                    slideUp(equalizerPagerLayout);
                    equalizerFlag = true;


                }else {

                    Vibration.Companion.vibrate(20);
                    slideDown(equalizerPagerLayout);
                    equalizerFlag=false;
                    viewPager.setAdapter(null);
                    reverbPager.setAdapter(null);
                    EqViewPager.setAdapter(null);

                    if(misVideoPlaying){

                        inflateVideoView();
                        videoView.seekTo((int)mediaController.getPlaybackState().getPosition());
                    }else {
                        inflateVisualizer();
                        startVisualizer();}


                        nextPage.clearAnimation();
                    nextPage.setVisibility(View.GONE);
                    prevPage.clearAnimation();
                    prevPage.setVisibility(View.GONE);


                }
            }
        });

        EqualizerBtn.setLongClickable(true);
        EqualizerBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                viewHiderHandler.removeCallbacks(mViewHider);
                albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);

                if(sleepflag)
                {
                    sleepLayout.setVisibility(View.GONE);
                    sleepflag=false;
                }

                EqualizerBtn.startAnimation(myAnim);
                if (!equalizerFlag) {

                    if (videoView != null) {
                        if (misVideoPlaying) {
                            videoView.pause();
                            clearEqualizers();
                            videoView = null;
                            inflateAudioSeekbar();
                        }
                    }
                    clearVisualizer(clearVisualizerIndex);
                    inflateEquailizerPager(0);
                    slideUp(equalizerPagerLayout);
                    equalizerFlag = true;


                }else {
                    slideDown(equalizerPagerLayout);
                    viewPager.setAdapter(null);
                    reverbPager.setAdapter(null);
                    EqViewPager.setAdapter(null);
                    equalizerFlag = false;
                    if (misVideoPlaying) {

                        inflateVideoView();
                        videoView.seekTo((int) mediaController.getPlaybackState().getPosition());
                    } else {
                        inflateVisualizer();
                        startVisualizer();
                    }
                    nextPage.clearAnimation();
                    nextPage.setVisibility(View.GONE);
                    prevPage.clearAnimation();
                    prevPage.setVisibility(View.GONE);

                }

                return true;
            }
        });


        mainOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                   Vibration.Companion.vibrate(20);

                    String title = mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE);
                    String fileName = mediaController.getMetadata().getString("FILENAME");
                    int durationRaw = (((int) mediaController.getMetadata().getLong(MediaMetadataCompat.METADATA_KEY_DURATION)) / 1000);
                    String duration = timeConvert(durationRaw);
                    String format = mediaController.getMetadata().getString("SONGFORMAT");
                    String size = mediaController.getMetadata().getString("SIZE");
                    String songMeta = duration + " | " + format + " | " + size;
                    String songPath = mediaController.getMetadata().getString("SONGPATH");
                  //  String albumUri = mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);
                    String mediaId = mediaController.getMetadata().getDescription().getMediaId();
                    boolean isVideoFile = false;
                    if (mediaController.getMetadata().getString("VIDEO").equals("true")) {
                        isVideoFile = true;
                    }

                    if (title.length() < 15) {
                        title = title + " - " + fileName;
                    }

                    Bundle bundleData = getInfoBundleMeta();
                    Bundle bundleTags = getInfoBundleTags();

                    String subTitle = mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ALBUM) + " - " + mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST);

                    inflateOptionMenu(getApplicationContext(), title, subTitle, songMeta, songPath, mediaId, isVideoFile,bundleData,bundleTags);
                }catch (Throwable e){
                    Snackbar  snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "Player Not Initialized", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                }
            }
        });

    }


    public  void startAnimListener(){

        zoomin.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                 

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                 


            }

            @Override
            public void onAnimationEnd(Animation arg0) {

                if(mainAlbumArt!=null && mainAlbumArt.getVisibility()==View.VISIBLE && mIsPlaying)
                    mainAlbumArt.startAnimation(zoomout);


            }
        });

        zoomout.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                 

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                 



            }

            @Override
            public void onAnimationEnd(Animation arg0) {

                if(mainAlbumArt!=null && mainAlbumArt.getVisibility()==View.VISIBLE && mIsPlaying)
                    mainAlbumArt.startAnimation(zoomin);

            }
        });


    }


    public void clickManual(){

        if(videoView==null) {
            supressPlay = true;
            Log.e("SupressPlay", "clickManual:ValueChanged:true" );
        }

        final ImageButton PlayPause = findViewById(R.id.PlayPause);
        PlayPause.setImageResource(R.drawable.btn_play);
        if (isPlay) {
            Log.e("IsPaused Inoked", "clickManual: pause");

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
            // fader.runAlphaAnimation(MainActivity.this,PlayPause.getId());
            PlayPause.setImageResource(R.drawable.btn_play);
            if(misVideoPlaying && videoView!=null){
                misVideoPaused=true;
                supressOnPause=true;
                videoseekPos=videoView.getCurrentPosition();
                videoView.pause();
                targetVolume= mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);


            }else {
                mMediaBrowserHelper.getTransportControls().pause();

            }
            isPlay = false; // reverse
        } else {

            Log.e("IsPlay Inoked", "clickManual: play");
            //fader.runAlphaAnimation(MainActivity.this,PlayPause.getId());

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


            PlayPause.setImageResource(R.drawable.btn_pause);
            if(misVideoPlaying && videoView!=null){

                if(videofinished){
                    if(fadeInFadeOut)
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,1,0);
                    misVideoPaused = false;
                    Log.d("VideoView OnPlay", "clickManual: Invoked");
                    videoView.seekTo(0);
                    videoView.start();
                    if(fadeInFadeOut)
                    volDuckUpHandler.postDelayed(duckUpVolumeRunnable,0);

                }else {
                    if(fadeInFadeOut)
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,1,0);
                    misVideoPaused = false;
                    Log.d("VideoView OnPlay", "clickManual: Invoked");
                    videoView.seekTo(videoseekPos);
                    videoView.start();
                    if(fadeInFadeOut)
                    volDuckUpHandler.postDelayed(duckUpVolumeRunnable,0);

                }
            }else {
                mMediaBrowserHelper.getTransportControls().play();
            }
            isPlay = true;
            mIsStop=false;// reverse
        }
    }
    @Override
    protected void onStart(){

        super.onStart();
        getWindow().setBackgroundDrawable(null);


        if(ApplicationContextProvider.getFontFlag()) {
            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                    "Font "+ApplicationContextProvider.fontNameArray[fontIndex]+" Applied", Snackbar.LENGTH_SHORT);
            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
            snack.show();

            ApplicationContextProvider.setFontFlag(false);
        }



    }

    @Override
    protected  void  onPause(){
    super.onPause();
      //  overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        isActivityRunning=false;
        Log.d("activityPaused", "onPause: MainActivityPaused");
        if(videoView!=null){
            videoseekPos= videoView.getCurrentPosition();
            currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putLong("currentPosition",videoView.getCurrentPosition());
            editor.commit();
            Log.d("LastVideoTime", "onPause:Saved is: "+(long)videoseekPos);
            if(misVideoPaused) {
                supressOnPause = true;
            }
            videoView.pause();
            videoView.stopPlayback();
            clearEqualizers();
        }

            if(!SonaHeartService.isServiceRunning && !isFullScreen ) {
                Intent serviceIntent = new Intent(getApplicationContext(), HeadsetTriggerService.class);
                getApplicationContext().startService(serviceIntent);
                Log.d("HeadSetTriggerService", "onMainActivity: HeadsetWatchdogStarted");
            }

        if(customAlbum){

            if(mainAlbumArt!=null)
            mainAlbumArt.clearAnimation();
            zoomin.cancel();
            zoomout.cancel();
            albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
        }

        if(isTrackLayout)
            removeTrackLayout();

        if(!startup && mediaController!=null && SonaHeartService.isServiceRunning && !isFullScreen) {
            resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
            mediaController.sendCommand("MainPaused", null, null);
        }
        Log.d("activityPausedOut", "onPause: MainActivityPausedOut");

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]permissions ,@NonNull int[]grantResults){
        super.onRequestPermissionsResult( requestCode,permissions,grantResults);
        if(requestCode==PERMISSION_CALLBACK_CONSTANT){

           // boolean allgranted=false;
            for (int grantResult : grantResults) {

                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;

                } else {
                    allgranted = false;
                    break;

                }
            }
            if(allgranted){

                proceedAfterPermission();

            }else if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionRequired[3])){

                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("Sona Player needs Storage and Phone status Monitoring permissions");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
                builder.show();

            }else {

                Toast.makeText(getBaseContext(),"Unable to get permissions",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("OnactivityResult", "onActivityResult:Invoked ");
        if (requestCode == REQUEST_PERMISSION_SETTING) {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                proceedAfterPermission();

            }
        }

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {

                    String mediaId = data.getStringExtra("mediaId");
                    boolean queueStatus=data.getBooleanExtra("queueStatus",false);

                    if(queueStatus && !isQueueActive){
                        resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                        Bundle bundleQT = new Bundle();
                        bundleQT.putBoolean("QueueSwitch", true);
                        isQueueActive=true;
                        mediaController.sendCommand("QueueToggle", bundleQT, resultReceiver);
                        stringTrackSwitcher[0]="Queue:";
                        Snackbar  snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                "Queue Enabled", Snackbar.LENGTH_SHORT);
                        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                        snack.show();
                    }else if(!queueStatus && isQueueActive){
                        resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                        Bundle bundleQT = new Bundle();
                        bundleQT.putBoolean("QueueSwitch", false);
                        isQueueActive=false;
                        mediaController.sendCommand("QueueToggle", bundleQT, resultReceiver);
                        stringTrackSwitcher[0]="Track:";

                        Snackbar  snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                "Queue Disabled", Snackbar.LENGTH_SHORT);
                        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                        snack.show();
                    }
                    try {
                        if (Objects.equals(mediaController.getMetadata().getDescription().getMediaId(), mediaId)) {
                            Log.e("SelectedMediaId", "onActivityResult: MediaIdIsSame Returning ");
                            return;
                        }
                    }catch (Throwable e){
                        e.printStackTrace();
                    }

                   if(videoView!=null) {
                        middleParent.removeAllViews();
                        videoView=null;
                    }

                    if(mediaId!=null)
                    mediaController.getTransportControls().playFromMediaId(mediaId, null);
                    Log.d("OnactivityResultOut", "onActivityResultOut:MediaIDReceived "+mediaId);

                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    isActivityRunning=true;
                    isFullScreen = true;
                    videoseekPos = data.getIntExtra("seekPos", 0);
                    brightnessCurrent=data.getIntExtra("bright",brightnessSystem);
                    videoView=null;
                    inflateVideoView();
                    videoView.seekTo(videoseekPos);
                    isRepeatAB=data.getBooleanExtra("repeatABflag",false);
                    Log.e("ValueOfRepeatAB", "onActivityResult: "+isRepeatAB );
                    if(isRepeatAB){
                        repeatDurationA=data.getIntExtra("repeatStartTime",-1);
                        repeatDurationB=data.getIntExtra("repeatEndTime",-1);
                        Log.d("repeatTimeA", "onActivityResult: "+repeatDurationA);
                        Log.d("repeatTimeB", "onActivityResult: "+repeatDurationB);
                        if(repeatDurationA!=-1 && repeatDurationB!=-1 && repeatDurationB>repeatDurationA) {
                            Log.d("EnterendInRepeatLoop", "onActivityResult: Invoked");
                            isRepeatActive=true;
                            repeatTimeA.setText(timeConvert(repeatDurationA));
                            repeatTimeB.setText(timeConvert(repeatDurationB));

                            Snackbar  snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "RepeatAB Enabled", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();

                        }
                    }

                    Log.d("VideoSeekPosReceived", "onActivityResult: SeekPos=" + videoseekPos);

                }

                break;
            case 3:

                if(resultCode==RESULT_OK){
                    slideMode=true;
                    albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                    SlideArtIndex=data.getIntExtra("artIndex",0);
                    String slideRId=albumArtList.get(SlideArtIndex);

                    Log.e("MainActivityArtIndex", "FromIntent: "+data.getIntExtra("artIndex",0));

                    albumArt=loadScaledBitmap(true,slideRId);
                    mainAlbumArt.setImageBitmap(albumArt);
                    albumImage.setImageBitmap(albumArt);
                    mainAlbumArt.startAnimation(zoomin);
                    albumArtChangerHandler.postDelayed(mAlbumArtChanger,10000);
                }

                break;

            case 4:
                if (resultCode == RESULT_OK) {
                    boolean vFlag = data.getBooleanExtra("vFlag", false);
                    final boolean albumFlag = data.getBooleanExtra("albumFlag", false);
                    boolean crossFlag =data.getBooleanExtra("crossFlag", false);
                    boolean fadingFlag =data.getBooleanExtra("fadingFlag", false);
                    boolean audioRejectF =data.getBooleanExtra("audioReject", false);
                    boolean autoResumeFlag=data.getBooleanExtra("autoResumeFlag", false);
                    boolean crossTimeFlag=data.getBooleanExtra("crossTimeFlag", false);
                    boolean manualCrossFlag=data.getBooleanExtra("manualCrossFlag", false);
                    boolean fadeTimeFlag=data.getBooleanExtra("fadeTimeFlag", false);
                    boolean powerFlag=data.getBooleanExtra("powerFlag",false);
                    boolean playerWakeFlag=data.getBooleanExtra("playerWakeFlag",false);
                    boolean screenOnFlag=data.getBooleanExtra("screenOnFlag",false);
                    boolean alterEqFlag=data.getBooleanExtra("AlterEqFlag",false);
                    boolean windowAnim=data.getBooleanExtra("WindowAnim",false);

                    if (vFlag && !audioRejectF) {
                        Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                "Refreshing Videos...", Snackbar.LENGTH_SHORT);
                        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                        snack.show();
                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
                            public void run() {
                                Log.d("VideoRefresh", "runOnActivityResult: VideoRefreshHandlerInvoked");
                                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                                mediaController.sendCommand("scanVideoMedia", null, resultReceiver);
                                mediaController.sendCommand("nextSong", null, resultReceiver);
                            }
                        }, 1500);

                    }

                    if(albumFlag){
                        boolean customAlbums = data.getBooleanExtra("customAlbum", false);


                        if(customAlbums){
                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "Refreshing AlbumArt...", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();

                            if (customAlbum) {
                                mainAlbumArt.clearAnimation();
                                mainAlbumArt.setImageBitmap(null);
                                mainAlbumArt.setVisibility(View.GONE);
                                albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                                albumArt = null;
                                albumImage.setImageResource( R.drawable.main_art);

                            }

                            new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
                                public void run() {
                                    Log.d("AlbumRefresh", "runOnActivityResult: AlbumRefreshHandlerInvoked");
                                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                                    mediaController.sendCommand("scanAlbums", null, resultReceiver);
                                }
                            }, 1500);


                        }else if(customAlbum) {


                            new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
                                public void run() {
                                    Log.d("AlbumRefresh", "runOnActivityResult: AlbumRefreshHandlerInvoked");
                                    customAlbum=false;
                                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                                    mediaController.sendCommand("clearAlbums", null, resultReceiver);
                                    albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                                    zoomin = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_in_default);
                                    zoomout = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_out_default);

                                    albumArt=mediaController.getMetadata().getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
                                    if(albumArt==null){
                                        try {
                                            MediaMetadataRetriever metadataRetrieverG=new MediaMetadataRetriever();
                                            metadataRetrieverG.setDataSource(mediaController.getMetadata().getString("SONGPATH"));
                                            byte[] art = metadataRetrieverG.getEmbeddedPicture();
                                            albumArt = BitmapFactory.decodeByteArray(art, 0, Objects.requireNonNull(art).length);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if(albumArt!=null) {
                                        albumImage.setImageBitmap(albumArt);
                                        if(mainAlbumArt!=null) {
                                            mainAlbumArt.clearAnimation();
                                            mainAlbumArt.setImageBitmap(albumArt);
                                            mainAlbumArt.startAnimation(zoomin);
                                            startAnimListener();
                                            if(albumAutoHide) {
                                                albumArtHiderHandler.postDelayed(mHideAlbum, 7000);
                                            }

                                        }
                                    }
                                    else {
                                        albumImage.setImageResource(R.drawable.main_art);
                                        if(mainAlbumArt!=null) {
                                            mainAlbumArt.clearAnimation();
                                            mainAlbumArt.setVisibility(View.GONE);
                                        }

                                    }

                                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout), "Custom Art Disabled", Snackbar.LENGTH_SHORT);
                                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                                    snack.show();
                                }
                            }, 1500);


                        }

                    }

                    if(crossFlag){

                        boolean mCrosssfade=data.getBooleanExtra("mCrossFade", true);
                        if(mCrosssfade){
                            resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                            Bundle bundleC = new Bundle();
                            bundleC.putBoolean("crossFading", true);
                            mediaController.sendCommand("CrossFade", bundleC, resultReceiver);
                        }else {

                            resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                            Bundle bundleC = new Bundle();
                            bundleC.putBoolean("crossFading", false);
                            mediaController.sendCommand("CrossFade", bundleC, resultReceiver);


                        }

                    }

                    if(fadingFlag){
                        boolean fadeInOut=data.getBooleanExtra("FadeInFadeOut", true);

                            if(fadeInOut){
                                fadeInFadeOut=true;
                                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                                Bundle bundleF = new Bundle();
                                bundleF.putBoolean("Fading", true);
                                mediaController.sendCommand("FadingInOut", bundleF, resultReceiver);

                            }else {
                                fadeInFadeOut=false;
                                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                                Bundle bundleF = new Bundle();
                                bundleF.putBoolean("Fading", false);
                                mediaController.sendCommand("FadingInOut", bundleF, resultReceiver);

                            }

                    }

                    if(audioRejectF){
                        Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                "Refreshing Media...", Snackbar.LENGTH_SHORT);
                        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                        snack.show();
                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
                            public void run() {
                                Log.d("AudioRefresh", "runOnActivityResult: AudioRefreshHandlerInvoked");
                                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                                mediaController.sendCommand("scanMedia", null, resultReceiver);
                                mediaController.sendCommand("nextSong", null, resultReceiver);
                            }
                        }, 1500);


                    }
                    if(autoResumeFlag){
                        boolean autoResume=data.getBooleanExtra("autoResume", true);
                        Bundle bundleR = new Bundle();
                        bundleR.putBoolean("autoResume", autoResume);
                        resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                        mediaController.sendCommand("AutoResume", bundleR, resultReceiver);

                    }

                    if(crossTimeFlag||manualCrossFlag||fadeTimeFlag){
                        if(crossTimeFlag){
                            resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                            mediaController.sendCommand("CrossfadeTime", null, resultReceiver);
                        }
                        if(manualCrossFlag){
                            resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                            mediaController.sendCommand("ManualFadeTime", null, resultReceiver);
                        }
                        if(fadeTimeFlag){
                            resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                            mediaController.sendCommand("FadeTime", null, resultReceiver);
                        }
                    }

                    if(powerFlag){
                        isPowerEnabled=currentState.getBoolean("powerFlag",true);
                        resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                        Bundle bundlePF = new Bundle();
                        mediaController.sendCommand("PowerSaver", bundlePF, resultReceiver);

                        try {
                            if (isPowerEnabled)
                                registerReceiver(killActivity, new IntentFilter("killActivity"));
                            else
                                unregisterReceiver(killActivity);
                        }catch (Throwable e){

                            e.printStackTrace();
                        }
                        Log.d("OnactivityResult", "onActivityResult:PowerSaverInvoked ");
                    }

                    if(playerWakeFlag){

                        resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                        Bundle bundlePWF = new Bundle();
                        bundlePWF.putBoolean("playerWakeLock",data.getBooleanExtra("playerWakeLock",false) );
                        mediaController.sendCommand("WakeLock", bundlePWF, resultReceiver);
                        Log.d("OnactivityResult", "onActivityResult:PlayerWakeLOckInvoked ");
                    }
                    if(screenOnFlag){

                        if(data.getBooleanExtra("screenStatus",true)){

                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            Log.d("ScreenFlag", "onActivityResult: ScreenOnEnabled");

                        }else {

                            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            Log.d("ScreenFlag", "onActivityResult: ScreenDisabled");

                        }
                    }

                    if(alterEqFlag){
                        resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                        Bundle bundleAE = new Bundle();
                        bundleAE.putBoolean("alterEqMode",data.getBooleanExtra("alterEqMode",false) );
                        mediaController.sendCommand("AlterEqMode", bundleAE, resultReceiver);
                        Log.d("OnactivityResult", "onActivityResult:AlterEqModeInvoked ");
                    }
                    if(windowAnim){
                        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        enterAnim=currentState.getInt("enterAnim",0);
                        exitAnim=currentState.getInt("exitAnim",0);
                    }
                }

            case 5:
               if (resultCode == Activity.RESULT_OK) {
                    sdCardUri = data.getData();
                    if(sdCardUri!=null) {
                        final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION| Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(sdCardUri, takeFlags);

                        Log.d("SDcardUri", "onActivityResult: Uri:  " + sdCardUri.toString());
                        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = currentState.edit();
                        editor.putString("SDCardUri", sdCardUri.toString());
                        editor.putBoolean("canReadSD",true);
                        editor.commit();
                         if(TagEditFlag){

                            SonaToast.setToast(getApplicationContext(),"Permission Granted",0);
                            TagEditFlag=false;

                        } else {
                            deleteItem(deletePath, deleteMediaId);
                        }
                    }
                }
                break;


            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                if (resultCode == RESULT_OK && data!=null) {

                    try {

                            CropImage.ActivityResult result = CropImage.getActivityResult(data);
                                Uri resultUri = result.getUri();
                                Bitmap selectedBitmap = decodeUriAsBitmap(resultUri);

                        infoTabDialog.dismiss();
                        inflateInfoWindow(getApplicationContext(), bundleMetaTemp, songDataTemp, isVideoFileTemp, false, mediaIdTemp, bundleTagsTemp, true, selectedBitmap, bundleRestoreText);

                    }catch (Throwable e){

                        SonaToast.setToast(getApplicationContext(),"Something Went Wrong",0);
                        inflateInfoWindow(getApplicationContext(),bundleMetaTemp,songDataTemp,isVideoFileTemp,false,mediaIdTemp,bundleTagsTemp,true,null,bundleRestoreText);

                    }

                }
                break;
        }

    }
    private Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap ;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            final boolean delete = new File(Objects.requireNonNull(uri.getPath())).delete();
            if(delete){
                Log.d("ImageCacheDeleted", "decodeUriAsBitmap:Delete Successful ");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }



        private void proceedAfterPermission(){
            mMediaBrowserHelper = new MediaBrowserConnection(this);
            mMediaBrowserHelper.registerCallback(new MediaBrowserListener());
            mMediaBrowserHelper.onStart();

        }

        @Override
        protected void onResume(){
            super.onResume();
            Animations.Animations(MainActivity.this,exitAnim);
            isActivityRunning=true;
            SonaHeartService.isActivityDestroyed=false;
            getWindow().setBackgroundDrawable(null);
            Log.d("activityResume", "onPause: MainActivityResumed");

            if(videoView!=null && !isFullScreen){
                if(mediaController.getPlaybackState().getState()==PlaybackStateCompat.STATE_PLAYING)
                    videoseekPos=(int) mediaController.getPlaybackState().getPosition();

                mediaController.getTransportControls().stop();
                clearEqualizers();
                videoView.stopPlayback();
                videoView=null;
                inflateVideoView();
                videoView.seekTo(videoseekPos);
                mSeekbarUpdateHandler.postDelayed(mUpdateseekbar,1000);
                Log.d("VideoViewResume", "onActivityResume: Invoked!");

            }
            isFullScreen=false;

            if(customAlbum && mIsPlaying && !slideMode){

                albumArtChangerHandler.postDelayed(mAlbumArtChanger,0);
            }else {

                slideMode = false;
            }

            if( !startup) {
                stopService(new Intent(getBaseContext(), HeadsetTriggerService.class));
                Log.d("HeadSetTriggerService", "onCommand: HeadsetWatchdogKilled");

            }

            MusicLibrary.registerMetaUIUpdateListener(this);

            Log.d("activityResumeOut", "onPause: MainActivityResumedOut");

        }

        @Override
        protected  void onPostResume(){
            super.onPostResume();
            SonaHeartService.isActivityDestroyed=false;

            if(sentToSetting){
                if(ActivityCompat.checkSelfPermission(MainActivity.this,permissionRequired[0])==PackageManager.PERMISSION_GRANTED){
                    proceedAfterPermission();

                }
            }

        }


        private void expandNotification(){
            try {
                @SuppressLint("WrongConstant")
                Object sbservice = getSystemService( "statusbar" );
                Class<?> statusbarManager = Class.forName( "android.app.StatusBarManager" );
                Method showsb = statusbarManager.getMethod("expandNotificationsPanel");
                showsb.invoke( sbservice );
                Log.d("statusExpand", "onOptionsItemSelected:Invoked ");
            }catch (Throwable e){
                e.printStackTrace();
            }
        }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Animations.Animations(MainActivity.this,exitAnim);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.toggle_notyfication:

                if (videoViewDisabled && SonaHeartService.playlistSize>0) {

                    SharedPreferences OreoNotification = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    boolean oreoStyle = OreoNotification.getBoolean("oreoStyle", false);

                    if (oreoStyle) {

                        mediaController.sendCommand("oreoStyle", null, null);
                        Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                "Advanced Style Set", Snackbar.LENGTH_SHORT);
                        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                        snack.show();
                        expandNotification();

                    } else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            mediaController.sendCommand("oreoStyle", null, null);
                           Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "Colorized Style Set", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                            expandNotification();
                        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {

                            mediaController.sendCommand("oreoStyle", null, null);
                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "Native Style Set", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                            expandNotification();
                        } else {

                              Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "Not Supported!", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                        }
                    }

                } else {

                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "Notification Unavailable!", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                }
                return true;
            case R.id.toggle_visualizer:

                try {
                    if (!misVideoPlaying) {

                        boolean isAlbumHidden = false;
                        if (mainAlbumArt.getVisibility() != View.VISIBLE)
                            isAlbumHidden = true;

                        startVisualizer();

                        if (isAlbumHidden) {
                            zoomin.cancel();
                            zoomout.cancel();
                            mainAlbumArt.clearAnimation();
                            albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                            mainAlbumArt.setVisibility(View.GONE);
                        }

                    } else {

                        Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                "Visualizer Unavailable!", Snackbar.LENGTH_SHORT);
                        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                        snack.show();
                    }
                }catch (Throwable e){

                    e.printStackTrace();
                }
                break;

            case R.id.action_refresh_media:

                Snackbar  snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        "Refreshing...", Snackbar.LENGTH_SHORT);
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();

                new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
                    public void run() {

                        resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                        mediaController.sendCommand("scanMedia", null, resultReceiver);
                    }}, 300);

                break;

            case R.id.action_sona_font:
                  showFontDialog();
                break;

            case R.id.action_terminate:

                AlertDialog.Builder builder1 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
                builder1.setMessage("Termination will Kill Sona Player Completely! ");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "KILL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                try {

                                    mediaController.getTransportControls().stop();
                                    MainActivity.this.onDestroy();
                                    finishAffinity();
                                    finishAndRemoveTask();
                                    stopService(new Intent(MainActivity.this, SonaHeartService.class));
                                    //android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(0);



                                }catch (Throwable e){

                                    Log.e("ExceptionRaised", "WhileKillingApp: "+e);
                                    System.exit(0);
                                }
                            }
                        });

                builder1.setNegativeButton(
                        "SUSPEND",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                builder1.setNeutralButton(
                        "RESTART",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    mediaController.getTransportControls().stop();
                                    MainActivity.this.onDestroy();
                                    finishAffinity();
                                    finishAndRemoveTask();
                                    stopService(new Intent(MainActivity.this, SonaHeartService.class));
                                    ProcessPhoenix.triggerRebirth(getApplicationContext());
                                }catch(Throwable e){
                                    Log.e("ExceptionRaised", "WhileKillingApp: "+e);
                                    ProcessPhoenix.triggerRebirth(getApplicationContext());

                                }

                            }
                        });

                AlertDialog alert11 = builder1.create();
                if(alert11.getWindow()!=null)
                alert11.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                alert11.show();

                break;


            case R.id.action_settings:

                Intent intent = new Intent(this, PreferenceActivity.class);
                startActivityForResult(intent, 4);
                Animations.Animations(MainActivity.this,enterAnim);
                break;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.all_songs:

                sendMessage();

                break;
            case R.id.nav_albums: {

                Intent intent = new Intent(this, AlbumListActivity.class);
                startActivityForResult(intent, 1);
                Animations.Animations(MainActivity.this,enterAnim);
                break;
            }
            case R.id.nav_artists: {

                Intent intent = new Intent(this, ArtistListActivity.class);
                startActivityForResult(intent, 1);
                Animations.Animations(MainActivity.this,enterAnim);
                break;
            }
            case R.id.nav_folders: {

                Intent intent = new Intent(this, FolderListActivity.class);
                startActivityForResult(intent, 1);
                Animations.Animations(MainActivity.this,enterAnim);
                break;
            }
            case R.id.nav_queue: {
                Intent intent = new Intent(this, QueueListActivity.class);
                startActivityForResult(intent,1);
                Animations.Animations(MainActivity.this,enterAnim);
                break;
            }
            case R.id.nav_recent: {

                Intent intentR = new Intent(this, RecentListActivity.class);
                startActivityForResult(intentR, 1);
                Animations.Animations(MainActivity.this,enterAnim);

                break;
            }
            case R.id.nav_help: {

                Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        "Currently Help Unavailable", Snackbar.LENGTH_SHORT);
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();

                break;
            }
            case R.id.nav_share: {

                shareApplication();
                Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        "Preparing...", Snackbar.LENGTH_SHORT);
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();

                break;
            }
            case R.id.nav_about: {

                Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        "Currently About Unavailable", Snackbar.LENGTH_SHORT);
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();

                break;
            }
            case R.id.nav_videos:

                Intent intent = new Intent(this, VideoListActivity.class);
                //intent.putExtra("LIST", (Serializable) video_data);
                intent.putExtra("FOCUS", false);
                intent.putExtra("scrollId", mediaController.getMetadata().getDescription().getMediaId());
                startActivityForResult(intent, 1);
                Animations.Animations(MainActivity.this,enterAnim);

                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState){

        //saveInstanceState.putBoolean("ServiceState",serviceBound);
        super.onSaveInstanceState(saveInstanceState);

    }


    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState){

        super.onRestoreInstanceState(savedInstanceState);

      //  serviceBound=savedInstanceState.getBoolean("ServiceState");

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        Log.d("VolumeKeyPressed", "dispatchKeyEvent:Main: ");

        if(music_styles==null)
            return  super.dispatchKeyEvent(event);

            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (videoView==null) {
                            Log.d("VolumeKeyPressed", "dispatchKeyEvent:Case1: ");


                            if (sleepflag) {
                                slideDown(sleepLayout);
                                sleepflag = false;
                            }


                            if (!equalizerFlag) {
                                //equalizerLayout.setVisibility(View.VISIBLE);
                                clearVisualizer(clearVisualizerIndex);
                                inflateEquailizerPager(1);
                                slideUp(equalizerPagerLayout);
                                equalizerFlag = true;


                            }else {
                                EqViewPager.setCurrentItem(1);
                            }
                            autoVol = true;

                            if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) < mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
                                volKnob.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1);

                            viewHiderHandler.removeCallbacks(mViewHider);
                            viewHiderHandler.postDelayed(mViewHider, 2000);

                        }else {

                            volumeSeekbar.setAlpha(1f);
                            volSeekHiderHandler.postDelayed(volSeekHider,2000);

                            if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) < mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {

                                Log.d("InvokingVolSeekkbar", "dispatchKeyEvent: ");
                                volumeSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1);
                            }

                        }
                        return true;
                    }

                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (action == KeyEvent.ACTION_DOWN) {
                        Log.d("VolumeKeyPressed", "dispatchKeyEvent:Case2: ");

                        if (videoView==null ) {

                            if (sleepflag) {
                                slideDown(sleepLayout);
                                sleepLayout.setVisibility(View.GONE);
                                sleepflag = false;
                            }

                            if (!equalizerFlag) {
                                //equalizerLayout.setVisibility(View.VISIBLE);
                                clearVisualizer(clearVisualizerIndex);
                                inflateEquailizerPager(1);
                                slideUp(equalizerPagerLayout);
                                equalizerFlag = true;


                            }else {
                                EqViewPager.setCurrentItem(1);
                            }
                            autoVol = true;

                            viewHiderHandler.removeCallbacks(mViewHider);
                            viewHiderHandler.postDelayed(mViewHider, 2000);
                            currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                            if (volKnob.getProgress() > volKnob.getMin())
                                volKnob.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 1);

                        }
                        else {


                            volumeSeekbar.setAlpha(1f);
                            volSeekHiderHandler.postDelayed(volSeekHider,2000);

                            if (volumeSeekbar.getProgress() > 0)
                                volumeSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 1);

                        }
                        return true;
                    }


                case KeyEvent.KEYCODE_HEADSETHOOK:

                    if (action == KeyEvent.ACTION_DOWN) {
                        Log.d("HEADSETHOOK", "dispatchKeyEvent:Invoked ");

                        if(!videoViewDisabled) {
                            clickManual();
                            return true;
                        }
                        else
                          return   super.dispatchKeyEvent(event);

                    }

                default:
                    Log.d("VolumeKeyPressed", "dispatchKeyEvent:CaseDefault: ");
                    return super.dispatchKeyEvent(event);


            }
    }
    @Override
    protected void onStop(){

        super.onStop();

    }

   @Override
    protected void onDestroy()
    {
        MusicLibrary.registerMetaUIUpdateListener(null);
        isActivityRunning=false;
        videoViewDisabled=true;
        SonaHeartService.isActivityDestroyed=true;
        removeHandlers();

        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
       boolean RandomFont= currentState.getBoolean("Random",true);
       if(RandomFont) {
           if (fontIndex >= 10) {

               fontIndex = 1;
               currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
               SharedPreferences.Editor editor = currentState.edit();
               editor.putInt("fontIndex", fontIndex);
               editor.commit();
           } else {

               fontIndex++;
               currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor  editor = currentState.edit();
               editor.putInt("fontIndex", fontIndex);
               editor.commit();

           }
       }

        sleepflag=false;
        equalizerFlag=false;
        startup=true;
        videoFirstSeek=true;
        misVideoPlaying=false;
        mMediaBrowserHelper.onStop();
        try {
            if(isPowerEnabled)
            unregisterReceiver(killActivity);

            unregisterReceiver(refreshLibrary);
        }catch (Throwable e){
            e.printStackTrace();
        }

        clearVisualizer(clearVisualizerIndex);
        Log.d("OndestroyInvoked", "onDestroy: MainActivity");
        /*finish();
        this.releaseInstance();*/
        Runtime.getRuntime().gc();
        System.gc();
        super.onDestroy();


    }


   public void  startVisualizer(){
    if(sleepflag){
            slideDown(sleepLayout);
            sleepLayout.setVisibility(View.GONE);
            sleepflag=false;
        inflateVisualizer();

    }else if(equalizerFlag){
        slideDown(equalizerPagerLayout);
        equalizerFlag=false;
        inflateVisualizer();
        nextPage.clearAnimation();
        nextPage.setVisibility(View.GONE);
        prevPage.clearAnimation();
        prevPage.setVisibility(View.GONE);
    }


     clearVisualizer(clearVisualizerIndex);
      toggleVisualization(visualizerIndex,0);

    }


    public  void toggleVisualization(int visualIndex, int songToken){
        density=getResources().getDisplayMetrics().densityDpi;

    try {
        switch (visualIndex) {

            case 0:

                visualID=2;
                start();
                textureView.setVisibility(View.VISIBLE);
                visualizerIndex=1;
                clearVisualizerIndex=0;

                break;

            case 1:

                visualID=3;
                start();
                textureView.setVisibility(View.VISIBLE);
                visualizerIndex=2;
                clearVisualizerIndex=0;

                break;

            case 2:

                visualID=5;
                start();
                textureView.setVisibility(View.VISIBLE);
                visualizerIndex=3;
                clearVisualizerIndex=0;

                break;

            case 3:

                visualID=6;
                start();
                textureView.setVisibility(View.VISIBLE);
                visualizerIndex=4;
                clearVisualizerIndex=0;

                break;
            case 4:

                visualID=4;
                start();
                textureView.setVisibility(View.VISIBLE);
                visualizerIndex=5;
                clearVisualizerIndex=0;

                break;
            case 5:
                lineBarVisualizer = findViewById(R.id.lineBarVisualizerView);
                lineBarVisualizer.setVisibility(View.VISIBLE);
                lineBarVisualizer.setColor(ContextCompat.getColor(this, R.color.knobPointerIndigo));
                lineBarVisualizer.setDensity(120);
                lineBarVisualizer.setPlayer(songToken);
                visualizerIndex=6;
                clearVisualizerIndex =1;
                break;


            case 6:
                circleBarVisualizer = findViewById(R.id.circleBarVisualizerView);
                circleBarVisualizer.setVisibility(View.VISIBLE);
                circleBarVisualizer.setColor(ContextCompat.getColor(this, R.color.colorAccent));
                circleBarVisualizer.setPlayer(songToken);
                visualizerIndex=7;
                clearVisualizerIndex = 2;
                break;

            case 7:
                barVisualizer = findViewById(R.id.barVisualizerView);
                barVisualizer.setVisibility(View.VISIBLE);
                barVisualizer.setColor(ContextCompat.getColor(this, R.color.cyanMix));
                barVisualizer.setDensity(120);
                barVisualizer.setPlayer(songToken);
                visualizerIndex=8;
                clearVisualizerIndex = 3;

                break;
            case 8:
                lineVisualizer = findViewById(R.id.linVisualizerView);
                lineVisualizer.setVisibility(View.VISIBLE);
                lineVisualizer.setColor(ContextCompat.getColor(this, R.color.PurpleMix));
                lineVisualizer.setPlayer(songToken);
                lineVisualizer.setStrokeWidth(1);
                visualizerIndex=9;
                clearVisualizerIndex = 4;

                break;



            case 9:

                visualizerManager = new NierVisualizerManager();
                 visualizerManager.init(0);
                surfaceView=findViewById(R.id.visualizerTestView);
                surfaceView.setVisibility(View.VISIBLE);
                visualizerManager.start(surfaceView, new IRenderer[]{new CircleRenderer(true),new  CircleBarRenderer(),new  LineRenderer(true)});
                visualizerIndex =0;
                clearVisualizerIndex = 5;

                break;

                default:
                    visualizerIndex=0;
        }
    }catch (Throwable e){

        inflateVisualizer();
        startVisualizer();
        Log.e("ExceptionInVisulizer", "toggleVisualization: "+e);
    }


    }

        public  void clearVisualizer(int visualIndex){
        try{
            switch (visualIndex) {

                case 0:
                   // textureView.setVisibility(View.GONE);
                    textureView=null;
                    mRender=null;
                    mVisualizer.release();
                    mVisualizer=null;
                    mSceneList.clear();
                    middleParent.removeAllViews();
                    inflateVisualizer();
                    break;

                case 1:
                    if (lineBarVisualizer != null){
                        lineBarVisualizer.setEnabled(false);
                    lineBarVisualizer.setVisibility(View.GONE);
                    lineBarVisualizer.release();
                    lineBarVisualizer.invalidate();
                    lineBarVisualizer = null;
            }
                    break;


                case 2:
                    if (circleBarVisualizer != null){
                        circleBarVisualizer.setEnabled(false);
                    circleBarVisualizer.setVisibility(View.GONE);
                    circleBarVisualizer.release();
                    circleBarVisualizer.invalidate();
                    circleBarVisualizer = null;
            }
               break;

           case 3:
               if(barVisualizer!=null) {
                   barVisualizer.setEnabled(false);
                   barVisualizer.setVisibility(View.GONE);
                   barVisualizer.release();
                   barVisualizer.invalidate();
                   barVisualizer = null;
               }
               break;
           case 4:
                if(lineVisualizer!=null) {
                    lineVisualizer.setEnabled(false);
                    lineVisualizer.setVisibility(View.GONE);
                    lineVisualizer.release();
                    lineVisualizer.invalidate();
                    lineVisualizer = null;
                }
              break;

                case 5:

                  visualizerManager.release();
                  visualizerManager=null;
                  surfaceView.setVisibility(View.GONE);
                  break;


           default:

       }
       }catch(Throwable e){

            Log.d("ExceptionInVisulizer", "clearVisualizer: "+e);

       }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private class MediaBrowserConnection extends MediaBrowserHelper {
        private MediaBrowserConnection(Context context) {
            super(context, SonaHeartService.class);
        }

        @Override
        protected void onConnected(@NonNull MediaControllerCompat mediaController) {
            super.onConnected(mediaController);
          resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
           mediaController.sendCommand("refreshMedia", null, resultReceiver);

        }

        @Override
        protected void onChildrenLoaded(@NonNull String parentId,
                                        @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);

            try {

                customAlbum=currentState.getBoolean("customAlbum",false);
                mediaController = getMediaController();

                if(customAlbum){

                    zoomin = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_in);
                    zoomout = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_out);
                }else {

                    zoomin = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_in_default);
                    zoomout = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_out_default);
                }
                startAnimListener();
                // Queue up all media items for this simple sample.
               /* for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                    mediaController.addQueueItem(mediaItem.getDescription());
                   // data.add(new Data(String.valueOf(mediaItem.getDescription().getTitle()),String.valueOf(mediaItem.getDescription().getSubtitle())+" - "+ String.valueOf(mediaItem.getDescription().getDescription()), String.valueOf(mediaItem.getDescription().getIconUri()),mediaItem.getMediaId()));
                }*/

                //Call prepare now so pressing play just works.
                mediaController.getTransportControls().prepare();
                mediaController.getTransportControls().play();
                mSeekbarUpdateHandler.postDelayed(mUpdateseekbar, 0);
                TimerSyncView.setVisibility(View.VISIBLE);
                mSleepSyncHandler.postDelayed(mSleepSync, 1000);

                if(SonaHeartService.playlistSize>0)
                {
                    sleepTimer.setEnabled(true);
                    playList.setEnabled(true);
                    EqualizerBtn.setEnabled(true);
                    shuffle.setEnabled(true);
                    repeat.setEnabled(true);
                    PlayPause.setEnabled(true);
                    next.setEnabled(true);
                    prev.setEnabled(true);
                    Log.e("PlayListSizeInActivity", "onChildrenLoaded: "+SonaHeartService.playlistSize );

                }else {

                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "No Media Found", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                }





            }catch (Throwable e){

                mMediaBrowserHelper.onStart();
                Log.e("ExceptionOnController", "onChildrenLoaded: "+e);
            }




        }

    }

    private class AudioTokenReceiver extends ResultReceiver {

         AudioTokenReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {

            case 2:

                stringTrackValue[0]=resultData.getString("ListMeta");
                trackSwitcher.setCurrentText(stringTrackSwitcher[0]);
                trackValueSwitcher.setCurrentText(stringTrackValue[0]);

                if(isQueueActive)
                    stringTrackSwitcher[0]="Queue:";

                break;
            case 3:
                if (resultData.getLong("sleepVal") != 0){
                    TimerSyncView.setText(timeConvert((int) (resultData.getLong("sleepVal"))));
                      if(resultData.getLong("sleepVal")<2000){

                          Calendar calendar = Calendar.getInstance();
                          Date date = new Date();
                          date.setTime(System.currentTimeMillis() + (300 * 60 * 1000));
                          calendar.setTime(date);

                          Intent intent = new Intent(MainActivity.this, HeadsetTriggerService.class);
                          PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intent,  0);
                          AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                          if (alarm != null) {
                              alarm.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), pintent);
                          }

                          mediaController.getTransportControls().stop();
                          MainActivity.this.onDestroy();
                          finishAffinity();
                          finishAndRemoveTask();
                          stopService(new Intent(MainActivity.this, SonaHeartService.class));
                          //android.os.Process.killProcess(android.os.Process.myPid());
                          System.exit(0);
                      }
                }
                else
                {
                    TimerSyncView.setText("Running");
                    TimerSyncView.setVisibility(View.INVISIBLE);
                     mSleepSyncHandler.removeCallbacks(mSleepSync);
                  }
                break;

            case 4:

        try {
            music_styles = resultData.getStringArray("EqName");
            m = resultData.getShort("Eqnum");
            equalizerIndex = resultData.getInt("EqIndex");
            eLavel = resultData.getShort("eLavel");
            initStatusView();
            Log.e("ResultReceiverInvoked", "Number:4 " + eLavel);
        }catch (Throwable e){

            e.printStackTrace();
        }
                    break;

            case 5:
                switcher.setInAnimation(in);
                switcher.setOutAnimation(out);
                tagSwitcher.setInAnimation(fadeIn);
                tagSwitcher.setOutAnimation(fadeOut);

                mUpdateTagsHandler.removeCallbacks(mUpdateTags);
                strings[6]=resultData.getString("nextSong");
                currentIndex=6;
                tagSwitcher.setText(stringTag[currentIndex]);
                switcher.setText(strings[currentIndex]);
                switcher.setInAnimation(up);
                switcher.setOutAnimation(down);
                tagSwitcher.setInAnimation(fadeIn);
                tagSwitcher.setOutAnimation(fadeOut);
                mUpdateTagsHandler.postDelayed(mUpdateTags,3000);

                break;
            case 6:

                Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        resultData.getString("videoCount")+" Videos Scanned", Snackbar.LENGTH_SHORT);
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();
                stringTrackValue[0]=resultData.getString("ListMeta");
                stringTrackValue[1]=resultData.getString("audioCount")+"/"+resultData.getString("trackCount");
                stringTrackValue[2]=resultData.getString("videoCount")+"/"+resultData.getString("trackCount");
                trackSwitcher.setCurrentText(stringTrackSwitcher[2]);
                trackValueSwitcher.setCurrentText(stringTrackValue[2]);

                currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putInt("trackCount", Integer.parseInt(Objects.requireNonNull(resultData.getString("trackCount"))));
                editor.commit();


                break;

            case 7:

                snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        resultData.getString("trackCount")+" Tracks Scanned", Snackbar.LENGTH_SHORT);
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();
                stringTrackValue[0]=resultData.getString("ListMeta");
                stringTrackValue[1]=resultData.getString("audioCount")+"/"+resultData.getString("trackCount");
                stringTrackValue[2]=resultData.getString("videoCount")+"/"+resultData.getString("trackCount");
                trackSwitcher.setCurrentText(stringTrackSwitcher[0]);
                trackValueSwitcher.setCurrentText(stringTrackValue[0]);

                currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                editor = currentState.edit();
                editor.putInt("trackCount", Integer.parseInt(Objects.requireNonNull(resultData.getString("trackCount"))));
                editor.commit();

                break;

            case 8:

                currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                int oldTrack=currentState.getInt("trackCount",0);
                int newTrack=Integer.parseInt(Objects.requireNonNull(resultData.getString("trackCount")));

               if( oldTrack<newTrack){

                 int  diffrence= newTrack-oldTrack;

                 if(diffrence>1) {
                     snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                             diffrence + " Tracks Added", Snackbar.LENGTH_SHORT);
                     SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                     snack.show();
                 }else if(diffrence==1){
                     snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                             diffrence + " Track Added", Snackbar.LENGTH_SHORT);
                     SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                     snack.show();
                 }

               }else if(oldTrack>newTrack){

                   snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                           newTrack+" Tracks Scanned", Snackbar.LENGTH_SHORT);
                   SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                   snack.show();
               }

                 editor = currentState.edit();
                editor.putInt("trackCount", Integer.parseInt(Objects.requireNonNull(resultData.getString("trackCount"))));
                editor.commit();

                break;

            case 9:


                customAlbum = currentState.getBoolean("customAlbum", false);
                albumAutoHide = currentState.getBoolean("albumAutoHide", true);
                Log.d("CustomAlbumValue", "onChoosePath:MainActivity :  "+customAlbum);

                if (customAlbum) {

                    zoomin = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_in);
                    zoomout = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_out);
                    if(mainAlbumArt!=null) {
                        Log.d("CustomAlbumTrue", "onChoosePath:MainActivity Invoked ");
                        mainAlbumArt.clearAnimation();
                        mainAlbumArt.setImageBitmap(null);
                        mainAlbumArt.setVisibility(View.VISIBLE);
                        albumArtChangerHandler.postDelayed(mAlbumArtChanger, 0);
                        startAnimListener();
                    }


                } else {

                    zoomin = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_in_default);
                    zoomout = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_out_default);

                    if(mainAlbumArt!=null)
                    {
                        if (mainAlbumArt.getVisibility() != View.VISIBLE) {
                        mainAlbumArt.clearAnimation();
                            startAnimListener();
                    }


                    }}

            if(resultData.getInt("albumCount",0)==0){
                snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        "Folder Is Empty!", Snackbar.LENGTH_SHORT);

            }else {
                snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        resultData.getInt("albumCount", 0) + " Images Scanned", Snackbar.LENGTH_SHORT);
            }
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();

                break;

            case 10:
                    String title=resultData.getString("Title","");
                    if(!title.equals("")) {
                        if(title.length()<8)
                            title=title+" - "+resultData.getString("FileName","");

                        if(title.length()>20)
                       title = title.substring(0,20);
                        track_input_layout.setHint(title);
                        if(title.equals("Track Not Available!")){
                            statusLayout.startAnimation(shakeSmall);
                        }
                    }

                break;
        }

        super.onReceiveResult(resultCode,resultData);
        }

    }

    /**
     * Implementation of the {@link MediaControllerCompat.Callback} methods we're interested in.
     * <p>
     * Here would also be where one could override
     * {@code onQueueChanged(List<MediaSessionCompat.QueueItem> queue)} to get informed when items
     * are added or removed from the queue. We don't do this here in order to keep the UI
     * simple.
     */
    private class MediaBrowserListener extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
            Log.d("PlaybackStateChanged", "onPlaybackStateChanged:Invoked ");
            mIsPlaying = playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
            if( playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_STOPPED) {


                Log.d("PlaybackStateStopped", "onPlaybackStateChanged: Stop Invoked");

                if(videoViewDisabled ) {
                   // PlayPause.setImageResource(R.drawable.btn_play);
                    isPlay = false;
                    currentIndex = -1;
                    bitIndex = -1;
                    //mUpdateTagsHandler.removeCallbacks(mUpdateTags);
                    mSeekbarUpdateHandler.removeCallbacks(mUpdateseekbar);
                }

                if(mainAlbumArt!=null){
                if(mainAlbumArt.getVisibility()==View.VISIBLE){

                    mainAlbumArt.clearAnimation();
                    zoomin.cancel();
                    zoomout.cancel();
                    albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                    albumArtHiderHandler.removeCallbacks(mHideAlbum);
                }
                }
            }

            if( playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PAUSED && videoViewDisabled) {
                mSeekbarUpdateHandler.removeCallbacks(mUpdateseekbar);
                PlayPause.setImageResource(R.drawable.btn_play);
                if(mainAlbumArt!=null){
                    if(mainAlbumArt.getVisibility()==View.VISIBLE){
                        mainAlbumArt.clearAnimation();

                        zoomin.cancel();
                        zoomout.cancel();
                        albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                        albumArtHiderHandler.removeCallbacks(mHideAlbum);

                        Log.d("AnimationPause", "onPlaybackStateChanged: Animation Pause Invoked" );
                    }
                }
            }

            if(mIsPlaying && videoViewDisabled ){
                Log.d("PlaybackStatePlaying", "onPlaybackStateChanged: Playing Invoked");

                currentIndex=-1;
                bitIndex=-1;
                mSeekbarUpdateHandler.postDelayed(mUpdateseekbar, 0);
                mUpdateTagsHandler.postDelayed(mUpdateTags,0);
               PlayPause.setImageResource(R.drawable.btn_pause);
                isPlay=true;

                if(mainAlbumArt!=null){

                    if(mainAlbumArt.getVisibility()==View.VISIBLE){

                        Log.e("AnimationPlay", "onPlaybackStateChanged: Animation Play Invoked" );
                         mainAlbumArt.startAnimation(zoomin);
                        if(customAlbum)
                        albumArtChangerHandler.postDelayed(mAlbumArtChanger,10000);

                    }
                }

            }else if(!mIsStop) {

                if(!misVideoPlaying) {
                    PlayPause.setImageResource(R.drawable.btn_play);
                    isPlay = false;
                }

            }

            PlayPause.setEnabled(true);


        }


        String currentId;
        public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
            Log.d("MetaActivityTest1", "onMetadataChanged: Invoked");

            if(!startup && mediaMetadata!=null) {
                if (!TextUtils.isEmpty(currentId) && currentId.equals(mediaMetadata.getDescription().getMediaId())) {
                    Log.d("MetaDataNotChanged", "onMetadataChanged: Returning");
                    return;
                }

                currentId = mediaMetadata.getDescription().getMediaId();
                Log.d("MetadataNewID", "onMetadataChanged:SuccessFully Set ");
            }


                Log.d("MetaActivityTest2", "onMetadataChanged: Invoked");
                if(videoView!=null && isActivityRunning) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(MainActivity.this)) {
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessSystem);
                        }
                    } else {
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessSystem);
                    }
                }
                if (mainAlbumArt != null)
                    mainAlbumArt.clearAnimation();
                zoomin.cancel();
                zoomout.cancel();
                albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                albumArtHiderHandler.removeCallbacks(mHideAlbum);
                videoViewDisabled=true;

                if (mediaMetadata == null || TextUtils.isEmpty(mediaMetadata.getString("VIDEO")) ) {
                    return;
                }

            clearVisualizer(clearVisualizerIndex);
                misVideoPlaying = false;
                videoView = null;
                misVideoPaused = false;


                if (mediaMetadata.getString("VIDEO").equals("true")) {
                    if (!startup) {
                        duration = ((int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)) / 1000;
                        albumArt = mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
                        loadingAnimationView.setVisibility(View.GONE);
                        loadingAnimationView.stopAnimation();
                        videoUri = getURI(mediaMetadata.getDescription().getMediaId());
                        inflateVideoView();
                        if (videoFirstSeek && isActivityRunning) {
                            videoView.seekTo((int) (mediaController.getPlaybackState().getPosition()));
                        }
                        misVideoPlaying = true;
                        stringTag[5] = "Title:";
                        FullScreenTitle = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
                        videoFirstSeek = false;

                    }

                } else if (!startup && !mediaMetadata.getString("VIDEO").equals("true")) {

                    duration = ((int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)) / 1000;

                    albumArt = mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
                    loadingAnimationView.setVisibility(View.GONE);
                    loadingAnimationView.stopAnimation();
                    inflateVisualizer();
                    startVisualizer();
                    stringTag[5] = "FileName:";
                    mSeekBarAudio.setEnabled(true);

                    if (mSeekBarAudio.getVisibility() != View.VISIBLE) {

                        inflateAudioSeekbar();

                    } else {
                        mSeekBarAudio.setMax(duration);
                    }

                    videoFirstSeek = false;
                }

                if (mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE).length() < 15) {
                    songName.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE) + " - " + mediaMetadata.getString("FILENAME"));
                } else {
                    songName.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                }

                if(isRepeatAB)
                    removeRepeatAB();

            if(isTrackLayout)
                removeTrackLayout();

                //  StartTime.setText( timeConvert( mSeekBarAudio.getProgress()));
                EndTime.setText(timeConvert(((int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)) / 1000));


                strings[0] = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) != null
                        ? mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
                        : "Unknown Album";
                strings[1] = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST) != null
                        ? mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                        : "Unknown Artist";

                strings[2] = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_COMPOSER) != null
                        ? mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_COMPOSER)
                        : "Unknown";

                strings[3] = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE) != null
                        ? mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE)
                        : "Unknown Genre";

                strings[4] = mediaMetadata.getString("YEAR") != null
                        ? mediaMetadata.getString("YEAR")
                        : "Unknown Year";
                strings[5] = mediaMetadata.getString("FILENAME") != null
                        ? mediaMetadata.getString("FILENAME")
                        : "Unknown Filename";


                bitValueStrings[0] = mediaMetadata.getString("BITRATE") != null
                        ? mediaMetadata.getString("BITRATE")
                        : "N/A";
                bitValueStrings[1] = mediaMetadata.getString("SAMPLERATE") != null
                        ? mediaMetadata.getString("SAMPLERATE")
                        : "N/A";
                bitValueStrings[2] = mediaMetadata.getString("SONGFORMAT") != null
                        ? mediaMetadata.getString("SONGFORMAT")
                        : "N/A";
                bitValueStrings[3] = mediaMetadata.getString("CHANNEL") != null
                        ?  mediaMetadata.getString("CHANNEL")
                        : "N/A";

                bitValueStrings[4] = mediaMetadata.getString("SIZE") != null
                        ? mediaMetadata.getString("SIZE")
                        : "N/A";

                stringTrackValue[1] = mediaMetadata.getString("AUDIOCOUNT") != null
                        ? mediaMetadata.getString("AUDIOCOUNT") + "/" + mediaMetadata.getString("TRACKCOUNT") : "N/A";
                stringTrackValue[2] = mediaMetadata.getString("VIDEOCOUNT") != null
                        ? mediaMetadata.getString("VIDEOCOUNT") + "/" + mediaMetadata.getString("TRACKCOUNT") : "N/A";


                if (mediaMetadata.getString("VIDEO").equals("true")) {

                    vbitValueStrings[2] = bitValueStrings[0];
                    vbitValueStrings[3] = bitValueStrings[1];
                    vbitValueStrings[4] = bitValueStrings[2];
                    vbitValueStrings[5] = bitValueStrings[3];
                    vbitValueStrings[6] = bitValueStrings[4];

                    vbitValueStrings[0] = mediaMetadata.getString("WIDTH") + "x" + mediaMetadata.getString("HEIGHT");
                    vbitValueStrings[1] = mediaMetadata.getString("FRAMERATE");

                }

                currentIndex = -1;
                bitIndex = -1;
                trackIndex = -1;
                statusIndex=-1;

            mUpdateTagsHandler.postDelayed(mUpdateTags,0);
            mUpdateMetaHandler.postDelayed(mUpdateMeta,0);
            mStatusUpdateHandler.postDelayed(mUpdateStatus,0);
            mUpdateTrackHandler.postDelayed(mUpdateTrack,0);


            if (albumArt == null) {
                    albumImage.setImageResource(R.drawable.main_art);
                } else {
                    albumImage.setImageBitmap(albumArt);
                }


                if (!startup) {

                    try {

                        resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                        mediaController.sendCommand("TempoX", null, resultReceiver);
                        mediaController.sendCommand("ListMeta", null, resultReceiver);
                        mediaController.sendCommand("nextSong", null, resultReceiver);
                        mediaController.sendCommand("Equalizer", null, resultReceiver);
                        mUpdateTagsHandler.postDelayed(mUpdateTags, 0);

                        Log.e("OnmetaEqualizer", "onMetadataChanged: ExualizerChanged");

                        Log.d("onMetChangeAudioToken", "onMetadataChanged: AudioTokenSent ");

                        SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = currentState.edit();
                        editor.putString("searchId", mediaController.getMetadata().getDescription().getMediaId());
                        editor.commit();
                    }catch (Throwable e){

                        Log.e("ExceptionRaised", "onMetadataChanged: "+e);
                    }

                }


                startup = false;

        }


        @Override
        public void onSessionDestroyed() {


            super.onSessionDestroyed();
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
        }
    }


    @Override
    public void metaUIUpdateEvent(MediaMetadataCompat mediaMetadata){

        Log.d("LoadMetaXComplete", "metaUIUpdateEvent:MetaUpdateCallbackInActivity ");
        if (mediaMetadata.getString("VIDEO").equals("true")) {
                duration = ((int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)) / 1000;
                albumArt = mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);

                stringTag[5] = "Title:";
                FullScreenTitle = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);

        } else if ( !mediaMetadata.getString("VIDEO").equals("true")) {

            duration = ((int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)) / 1000;

            albumArt = mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
            inflateVisualizer();
            startVisualizer();
            stringTag[5] = "FileName:";
        }

        if (mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE).length() < 15) {
            songName.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE) + " - " + mediaMetadata.getString("FILENAME"));
        } else {
            songName.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        }

        //  StartTime.setText( timeConvert( mSeekBarAudio.getProgress()));
        EndTime.setText(timeConvert(((int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)) / 1000));

        SlideArtIndex= currentArtIndex=(int)mediaMetadata.getLong("CurrentArt");
        Log.d("CurrentArtIndex", "metaUIUpdateEvent: Unpacked"+currentArtIndex);

        strings[0] = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) != null
                ? mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
                : "Unknown Album";
        strings[1] = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST) != null
                ? mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                : "Unknown Artist";

        strings[2] = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_COMPOSER) != null
                ? mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_COMPOSER)
                : "Unknown";

        strings[3] = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE) != null
                ? mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE)
                : "Unknown Genre";

        strings[4] = mediaMetadata.getString("YEAR") != null
                ? mediaMetadata.getString("YEAR")
                : "Unknown Year";
        strings[5] = mediaMetadata.getString("FILENAME") != null
                ? mediaMetadata.getString("FILENAME")
                : "Unknown Filename";


        bitValueStrings[0] = mediaMetadata.getString("BITRATE") != null
                ? mediaMetadata.getString("BITRATE")
                : "N/A";
        bitValueStrings[1] = mediaMetadata.getString("SAMPLERATE") != null
                ? mediaMetadata.getString("SAMPLERATE")
                : "N/A";
        bitValueStrings[2] = mediaMetadata.getString("SONGFORMAT") != null
                ? mediaMetadata.getString("SONGFORMAT")
                : "N/A";
        bitValueStrings[3] = mediaMetadata.getString("CHANNEL") != null
                ?  mediaMetadata.getString("CHANNEL")
                : "N/A";

        bitValueStrings[4] = mediaMetadata.getString("SIZE") != null
                ? mediaMetadata.getString("SIZE")
                : "N/A";

        stringTrackValue[1] = mediaMetadata.getString("AUDIOCOUNT") != null
                ? mediaMetadata.getString("AUDIOCOUNT") + "/" + mediaMetadata.getString("TRACKCOUNT") : "N/A";
        stringTrackValue[2] = mediaMetadata.getString("VIDEOCOUNT") != null
                ? mediaMetadata.getString("VIDEOCOUNT") + "/" + mediaMetadata.getString("TRACKCOUNT") : "N/A";


        if (mediaMetadata.getString("VIDEO").equals("true")) {

            vbitValueStrings[2] = bitValueStrings[0];
            vbitValueStrings[3] = bitValueStrings[1];
            vbitValueStrings[4] = bitValueStrings[2];
            vbitValueStrings[5] = bitValueStrings[3];
            vbitValueStrings[6] = bitValueStrings[4];

            vbitValueStrings[0] = mediaMetadata.getString("WIDTH") + "x" + mediaMetadata.getString("HEIGHT");
            vbitValueStrings[1] = mediaMetadata.getString("FRAMERATE");

        }

        currentIndex = -1;
        bitIndex = -1;
        trackIndex = -1;
        statusIndex=-1;

        mUpdateTagsHandler.postDelayed(mUpdateTags,0);
        mUpdateMetaHandler.postDelayed(mUpdateMeta,0);
        mStatusUpdateHandler.postDelayed(mUpdateStatus,0);
        mUpdateTrackHandler.postDelayed(mUpdateTrack,0);


        if (albumArt == null) {
            albumImage.setImageResource(R.drawable.main_art);
        } else {
            albumImage.setImageBitmap(albumArt);
        }
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


    Runnable mUpdateseekbar = new Runnable() {
        @Override
        public void run() {

            mSeekbarUpdateHandler.removeCallbacksAndMessages(null);
    try {
                if (videoView != null && videoView.isPlaying()) {
                    if(isRepeatActive && videoView.getCurrentPosition()>=repeatDurationB) {


                            videoView.seekTo(repeatDurationA);
                    }
                    videoSeekbar.setProgress(videoView.getCurrentPosition());
                    if(StartTime.getVisibility()==View.VISIBLE)
                    StartTime.setText(timeConvert(videoView.getCurrentPosition()));

                } else {

                    if(mediaController!=null && mediaController.getPlaybackState().getState()==PlaybackStateCompat.STATE_PLAYING) {
                        if(isRepeatActive && (int) (mediaController.getPlaybackState().getPosition())>=repeatDurationB) {


                                mediaController.getTransportControls().seekTo(repeatDurationA);

                        }
                        if(StartTime.getVisibility()==View.VISIBLE)
                        StartTime.setText(timeConvert((int) mediaController.getPlaybackState().getPosition()));

                       mSeekBarAudio.setProgress(((int) mediaController.getPlaybackState().getPosition()));




                    }
                }


                mSeekbarUpdateHandler.postDelayed(this, 1000);
    }catch (Throwable e){

        Log.e("ExceptionRaised", "run:UpdateSeekbar " +e);
    }
        }};

    Runnable mSleepSync = new Runnable() {
        @Override
        public void run() {

            mSleepSyncHandler.removeCallbacksAndMessages(null);
            resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
            mediaController.sendCommand("SleepSync", null, resultReceiver);

            mSleepSyncHandler.postDelayed(this, 1000);
        }};

    Runnable mUpdateTags = new Runnable() {
        @Override
        public void run() {

            mUpdateTagsHandler.removeCallbacksAndMessages(null);

                ++currentIndex;

                if (currentIndex == messageCount)
                    currentIndex = 0;


           tagSwitcher.setText(stringTag[currentIndex]);
           switcher.setText(strings[currentIndex]);

            mUpdateTagsHandler.postDelayed(this, 4000);
            mUpdateMetaHandler.postDelayed(mUpdateMeta,3000);
            mStatusUpdateHandler.postDelayed(mUpdateStatus,2000);
            mUpdateTrackHandler.postDelayed(mUpdateTrack,1000);

        }};


    Runnable mUpdateStatus = new Runnable() {
        @Override
        public void run() {

            mStatusUpdateHandler.removeCallbacksAndMessages(null);

            ++statusIndex;

            if (statusIndex == statusCount)
                statusIndex = 0;

            statusTextSwitcher.setText(stringStatusSwitcher[statusIndex]);
            statusValueSwitcher.setText(stringValueSwitcher[statusIndex]);



        }};

    Runnable mUpdateTrack = new Runnable() {
        @Override
        public void run() {

            mUpdateTrackHandler.removeCallbacksAndMessages(null);

            ++trackIndex;

            if (trackIndex == trackCount)
                trackIndex = 0;

            trackSwitcher.setText(stringTrackSwitcher[trackIndex]);
            trackValueSwitcher.setText(stringTrackValue[trackIndex]);



        }};

    final Runnable volSeekHider = new Runnable() {
        @Override
        public void run() {


            volSeekHiderHandler.removeCallbacksAndMessages(null);

            volumeSeekbar.setAlpha(0.1f);
            brightnessSeekbar.setAlpha(0.1f);



        }

    };

    Runnable mHideAlbum = new Runnable() {
        @Override
        public void run() {

            albumArtHiderHandler.removeCallbacksAndMessages(null);
            Log.d("AlbumHiderRunning", "run:Invoked ");
            if(mainAlbumArt!=null && mainAlbumArt.getVisibility()==View.VISIBLE) {

                zoomin.cancel();
                zoomout.cancel();
                mainAlbumArt.clearAnimation();
                mainAlbumArt.setVisibility(View.GONE);
            }else {
                albumArtHiderHandler.removeCallbacks(this);
            }

        }};

    Runnable mAlbumArtChanger = new Runnable() {
        @Override
        public void run() {

            albumArtChangerHandler.removeCallbacksAndMessages(null);

            if(customAlbum && albumArtList.size()!=0 && mainAlbumArt!=null && mainAlbumArt.getVisibility()==View.VISIBLE && isActivityRunning) {

                changeAlbumArt();
                zoomout.cancel();
                mainAlbumArt.startAnimation(zoomin);

                albumArtChangerHandler.postDelayed(mAlbumArtChanger,10000);

        }
        }


    };

    Bitmap preloadBitmap=null;


    private void changeAlbumArt(){

        try {

           if(preloadBitmap==null){
               preloadBitmap=loadScaledBitmap(false,randomArt());
               Log.e("LoadFromMainFunction", "changeAlbumArt: Invoked");
           }

               albumImage.setImageBitmap(preloadBitmap);
               mainAlbumArt.setImageBitmap(preloadBitmap);
               SlideArtIndex=currentArtIndex;

            GlideApp
                    .with(mainAlbumArt.getContext())
                    .asBitmap()
                    .load(new File(randomArt()))
                    .dontAnimate()
                    .error(R.drawable.main_art)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .apply(new RequestOptions())
                    .into(new CustomTarget<Bitmap>(1000,1000) {
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

    private Bitmap loadScaledBitmap(Boolean slidestart,String artPath){

        Uri imageUri;



        if(slidestart) {
            imageUri = Uri.fromFile(new File(artPath));

        }else {

            imageUri = Uri.fromFile(new File(randomArt()));

        }

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
            if(is!=null)
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
            if(is!=null)
            is.close();
        } catch (IOException e) {
            // ignore
        }

        return bitmap;


    }



        private  String randomArt() {

            int max = albumArtList.size();
            Random rand = new Random();
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
                    String newAlbum = albumArtList.get(newAlbumIndex);
                    currentArtIndex=newAlbumIndex;
                    Log.e("CurrentArtIndex", "randomArt: "+newAlbumIndex+":"+currentArtIndex);
                    return newAlbum;
                }else
                    currentArtIndex=-1;
                return "";
            }catch (Throwable e){

                Log.e("ExceptionRaised", "randomArt: "+e );
                currentArtIndex=-1;
                return "";
            }

        }

    Runnable mUpdateMeta = new Runnable() {
        @Override
        public void run() {
            mUpdateMetaHandler.removeCallbacksAndMessages(null);
try {
    if (misVideoPlaying) {
        ++bitIndex;
        if (bitIndex == vbitCount)
            bitIndex = 0;
        bitrateSwitcher.setText(vbitStrings[bitIndex]);
        bitValueSwitcher.setText(vbitValueStrings[bitIndex]);
    } else {
        ++bitIndex;
        if (bitIndex == bitCount)
            bitIndex = 0;
        bitrateSwitcher.setText(bitStrings[bitIndex]);
        bitValueSwitcher.setText(bitValueStrings[bitIndex]);
    }


}catch (Throwable e){


    bitIndex=-1;
    Log.e("ExceptionRaised", "run: ExceptionInUpdateMetaRunnable"+e);
}

        }};

    Runnable mViewHider = new Runnable() {
        @Override
        public void run() {
            viewHiderHandler.removeCallbacksAndMessages(null);
            if(equalizerFlag){
                slideDown(equalizerPagerLayout);
                viewPager.setAdapter(null);
                reverbPager.setAdapter(null);
                EqViewPager.setAdapter(null);
                equalizerFlag=false;
                if(misVideoPlaying){

                    inflateVideoView();
                    videoView.seekTo((int)mediaController.getPlaybackState().getPosition());
                }else {
                    inflateVisualizer();
                    startVisualizer();}
                autoVol=false;

                nextPage.clearAnimation();
                nextPage.setVisibility(View.GONE);
                prevPage.clearAnimation();
                prevPage.setVisibility(View.GONE);

            }

        }};

        public  void removeHandlers(){

            mSleepSyncHandler.removeCallbacks(mSleepSync);
            mSeekbarUpdateHandler.removeCallbacks(mUpdateseekbar);
            mUpdateTagsHandler.removeCallbacks(mUpdateTags);
            mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
            mUpdateTrackHandler.removeCallbacks(mUpdateTrack);
            mUpdateMetaHandler.removeCallbacks(mUpdateMeta);
            albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);



        }

    public void sendMessage() {

    Intent intent = new Intent(this, MediaListActivity.class);
    intent.putExtra("FOCUS", false);
    intent.putExtra("scrollId", mediaController.getMetadata().getDescription().getMediaId());
    startActivityForResult(intent, 1);
    Animations.Animations(this,enterAnim);

    }


    public void slideUp(View view){
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_up);
        ViewGroup hiddenPanel = (ViewGroup)view;
        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.VISIBLE);
       /* view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);
        animate.setDuration(400);
        animate.setFillAfter(true);
        view.startAnimation(animate);*/

    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        try {
            TranslateAnimation animate = new TranslateAnimation(
                    0,                 // fromXDelta
                    0,                 // toXDelta
                    0,                 // fromYDelta
                    view.getHeight()); // toYDelta
            animate.setDuration(500);
            animate.setFillAfter(true);
            view.startAnimation(animate);
            view.setVisibility(View.GONE);
        }catch (Throwable e){

            Log.e("slideDownException", "slideDown:  "+e);
        }
    }



    private void initViewPager(View v) {
        Log.d("Creating ViewPager", "initViewPager: ");
        viewPager = v.findViewById(R.id.equalizer_viewpager);
        viewPager.setPageTransformer(false, new ZoomOutTransformer());
       // viewPager.setPageTransformer(true, new StackTransformer());


        try {
            viewPager.setAdapter(new ContentFragmentAdapter.Holder(getSupportFragmentManager())
                    .add(ContentFragment.newInstance(music_styles[0], 0))
                    .add(ContentFragment.newInstance(music_styles[1], 1))
                    .add(ContentFragment.newInstance(music_styles[2], 2))
                    .add(ContentFragment.newInstance(music_styles[3], 3))
                    .add(ContentFragment.newInstance(music_styles[4], 4))
                    .add(ContentFragment.newInstance(music_styles[5], 5))
                    .add(ContentFragment.newInstance(music_styles[6], 6))
                    .add(ContentFragment.newInstance(music_styles[7], 7))
                    .add(ContentFragment.newInstance(music_styles[8], 8))
                    .add(ContentFragment.newInstance(music_styles[9], 9))
                    .set());
        }catch (Throwable e){

            Snackbar  snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                    "Player Not Initialized", Snackbar.LENGTH_SHORT);
            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
            snack.show();
            slideDown(equalizerPagerLayout);
            equalizerFlag=false;
            nextPage.clearAnimation();
            nextPage.setVisibility(View.GONE);
            prevPage.clearAnimation();
            prevPage.setVisibility(View.GONE);
            inflateAnimation();
        }

            // Offset between sibling pages in dp
            int pageOffset = 25;

            // Visible part of sibling pages at the edges in dp
                    int sidePageVisibleWidth = 20;

            // Horizontal padding will be
                    int horPadding = pageOffset + sidePageVisibleWidth;

            // Apply parameters
            viewPager.setClipToPadding(false);
            viewPager.setPageMargin(dpToPx(pageOffset, getContext()));
            viewPager.setPadding(0,dpToPx(horPadding, getContext()) ,0 , dpToPx(horPadding, getContext()));
            //If you setting other scroll mode, the scrolled fade is shown from either side of display.
             viewPager.setOffscreenPageLimit(10);
            viewPager.setPageMargin(-40);
            viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
             viewPager.setCurrentItem(equalizerIndex);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {

                Log.d("onPageSelected", "OnselectedInvoked: ");
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                equalizerIndex=index;
                Bundle bundleq = new Bundle();
                bundleq.putInt("EqIndex",index );
                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                mediaController.sendCommand("Equalizerx", bundleq, resultReceiver);

                stringValueSwitcher[3] = music_styles[index];
                statusTextSwitcher.setText(stringStatusSwitcher[3]);
                statusValueSwitcher.setText( stringValueSwitcher[3]);

                Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                      getSnackWord(music_styles[index]), Snackbar.LENGTH_SHORT);
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();

                if(autoVol){
                    viewHiderHandler.removeCallbacks(mViewHider);
                    viewHiderHandler.postDelayed(mViewHider, 2000);

                    Log.d("viewPager", "autoVolumeInvoked: "+autoVol);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
               // Toast.makeText(getApplicationContext(),"onPageScrollStateChangedInvoked",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                //Toast.makeText(getApplicationContext(),"onPageScrolledInvoked",Toast.LENGTH_LONG).show();

            }

        });
    }

    private void initReverbPager(View v) {
        reverbPager = v.findViewById(R.id.reverb_pager);
        reverbPager.setPageTransformer(false, new ZoomOutTransformer());
        // viewPager.setPageTransformer(true, new StackTransformer());
        reverbPager.setAdapter(new ContentFragmentAdapter.Holder(getSupportFragmentManager())
                .add(ContentFragment.newInstance(reverb_styles[0], 0))
                .add(ContentFragment.newInstance(reverb_styles[1], 1))
                .add(ContentFragment.newInstance(reverb_styles[2], 2))
                .add(ContentFragment.newInstance(reverb_styles[3], 3))
                .add(ContentFragment.newInstance(reverb_styles[4], 4))
                .add(ContentFragment.newInstance(reverb_styles[5], 5))
                .add(ContentFragment.newInstance(reverb_styles[6], 6))
                .set());

        // Offset between sibling pages in dp
        int pageOffset = 25;

        // Visible part of sibling pages at the edges in dp
        int sidePageVisibleWidth = 20;

        // Horizontal padding will be
        int horPadding = pageOffset + sidePageVisibleWidth;

        // Apply parameters
        reverbPager.setClipToPadding(false);
        reverbPager.setPageMargin(dpToPx(pageOffset, getContext()));
        reverbPager.setPadding(0,dpToPx(horPadding, getContext()) ,0 , dpToPx(horPadding, getContext()));
        //If you setting other scroll mode, the scrolled fade is shown from either side of display.
        reverbPager.setOffscreenPageLimit(10);
        reverbPager.setPageMargin(-40);
        reverbPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        reverbPager.setCurrentItem(reverbIndex);


        reverbPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                reverbIndex=index;

                Log.d("onPageSelected", "OnselectedInvoked: index- "+index);

                Bundle bundleRV = new Bundle();
                bundleRV.putShort("reverb",(short) index );
                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                mediaController.sendCommand("Reverb", bundleRV, resultReceiver);

                stringValueSwitcher[8] = reverb_styles[index];
                statusTextSwitcher.setText(stringStatusSwitcher[8]);
                statusValueSwitcher.setText( stringValueSwitcher[8]);

                Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        getSnackWord(reverb_styles[index]), Snackbar.LENGTH_SHORT);
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();


            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                // Toast.makeText(getApplicationContext(),"onPageScrollStateChangedInvoked",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                //Toast.makeText(getApplicationContext(),"onPageScrolledInvoked",Toast.LENGTH_LONG).show();

            }

        });
    }


    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }


    private String getSnackWord(String text) {
       if(text.length()>=7){

           return "* "+text+" Set *";
       }else {
           return "*    "+text+" Set    *";
       }
    }



    public void inflateAnimation(){


       View v = getLayoutInflater().inflate(R.layout.animation_layout, middleParent,false);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT);
        middleParent.addView(v,lp);
        loadingAnimationView = findViewById(R.id.animation);
        loadingAnimationView.setVisibility(View.VISIBLE);
        loadingAnimationView.startAnimation();

    }

    public void inflateSleep(){


        View v = getLayoutInflater().inflate(R.layout.sleep_layout, middleParent,false);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT);
        middleParent.removeAllViews();
        middleParent.addView(v,lp);

        initSleepListener();


    }


    public void inflateEquailizerPager(int position){

        View v = getLayoutInflater().inflate(R.layout.equalizer_view_pager, middleParent,false);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT);

        middleParent.removeAllViews();
        middleParent.addView(v,lp);

      EqViewPager =  findViewById(R.id.equailizer_pager);
        EqViewPager.setAdapter(new CustomPagerAdapter());
        EqViewPager.setOffscreenPageLimit(2);
        EqViewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        EqViewPager.setCurrentItem(position);
        equalizerPagerLayout=findViewById(R.id.equalizer_view_pager_parent);

        nextPage.setVisibility(View.VISIBLE);
        prevPage.setVisibility(View.VISIBLE);
        nextPage.startAnimation(animationLeft);
        prevPage.startAnimation(animationRight);


        EqViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {

                viewHiderHandler.removeCallbacks(mViewHider);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                // Toast.makeText(getApplicationContext(),"onPageScrollStateChangedInvoked",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                //Toast.makeText(getApplicationContext(),"onPageScrolledInvoked",Toast.LENGTH_LONG).show();

            }

        });


    }


    private void initBassListener(View v){

        bassLayout=v.findViewById(R.id.bass_wrapper);
        trableKnob=v.findViewById(R.id.trableKnob);
        Croller bassKnob = v.findViewById(R.id.bassKnob);
        Croller virtualKnob = v.findViewById(R.id.virtualKnob);

        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        bassKnob.setProgress(((currentState.getInt("BassValue",500))/100));
        Log.d("BassKnobValue", "val: "+((currentState.getInt("BassValue",500))/100));
        virtualKnob.setProgress((currentState.getInt("VirtualValue",500))/100);

        if(currentState.getInt("TrableValue",0)>0)
            trableKnob.setProgress((currentState.getInt("TrableValue",0))/100);
        else
            trableKnob.setProgress((currentState.getInt("TrableValue",0)));





        if (density ==DisplayMetrics.DENSITY_XHIGH) {

            bassKnob.setProgressRadius(95);
            virtualKnob.setProgressRadius(95);
            trableKnob.setProgressRadius(95);

        } else if(density ==DisplayMetrics.DENSITY_XXHIGH) {

            bassKnob.setProgressRadius(141);
            bassKnob.setProgressPrimaryStrokeWidth(10);
            virtualKnob.setProgressRadius(141);
            virtualKnob.setProgressPrimaryStrokeWidth(10);
            trableKnob.setProgressRadius(141);
            trableKnob.setProgressPrimaryStrokeWidth(10);

        }else if(density ==DisplayMetrics.DENSITY_420){
            bassKnob.setProgressRadius(123);
            bassKnob.setProgressPrimaryStrokeWidth(10);
            virtualKnob.setProgressRadius(123);
            virtualKnob.setProgressPrimaryStrokeWidth(10);
            trableKnob.setProgressRadius(123);
            trableKnob.setProgressPrimaryStrokeWidth(10);
        }

        else if(density ==DisplayMetrics.DENSITY_560){
            bassKnob.setProgressRadius(164);
            bassKnob.setProgressPrimaryStrokeWidth(12);
            virtualKnob.setProgressRadius(164);
            virtualKnob.setProgressPrimaryStrokeWidth(12);
            trableKnob.setProgressRadius(164);
            trableKnob.setProgressPrimaryStrokeWidth(12);
        } else if(density ==DisplayMetrics.DENSITY_XXXHIGH) {
            bassKnob.setProgressRadius(195);
            bassKnob.setProgressPrimaryStrokeWidth(13);
            virtualKnob.setProgressRadius(195);
            virtualKnob.setProgressPrimaryStrokeWidth(13);
            trableKnob.setProgressRadius(195);
            trableKnob.setProgressPrimaryStrokeWidth(13);

        }

        trableKnob.setMax(eLavel/100);

        bassKnob.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // use the progress
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                Bundle bundle = new Bundle();
                bundle.putShort("BassValue",(short) (((float) 1000 / 10) * (progress)));
                mediaController.sendCommand("BassBoost", bundle, resultReceiver);
                stringValueSwitcher[1] = ( progress* 100) / 10+"%";
                statusTextSwitcher.setCurrentText(stringStatusSwitcher[1]);
                statusValueSwitcher.setCurrentText( stringValueSwitcher[1]);



            }

            @Override
            public void onStartTrackingTouch(Croller croller) {
                // tracking started
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                statusTextSwitcher.setText(stringStatusSwitcher[1]);
                statusValueSwitcher.setText( stringValueSwitcher[1]);

            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                // tracking stopped


            }
        });

        virtualKnob.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // use the progress
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                Bundle bundle = new Bundle();
                bundle.putShort("VirtualValue",(short) (((float) 1000 / 10) * (progress)));
                mediaController.sendCommand("Virtualizer", bundle, resultReceiver);

                stringValueSwitcher[2] = (( progress* 100) / 10)+"%";
                statusTextSwitcher.setCurrentText(stringStatusSwitcher[2]);
                statusValueSwitcher.setCurrentText( stringValueSwitcher[2]);

            }

            @Override
            public void onStartTrackingTouch(Croller croller) {
                // tracking started
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                statusTextSwitcher.setText(stringStatusSwitcher[2]);
                statusValueSwitcher.setText( stringValueSwitcher[2]);

            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                // tracking stopped

            }
        });


        trableKnob.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // use the progress
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                Bundle bundle = new Bundle();
                bundle.putShort("TrableValue",(short)(100*progress));
                mediaController.sendCommand("TrableBoost", bundle, resultReceiver);
                stringValueSwitcher[5] = ( progress* 100) / trableKnob.getMax()+"%";
                statusTextSwitcher.setCurrentText(stringStatusSwitcher[5]);
                statusValueSwitcher.setCurrentText( stringValueSwitcher[5]);


            }

            @Override
            public void onStartTrackingTouch(Croller croller) {
                // tracking started
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                statusTextSwitcher.setText(stringStatusSwitcher[5]);
                statusValueSwitcher.setText( stringValueSwitcher[5]);
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                // tracking stopped
            }
        });




    }

    public void initEqListener(View v){


        density=getResources().getDisplayMetrics().densityDpi;
        equalizerLayout=v.findViewById(R.id.volume_view);
        volKnob=v.findViewById(R.id.volume_knob);
        Croller loudKnob = v.findViewById(R.id.loud_knob);

        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        volKnob.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        loudKnob.setProgress((currentState.getInt("LoudValue",50))/10);


        if (density ==DisplayMetrics.DENSITY_XHIGH) {

            volKnob.setProgressRadius(95);
            loudKnob.setProgressRadius(95);

           // Toast.makeText(getApplicationContext(),"MyScreen Density is xhdpi: "+density,Toast.LENGTH_LONG).show();

        } else if(density ==DisplayMetrics.DENSITY_XXHIGH) {

            volKnob.setProgressRadius(141);
            volKnob.setProgressPrimaryStrokeWidth(10);
            loudKnob.setProgressRadius(141);
            loudKnob.setProgressPrimaryStrokeWidth(10);

           // Toast.makeText(getApplicationContext(),"MyScreen Density is xxhdpi: "+density,Toast.LENGTH_LONG).show();
        }else if(density ==DisplayMetrics.DENSITY_420){
            volKnob.setProgressRadius(123);
            volKnob.setProgressPrimaryStrokeWidth(10);
            loudKnob.setProgressRadius(123);
            loudKnob.setProgressPrimaryStrokeWidth(10);

           // Toast.makeText(getApplicationContext(),"MyScreen Density is : "+density,Toast.LENGTH_LONG).show();
        }

        else if(density ==DisplayMetrics.DENSITY_560){
            volKnob.setProgressRadius(164);
            volKnob.setProgressPrimaryStrokeWidth(12);
            loudKnob.setProgressRadius(164);
            loudKnob.setProgressPrimaryStrokeWidth(12);

          //  Toast.makeText(getApplicationContext(),"MyScreen Density is: "+density,Toast.LENGTH_LONG).show();
        } else if(density ==DisplayMetrics.DENSITY_XXXHIGH) {
            volKnob.setProgressRadius(195);
            volKnob.setProgressPrimaryStrokeWidth(13);
            loudKnob.setProgressRadius(195);
            loudKnob.setProgressPrimaryStrokeWidth(13);

        } else {


            Toast.makeText(getApplicationContext(),"Thrown Screen Density is: "+density,Toast.LENGTH_LONG).show();

        }


        volKnob.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volKnob.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volKnob.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // use the progress
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                stringValueSwitcher[0] = ((progress*100)/mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))+"%";
                statusTextSwitcher.setCurrentText(stringStatusSwitcher[0]);
                statusValueSwitcher.setCurrentText( stringValueSwitcher[0]);
                currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putInt("PlayVol", progress);
                editor.commit();

                Log.d("volKnob", "onProgressChanged:PlayVol: "+progress);



            }

            @Override
            public void onStartTrackingTouch(Croller croller) {
                // tracking started
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                if(autoVol){
                    viewHiderHandler.removeCallbacks(mViewHider);
                    viewHiderHandler.postDelayed(mViewHider, 2000);

                    Log.d("volumeTouchStart", "autoVolumeInvoked: "+autoVol);
                }
                statusTextSwitcher.setText(stringStatusSwitcher[0]);
                statusValueSwitcher.setText( stringValueSwitcher[0]);
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                // tracking stopped
                volLevel = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putInt("volLevel", volLevel);
                editor.commit();
                if(autoVol){
                    viewHiderHandler.removeCallbacks(mViewHider);
                    viewHiderHandler.postDelayed(mViewHider, 2000);
                    Log.d("volumeTouchEnd", "autoVolumeInvoked: "+autoVol);
                }
            }
        });


        loudKnob.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // use the progress
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                Bundle bundle = new Bundle();
                bundle.putInt("LoudValue",progress*10 );
                mediaController.sendCommand("Loudness", bundle, resultReceiver);
                stringValueSwitcher[4] = (( progress* 100) / 100)+"%";
                statusTextSwitcher.setCurrentText(stringStatusSwitcher[4]);
                statusValueSwitcher.setCurrentText( stringValueSwitcher[4]);

            }

            @Override
            public void onStartTrackingTouch(Croller croller) {
                // tracking started
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                if(autoVol){
                    viewHiderHandler.removeCallbacks(mViewHider);
                    viewHiderHandler.postDelayed(mViewHider, 2000);
                }
                statusTextSwitcher.setText(stringStatusSwitcher[4]);
                statusValueSwitcher.setText( stringValueSwitcher[4]);
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                // tracking stopped
                if(autoVol){
                    viewHiderHandler.removeCallbacks(mViewHider);
                    viewHiderHandler.postDelayed(mViewHider, 2000);
                }
            }
        });


    }




    private void initSleepListener(){

        sleepLayout=findViewById(R.id.sleep_view);
        sleepLabel = findViewById(R.id.sleep_label);
        Button sleepOk = findViewById(R.id.sleep_ok);
        Button sleepCancel = findViewById(R.id.sleep_cancel);
        sleepstop = findViewById(R.id.sleep_stop);
        sleepOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                slideDown(sleepLayout);

                if (sleepLong > 0) {
                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                    Bundle bundle = new Bundle();
                    bundle.putLong("time", sleepLong);
                    mediaController.sendCommand("SleepTimer", bundle, resultReceiver);
                    mSleepSyncHandler.postDelayed(mSleepSync, 1000);
                    TimerSyncView.setVisibility(View.VISIBLE);

                    currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putLong("sleepTime", sleepLong);
                    editor.putInt("sleepSeek", seekBar.getProgress());
                    editor.commit();

                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "Sleep Timer Running", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                }
                sleepLayout.setVisibility(View.GONE);
                sleepflag = false;
                if(misVideoPlaying){

                    inflateVideoView();
                    videoView.seekTo((int)mediaController.getPlaybackState().getPosition());
                }else {
                    inflateVisualizer();
                    startVisualizer();}



            }
        });

        sleepCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown(sleepLayout);
                sleepLayout.setVisibility(View.GONE);
                sleepflag = false;
                if(misVideoPlaying){

                    inflateVideoView();
                    videoView.seekTo((int)mediaController.getPlaybackState().getPosition());
                }else {
                    inflateVisualizer();
                    startVisualizer();}

            }


        });

        sleepstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerSyncView.setText("Running");
                TimerSyncView.setVisibility(View.INVISIBLE);
                slideDown(sleepLayout);
                mSleepSyncHandler.removeCallbacks(mSleepSync);
                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                mediaController.sendCommand("SleepStop", null, resultReceiver);
                sleepLayout.setVisibility(View.GONE);
                sleepflag = false;
                if(misVideoPlaying){

                    inflateVideoView();
                    videoView.seekTo((int)mediaController.getPlaybackState().getPosition());
                }else {
                    inflateVisualizer();
                    startVisualizer();}


                Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        "Sleep Timer Stopped", Snackbar.LENGTH_SHORT);
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();

            }
        });

        seekBar = findViewById(R.id.sleep_seekbar);
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    switch (seekBar.getProgress()) {

                        case 0:
                            sleepLabel.setText("Sleep in: " + (seekBar.getProgress() + 1) + " Minutes");
                            sleepLong = 60000;
                            break;

                        case 1:
                            sleepLabel.setText("Sleep in: " + (seekBar.getProgress() * 5) + " Minutes");
                            sleepLong = 300000;
                            break;

                        case 2:
                            sleepLabel.setText("Sleep in: " + (seekBar.getProgress() * 5) + " Minutes");
                            sleepLong = 600000;
                            break;
                        case 3:
                            sleepLabel.setText("Sleep in: " + (seekBar.getProgress() * 5) + " Minutes");
                            sleepLong = 900000;
                            break;
                        case 4:
                            sleepLabel.setText("Sleep in: " + (seekBar.getProgress() * 5) + " Minutes");
                            sleepLong = 1200000;
                            break;
                        case 5:
                            sleepLabel.setText("Sleep in: " + (seekBar.getProgress() * 6) + " Minutes");
                            sleepLong = 1800000;
                            break;
                        case 6:
                            sleepLabel.setText("Sleep in: 45 Minutes");
                            sleepLong = 2700000;
                            break;
                        case 7:
                            sleepLabel.setText("Sleep in: 60 Minutes");
                            sleepLong = 3600000;
                            break;
                        case 8:
                            sleepLabel.setText("Sleep in: 120 Minutes");
                            sleepLong = 7200000;
                            break;

                        default:
                    }
                    // Write code to perform some action when progress is changed.
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {


                    Vibration.Companion.vibrate(20);
                    // Write code to perform some action when touch is started.
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Write code to perform some action when touch is stopped.
                    //Toast.makeText(MainActivity.this, "Current value is " + seekBar.getProgress(), Snackbar.LENGTH_SHORT).show();

                }
            });

        }
        }

    private void initChListener(View v){
        density=getResources().getDisplayMetrics().densityDpi;
        channelLayout=v.findViewById(R.id.channel_view);
        Croller balanceKnob = v.findViewById(R.id.channel_knob);
        tempoKnob=v.findViewById(R.id.tempo_knob);
        balanceKnob.setProgress(channelprogress);
        tempoKnob.setProgress(tempoProgress);

        if (density ==DisplayMetrics.DENSITY_XHIGH) {
            balanceKnob.setProgressRadius(95);
            tempoKnob.setProgressRadius(95);

            // Toast.makeText(getApplicationContext(),"MyScreen Density is xhdpi: "+density,Toast.LENGTH_LONG).show();

        } else if(density ==DisplayMetrics.DENSITY_XXHIGH) {

            balanceKnob.setProgressRadius(141);
            balanceKnob.setProgressPrimaryStrokeWidth(10);
            tempoKnob.setProgressRadius(141);
            tempoKnob.setProgressPrimaryStrokeWidth(10);

            // Toast.makeText(getApplicationContext(),"MyScreen Density is xxhdpi: "+density,Toast.LENGTH_LONG).show();
        }else if(density ==DisplayMetrics.DENSITY_420){
            balanceKnob.setProgressRadius(123);
            balanceKnob.setProgressPrimaryStrokeWidth(10);
            tempoKnob.setProgressRadius(123);
            tempoKnob.setProgressPrimaryStrokeWidth(10);

            // Toast.makeText(getApplicationContext(),"MyScreen Density is : "+density,Toast.LENGTH_LONG).show();
        }

        else if(density ==DisplayMetrics.DENSITY_560){
            balanceKnob.setProgressRadius(164);
            balanceKnob.setProgressPrimaryStrokeWidth(12);
            tempoKnob.setProgressRadius(164);
            tempoKnob.setProgressPrimaryStrokeWidth(12);

            //  Toast.makeText(getApplicationContext(),"MyScreen Density is: "+density,Toast.LENGTH_LONG).show();
        }else if(density ==DisplayMetrics.DENSITY_XXXHIGH) {
            balanceKnob.setProgressRadius(195);
            balanceKnob.setProgressPrimaryStrokeWidth(13);
            tempoKnob.setProgressRadius(195);
            tempoKnob.setProgressPrimaryStrokeWidth(13);

        }
        else {

            Toast.makeText(getApplicationContext(),"Thrown Screen Density is: "+density,Toast.LENGTH_LONG).show();

        }
        balanceKnob.setMax(20);
        balanceKnob.setMin(0);
        //balanceKnob.setProgress(channelprogress);

        balanceKnob.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                channelprogress=progress;

                if(progress<10){
                    left=((float)progress)/10f;
                    right=1.0f;
                    leftRead=-(10-progress);
                }else if(progress>10){
                    right=((float)(progress -((progress-10)*2)))/10f;
                    left=1.0f;
                    rightRead=-(10-(progress -((progress-10)*2)));
                }else{
                    left=1.0f;
                    right=1.0f;
                    leftRead=0;
                    rightRead=0;
                    Vibration.Companion.vibrate(20);
                }

                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                Bundle bundleCB = new Bundle();
                bundleCB.putFloat("left",left );
                bundleCB.putFloat("right",right );
                if(mIsPlaying)
                    mediaController.sendCommand("channelBalance", bundleCB, resultReceiver);

                Log.e("ProgressVol", "onProgressChanged: "+progress);

                stringValueSwitcher[6] = "L"+leftRead+"  "+"R"+rightRead;
                statusTextSwitcher.setCurrentText(stringStatusSwitcher[6]);
                statusValueSwitcher.setCurrentText(stringValueSwitcher[6]);

                Log.d("ChannelProgressVal", "onProgressChanged:Left: "+left);
                Log.d("ChannelProgressVal", "onProgressChanged:Right: "+right);

                currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putInt("CP", progress);
                editor.putInt("leftRead",leftRead);
                editor.putInt("rightRead",rightRead);
                editor.commit();

                Log.d("balanceKnob", "onProgressChanged:Balance: "+progress);
            }

            @Override
            public void onStartTrackingTouch(Croller croller) {
                // tracking started
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                if(!mIsPlaying)
                mediaController.getTransportControls().play();
                statusTextSwitcher.setText(stringStatusSwitcher[6]);
                statusValueSwitcher.setText(stringValueSwitcher[6]);
                Log.d("volumeTouchStart", "autoVolumeInvoked: "+autoVol);
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {

                statusTextSwitcher.setCurrentText(stringStatusSwitcher[6]);
                statusValueSwitcher.setCurrentText(stringValueSwitcher[6]);
                // tracking stopped

                Log.d("volumeTouchEnd", "autoVolumeInvoked: "+autoVol);
            }
        });


        tempoKnob.setMax(10);
        tempoKnob.setMin(0);
        // tempoKnob.setProgress(tempoProgress);

        tempoKnob.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                if (Build.VERSION.SDK_INT <Build.VERSION_CODES.M) {
                    tempoKnob.setEnabled(false);
                    return;
                }

                switch (progress){

                    case 0: tempo=0.5f;
                        break;
                    case 1: tempo=0.6f;
                        break;
                    case 2: tempo=0.7f;
                        break;
                    case 3: tempo=0.8f;
                        break;
                    case 4: tempo=0.9f;
                        break;
                    case 5: tempo=1.0f;
                        Vibration.Companion.vibrate(20);
                        break;
                    case 6: tempo=1.2f;
                        break;
                    case 7: tempo=1.4f;
                        break;
                    case 8: tempo=1.6f;
                        break;
                    case 9: tempo=1.8f;
                        break;
                    case 10: tempo=2.0f;
                        break;

                }
                tempoProgress=progress;

                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                Bundle bundleTMP = new Bundle();
                bundleTMP.putFloat("tempo",tempo );
                if(mIsPlaying) {
                    Log.e("TempoInvoked", "onProgressChanged: "+tempo );
                    mediaController.sendCommand("Tempo", bundleTMP, resultReceiver);
                }
                stringValueSwitcher[7] = tempo+"x";
                statusTextSwitcher.setCurrentText(stringStatusSwitcher[7]);
                statusValueSwitcher.setCurrentText( stringValueSwitcher[7]);


                currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putInt("tempoProgress", progress);
                editor.commit();

            }

            @Override
            public void onStartTrackingTouch(Croller croller) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "Unsupported Feature!", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                    Vibration.Companion.vibrate(20);
                    tempoKnob.setEnabled(false);
                    return;
                }
                    mStatusUpdateHandler.removeCallbacks(mUpdateStatus);
                    if (!mIsPlaying)
                        mediaController.getTransportControls().play();

                    statusTextSwitcher.setText(stringStatusSwitcher[7]);
                    statusValueSwitcher.setText(stringValueSwitcher[7]);
                    // tracking started

            }

            @Override
            public void onStopTrackingTouch(Croller croller) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    tempoKnob.setEnabled(false);
                }

                statusTextSwitcher.setCurrentText(stringStatusSwitcher[7]);
                statusValueSwitcher.setCurrentText( stringValueSwitcher[7]);

            }
        });

    }


    public void inflateVisualizer(){
        albumArtHiderHandler.removeCallbacks(mHideAlbum);
        currentArtIndex=MusicLibrary.currentArtIndex;
        if(equalizerFlag){
            slideDown(equalizerPagerLayout);
            viewPager.setAdapter(null);
            reverbPager.setAdapter(null);
            EqViewPager.setAdapter(null);
            equalizerFlag=false;
            nextPage.clearAnimation();
            nextPage.setVisibility(View.GONE);
            prevPage.clearAnimation();
            prevPage.setVisibility(View.GONE);
        }

        View v = getLayoutInflater().inflate(R.layout.visualizer_layout, middleParent,false);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT);
        middleParent.removeAllViews();
        middleParent.addView(v,lp);
        mainAlbumArt=findViewById(R.id.main_album_art);

        if(albumArt==null)
        {
            mainAlbumArt.setImageBitmap(null);

        }else {
            mainAlbumArt.setImageBitmap(albumArt);
            mainAlbumArt.startAnimation(zoomin);
            startAnimListener();
        }

        if(albumAutoHide || albumArt==null) {
            albumArtHiderHandler.postDelayed(mHideAlbum, 7000);

        }else if(customAlbum){

            albumArtChangerHandler.postDelayed(mAlbumArtChanger,10000);
        }

        mainAlbumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(customAlbum) {


                    Vibration.Companion.vibrate(20);
                    changeAlbumArt();
                    zoomout.cancel();
                    mainAlbumArt.startAnimation(zoomin);
                    albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                    albumArtChangerHandler.postDelayed(mAlbumArtChanger, 10000);
                }

            }

        });


        mainAlbumArt.setLongClickable(true);

        mainAlbumArt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(customAlbum) {
                    slideMode=true;

                    Intent intent = new Intent(MainActivity.this, SlideshowActivity.class);
                    intent.putExtra("artIndex", SlideArtIndex);
                    Log.e("sendArtIndex", "onLongClick: "+ SlideArtIndex);
                    startActivityForResult(intent, 3);
                    Animations.Animations(MainActivity.this,enterAnim);


                }else {
                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "Fullscreen Unavailable!", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();

                }

                return true;
            }

        });

            }

    private void initStatusView() {
        currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);

    try {
    stringValueSwitcher[0] = (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100) / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) + "%";
    stringValueSwitcher[3] = music_styles[equalizerIndex];
    stringValueSwitcher[1] = ((currentState.getInt("BassValue", 500) * 100) / 1000) + "%";
    stringValueSwitcher[2] = ((currentState.getInt("VirtualValue", 500) * 100) / 1000) + "%";
    stringValueSwitcher[4] = ((currentState.getInt("LoudValue", 500)*100)/1000) + "%";
    stringValueSwitcher[5] = ((currentState.getInt("TrableValue", 500)*100)/1000) + "%";
        stringValueSwitcher[8] = reverb_styles[reverbIndex];
        stringValueSwitcher[6] = "L"+leftRead+"  "+"R"+rightRead;
        stringValueSwitcher[7] = tempo+"x";


        }catch (Throwable e){

        Log.e("StatusViewException", "initStatusView: Strings "+e );
        }
    }

    public void inflateVideoView(){
        Log.d("VideoViewInflating", "inflateVideoView: In ");

            if(isActivityRunning) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(MainActivity.this)) {
                        brightnessSystem = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
                        if (brightnessCurrent > -1) {
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessCurrent);
                        }
                    }
                } else {
                    brightnessSystem = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
                    if (brightnessCurrent > -1){
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessCurrent);
                }}
            }
            clearEqualizers();
            if(isActivityRunning && mediaController!=null)
            mediaController.getTransportControls().stop();

        if(equalizerFlag){
            slideDown(equalizerPagerLayout);
            equalizerPagerLayout.setVisibility(View.GONE);
            equalizerFlag=false;
            nextPage.clearAnimation();
            nextPage.setVisibility(View.GONE);
            prevPage.clearAnimation();
            prevPage.setVisibility(View.GONE);
        }

        if(sleepflag){
            slideDown(sleepLayout);
            sleepLayout.setVisibility(View.GONE);
            sleepflag=false;
        }

        View v = getLayoutInflater().inflate(R.layout.video_view, middleParent,false);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT);

        View vsx = getLayoutInflater().inflate(R.layout.video_volume_layout, middleParent,false);
       ViewGroup.LayoutParams lps =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

        View vbx = getLayoutInflater().inflate(R.layout.video_brightness_layout, middleParent,false);


        middleParent.removeAllViews();
        middleParent.addView(v,lp);
        middleParent.addView(vsx,lps);
        middleParent.addView(vbx,lps);

        RotateLayout volume_seek_wrap = findViewById(R.id.vol_seekbar_wrapper);
        RotateLayout bright_seek_wrap = findViewById(R.id.bright_seekbar_wrapper);

        ConstraintSet constraintSet=new ConstraintSet();
        constraintSet.clone(middleParent);
        constraintSet.connect(volume_seek_wrap.getId(), ConstraintSet.END, middleParent.getId(), ConstraintSet.END,15);
        constraintSet.connect(volume_seek_wrap.getId(), ConstraintSet.TOP, middleParent.getId(), ConstraintSet.TOP,0);
        constraintSet.connect(volume_seek_wrap.getId(), ConstraintSet.BOTTOM, middleParent.getId(), ConstraintSet.BOTTOM,0);
        constraintSet.applyTo(middleParent);

        ConstraintSet constraintSetB=new ConstraintSet();
        constraintSetB.clone(middleParent);
        constraintSetB.connect(bright_seek_wrap.getId(), ConstraintSet.START, middleParent.getId(), ConstraintSet.START,15);
        constraintSetB.connect(bright_seek_wrap.getId(), ConstraintSet.TOP, middleParent.getId(), ConstraintSet.TOP,0);
        constraintSetB.connect(bright_seek_wrap.getId(), ConstraintSet.BOTTOM, middleParent.getId(), ConstraintSet.BOTTOM,0);
        constraintSetB.applyTo(middleParent);


    if(videoSeekbar==null) {
        mSeekBarAudio.setVisibility(View.GONE);
        mSeekBarAudio.setEnabled(false);


    View vs = getLayoutInflater().inflate(R.layout.video_seekbar, seekbarWrapper, false);
        seekbarWrapper.addView(vs);
        videoSeekbar = vs.findViewById(R.id.video_seekbar);
        videoSeekbar.setVisibility(View.VISIBLE);
        videoSeekbar.setEnabled(true);

    }else {
        mSeekBarAudio.setVisibility(View.GONE);
        mSeekBarAudio.setEnabled(false);
        videoSeekbar.setVisibility(View.VISIBLE);
        videoSeekbar.setEnabled(true);
    }


        videoView=findViewById(R.id.videoView);
         videoWrapper=findViewById(R.id.video_view_wrapper);
        volumeSeekbar=findViewById(R.id.video_vol_seekbar);
        brightnessSeekbar=findViewById(R.id.video_bright_seekbar);

        if(density<=DisplayMetrics.DENSITY_XHIGH){
            fitwidth=currentState.getBoolean("isFillWidth",false);
            widthDelta=currentState.getInt("DeltaWidth",80);
            heightDelta=currentState.getInt("DeltaHeight",100);
        }

        if(!fitwidth){

            ConstraintLayout.LayoutParams lpv =
                    new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.MATCH_PARENT);
            videoView.setLayoutParams(lpv);

            BitmapDrawable bg = (BitmapDrawable)getResources().getDrawable(R.drawable.video_back);
            bg.setGravity(Gravity.FILL);
            videoWrapper.setBackground(bg);

            ConstraintSet constraintSetx=new ConstraintSet();
            constraintSetx.clone(videoWrapper);
            constraintSetx.connect(videoView.getId(), ConstraintSet.LEFT, videoWrapper.getId(), ConstraintSet.LEFT,0);
            constraintSetx.connect(videoView.getId(), ConstraintSet.RIGHT, videoWrapper.getId(), ConstraintSet.RIGHT,0);
            constraintSetx.applyTo(videoWrapper);

        }else{

            videoWrapper.setBackgroundColor(getResources().getColor(R.color.black));
        }
        videoView.setVideoURI(videoUri);
        initVideoView();

        Log.d("VideoViewInflatingOut", "inflateVideoView: Out ");


    }

    private void initVideoView(){

        mScaleGestureDetector = new ScaleGestureDetector(videoView.getContext(), new MyScaleGestureListener());

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                Log.d("VideoViewOnCompletion", "onCompletion: Invoked");
                misVideoPlaying=true;
                videofinished=true;
                mSeekbarUpdateHandler.removeCallbacks(mUpdateseekbar);
                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                clearEqualizers();
                SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putLong("currentPosition", 0);
                editor.commit();
                PlayPause.setImageResource(R.drawable.btn_play);
                isPlay=false;

                if(mIsRepeat==1) {
                    misVideoPlaying=true;
                    videoView.setVideoURI(videoUri);
                  //  videoView.start();
                    PlayPause.setImageResource(R.drawable.btn_pause);
                    isPlay=true;
                    mSeekbarUpdateHandler.postDelayed(mUpdateseekbar,0);
                }else if(mIsRepeat==2) {
                    mediaController.getTransportControls().skipToNext();
                }
            }
        });

        final Runnable mLongTouchRun = new Runnable() {
            @Override
            public void run() {

                vLongTouchHandler.removeCallbacksAndMessages(null);
                if(longTouch && !scaleGesture)
                changeVideoAspect();
                longTouch=false;

                }
            };


        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mScaleGestureDetector.onTouchEvent(event);


                if(event.getAction() == MotionEvent.ACTION_DOWN && !scaleGesture)
                {
                    Log.d("OnTouchDown", "ACTION_DOWN");
                    longTouch=true;

                    vLongTouchHandler.postDelayed(mLongTouchRun,500);

                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    Log.d("OnTouchUp", "ACTION_UP");

                    if(longTouch)
                    {
                        longTouch=false;
                      vLongTouchHandler.removeCallbacks(mLongTouchRun);



                        if(touchReady) {
                            isFullScreen=true;
                            videoseekPos=videoView.getCurrentPosition();
                            Intent intent = new Intent(MainActivity.this, FullscreenVideoActivity.class);
                            intent.putExtra("VideoUri", videoUri.toString());
                            intent.putExtra("Title",FullScreenTitle);
                            intent.putExtra("seekPos",videoseekPos);
                            intent.putExtra("repeatABflag",isRepeatActive);
                            intent.putExtra("repeatStartTime",repeatDurationA);
                            intent.putExtra("repeatEndTime",repeatDurationB);
                            startActivityForResult(intent, 2);
                            Animations.Animations(MainActivity.this,enterAnim);

                            touchReady=false;
                        }else {

                            touchReady = true;
                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "Tap again to Fullscreen", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();

                            new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    touchReady=false;
                                }
                            }, 3000);
                        }
                    }
                }

                return true;
            }});



        videoView.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {

            @Override
            public void onPlay() {

                if(isActivityRunning) {
                    videoViewDisabled=false;

                   if(videoSeekbar.getVisibility()!=View.VISIBLE){
                       mSeekBarAudio.setVisibility(View.GONE);
                       mSeekBarAudio.setEnabled(false);
                       videoSeekbar.setVisibility(View.VISIBLE);
                       videoSeekbar.setEnabled(true);
                   }
                    PlayPause.setImageResource(R.drawable.btn_pause);
                    isPlay=true;
                    misVideoPaused=false;
                   videofinished=false;
                   volumeSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                   mSeekbarUpdateHandler.postDelayed(mUpdateseekbar,1000);
                   volSeekHiderHandler.postDelayed(volSeekHider,2000);
               }
            }


            @Override
            public void onPause() {

                System.out.println("PauseVideoView!");
                if(supressOnPause){
                    videoseekPos= videoView.getCurrentPosition();
                    supressOnPause=false;

                }else if(mediaController!=null && !isFullScreen) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (Settings.System.canWrite(MainActivity.this)) {
                                brightnessCurrent = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
                                SharedPreferences.Editor editor = currentState.edit();
                                editor.putInt("brightness", brightnessCurrent);
                                editor.commit();
                                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessSystem);
                            }
                        } else {
                            brightnessCurrent = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
                            SharedPreferences.Editor editor = currentState.edit();
                            editor.putInt("brightness", brightnessCurrent);
                            editor.commit();
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessSystem);
                        }

                    clearEqualizers();
                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                    mediaController.getTransportControls().play();
                    mediaController.getTransportControls().seekTo(videoView.getCurrentPosition());
                    mediaController.sendCommand("TempoX",null,resultReceiver);
                    misVideoPaused=false;
                    videoViewDisabled=true;

                }
                mSeekbarUpdateHandler.removeCallbacks(mUpdateseekbar);

            }

        });


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {

                if(fadeInFadeOut)
                targetVolume= mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                Log.d("TargetVol", "onPrepared:TargetVolumeSet : "+targetVolume);
                originalHeight=videoView.getHeight();
                originalWidth=videoView.getWidth();

                Log.d("VideoOriginalDimens", "originalHeight:"+originalHeight+" OriginalWidth: "+originalWidth );
                Log.d("VideoDeltaDimens", "DeltaHeight:"+heightDelta+" DeltaWidth: "+widthDelta );
                int duration = mp.getDuration();
                int videoDuration = videoView.getDuration();
                Log.d("onpreparedDuratn", String.format("onPrepared: duration=%d, videoDuration=%d", duration, videoDuration));

                if(isActivityRunning)
                mediaController.getTransportControls().stop();

                videoSeekbar.setMax(videoDuration);

                if(mEqualizerV==null)
                    startEqualizers(equalizerIndex,mp.getAudioSessionId(),mp);

                mp.setVolume(left, right);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && tempo!=1.0f ) {

                    mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(tempo));
                }


                EndTime.setText(timeConvert(videoDuration));
                videoSeekbar.setProgress(0);
                mSeekbarUpdateHandler.postDelayed(mUpdateseekbar,0);

               if(widthDelta!=0&& heightDelta!=0) {
                    ViewGroup.LayoutParams mRootParam = videoWrapper.getLayoutParams();
                    videoView.setFixedVideoSize(originalWidth+widthDelta, originalHeight+heightDelta); // important
                    mRootParam.width = originalWidth+widthDelta;
                    mRootParam.height = originalHeight+heightDelta;
                    ConstraintSet constraintSet=new ConstraintSet();
                    constraintSet.clone(middleParent);
                    constraintSet.connect(videoWrapper.getId(), ConstraintSet.LEFT, middleParent.getId(), ConstraintSet.LEFT,0);
                    constraintSet.connect(videoWrapper.getId(), ConstraintSet.RIGHT, middleParent.getId(), ConstraintSet.RIGHT,0);
                   constraintSet.connect(videoWrapper.getId(), ConstraintSet.TOP, middleParent.getId(), ConstraintSet.TOP,0);
                   constraintSet.connect(videoWrapper.getId(), ConstraintSet.BOTTOM, middleParent.getId(), ConstraintSet.BOTTOM,0);
                    constraintSet.applyTo(middleParent);
                    Log.e("onPrepareScale", "scale=" + ", w=" + videoWidth + ", h=" + videoHeight);
                }

                if(isActivityRunning) {

                   if(fadeInFadeOut) {
                       mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                       videoView.setFocusable(true);
                       videoView.start();
                       volDuckUpHandler.postDelayed(duckUpVolumeRunnable, 0);
                   }else {

                       videoView.setFocusable(true);
                       videoView.start();
                   }
                }

                }

        });

        videoSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(videoView!=null && fromUser) {

                    videoseekPos=progress;
                    StartTime.setText(timeConvert(progress));


                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if(isRepeatActive){

                    isRepeatActive=false;
                    repeatTimeA.setText("00:00");
                    repeatDurationA=-1;
                    repeatTimeB.setText("00:00");
                    repeatDurationB=-1;
                    Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                            "  RepeatA-B Reset  ", Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                    snack.show();
                }

                StartTime.setScaleX(1.1f);
                StartTime.setScaleY(1.1f);
                mSeekbarUpdateHandler.removeCallbacks(mUpdateseekbar);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                StartTime.setScaleX(1.0f);
                StartTime.setScaleY(1.0f);
                videoView.seekTo(videoseekPos);
                mSeekbarUpdateHandler.postDelayed(mUpdateseekbar,0);

            }
        });


        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        volumeSeekbar.setMax(mAudioManager != null ? mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) : 15);
        volumeSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        brightnessSeekbar.setProgress(Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0));

        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                stringValueSwitcher[0] =((progress*100)/mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))+"%";
                statusTextSwitcher.setCurrentText(stringStatusSwitcher[0]);
                statusValueSwitcher.setCurrentText( stringValueSwitcher[0]);
                currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putInt("PlayVol", progress);
                editor.commit();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


                volumeSeekbar.setAlpha(1f);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                volLevel = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                SharedPreferences.Editor editor = currentState.edit();
                editor.putInt("volLevel", volLevel);
                editor.commit();
                volSeekHiderHandler.postDelayed(volSeekHider,2000);


            }
        });

        brightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser && isActivityRunning) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(MainActivity.this)) {
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                            brightnessCurrent = progress;
                            SharedPreferences.Editor editor = currentState.edit();
                            editor.putInt("brightness", brightnessCurrent);
                            editor.commit();
                        } else {

                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "Grant Permission", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                        }
                    } else {
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                        brightnessCurrent = progress;
                        SharedPreferences.Editor editor = currentState.edit();
                        editor.putInt("brightness", brightnessCurrent);
                        editor.commit();

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(MainActivity.this)) {
                        openAndroidPermissionsMenu();
                    }
                }

                brightnessSeekbar.setAlpha(1f);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                volSeekHiderHandler.postDelayed(volSeekHider,2000);

            }
        });



    }

    private void inflateAudioSeekbar(){

        if(mSeekBarAudio.getVisibility()!=View.VISIBLE){
            videoSeekbar.setEnabled(false);
            videoSeekbar.setVisibility(View.GONE);

            mSeekBarAudio.setVisibility(View.VISIBLE);
            mSeekBarAudio.setEnabled(true);
            mSeekBarAudio.setMax(duration);


        }
        mSeekbarUpdateHandler.postDelayed(mUpdateseekbar, 0);



    }

    private static Uri  getURI(String id){

        return Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI + "/" + id);
    }


    private void changeVideoAspect(){
        if(fitwidth){

            videoView.setFixedVideoSize(middleParent.getWidth(), middleParent.getHeight()); // important
            ViewGroup.LayoutParams mRootParam =  videoWrapper.getLayoutParams();
            mRootParam.width = middleParent.getWidth();
            mRootParam.height = middleParent.getHeight();
            ConstraintSet constraintSetX=new ConstraintSet();
            constraintSetX.clone(middleParent);
            constraintSetX.connect(videoWrapper.getId(), ConstraintSet.LEFT, middleParent.getId(), ConstraintSet.LEFT,0);
            constraintSetX.connect(videoWrapper.getId(), ConstraintSet.RIGHT, middleParent.getId(), ConstraintSet.RIGHT,0);
            constraintSetX.connect(videoWrapper.getId(), ConstraintSet.TOP, middleParent.getId(), ConstraintSet.TOP,0);
            constraintSetX.connect(videoWrapper.getId(), ConstraintSet.BOTTOM, middleParent.getId(), ConstraintSet.BOTTOM,0);
            constraintSetX.applyTo(middleParent);

            ConstraintLayout.LayoutParams lpv =
                    new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.MATCH_PARENT);
            videoView.setLayoutParams(lpv);

           BitmapDrawable bg = (BitmapDrawable)getResources().getDrawable(R.drawable.video_back);
            bg.setGravity(Gravity.FILL);
            videoWrapper.setBackground(bg);

            ConstraintSet constraintSet=new ConstraintSet();
            constraintSet.clone(videoWrapper);
            constraintSet.connect(videoView.getId(), ConstraintSet.LEFT, videoWrapper.getId(), ConstraintSet.LEFT,0);
            constraintSet.connect(videoView.getId(), ConstraintSet.RIGHT, videoWrapper.getId(), ConstraintSet.RIGHT,0);
            constraintSet.applyTo(videoWrapper);



            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                    "Fit to height", Snackbar.LENGTH_SHORT);
            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
            snack.show();
            fitwidth=false;
            widthDelta=0;
            heightDelta=0;
            currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putBoolean("isFillWidth", false);
            editor.putInt("DeltaWidth", widthDelta);
            editor.putInt("DeltaHeight", heightDelta);
            editor.commit();

        }else {

            videoView.setFixedVideoSize(middleParent.getWidth(), middleParent.getHeight()); // important
            ViewGroup.LayoutParams mRootParam = videoWrapper.getLayoutParams();
            mRootParam.width = middleParent.getWidth();
            mRootParam.height = middleParent.getHeight();
            ConstraintSet constraintSetZ=new ConstraintSet();
            constraintSetZ.clone(middleParent);
            constraintSetZ.connect(videoWrapper.getId(), ConstraintSet.LEFT, middleParent.getId(), ConstraintSet.LEFT,0);
            constraintSetZ.connect(videoWrapper.getId(), ConstraintSet.RIGHT, middleParent.getId(), ConstraintSet.RIGHT,0);
            constraintSetZ.connect(videoWrapper.getId(), ConstraintSet.TOP, middleParent.getId(), ConstraintSet.TOP,0);
            constraintSetZ.connect(videoWrapper.getId(), ConstraintSet.BOTTOM, middleParent.getId(), ConstraintSet.BOTTOM,0);
            constraintSetZ.applyTo(middleParent);

            ConstraintLayout.LayoutParams lpv =
                    new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT);
            videoView.setLayoutParams(lpv);

            ConstraintSet constraintSet=new ConstraintSet();
            constraintSet.clone(videoWrapper);
            constraintSet.connect(videoView.getId(), ConstraintSet.TOP, videoWrapper.getId(), ConstraintSet.TOP,0);
            constraintSet.connect(videoView.getId(), ConstraintSet.BOTTOM, videoWrapper.getId(), ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(videoWrapper);


            videoWrapper.setBackgroundColor(getResources().getColor(R.color.black));

            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                    "Fit to width", Snackbar.LENGTH_SHORT);
            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
            snack.show();
            fitwidth=true;

            widthDelta=0;
            heightDelta=0;
            currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putBoolean("isFillWidth", true);
            editor.putInt("DeltaWidth", widthDelta);
            editor.putInt("DeltaHeight", heightDelta);
            editor.commit();


        }

    }

    private void inflateRepeatAB(){

            StartTime.setVisibility(View.GONE);
            EndTime.setVisibility(View.GONE);

        View v = getLayoutInflater().inflate(R.layout.repeat_point_a, metaLayoutView,false);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.BELOW,R.id.seekbar_wrapper);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_START);

        metaLayoutView.addView(v,relativeParams);

        View vb = getLayoutInflater().inflate(R.layout.repeat_point_b, metaLayoutView,false);
        RelativeLayout.LayoutParams relativeParamsb = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParamsb.addRule(RelativeLayout.BELOW,R.id.seekbar_wrapper);
        relativeParamsb.addRule(RelativeLayout.ALIGN_PARENT_END);
        metaLayoutView.addView(vb,relativeParamsb);

        isRepeatAB=true;
        repeatParentA=findViewById(R.id.point_a_parent);
        repeatParentB=findViewById(R.id.point_b_parent);
        repeatBtnA=findViewById(R.id.point_a_button);
        repeatBtnB=findViewById(R.id.point_b_button);
        repeatTimeA=findViewById(R.id.point_a_time);
        repeatTimeB=findViewById(R.id.point_b_time);
        repeatParentA.startAnimation(shakeSmall);
        repeatParentB.startAnimation(shakeSmall);
        repeatBtnA.setClickable(false);
        repeatBtnB.setClickable(false);
        repeatTimeA.setClickable(false);
        repeatTimeB.setClickable(false);
        repeatParentA.setClickable(true);
        repeatParentB.setClickable(true);

        Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                "  RepeatA-B Enabled  ", Snackbar.LENGTH_SHORT);
        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
        snack.show();

        initRepeatAB();

    }

    private void initRepeatAB(){

            if(isRepeatAB){

                repeatParentA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repeatParentA.startAnimation(shakeSmall);

                        if(repeatDurationA>-1 && !isRepeatActive){
                            repeatTimeA.setText("00:00");
                            repeatDurationA=-1;
                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "  Point A Reset  ", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                            return;

                        }

                        if(isRepeatActive){
                            isRepeatActive=false;
                            repeatTimeA.setText("00:00");
                            repeatDurationA=-1;
                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "  RepeatA-B Stopped  ", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                            return;
                        }

                        if(videoView!=null && misVideoPlaying) {
                            repeatTimeA.setText(timeConvert(videoView.getCurrentPosition()));
                            repeatDurationA=videoView.getCurrentPosition();

                        }else if(mediaController!=null){

                            repeatTimeA.setText(timeConvert((int) mediaController.getPlaybackState().getPosition()));
                            repeatDurationA=(int) (mediaController.getPlaybackState().getPosition());
                        }

                        if(repeatDurationB>0 && repeatDurationA>-1 && repeatDurationA<repeatDurationB){
                            isRepeatActive=true;
                            if(videoView!=null && misVideoPlaying){
                                videoView.seekTo(repeatDurationA);
                            }else if(mediaController!=null) {

                                mediaController.getTransportControls().seekTo(repeatDurationA);
                            }

                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "  RepeatA-B Started  ", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                        }else if(repeatDurationB==-1){
                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "  Now Select B  ", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();

                        } else if(repeatDurationA>repeatDurationB){

                            isRepeatActive=false;
                            repeatTimeA.setText("00:00");
                            repeatDurationA=-1;

                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "  Wrong Selection   ", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                        }



                    }
                    });

                repeatParentB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repeatParentB.startAnimation(shakeSmall);

                        if(repeatDurationB>-1 && !isRepeatActive){
                            repeatTimeB.setText("00:00");
                            repeatDurationB=-1;
                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "  Point B Reset  ", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                            return;

                        }
                        if(repeatDurationA==-1)
                        {
                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "  First Select A  ", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                            return;
                        }

                        if(isRepeatActive){
                            isRepeatActive=false;
                            repeatTimeB.setText("00:00");
                            repeatDurationB=-1;
                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "  RepeatA-B Stopped  ", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                            return;
                        }

                        if(videoView!=null && misVideoPlaying) {
                            repeatTimeB.setText(timeConvert(videoView.getCurrentPosition()));
                            repeatDurationB=videoView.getCurrentPosition();
                        }else if(mediaController!=null){

                            repeatTimeB.setText(timeConvert((int) mediaController.getPlaybackState().getPosition()));
                            repeatDurationB=(int) mediaController.getPlaybackState().getPosition();
                        }

                        if(repeatDurationA>-1 && repeatDurationB>0 && repeatDurationB>repeatDurationA) {

                            if (videoView != null && misVideoPlaying) {
                                videoView.seekTo(repeatDurationA);
                            } else if (mediaController != null) {

                                mediaController.getTransportControls().seekTo(repeatDurationA);
                            }

                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "  RepeatA-B Started  ", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                            isRepeatActive = true;
                        }else if(repeatDurationB<=repeatDurationA){
                            isRepeatActive=false;
                            repeatTimeB.setText("00:00");
                            repeatDurationB=-1;
                            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                                    "  Wrong selection ", Snackbar.LENGTH_SHORT);
                            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                            snack.show();
                        }


                    }
                });

            }
    }

    private void  removeRepeatAB(){

            if(isRepeatAB)
            {
                isRepeatAB=false;
                isRepeatActive=false;
                metaLayoutView.removeView(repeatParentA);
                metaLayoutView.removeView(repeatParentB);
                StartTime.setVisibility(View.VISIBLE);
                EndTime.setVisibility(View.VISIBLE);
                repeatParentA=null;
                repeatParentB=null;
                repeatBtnA=null;
                repeatBtnB=null;
                repeatTimeA=null;
                repeatTimeB=null;
                repeatDurationA=-1;
                repeatDurationB=-1;

                try {
                    if (videoView != null) {

                        StartTime.setText(timeConvert(videoView.getCurrentPosition()));

                    } else if(mediaController!=null) {

                        StartTime.setText(timeConvert((int) mediaController.getPlaybackState().getPosition()));
                    }
                }catch (Throwable e){

                    Log.e("ExceptionRaised", "removeRepeatAB: "+e );
                }

                Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                        "  RepeatA-B Disabled  ", Snackbar.LENGTH_SHORT);
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();
            }

    }


    private void inflateTrackLayout(){
         imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        isTrackLayout=true;
        trackSwitcher.setVisibility(View.GONE);
        trackValueSwitcher.setVisibility(View.GONE);
        statusTextSwitcher.setVisibility(View.GONE);
        statusValueSwitcher.setVisibility(View.GONE);

        View v = getLayoutInflater().inflate(R.layout.track_number_layout, statusLayout,false);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        statusLayout.addView(v,relativeParams);
        track_goto_parent=findViewById(R.id.track_goto_parent);
         track_num_input=findViewById(R.id.track_num_input);
        track_num_input.setTransformationMethod(null);
       // track_num_input.setRawInputType(Configuration.KEYBOARD_12KEY);
         track_input_layout=findViewById(R.id.track_num_parent);
         if(!ApplicationContextProvider.systemFont) {
             Typeface type = Typeface.createFromAsset(getAssets(), ApplicationContextProvider.getFontPath());
             track_input_layout.setTypeface(type);
         }
        track_num_input.requestFocus();
        initTrackLayout();


    }

    private void initTrackLayout() {

        ImageButton closeTrackLayout = findViewById(R.id.close_track_btn);

        closeTrackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.Companion.vibrate(20);
                removeTrackLayout();
            }
        });

        track_num_input.setKeyImeChangeListener(new CustomTextInputEditText.KeyImeChange() {

            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                // All keypresses with the keyboard open will come through here!
                // You could also bubble up the true/false if you wanted
                // to disable propagation.

                if(isTrackLayout)
                    removeTrackLayout();

            }
        });

        track_num_input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if(TextUtils.isEmpty(track_num_input.getText()) || Integer.parseInt(Objects.requireNonNull(track_num_input.getText()).toString())-1==-1) {
                        removeTrackLayout();
                        return true;
                    }
                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                    Bundle bundleT = new Bundle();
                    bundleT.putInt("trackId",Integer.parseInt(track_num_input.getText().toString())-1);
                    mediaController.sendCommand("playTrackId", bundleT, resultReceiver);
                    removeTrackLayout();
                    return true;
                }
                return false;
            }
        });

        track_num_input.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!TextUtils.isEmpty(s) ) {
                    //do your work here
                    resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                    Bundle bundleTI = new Bundle();
                    bundleTI.putInt("trackId",Integer.parseInt(s.toString())-1);
                    mediaController.sendCommand("playTrackInfo", bundleTI, resultReceiver);

                }else if(TextUtils.isEmpty(s)) {

                    track_input_layout.setHint("Play Track no.");
                }else if(Integer.parseInt(s.toString())-1==-1){
                    track_input_layout.setHint("Track not available!");
                    statusLayout.startAnimation(shakeSmall);

                }
            }



            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void removeTrackLayout(){

        imm.hideSoftInputFromWindow(track_num_input.getWindowToken(), 0);
        isTrackLayout=false;
           statusLayout.removeView(track_goto_parent);
        trackSwitcher.setVisibility(View.VISIBLE);
        trackValueSwitcher.setVisibility(View.VISIBLE);
        statusTextSwitcher.setVisibility(View.VISIBLE);
        statusValueSwitcher.setVisibility(View.VISIBLE);

    }


    private void start() {
        int captureSize = Visualizer.getCaptureSizeRange()[1];
        captureSize = Math.min(captureSize, 512);

        mVisualizer = new Visualizer(0);
        mVisualizer.setCaptureSize(captureSize);
        mVisualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, true);
        // Start capturing
        mVisualizer.setEnabled(true);


        /*
          Setup texture view
         */
        textureView = findViewById(R.id.visualizerTextureView);
        textureView.setSurfaceTextureListener(mRender = new VisualizerRenderer(this, captureSize / 2));
        textureView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v, int left, int top, int right, int bottom,
                    int oldLeft, int oldTop, int oldRight, int oldBottom) {

                mRender.onSurfaceTextureSizeChanged(null, v.getWidth(), v.getHeight());
            }
        });
        textureView.requestLayout();

        mRender.setSceneController(new SceneController() {
            @Override
            public void onSetup(Context context, int audioTextureId, int textureWidth) {
                mSceneList = new ArrayList<>();
                GLScene defaultScene = new BasicSpectrumScene(context, audioTextureId, textureWidth);
                mSceneList.add(Pair.create("Basic Spectrum", defaultScene));
                mSceneList.add(Pair.create("Circle Spectrum", new CircSpectrumScene(context, audioTextureId, textureWidth)));
                mSceneList.add(Pair.create("Enhanced Spectrum", new EnhancedSpectrumScene(context, audioTextureId, textureWidth)));
                mSceneList.add(Pair.create("Input Sound", new InputSoundScene(context, audioTextureId, textureWidth)));
                mSceneList.add(Pair.create("Sa2Wave", new Sa2WaveScene(context, audioTextureId, textureWidth)));
                mSceneList.add(Pair.create("Waves Remix", new WavesRemixScene(context, audioTextureId, textureWidth)));
                mSceneList.add(Pair.create("Rainbow Spectrum", new RainbowSpectrumScene(context, audioTextureId, textureWidth)));
                mSceneList.add(Pair.create("Chlast", new ChlastScene(context, audioTextureId, textureWidth)));
                mSceneList.add(Pair.create("Origin Texture", new OriginScene(context, audioTextureId, textureWidth)));

                try {
                    changeScene(mSceneList.get(visualID).second);
                }catch (Throwable e){
                    Log.e("ExceptionRaised", "onSetup: "+e );
                }
            }
        });




}

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        try {
            mRender.updateWaveFormFrame(new WaveFormFrame(waveform, 0, waveform.length / 2));
        }catch (Throwable e){

            Log.e("ExceptionRaised", "onWaveFormDataCapture: "+e );
        }
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        try {
            mRender.updateFFTFrame(new FFTFrame(fft, 0, fft.length / 2));
        }catch (Throwable e){

            Log.e("ExceptionRaised", "onFftDataCapture: "+e );
        }
    }








    private void shareApplication() {

        try {

            PackageManager pm = MainActivity.this.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(MainActivity.this.getPackageName(), 0);
            File srcFile = new File(ai.publicSourceDir);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            Uri uri = FileProvider.getUriForFile(getApplicationContext(), MainActivity.this.getPackageName(), srcFile);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            MainActivity.this.grantUriPermission(MainActivity.this.getPackageManager().toString(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            MainActivity.this.startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showFontDialog() {

        FragmentManager fm = getSupportFragmentManager();

       Sona_Font_Dialog sona_font_dialog = Sona_Font_Dialog.newInstance("Sona Font Chooser");

        sona_font_dialog.show(fm, "Sona Fonts");
    }

    @Override
    public void onFontApplied(int index) {

       // Toast.makeText(this, "Font Index: " + index, Snackbar.LENGTH_SHORT).show();

        if(index==0){
            currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putInt("fontIndex", index);
            editor.putBoolean("systemFont",true);
            editor.putBoolean("Random", false);
            editor.commit();
            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                    "Font " + ApplicationContextProvider.fontNameArray[index] + " Selected", Snackbar.LENGTH_SHORT);
            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
            snack.show();
        }else if(index<11){
            SharedPreferences.Editor editor ;
            currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            editor = currentState.edit();
            editor.putInt("fontIndex", index);
            editor.putBoolean("systemFont",false);
            editor.putBoolean("Random", false);
            editor.commit();
            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                    "Font " + ApplicationContextProvider.fontNameArray[index] + " Selected", Snackbar.LENGTH_SHORT);
            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
            snack.show();
        }else if(index==11){
            SharedPreferences.Editor editor;
            currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            editor = currentState.edit();
            editor.putBoolean("Random", true);
            editor.putBoolean("systemFont",false);
            editor.commit();
            Snackbar snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                    "Fonts Random Selected", Snackbar.LENGTH_SHORT);
            SnackbarHelper.configSnackbar(getApplicationContext(), snack);
            snack.show();
        }

        restartApp();

    }


    public void restartApp(){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder1.setMessage("Sona Player needs to be restarted for changes to take effect!");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "RESTART",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        try {

                            mediaController.getTransportControls().stop();

                            MainActivity.this.onDestroy();
                            finishAffinity();
                            finishAndRemoveTask();
                            stopService(new Intent(MainActivity.this, SonaHeartService.class));

                            ProcessPhoenix.triggerRebirth(getApplicationContext());

                        }catch (Throwable e){
                            Log.e("ExceptionRaised", "WhileKillingApp: "+e);
                            ProcessPhoenix.triggerRebirth(getApplicationContext());

                        }
                    }
                });

        builder1.setNegativeButton(
                "SUSPEND",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();

        if(alert11.getWindow()!=null)
        alert11.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alert11.show();




    }



    public  void loadEqualizers(int num,int audioId) {


        EndEqualizers();

        mEqualizerV = new Equalizer(1000, audioId);
        m = mEqualizerV.getNumberOfPresets();
        bandRange=mEqualizerV.getBandLevelRange()[1];
        music_styles = new String[m];
        for (int k = 0; k < m; k++) {
            music_styles[k] = mEqualizerV.getPresetName((short) k);
            Log.d("Equalizers", "Names: " + music_styles[k]);
        }
        mEqualizerV.usePreset((short) num);

        Log.d("EqualizerSet", "EqualizerIndex: "+num+" AudioId:"+audioId);



        mEqualizerV.setEnabled(true);
        equalizerIndex = num;


        Log.d("Trable lavel ", "UpperLimit: "+mEqualizerV.getBandLevelRange()[1]);
        Log.d("Trable lavel ", "LowerLimit: "+mEqualizerV.getBandLevelRange()[0]);



    }

    public void switchEq(int index) {

        try {
            mEqualizerV.usePreset((short) index);
            //mEqualizerV.setEnabled(true);
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
                mEqualizerV.setBandLevel((short) 4, val);
                mEqualizerV.setBandLevel((short) 3, val);
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

            if(isActivityRunning) {
                loadEqualizers(num, audioId);
                initBass(audioId, (short) currentState.getInt("BassValue", 1000));
                initVirtualizer(audioId, (short) currentState.getInt("VirtualValue", 1000));
                initLoudnessEnhancer(audioId, (short) currentState.getInt("LoudValue", 0));
                trableBoost((short) currentState.getInt("TrableValue", 0));
                initReverb((short) reverbIndex, mediaPlayer);
            }

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

            bassBoostV = new BassBoost(1000, audioID);
            short savestr = (short) bassVal;
            if (savestr > 0) {
                setBassBoostStrength(savestr);

            } else {
                setBassBoostStrength((short) 0);
            }
            bassBoostV.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void EndBass() {
        if (bassBoostV != null) {
            bassBoostV.release();
            bassBoostV = null;

        }

    }

    public  void EndEqualizers(){
        if(mEqualizerV!=null){
            mEqualizerV.release();
            mEqualizerV=null;}


    }

    public  void setBassBoostStrength(short strength) {
        if (bassBoostV != null && bassBoostV.getStrengthSupported() &&  strength >= 0) {
            try {
                if (strength <= 1000) {
                    bassBoostV.setStrength(strength);
                    Log.d("BassBoostSet", "BassBoostStrength: "+strength);
                    SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putInt("BassValue",strength);
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
            virtualizerV = new Virtualizer(1000, audioID);
            if (strength > 0) {
                setVirtualizerStrength(strength);
            }else {
                setVirtualizerStrength((short) 0);
            }
            virtualizerV.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void setVirtualizerStrength(short strength) {
        if (virtualizerV != null && virtualizerV.getStrengthSupported() && strength >= 0) {
            try {
                if (strength <= 1000) {
                    virtualizerV.setStrength(strength);

                    Log.d("virtualizerSet", "setVirtualizerStrength: "+strength);
                    SharedPreferences currentState = this.getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = currentState.edit();
                    editor.putInt("VirtualValue", strength);
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
        if (virtualizerV != null) {
            virtualizerV.release();
            virtualizerV = null;
        }
    }






    public  void initLoudnessEnhancer(int audioID,int loud) {
        EndLoudnessEnhancer();
        try {
            loudnessEnhancerV = new LoudnessEnhancer(audioID);
            setLoudnessEnhancerGain(Math.max(loud, 0));
            loudnessEnhancerV.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void setLoudnessEnhancerGain(int gain) {
        if (loudnessEnhancerV != null && gain >= 0) {
            try {
                if (gain <= 1000) {
                    loudnessEnhancerV.setTargetGain(gain);
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
        if (loudnessEnhancerV != null) {
            loudnessEnhancerV.release();
            loudnessEnhancerV = null;
        }
    }

    public static void initReverb( short strength,MediaPlayer mediaPlayer) {
        EndReverb();
        try {
            if(strength!=0 ) {
                presetReverbV = new PresetReverb(1, mediaPlayer.getAudioSessionId());
                presetReverbV.setPreset(strength);
                presetReverbV.setEnabled(true);
                // mediaPlayer.attachAuxEffect(presetReverbV.getId());
              //  mediaPlayer.setAuxEffectSendLevel(1.0f);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void EndReverb() {
        if (presetReverbV != null) {
            presetReverbV.setEnabled(false);
            presetReverbV.release();
            presetReverbV = null;
        }
    }


    public  class CustomPagerAdapter extends PagerAdapter {

        private CustomPagerAdapter() {

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup collection, int position) {
            ModelObject modelObject = ModelObject.values()[position];
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
           if(position==1) {
               initEqListener(layout);
               initViewPager(layout);
           }else if(position==0){
               initBassListener(layout);
           }else if(position==2){
               initReverbPager(layout);
               initChListener(layout);
           }
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup collection, int position,@NonNull Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return ModelObject.values().length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view,@NonNull Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            ModelObject customPagerEnum = ModelObject.values()[position];
            return customPagerEnum.getTitleResId();
        }


    }

    private class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

        SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = currentState.edit();

    ViewGroup.LayoutParams mRootParam=videoWrapper.getLayoutParams();
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // scale our video view


            videoWidth *= detector.getScaleFactor();
            videoHeight *= detector.getScaleFactor();


            if (videoWidth < 400 || videoWidth > 1800) { // limits width
                videoWidth = videoView.getWidth();
                videoHeight = videoView.getHeight();
            }


            Log.d("onScale", "scale=" + detector.getScaleFactor() + ", w=" + videoWidth + ", h=" + videoHeight);


            videoView.setFixedVideoSize(videoWidth, videoHeight); // important
            mRootParam.width = videoWidth;
            mRootParam.height = videoHeight;

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(middleParent);
            constraintSet.connect(videoWrapper.getId(), ConstraintSet.LEFT, middleParent.getId(), ConstraintSet.LEFT, 0);
            constraintSet.connect(videoWrapper.getId(), ConstraintSet.RIGHT, middleParent.getId(), ConstraintSet.RIGHT, 0);
            constraintSet.connect(videoWrapper.getId(), ConstraintSet.TOP, middleParent.getId(), ConstraintSet.TOP, 0);
            constraintSet.connect(videoWrapper.getId(), ConstraintSet.BOTTOM, middleParent.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.applyTo(middleParent);

            Log.d("onScaleprogress", " OriginalWidth:" + originalWidth + " OriginalHeight" + originalHeight);


            if (originalWidth < videoWidth && originalHeight < videoHeight) {
                widthDelta = videoWidth - originalWidth;
                    heightDelta = videoHeight - originalHeight;
                Log.d("DeltaCalculationInvoked", "WidthDelta: " + widthDelta + " HeightDelta:" + heightDelta);

            } else {

                widthDelta = 0;
                heightDelta = 0;
            }


            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            scaleGesture=true;
            longTouch=false;
            videoWidth = videoView.getWidth();
            videoHeight = videoView.getHeight();

            Log.d("onScaleBegin", "scale=" + detector.getScaleFactor() + ", w=" + videoWidth + ", h=" + videoHeight);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.d("onScaleEnd", "scale=" + detector.getScaleFactor() + ", w=" + videoWidth + ", h=" + videoHeight);
            Log.d("onScaleEnd",  " OriginalWidth:" + originalWidth + " OriginalHeight" + originalHeight);


            scaleGesture=false;

            editor.putInt("DeltaWidth", widthDelta);
            editor.putInt("DeltaHeight", heightDelta);
            editor.commit();

            Log.e("DeltaCalculationFinal", "WidthDelta: " + widthDelta + " HeightDelta:" + heightDelta);

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


    private Runnable duckUpVolumeRunnable = new Runnable() {

        @Override
        public void run() {
            int currentVolume= mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.d("VideoVolHandler", "run: Starting");
            if (currentVolume < targetVolume) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (currentVolume + 1),0);
                volumeSeekbar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                Log.d("VideoVolHandler", "volumeChanged: "+(currentVolume+1));

                volDuckUpHandler.postDelayed(this, 50);
            }

        }

    };



    private void openAndroidPermissionsMenu() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Special Permission");
        builder.setMessage("Sona Player needs MODIFY SYSTEM SETTINGS permissions to control Brightness");
        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent=null ;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                }
                if (intent != null) {
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }


    private final BroadcastReceiver killActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mediaController.getTransportControls().stop();
            MainActivity.this.onDestroy();
            finishAffinity();
            finishAndRemoveTask();
            stopService(new Intent(MainActivity.this, SonaHeartService.class));
            //android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);

        }
    };
    private final BroadcastReceiver QueueToggle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean queueSwitch=intent.getBooleanExtra("QueueSwitch",false);
            boolean startActivity=intent.getBooleanExtra("StartActivity",false);
            boolean startQueue=intent.getBooleanExtra("StartQueue",false);
            boolean skipNext=intent.getBooleanExtra("SkipNext",false);

            if(startActivity) {
                Intent intentQA = new Intent(MainActivity.this, QueueListActivity.class);
                startActivityForResult(intentQA, 1);
                Animations.Animations(MainActivity.this, enterAnim);
            }

            if(startQueue) {
                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                Bundle bundleQT = new Bundle();
                bundleQT.putBoolean("QueueSwitch", queueSwitch);
                isQueueActive=queueSwitch;
                mediaController.sendCommand("QueueToggle", bundleQT, resultReceiver);
                mUpdateTagsHandler.postDelayed(mUpdateTags, 0);
                if(isQueueActive){
                    stringTrackSwitcher[0]="Queue:";
                }else {
                    stringTrackSwitcher[0]="Track:";
                }
            }

            if(skipNext){
                mediaController.getTransportControls().skipToNext();
            }

        }
    };

    private final BroadcastReceiver refreshLibrary = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
            mediaController.sendCommand("scanMedia", null, resultReceiver);
             Log.d("RefreshBroadcast", "onReceive:Invoked ");
            //Toast.makeText(getApplicationContext(),"Refresh ActivityBroadcast",Toast.LENGTH_LONG).show();
        }
    };

    private final BroadcastReceiver saveTagResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle newTagsBundle=intent.getBundleExtra("BUNDLE");


            if(newTagsBundle!=null && mediaController.getMetadata().getString("SONGPATH").equals(newTagsBundle.getString("SONGPATH"))) {
                Log.e("EnteredInTagUpdate", "onReceive: Invoked");
                Bitmap bmp = null;
                try {
                    String filename = intent.getStringExtra("albumImage");
                    FileInputStream is = MainActivity.this.openFileInput(filename);
                    bmp = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (bmp != null) {
                    newTagsBundle.putParcelable("IMAGE", bmp);
                    Log.d("BitmapReceivedSuccess", "onReceive: ");
                } else {
                    Log.d("NullBitmapReceived", "onReceive: ");
                }
                updateNewTags(newTagsBundle);
            }else {

                Log.e("TagUpdateSuspended", "onReceive: Invoked");
            }

            Log.d("SaveTagBroadcast", "onReceive:Invoked:MediaId= "+intent.getStringExtra("renameId"));
            SharedPreferences currentState = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = currentState.edit();
            editor.putString("searchId", intent.getStringExtra("renameId"));
            editor.commit();

            Bundle bundle = new Bundle();
            bundle.putBoolean("rename", true);
            bundle.putString("renameId",intent.getStringExtra("renameId"));
            bundle.putBoolean("supressSeek",true);
            resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
            mediaController.sendCommand("scanMedia", bundle, resultReceiver);
        }
        };


    private  void  inflateOptionMenu(final Context context, String title, String subtitle, String meta, final String songData, final String mediaId, final boolean isVideoFile, final Bundle bundleMeta, final Bundle bundleTags) {
        TagEditFlag=false;

        Typeface currentTypeface=getTypeface();
        parentLayout = findViewById(R.id.main_activity_parent);
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
      View customView = inflater != null ? inflater.inflate(R.layout.popup_window_layout, null) : null;
        assert customView != null;
        final TextView headerTitle=customView.findViewById(R.id.popup_header_title);
        final TextView headerSubTitle=customView.findViewById(R.id.popup_header_subtitle);
        TextView headerMeta=customView.findViewById(R.id.popup_header_meta);
        TextView popup_delete=customView.findViewById(R.id.popup_delete_text);
        TextView popup_send=customView.findViewById(R.id.popup_send_text);
        TextView popup_info=customView.findViewById(R.id.popup_info_text);
        TextView popup_ringtone=customView.findViewById(R.id.popup_ringtone_text);
        ImageView headerIcon=customView.findViewById(R.id.popup_header_icon);
        LinearLayout popUpMarkLaout=customView.findViewById(R.id.popup_mark_layout);
       popUpMarkLaout.setVisibility(View.GONE);
        LinearLayout popUpSendLayout=customView.findViewById(R.id.popup_send_layout);
        popUpSendLayout.setPadding(0,20,0,0);

        headerTitle.setTypeface(currentTypeface);
        headerSubTitle.setTypeface(currentTypeface);
        headerMeta.setTypeface(currentTypeface);
        popup_delete.setTypeface(currentTypeface);
        popup_send.setTypeface(currentTypeface);
        popup_info.setTypeface(currentTypeface);
        popup_ringtone.setTypeface(currentTypeface);

        headerTitle.setText(title);
        headerSubTitle.setText(subtitle);
        headerMeta.setText(meta);
        headerSubTitle.setSelected(false);
        headerTitle.setSelected(false);

        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
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
        if (isVideoFile) {
            popup_ringtone.setTextColor(Color.parseColor("#004D68"));

            GlideApp
                    .with(headerIcon.getContext())
                    .asBitmap()
                    .load(Uri.fromFile(new File(songData)))
                    .transition(withCrossFade(factory))
                    .placeholder(R.drawable.default_album)
                    .error(R.drawable.default_album)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(headerIcon);
        } else {


            GlideApp
                    .with(headerIcon.getContext())
                    .asBitmap()
                    .load(songData)
                    .placeholder(R.drawable.main_art)
                    .transition(withCrossFade(factory))
                    .signature(new ObjectKey(new File(songData).lastModified()))
                    .error(R.drawable.default_album)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(headerIcon);
        }




        popup_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.Companion.vibrate(20);
                mPopupWindow.dismiss();
                sendItem(songData);
            }
        });

        popup_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.Companion.vibrate(20);
                mPopupWindow.dismiss();
                deleteItem(songData,mediaId);
            }
        });

        popup_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isVideoFile) {
                    Vibration.Companion.vibrate(20);
                    SonaToast.setToast(getApplicationContext(), "Unavailable for Videos", 0);

                } else {

                    final AlertDialog.Builder builderR = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog));
                    builderR.setMessage("Confirm set as ringtone?");
                    builderR.setCancelable(true);

                    builderR.setPositiveButton(
                            "YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    mPopupWindow.dismiss();
                                    Uri ringUri = Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + mediaId);
                                    RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, ringUri);
                                    SonaToast.setToast(getApplicationContext(), "Ringtone Set", 0);

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
                    if(alertR.getWindow()!=null)
                    alertR.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                    alertR.show();




                }
            }

        });

        popup_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.Companion.vibrate(20);
                    mPopupWindow.dismiss();
                inflateInfoWindow(getApplicationContext(),bundleMeta,songData,isVideoFile,true,mediaId,bundleTags,false,null,null);

            }
        });
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



    private void sendItem( String path){

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("*/*");
        Uri uri;
        File file = new File(path);
        Log.d("FilePacked", "path:"+path);
        uri = FileProvider.getUriForFile(getApplicationContext(), MainActivity.this.getPackageName(), file);
        MainActivity.this.grantUriPermission(MainActivity.this.getPackageManager().toString(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        MainActivity.this.startActivity(Intent.createChooser(intent, "Share Media via"));
    }


    private void deleteItem(final String path,final String mediaId) {
        TagEditFlag=false;
        Vibration.Companion.vibrate(20);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog));
        builder1.setTitle("Confirm Delete ?");
        builder1.setMessage("         Selected: " + 1);
        builder1.setCancelable(true);
        builder1.setIcon(R.drawable.delete_icons);
        builder1.setPositiveButton(
                "DELETE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        showProgressDialog();
                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
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
        if(alertDialog1.getWindow()!=null)
          alertDialog1.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog1.show();
    }

    private void showProgressDialog(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Deleting Media File");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        if( progressDialog.getWindow()!=null)
            //progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
            progressDialog.show();

    }

    private void updateLibrary(){
        progressDialog.setMessage("Updating Library...");
        resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
        mediaController.sendCommand("scanMedia", null, resultReceiver);
        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                Vibration.Companion.vibrate(20);

                SonaToast.setToast(getApplicationContext(),"Media Files Deleted",1);
            }}, 2000);

    }

    private boolean deleteMedia(Uri sdCardUri ,File file, String mediaId){
        DocumentFile documentFile=null;
        try {


            if( sdCardUri!=null)
                documentFile = DocumentFile.fromTreeUri(this, sdCardUri);



            String[] parts = (file.getPath()).split("/");

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

                    long longMediaId = Long.parseLong(mediaId);

                    Uri mediaContentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            longMediaId);
                    getContentResolver().delete(mediaContentUri, null, null);

                    deletePath=null;
                    deleteMediaId=null;
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


    private void getSdCardUri(){
        if(builder==null) {
            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Need SDCard Write Permission");
            builder.setMessage("Open File Manager > Choose SDCard > Tap SELECT to Grant Permission");
            builder.setPositiveButton("Open File Manager", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION|Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                    startActivityForResult(intent, 5);
                }
            });
            alertDialog = builder.create();
            if(alertDialog.getWindow()!=null)
           alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            alertDialog.show();
        }else {

            if(!alertDialog.isShowing()){
                //if its visibility is not showing then show here
                alertDialog.show();
            }
        }
    }

    private void inflateInfoWindow(Context context, final Bundle bundleMeta, final String songPath, final boolean isVideoFile,boolean isFirstTab, String mediaId,Bundle bundleTags,boolean isScrollEnd,Bitmap albumCropped,Bundle bundleRestoreText){
        TagEditFlag=false;

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        FragmentManager fm = getSupportFragmentManager();
         infoTabDialog = InfoTabDialog.newInstance("Info Tag Dialog",context,bundleMeta,songPath,isVideoFile,isFirstTab,imm,mediaId,bundleTags,isScrollEnd,albumCropped,bundleRestoreText);
        infoTabDialog.show(fm, "Sona Fonts");

    }

    private Bundle getInfoBundleMeta(){

        Bundle bundleData = new Bundle();
        boolean isVideoFile = false;
        if (mediaController.getMetadata().getString("VIDEO").equals("true")) {
            isVideoFile = true;
        }
        String fileName = mediaController.getMetadata().getString("FILENAME");
        bundleData.putString("FILENAME",fileName);
        if(isVideoFile) {
            bundleData.putString("RESOLUTION", mediaController.getMetadata().getString("WIDTH") + "x" + mediaController.getMetadata().getString("HEIGHT"));
            bundleData.putString("FRAMERATE", mediaController.getMetadata().getString("FRAMERATE"));
        }
        bundleData.putString("BITRATE",mediaController.getMetadata().getString("BITRATE"));
        bundleData.putString("SAMPLERATE",mediaController.getMetadata().getString("SAMPLERATE"));
        bundleData.putString("FORMAT",mediaController.getMetadata().getString("SONGFORMAT"));
        bundleData.putString("CHANNEL",mediaController.getMetadata().getString("CHANNEL"));
        bundleData.putString("SIZE",mediaController.getMetadata().getString("SIZE"));
        bundleData.putInt("DURATION",(((int)mediaController.getMetadata().getLong(MediaMetadataCompat.METADATA_KEY_DURATION)) / 1000));

        return bundleData;
    }

    private Bundle getInfoBundleTags(){
        Bundle bundleTags = new Bundle();
        bundleTags.putString("TITLE",mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        bundleTags.putString("ALBUM",mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
        bundleTags.putString("ARTIST",mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        bundleTags.putString("COMPOSER",mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_COMPOSER));
        bundleTags.putString("YEAR",mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_YEAR));
        bundleTags.putString("GENRE",mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_GENRE));
        bundleTags.putString("ALBUM_ID",mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));

    return bundleTags;
    }

    @Override
    public void onSaveTagResult(final String mediaNewId,Bundle newTagsBundle){
        Snackbar  snack = Snackbar.make(findViewById(R.id.snackbarLayout),
                "Updating...", Snackbar.LENGTH_LONG);
        SnackbarHelper.configSnackbar(getApplicationContext(), snack);
        snack.show();
        if(newTagsBundle!=null)
        updateNewTags(newTagsBundle);
        Log.e("renameID", "onSaveTagResult: ID:"+mediaNewId );

        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putBoolean("rename", true);
                bundle.putString("renameId",mediaNewId);
                resultReceiver = new AudioTokenReceiver(new Handler(Objects.requireNonNull(Looper.myLooper())));
                mediaController.sendCommand("scanMedia", bundle, resultReceiver);
            }
        }, 1000);


    }
    @Override
    public  void onSdCardUriResult(){
        TagEditFlag=true;
        getSdCardUri();

    }

    @Override
    public void onChooseAlbumArt(Bundle bundleMetaTemp, String songDataTemp, boolean isVideoFileTemp,String mediaId, Bundle bundleTagsTemp,Bundle restoreText){

        MainActivity.bundleMetaTemp =bundleMetaTemp;
        MainActivity.songDataTemp =songDataTemp;
        MainActivity.isVideoFileTemp =isVideoFileTemp;
        mediaIdTemp=mediaId;
        MainActivity.bundleTagsTemp =bundleTagsTemp;
        bundleRestoreText=restoreText;

        if(!sleepflag && !equalizerFlag && !misVideoPlaying ){

            boolean isAlbumHidden = false;
            if (mainAlbumArt.getVisibility() != View.VISIBLE)
                isAlbumHidden = true;

            clearVisualizer(clearVisualizerIndex);
            toggleVisualization(5,0);

            if (isAlbumHidden) {
                zoomin.cancel();
                zoomout.cancel();
                mainAlbumArt.clearAnimation();
                albumArtChangerHandler.removeCallbacks(mAlbumArtChanger);
                mainAlbumArt.setVisibility(View.GONE);
            }
        }else {
            visualizerIndex=5;
        }

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setAspectRatio(1,1)
                .start(this);

       /* Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 6);*/
    }



    private void updateNewTags(Bundle newTagsBundle){

        if(newTagsBundle!=null) {
            Log.d("BundleReceived", "updateNewTags:Started ");

            String newTitle = newTagsBundle.getString("TITLE");
            String newAlbum = newTagsBundle.getString("ALBUM");
            String newArtist = newTagsBundle.getString("ARTIST");
            String newComposer = newTagsBundle.getString("COMPOSER");
            String newYear = newTagsBundle.getString("YEAR");
            String newGenre = newTagsBundle.getString("GENRE");
            albumArt = newTagsBundle.getParcelable("IMAGE");

            Log.d("BundleValuesAre", "updateNewTags: "+newTitle+","+newAlbum);

            songName.setText(newTitle);
            strings[0] =newAlbum != null ? newAlbum : "Unknown Album";
            strings[1] =newArtist != null ? newArtist : "Unknown Artist";
            strings[2] = newComposer != null ? newComposer : "Unknown";
            strings[3] = newGenre != null ? newGenre : "Unknown Genre";
            strings[4] = newYear != null ? newYear : "Unknown Year";

            if (albumArt == null) {
                albumImage.setImageResource(R.drawable.main_art);
                if(mainAlbumArt!=null) {
                    mainAlbumArt.clearAnimation();
                    zoomin.cancel();
                    zoomout.cancel();
                    mainAlbumArt.setVisibility(View.GONE);
                }
            } else {
                albumImage.setImageBitmap(albumArt);
                if (mainAlbumArt != null) {
                    mainAlbumArt.setImageBitmap(albumArt);
                    mainAlbumArt.setVisibility(View.VISIBLE);
                    mainAlbumArt.setAnimation(zoomin);
                    startAnimListener();
                    if (albumAutoHide)
                        albumArtHiderHandler.postDelayed(mHideAlbum, 7000);

                }
            }

                mUpdateTagsHandler.postDelayed(mUpdateTags, 0);

        }else {

            Log.d("NullBundleReceived", "updateNewTags: Failed");
        }

    }
}




