package com.McDevelopers.sonaplayer;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.palette.graphics.Palette;

import com.McDevelopers.sonaplayer.AlbumListActivity.AlbumLibrary;
import com.McDevelopers.sonaplayer.ArtistListActivity.ArtistLibrary;
import com.McDevelopers.sonaplayer.FolderListActivity.FolderLibrary;
import com.McDevelopers.sonaplayer.QueueListActivity.QueueItems;
import com.McDevelopers.sonaplayer.RecentlyAddedActivity.RecentlyLibrary;
import com.McDevelopers.sonaplayer.videolist.VideoData;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.images.Artwork;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public class MusicLibrary{

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static final HashMap<String, String> albumRes = new HashMap<>();
   private static final HashMap<String, String> musicFileName = new HashMap<>();
      static final HashMap<String, String> videoFileName = new HashMap<>();
   private static LinkedHashSet<String> folderName = new LinkedHashSet<>();
    private static LinkedHashSet<String> folderNameVideo = new LinkedHashSet<>();
    private static postMetaUpdateListener updateListener;
    private static albumUpdateListener albumsUpdateListener;
    private static artistUpdateListener artistUpdater;
    private static folderUpdateListener folderUpdater;
    private static postMetaUIUpdateListener uIUpdateListener;

   public static List<Data> datax = new ArrayList<>();
    public static List<VideoData> datav = new ArrayList<>();
    public   static ArrayList<String> listOfAllImages = new ArrayList<>();
    public static  List<QueueItems> queueItems = new ArrayList<>();
    public static List<MediaSessionCompat.QueueItem> queueData = new ArrayList<>();
    static List<MediaSessionCompat.QueueItem> masterPlaylist = new ArrayList<>();
    public static List<String> queueArray=new ArrayList<>();

    private static int majorColorX= -15263969;
    private static int minorColorX= -1;
    private static int middleColorX= -1;

    private static String videoFolder;
  private static String albumFolder;
  private  static  boolean isCustomArt=false;
    public static  int currentArtIndex;

   private static boolean isVideo=false;
    public static boolean isAlbumUpdated=false;
    public static boolean isArtistUpdated=false;
    public static boolean isFolderUpdated=false;

    private static long audioReject=60000;
    private static long videoReject=120000;

    private static  MediaExtractor mediaExtractorG;
   private static MediaFormat mediaFormatG;

    private  static Tag tagG;
    private static AudioHeader audioHeaderG;
   private static MediaMetadataRetriever metadataRetrieverG;
   static private MetaDataComputationTask taskMeta;



    interface postMetaUpdateListener{

        void metaUpdateEvent(MediaMetadataCompat mediaMetadataCompat);
    }
   public interface albumUpdateListener{

        void albumUpdateEvent();
    }
    public interface artistUpdateListener{

        void artistUpdateEvent();
    }
    public interface folderUpdateListener{

        void folderUpdateEvent();
    }
    public static void registerFolderUpdateListener(folderUpdateListener foldersUpdateListener){

        folderUpdater=foldersUpdateListener;
    }

    public static void registerArtistUpdateListener(artistUpdateListener artistsUpdateListener){

        artistUpdater=artistsUpdateListener;
    }
    public static void registerAlbumUpdateListener(albumUpdateListener albmUpdateListener){

        albumsUpdateListener=albmUpdateListener;
    }

    interface postMetaUIUpdateListener{

        void metaUIUpdateEvent(MediaMetadataCompat mediaMetadataCompat);
    }

     static void registerMetaUpdateListener(postMetaUpdateListener metaUpdateListener){

        updateListener=metaUpdateListener;
    }

    static void registerMetaUIUpdateListener(postMetaUIUpdateListener metaUpdateListener){

        uIUpdateListener=metaUpdateListener;
    }

     static String getRoot() {
        return "SonaPlayer";
    }


   private static void setDecoders( String path,boolean isVideoFile){

       try {
           mediaExtractorG = new MediaExtractor();
           mediaExtractorG.setDataSource(path);

       }catch (Throwable e){

           mediaExtractorG.release();
           mediaExtractorG=null;
           mediaFormatG=null;
           e.printStackTrace();
       }

       AudioFile audioFileG;
       try{

           if(!isVideoFile) {
               TagOptionSingleton.getInstance().setAndroid(true);
               audioFileG = AudioFileIO.read(new File(path));
               tagG = audioFileG.getTag();
               audioHeaderG = audioFileG.getAudioHeader();
           }else {

               audioHeaderG=null;
               tagG=null;
           }

       }catch (Throwable e){
           audioHeaderG=null;
           tagG=null;
         e.printStackTrace();
       }

       try{

          metadataRetrieverG = new MediaMetadataRetriever();
           metadataRetrieverG.setDataSource(path);
       }catch (Throwable e){
           metadataRetrieverG.release();
           metadataRetrieverG=null;
           e.printStackTrace();
       }


       Log.d("DecoderIsSet", "setDecoders:For "+path);
   }

    private static Bitmap getAlbumBitmap( String mediaId,String songPath) {

        Log.e("NowFatchingAlbumArt", "getAlbumBitmap: Invoked" );
        Bitmap artwork=null;
        ContentResolver contentResolver=ApplicationContextProvider.getContext().getContentResolver();
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize = 1;

        if(isCustomArt && listOfAllImages.size()!=0) {

            try {
                artwork=loadScaledBitmap();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return artwork;
        }else {

            if (isVideoFileX(songPath)) {

                try {
                    artwork = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, Integer.valueOf(mediaId), MediaStore.Video.Thumbnails.MINI_KIND, options);
                }catch (Throwable e){

                    e.printStackTrace();
                }

                } else {

                try {

                    Artwork jArtwork= tagG.getFirstArtwork();
                    InputStream is = new ByteArrayInputStream(jArtwork.getBinaryData());
                    artwork = BitmapFactory.decodeStream(is);

                    Log.d("albumMetaJaudioTagger", "getAlbumBitmap: SetByJaudioTagger ");
                }catch (Throwable e) {
                    Log.e("FailedJaudioTagger", "getAlbumBitmap: FailedToSetByJaudioTagger ");
                    try {
                        byte[] art = metadataRetrieverG.getEmbeddedPicture();
                        artwork = BitmapFactory.decodeByteArray(art, 0, art.length);

                    } catch (Exception e1) {
                        Log.e("MetaDataRetrieverFailed", "getAlbumBitmap: Failed " );
                    }

                }

                if(artwork==null) {
                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(albumRes.get(mediaId)));
                    ContentResolver res = ApplicationContextProvider.getContext().getContentResolver();
                    try {
                        InputStream in = res.openInputStream(uri);
                        artwork = BitmapFactory.decodeStream(in);

                    } catch (Throwable e) {

                        artwork = null;
                        // artwork = BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(), R.drawable.sona_dvd_w);

                    }
                }
            }
            return artwork;
        }
    }

     static String getVideoCount(){


        return String.valueOf(videoFileName.size());
    }

     static String getTrackCount(){


        return String.valueOf(music.size());
    }
     static String getAudioCount(){


        return String.valueOf(music.size()-videoFileName.size());
    }

    private static String getSampleRate(){
        float samplerate;

        try{

            if(isVideo){
                mediaFormatG = mediaExtractorG.getTrackFormat(1);

            }else {
                mediaFormatG = mediaExtractorG.getTrackFormat(0);

            }

            samplerate = (float) mediaFormatG.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            samplerate = (samplerate / 1000);
            Log.d("SampleRate",String.valueOf(samplerate));


        }catch (Throwable e){

            samplerate=48;
            e.printStackTrace();
        }

        return samplerate+ "KHz";
    }

    private static String getStringSizeLengthFile(String data) {


        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String getBitRate(boolean isVideoFile) {

        int Bitrate=0;

        if(!isVideoFile) {
            try {

                Bitrate = (int)audioHeaderG.getBitRateAsNumber();

                Log.d("JAudioTaggerBitrateMeta", "getBitRate: SetByJAudioTagger");
            } catch (Throwable e) {

                e.printStackTrace();
            }
        }

        if(isVideoFile || Bitrate==0) {

            try {
                Bitrate = Integer.parseInt(metadataRetrieverG.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
                Bitrate = Bitrate / 1000;
                Log.d("Bitrate:", String.valueOf(Bitrate));

            } catch (Throwable e) {
                Bitrate = 128;
                Log.d("BitRate Exception: " + e, String.valueOf(Bitrate));
            }
        }

        return Bitrate + "Kbps";

    }

    private static String getGenre() {

       String genre;
       try{
           if(!isVideo) {
               genre = tagG.getFirst(FieldKey.GENRE);
               if(genre.isEmpty())
                   genre=null;
           }else {

               genre=metadataRetrieverG.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
           }

       }catch (Throwable e){
           genre=metadataRetrieverG.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
       }


        return genre;

    }

    private static String getComposer() {
        String composer;
        try{
            if(!isVideo) {
                composer = tagG.getFirst(FieldKey.COMPOSER);
                if(composer.isEmpty())
                    composer=null;

            }else {

                composer=metadataRetrieverG.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
            }

        }catch (Throwable e){
            composer=metadataRetrieverG.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
        }

        return composer;

    }

    private static String getYear() {
        String year;
        try{
            if(!isVideo) {
                year = tagG.getFirst(FieldKey.YEAR);
                if(year.isEmpty())
                    year=null;
            }else {

                year=metadataRetrieverG.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
            }

        }catch (Throwable e){
            year=metadataRetrieverG.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
        }

        return year;

    }

    private static String getFramerate(){

            try {

                mediaFormatG = mediaExtractorG.getTrackFormat(0);
                int framerate = mediaFormatG.getInteger(MediaFormat.KEY_FRAME_RATE);
                Log.d("VideoMetadata", "FrameRate: "+framerate);
                return framerate+"FPS";

            }catch (Throwable e){
                Log.e("ExceptionRaised", "getFramerate: "+e );
                return "24FPS";
                }


    }

    private static String getResolution(boolean iswidth){

        try {
            Bitmap frame = metadataRetrieverG.getFrameAtTime();
            int width = frame.getWidth();
            int height = frame.getHeight();
            Log.d("VideoMetadata", "Resolution: "+width+"x"+height);

            if(iswidth){

                return String.valueOf(width);
            }else {


                return String.valueOf(height);
            }

        }catch (Throwable e){
            Log.e("ExceptionRaised", "getFramerate: "+e );
            return "NA";
        }


    }

    private static String getSongFormat(String data){

        return  ((data).substring((data.lastIndexOf(".") + 1)).toUpperCase());
    }

    private static String getChannel(){
        int channel=0;
        String channel_text="Stereo";

        try {

            if(isVideo){
                mediaFormatG = mediaExtractorG.getTrackFormat(1);

            }else {
                mediaFormatG = mediaExtractorG.getTrackFormat(0);

            }
            channel=mediaFormatG.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

            if(channel<2)
                channel_text="Mono";
            else
                channel_text="Stereo";

        }catch (Throwable e){

            Log.d("Channel Exception: "+e,String.valueOf(channel));
        }

            return channel_text;
    }




     static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));

            Log.d("Looping On", "getMediaItems: Invoked");
            break;
        }

        return result;
    }

     static MediaMetadataCompat getMetadata(String mediaId) {

         try {
             isVideo=isVideoFileX(musicFileName.get(mediaId));

             Log.d("VideoFileTest", "IsVideoFile:  "+isVideo);
             Log.e("SizeOfMusic", "getMetadata: "+music.size());

             MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);
             // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
             // We don't set it initially on all items so that they don't take unnecessary memory.
             MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
             builder.putString("VIDEO", String.valueOf(isVideo));
             for (String key :
                     new String[]{
                             MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                             MediaMetadataCompat.METADATA_KEY_ALBUM,
                             MediaMetadataCompat.METADATA_KEY_ARTIST,
                             MediaMetadataCompat.METADATA_KEY_TITLE,
                             MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
                             MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                             MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                             MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
                     }) {
                 if (metadataWithoutBitmap != null) {
                     builder.putString(key, metadataWithoutBitmap.getString(key));
                 }
             }
             builder.putLong(
                     MediaMetadataCompat.METADATA_KEY_DURATION,
                     Objects.requireNonNull(metadataWithoutBitmap).getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
             builder.putString("DATA", musicFileName.get(mediaId));
             builder.putString("VIDEOCOUNT", getVideoCount());
             builder.putString("AUDIOCOUNT", getAudioCount());
             builder.putString("TRACKCOUNT", getTrackCount());
             builder.putString("SONGPATH", musicFileName.get(mediaId));
             builder.putString("FILENAME", (Objects.requireNonNull(musicFileName.get(mediaId))).substring((Objects.requireNonNull(musicFileName.get(mediaId))).lastIndexOf("/") + 1));

                 builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, null);
                 builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, null);

                 builder.putString("SAMPLERATE", "____");
                 builder.putString("BITRATE", "____");
                 builder.putString("SONGFORMAT","____");
                 builder.putString("CHANNEL", "____");
                 builder.putString(MediaMetadataCompat.METADATA_KEY_GENRE, "__________");
                 builder.putString(MediaMetadataCompat.METADATA_KEY_COMPOSER, "__________");
                 builder.putString("YEAR","__________");
                 builder.putString("SIZE","____");
                 if (isVideo){
                     builder.putString("FRAMERATE","____");
                     builder.putString("WIDTH", "____");
                     builder.putString("HEIGHT", "____");
                 }
                 if(taskMeta!=null) {
                     taskMeta.cancel(true);
                     taskMeta = null;
                 }
              taskMeta= new MetaDataComputationTask(mediaId);
             taskMeta.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
             Log.d("CurrentMetaCheck", "getMetadata: AlbumArtID:"+metadataWithoutBitmap.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
             return builder.build();
         } catch (Exception e) {
             e.printStackTrace();
         }
         return null;
     }

    private static MediaMetadataCompat getMetadata2(String mediaId) {

            isVideo=isVideoFileX(musicFileName.get(mediaId));

            setDecoders(musicFileName.get(mediaId),isVideo);
            Log.d("VideoFileTest", "IsVideoFile:  "+isVideo);

            Log.e("SizeOfMusic", "getMetadata: "+music.size());

            MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);
            Bitmap albumArt = getAlbumBitmap( mediaId,musicFileName.get(mediaId));
              int[] colorArray=getpaletteColor(albumArt);

            // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
            // We don't set it initially on all items so that they don't take unnecessary memory.
            MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
             builder.putString("VIDEO",String.valueOf(isVideo));
            for (String key :
                    new String[]{
                            MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                            MediaMetadataCompat.METADATA_KEY_ALBUM,
                            MediaMetadataCompat.METADATA_KEY_ARTIST,
                            MediaMetadataCompat.METADATA_KEY_TITLE,
                            MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
                            MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                            MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                            MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
                    }) {
                if (metadataWithoutBitmap != null) {
                    builder.putString(key, metadataWithoutBitmap.getString(key));
                }
            }
        builder.putLong("CurrentArt",(long)currentArtIndex);
        Log.d("CurrentArtIndex", "getMetadata2: Packed"+currentArtIndex);

        builder.putString("SONGPATH",musicFileName.get(mediaId));
        builder.putString("DATA",musicFileName.get(mediaId));
        builder.putString("VIDEOCOUNT",getVideoCount());
        builder.putString("AUDIOCOUNT",getAudioCount());
        builder.putString("TRACKCOUNT",getTrackCount());
        builder.putString("MajorColor",String.valueOf(colorArray[0]));
        builder.putString("MinorColor",String.valueOf(colorArray[1]));
        builder.putString("MiddleColor",String.valueOf(colorArray[2]));
        try {
            builder.putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    Objects.requireNonNull(metadataWithoutBitmap).getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, albumArt);
            builder.putString("SAMPLERATE",getSampleRate());
            builder.putString("BITRATE",getBitRate(isVideoFileX(musicFileName.get(mediaId))));
            builder.putString("SONGFORMAT",getSongFormat(Objects.requireNonNull(musicFileName.get(mediaId))));
            builder.putString("CHANNEL",getChannel());
            builder.putString( MediaMetadataCompat.METADATA_KEY_GENRE,getGenre());
            builder.putString( MediaMetadataCompat.METADATA_KEY_COMPOSER,getComposer());
            builder.putString( "YEAR",getYear());
            builder.putString( "SIZE",getStringSizeLengthFile(musicFileName.get(mediaId)));
            builder.putString("FILENAME",(Objects.requireNonNull(musicFileName.get(mediaId))).substring((Objects.requireNonNull(musicFileName.get(mediaId))).lastIndexOf("/")+1));

            if(isVideo){
                builder.putString("FRAMERATE",getFramerate());
                builder.putString("WIDTH",getResolution(true));
                builder.putString("HEIGHT",getResolution(false));
            }

            Log.d("CurrentMetaCheck", "getMetadata: AlbumArtID:"+metadataWithoutBitmap.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder.build();
    }

    private static Bitmap loadScaledBitmap(){

        Uri imageUri;
        imageUri = Uri.fromFile(new File(Objects.requireNonNull(randomArt())));

        ContentResolver resolver =ApplicationContextProvider.getContext().getContentResolver();
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
            Objects.requireNonNull(is).close();
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

    private static void createMediaMetadataCompat(
            String mediaId,
            String title,
            String artist,
            String album,
            long duration,
            TimeUnit durationUnit,
            String musicFilename,
            String albumArtResId,
            String data,
             boolean isVideo)
    {

        long durationX= TimeUnit.MILLISECONDS.convert(duration, durationUnit);

        music.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, musicFilename)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, durationX)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, albumArtResId)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .putString("FILENAME", musicFilename)
                        .build());


        albumRes.put(mediaId,albumArtResId);
        musicFileName.put(mediaId, data);
        datax.add(new Data(title, album+ " - " + artist, albumArtResId, mediaId, data, musicFilename, durationX,getStringSizeLengthFile(data)));

        if(isVideo) {
            videoFileName.put(mediaId, data);
            datav.add(new VideoData(title, data,mediaId,album, durationX,getStringSizeLengthFile(data), musicFilename));
        }
    }

    private static boolean isVideoFileX(String path) {
        try {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("video");

        }catch (Throwable e){
           // Log.e("ExceptionInIsVideo", "isVideoFile: "+e );
            return false;
        }
    }


    static List<MediaSessionCompat.QueueItem> refreshMetadata(){

            isAlbumUpdated=false;
            isArtistUpdated=false;
            isFolderUpdated=false;
            SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
            videoFolder = currentState.getString("videoFolder", null);
            audioReject=currentState.getLong("audioReject",60000);
            videoReject=currentState.getLong("videoReject",120000);
            Log.d("audioReject", "static initializer: MusicLibrary: audioRejectVal="+audioReject );
            Log.d("videoReject", "static initializer: MusicLibrary: videoRejectVal="+videoReject );

            music.clear();
            albumRes.clear();
            videoFileName.clear();
            musicFileName.clear();
            masterPlaylist.clear();
            datax.clear();
            datav.clear();
            folderName.clear();
            folderNameVideo.clear();
            refreshLibrary();
            refresAlbums();

            List<MediaSessionCompat.QueueItem> queue = new ArrayList<>();
            for (MediaMetadataCompat metadata : music.values()) {
                MediaMetadataCompat trackCopy = new MediaMetadataCompat.Builder(metadata).build();

                MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(trackCopy.getDescription(), trackCopy.getDescription().hashCode());
                queue.add(item);
            }
             masterPlaylist=queue;
            Collections.sort(datax, new Comparator<Data>() {
                @Override
                public int compare(Data o1, Data o2) {
                    return o1.title.toLowerCase().compareTo(o2.title.toLowerCase());
                }
            });

           Collections.reverse(datav);

        getQueue();
        RecentlyLibrary recentlyLibrary=new RecentlyLibrary();
        recentlyLibrary.displayRecentSongs();

        AlbumLibrary album=new AlbumLibrary();
        album.setDummydata();

        ArtistLibrary artistLibrary=new ArtistLibrary();
        artistLibrary.setDummydata();

        FolderLibrary folderLibrary=new FolderLibrary();
        folderLibrary.setDummydata();

        AlbumComputationTask taskAlbum= new AlbumComputationTask();
        taskAlbum.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        ArtistComputationTask taskArtist= new ArtistComputationTask();
        taskArtist.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        FolderComputationTask taskFolder= new FolderComputationTask();
        taskFolder.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            Log.d("SizeOfVideoArray", "refreshMetadata:sizeIS: " + datav.size());

            return queue;
    }

    public static void buildQueue() {
        queueData.clear();
        Log.d("SizeOfMasterPlaylist", "buildQueue:Size- " + masterPlaylist.size());
        for (QueueItems tempQueus : queueItems) {
            for (MediaSessionCompat.QueueItem tempItems : masterPlaylist) {
                if (Objects.equals(tempItems.getDescription().getMediaId(),tempQueus.mediaId)) {
                    queueData.add(tempItems);
                    break;
                }
            }
            queueArray.add(tempQueus.mediaId);
        }
        saveQueue();
        Log.d("SizeOfNewQueueList", "buildQueue:Size: " + queueData.size());

    }

    public static void saveQueue(){
        Set<String> set = new HashSet<>(queueArray);
        SharedPreferences  currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = currentState.edit();
        editor.remove("QueueData");
        editor.commit();
        editor = currentState.edit();
        editor.putStringSet("QueueData", set);
        editor.apply();
        Log.d("SizeOfSavedQueue", "buildQueue: "+queueArray.size());


    }

    public static void getQueue(){
        queueArray.clear();
        SharedPreferences  currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        Set<String> setx=currentState.getStringSet("QueueData",null);
         if(setx!=null) {
             queueArray.addAll(setx);
             Log.d("GetQueueStartup", "getQueue: Fetching...");
             for (String mid:setx) {

                 Log.d("getQueue: ", Objects.requireNonNull(music.get(mid)).getString(MediaMetadataCompat.METADATA_KEY_TITLE));
             }
             prepareQueue();
         }

        Log.d("QueueItemsRetrieved", "getQueue: "+queueArray.size());
    }

    private static void prepareQueue(){
        queueItems.clear();
        for(String queueZ:queueArray){
            for (Data tempData:datax){
                if(queueZ.equals(tempData.mediaId)){
                    QueueItems queueItm=new QueueItems(tempData.title,tempData.duration,tempData.size,tempData.data,tempData.mediaId,tempData.fileName);
                    queueItems.add(queueItm);
                    Log.d("AddToQueue", "prepareQueue: "+tempData.title);
                }
            }
        }
        buildQueue();
        Log.d("QueueListGenerated", "prepareQueue: "+queueItems.size());
    }

    private static void refreshLibrary(){
        try {
            ContentResolver contentResolver = ApplicationContextProvider.getContext().getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";
            String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
            Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

            if (cursor != null && cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    @SuppressLint("InlinedApi") long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                    TimeUnit durationUnit = TimeUnit.SECONDS;
                    String filename = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String albumArtId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    String tempFolder = data;
                    tempFolder = tempFolder.replace(filename,"");

                    if (!(filename.endsWith(".amr") || filename.endsWith(".AMR"))) {

                        if (duration >= audioReject) {
                            createMediaMetadataCompat(id, title, artist, album, duration, durationUnit, filename, albumArtId, data, false);
                            folderName.add(tempFolder);
                        }
                    }

                }

                cursor.close();

            }

            Cursor cursorx;
            Uri urix = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String sortOrderv = MediaStore.Video.Media.DATE_MODIFIED + " ASC";
            String selectionV = MediaStore.Video.Media.DATA + " like ? ";
            String folderV = videoFolder+"%";
            String[] whereArgV = new String[]{folderV};
            if(videoFolder==null) {
                cursorx = contentResolver.query(urix, null, null, null, sortOrderv);
            }else {
                cursorx = contentResolver.query(urix, null, selectionV, whereArgV, sortOrderv);
            }
            File f ;
            if (cursorx != null) {
                while (cursorx.moveToNext()) {

                    String ids = cursorx.getString(cursorx.getColumnIndex(MediaStore.Video.Media._ID));
                    String datas =  cursorx.getString(cursorx.getColumnIndex(MediaStore.Video.Media.DATA));
                    String titles = cursorx.getString(cursorx.getColumnIndex(MediaStore.Video.Media.TITLE));
                    @SuppressLint("InlinedApi") long durations = cursorx.getLong(cursorx.getColumnIndex(MediaStore.Video.Media.DURATION));
                    String filenames = cursorx.getString(cursorx.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    f=new File(datas);
                    String album= Objects.requireNonNull(f.getParentFile()).getName();

                    String tempFolderX = datas;
                    tempFolderX = tempFolderX.replace(filenames,"");


                    if(durations>=videoReject) {
                        createMediaMetadataCompat(ids, filenames, "Unknown Artist", album, durations, TimeUnit.SECONDS, titles, ids, datas, true);
                            folderNameVideo.add(tempFolderX);
                    }
                    //  Log.d("VideoID", ids);
                }
                cursorx.close();
            }


        }catch (Throwable e){

            Toast.makeText(ApplicationContextProvider.getContext(),"Exception raised in Query:"+e,Toast.LENGTH_LONG).show();
        }


    }


     @SuppressLint("ApplySharedPref")
     static int refresAlbums(){

        Log.d("RefreshAlbum", "refresAlbums:Invoked ");
        SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        albumFolder=currentState.getString("albumFolder", null);
        isCustomArt=currentState.getBoolean("customAlbum", false);

        ContentResolver contentResolver = ApplicationContextProvider.getContext().getContentResolver();
        Log.d("RefreshAlbum", "refresAlbums:CustumArt: "+isCustomArt);

        try{

            listOfAllImages.clear();
            MainActivity.albumArtList.clear();

            if(isCustomArt) {

                Log.d("RefreshAlbum", "refresAlbums:Started ");


                Uri uriZ;
                Cursor cursorZ;
                int column_index_data;

                String absolutePathOfImage;
                uriZ = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String selectionI = MediaStore.Images.Media.DATA + " like ? ";
                String folder = albumFolder+"%";
                String[] whereArgs = new String[]{folder};
                cursorZ = contentResolver.query(uriZ, null, selectionI,
                        whereArgs, null);

                if (cursorZ != null) {
                    column_index_data = cursorZ.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

                    while (cursorZ.moveToNext()) {
                        absolutePathOfImage = cursorZ.getString(column_index_data);
                        listOfAllImages.add(absolutePathOfImage);

                    }

                    Log.d("RefreshAlbum", "refresAlbums:Finished ");


                    if (listOfAllImages.isEmpty()) {

                        Log.d("RefreshAlbumEmpty", "refresAlbums:Invoked ");


                        currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor;
                        editor = currentState.edit();
                        editor.putString("albumFolder", null);
                        editor.putBoolean("customAlbum", false);
                        editor.putBoolean("albumAutoHide", true);
                        editor.commit();
                    } else {

                        Log.d("RefreshAlbumSuccess", "refresAlbums:Invoked ");


                        currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor;
                        editor = currentState.edit();
                        editor.putBoolean("customAlbum", true);
                        editor.putBoolean("albumAutoHide", false);
                        editor.commit();
                    }

                    cursorZ.close();
                }
            }

            MainActivity.albumArtList=listOfAllImages;

            return listOfAllImages.size();

        }catch (Throwable e){

            Log.e("ExceptionRaiseed", "refresAlbums: "+e );
            return listOfAllImages.size();
        }
    }

     static void clearAlbumList(){

        listOfAllImages.clear();

        Log.d("AlbumArtClear", "clearAlbumList:Invoked ");
    }

    private static String randomArt() {

        int max = MusicLibrary.listOfAllImages.size();
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
                String newAlbum = MusicLibrary.listOfAllImages.get(newAlbumIndex);
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




    private static class AlbumComputationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // Runs on UI thread
            isAlbumUpdated=false;
            Log.d("AlbumPreExecute", "About to start...");

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                AlbumLibrary album=new AlbumLibrary();
                album.getAlbumsLists();
                Log.d("AlbumExecuteInvoked", "About to start...");

            } catch (Throwable e) {

                Log.e("Exception in AsyncTask", "doInBackground: AsyncTask Sleep Error:" + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            // Runs on the UI thread
            isAlbumUpdated=true;
            if(albumsUpdateListener!=null)
                albumsUpdateListener.albumUpdateEvent();
            Log.d("AlbumPostExecute", "Big computation finished");
        }

    }


    private static class ArtistComputationTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            // Runs on UI thread
            isArtistUpdated=false;
            Log.d("ArtistPreExecute", "About to start...");

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                ArtistLibrary artistLibrary=new ArtistLibrary();
                artistLibrary.getAlbumsLists();

                Log.d("ArtistExecuteInvoked", "About to start...");

            } catch (Throwable e) {

                Log.e("Exception in AsyncTask", "doInBackground: AsyncTask Sleep Error:" + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            // Runs on the UI thread
            isArtistUpdated=true;
            if(artistUpdater!=null)
                artistUpdater.artistUpdateEvent();
            Log.d("ArtistPostExecute", "Big computation finished");
        }

    }

    private static class FolderComputationTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            // Runs on UI thread
            isFolderUpdated=false;
            Log.d("AlbumPreExecute", "About to start...");

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                FolderLibrary folderLibrary=new FolderLibrary();
                folderLibrary.FolderSongLibrary(folderName);
                folderLibrary.FolderVideoLibrary(folderNameVideo);

                Log.d("FolderExecuteInvoked", "About to start...");

            } catch (Throwable e) {

                Log.e("Exception in AsyncTask", "doInBackground: AsyncTask Sleep Error:" + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            // Runs on the UI thread
            isFolderUpdated=true;
            if(folderUpdater!=null)
                folderUpdater.folderUpdateEvent();
            Log.d("FolderPostExecute", "Big computation finished");
        }

    }


    private static class MetaDataComputationTask extends AsyncTask<Void, Void, Void> {

        String currentMediaId;
        MediaMetadataCompat mediaMetadataCompat;
        MetaDataComputationTask(String mediaId){
            super();
            currentMediaId=mediaId;
        }

        @Override
        protected void onPreExecute() {
            // Runs on UI thread
            mediaMetadataCompat=null;
            Log.d("MetaDataPreExecute", "About to start...");

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                if(!isCancelled())
                mediaMetadataCompat=getMetadata2(currentMediaId);

                Log.d("MetaDataExecuteInvoked", "About to start...");

            } catch (Throwable e) {

                Log.e("Exception in AsyncTask", "doInBackground: AsyncTask Sleep Error:" + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            // Runs on the UI thread
            if(!isCancelled()) {
                try {
                    if(updateListener!=null)
                    updateListener.metaUpdateEvent(mediaMetadataCompat);
                    if(uIUpdateListener!=null)
                    uIUpdateListener.metaUIUpdateEvent(mediaMetadataCompat);
                }catch (Throwable e){
                    uIUpdateListener=null;
                }
            }

            Log.d("MetaDataPostExecute", "Big computation finished");
        }

    }

     static void clearLibrary(){
        datax.clear();
        datav.clear();
        folderName.clear();
        folderNameVideo.clear();
        AlbumLibrary.albumxCategoryExpand.clear();
        ArtistLibrary.artistCategory.clear();
        FolderLibrary.folderCategory.clear();
        RecentlyLibrary.recentData.clear();

    }


     private static int[] getpaletteColor(Bitmap bitmap) {

        int[] colorArray={ majorColorX,minorColorX,middleColorX};

        if(bitmap==null)
            return colorArray;

            Palette palette = Palette.from(bitmap).generate();

        // int defaultValue = -4671304;
        // int defaultDark=-15263969;

         Log.d("majorColor", "getpaletteColor:  "+majorColorX);
         Log.d("middleColor", "getpaletteColor: "+middleColorX);

         int mutedLight ;
                 //= palette.getLightMutedColor(middleColorX);
         int mutedDark;
         //= palette.getDarkMutedColor(majorColorX);
         //int vibrant = palette.getVibrantColor(defaultValue);
         int vibrantLight;
         //= palette.getLightVibrantColor(mutedLight);
         int vibrantDark;
                 //= palette.getDarkVibrantColor(mutedDark);
        // int muted = palette.getMutedColor(defaultValue);
         Palette.Swatch lightMutedSwatch = palette.getLightMutedSwatch();
         mutedLight= lightMutedSwatch != null ? lightMutedSwatch.getRgb() : middleColorX;
         Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
         mutedDark= darkMutedSwatch != null ? darkMutedSwatch.getRgb() : majorColorX;

         Palette.Swatch vibrantSwatch = palette.getLightVibrantSwatch();
         vibrantLight= vibrantSwatch != null ? vibrantSwatch.getRgb() :  mutedLight;

         Palette.Swatch darkVibrantSwatch = palette.getDarkMutedSwatch();
         vibrantDark= darkVibrantSwatch != null ? darkVibrantSwatch.getRgb() : mutedDark;



          majorColorX= colorArray[0]=vibrantDark;
           minorColorX= colorArray[1]=mutedLight;
          middleColorX=  colorArray[2]=vibrantLight;

         Log.d("majorColorSet", "getpaletteColor:  "+majorColorX);
         Log.d("middleColorSet", "getpaletteColor: "+middleColorX);

           //  palette=Palette.from(BitmapFactory.decodeResource(ApplicationContextProvider.getContext().getResources(),R.drawable.diskkg)).generate();
         //Log.d("DiskG", "getpaletteColor: Vibrant"+palette.getVibrantColor(defaultValue));
         return colorArray;
        }

}