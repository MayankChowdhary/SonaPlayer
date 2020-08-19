package com.McDevelopers.sonaplayer.FolderListActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.McDevelopers.sonaplayer.ApplicationContextProvider;
import com.McDevelopers.sonaplayer.ArtistListActivity.ArtistCategoryExpanded;
import com.McDevelopers.sonaplayer.ArtistListActivity.Artists;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FolderLibrary {

    public static List<FolderCategoryExpanded> folderCategory = new ArrayList<>();
    public static List<FolderCategoryExpanded> albumxDummyData=new ArrayList<>();

    private String songCounts;
    private static long audioReject=60000;

    public void FolderSongLibrary(LinkedHashSet<String> folderName) {
        SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        audioReject=currentState.getLong("audioReject",60000);
        folderCategory.clear();
        Log.e("FolderListCleared", "FolderLibrary: Invoked" );


        List<String> folderNameSimple = new ArrayList<>(folderName);
        Collections.sort(folderNameSimple, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        for (int i = 0; i < folderNameSimple.size(); i++) {

            String folder=folderNameSimple.get(i);
            String folderX="%"+folder+"%";

            String albumX=null;


        try {


                ContentResolver contentResolver = ApplicationContextProvider.getContext().getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String selection = MediaStore.Audio.Media.DATA + " like ? ";
                String sortOrder = MediaStore.Audio.Media.TITLE;
                 String[] whereArgs =  new String[]{folderX};
                Cursor cursor = contentResolver.query(uri, null, selection,whereArgs, sortOrder);


                 File f ;
                if (cursor != null && cursor.getCount() > 0) {

                    List<Folders> folderXCategory = new ArrayList<>();


                    while (cursor.moveToNext()) {

                        String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                        TimeUnit durationUnit = TimeUnit.SECONDS;
                        String filename = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        String albumArtId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));


                        f=new File(data);
                       albumX= f.getParentFile().getName();



                        if (!(filename.endsWith(".amr") || filename.endsWith(".AMR"))) {
                            if (duration >= audioReject) {

                                Folders albumone = new Folders(title, id, albumArtId, filename,data,false,album,artist,duration,size);
                                folderXCategory.add(albumone);

                            }
                        }

                    }

                    if(folderXCategory.size()>1){

                        songCounts=folderXCategory.size()+" "+"Songs";
                    }else {

                        songCounts=folderXCategory.size()+" "+"Song";
                    }

                    if(folderXCategory.size()>0) {
                        FolderCategoryExpanded folderCategory1 = new FolderCategoryExpanded(albumX, folderXCategory, songCounts,false);
                        folderCategory.add(folderCategory1);
                    }


                }


                cursor.close();

            } catch(Throwable e){

                Log.e("ExceptionRaised", "FolderSongLibrary: " + e);
            }



     }


    }



    public void setDummydata(){

        albumxDummyData.clear();

        List<Folders> albumzCategory=new ArrayList<>();

        Folders albumone = new Folders("Album1", "456", "fileAlbum1","albumData1","album",true,"albumName","artistName",45588,8855);
        albumzCategory.add(albumone);
        FolderCategoryExpanded album_category_expand = new FolderCategoryExpanded("Group1",albumzCategory, "4555", false);
        albumxDummyData.add(album_category_expand);

    }

    public void FolderVideoLibrary(LinkedHashSet<String> folderName) {
        Log.e("FolderListCleared", "FolderLibrary: Invoked" );


        List<String> folderNameSimple = new ArrayList<>(folderName);
        Collections.sort(folderNameSimple, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        for (int i = 0; i < folderNameSimple.size(); i++) {

            String folder=folderNameSimple.get(i);
            String folderX="%"+folder+"%";

            String albumX=null;


            try {


                ContentResolver contentResolver = ApplicationContextProvider.getContext().getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String sortOrder = MediaStore.Video.Media.TITLE;
                Cursor cursor = contentResolver.query(uri, null, MediaStore.Video.Media.DATA + " like ? ", new String[]{folderX}, sortOrder);


                File f ;
                if (cursor != null && cursor.getCount() > 0) {

                    List<Folders> folderXCategory = new ArrayList<>();


                    while (cursor.moveToNext()) {

                        String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        String filename = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));


                        f=new File(data);
                        albumX= f.getParentFile().getName();

                       // Log.d("FolderName", albumX);
                       // Log.d("Title", title);


                            if (duration >= 120000) {

                                Folders albumone = new Folders(filename, id,data, title, data,true,albumX,"N/A",duration,size);
                                folderXCategory.add(albumone);
                            }


                    }

                    if(folderXCategory.size()>1){

                        songCounts=folderXCategory.size()+" "+"Videos";
                    }else {

                        songCounts=folderXCategory.size()+" "+"Video";
                    }

                    if(folderXCategory.size()>0) {
                        FolderCategoryExpanded folderCategory1 = new FolderCategoryExpanded(albumX, folderXCategory, songCounts,true);
                        folderCategory.add(folderCategory1);
                    }


                }


                cursor.close();

            } catch(Throwable e){

                Log.e("ExceptionRaised", "FolderSongLibrary: " + e);
            }



        }


    }
}

