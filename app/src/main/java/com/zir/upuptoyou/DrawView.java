package com.zir.upuptoyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.w3c.dom.Text;

import java.lang.reflect.Field;


/**
 * Created by zirco on 17-8-26.
 */

public class DrawView extends View {

    private int Width;  //px
    private int Height; //px

    final private int outputWidth = 500; //px
    final private int outputHeight = 500; //px

    final int PeopleStartX = 120;
    final int PeopleStartY = 40;
    final int PeopleGapX = 50;
    final int PeopleGapY = 22;
    final int PeopleWidth = 76;
    final int PeopleHeight = 149;
    final int PeoplelinebreakX = -52;
    final int PeoplelinebreakY = 36;

    final float TextStartX = 4f + 26.4f; //
    final float TextStartY = 34.5f - 6f;
    final float TextGapX = 37.7f;
    final float TextGapY = 1.4f;
    final int FontSizeZh = 20;
    final int FontSizeEn = 23;
    final float TextLinebreakX = -0.8f;
    final float TextLinebreakY = 35.5f;

    Paint paint_text;

    protected Bitmap watermark_bitmap;
    protected Rect watermark_dst;

    protected Bitmap person;
    protected Rect person_dst;
    protected String words;

    protected Matrix matrix_text;
    protected float r_x;
    protected float r_y;

    Typeface fontEn;
    Typeface fontZh;

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        watermark_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
        words="";
        paint_text = new Paint();
        paint_text.setColor(0xff40210f);
        paint_text.setFakeBoldText(true);
        paint_text.setTextAlign(Paint.Align.CENTER);
        paint_text.setAntiAlias(true);


        //TODO: change font before releasing to play store
        fontEn= Typeface.createFromAsset(getContext().getAssets(), "fonts/ITC_Avant_Garde_Gothic_LT_Bold.ttf");
        fontZh= Typeface.createFromAsset(getContext().getAssets(), "fonts/LiHei_Pro.ttf");

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Height = this.getHeight();
        Width = this.getHeight();
        Log.d("onSizeChanged", Integer.toString(Width) + "*" + Integer.toString(Height));
        watermark_dst = new Rect((int) (10.0 / (float) outputWidth * Width), (int) (8 / (float) outputHeight * Height), (int) ((101 + 10.0) / (float) outputWidth * Width), (int) ((8.0 + 45) / (float) outputHeight * Height));
        r_x = Width/(float)outputWidth;
        r_y = Height/(float)outputHeight;
        matrix_text = new Matrix();
        matrix_text.setValues(new float[]{1.3f*r_x, -1.5f*r_x, 191*r_x, 0.6f*r_x, 1*r_y, 32*r_x, 0,0,1*r_x});


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //watermark
        canvas.drawBitmap(watermark_bitmap, null, watermark_dst, null);
        Log.d("ondraw","canvaswidth: "+Integer.toString(canvas.getWidth()));

        //draw people
        Log.d("drawPeople", "start drawpeople");
        int x_people = PeopleStartX;
        int y_people = PeopleStartY;
        int line = 0;
        for (int i = 0; i < words.length(); i++)
            if (words.charAt(i) != '\n') {
                //load image
                //TODO: trade ram for speed by loading all bitmaps in constructor?
                int id = 1 + (int) (Math.random() * 25);
                //Log.d("Drawpeople","id:"+Integer.toString(id));
                int resId;
                if(id<10)
                    resId= getResId("p40"+Integer.toString(id), R.drawable.class);
                else
                    resId= getResId("p4"+Integer.toString(id), R.drawable.class);
                person = BitmapFactory.decodeResource(getResources(), resId);

                //set location
                x_people += PeopleGapX;
                y_people += PeopleGapY;
                //Log.d("drawpeople","image "+Integer.toString(i)+" : x"+ Integer.toString(x_people)+ " / y " + Integer.toString(y_people));
                person_dst = new Rect((int) (x_people * r_x), (int) (y_people * r_y), (int) ((x_people + PeopleWidth) * r_x), (int) ((y_people + PeopleHeight) * r_y));
                canvas.drawBitmap(person, null, person_dst, null);
            } else {
                line++;
                x_people = PeopleStartX + line * PeoplelinebreakX;
                y_people = PeopleStartY + line * PeoplelinebreakY;
            }

        //write words
        //Log.d("drawpeople","Matrix: "+ matrix_text.toShortString()) ;
        canvas.setMatrix(matrix_text);
        canvas.rotate(-3);
        float x_text = TextStartX;
        float y_text = TextStartY;
        line = 0;
        char c= (char)-1;
        for (int i = 0; i < words.length(); i++) {
            c = words.charAt(i);
            if (c != '\n') {

                x_text += TextGapX;
                y_text += TextGapY;

                //TODO: Add support for emoji

                if ((int) c < 128) {
                    paint_text.setTextSize(FontSizeEn * r_x);
                    paint_text.setTypeface(fontEn);
                    //Log.d("drawText",c+"'s Size"+ String.valueOf(paint_text.getTextSize()));
                }
                else {
                    paint_text.setTextSize(FontSizeZh * r_x);
                    paint_text.setTypeface(Typeface.DEFAULT_BOLD);
                    //Log.d("drawText", c + "'s Size" + String.valueOf(paint_text.getTextSize()));
                }

/*                switch (words.charAt(i)){             //unused since ❤ and ♥ have colors in Android
                    case '❤':
                        paint_text.setColor(0xffd92b6d);
                        break;
                    case '♥':
                        paint_text.setColor(0xffca2626);
                        break;
                }*/

                canvas.drawText(String.valueOf(words.charAt(i)).toUpperCase(), x_text * r_x, y_text * r_y, paint_text);
                //paint_text.setColor(0xff40210f);      //set color to default value if it's changed.
            } else {
                line++;
                x_text = TextStartX + line * TextLinebreakX;
                y_text = TextStartY + line * TextLinebreakY;
            }
        }
    }


    //redraw all people
    //TODO: refine drawing process: not all people at the same time
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
