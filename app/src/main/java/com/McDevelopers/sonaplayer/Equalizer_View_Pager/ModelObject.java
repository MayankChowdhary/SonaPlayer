package com.McDevelopers.sonaplayer.Equalizer_View_Pager;

import com.McDevelopers.sonaplayer.R;

public enum ModelObject {

    BASS("BassPage", R.layout.bass_layout),
    VOLUME("VolumePage", R.layout.volume_layout),
    CHANNEL("ChannelPage", R.layout.channel_layout);

    private String mTitleResId;
    private int mLayoutResId;

    ModelObject(String titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public String getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}