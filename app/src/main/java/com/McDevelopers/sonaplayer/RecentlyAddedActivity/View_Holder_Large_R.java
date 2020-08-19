package com.McDevelopers.sonaplayer.RecentlyAddedActivity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.McDevelopers.sonaplayer.R;


public class View_Holder_Large_R extends RecyclerView.ViewHolder{

    TextView title;
    TextView description;
    TextView songMeta;
    ImageView imageView;
    ImageView videoIcon;
    CheckBox markBox;

    View_Holder_Large_R(View itemView) {
        super(itemView);
        title =  itemView.findViewById(R.id.title);
        description =  itemView.findViewById(R.id.description);
        songMeta = itemView.findViewById(R.id.song_meta);
        imageView =  itemView.findViewById(R.id.album_imageView);
        videoIcon =  itemView.findViewById(R.id.video_icon);
        videoIcon.setVisibility(View.INVISIBLE);
        markBox=itemView.findViewById(R.id.mark_box);

    }

}

