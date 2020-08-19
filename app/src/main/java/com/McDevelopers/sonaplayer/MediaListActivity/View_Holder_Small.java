package com.McDevelopers.sonaplayer.MediaListActivity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.McDevelopers.sonaplayer.R;

import io.gresse.hugo.vumeterlibrary.VuMeterView;


public class View_Holder_Small extends RecyclerView.ViewHolder{

    TextView title;
    TextView description;
    ImageView imageView;
    CheckBox markBox;
    VuMeterView vuMeterView;
    ImageView videoIcon;

    View_Holder_Small(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        imageView = (ImageView) itemView.findViewById(R.id.album_imageView);
        vuMeterView=itemView.findViewById(R.id.vumeter);
        markBox=itemView.findViewById(R.id.mark_box);
        videoIcon =  itemView.findViewById(R.id.video_icon);

    }

}

