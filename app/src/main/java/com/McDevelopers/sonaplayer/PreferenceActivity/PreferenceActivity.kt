@file:Suppress("PackageName")

package com.McDevelopers.sonaplayer.PreferenceActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.preference.*
import com.McDevelopers.sonaplayer.R
import com.McDevelopers.sonaplayer.SonaToast
import com.McDevelopers.sonaplayer.Vibration
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import android.view.animation.Animation
import com.labo.kaji.fragmentanimations.*


@SuppressLint("ApplySharedPref")
class PreferenceActivity : AppCompatActivity() {

    companion object {
        lateinit  var sIntent: Intent
        lateinit var  currentState: SharedPreferences
        private var crossFadeStatus: Boolean = false
        private var playPauseFade: Boolean = false
        private var audioReject: Long = 60000
        private var videoReject: Long = 120000
        private var autoPlay = false
        private var autoResume = false
        private var pFocuspause = true
        private var tFocuspause = true
        private var canduck = true
        private var wakelockx = false
        private var isPowerEnabled = true
        private var playerWakelock = false
        private var isAutoSleep = false
        private var keepScreenOn = true
        private var listAnimation = true
        private var alterEqMode = false
        private var marqueeFlag = true
        private var crossfadeTime = 15000
        private var manualFadeTime = 400
        private var fadeTime=700
        private var notyFlag=false
        private var powerSeekLevel = 20
        private var sleepSeekLevel=30
        private var colorized=true
        private var enterAnim=6
        private var exitAnim=0
        private var animList: Array<String> = arrayOf("Zoom", "Fade", "Windmill","Spin","Diagonal","Split","Shrink","Card","In and Out","Swipe Left","Swipe Right","Slide Left","Slide Right","Slide Down","Slide Up","Random")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
          sIntent= Intent()
        window.setBackgroundDrawable(null)
        currentState=getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
        crossFadeStatus = currentState.getBoolean("mCrossFade", true)
        playPauseFade = currentState.getBoolean("FadeInFadeOut", true)
        audioReject = currentState.getLong("audioReject", 60000)
        videoReject = currentState.getLong("videoReject", 120000)
        autoPlay = currentState.getBoolean("autoPlay", false)
        autoResume = currentState.getBoolean("autoResume", true)
        pFocuspause = currentState.getBoolean("pFocusPause", true)
        tFocuspause = currentState.getBoolean("tFocusPause", true)
        canduck = currentState.getBoolean("canDuck", true)
        wakelockx = currentState.getBoolean("wakeFlag", false)
        isPowerEnabled = currentState.getBoolean("powerFlag", true)
        playerWakelock = currentState.getBoolean("playerWakeLock", false)
        isAutoSleep = currentState.getBoolean("isAutoSleep", false)
        isAutoSleep = currentState.getBoolean("isAutoSleep", false)
        keepScreenOn = currentState.getBoolean("keepScreenOn", true)
        listAnimation = currentState.getBoolean("isAnimEnabled", true)
        alterEqMode = currentState.getBoolean("alterEqMode", false)
        marqueeFlag = currentState.getBoolean("marqueeFlag", true)
        crossfadeTime = currentState.getInt("crossfadeTime", 15000)
        manualFadeTime = currentState.getInt("manualFadeTime", 400)
        fadeTime = currentState.getInt("fadeTime", 700)
        notyFlag=currentState.getBoolean("notiFlag", false)
        powerSeekLevel = currentState.getInt("batteryLevel", 20)
        sleepSeekLevel= currentState.getInt("sleepBatteryLevel", 30)
        colorized=currentState.getBoolean("colorized", true)
        enterAnim=currentState.getInt("enterAnim", 6)
        exitAnim=currentState.getInt("exitAnim", 0)

        setContentView(R.layout.preference_activity_layout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this,R.color.setting_head_grey)))
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root, rootKey)
        }

        override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
             return  SidesAnimation.create(SidesAnimation.RIGHT, enter, 500)
            }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setDivider(ColorDrawable(Color.DKGRAY))
            setDividerHeight(2)
        }
    }

    class ScanPreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.scan_settings, rootKey)

        }
        override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
            return PushPullAnimation.create(PushPullAnimation.RIGHT, enter, 500)

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setDivider(ColorDrawable(Color.DKGRAY))
            setDividerHeight(2)

            val prefRejectAudio :DropDownPreference = findPreference<Preference>("pref_reject_audio") as DropDownPreference
            prefRejectAudio.value = audioReject.toString()
            prefRejectAudio.summary=((audioReject / 1000).toString() + " Seconds")

            val prefRejectVideo : DropDownPreference = findPreference<Preference>("pref_reject_video") as DropDownPreference
            prefRejectVideo.value = videoReject.toString()
            prefRejectVideo.summary=((videoReject / 1000).toString() + " Seconds")

            prefRejectAudio.setOnPreferenceChangeListener { preference, newValue ->
                Log.d("PreferenceChangeBlock", "Invoked")
               preference as DropDownPreference
                val stringValue = newValue.toString()
                val index = preference.findIndexOfValue(stringValue)
                preference.summary=preference.entries[index]
                audioReject=stringValue.toLong()
                        val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                        val editor = sharedPreference.edit()
                        editor.putLong("audioReject", stringValue.toLong())
                        editor.commit()

                        sIntent.putExtra("audioReject", true)
                        Log.d("VideoRejectValue", "SavedValue: ${sharedPreference.getLong("audioReject", 0)}")
                true

            }

            prefRejectVideo.setOnPreferenceChangeListener { preference, newValue ->
                val stringValue = newValue.toString()
                Log.d("PreferenceChangeBlock", "StringValue: $stringValue")
                preference as DropDownPreference
                    val index = preference.findIndexOfValue(stringValue)
                    preference.summary= preference.entries[index]
                    videoReject=stringValue.toLong()
                    val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                    val editor = sharedPreference.edit()
                    editor.putLong("videoReject", stringValue.toLong())
                    editor.commit()

                    sIntent.putExtra("vFlag", true)
                    Log.d("VideoRejectValue", "SavedValue: ${sharedPreference.getLong("videoReject", 0)}")

                true
            }




        }

        }

    class CrossfadePreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.crossfade_settings, rootKey)

        }

        override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
            return  FlipAnimation.create(FlipAnimation.LEFT, enter, 500)

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setDivider(ColorDrawable(Color.DKGRAY))
            setDividerHeight(2)

            val crossfadeSwitch: SwitchPreferenceCompat  = findPreference<Preference>("crossfade_switch") as SwitchPreferenceCompat
            crossfadeSwitch.isChecked = crossFadeStatus

            val fadeSwitch:SwitchPreferenceCompat = findPreference<Preference>("play_pause_fade") as SwitchPreferenceCompat
            fadeSwitch.isChecked = playPauseFade

            crossfadeSwitch.setOnPreferenceChangeListener { preference, _ ->
                       Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                        val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("mCrossFade", switched)
                editor.commit()
                crossFadeStatus=switched
                        sIntent.putExtra("crossFlag", true)
                        sIntent.putExtra("mCrossFade", switched)
                        if (switched) {
                            SonaToast.setToast(requireContext(), "Crossfade enabled", 0)
                        } else {
                            SonaToast.setToast(requireContext(), "Crossfade disabled", 0)
                        }
                        Log.d("Crossfade", "onPreferenceChange:crossfadeStatus: $switched")
                true
            }

            fadeSwitch.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("FadeInFadeOut", switched).commit()
                editor.commit()
                sIntent.putExtra("fadingFlag", true)
                sIntent.putExtra("FadeInFadeOut", switched)
                playPauseFade=switched
                if (switched) {
                    SonaToast.setToast(requireContext(), "Play/Pause Fade enabled", 0)
                } else {
                    SonaToast.setToast(requireContext(), "Play/Pause Fade disabled", 0)
                }
                Log.d("Play/Pause Fade", "onPreferenceChange:Play/Pause Fade status: $switched")

                true
            }


            val crossfadeSeekbar : SeekBarPreference = findPreference<Preference>("crossfade_seekbar") as SeekBarPreference
            crossfadeSeekbar.summary= crossfadeTime.toString()+"ms"
            crossfadeSeekbar.value= crossfadeTime/250
            crossfadeSeekbar.updatesContinuously=true
            crossfadeSeekbar.setOnPreferenceChangeListener { preference, newValue ->

                preference as SeekBarPreference
                Log.d("CrossfadeSeekbar","ValueBefore:$newValue")
                val valueAfter: Int = ((newValue.toString()).toInt())*250
                Log.d("CrossFadeSeekbar","valueAfter: $valueAfter")

                preference.summary=valueAfter.toString()+"ms"
                crossfadeTime=valueAfter
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putInt("crossfadeTime", valueAfter)
                editor.commit()
                sIntent.putExtra("crossTimeFlag",true)

                true
            }

            val manualFadeSeekbar : SeekBarPreference = findPreference<Preference>("manual_crossfade_seekbar") as SeekBarPreference
            manualFadeSeekbar.summary= manualFadeTime.toString()+"ms"
            manualFadeSeekbar.value= manualFadeTime/10
            manualFadeSeekbar.updatesContinuously=true

            manualFadeSeekbar.setOnPreferenceChangeListener { preference, newValue ->
                preference as SeekBarPreference
                Log.d("ManualfadeSeekbar","ValueBefore:$newValue")
                val valueAfter: Int = ((newValue.toString()).toInt())*10
                Log.d("ManualfadeSeekbar","valueAfter: $valueAfter")

                preference.summary=valueAfter.toString()+"ms"
                manualFadeTime=valueAfter
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putInt("manualFadeTime", valueAfter)
                editor.commit()
                sIntent.putExtra("manualCrossFlag",true)
                true
            }

            val fadeSeekbar : SeekBarPreference = findPreference<Preference>("fade_seekbar") as SeekBarPreference
            fadeSeekbar.summary= fadeTime.toString()+"ms"
            fadeSeekbar.value= fadeTime/10
            fadeSeekbar.updatesContinuously=true

            fadeSeekbar.setOnPreferenceChangeListener { preference, newValue ->
                preference as SeekBarPreference
                Log.d("fadeSeekbar","ValueBefore:$newValue")
                val valueAfter: Int = ((newValue.toString()).toInt())*10
                Log.d("FadeSeekbar","valueAfter: $valueAfter")

                preference.summary=valueAfter.toString()+"ms"
                fadeTime=valueAfter
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putInt("fadeTime", valueAfter)
                editor.commit()
                sIntent.putExtra("fadeTimeFlag",true)
                true
            }


            val resetCrossfade:Preference = findPreference<Preference>("reset_crossfade_pref") as Preference
            resetCrossfade.setOnPreferenceClickListener{
                val dialog: AlertDialog
                val builder = AlertDialog.Builder(requireContext(), R.style.myDialog)
                builder.setTitle("Restore Defaults")
                builder.setMessage("All options on this page will be restored to the default values")
                builder.setPositiveButton("RESET") { dialogs, _ ->
                    dialogs.cancel()
                    val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                    val editor = sharedPreference.edit()
                    editor.putInt("crossfadeTime", 15000)
                    editor.putInt("manualFadeTime", 400)
                    editor.putInt("fadeTime", 700)
                    editor.putBoolean("mCrossFade", true)
                    editor.putBoolean("FadeInFadeOut", true)
                    editor.commit()
                    crossFadeStatus = true
                    playPauseFade = true
                    crossfadeSwitch.isChecked=true
                    fadeSwitch.isChecked=true
                    crossfadeTime=15000
                    manualFadeTime=400
                    fadeTime=700
                    fadeSeekbar.summary= fadeTime.toString()+"ms"
                    manualFadeSeekbar.summary= manualFadeTime.toString()+"ms"
                    crossfadeSeekbar.summary= crossfadeTime.toString()+"ms"
                    crossfadeSeekbar.value= crossfadeTime/250
                    manualFadeSeekbar.value= manualFadeTime/10
                    fadeSeekbar.value= fadeTime/10

                    Log.d("scuccesfully Reset", "onPreferenceClick: ResetSuccessful")

                    SonaToast.setToast(requireContext(), "Setting Reset to defaults", 0)
                }
                builder.setNegativeButton("CANCEL") { dialogs, _ ->
                    dialogs.cancel()
                    Log.d("Cancel Reset", "onPreferenceClick: ResetCancelled")
                }

                dialog = builder.create()
                dialog.window?.attributes?.windowAnimations = R.style.dialog_animation
                //builder.show();
                dialog.show()

                true
            }

        }
    }


    class AutoPlayPreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.autoplay_settings, rootKey)
        }

        override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
            return  CubeAnimation.create(CubeAnimation.RIGHT, enter, 500)
        }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setDivider(ColorDrawable(Color.DKGRAY))
            setDividerHeight(2)

            val autoplaySwitch: SwitchPreferenceCompat = findPreference<Preference>("auto_play") as SwitchPreferenceCompat
            autoplaySwitch.isChecked = autoPlay

            autoplaySwitch.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("autoPlay", switched)
                editor.commit()
                autoPlay = switched
                sIntent.putExtra("autoPlayFlag", true)
                if (switched) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        SonaToast.setToast(requireContext(), "A notification will be always shown while running this feature", 1)

                    } else {
                        SonaToast.setToast(requireContext(), "Auto play enabled", 0)
                    }
                } else {
                    SonaToast.setToast(requireContext(), "Auto play disabled", 0)
                }
                Log.d("AutoPlay", "onPreferenceChange:AutoPlayStatus: $switched")


                true
            }


            val autoResumeSwitch: SwitchPreferenceCompat = findPreference<Preference>("auto_resume") as SwitchPreferenceCompat
            autoResumeSwitch.isChecked = autoResume

            autoResumeSwitch.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("autoResume", switched)
                editor.commit()
                autoResume = switched
                sIntent.putExtra("autoResumeFlag", true)
                sIntent.putExtra("autoResume", switched)
                if (switched) {
                    SonaToast.setToast(requireContext(), "Auto resume enabled", 0)
                } else {
                    SonaToast.setToast(requireContext(), "Auto resume disabled", 0)
                }
                Log.d("AutoResume", "onPreferenceChange:AutoResumeStatus: $switched")

                true
            }


            val autoPlayWakelock: SwitchPreferenceCompat = findPreference<Preference>("wake_lock") as SwitchPreferenceCompat
            autoPlayWakelock.isChecked = wakelockx

            autoPlayWakelock.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("wakeFlag", switched)
                editor.commit()
                wakelockx = switched
                if (switched) {
                    SonaToast.setToast(requireContext(), "WARNING: WakeLock will drain device battery faster! Must Use Power Saver AutoStop Feature", 1)
                } else {
                    SonaToast.setToast(requireContext(), "WakeLock disabled", 0)
                }
                Log.d("WakeLock", "onPreferenceChange:WakelockStatus: $switched")

                true
            }


            val playerWakeSwitch: SwitchPreferenceCompat = findPreference<Preference>("player_wake_lock") as SwitchPreferenceCompat
            playerWakeSwitch.isChecked = playerWakelock
            playerWakeSwitch.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("playerWakeLock", switched)
                editor.commit()
                playerWakelock = switched
                sIntent.putExtra("playerWakeLock", switched)
                sIntent.putExtra("playerWakeFlag", true)
                if (switched) {
                    SonaToast.setToast(requireContext(), "WARNING: WakeLock will drain device battery faster! Must Use Power Saver AutoStop Feature", 1)
                } else {
                    SonaToast.setToast(requireContext(), "Auto Resume WakeLock Disabled", 0)
                }
                Log.d("PlayerWakeSetting", "onPreferenceChange:PlayerWakeLockStatus: $switched")

                true
            }

            val notyFlagSwitch: SwitchPreferenceCompat = findPreference<Preference>("noti_switch") as SwitchPreferenceCompat

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notyFlagSwitch.isChecked = true
                notyFlagSwitch.isEnabled = false
               notyFlagSwitch.summary="notification cannot be disabled in Android Oreo+ devices"
            } else {
                notyFlagSwitch.isChecked = notyFlag
                notyFlagSwitch.setOnPreferenceChangeListener { preference, _ ->
                    Vibration.vibrate(50)
                    preference as SwitchPreferenceCompat
                    val switched = !preference.isChecked
                    val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                    val editor = sharedPreference.edit()
                    editor.putBoolean("notiFlag", switched)
                    editor.commit()
                    notyFlag = switched
                    if (switched) {
                        SonaToast.setToast(requireContext(), "Auto Play Notification Enabled", 0)
                    } else {
                        SonaToast.setToast(requireContext(), "Auto Play Notification Disabled", 0)
                    }
                    Log.d("AutoPlay Notification", "onPreferenceChange:NotificationStatus: $switched")

                    true
                }
            }
        }
    }

            class AudioFocusPreferencesFragment : PreferenceFragmentCompat() {
                override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
                    setPreferencesFromResource(R.xml.audiofocus_settings, rootKey)

                }
                override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
                    return  MoveAnimation.create(MoveAnimation.RIGHT, enter, 500)

                }

                override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                    super.onViewCreated(view, savedInstanceState)
                    setDivider(ColorDrawable(Color.DKGRAY))
                    setDividerHeight(2)

                    val permaAudioFocus: SwitchPreferenceCompat = findPreference<Preference>("pref_focus_p") as SwitchPreferenceCompat
                    permaAudioFocus.isChecked= pFocuspause

                    permaAudioFocus.setOnPreferenceChangeListener { preference, _ ->
                        Vibration.vibrate(50)
                        preference as SwitchPreferenceCompat
                        val switched = !preference.isChecked
                        val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                        val editor = sharedPreference.edit()
                        editor.putBoolean("pFocusPause", switched)
                        editor.commit()
                        pFocuspause = switched
                        if (switched) {
                            SonaToast.setToast(requireContext(), "Resume After Permanent Audio Focus Gain Enabled", 0)
                        } else {
                            SonaToast.setToast(requireContext(), "Resume After Permanent Audio Focus Gain Disabled", 0)
                        }
                        Log.d("PermanentAudioFocus", "onPreferenceChange:PAudioFocus: $switched")


                        true}

                    val transAudioFocus: SwitchPreferenceCompat = findPreference<Preference>("pref_focus_t") as SwitchPreferenceCompat
                    transAudioFocus.isChecked= pFocuspause

                    transAudioFocus.setOnPreferenceChangeListener { preference, _ ->
                        Vibration.vibrate(50)
                        preference as SwitchPreferenceCompat
                        val switched = !preference.isChecked
                        val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                        val editor = sharedPreference.edit()
                        editor.putBoolean("tFocusPause", switched)
                        editor.commit()
                        tFocuspause = switched
                        if (switched) {
                            SonaToast.setToast(requireContext(), "Pause on Transient Audio Focus Loss Enabled", 0)
                        } else {
                            SonaToast.setToast(requireContext(), "Pause on Transient Audio Focus Loss Disabled", 0)
                        }
                        Log.d("TransientAudioFocus", "onPreferenceChange:TAudioFocus: $switched")


                        true}


                    val canDuckSwitch: SwitchPreferenceCompat = findPreference<Preference>("pref_can_duck") as SwitchPreferenceCompat
                    canDuckSwitch.isChecked= canduck

                    canDuckSwitch.setOnPreferenceChangeListener { preference, _ ->
                        Vibration.vibrate(50)
                        preference as SwitchPreferenceCompat
                        val switched = !preference.isChecked
                        val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                        val editor = sharedPreference.edit()
                        editor.putBoolean("canDuck", switched)
                        editor.commit()
                        canduck = switched
                        if (switched) {
                            SonaToast.setToast(requireContext(), "Duck Volume Enabled", 0)
                        } else {
                            SonaToast.setToast(requireContext(), "Duck Volume Disabled", 0)
                        }
                        Log.d("CanDuckAudioFocus", "onPreferenceChange:DuckAudioFocus: $switched")


                        true}
                }
        }

    class PowerPreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.power_settings, rootKey)

        }
        override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
            return SidesAnimation.create(SidesAnimation.LEFT, enter, 500)

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setDivider(ColorDrawable(Color.DKGRAY))
            setDividerHeight(2)


            val keepScreenSwitch: SwitchPreferenceCompat = findPreference<Preference>("keep_screen_on") as SwitchPreferenceCompat
            keepScreenSwitch.isChecked= keepScreenOn
            keepScreenSwitch.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("keepScreenOn", switched)
                editor.commit()
                sIntent.putExtra("screenStatus", switched)
                sIntent.putExtra("screenOnFlag", true)
                keepScreenOn = switched
                if (switched) {
                    SonaToast.setToast(requireContext(), "Keep Screen On Enabled", 0)
                } else {
                    SonaToast.setToast(requireContext(), "Keep Screen On Disabled", 0)
                }
                Log.d("KeepScreenOn", "onPreferenceChange:ScreenOn: $switched")


                true}


            val autoStopSwitch: SwitchPreferenceCompat = findPreference<Preference>("pref_power_saver") as SwitchPreferenceCompat
            autoStopSwitch.isChecked= isPowerEnabled
            autoStopSwitch.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("powerFlag", switched)
                editor.commit()
                sIntent.putExtra("powerFlag", true)
                isPowerEnabled = switched
                if (switched) {
                    SonaToast.setToast(requireContext(), "Auto Stop Enabled", 0)
                } else {
                    SonaToast.setToast(requireContext(), "Auto Stop Disabled", 0)
                }
                Log.d("PowerSaver", "onPreferenceChange:AutoStop: $switched")


                true}


            val autoStopSeekbar : SeekBarPreference = findPreference<Preference>("power_seekbar") as SeekBarPreference
            autoStopSeekbar.summary= "$powerSeekLevel%"
            autoStopSeekbar.value= powerSeekLevel*2
            autoStopSeekbar.updatesContinuously=true

            autoStopSeekbar.setOnPreferenceChangeListener { preference, newValue ->
                preference as SeekBarPreference
                Log.d("AutoStopSeekbar","ValueBefore:$newValue")
                val valueAfter: Int = ((newValue.toString()).toInt())/2
                Log.d("AutoStopSeekbar","valueAfter: $valueAfter")

                preference.summary= "$valueAfter%"
                powerSeekLevel=valueAfter
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putInt("batteryLevel", valueAfter)
                editor.commit()
                sIntent.putExtra("powerFlag",true)
                true
            }


            val autoSleepSwitch: SwitchPreferenceCompat = findPreference<Preference>("pref_auto_sleep") as SwitchPreferenceCompat
            autoSleepSwitch.isChecked= isAutoSleep
            autoSleepSwitch.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("isAutoSleep", switched)
                editor.commit()
                sIntent.putExtra("powerFlag", true)
                isAutoSleep = switched
                if (switched) {
                    SonaToast.setToast(requireContext(), "Auto Release Wakelock Enabled", 0)
                } else {
                    SonaToast.setToast(requireContext(), "Auto Release Wakelock Disabled", 0)
                }
                Log.d("AllowSleep", "onPreferenceChange:AutoSleep: $switched")


                true}

            val autoSleepSeekbar : SeekBarPreference = findPreference<Preference>("sleep_seekbar") as SeekBarPreference
            autoSleepSeekbar.summary= "$sleepSeekLevel%"
            autoSleepSeekbar.value= sleepSeekLevel*2
            autoSleepSeekbar.updatesContinuously=true

            autoSleepSeekbar.setOnPreferenceChangeListener { preference, newValue ->
                preference as SeekBarPreference
                Log.d("AutoSleepSeekbar","ValueBefore:$newValue")
                val valueAfter: Int = ((newValue.toString()).toInt())/2
                Log.d("AutoSleepSeekbar","valueAfter: $valueAfter")

                preference.summary= "$valueAfter%"
                sleepSeekLevel=valueAfter
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putInt("sleepBatteryLevel", valueAfter)
                editor.commit()
                sIntent.putExtra("powerFlag",true)
                true
            }
        }
    }

    class MiscPreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.misc_settings, rootKey)

        }
        override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
            return FlipAnimation.create(FlipAnimation.RIGHT, enter, 500)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setDivider(ColorDrawable(Color.DKGRAY))
            setDividerHeight(2)

            val listAnimSwitch: SwitchPreferenceCompat = findPreference<Preference>("list_animation") as SwitchPreferenceCompat
            listAnimSwitch.isChecked= listAnimation

            listAnimSwitch.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("isAnimEnabled", switched)
                editor.commit()
               listAnimation = switched
                if (switched) {
                    SonaToast.setToast(requireContext(), "Playlist Animation Enabled", 0)
                } else {
                    SonaToast.setToast(requireContext(), "Playlist Animation Disabled", 0)
                }
                Log.d("PlayListAnimation", "onPreferenceChange:PlaylistAnimation: $switched")


                true}


            val autoScrollTitle: SwitchPreferenceCompat = findPreference<Preference>("auto_scroll_title") as SwitchPreferenceCompat
            autoScrollTitle.isChecked= marqueeFlag

            autoScrollTitle.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("marqueeFlag", switched)
                editor.commit()
                marqueeFlag = switched
                if (switched) {
                    SonaToast.setToast(requireContext(), "Auto Scroll Song Title Enabled", 0)
                } else {
                    SonaToast.setToast(requireContext(), "Auto Scroll Song Title Disabled", 0)
                }
                Log.d("AutoScrollTitle", "onPreferenceChange:Status: $switched")


                true}


            val alterEqModeSwitch: SwitchPreferenceCompat = findPreference<Preference>("alter_eq_mode") as SwitchPreferenceCompat
            alterEqModeSwitch.isChecked= alterEqMode

            alterEqModeSwitch.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("alterEqMode", switched)
                editor.commit()
                alterEqMode = switched
                sIntent.putExtra("alterEqMode", switched)
                sIntent.putExtra("AlterEqFlag", true)
                if (switched) {
                    SonaToast.setToast(requireContext(), "Warning : Use only when BassBoost failed to work", 1)
                } else {
                    SonaToast.setToast(requireContext(), "Alter Equalizer Mode Disabled", 0)
                }
                Log.d("AlterEqMode", "onPreferenceChange:Status: $switched")


                true}



            val colorizedMode: SwitchPreferenceCompat = findPreference<Preference>("colorized_mode") as SwitchPreferenceCompat
            colorizedMode.isChecked= colorized

            colorizedMode.setOnPreferenceChangeListener { preference, _ ->
                Vibration.vibrate(50)
                preference as SwitchPreferenceCompat
                val switched = !preference.isChecked
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putBoolean("colorized", switched)
                editor.commit()
                colorized = switched
                if (switched) {
                    SonaToast.setToast(requireContext(), "Colorized Advanced Notification Enabled", 0)
                } else {
                    SonaToast.setToast(requireContext(), "Colorized Advanced Notification Disabled", 0)
                }
                Log.d("ColorizedMode", "onPreferenceChange:Status: $switched")


                true}


            val windowEnterAnim : DropDownPreference = findPreference<Preference>("window_enter_anim") as DropDownPreference
            windowEnterAnim.value = enterAnim.toString()
            windowEnterAnim.summary= animList[enterAnim]

            windowEnterAnim.setOnPreferenceChangeListener { preference, newValue ->
                preference as DropDownPreference
                val stringValue = newValue.toString()
                val index = preference.findIndexOfValue(stringValue)
                preference.summary=preference.entries[index]
                enterAnim=stringValue.toInt()
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putInt("enterAnim", enterAnim)
                editor.commit()

                sIntent.putExtra("WindowAnim", true)
                Log.d("EnterAnimValue", "$enterAnim")
                true

            }

            val windowExitAnim : DropDownPreference = findPreference<Preference>("window_exit_anim") as DropDownPreference
            windowExitAnim.value = exitAnim.toString()
            windowExitAnim.summary= animList[exitAnim]

            windowExitAnim.setOnPreferenceChangeListener { preference, newValue ->
                preference as DropDownPreference
                val stringValue = newValue.toString()
                val index = preference.findIndexOfValue(stringValue)
                preference.summary=preference.entries[index]
                exitAnim=stringValue.toInt()
                val sharedPreference = requireContext().getSharedPreferences("com.McDevelopers.sonaplayer", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putInt("exitAnim", exitAnim)
                editor.commit()

                sIntent.putExtra("WindowAnim", true)
                Log.d("ExitAnimValue", "$enterAnim")
                true

            }

        }
    }


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
    override fun onBackPressed() {

        setResult(Activity.RESULT_OK, sIntent)
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
    }


}

