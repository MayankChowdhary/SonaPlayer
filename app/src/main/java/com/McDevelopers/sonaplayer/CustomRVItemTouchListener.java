package com.McDevelopers.sonaplayer;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CustomRVItemTouchListener implements RecyclerView.OnItemTouchListener {

    //GestureDetector to intercept touch events
    GestureDetector gestureDetector;
    private ClickListener clickListener;

    public CustomRVItemTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                final ViewGroup childViewGroup = (ViewGroup) recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (childViewGroup != null && clickListener != null) {
                    final List<View> viewHierarchy = new ArrayList<View>();
                    // Important: x and y are raw screen coordinates here
                    getViewHierarchyUnderChild(childViewGroup, e.getRawX(), e.getRawY(), viewHierarchy);
                    View touchedView = childViewGroup;
                    if (viewHierarchy.size() > 0) {
                        touchedView = viewHierarchy.get(0);
                    }
                    clickListener.onClick(touchedView, recyclerView.getChildLayoutPosition(childViewGroup));
                    return false;
                }
                else
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                //find the long pressed view
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {

        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, recyclerView.getChildLayoutPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


    public void getViewHierarchyUnderChild(ViewGroup root, float x, float y, List<View> viewHierarchy) {
        int[] location = new int[2];
        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; ++i) {
            final View child = root.getChildAt(i);
            child.getLocationOnScreen(location);
            final int childLeft = location[0], childRight = childLeft + child.getWidth();
            final int childTop = location[1], childBottom = childTop + child.getHeight();

            if (child.isShown() && x >= childLeft && x <= childRight && y >= childTop && y <= childBottom) {
                viewHierarchy.add(0, child);
            }
            if (child instanceof ViewGroup) {
                getViewHierarchyUnderChild((ViewGroup) child, x, y, viewHierarchy);
            }
        }
    }
}