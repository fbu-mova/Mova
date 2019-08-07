package com.example.mova.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.example.mova.icons.Icons;

public class ColorUtils {
    /** @source https://stackoverflow.com/questions/4928772/using-color-and-color-darker-in-android */
    public static int lighten(int color, float quantity) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 1.0f - quantity * (1.0f - hsv[2]);
        return Color.HSVToColor(hsv);
    }

    public static Bitmap changeColorFromBlack(Drawable drawable, int color) {
        Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        drawable.draw(canvas);

        return changeColorFromBlack(bmp, color);
    }

    public static Bitmap changeColorFromBlack(Bitmap bmp, int color) {
        bmp.setHasAlpha(true);
        recolorPixels(bmp, Color.WHITE, Color.RED);
        recolorPixels(bmp, Color.BLACK, color);
//        Canvas canvas = new Canvas(bmp);
//        Paint p = new Paint(color);
//        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
//        ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
//        p.setColorFilter(filter);
//        canvas.drawBitmap(bmp, new Matrix(), p);
//        p.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
//        canvas.drawBitmap(bmp, new Matrix(), p);
//        p.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SCREEN));
//        canvas.drawBitmap(bmp, new Matrix(), p);
//        p.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
//        canvas.drawBitmap(bmp, new Matrix(), p);

        return bmp;
    }

    // FIXME: Doesn't successfully change pixel colors
    public static void recolorPixels(Bitmap bmp, int selectColor, int color) {
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bmp, new Matrix(), new Paint());
        for (int i = 0; i < bmp.getHeight(); i++) {
            for (int j = 0; j < bmp.getWidth(); j++) {
                if (bmp.getPixel(i, j) == selectColor) {
                    Paint p = new Paint();
                    p.setStyle(Paint.Style.FILL);
                    p.setColor(color);
                    canvas.drawPoint(i, j, p);
                }
            }
        }
    }
}
