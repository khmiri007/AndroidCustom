package com.ziedkhmiri.custom.cardview;

/**
 * Created by zied khmiri on 28/10/2016.
 */
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

class CardViewJellybeanMr1 extends CardViewEclairMr1 {

    @Override
    public void initStatic() {
        OptRoundRectDrawableWithShadow.sRoundRectHelper
                = new OptRoundRectDrawableWithShadow.RoundRectHelper() {
            @Override
            public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
                                      Paint paint) {
                canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
            }
        };
    }
}
