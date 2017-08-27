package com.zir.upuptoyou;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 * Created by zirco on 17-8-26.
 */

public class DrawView extends View {

    private int Width;  //px
    private int Height; //px

    final private int outputWidth = 500; //px
    final private int outputHeight = 500; //px

    Bitmap watermark_bitmap;
    Rect watermark_dst;

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        watermark_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.watermark);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Height = this.getHeight();
        Width = this.getHeight();
        Log.d("onSizeChanged", Integer.toString(Width) + "*" + Integer.toString(Height));
        watermark_dst = new Rect((int) (10.0 / (float) outputWidth * Width), (int) (8 / (float) outputHeight * Height), (int) ((101 + 10.0) / (float) outputWidth * Width), (int) ((8.0 + 45) / (float) outputHeight * Height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //watermark
        canvas.drawBitmap(watermark_bitmap, null, watermark_dst, null);


    }


}
