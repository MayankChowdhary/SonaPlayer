package com.McDevelopers.sonaplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class ApplicationContextProvider extends Application {


    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;
    private static String fontPath;
    public static ApplicationContextProvider instance;
    private static int fontIndex;
    private static boolean fontFlag=false;

    public static  String fontArray[]={"Default","fonts/newtserif.otf","fonts/josefinslab.ttf","fonts/macondo.ttf","fonts/cagliostro.ttf","fonts/Iceberg.ttf","fonts/comfortaa.ttf","fonts/elmessiri.otf","fonts/SofadiOne.ttf","fonts/convincing.ttf","fonts/handlee.ttf"};

    public static  String fontNameArray[]={"Default","NewtSerif","JosefinSlab","Macondo","Cagliostro","Iceberg","Comfortaa","Elmessiri","SofadiOne","Convincing","Handlee"};

    public static boolean systemFont;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SharedPreferences currentIndex = getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE);
        fontIndex = currentIndex.getInt("fontIndex", 1);
        systemFont=currentIndex.getBoolean("systemFont", false);
        sContext = getApplicationContext();
        fontPath = fontArray[fontIndex];

        if (!systemFont) {

            ViewPump.init(ViewPump.builder()
                    .addInterceptor(new CalligraphyInterceptor(
                            new CalligraphyConfig.Builder()
                                    .setDefaultFontPath(fontPath)
                                    .setFontAttrId(R.attr.fontPath)
                                    .build()))
                    .build());

            fontFlag = true;
        }
    }

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }
    public static ApplicationContextProvider getInstance() {
        return instance;
    }

    public static String getFontPath() {
        return fontPath;
    }

    public static int getFontIndex() {
        return fontIndex;
    }


    public static boolean getFontFlag() {
        return fontFlag;
    }

    public static void setFontFlag(boolean flag) {

        fontFlag=flag;

    }


}

