package com.McDevelopers.sonaplayer.ArtistListActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.McDevelopers.sonaplayer.ApplicationContextProvider;
import com.McDevelopers.sonaplayer.GlideApp;
import com.McDevelopers.sonaplayer.MusicLibrary;
import com.McDevelopers.sonaplayer.R;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.io.File;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArtistExpandChildHolder extends ChildViewHolder {

    private TextView childTextView;
    private CircleImageView icon;
    private CheckBox markBox;

    private static boolean isCustomArt = false;
    private static Random rand;
    private static int currentArtIndex;

    public ArtistExpandChildHolder(View itemView) {
        super(itemView);

        SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        isCustomArt = currentState.getBoolean("customAlbum", false);

        childTextView = (TextView) itemView.findViewById(R.id.tv_albums);
        icon =(CircleImageView) itemView.findViewById(R.id.album_imageViews);
        markBox=itemView.findViewById(R.id.mark_box_child);
    }

    public void setArtistName(String name) {
        childTextView.setText(name);
    }

    public void setMarkBox(boolean isVisible) {

        if(isVisible) {

            markBox.setVisibility(View.VISIBLE);
        }else {

            if(markBox.getVisibility()==View.VISIBLE){
                markBox.setVisibility(View.GONE);
            }
        }
    }

    public  void setMarkBoxCheck(boolean isChecked){

        markBox.setChecked(isChecked);
    }

    public void setChildAlbumArt(String artId ,boolean checkFlag) {
        if (isCustomArt && MusicLibrary.listOfAllImages.size() != 0 && !checkFlag) {


            GlideApp
                    .with(icon.getContext())
                    .asBitmap()
                    .thumbnail(0.1f)
                    .load(new File(randomArt()))
                    .dontAnimate()
                    .placeholder(R.drawable.loading_art)
                    .error(R.drawable.loading_art)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(icon);
        } else {


            GlideApp
                    .with(icon.getContext())
                    .asBitmap()
                    .load(artId)
                    .dontAnimate()
                    .signature(new ObjectKey(new File(artId).lastModified()))
                    .placeholder(R.drawable.albumitem)
                    .error(R.drawable.albumitem)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(icon);
        }
    }

    private static Uri getAlbumUri(String mediaID) {
        try {
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Log.d("AlbumArtUri", ContentUris.withAppendedId(sArtworkUri, Long.parseLong(mediaID)).toString());
            return ContentUris.withAppendedId(sArtworkUri, Long.parseLong(mediaID));
        }catch (Throwable e){
            return null;
        }

    }

    private static String randomArt() {

        int max = MusicLibrary.listOfAllImages.size();
        rand = new Random();
        int newAlbumIndex = currentArtIndex;

        try {
            if (max > 1) {

                if (max > 50) {
                    while (newAlbumIndex == currentArtIndex) {
                        newAlbumIndex = rand.nextInt(max);
                    }
                } else {

                    newAlbumIndex = rand.nextInt(max);
                }
                String newAlbum = MusicLibrary.listOfAllImages.get(newAlbumIndex);
                currentArtIndex = newAlbumIndex;
                return newAlbum;
            } else
                currentArtIndex = -1;
            return "";
        } catch (Throwable e) {

            Log.e("ExceptionRaised", "randomArt: " + e);
            currentArtIndex = -1;
            return "";
        }


    }
}