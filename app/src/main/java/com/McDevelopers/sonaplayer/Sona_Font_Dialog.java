package com.McDevelopers.sonaplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Sona_Font_Dialog extends DialogFragment {

    private RadioGroup mRadioGroup;

    public Sona_Font_Dialog() {

        // Empty constructor is required for DialogFragment

        // Make sure not to add arguments to the constructor

        // Use `newInstance` instead as shown below

    }

    public interface Sona_font_result {

        void onFontApplied(int fontIndex);

    }



    public static Sona_Font_Dialog newInstance(String title) {

        Sona_Font_Dialog frag = new Sona_Font_Dialog();

        Bundle args = new Bundle();

        args.putString("title", title);

        frag.setArguments(args);

        return frag;

    }


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sona_font_dialog, container);

    }



    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Get field from view

        mRadioGroup = view.findViewById(R.id.font_radio_group);
        RadioButton  fontRButton0 = view.findViewById(R.id.fontRadio0);
        RadioButton  fontRButton1 = view.findViewById(R.id.fontRadio1);
        RadioButton fontRButton2 = view.findViewById(R.id.fontRadio2);
        RadioButton fontRButton3 = view.findViewById(R.id.fontRadio3);
        RadioButton fontRButton4 = view.findViewById(R.id.fontRadio4);
        RadioButton  fontRButton5 = view.findViewById(R.id.fontRadio5);
        RadioButton  fontRButton6 = view.findViewById(R.id.fontRadio6);
        RadioButton fontRButton7 = view.findViewById(R.id.fontRadio7);
        RadioButton fontRButton8 = view.findViewById(R.id.fontRadio8);
        RadioButton  fontRButton9 = view.findViewById(R.id.fontRadio9);
        RadioButton fontRButton10 = view.findViewById(R.id.fontRadio10);
        RadioButton fontRButton11 = view.findViewById(R.id.fontRadio11);

        Button  cancelButton = view.findViewById(R.id.cancelButton);
        Button  chooseButton = view.findViewById(R.id.chooseButton);

        TextView fontHeading=view.findViewById(R.id.font_heading);


        fontRButton0.setText("System Default");
        fontRButton1.setText(ApplicationContextProvider.fontNameArray[1]);
        fontRButton2.setText(ApplicationContextProvider.fontNameArray[2]);
        fontRButton3.setText(ApplicationContextProvider.fontNameArray[3]);
        fontRButton4.setText(ApplicationContextProvider.fontNameArray[4]);
        fontRButton5.setText(ApplicationContextProvider.fontNameArray[5]);
        fontRButton6.setText(ApplicationContextProvider.fontNameArray[6]);
        fontRButton7.setText(ApplicationContextProvider.fontNameArray[7]);
        fontRButton8.setText(ApplicationContextProvider.fontNameArray[8]);
        fontRButton9.setText(ApplicationContextProvider.fontNameArray[9]);
        fontRButton10.setText(ApplicationContextProvider.fontNameArray[10]);
        fontRButton11.setText("RANDOM");

        Typeface face0=Typeface.DEFAULT;
        Typeface face1 = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                ApplicationContextProvider.fontArray[1]);
        Typeface face2 = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                ApplicationContextProvider.fontArray[2]);
        Typeface face3 = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                ApplicationContextProvider.fontArray[3]);
        Typeface face4 = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                ApplicationContextProvider.fontArray[4]);
        Typeface face5 = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                ApplicationContextProvider.fontArray[5]);
        Typeface face6 = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                ApplicationContextProvider.fontArray[6]);
        Typeface face7 = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                ApplicationContextProvider.fontArray[7]);
        Typeface face8 = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                ApplicationContextProvider.fontArray[8]);
        Typeface face9 = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                ApplicationContextProvider.fontArray[9]);
        Typeface face10 = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                ApplicationContextProvider.fontArray[10]);

        Typeface face11 = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                "fonts/twilight.ttf");

        Typeface faceHead=Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(),
                "fonts/Ageta.otf");

        fontHeading.setTypeface(faceHead);
        cancelButton.setTypeface(faceHead);
        chooseButton.setTypeface(faceHead);

        fontRButton0.setTypeface(face0);
        fontRButton1.setTypeface(face1);
        fontRButton2.setTypeface(face2);
        fontRButton3.setTypeface(face3);
        fontRButton4.setTypeface(face4);
        fontRButton5.setTypeface(face5);
        fontRButton6.setTypeface(face6);
        fontRButton7.setTypeface(face7);
        fontRButton8.setTypeface(face8);
        fontRButton9.setTypeface(face9);
        fontRButton10.setTypeface(face10);
        fontRButton11.setTypeface(face11);


        SharedPreferences currentState = ApplicationContextProvider.getContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        boolean RandomFont = currentState.getBoolean("Random", true);

        if (RandomFont) {

            mRadioGroup.check(mRadioGroup.getChildAt(11).getId());


    }else if(ApplicationContextProvider.getFontIndex()>=0 && ApplicationContextProvider.getFontIndex()<=10)
    {
        mRadioGroup.check(mRadioGroup.getChildAt(ApplicationContextProvider.getFontIndex()).getId());
    }


      //  String title = getArguments().getString("title", "Enter Name");
      //  getDialog().setTitle(title);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(ApplicationContextProvider.getContext(),"CancelButtonClicked",Toast.LENGTH_LONG).show();
                dismiss();

            }
        });

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton SButton = mRadioGroup.findViewById(mRadioGroup.getCheckedRadioButtonId());
                 int Rindex = mRadioGroup.indexOfChild(SButton);


                Sona_font_result result = (Sona_font_result) getActivity();
                result.onFontApplied(Rindex);
               // Toast.makeText(ApplicationContextProvider.getContext(), "Font :"+Rindex,Toast.LENGTH_LONG).show();
                dismiss();
            }
        });


        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton SButton = group.findViewById(checkedId);
                int Rindex = group.indexOfChild(SButton);
             if(Rindex==11){
                 Vibrator vb = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                 if(vb != null)
                 vb.vibrate(50);
                 Toast.makeText(getContext(),"RANDOM will change font automatically on every COLD StartUp!",Toast.LENGTH_LONG).show();
             }

            }
        });


       if(getDialog().getWindow()!=null)
            getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


    }



   }