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
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.preference.PreferenceManager;
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

    private final int PeopleStartX = 120;
    private final int PeopleStartY = 40;
    private final int PeopleGapX = 50;
    private final int PeopleGapY = 22;
    private final int PeopleWidth = 76;
    private final int PeopleHeight = 149;
    private final int PeoplelinebreakX = -52;
    private final int PeoplelinebreakY = 36;

    private final float TextStartX = 4f; //
    private final float TextStartY = 34.5f;
    private final float TextGapX = 37.7f;
    private final float TextGapY = 1.4f;
    private final int FontSizeZh = 20;
    private final int FontSizeEn = 23;
    private final float TextLinebreakX = -0.8f;
    private final float TextLinebreakY = 35.5f;

    private Paint paint_text;

    private final Bitmap watermark_bitmap;
    private Rect watermark_dst;

    private String words;


    private Matrix matrix_text;


    private Bitmap bitmap_normal;
    private Canvas canvas_normal;

    private Bitmap[] bitmaps_people;

    Rect dstFromNormal;


    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        watermark_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
        words = "";
        paint_text = new Paint();
        paint_text.setColor(0xff40210f);
        paint_text.setFakeBoldText(true);
        paint_text.setTextAlign(Paint.Align.CENTER);
        paint_text.setAntiAlias(true);
        paint_text.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        dstFromNormal = new Rect();
        bitmaps_people = new Bitmap[25];


        loadPeopleBitmap();
    }

    private void loadPeopleBitmap() {
        int resID;
        for (int i = 0; i < bitmaps_people.length; i++) {
            if (i < 9)
                resID = getResId("p40" + Integer.toString(i + 1), R.drawable.class);
            else
                resID = getResId("p4" + Integer.toString(i + 1), R.drawable.class);
            bitmaps_people[i] = BitmapFactory.decodeResource(getResources(), resID);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Height = this.getHeight();
        Width = this.getHeight();
        matrix_text = new Matrix();
        matrix_text.setValues(new float[]{1.3f, -1.5f, 191, 0.6f, 1, 32, 0, 0, 1});
        bitmap_normal = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        canvas_normal = new Canvas(bitmap_normal);
        watermark_dst = new Rect(10, 8, (101 + 10), (8 + 45));
        dstFromNormal = new Rect(0, 0, Width, Height);
        drawNormal();
    }

    private void drawNormal() {
        bitmap_normal.eraseColor(Color.TRANSPARENT);
        canvas_normal.setMatrix(new Matrix());
        //region draw in 500*500
        //watermark
        canvas_normal.drawBitmap(watermark_bitmap, null, watermark_dst, null);

        //region draw people
        int x_people = PeopleStartX;
        int y_people = PeopleStartY;
        int line = 0;
        int picId;
        for (int i = 0; i < words.length(); i++)
            if (words.charAt(i) != '\n') {
                //load image
                picId = (int) (Math.random() * 25);
                Bitmap person = bitmaps_people[picId];

                //set location
                x_people += PeopleGapX;
                y_people += PeopleGapY;
                if (words.charAt(i) > 32) {
                    Rect person_dst = new Rect(x_people, y_people, (x_people + PeopleWidth), (y_people + PeopleHeight));
                    canvas_normal.drawBitmap(person, null, person_dst, null);
                }
            } else {
                line++;
                x_people = PeopleStartX + line * PeoplelinebreakX;
                y_people = PeopleStartY + line * PeoplelinebreakY;
            }
        //endregion

        //region write words
        canvas_normal.setMatrix(matrix_text);
        canvas_normal.rotate(-3);
        float x_text = TextStartX;
        float y_text = TextStartY;
        line = 0;
        char c;
        for (int i = 0; i < words.length(); i++) {
            c = words.charAt(i);
            if (c != '\n') {

                x_text += TextGapX;
                y_text += TextGapY;

                //TODO: Add support for emoji
                if (c > 32) {
                    if ((int) c < 128) {
                        paint_text.setTextSize(FontSizeEn);
                    } else {
                        paint_text.setTextSize(FontSizeZh);
                    }

/*                switch (words.charAt(i)){             //unused since ❤ and ♥ have colors in Android
                    case '❤':
                        paint_text.setColor(0xffd92b6d);
                        break;
                    case '♥':
                        paint_text.setColor(0xffca2626);
                        break;
                }*/

                    canvas_normal.drawText(String.valueOf(words.charAt(i)).toUpperCase(), x_text, y_text, paint_text);
                    //paint_text.setColor(0xff40210f);      //set color to default value if it's changed.
                }
            } else {
                line++;
                x_text = TextStartX + line * TextLinebreakX;
                y_text = TextStartY + line * TextLinebreakY;
            }
        }
        //endregion

        //endregion

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap_normal, null, dstFromNormal, null);
    }


    public void refresh() {
        drawNormal();
        invalidate();
    }

    //redraw all people
    public void drawPeople(String newWords) {
        if (!words.equals(newWords)) {
            this.words = newWords;
            drawNormal();
            invalidate();
        }
    }


    //TODO: choose pic size
    public void save(Context context) {
        Bitmap bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(((ColorDrawable) getBackground()).getColor());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap_normal, 0, 0, null);
        try {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File path = new File(root + "/upuptoyou");
            if (!path.mkdirs()) {
                Log.e("savePic", "Directory not created");
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
            Date now = new Date();
            String fileName = formatter.format(now);
            File file = new File(path.getAbsolutePath() + File.separator + "message_" + fileName + ".jpg");
            FileOutputStream ostream = new FileOutputStream(file);
            int quality = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_quality_key), "85"));
            Bitmap.createScaledBitmap(bitmap, outputWidth, outputHeight, true).compress(Bitmap.CompressFormat.JPEG, quality, ostream);
            ostream.close();

            Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.toast_file_saved) + "/" + Environment.DIRECTORY_PICTURES + "/upuptoyou/" + fileName + ".jpg", Toast.LENGTH_SHORT);
            toast.show();

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

    public Uri share() {
        Bitmap bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(((ColorDrawable) getBackground()).getColor());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap_normal, 0, 0, null);
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
            Date now = new Date();
            String fileName = formatter.format(now);
            File file = new File(getContext().getCacheDir(), "message_" + fileName + ".jpg");
            FileOutputStream ostream = new FileOutputStream(file);
            Bitmap.createScaledBitmap(bitmap, outputWidth, outputHeight, true).compress(Bitmap.CompressFormat.JPEG, 75, ostream);
            ostream.close();

            return FileProvider.getUriForFile(getContext(), "com.zir.upuptoyou.FileProvider", file);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
