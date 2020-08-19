package com.McDevelopers.sonaplayer.PreferenceActivity;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.McDevelopers.sonaplayer.ApplicationContextProvider;
import com.McDevelopers.sonaplayer.R;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;

public class CustomAlbumArtPreference extends Preference {
    private  SharedPreferences currentState;
    private TextView summaryTextView;
    private  String videoSummery;

    public CustomAlbumArtPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWidgetLayoutResource(R.layout.pref_albumart_layout);
         currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
         videoSummery = currentState.getString("albumFolder", null);
         if (TextUtils.isEmpty(videoSummery)){
             videoSummery="Currently no folder selected";
         }
    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        LinearLayout videoFolder=(LinearLayout) view.findViewById(R.id.pref_albumart_wrapper);
        videoFolder.setClickable(true);
       summaryTextView=(TextView) view.findViewById(R.id.pref_albumart_summary);
       summaryTextView.setText(videoSummery);

        videoFolder.setClickable(true);
            videoFolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ChooserDialog().with(view.itemView.getContext())
                            .withFilter(true, false)
                            .withResources(R.string.folder_chooser_title, R.string.title_choose, R.string.dialog_cancel)
                            //.withStartFile("storage/emulated/0/Movies")
                            .withChosenListener(new ChooserDialog.Result() {
                                @Override
                                public void onChoosePath(String path, File pathFile) {
                                    currentState = getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor;
                                    editor = currentState.edit();
                                    editor.putBoolean("customAlbum", true);
                                    editor.putString("albumFolder", path);
                                    editor.commit();
                                    videoSummery=path;
                                    summaryTextView.setText(videoSummery);

                                    PreferenceActivity.sIntent.putExtra("albumFlag",true);
                                    PreferenceActivity.sIntent.putExtra("customAlbum",true);

                                }
                            })

                            .build()
                            .show();
                }

             });


        Button resetButton=(Button)view.findViewById(R.id.pref_albumart_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(currentState.getString("albumFolder", null))){

                    Toast.makeText(getContext(),"Nothing to reset",Toast.LENGTH_LONG).show();
                    return;
                }
                Vibrator vb = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(50);
                currentState = getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor;
                editor = currentState.edit();
                editor.putString("albumFolder", null);
                editor.putBoolean("customAlbum", false);
                editor.commit();

                videoSummery="Currently no folder selected";
                summaryTextView.setText(videoSummery);

                PreferenceActivity.sIntent.putExtra("albumFlag",true);
                PreferenceActivity.sIntent.putExtra("customAlbum",false);
                Toast.makeText(getContext(),"AlbumArt folder set to defaults",Toast.LENGTH_LONG).show();

            }
            });

    }

}