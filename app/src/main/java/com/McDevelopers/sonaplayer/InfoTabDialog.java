package com.McDevelopers.sonaplayer;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.DialogFragment;
import androidx.documentfile.provider.DocumentFile;
import androidx.appcompat.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.images.Artwork;
import static org.jaudiotagger.tag.images.AndroidArtwork.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.calligraphy3.CalligraphyUtils;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static org.jaudiotagger.tag.FieldKey.ALBUM;
import static org.jaudiotagger.tag.FieldKey.ARTIST;
import static org.jaudiotagger.tag.FieldKey.COMPOSER;
import static org.jaudiotagger.tag.FieldKey.GENRE;
import static org.jaudiotagger.tag.FieldKey.TITLE;
import static org.jaudiotagger.tag.FieldKey.YEAR;



public class InfoTabDialog extends DialogFragment {
    @SuppressLint("StaticFieldLeak")
    private static Context contextG;
    private static boolean isVideoFileG;
    private static Bundle bundleMetaG;
    private static Bundle bundleTagsG;
    private static String songPathG;
    private static String mediaIdG;
    private static boolean isFirstTabG;
    private static InputMethodManager immG;
    private static Uri sdCardUri;
    private static SharedPreferences currentState;
   private static InputFilter filter;
    private static InfoTabResult resultTab;
    private static Bundle restoreTextBundleG;
    private  static boolean isScrollEndX=false;
    private static Bitmap albumNewCroppedX=null;
    private String[] genreMenu = {"Acid Punk","Acoustic","Bass","Blues","Chillout","Classical","Club","Comedy","Dance","Dark Wave","Death metal","Disco","Dream","Drum & Bass",
            "Electronic","Fast Fusion","Folk","Game","Heavy Metal","Hip-Hop","Instrumental","Jazz","Metal","New Age","Oldies",
            "Other","Pop","Power ballad","Rap", "Retro","Reggae","Rock","Rock ’n’ Roll","Slow jam","Soul","Soundtrack","Techno","Vocal"};

    private InputFilter[] filterArray = new InputFilter[7];

    public InfoTabDialog() {

        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public interface InfoTabResult {
        void onSaveTagResult(String mediaNewId,Bundle bundle);
        void onSdCardUriResult();
        void onChooseAlbumArt( Bundle bundleMetaTemp, String songDataTemp, boolean isVideoFileTemp,String mediaId, Bundle bundleTagsTemp,Bundle restoreText);
    }

    public static InfoTabDialog newInstance(String dialogTitle, Context context, final Bundle bundleMeta, final String songPath, final boolean isVideoFile,boolean isFirstTab,InputMethodManager imm,String mediaId,Bundle bundleTags,boolean isScrollEnd,Bitmap albumCropped,Bundle restoreTextBundle) {
        contextG=context;
        isVideoFileG=isVideoFile;
        bundleMetaG=bundleMeta;
        songPathG=songPath;
        isFirstTabG=isFirstTab;
        mediaIdG=mediaId;
        immG=imm;
        bundleTagsG=bundleTags;
        isScrollEndX=isScrollEnd;
        albumNewCroppedX=albumCropped;
        restoreTextBundleG=restoreTextBundle;
        currentState = context.getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        Log.d("GlobalSongPath", "newInstance: "+songPathG);
        String UriRaw=currentState.getString("SDCardUri",null);
        if(!TextUtils.isEmpty(UriRaw)) {
            sdCardUri = Uri.parse(UriRaw);
            Log.d("MediaListActivity", "onCreate: SdcardUri:" + UriRaw);
        }

        InfoTabDialog frag = new InfoTabDialog();
        Bundle args = new Bundle();
        args.putString("title", dialogTitle);
        frag.setArguments(args);
        return frag;

    }
        @Override
        public void onStart(){
            super.onStart();
            Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragment);
        return inflater.inflate(R.layout.info_tab_layout, container);

    }


    private String fileNewName="";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        resultTab= (InfoTabResult) getActivity();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final Typeface currentTypeface=getTypeface();
        final String fileName=bundleMetaG.getString("FILENAME");
        filterArray[0] = new InputFilter.LengthFilter(100);
        filterArray[1] = new InputFilter.LengthFilter(50);
        filterArray[2] = new InputFilter.LengthFilter(50);
        filterArray[3] = new InputFilter.LengthFilter(50);
        filterArray[4] = new InputFilter.LengthFilter(50);
        filterArray[5] = new InputFilter.LengthFilter(4);
        filterArray[6] = new InputFilter.LengthFilter(20);

        Log.d("FileNameInInfoWindow", "inflateInfoWindow: "+fileName);
        if(fileName!=null) {
            String target = (".") + (bundleMetaG.getString("FORMAT"));
            fileNewName = fileName.replaceAll("(?i)" + target, "");
        }

        // Initialize a new instance of LayoutInflater service

        TabLayout tabLayout = view.findViewById(R.id.tab_header);
        final RelativeLayout tabViewParent=view.findViewById(R.id.tab_view_parent);
        changeFontInViewGroup(tabLayout,ApplicationContextProvider.getFontPath());


         filter = new InputFilter()
        {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
            {
                if (source.length() < 1) return null;
                char last = source.charAt(source.length() - 1);
                String reservedChars = "?:\"*|/\\<>.";
                int type = Character.getType(last);
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
                if(reservedChars.indexOf(last) > -1) return source.subSequence(0, source.length() - 1);
                return null;
            }
        };

        if(isFirstTabG) {
            inflateFileInfoItem(tabViewParent, isVideoFileG, currentTypeface, bundleMetaG, songPathG, fileName);
        }else {
            inflateTagWindow(tabViewParent,currentTypeface,songPathG,bundleMetaG,bundleTagsG,isScrollEndX,albumNewCroppedX,restoreTextBundleG);
            Objects.requireNonNull(tabLayout.getTabAt(1)).select();
        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    inflateFileInfoItem(tabViewParent,isVideoFileG,currentTypeface,bundleMetaG,songPathG,fileName);
                } else {
                    inflateTagWindow(tabViewParent,currentTypeface,songPathG,bundleMetaG,bundleTagsG,false,null,null);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        if(getDialog().getWindow()!=null)
            getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

    }

    private Typeface getTypeface(){
        Typeface face;
        if(!ApplicationContextProvider.systemFont) {
            face = Typeface.createFromAsset(contextG.getAssets(),
                    ApplicationContextProvider.getFontPath());
        }else {

            face=Typeface.DEFAULT;
        }

        return face;
    }


    private void changeFontInViewGroup(ViewGroup viewGroup, String fontPath) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (TextView.class.isAssignableFrom(child.getClass())) {
                CalligraphyUtils.applyFontToTextView(child.getContext(), (TextView) child, fontPath);
            } else if (ViewGroup.class.isAssignableFrom(child.getClass())) {
                changeFontInViewGroup((ViewGroup) viewGroup.getChildAt(i), fontPath);
            }
        }
    }

    String title="Unknown";
    private String album="Unknown";
    private String artist="Unknown";
    private String composer="Unknown";
    private String year="Unknown";
    private String genre="Unknown";
    private Artwork artwork=null;
    private Artwork artworkReverted=null;
    private File albumArtFile=null;
    private boolean isArtworkChanged=false;

    private void inflateTagWindow(RelativeLayout tabViewParent, Typeface currentTypeface, final String songPath, final Bundle bundle, final Bundle bundleTags,final boolean isScrollEnd, Bitmap albumCroppedNew,Bundle restoreText){

        LayoutInflater inflater = (LayoutInflater) contextG.getSystemService(LAYOUT_INFLATER_SERVICE);
        RelativeLayout.LayoutParams layoutParams= new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        @SuppressLint("InflateParams") View customView = Objects.requireNonNull(inflater).inflate(R.layout.tag_popup_window,null);
        tabViewParent.removeAllViews();
        tabViewParent.addView(customView,layoutParams);

        final String ext= Objects.requireNonNull(bundle.getString("FORMAT")).toLowerCase();
        final String albumArtId=bundleTags.getString("ALBUM_ID");
        Log.d("AlbumIDReceiver", "inflateTagWindow: "+albumArtId);
        Uri albumUri=getAlbumUri(albumArtId);

        try {
            if (albumUri != null && albumUri.getPath() != null && !isVideoFileG)
                albumArtFile = new File(getRealPathFromURI(albumUri));
        }catch (Throwable e){
            albumArtFile=null;
            e.printStackTrace();
        }

            if(!isVideoFileG ) {

                try {
                TagOptionSingleton.getInstance().setAndroid(true);
                AudioFile f = AudioFileIO.read(new File(songPath));
                Tag tag = f.getTag();
                AudioHeader audioHeader=f.getAudioHeader();

                    Log.d("AudioHeader", "Bitrate: "+audioHeader.getBitRate());
                    Log.d("AudioHeader", "Channels: "+audioHeader.getChannels());
                    Log.d("AudioHeader", "EncodingType: "+audioHeader.getEncodingType());
                    Log.d("AudioHeader", "Format: "+audioHeader.getFormat());
                    Log.d("AudioHeader", "SampleRate: "+audioHeader.getSampleRate());
                    Log.d("AudioHeader", "TrackLength: "+audioHeader.getTrackLength());

                    try {
                        title = tag.getFirst(FieldKey.TITLE);
                        if(TextUtils.isEmpty(title)){
                            title=bundleTags.getString("TITLE");
                        }
                    } catch (Exception e) {
                        title=bundleTags.getString("TITLE");
                        Log.e("TitleNotFound", "inflateTagWindow: JaudioTagger");
                    }
                    try {
                        album = tag.getFirst(ALBUM);
                        if(TextUtils.isEmpty(album)){
                            album=bundleTags.getString("ALBUM");
                        }
                    } catch (Exception e) {
                        album=bundleTags.getString("ALBUM");
                        Log.e("AlbumNotFound", "inflateTagWindow: JaudioTagger");
                    }
                    try {
                        artist = tag.getFirst(FieldKey.ARTIST);
                        if(TextUtils.isEmpty(artist)){
                            artist=bundleTags.getString("ARTIST");
                        }
                    } catch (Exception e) {
                        artist=bundleTags.getString("ARTIST");
                        Log.e("ArtistNotFound", "inflateTagWindow: JaudioTagger");
                    }
                    try {
                        composer = tag.getFirst(FieldKey.COMPOSER);
                        if(TextUtils.isEmpty(composer)){
                            composer=bundleTags.getString("COMPOSER");
                        }
                    } catch (Exception e) {
                        composer=bundleTags.getString("COMPOSER");
                        Log.e("ComposerNotFound", "inflateTagWindow: JaudioTagger");
                    }
                    try {
                        year = tag.getFirst(FieldKey.YEAR);
                        if(TextUtils.isEmpty(year)){
                            year=bundleTags.getString("YEAR");
                        }
                    } catch (Exception e) {
                        year=bundleTags.getString("YEAR");
                        Log.e("YearNotFound", "inflateTagWindow: JaudioTagger");
                    }
                    try {
                        genre = tag.getFirst(FieldKey.GENRE);
                        if(TextUtils.isEmpty(genre)){
                            genre=bundleTags.getString("GENRE");
                        }
                    } catch (Exception e) {
                        genre=bundleTags.getString("GENRE");
                        Log.e("GenreNotFound", "inflateTagWindow: JaudioTagger");
                    }
                    try {
                        artwork = tag.getFirstArtwork();
                    } catch (Exception e) {
                        Log.e("ArtworkNotFound", "inflateTagWindow: JaudioTagger");
                    }

                    if(artwork==null){

                        try {
                            Log.e("ArtworkFromMetadata", "inflateTagWindow: Entered");
                            MediaMetadataRetriever metadataRetrieverG=new MediaMetadataRetriever();
                            metadataRetrieverG.setDataSource(songPath);
                            byte[] artByte = metadataRetrieverG.getEmbeddedPicture();
                            if(artByte!=null) {

                                artwork=createArtworkFromMetadataBlockDataPicture(new MetadataBlockDataPicture(artByte,0,"JPEG","",0,0,0,0));
                            }
                            Log.e("ArtworkFromMetadata", "inflateTagWindow: Exit");

                        } catch (Exception em) {
                            em.printStackTrace();
                        }
                    }

                    if(artwork==null && albumArtFile!=null){

                            try {

                                Log.d("ArtworkFromMediaStore", "inflateTagWindow: " + albumArtFile.getAbsolutePath());
                                artwork = createArtworkFromFile(albumArtFile);

                            } catch (Throwable ex) {

                                ex.printStackTrace();
                            }
                     }

                }catch (Throwable e){
                    e.printStackTrace();

                    title=bundleTags.getString("TITLE");
                    album=bundleTags.getString("ALBUM");
                    artist=bundleTags.getString("ARTIST");
                    composer=bundleTags.getString("COMPOSER");
                    year=bundleTags.getString("YEAR");
                    genre=bundleTags.getString("GENRE");

                    if(artwork==null){

                        try {
                            Log.e("ArtworkFromMetadata", "inflateTagWindow: Entered");
                            MediaMetadataRetriever metadataRetrieverG=new MediaMetadataRetriever();
                            metadataRetrieverG.setDataSource(songPath);
                            byte[] artByte = metadataRetrieverG.getEmbeddedPicture();
                            if(artByte!=null) {

                                artwork= createArtworkFromMetadataBlockDataPicture(new MetadataBlockDataPicture(artByte,0,"JPEG","",0,0,0,0));
                            }
                            Log.e("ArtworkFromMetadata", "inflateTagWindow: Exit");

                        } catch (Exception em) {
                            em.printStackTrace();
                        }
                    }

                    if(artwork==null && albumArtFile!=null) {
                        try {
                            Log.d("CurrentArtworkPath", "inflateTagWindow: " + albumArtFile.getAbsolutePath());
                            artwork = createArtworkFromFile(albumArtFile);

                        } catch (Throwable ex) {
                            artwork = null;
                            ex.printStackTrace();
                        }
                    }

                     }
                }else  {

                title=bundleTags.getString("TITLE");
                album=bundleTags.getString("ALBUM");
                artist=bundleTags.getString("ARTIST");
                composer=bundleTags.getString("COMPOSER");
                year=bundleTags.getString("YEAR");
                genre=bundleTags.getString("GENRE");


            }


            artworkReverted=artwork;

        TextView tagAlbumHeader=customView.findViewById(R.id.tag_album_header);
        tagAlbumHeader.setTypeface(currentTypeface);
        final TextView titleHeader=customView.findViewById(R.id.tag_title_textview);
        titleHeader.setTypeface(currentTypeface);
        final CustomEditText titleEditText=customView.findViewById(R.id.tag_title_edittext);
        titleEditText.setTypeface(currentTypeface);
        TextView albumHeader=customView.findViewById(R.id.tag_album_textview);
        albumHeader.setTypeface(currentTypeface);
        final CustomEditText albumEditText=customView.findViewById(R.id.tag_album_edittext);
        albumEditText.setTypeface(currentTypeface);
        TextView artistHeader=customView.findViewById(R.id.tag_artist_textview);
        artistHeader.setTypeface(currentTypeface);
        final CustomEditText artistEditText=customView.findViewById(R.id.tag_artist_edittext);
        artistEditText.setTypeface(currentTypeface);
        TextView composerHeader=customView.findViewById(R.id.tag_composer_textview);
        composerHeader.setTypeface(currentTypeface);
        final CustomEditText composerEditText=customView.findViewById(R.id.tag_composer_edittext);
        composerEditText.setTypeface(currentTypeface);
        TextView yearHeader=customView.findViewById(R.id.tag_year_textview);
        yearHeader.setTypeface(currentTypeface);
        final CustomEditText yearEditText=customView.findViewById(R.id.tag_year_edittext);
        yearEditText.setTypeface(currentTypeface);
        TextView genreHeader=customView.findViewById(R.id.tag_genre_textview);
        genreHeader.setTypeface(currentTypeface);
        final AutoCompleteTextView genreEditText=customView.findViewById(R.id.tag_genre_edittext);
        genreEditText.setTypeface(currentTypeface);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.genre_popup_menu, genreMenu);
        genreEditText.setThreshold(1);
        genreEditText.setAdapter(adapter);
        final ImageButton genreDropDown=customView.findViewById(R.id.tag_genre_dropdown);

       InputStream is;
        Bitmap bitmap=null;

        if(isScrollEnd){
            bitmap=albumCroppedNew;
            try {
                File newAlbumArt = saveBitmapToCache(albumCroppedNew);
                if(newAlbumArt!=null ) {
                    artwork = createArtworkFromFile(newAlbumArt);
                    isArtworkChanged=true;
                    Log.d("NewAlbumImageSet", "inflateTagWindow: Invoked"+newAlbumArt.getAbsolutePath());
                    final boolean delete = newAlbumArt.delete();
                    Log.d("CacheArtDelete", "inflateTagWindow: Cache ArtworkDeleted"+delete);
                }
            }catch (Throwable e){
                e.printStackTrace();
            }

        }else {
            if(artwork!=null) {
                try {
                    is = new ByteArrayInputStream(artwork.getBinaryData());
                    bitmap = BitmapFactory.decodeStream(is);
                }catch (Throwable e){
                    e.printStackTrace();
                }
            }else if(isVideoFileG){

                MediaMetadataRetriever metadataRetrieverG=new MediaMetadataRetriever();
                metadataRetrieverG.setDataSource(songPath);
                 bitmap = metadataRetrieverG.getFrameAtTime();
            }
        }


        final ImageView albumArtView=customView.findViewById(R.id.tag_album_art);
        if(bitmap!=null)
        albumArtView.setImageBitmap(bitmap);
        final Button clearAlbumArt=customView.findViewById(R.id.clear_albumart_button);
        clearAlbumArt.setTypeface(currentTypeface);
        final Button chooseAlbumArt=customView.findViewById(R.id.choose_albumart_button);
        chooseAlbumArt.setTypeface(currentTypeface);

        clearAlbumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Vibration.Companion.vibrate(10);

                if(artwork!=null) {
                    albumArtView.setImageResource(R.drawable.main_art);
                    artwork = null;
                    isArtworkChanged = true;
                }else {
                    SonaToast.setToast(contextG,"Nothing to remove",0);
                }

            }
        });

        albumArtView.setClickable(true);
        albumArtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Vibration.Companion.vibrate(10);

                if(isArtworkChanged) {
                    SonaToast.setToast(contextG, "Album Art Reverted", 0);
                    artwork = artworkReverted;
                    if (artwork != null) {
                        InputStream is = new ByteArrayInputStream(artwork.getBinaryData());
                        Bitmap bitmapX = BitmapFactory.decodeStream(is);
                        albumArtView.setImageBitmap(bitmapX);
                    } else {
                        albumArtView.setImageResource(R.drawable.main_art);
                    }
                    isArtworkChanged=false;
                }else {
                    SonaToast.setToast(contextG, "Nothing to revert", 0);

                }
            }
        });

        chooseAlbumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundleT=new Bundle();
                try {
                    bundleT.putString("TITLE", Objects.requireNonNull(titleEditText.getText()).toString());
                    bundleT.putString("ALBUM", Objects.requireNonNull(albumEditText.getText()).toString());
                    bundleT.putString("ARTIST", Objects.requireNonNull(artistEditText.getText()).toString());
                    bundleT.putString("COMPOSER", Objects.requireNonNull(composerEditText.getText()).toString());
                    bundleT.putString("YEAR", Objects.requireNonNull(yearEditText.getText()).toString());
                    bundleT.putString("GENRE",genreEditText.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resultTab.onChooseAlbumArt(bundle,songPath,isVideoFileG,mediaIdG,bundleTags,bundleT);
            }
        });

        TextView tagNote=customView.findViewById(R.id.tag_note);
        if(isVideoFileG){
            tagNote.setVisibility(View.VISIBLE);
            tagNote.setTypeface(currentTypeface);
        }else {
            tagNote.setVisibility(View.GONE);
        }

        final ImageButton tag_help_button=customView.findViewById(R.id.tag_help_button);

        final Button saveTag =customView.findViewById(R.id.save_tag_button);
        saveTag.setTypeface(currentTypeface);
        Button closeTag =customView.findViewById(R.id.close_tag_button);
        closeTag.setTypeface(currentTypeface);




        if(!isScrollEnd || restoreText==null) {
            titleEditText.setText(title);
            albumEditText.setText(album);
            artistEditText.setText(artist);
            composerEditText.setText(composer);
            yearEditText.setText(year);
            genreEditText.setText(genre);
        }else {
            titleEditText.setText(restoreText.getString("TITLE"));
            albumEditText.setText(restoreText.getString("ALBUM"));
            artistEditText.setText(restoreText.getString("ARTIST"));
            composerEditText.setText(restoreText.getString("COMPOSER"));
            yearEditText.setText(restoreText.getString("YEAR"));
            genreEditText.setText(restoreText.getString("GENRE"));

        }

        titleEditText.setFilters(new InputFilter[] { filter ,filterArray[1]});
        albumEditText.setFilters(new InputFilter[] { filter ,filterArray[2]});
        artistEditText.setFilters(new InputFilter[] { filter ,filterArray[3]});
        composerEditText.setFilters(new InputFilter[] { filter ,filterArray[4]});
        yearEditText.setFilters(new InputFilter[] { filter ,filterArray[5]});
        genreEditText.setFilters(new InputFilter[] { filter ,filterArray[6]});

        adapter.getFilter().filter(null);
        genreEditText.setDropDownBackgroundResource(R.drawable.popup_back_color);
        genreDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!genreEditText.isPopupShowing()) {
                    genreEditText.requestFocus();
                    genreEditText.showDropDown();
                }
                Log.d("DropDownClicked", "onClick: Invoked");
            }
        });

        titleEditText.setKeyImeChangeListener(new CustomEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                immG.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
                if ((Objects.requireNonNull(titleEditText.getText()).toString().trim().length() == 0)){
                    titleEditText.setText(title);
                }else{

                    titleEditText.setText(titleEditText.getText().toString().trim());
                }
                Log.d("EditTextClose", "onEditorAction: Invoked");
            }
        });

        albumEditText.setKeyImeChangeListener(new CustomEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                immG.hideSoftInputFromWindow(albumEditText.getWindowToken(), 0);
                if ((Objects.requireNonNull(albumEditText.getText()).toString().trim().length() == 0)){
                    albumEditText.setText(album);
                }else{

                    albumEditText.setText(albumEditText.getText().toString().trim());
                }
                Log.d("EditTextClose", "onEditorAction: Invoked");
            }
        });

        artistEditText.setKeyImeChangeListener(new CustomEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                immG.hideSoftInputFromWindow(artistEditText.getWindowToken(), 0);
                if ((Objects.requireNonNull(artistEditText.getText()).toString().trim().length() == 0)){
                    artistEditText.setText(artist);
                }else{

                    artistEditText.setText(artistEditText.getText().toString().trim());
                }
                Log.d("EditTextClose", "onEditorAction: Invoked");
            }
        });

        composerEditText.setKeyImeChangeListener(new CustomEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                immG.hideSoftInputFromWindow(composerEditText.getWindowToken(), 0);
                if ((Objects.requireNonNull(composerEditText.getText()).toString().trim().length() == 0)){
                    composerEditText.setText(composer);
                }else{

                    composerEditText.setText(composerEditText.getText().toString().trim());
                }
                Log.d("EditTextClose", "onEditorAction: Invoked");
            }
        });

        yearEditText.setKeyImeChangeListener(new CustomEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                immG.hideSoftInputFromWindow(yearEditText.getWindowToken(), 0);
                if ((Objects.requireNonNull(yearEditText.getText()).toString().trim().length() == 0)){
                    yearEditText.setText(year);
                }else{

                    yearEditText.setText(yearEditText.getText().toString().trim());
                }
                Log.d("EditTextClose", "onEditorAction: Invoked");
            }
        });

        titleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    albumEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        albumEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    artistEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        artistEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    composerEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        composerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    yearEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        yearEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    genreEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        saveTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SonaToast.setToast(ApplicationContextProvider.getContext(),"Processing... Please Wait!",1);
               Vibration.Companion.vibrate(50);
                new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
                    public void run() {

                String UriRaw=currentState.getString("SDCardUri",null);
                if(!TextUtils.isEmpty(UriRaw)) {
                    sdCardUri = Uri.parse(UriRaw);
                    Log.d("MediaListActivity", "onCreate: SdcardUri:" + UriRaw);
                }

                final  String newTitle= Objects.requireNonNull(titleEditText.getText()).toString();
                final   String newAlbum= Objects.requireNonNull(albumEditText.getText()).toString();
                final   String newArtist= Objects.requireNonNull(artistEditText.getText()).toString();
                final   String newComposer= Objects.requireNonNull(composerEditText.getText()).toString();
                final   String newYear= Objects.requireNonNull(yearEditText.getText()).toString();
                final   String newGenre=genreEditText.getText().toString();

                if( !isArtworkChanged && newTitle.equals(title) && newAlbum.equals(album)&& newArtist.equals(artist) && newComposer.equals(composer) && newYear.equals(year) && newGenre.equals(genre)){
                    dismiss();
                    return;
                }

                File editFileX=null;
                final Bundle newTagsBundle=new Bundle();
                newTagsBundle.putString("TITLE",newTitle);
                newTagsBundle.putString("ALBUM",newAlbum);
                newTagsBundle.putString("ARTIST",newArtist);
                newTagsBundle.putString("COMPOSER",newComposer);
                newTagsBundle.putString("YEAR",newYear);
                newTagsBundle.putString("GENRE",newGenre);
                newTagsBundle.putString("SONGPATH",songPath);
                Bitmap bitmapZ=null;
                if (artwork != null) {
                    try {
                        InputStream is = new ByteArrayInputStream(artwork.getBinaryData());
                        bitmapZ = BitmapFactory.decodeStream(is);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                newTagsBundle.putParcelable("IMAGE",bitmapZ);


                try {
                    File editFile=new File(songPath);
                    if(editFile.canWrite()) {
                        TagOptionSingleton.getInstance().setAndroid(true);
                        AudioFile audioFile = AudioFileIO.read(editFile);
                        AudioFileIO.delete(audioFile);
                         audioFile = AudioFileIO.read(editFile);
                        Tag newTag = audioFile.getTagOrCreateAndSetDefault();

                        if (!(TextUtils.isEmpty(newTitle))) {
                            try {
                                newTag.setField(TITLE, newTitle);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!(TextUtils.isEmpty(newAlbum))) {
                            try {
                            newTag.setField(ALBUM, newAlbum);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!(TextUtils.isEmpty(newArtist))) {
                            try{
                            newTag.setField(ARTIST, newArtist);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!(TextUtils.isEmpty(newComposer))) {
                            try{
                            newTag.setField(COMPOSER, newComposer);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!(TextUtils.isEmpty(newYear))) {
                            try{
                            newTag.setField(YEAR, newYear);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!(TextUtils.isEmpty(newGenre))) {
                            try{
                            newTag.setField(GENRE, newGenre);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if(artwork!=null){
                            try {
                                newTag.addField(artwork);
                                newTag.setField(artwork);
                                Log.d("ArtworkFinalExit1", "onClick: ");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        audioFile.commit();
                        SonaToast.setToast(contextG,"ID3 Tags Saved",0);
                        scanMedia(songPath);
                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
                            public void run() {
                                resultTab.onSaveTagResult(mediaIdG,newTagsBundle);
                                dismiss();
                            }
                        }, 1000);


                    }else {
                        Log.e("FileIsNotWritable", "onClick: ");
                        String cachedFilePath = copyToCache(new File(songPath), fileNewName, ext);
                         editFileX = new File(Objects.requireNonNull(cachedFilePath));

                            if (editFileX.canWrite()) {
                                TagOptionSingleton.getInstance().setAndroid(true);
                                AudioFile audioFile = AudioFileIO.read(editFileX);
                                AudioFileIO.delete(audioFile);
                                audioFile = AudioFileIO.read(editFileX);
                                Tag newTag = audioFile.getTagOrCreateAndSetDefault();

                                if (!(TextUtils.isEmpty(newTitle))) {
                                    try {
                                        newTag.setField(TITLE, newTitle);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (!(TextUtils.isEmpty(newAlbum))) {
                                    try {
                                        newTag.setField(ALBUM, newAlbum);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (!(TextUtils.isEmpty(newArtist))) {
                                    try{
                                        newTag.setField(ARTIST, newArtist);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (!(TextUtils.isEmpty(newComposer))) {
                                    try{
                                        newTag.setField(COMPOSER, newComposer);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (!(TextUtils.isEmpty(newYear))) {
                                    try{
                                        newTag.setField(YEAR, newYear);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (!(TextUtils.isEmpty(newGenre))) {
                                    try{
                                        newTag.setField(GENRE, newGenre);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(artwork!=null){
                                    try {
                                        newTag.addField(artwork);
                                        newTag.setField(artwork);
                                    }catch (Throwable e){

                                        e.printStackTrace();
                                    }
                                    Log.d("ArtworkFinalExit", "onClick: ");
                                }
                                audioFile.commit();

                                if(copyToExternal(editFileX,new File(songPath).getParent(),contextG)){

                                    SonaToast.setToast(contextG,"ID3 Tags Saved",0);

                                    final String fileAlbumTemp=editFileX.getAbsolutePath();
                                    new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
                                        public void run() {
                                            updateAlbumArtMediaStore(albumArtId,fileAlbumTemp);
                                            resultTab.onSaveTagResult(getSongIdFromMediaStore(songPath,contextG,false),newTagsBundle);
                                            dismiss();
                                        }
                                    }, 2000);

                                    Log.d("FileSuccessFullyCopied", "Success ");
                                }else {

                                    Log.e("FileCopingFailed", "Failed");
                                }
                            }

                           //editFileX.delete();
                    }

                }catch (Throwable e){

                    if(editFileX!=null) {
                        final boolean delete = editFileX.delete();
                        Log.d("CacheFileDelete", "run:Status "+delete);
                    }
                    SonaToast.setToast(contextG,"Unsupported Codec",1);
                    e.printStackTrace();
                }


                    }
                }, 100);
            }
        });


        if(isVideoFileG){

            saveTag.setEnabled(false);
            saveTag.setVisibility(View.INVISIBLE);
            titleEditText.setEnabled(false);
            albumEditText.setEnabled(false);
            artistEditText.setEnabled(false);
            composerEditText.setEnabled(false);
            yearEditText.setEnabled(false);
            genreEditText.setEnabled(false);
            albumArtView.setEnabled(false);
            chooseAlbumArt.setEnabled(false);
            clearAlbumArt.setEnabled(false);
            genreDropDown.setEnabled(false);
        }


        closeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        tag_help_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog));
                builder.setTitle("Note:");
                builder.setMessage("Sona Player Supports Only Audio Tagging.Video Tagging is not Supported.\n\nSupported Audio Formats are:\nMp3, M4a,M4p, Ogg, Flac, Wav, Aif, Dsf and Wma");
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });
        final ScrollView infoTagScrollview=customView.findViewById(R.id.info_tag_scrollview);

        if(isScrollEnd){

            infoTagScrollview.post(new Runnable() {
                @Override
                public void run() {
                    infoTagScrollview.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    private static Uri getAlbumUri(String mediaID) {

        try {

            if (mediaID != null && !mediaID.equals("")) {
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Log.d("AlbumArtUri", ContentUris.withAppendedId(sArtworkUri, Long.parseLong(mediaID)).toString());
                return ContentUris.withAppendedId(sArtworkUri, Long.parseLong(mediaID));
            } else
                return null;
        }catch (Throwable e){
            return null;
        }

    }

    private  void  inflateFileInfoItem(RelativeLayout tabViewParent,final boolean isVideoFile,Typeface currentTypeface,final Bundle bundleMeta,final String songPath,final String fileName){

        LayoutInflater inflater = (LayoutInflater) contextG.getSystemService(LAYOUT_INFLATER_SERVICE);
        RelativeLayout.LayoutParams layoutParams= new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        @SuppressLint("InflateParams") View customView = Objects.requireNonNull(inflater).inflate(R.layout.info_popup_window,null);
        tabViewParent.removeAllViews();
        tabViewParent.addView(customView,layoutParams);

        File file = new File(songPath);
        Date lastModDate = new Date(file.lastModified());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String datestr = dateFormat.format(lastModDate);

        if(isVideoFile){
            TextView fileResolution=customView.findViewById(R.id.file_resolution_textview);
            fileResolution.setTypeface(currentTypeface);
            TextView fileResolutionValue=customView.findViewById(R.id.file_resolution_value);
            fileResolutionValue.setTypeface(currentTypeface);
            fileResolutionValue.setText(bundleMeta.getString("RESOLUTION"));
            TextView fileFramerate=customView.findViewById(R.id.file_framerate_textview);
            fileFramerate.setTypeface(currentTypeface);
            TextView fileFramerateValue=customView.findViewById(R.id.file_framerate_value);
            fileFramerateValue.setTypeface(currentTypeface);
            fileFramerateValue.setText(bundleMeta.getString("FRAMERATE"));

        }else {
            LinearLayout resolutionParent = customView.findViewById(R.id.file_resolution_parent);
            LinearLayout framerateParent = customView.findViewById(R.id.file_framerate_parent);
            resolutionParent.setVisibility(View.GONE);
            framerateParent.setVisibility(View.GONE);
        }

        TextView filenameHeader=customView.findViewById(R.id.filename_textview);
        final CustomEditText filenameEditText=customView.findViewById(R.id.filename_edittext);
        filenameEditText.setText(fileNewName);
        TextView filepath=customView.findViewById(R.id.filepath_textview);
        filepath.setTypeface(currentTypeface);
        final TextView filepathvalue=customView.findViewById(R.id.filepath_value);
        filepathvalue.setTypeface(currentTypeface);
        filepathvalue.setText(songPath);
        filepathvalue.setMovementMethod(new ScrollingMovementMethod());
        ImageButton editFilename =customView.findViewById(R.id.filename_edit);
        final ImageButton saveFilename =customView.findViewById(R.id.filename_save);

        final TextView fileBitrate=customView.findViewById(R.id.file_bitrate_textview);
        fileBitrate.setTypeface(currentTypeface);
        TextView fileBitrateValue=customView.findViewById(R.id.file_bitrate_value);
        fileBitrateValue.setTypeface(currentTypeface);
        fileBitrateValue.setText(bundleMeta.getString("BITRATE"));

        TextView fileSampleRate=customView.findViewById(R.id.file_SampleRate_textview);
        fileSampleRate.setTypeface(currentTypeface);
        TextView fileSampleValue=customView.findViewById(R.id.file_SampleRate_value);
        fileSampleValue.setTypeface(currentTypeface);
        fileSampleValue.setText(bundleMeta.getString("SAMPLERATE"));

        TextView fileFormat=customView.findViewById(R.id.file_format_textview);
        fileFormat.setTypeface(currentTypeface);
        TextView fileFormatValue=customView.findViewById(R.id.file_format_value);
        fileFormatValue.setTypeface(currentTypeface);
        fileFormatValue.setText(bundleMeta.getString("FORMAT"));

        TextView fileChannel=customView.findViewById(R.id.file_channel_textview);
        fileChannel.setTypeface(currentTypeface);
        TextView fileChannelValue=customView.findViewById(R.id.file_channel_value);
        fileChannelValue.setTypeface(currentTypeface);
        fileChannelValue.setText(bundleMeta.getString("CHANNEL"));

        TextView fileSize=customView.findViewById(R.id.file_size_textview);
        fileSize.setTypeface(currentTypeface);
        TextView fileSizeValue=customView.findViewById(R.id.file_size_value);
        fileSizeValue.setTypeface(currentTypeface);
        fileSizeValue.setText(bundleMeta.getString("SIZE"));
        filenameHeader.setTypeface(currentTypeface);
        filenameEditText.setTypeface(currentTypeface);

        TextView fileDate=customView.findViewById(R.id.file_date_textview);
        fileDate.setTypeface(currentTypeface);
        TextView fileDateValue=customView.findViewById(R.id.file_date_value);
        fileDateValue.setTypeface(currentTypeface);
        fileDateValue.setText(datestr);

        TextView fileDuration=customView.findViewById(R.id.file_duration_textview);
        fileDuration.setTypeface(currentTypeface);
        TextView fileDurationValue=customView.findViewById(R.id.file_duration_value);
        fileDurationValue.setTypeface(currentTypeface);
        fileDurationValue.setText(timeConvert(bundleMeta.getInt("DURATION")));

        editFilename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibration.Companion.vibrate(10);

                filenameEditText.setSelectAllOnFocus(true);
                filenameEditText.setFocusableInTouchMode(true);
                filenameEditText.setFocusable(true);
                filenameEditText.setTextIsSelectable(true);
                filenameEditText.setLongClickable(true);
                filenameEditText.setClickable(true);
                filenameEditText.requestFocus();
                filenameEditText.setCursorVisible(true);
                immG.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                Log.d("EditTextClicked", "onClick:Invoked ");
            }
        });


        filenameEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveFilename.performClick();
                    return true;
                }
                return false;
            }
        });

        filenameEditText.setKeyImeChangeListener(new CustomEditText.KeyImeChange() {

            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {

                filenameEditText.setSelectAllOnFocus(false);
                filenameEditText.setFocusableInTouchMode(false);
                filenameEditText.clearFocus();
                filenameEditText.setFocusable(false);
                filenameEditText.setCursorVisible(false);
                immG.hideSoftInputFromWindow(filenameEditText.getWindowToken(), 0);

                if ((Objects.requireNonNull(filenameEditText.getText()).toString().trim().length() == 0)){
                    filenameEditText.setText(fileNewName);
                }else{

                    filenameEditText.setText(filenameEditText.getText().toString().trim());
                }
                Log.d("EditTextClose", "onEditorAction: Invoked");
            }

        });


        filenameEditText.setFilters(new InputFilter[] { filter,filterArray[0] });

        saveFilename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                immG.hideSoftInputFromWindow(filenameEditText.getWindowToken(), 0);
               Vibration.Companion.vibrate(10);

                String format =bundleMeta.getString("FORMAT");
                if(!TextUtils.isEmpty(format)){
                    format= Objects.requireNonNull(format).toLowerCase();
                }

                if((Objects.requireNonNull(filenameEditText.getText()).toString().trim().length() != 0 && !fileNewName.equals(filenameEditText.getText().toString())) ){
                    String fullName=filenameEditText.getText().toString().trim()+"."+format;
                    String fileNameX="/"+fileName;

                    File tempFile=new File(songPath);
                    File dir= new File(Objects.requireNonNull(tempFile.getParent()));
                    final  File addSong=new File(dir,"/"+fullName);

                    Log.d("ParentOfFile", "onClick: "+dir);
                    Log.d("PathBefore", "onClick: "+dir+fileNameX);
                    Log.d("PathAfter", "onClick: "+dir+fullName);

                    if(proceedToRename(dir,fileNameX,fullName,isVideoFile,addSong,tempFile)) {

                        SonaToast.setToast(contextG, "File Renamed", 0);

                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
                            public void run() {
                                final String newId = getSongIdFromMediaStore(addSong.getAbsolutePath(), contextG, isVideoFile);
                                Log.d("MediaIdRenamed", "onClick:path: " + addSong.getAbsolutePath());
                                resultTab.onSaveTagResult(newId,null);
                            }
                        }, 1000);

                        dismiss();
                        Log.d("FullName", "onClick: "+fullName);

                    }
                }

            }
        });

    }




    private static String copyToCache(@NonNull final File source, @NonNull final String targetName, @NonNull final String ext) {

            FileOutputStream outputStream;
        File file;
            try {
                File outputDir = contextG.getCacheDir();
               file= new File(outputDir+"/"+targetName+"."+ext);
               if(file.setWritable(true)){
                   Log.d("FileWriteAccess", "copyFile: Access Granted");
               }else {

                   Log.e("FileWriteFailed", "copyFile: Access Denied" );
               }
                file.deleteOnExit();
                outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[4096]; // MAGIC_NUMBER
                int bytesRead;
                FileInputStream inStream = new FileInputStream(source);
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);

                }
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
               return null;
            }

            String newPath=file.getAbsolutePath();

        Log.d("FileCreated", "copyFile: "+newPath);
        return  newPath;
    }


    private File saveBitmapToCache(Bitmap croppedImage){

        File f;
        try {

            File outputDir = contextG.getCacheDir();
            f = File.createTempFile("albumArtCache", ".jpg", outputDir);
            f.deleteOnExit();
//Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            croppedImage.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
//write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            Log.d("AlbumArtCached", "saveBitmapToCache: "+f.getAbsolutePath());
        }catch (Throwable e){
            e.printStackTrace();
            return null;
        }

        return f;
    }

    private DocumentFile getDocumentFileIfAllowedToWrite(Uri sdCardUri ,File file){
        DocumentFile documentFile=null;
        try {
            if( sdCardUri!=null)
                documentFile = DocumentFile.fromTreeUri(contextG, sdCardUri);
            String[] parts = (file.getPath()).split("/");

            for (int i = 3; i < parts.length; i++) {
                if (documentFile != null) {
                    documentFile = documentFile.findFile(parts[i]);
                }
            }

            if (documentFile == null) {

                resultTab.onSdCardUriResult();
                return null;

            } else {

               return documentFile;
            }
        }catch (Throwable e) {
            e.printStackTrace();
            resultTab.onSdCardUriResult();
            return null;
        }
    }
    private String mime(String URI) {
         String type=null;
        String extention = MimeTypeMap.getFileExtensionFromUrl(URI);
        if (extention != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
        }
        return type;
    }

    private boolean copyToExternal(File copy, String directory, Context con) {
         FileInputStream inStream = null;
         OutputStream outStream = null;
        DocumentFile copy1;
        DocumentFile dir= getDocumentFileIfAllowedToWrite(sdCardUri,new File(directory));
        String mime = mime(copy.toURI().toString());
        try {
            if(dir!=null) {
                boolean deleteMedia = deleteMedia(sdCardUri, new File(songPathG), mediaIdG);
                Log.d("DeleteStatus", "copyToExternal: Status"+deleteMedia);
                copy1 = dir.createFile(mime, copy.getName());
                inStream = new FileInputStream(copy);
                outStream = con.getContentResolver().openOutputStream(Objects.requireNonNull(copy1).getUri());
                byte[] buffer = new byte[16384];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    Objects.requireNonNull(outStream).write(buffer, 0, bytesRead);

                }

                Log.d("NewFileDoc", "copyToExternal: +"+copy1.getUri().getPath());
                addMedia(con, new File(songPathG));
                return true;
            }else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(inStream!=null)
                    inStream.close();
                if(outStream!=null) {
                    outStream.flush();
                    outStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private boolean deleteMedia(Uri sdCardUri ,File file, String mediaId){
        DocumentFile documentFile=null;
        try {
            if( sdCardUri!=null)
                documentFile = DocumentFile.fromTreeUri(contextG, sdCardUri);
            String[] parts = (file.getPath()).split("/");

            for (int i = 3; i < parts.length; i++) {
                if (documentFile != null) {
                    documentFile = documentFile.findFile(parts[i]);
                }
            }

            if (documentFile == null) {

                return false;

            } else {

                if (documentFile.delete()) {// if delete file succeed
                    Log.e("MediaSuccessfullyDelete", "deleteMedia: Invoked" );
                    long longMediaId = Long.parseLong(mediaId);

                    Uri mediaContentUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            longMediaId);
                    contextG.getContentResolver().delete(mediaContentUri, null, null);

                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                     contextG.getContentResolver().delete(ContentUris.withAppendedId(sArtworkUri, Long.parseLong(Objects.requireNonNull(bundleTagsG.getString("ALBUM_ID")))), null, null);

                }
            }
        }catch (Throwable e) {
            e.printStackTrace();
            //getSdCardUri();
            return false;
            // Log.e("Exception Raised", "deleteMedia: "+e);
        }
        return true;
    }

    private static void scanMedia(String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
     contextG.sendBroadcast(scanFileIntent);
    }


    private static void addMedia(Context c, File f) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(f));
        c.sendBroadcast(intent);

    }

    private static void removeMedia(Context c, File f,boolean isVideoFile) {
        ContentResolver resolver = c.getContentResolver();
        if (isVideoFile) {
            resolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.DATA + "=?", new String[]{f.getAbsolutePath()});
        } else {
            resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.DATA + "=?", new String[]{f.getAbsolutePath()});
        }
    }


    private boolean renameFile(Uri sdCardUri ,File file,String fullPathtoName){
        DocumentFile documentFile=null;
        Log.d("MainRenameBlock", "File: "+file.getAbsolutePath());
        Log.d("MainRenameBlock", "FullPathName: "+fullPathtoName);

        try {

            if( sdCardUri!=null) {
                documentFile = DocumentFile.fromTreeUri(contextG, sdCardUri);
                Log.d("SdCardIsNotNull", "SdcardUri: "+sdCardUri.getPath());
                Log.d("DocumentFileSet", "DocumentFile: "+ Objects.requireNonNull(documentFile).getUri().getPath());
            }

            String[] parts = (file.getPath()).split("/");

            for (String element: parts) {
                System.out.println("Splitting Document Parts: "+element);
            }

            for (int i = 3; i < parts.length; i++) {
                if (documentFile != null) {
                    documentFile = documentFile.findFile(parts[i]);
                    Log.d("ValueOfDocfileInLoop", "Loop: "+ Objects.requireNonNull(documentFile).getUri().getPath());
                }
            }
            if (documentFile == null) {
                Log.d("DocumentFileIsNull", "renameFile: Returning ");
                resultTab.onSdCardUriResult();
                return false;

            } else {
                Log.d("DocumentFileIsNotNull", "DocumentFile: "+documentFile.getUri().getPath());

                if (documentFile.renameTo(fullPathtoName)) {// if delete file succeed

                    Log.d("RenameSucceed", "renameFile:Invoked ");
                }
            }
        }catch (Throwable e) {
            e.printStackTrace();
            resultTab.onSdCardUriResult();
            return false;
        }
        return true;
    }

    private static String getSongIdFromMediaStore(String songPath, Context context, boolean misVideoFile) {
        String id = "";
        ContentResolver cr = context.getContentResolver();
        if(!misVideoFile) {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.DATA;
            String[] selectionArgs = {songPath};
            String[] projection = {MediaStore.Audio.Media._ID};
            String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

            Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, sortOrder);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    id = cursor.getString(idIndex);

                }
            }
            Objects.requireNonNull(cursor).close();
        }else {

            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Video.Media.DATA;
            String[] selectionArgs = {songPath};
            String[] projection = {MediaStore.Video.Media._ID};
            String sortOrder = MediaStore.Video.Media.TITLE + " ASC";

            Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, sortOrder);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int idIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                    id = cursor.getString(idIndex);

                }
            }
            Objects.requireNonNull(cursor).close();
        }
        Log.d("IdRetrieved", "getSongIdFromMediaStore: "+id);
        return id;
    }

    private boolean proceedToRename(File dir,String fileNameX,String fullName,final boolean isVideoFile,final File addSong,File tempFile) {

        try {
            File from = new File(dir, fileNameX);
            File to = new File(dir, fullName);

            if (from.exists()) {
                Log.e("EnterdInExist", "onClick: ");
                if (from.renameTo(to)) {
                    Log.e("EnterdInRenamed", "onClick: ");
                    removeMedia(contextG, from, isVideoFile);
                    addMedia(contextG, addSong);
                    return true;
                } else {

                    if (renameFile(sdCardUri, tempFile, fullName)) {
                        removeMedia(contextG, from, isVideoFile);
                        addMedia(contextG, addSong);
                        return true;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return false;
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

    private static String getRealPathFromURI(Uri contentURI) {
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
    }

    private void updateAlbumArtMediaStore(String album_id,String file_url){

        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
      contextG.getContentResolver().delete(ContentUris.withAppendedId(sArtworkUri,Long.parseLong(album_id)), null, null);

        ContentValues values = new ContentValues();
        values.put("album_id", Long.parseLong(album_id));
        values.put("_data", file_url);
        Uri num_updates = contextG.getContentResolver().insert(sArtworkUri, values);


                File f = new File(file_url);
        final boolean delete = f.delete();
        Log.d("AlbumArtInserted", "updateAlbumArtMediaStore:NewUri: "+num_updates+delete);
    }

}
