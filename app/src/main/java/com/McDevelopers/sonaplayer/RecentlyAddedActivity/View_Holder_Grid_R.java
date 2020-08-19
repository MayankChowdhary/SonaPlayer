package com.McDevelopers.sonaplayer.RecentlyAddedActivity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.McDevelopers.sonaplayer.R;


public class View_Holder_Grid_R extends RecyclerView.ViewHolder{
    TextView title;
    ImageView imageView;
    ImageView videoIcon;
    TextView audio_meta;
    CheckBox markBox;

    View_Holder_Grid_R(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.video_title);
        imageView = (ImageView) itemView.findViewById(R.id.video_thumb_view);
        videoIcon =  itemView.findViewById(R.id.video_icon);
        videoIcon.setVisibility(View.INVISIBLE);
        audio_meta = (TextView) itemView.findViewById(R.id.audio_meta);
        markBox=itemView.findViewById(R.id.mark_box);

    }

}

