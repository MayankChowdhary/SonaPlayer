package com.McDevelopers.sonaplayer;

import android.content.Context;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.Random;

public class Animations {
         private static   Random rand = new Random();

    public static void Animations(Context context,int AnimId){

        if(AnimId==15)
           AnimId=rand.nextInt(14);

        switch (AnimId){

            case 0:
                Animatoo.animateZoom(context);
                break;
            case 1:
                Animatoo.animateFade(context);
                break;
            case 2:
                Animatoo.animateWindmill(context);
                break;
            case 3:
                Animatoo.animateSpin(context);
                break;
            case 4:
                Animatoo.animateDiagonal(context);
                break;
            case 5:
                Animatoo.animateSplit(context);
                break;
            case 6:
                Animatoo.animateShrink(context);
                break;
            case 7:
                Animatoo.animateCard(context);
                break;
            case 8:
                Animatoo.animateInAndOut(context);
                break;
            case 9:
                Animatoo.animateSwipeLeft(context);
            break;
            case 10:
                Animatoo.animateSwipeRight(context);
                break;
            case 11:
                Animatoo.animateSlideLeft(context);
                break;
            case 12:
                Animatoo.animateSlideRight(context);
                break;
            case 13:
                Animatoo.animateSlideDown(context);
                break;
            case 14:
                Animatoo.animateSlideUp(context);
                break;

        }
    }
}
