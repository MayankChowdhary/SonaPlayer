package com.McDevelopers.sonaplayer

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class Vibration {

    companion object {

     fun vibrate(duration: Long) {
        val vibrator = ApplicationContextProvider.getContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
}
}