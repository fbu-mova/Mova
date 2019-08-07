package com.example.mova.icons;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Creates symmetrical, Github-style identifying images deterministically using hashes.
 * @source I've modified the original class, but all credit otherwise due here: https://github.com/davidhampgonsalves/Contact-Identicons/tree/master/src/com/davidhampgonsalves/identicon
 */
public class Identicon {
    public interface HashGeneratorInterface {
        byte[] generate(String userName);
    }

    public static final int DEFAULT_WIDTH = 5;
    public static final int DEFAULT_HEIGHT = 5;

    protected int width, height;

    public Identicon(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Bitmap gen(String username, HashGeneratorInterface hashGenerator) {
        return generate(username, width, height, hashGenerator);
    }

    public static Bitmap generate(String name, HashGeneratorInterface hashGenerator) {
        return generate(name, DEFAULT_WIDTH, DEFAULT_HEIGHT, hashGenerator);
    }

    private static Bitmap generate(String name, int width, int height, HashGeneratorInterface hashGenerator) {
        byte[] hash = hashGenerator.generate(name);

        Bitmap identicon = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int foreground = color(hash);
        int background = lighten(foreground, 0.2f);

        for (int x = 0; x < width; x++) {

            //make identicon horizontally symmetrical
            int i = x < 3 ? x : 4 - x;
            int pixelColor;
            for (int y = 0; y < height; y++) {

                if ((hash[i] >> y & 1) == 1)
                    pixelColor = foreground;
                else
                    pixelColor = background;

                identicon.setPixel(x, y, pixelColor);
            }
        }

        //scale image by 2 to add border
        Bitmap bmpWithBorder = Bitmap.createBitmap(12, 12, identicon.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(background);
        identicon = Bitmap.createScaledBitmap(identicon, 10, 10, false);
        canvas.drawBitmap(identicon, 1, 1, null);

        return bmpWithBorder;
    }

    public static int color(String name, HashGeneratorInterface hashGenerator) {
        byte[] hash = hashGenerator.generate(name);
        return color(hash);
    }

    private static int color(byte[] hash) {
        // get byte values as unsigned ints
        int r = hash[0] & 255;
        int g = hash[1] & 255;
        int b = hash[2] & 255;

        return Color.argb(255, r, g, b);
    }

    // FIXME: Test this!
    /** @source https://stackoverflow.com/questions/4928772/using-color-and-color-darker-in-android */
    private static int lighten(int color, float quantity) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 1.0f - quantity * (1.0f - hsv[2]);
        return Color.HSVToColor(hsv);
    }
}
