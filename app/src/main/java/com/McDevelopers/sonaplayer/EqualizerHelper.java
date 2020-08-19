package com.McDevelopers.sonaplayer;

import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;

/**
 * Equalizer helper class.
 *
 * @author Saravan Pantham
 *
 */
public class EqualizerHelper {

    //Context and helper objects.

    //Equalizer objects.
    private Equalizer mEqualizer;
    private Equalizer mEqualizer2;
    private Virtualizer mVirtualizer;
    private Virtualizer mVirtualizer2;
    private BassBoost mBassBoost;
    private BassBoost mBassBoost2;
    private PresetReverb mReverb;
    private PresetReverb mReverb2;
    private LoudnessEnhancer loudnessEnhancer;
    private LoudnessEnhancer loudnessEnhancer2;



    public EqualizerHelper( int audioSessionId1,
                           int audioSessionId2, boolean equalizerEnabled) {

        //Context and helper objects.

        //Init mMediaPlayer's equalizer engine.
        mEqualizer = new Equalizer(0, audioSessionId1);
        mEqualizer.setEnabled(equalizerEnabled);

        //Init mMediaPlayer2's equalizer engine.
        mEqualizer2 = new Equalizer(0, audioSessionId2);
        mEqualizer2.setEnabled(equalizerEnabled);

        //Init mMediaPlayer's virtualizer engine.
        mVirtualizer = new Virtualizer(0, audioSessionId1);
        mVirtualizer.setEnabled(equalizerEnabled);

        //Init mMediaPlayer2's virtualizer engine.
        mVirtualizer2 = new Virtualizer(0, audioSessionId2);
        mVirtualizer2.setEnabled(equalizerEnabled);

        //Init mMediaPlayer's bass boost engine.
        mBassBoost = new BassBoost(0, audioSessionId1);
        mBassBoost.setEnabled(equalizerEnabled);

        //Init mMediaPlayer2's bass boost engine.
        mBassBoost2 = new BassBoost(0, audioSessionId2);
        mBassBoost2.setEnabled(equalizerEnabled);

        //Init mMediaPlayer's reverb engine.
        mReverb = new PresetReverb(0, audioSessionId1);
        mReverb.setEnabled(equalizerEnabled);

        //Init mMediaPlayer's reverb engine.
        mReverb2 = new PresetReverb(0, audioSessionId2);
        mReverb2.setEnabled(equalizerEnabled);

        loudnessEnhancer=new LoudnessEnhancer(audioSessionId1);
        loudnessEnhancer.setEnabled(equalizerEnabled);

        loudnessEnhancer2=new LoudnessEnhancer(audioSessionId2);
        loudnessEnhancer2.setEnabled(equalizerEnabled);

    }

    /**
     * Releases all EQ objects and sets their references to null.
     */
    public void releaseEQObjects() throws Exception {
        mEqualizer.release();
        mEqualizer2.release();
        mVirtualizer.release();
        mVirtualizer2.release();
        mBassBoost.release();
        mBassBoost2.release();
        mReverb.release();
        mReverb2.release();
        loudnessEnhancer.release();
        loudnessEnhancer2.release();

        mEqualizer = null;
        mEqualizer2 = null;
        mVirtualizer = null;
        mVirtualizer2 = null;
        mBassBoost = null;
        mBassBoost2 = null;
        mReverb = null;
        mReverb2 = null;
        loudnessEnhancer2=null;
        loudnessEnhancer=null;

    }

    /*
     * Getter methods.
     */

    public void disableEq(boolean isFirstMedia){

        if(isFirstMedia){

           mEqualizer2.setEnabled(false);
            mBassBoost2.setEnabled(false);
            mVirtualizer2.setEnabled(false);
           mReverb2.setEnabled(false);
          loudnessEnhancer2.setEnabled(false);

        }else {

           mEqualizer.setEnabled(false);
            mBassBoost.setEnabled(false);
            mVirtualizer.setEnabled(false);
            mReverb.setEnabled(false);
            loudnessEnhancer.setEnabled(false);
        }

    }

    public void EnableEq(boolean isFirstMedia){

        if(isFirstMedia){

            mEqualizer.setEnabled(true);
            mVirtualizer.setEnabled(true);
           mBassBoost.setEnabled(true);
            mReverb.setEnabled(true);
            loudnessEnhancer.setEnabled(true);

        }else {

           mEqualizer2.setEnabled(true);
            mVirtualizer2.setEnabled(true);
            mBassBoost2.setEnabled(true);
            mReverb2.setEnabled(true);
            loudnessEnhancer2.setEnabled(true);
        }

    }



    public Equalizer getEqualizer() {
        return mEqualizer;
    }

    public Equalizer getEqualizer2() {
        return mEqualizer2;
    }

    public Virtualizer getVirtualizer() {
        return mVirtualizer;
    }

    public Virtualizer getVirtualizer2() {
        return mVirtualizer2;
    }

    public BassBoost getBassBoost() {
        return mBassBoost;
    }

    public BassBoost getBassBoost2() {
        return mBassBoost2;
    }


    public PresetReverb getReverb() {
        return mReverb;
    }

    public PresetReverb getReverb2() {
        return mReverb2;
    }

    public LoudnessEnhancer getLoudnessEnhancer() {
        return loudnessEnhancer;
    }
    public LoudnessEnhancer getLoudnessEnhancer2() {
        return loudnessEnhancer2;
    }

}