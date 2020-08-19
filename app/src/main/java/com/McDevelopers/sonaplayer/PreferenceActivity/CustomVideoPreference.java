package com.McDevelopers.sonaplayer.PreferenceActivity;



import android.content.Context;
import android.content.Intent;
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
public class CustomVideoPreference extends Preference {
    private  SharedPreferences currentState;
    private TextView summaryTextView;
    private  String videoSummery;

    public  CustomVideoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWidgetLayoutResource(R.layout.pref_video_layout);
         currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
         videoSummery = currentState.getString("videoFolder", null);
         if (TextUtils.isEmpty(videoSummery)){
             videoSummery="Currently all folders are selected";
         }

    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        LinearLayout videoFolder=(LinearLayout)view.findViewById(R.id.pref_video_wrapper);
        videoFolder.setClickable(true);
       summaryTextView=(TextView) view.findViewById(R.id.pref_video_summary);
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
                                    editor.putString("videoFolder", path);
                                    editor.commit();
                                    videoSummery=path;
                                    summaryTextView.setText(videoSummery);
                                    PreferenceActivity.sIntent.putExtra("vFlag",true);

                                }
                            })
                            .build()
                            .show();
                }

             });


        Button resetButton=(Button)view.findViewById(R.id.pref_video_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(currentState.getString("videoFolder", null))){
                    Toast.makeText(getContext(),"Nothing to Reset",Toast.LENGTH_LONG).show();
                    return;
                }
                Vibrator vb = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(50);
                currentState = getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor;
                editor = currentState.edit();
                editor.putString("videoFolder", null);
                editor.commit();
                videoSummery="Currently all folders are selected";
                summaryTextView.setText(videoSummery);
                PreferenceActivity.sIntent.putExtra("vFlag",true);
                Toast.makeText(getContext(),"Video folder set to defaults",Toast.LENGTH_LONG).show();

            }
            });

    }

}