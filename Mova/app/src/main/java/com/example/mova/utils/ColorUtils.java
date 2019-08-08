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
        recolorPixels(bmp, Color.BLACK, color, 0.3f, (orig, desired) -> {
            return androidx.core.graphics.ColorUtils.setAlphaComponent(desired, Color.alpha(orig));
            // FIXME: Possibly also modify lightness?
        });
        return bmp;
    }

    public interface BlendFunction {
        int blend(int color1, int color2);
    }

    public static void recolorPixels(Bitmap bmp, int selectColor, int color, float percentError, BlendFunction blend) {
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bmp, new Matrix(), new Paint());
        for (int i = 0; i < bmp.getHeight(); i++) {
            for (int j = 0; j < bmp.getWidth(); j++) {
                if (approxEqual(bmp.getPixel(i, j), selectColor, percentError)) {
                    int blendedColor = blend.blend(bmp.getPixel(i, j), color);
                    Paint p = new Paint();
                    p.setStyle(Paint.Style.FILL);
                    p.setColor(blendedColor);
                    canvas.drawPoint(i, j, p);
                }
            }
        }
    }

    public static boolean approxEqual(int color1, int color2, float percentError) {
        float[] hsla1 = new float[4];
        androidx.core.graphics.ColorUtils.colorToHSL(color1, hsla1);
        hsla1[3] = Color.alpha(color1);

        float[] hsla2 = new float[4];
        androidx.core.graphics.ColorUtils.colorToHSL(color2, hsla2);
        hsla2[3] = Color.alpha(color2);

        final float MAX_VALUE = 255f;
        final float ABSOLUTE_ERROR = MAX_VALUE * percentError;

        for (int i = 0; i < hsla1.length; i++) {
            if (Math.abs(hsla1[i] - hsla2[i]) > ABSOLUTE_ERROR) {
                return false;
            }
        }

        return true;
    }
}
