package com.ziedkhmiri.custom;

/**
 * Created by zied216 on 26/02/2017.
 */

import android.support.v4.view.ViewPager;
import android.view.View;


public class StackPageTransformer implements ViewPager.PageTransformer {
    private float mMinScale;
    private float mMaxScale;
    private int mStackCount;

    private float mPowBase;


    public StackPageTransformer(ViewPager viewPager, float minScale, float maxScale, int stackCount) {
        viewPager.setOffscreenPageLimit(stackCount);
        mMinScale = minScale;
        mMaxScale = maxScale;
        mStackCount = stackCount;

        if(mMaxScale < mMinScale)
            throw new IllegalArgumentException("The Argument: maxScale must bigger than minScale !");
        mPowBase = (float) Math.pow(mMinScale/mMaxScale, 1.0f/mStackCount);
    }

    public StackPageTransformer(ViewPager viewPager) {
        this(viewPager, 0.8f, 0.9f, 5);
    }

    public final void transformPage(View view, float position) {

        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        view.setPivotX(pageWidth/2);
        view.setPivotY(0);

        float bottomPos = mStackCount-1;

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            view.setAlpha(1);

            view.setTranslationX(0);
            view.setScaleX(mMaxScale);
            view.setScaleY(mMaxScale);

            if(!view.isClickable())
                view.setClickable(true);

        } else if (position <= bottomPos) { // (0, mStackCount-1]
            int index = (int)position;
            float minScale = mMaxScale * (float) Math.pow(mPowBase, index+1);
            float maxScale = mMaxScale * (float) Math.pow(mPowBase, index);
            float curScale = mMaxScale * (float) Math.pow(mPowBase, position);

            // Fade the page out.
            view.setAlpha(1);

            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);
            view.setTranslationY(pageHeight * (1-curScale)
                    - pageHeight * (1-mMaxScale) * (bottomPos-position) / bottomPos);

            // Scale the page down (between minScale and maxScale)
            float scaleFactor = minScale
                    + (maxScale - minScale) * (1 - Math.abs(position - index));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            if(position == 1 && view.isClickable())
                view.setClickable(false);

        } else { // (mStackCount-1, +Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}
