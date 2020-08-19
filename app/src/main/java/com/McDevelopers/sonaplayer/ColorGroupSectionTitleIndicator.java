package com.McDevelopers.sonaplayer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;


import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;

/**
 * Indicator for sections of type
 */
public class ColorGroupSectionTitleIndicator extends SectionTitleIndicator<Data> {

    public ColorGroupSectionTitleIndicator(Context context) {
        super(context);
    }

    public ColorGroupSectionTitleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorGroupSectionTitleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(Data dataGroup) {
        // Example of using a single character
        setTitleText(dataGroup.title.toUpperCase().charAt(0)+ "");
        // Example of using a longer string
        // setTitleText(colorGroup.getName());

        setIndicatorTextColor(Color.BLUE);
    }

}