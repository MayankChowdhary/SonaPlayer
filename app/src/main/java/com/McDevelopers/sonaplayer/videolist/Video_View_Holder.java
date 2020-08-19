package com.McDevelopers.sonaplayer.videolist;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.McDevelopers.sonaplayer.R;


public class Video_View_Holder extends RecyclerView.ViewHolder{

    TextView title;
    ImageView imageView;
    TextView videoMeta;
    CheckBox mark_box;

    Video_View_Holder(View itemView) {
        super(itemView);
        title =  itemView.findViewById(R.id.video_title);
        imageView =  itemView.findViewById(R.id.video_thumb_view);
        videoMeta =  itemView.findViewById(R.id.video_meta);
        mark_box = itemView.findViewById(R.id.mark_box_v);

    }

}

