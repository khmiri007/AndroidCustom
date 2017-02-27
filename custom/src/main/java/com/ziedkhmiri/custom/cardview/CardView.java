package com.ziedkhmiri.custom.cardview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;

import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ziedkhmiri.custom.R;


public class CardView extends FrameLayout implements CardViewDelegate {

    private static final CardViewImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IMPL = new CardViewApi21();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            IMPL = new CardViewJellybeanMr1();
        } else {
            IMPL = new CardViewEclairMr1();
        }
        IMPL.initStatic();
    }

    private boolean mCompatPadding;

    private boolean mPreventCornerOverlap;

    private final Rect mContentPadding = new Rect();

    private final Rect mShadowBounds = new Rect();


    public CardView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        // NO OP
    }

    public void setPaddingRelative(int start, int top, int end, int bottom) {
        // NO OP
    }


    @Override
    public boolean getUseCompatPadding() {
        return mCompatPadding;
    }


    public void setUseCompatPadding(boolean useCompatPadding) {
        if (mCompatPadding == useCompatPadding) {
            return;
        }
        mCompatPadding = useCompatPadding;
        IMPL.onCompatPaddingChanged(this);
    }


    public void setContentPadding(int left, int top, int right, int bottom) {
        mContentPadding.set(left, top, right, bottom);
        IMPL.updatePadding(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (IMPL instanceof CardViewApi21 == false) {
            final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            switch (widthMode) {
                case MeasureSpec.EXACTLY:
                case MeasureSpec.AT_MOST:
                    final int minWidth = (int) Math.ceil(IMPL.getMinWidth(this));
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minWidth,
                            MeasureSpec.getSize(widthMeasureSpec)), widthMode);
                    break;
            }

            final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            switch (heightMode) {
                case MeasureSpec.EXACTLY:
                case MeasureSpec.AT_MOST:
                    final int minHeight = (int) Math.ceil(IMPL.getMinHeight(this));
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minHeight,
                            MeasureSpec.getSize(heightMeasureSpec)), heightMode);
                    break;
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardView, defStyleAttr,
                R.style.CardView_Light);
        int backgroundColor = a.getColor(R.styleable.CardView_BackgroundColor, 0);
        float radius = a.getDimension(R.styleable.CardView_CornerRadius, 0);
        float elevation = a.getDimension(R.styleable.CardView_Elevation, 0);
        float maxElevation = a.getDimension(R.styleable.CardView_MaxElevation, 0);
        mCompatPadding = a.getBoolean(R.styleable.CardView_UseCompatPadding, false);
        mPreventCornerOverlap = a.getBoolean(R.styleable.CardView_PreventCornerOverlap, true);
        int defaultPadding = a.getDimensionPixelSize(R.styleable.CardView_ContentPadding, 0);
        mContentPadding.left = a.getDimensionPixelSize(R.styleable.CardView_ContentPaddingLeft,
                defaultPadding);
        mContentPadding.top = a.getDimensionPixelSize(R.styleable.CardView_ContentPaddingTop,
                defaultPadding);
        mContentPadding.right = a.getDimensionPixelSize(R.styleable.CardView_ContentPaddingRight,
                defaultPadding);
        mContentPadding.bottom = a.getDimensionPixelSize(R.styleable.CardView_ContentPaddingBottom,
                defaultPadding);
        if (elevation > maxElevation) {
            maxElevation = elevation;
        }

        int cornerFlag = 0;
        boolean flag = a.getBoolean(R.styleable.CardView_LeftTopCorner, true);
        cornerFlag += flag ? OptRoundRectDrawable.FLAG_LEFT_TOP_CORNER : 0;

        flag = a.getBoolean(R.styleable.CardView_RightTopCorner, true);
        cornerFlag += flag ? OptRoundRectDrawable.FLAG_RIGHT_TOP_CORNER : 0;

        flag = a.getBoolean(R.styleable.CardView_LeftBottomCorner, true);
        cornerFlag += flag ? OptRoundRectDrawable.FLAG_LEFT_BOTTOM_CORNER : 0;

        flag = a.getBoolean(R.styleable.CardView_RightBottomCorner, true);
        cornerFlag += flag ? OptRoundRectDrawable.FLAG_RIGHT_BOTTOM_CORNER : 0;

        int edgesFlag = 0;
        flag = a.getBoolean(R.styleable.CardView_LeftEdges, true);
        edgesFlag += flag ? OptRoundRectDrawableWithShadow.FLAG_LEFT_EDGES : 0;

        flag = a.getBoolean(R.styleable.CardView_CardTopEdges, true);
        edgesFlag += flag ? OptRoundRectDrawableWithShadow.FLAG_TOP_EDGES : 0;

        flag = a.getBoolean(R.styleable.CardView_RightEdges, true);
        edgesFlag += flag ? OptRoundRectDrawableWithShadow.FLAG_RIGHT_EDGES : 0;

        flag = a.getBoolean(R.styleable.CardView_BottomEdges, true);
        edgesFlag += flag ? OptRoundRectDrawableWithShadow.FLAG_BOTTOM_EDGES : 0;

        a.recycle();

        IMPL.initialize(this, context, backgroundColor, radius, elevation, maxElevation, cornerFlag, edgesFlag);
    }


    public void setCardBackgroundColor(int color) {
        IMPL.setBackgroundColor(this, color);
    }


    public int getContentPaddingLeft() {
        return mContentPadding.left;
    }


    public int getContentPaddingRight() {
        return mContentPadding.right;
    }


    public int getContentPaddingTop() {
        return mContentPadding.top;
    }


    public int getContentPaddingBottom() {
        return mContentPadding.bottom;
    }


    public void setRadius(float radius) {
        IMPL.setRadius(this, radius);
    }


    public float getRadius() {
        return IMPL.getRadius(this);
    }


    @Override
    public void setShadowPadding(int left, int top, int right, int bottom) {
        mShadowBounds.set(left, top, right, bottom);
        super.setPadding(left + mContentPadding.left, top + mContentPadding.top,
                right + mContentPadding.right, bottom + mContentPadding.bottom);
    }


    public void setCardElevation(float elevation) {
        IMPL.setElevation(this, elevation);
    }


    public float getCardElevation() {
        return IMPL.getElevation(this);
    }


    public void setMaxCardElevation(float maxElevation) {
        IMPL.setMaxElevation(this, maxElevation);
    }


    public float getMaxCardElevation() {
        return IMPL.getMaxElevation(this);
    }


    @Override
    public boolean getPreventCornerOverlap() {
        return mPreventCornerOverlap;
    }


    public void setPreventCornerOverlap(boolean preventCornerOverlap) {
        if (preventCornerOverlap == mPreventCornerOverlap) {
            return;
        }
        mPreventCornerOverlap = preventCornerOverlap;
        IMPL.onPreventCornerOverlapChanged(this);
    }

    private static final boolean SDK_LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    /**
     * show corner or rect
     */
    public void showCorner(boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom){
        if (SDK_LOLLIPOP) {
            ((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        }
    }

    public void showLeftTopCorner(boolean show){
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showLeftTopRect(!show);
        }
    }

    public void showRightTopCorner(boolean show){
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showRightTopRect(!show);
        }
    }

    public void showLeftBottomCorner(boolean show){
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showLeftBottomRect(!show);
        }
    }

    public void showRightBottomCorner(boolean show){
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showRightBottomRect(!show);
        }
    }

    /**
     * show Edge Shadow
     */
    public void showEdgeShadow(boolean left, boolean top, boolean right, boolean bottom){
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showEdgeShadow(left, top, right, bottom);
        }
    }

    public void showLeftEdgeShadow(boolean show){
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showLeftEdgeShadow(show);
        }
    }
    public void showTopEdgeShadow(boolean show){
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showTopEdgeShadow(show);
        }
    }
    public void showRightEdgeShadow(boolean show){
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showRightEdgeShadow(show);
        }
    }
    public void showBottomEdgeShadow(boolean show){
        if (SDK_LOLLIPOP) {
            //((OptRoundRectDrawable) getBackground()).showCorner(leftTop, rightTop, leftBottom, rightBottom);
        } else {
            ((OptRoundRectDrawableWithShadow) getBackground()).showBottomEdgeShadow(show);
        }
    }
}
