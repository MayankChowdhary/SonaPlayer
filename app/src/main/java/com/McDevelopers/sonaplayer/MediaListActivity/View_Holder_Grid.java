package com.McDevelopers.sonaplayer.MediaListActivity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.McDevelopers.sonaplayer.R;

import io.gresse.hugo.vumeterlibrary.VuMeterView;


public class View_Holder_Grid extends RecyclerView.ViewHolder{

    TextView title;
    TextView audioMeta;
    ImageView imageView;
    ImageView videoIcon;
    VuMeterView vuMeterView;
    CheckBox markBox;

    View_Holder_Grid(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.video_title);
        imageView = (ImageView) itemView.findViewById(R.id.video_thumb_view);
        videoIcon =  itemView.findViewById(R.id.video_icon);
        vuMeterView=itemView.findViewById(R.id.vumeter);
        markBox=itemView.findViewById(R.id.mark_box);
        audioMeta = (TextView) itemView.findViewById(R.id.audio_meta);

    }

}

