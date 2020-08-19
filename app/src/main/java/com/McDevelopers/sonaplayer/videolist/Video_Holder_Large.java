package com.McDevelopers.sonaplayer.videolist;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.McDevelopers.sonaplayer.R;


public class Video_Holder_Large extends RecyclerView.ViewHolder{

    TextView title;
    TextView description;
    TextView songMeta;
    ImageView imageView;
    CheckBox mark_box_vl;
    Video_Holder_Large(View itemView) {
        super(itemView);
        title =  itemView.findViewById(R.id.title);
        description =  itemView.findViewById(R.id.description);
        songMeta = itemView.findViewById(R.id.song_meta);
        imageView =  itemView.findViewById(R.id.album_imageView);
        mark_box_vl =  itemView.findViewById(R.id.mark_box_vl);

    }

}

