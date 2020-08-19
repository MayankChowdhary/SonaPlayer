package com.McDevelopers.sonaplayer.RecentlyAddedActivity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.McDevelopers.sonaplayer.R;


public class View_Holder_Small_R extends RecyclerView.ViewHolder{

    TextView title;
    TextView description;
    ImageView imageView;
    ImageView videIcon;
    CheckBox markBox;

    View_Holder_Small_R(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        imageView = (ImageView) itemView.findViewById(R.id.album_imageView);
        videIcon = (ImageView) itemView.findViewById(R.id.video_icon);
        videIcon.setVisibility(View.INVISIBLE);
        markBox=itemView.findViewById(R.id.mark_box);

    }

}

