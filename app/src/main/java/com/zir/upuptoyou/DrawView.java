package com.zir.upuptoyou;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


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
    protected String oldWords;

    protected int[] picIds;

    protected Matrix matrix_text;
    protected float r_x;
    protected float r_y;

    Typeface fontEn;
    Typeface fontZh;

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        watermark_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
        words = "";
        oldWords = "";
        picIds = new int[words.length()];
        paint_text = new Paint();
        paint_text.setColor(0xff40210f);
        paint_text.setFakeBoldText(true);
        paint_text.setTextAlign(Paint.Align.CENTER);
        paint_text.setAntiAlias(true);


        //TODO: change font before releasing to play store
        fontEn = Typeface.createFromAsset(getContext().getAssets(), "fonts/ITC_Avant_Garde_Gothic_LT_Bold.ttf");
        fontZh = Typeface.createFromAsset(getContext().getAssets(), "fonts/LiHei_Pro.ttf");

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Height = this.getHeight();
        Width = this.getHeight();
        Log.d("onSizeChanged", Integer.toString(Width) + "*" + Integer.toString(Height));
        watermark_dst = new Rect((int) (10.0 / (float) outputWidth * Width), (int) (8 / (float) outputHeight * Height), (int) ((101 + 10.0) / (float) outputWidth * Width), (int) ((8.0 + 45) / (float) outputHeight * Height));
        r_x = Width / (float) outputWidth;
        r_y = Height / (float) outputHeight;
        matrix_text = new Matrix();
        matrix_text.setValues(new float[]{1.3f * r_x, -1.5f * r_x, 191 * r_x, 0.6f * r_x, 1 * r_y, 32 * r_x, 0, 0, 1 * r_x});

    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean noChange = oldWords.equals(words);

        //watermark
        canvas.drawBitmap(watermark_bitmap, null, watermark_dst, null);
        Log.d("ondraw", "canvaswidth: " + Integer.toString(canvas.getWidth()));

        //region draw people
        Log.d("drawPeople", "start drawpeople");
        int x_people = PeopleStartX;
        int y_people = PeopleStartY;
        int line = 0;
        int resId;
        if(!noChange)
            picIds = new int[words.length()];
        for (int i = 0; i < words.length(); i++)
            if (words.charAt(i) != '\n') {
                //load image
                //TODO: trade ram for speed by loading all bitmaps in constructor?
                if(!noChange)
                    picIds[i] = 1 + (int) (Math.random() * 25);
                if (picIds[i] < 10)
                    resId = getResId("p40" + Integer.toString(picIds[i]), R.drawable.class);
                else
                    resId = getResId("p4" + Integer.toString(picIds[i]), R.drawable.class);
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
        //endregion

        //region write words
        canvas.setMatrix(matrix_text);
        canvas.rotate(-3);
        float x_text = TextStartX;
        float y_text = TextStartY;
        line = 0;
        char c = (char) -1;
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
                } else {
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
        //endregion

        oldWords=words;
    }


    public void refresh(){
        for(int i = 0; i<picIds.length;i++)
            picIds[i] = 1 + (int) (Math.random() * 25);
        invalidate();
    }
    //redraw all people
    //TODO: refine drawing process: not all people at the same time
    public void drawPeople(String newWords) {
        if(!words.equals(newWords)) {
            this.words = newWords;
            invalidate();
        }
    }


    //TODO: choose pic size
    public void save() {
        Bitmap bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        try {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File path = new File(root + "/upuptoyou");
            boolean createDir = path.mkdir();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
            Date now = new Date();
            String fileName = formatter.format(now);
            File file = new File(path.getAbsolutePath() + File.separator + "message_" + fileName + ".jpg");
            Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.toast_file_saved) + "/" + Environment.DIRECTORY_PICTURES + "/upuptoyou/" + fileName + ".jpg", Toast.LENGTH_LONG);
            toast.show();
            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            Log.e("getResId", "cant find res!!");
            e.printStackTrace();
            return -1;
        }
    }
}
