package com.McDevelopers.sonaplayer.MediaListActivity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.McDevelopers.sonaplayer.R;

import io.gresse.hugo.vumeterlibrary.VuMeterView;


public class View_Holder_Large extends RecyclerView.ViewHolder{

    CardView cv;
    TextView title;
    TextView description;
    TextView songMeta;
    ImageView imageView;
    ImageView videoIcon;
    CheckBox markBox;
    VuMeterView vuMeterView;

    View_Holder_Large(View itemView) {
        super(itemView);
        cv =  itemView.findViewById(R.id.cardView);
        title =  itemView.findViewById(R.id.title);
        description =  itemView.findViewById(R.id.description);
        songMeta = itemView.findViewById(R.id.song_meta);
        imageView =  itemView.findViewById(R.id.album_imageView);
        videoIcon =  itemView.findViewById(R.id.video_icon);
        vuMeterView=itemView.findViewById(R.id.vumeter);
        markBox=itemView.findViewById(R.id.mark_box);

    }

}

