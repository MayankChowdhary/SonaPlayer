package com.McDevelopers.sonaplayer.FolderListActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.McDevelopers.sonaplayer.ApplicationContextProvider;
import com.McDevelopers.sonaplayer.GlideApp;
import com.McDevelopers.sonaplayer.MusicLibrary;
import com.McDevelopers.sonaplayer.R;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.io.File;
import java.util.Random;


public class FolderCheckGroupHolder extends GroupViewHolder {

    private TextView genreName;
    private ImageView arrow;
    private ImageView icon;
    private TextView mNumTextView;
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private static boolean isCustomArt = false;
    private static Random rand;
    private static int currentArtIndex;


    public FolderCheckGroupHolder(View itemView) {
        super(itemView);
        genreName = (TextView) itemView.findViewById(R.id.tv_album_category);
        arrow = (ImageView) itemView.findViewById(R.id.iv_arrow_expand);
        icon = (ImageView) itemView.findViewById(R.id.album_imageView);
        mNumTextView=itemView.findViewById(R.id.song_num);

        SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        isCustomArt = currentState.getBoolean("customAlbum", false);
    }

    public void setGenreTitle(ExpandableGroup genre) {

            genreName.setText(genre.getTitle());
            mNumTextView.setText(((FolderCategoryExpanded) genre).getSongCount());

    }

    public  void setGroupIcon(boolean isVideo){
        if(isVideo){

            GlideApp
                    .with(icon.getContext())
                    .asBitmap()
                    .thumbnail(0.1f)
                    .placeholder(R.drawable.folder_movie)
                    .load(R.drawable.folder_movie)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(icon);

        }else {
            GlideApp
                    .with(icon.getContext())
                    .asBitmap()
                    .thumbnail(0.1f)
                    .placeholder(R.drawable.folder_music)
                    .load(R.drawable.folder_music)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(icon);
        }

    }

    @Override
    public void expand() {

        expansionToggled(false);
        arrow.setRotation(ROTATED_POSITION);

        Log.d("expandAnimation", "expand: Invoked");
    }

    @Override
    public void collapse() {

        expansionToggled(true);
        arrow.setRotation(INITIAL_POSITION);

        Log.d("CollapseAnimation", "collapse: Rotation:" + arrow.getRotation());
    }

    public void expansionToggled(boolean expanded) {

        RotateAnimation rotateAnimation;
        if (expanded) { // rotate clockwise
            rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);

            Log.d("AnimationExpand", " Rotation: Invoked" + arrow.getRotation());

        } else { // rotate counterclockwise
            rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            Log.d("AnimationCollapse", " Rotation:" + arrow.getRotation());

        }

        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        arrow.startAnimation(rotateAnimation);



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
            return null;
        } catch (Throwable e) {

            Log.e("ExceptionRaised", "randomArt: " + e);
            currentArtIndex = -1;
            return null;
        }


    }


}