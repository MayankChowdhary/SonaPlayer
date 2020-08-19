package com.McDevelopers.sonaplayer;

import android.app.Activity;


import com.jakewharton.processphoenix.ProcessPhoenix;

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Activity activity;

    public MyExceptionHandler(Activity a) {
        activity = a;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        activity.finish();
        ProcessPhoenix.triggerRebirth(ApplicationContextProvider.getContext());
        System.exit(2);

    }
}