package com.McDevelopers.sonaplayer.RecentlyAddedActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.McDevelopers.sonaplayer.ApplicationContextProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecentlyLibrary {

public static ArrayList<RecentData> recentData=new ArrayList<>();
    private static long audioReject=60000;

    public void displayRecentSongs() {
        recentData.clear();
        SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        audioReject=currentState.getLong("audioReject",60000);
        Log.e("RecentListCleared", "RecentLibrary: Invoked" );

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);


        try {
            //retrieve song info
            ContentResolver musicResolver = ApplicationContextProvider.getContext().getContentResolver();
            Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            //String where = MediaStore.Audio.Media.DATE_ADDED;
            String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";
            String sortOrder = MediaStore.Audio.Media.DATE_MODIFIED + " DESC";
            Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, sortOrder);

            if (musicCursor != null && musicCursor.moveToFirst()) {
                //get columns
                int idColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media._ID);
                int titleColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.TITLE);
                int pathColumn = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.DATA);
                int albumColumn = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM);
                int filename = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.DISPLAY_NAME);
                int albumId = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM_ID);
                int durationId = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.DURATION);
                int sizeId = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.SIZE);
                int dateId = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.DATE_MODIFIED);
                int artistId = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.ARTIST);
                //add songs to list
                do {
                    String thisId = musicCursor.getString(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisPath = musicCursor.getString(pathColumn);
                    String album = musicCursor.getString(albumColumn);
                    String displayName = musicCursor.getString(filename);
                    String artistName = musicCursor.getString(artistId);
                    String description = album + " - " + artistName;
                    String imageId = musicCursor.getString(albumId);
                    Long duration = musicCursor.getLong(durationId);
                    String size = musicCursor.getString(sizeId);
                    Date date = new Date((musicCursor.getLong(dateId))*1000);
                    String datestr = dateFormat.format(date);


                    if(duration>=audioReject && recentData.size()<100) {
                      //  Log.d("Duration:", duration.toString());
                        //Log.d("Size:", size);
                        recentData.add(new RecentData(thisTitle, description, imageId, thisId, thisPath, displayName, duration,size,datestr));

                   }else if(recentData.size()>=100){

                        break;
                    }
                }
                while (musicCursor.moveToNext());
                Log.e("Size of Recent:", "Size:" + recentData.size());
            }
            musicCursor.close();

        }catch (Throwable e){

            Log.e("ExceptionRaised", "displayRecentSongs: "+e );
        }
    }


}
