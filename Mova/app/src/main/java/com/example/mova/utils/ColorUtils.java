package com.example.mova.utils;

import android.content.Context;
import android.content.res.Resources;
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

import com.example.mova.R;
import com.example.mova.icons.Icons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ColorUtils {
    /** @source https://stackoverflow.com/questions/4928772/using-color-and-color-darker-in-android */
    public static int lighten(int color, float quantity) {
        float[] hsl = new float[3];
        androidx.core.graphics.ColorUtils.colorToHSL(color, hsl);
        hsl[2] = 1.0f - quantity * (1.0f - hsl[2]);
        return androidx.core.graphics.ColorUtils.HSLToColor(hsl);
    }

    public static Bitmap changeColorFromBlack(Drawable drawable, int color) {
        Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        drawable.draw(canvas);

        return changeColorFromBlack(bmp, color);
    }

    public static Bitmap changeColorFromBlack(Bitmap bmp, int color) {
        bmp.setHasAlpha(true);
        recolorPixels(bmp, Color.BLACK, color, 0.5f, (orig, desired) -> androidx.core.graphics.ColorUtils.setAlphaComponent(desired, Color.alpha(orig)));
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

    public static int randomColorInScheme(Resources res, boolean shouldInclude, ColorType... includeTypes) {
        List<ColorType> include = Arrays.asList(includeTypes);
        List<Integer> colors = new ArrayList<>();

        AsyncUtils.ItemReturnCallback<ColorType, Boolean> check = (type) -> (shouldInclude) == include.contains(type);

        if (check.call(ColorType.Blue)) {
            if (check.call(ColorType.UltraLight)) colors.add(res.getColor(R.color.blueUltraLight));
            if (check.call(ColorType.Light))      colors.add(res.getColor(R.color.blueLight));
            if (check.call(ColorType.Mid))        colors.add(res.getColor(R.color.blueMid));
            if (check.call(ColorType.Dark))       colors.add(res.getColor(R.color.blueDark));
        }

        if (check.call(ColorType.Purple)) {
            if (check.call(ColorType.UltraLight)) colors.add(res.getColor(R.color.purpleUltraLight));
            if (check.call(ColorType.Light))      colors.add(res.getColor(R.color.purpleLight));
            if (check.call(ColorType.Mid))        colors.add(res.getColor(R.color.purpleMid));
            if (check.call(ColorType.Dark))       colors.add(res.getColor(R.color.purpleDark));
        }

        if (check.call(ColorType.Orange)) {
            if (check.call(ColorType.UltraLight)) colors.add(res.getColor(R.color.orangeUltraLight));
            if (check.call(ColorType.Light))      colors.add(res.getColor(R.color.orangeLight));
            if (check.call(ColorType.Mid))        colors.add(res.getColor(R.color.orangeMid));
            if (check.call(ColorType.Dark))       colors.add(res.getColor(R.color.orangeDark));
        }

        if (colors.size() == 0) throw new IllegalArgumentException("Must allow at least one color.");

        Random random = new Random();
        int index = random.nextInt(colors.size());
        return colors.get(index);
    }

    public static int getColor(Resources res, Hue hue, Lightness lightness) {
        switch (hue) {
            case Blue:
                switch (lightness) {
                    case UltraLight:    return res.getColor(R.color.blueUltraLight);
                    case Light:         return res.getColor(R.color.blueLight);
                    case Mid:           return res.getColor(R.color.blueMid);
                    case Dark: default: return res.getColor(R.color.blueDark);
                }
            case Purple:
                switch (lightness) {
                    case UltraLight:    return res.getColor(R.color.purpleUltraLight);
                    case Light:         return res.getColor(R.color.purpleLight);
                    case Mid:           return res.getColor(R.color.purpleMid);
                    case Dark: default: return res.getColor(R.color.purpleDark);
                }
            case Orange:
            default:
                switch (lightness) {
                    case UltraLight:    return res.getColor(R.color.orangeUltraLight);
                    case Light:         return res.getColor(R.color.orangeLight);
                    case Mid:           return res.getColor(R.color.orangeMid);
                    case Dark: default: return res.getColor(R.color.orangeDark);
                }
        }
    }

    public enum ColorType {
        UltraLight,
        Light,
        Mid,
        Dark,
        Blue,
        Purple,
        Orange,
    }

    public enum Hue {
        Blue,
        Purple,
        Orange;

        public static Hue random() {
            Random random = new Random();
            Hue[] values = Hue.values();
            int i = random.nextInt(values.length);
            return values[i];
        }
    }

    public enum Lightness {
        UltraLight,
        Light,
        Mid,
        Dark;

        public static Lightness random() {
            Random random = new Random();
            Lightness[] values = Lightness.values();
            int i = random.nextInt(values.length);
            return values[i];
        }
    }
}
