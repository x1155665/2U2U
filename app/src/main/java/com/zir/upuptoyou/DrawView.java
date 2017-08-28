package com.zir.upuptoyou;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;


/**
 * Created by zirco on 17-8-26.
 */

public class DrawView extends View {

    private int Width;  //px
    private int Height; //px

    final private int outputWidth = 500; //px
    final private int outputHeight = 500; //px

    final int startX = 120;
    final int startY = 40;
    final int gapx = 50;
    final int gapY = 22;
    final int peopleWidth = 76;
    final int peopleHeight = 149;
    final int linebreakX = -52;
    final int linebreakY = 36;

    Bitmap watermark_bitmap;
    Rect watermark_dst;

    Bitmap person;
    Rect person_dst;
    String words;

    Canvas canvas;

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        watermark_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
        words="";
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
        this.canvas=canvas;
        //watermark
        canvas.drawBitmap(watermark_bitmap, null, watermark_dst, null);
        Log.d("ondraw","canvaswidth: "+Integer.toString(canvas.getWidth()));

        //draw people
        Log.d("drawPeople", "start drawpeople");
        int x = startX ;
        int y = startY ;
        int line = 0;
        for (int i = 0; i < words.length(); i++)
            if (words.charAt(i) != '\n') {

                //load image
                //Todo: trade ram for speed by loading all bitmaps in constructor?
                int id = 1 + (int) (Math.random() * 25);
                Log.d("Drawpeople","id:"+Integer.toString(id));
                int resId;
                if(id<10)
                    resId= getResId("p40"+Integer.toString(id), R.drawable.class);
                else
                    resId= getResId("p4"+Integer.toString(id), R.drawable.class);
                person = BitmapFactory.decodeResource(getResources(), resId);

                //set location
                x += gapx;
                y += gapY;
                Log.d("drawpeople",Integer.toString(i)+" : x"+ Integer.toString(x)+ " / y " + Integer.toString(y));
                person_dst = new Rect((int) (x / (float) outputWidth * Width), (int) (y / (float) outputHeight * Height), (int) ((x + peopleWidth) / (float) outputWidth * Width), (int) ((y + peopleHeight) / (float) outputHeight * Height));

                Log.d("Drawpeople","canvaswidth: "+Integer.toString(canvas.getWidth()));
                canvas.drawBitmap(person, null, person_dst, null);
            } else {
                line++;
                x = startX + line * linebreakX;
                y = startY + line * linebreakY;
            }
    }


    //redraw all people
    //// TODO: refine drawing process 
    public void drawPeople(String words) {
        this.words = words;
        invalidate();
    }



    private static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            Log.e("getResId","cant find res!!");
            e.printStackTrace();
            return -1;
        }
    }
}
