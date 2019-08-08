package com.example.mova.icons;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mova.R;
import com.example.mova.model.Goal;
import com.example.mova.model.Group;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.ColorUtils;
import com.example.mova.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Icons {
    private static Identicon.HashGeneratorInterface hashGenerator = new MessageDigestHashGenerator("MD5");
    private static Context context;
    private static NounProjectClient client;

    public static void setContext(Context context) {
        Icons.context = context;
        client = new NounProjectClient(context);
    }

    public static Bitmap identicon(@NonNull String name, int size) {
        Bitmap identicon = Identicon.generate(name, hashGenerator);
        identicon = Bitmap.createScaledBitmap(identicon, size, size, false);
        return identicon;
    }

    public static Bitmap identicon(@NonNull String name) {
        Resources res = context.getResources();
        int size = (int) res.getDimension(R.dimen.profileImage);
        return identicon(name, size);
    }

    public static Bitmap identicon(@NonNull User user, int size) {
        return identicon(user.getUsername(), size);
    }

    public static Bitmap identicon(@NonNull User user) {
        Resources res = context.getResources();
        int size = (int) res.getDimension(R.dimen.profileImage);
        return identicon(user, size);
    }

    public static int color(@NonNull String name) {
        int color = Identicon.color(name, hashGenerator);
        float[] hsl = new float[3];
        androidx.core.graphics.ColorUtils.colorToHSL(color, hsl);
        if (hsl[2] > (0.5f * 255f)) return secondaryColor(color);
        else                        return color;
    }

    public static int backgroundColor(@NonNull String name) {
        int color = Identicon.color(name, hashGenerator);
        float[] hsl = new float[3];
        androidx.core.graphics.ColorUtils.colorToHSL(color, hsl);
        if (hsl[2] > (0.5f * 255f)) return color;
        else                        return secondaryColor(color);
    }

    public static int secondaryColor(int mainColor) {
        return ColorUtils.lighten(mainColor, 0.3f);
    }

    public static void nounIcons(@NonNull String term, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        client.getIcons(term, cb);
    }

    public static void nounIcons(@NonNull String term, int limit, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        NounProjectClient.GetIconsConfig config = new NounProjectClient.GetIconsConfig();
        config.limit = limit;
        client.getIcons(term, config, cb);
    }

    public static void nounIcons(@NonNull String term, NounProjectClient.GetIconsConfig config, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        client.getIcons(term, config, cb);
    }

    public static void svgNounIcons(@NonNull String term, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        svgNounIcons(term, new NounProjectClient.GetIconsConfig(), cb);
    }

    public static void svgNounIcons(@NonNull String term, int limit, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        NounProjectClient.GetIconsConfig config = new NounProjectClient.GetIconsConfig();
        config.limit = limit;
        svgNounIcons(term, config, cb);
    }

    public static void svgNounIcons(@NonNull String term, NounProjectClient.GetIconsConfig config, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        Integer origLimit = config.limit;
        config.limit = 100;

        client.getIcons(term, config, (icons, e) -> {
            if (e != null) {
                cb.call(icons, e);
                return;
            }

            List<NounProjectClient.Icon> svgOnly = new ArrayList<>();
            Collections.addAll(svgOnly, icons);
            for (NounProjectClient.Icon icon : icons) {
                if (icon.iconUrl == null) {
                    svgOnly.remove(icon);
                }
            }

            if (origLimit == null) {
                NounProjectClient.Icon[] result = new NounProjectClient.Icon[svgOnly.size()];
                cb.call(svgOnly.toArray(result), null);
                return;
            }

            List<NounProjectClient.Icon> limited = svgOnly.subList(0, (origLimit <= svgOnly.size()) ? origLimit : svgOnly.size());
            NounProjectClient.Icon[] result = new NounProjectClient.Icon[limited.size()];
            cb.call(limited.toArray(result), null);
        });
    }

    public static void nounIcon(@NonNull String term, AsyncUtils.TwoItemCallback<NounProjectClient.Icon, Throwable> cb) {
        client.getIcon(term, cb);
    }

    public static void nounIcon(int id, AsyncUtils.TwoItemCallback<NounProjectClient.Icon, Throwable> cb) {
        client.getIcon(id, cb);
    }

    public static String lowestResImage(@NonNull NounProjectClient.Icon icon) {
        if (icon.previewUrl42 != null) return icon.previewUrl42;
        if (icon.previewUrl84 != null) return icon.previewUrl84;
        if (icon.previewUrl != null)   return icon.previewUrl;
//        if (icon.iconUrl != null)      return icon.iconUrl;
        return null;
    }

    public static String highestResImage(@NonNull NounProjectClient.Icon icon) {
//        if (icon.iconUrl != null)      return icon.iconUrl;
        if (icon.previewUrl != null)   return icon.previewUrl;
        if (icon.previewUrl84 != null) return icon.previewUrl84;
        if (icon.previewUrl42 != null) return icon.previewUrl42;
        return null;
    }

    public static void displayIdenticon(String name, CardView cv, ImageView iv) {
        iv.setImageBitmap(identicon(name));
        cv.setCardBackgroundColor(backgroundColor(name));
    }

    public static void displayIdenticon(User user, CardView cv, ImageView iv) {
        displayIdenticon(user.getUsername(), cv, iv);
    }

    public static void displayNounIcon(NounProjectClient.Icon icon, CardView cv, ImageView iv) {
        displayNounIcon(icon, cv, iv, new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                transitionColoredIcon(iv, resource, Icons.color(icon.term));
                cv.setCardBackgroundColor(Icons.backgroundColor(icon.term));
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    public static void displayNounIcon(Group group, CardView cv, ImageView iv) {
        if (group.getNounIconId() == 0) {
            Log.i("Icons", "Found noun icon 0 for goal \"" + group.getName() + "\"; displaying placeholder");
            displayPlaceholder(cv, iv);
            return;
        }

        group.getNounIcon((icon, e) -> {
            if (e != null) {
                Log.e("Icons", "Failed to get icon for group " + group.getName());
                displayPlaceholder(cv, iv);
            } else {
                displayNounIcon(icon, cv, iv);
            }
        });
    }

    public static void displayNounIcon(Goal goal, CardView cv, ImageView iv) {
        if (goal.getNounIconId() == 0) {
            Log.i("Icons", "Found noun icon 0 for goal \"" + goal.getTitle() + "\"; displaying placeholder");
            displayPlaceholder(cv, iv);
            return;
        }

        goal.getNounIcon((icon, e) -> {
            if (e != null) {
                Log.e("Icons", "Failed to get icon for goal " + goal.getTitle());
                displayPlaceholder(cv, iv);
            } else {
                displayNounIcon(icon, cv, iv, new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ColorUtils.Hue hue = goal.getHue();
                        if (hue == null) hue = ColorUtils.Hue.random();
                        int color = ColorUtils.getColor(context.getResources(), hue, ColorUtils.Lightness.Mid);
                        int bgColor = ColorUtils.getColor(context.getResources(), hue, ColorUtils.Lightness.UltraLight);

                        transitionColoredIcon(iv, resource, color);
                        cv.setCardBackgroundColor(bgColor);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
            }
        });
    }

    public static void displayPlaceholder(CardView cv, ImageView iv) {
        int placeholderColor = ColorUtils.randomColorInScheme(context.getResources(), false, ColorUtils.ColorType.Dark);
        iv.setImageBitmap(ImageUtils.makeTransparentBitmap(iv.getWidth(), iv.getHeight()));
        cv.setCardBackgroundColor(placeholderColor);
    }

    private static void displayNounIcon(NounProjectClient.Icon icon, CardView cv, ImageView iv, CustomTarget<Bitmap> target) {
        Glide.with(context)
                .asBitmap()
                .load(Icons.lowestResImage(icon))
                .into(target);
    }

    private static void transitionColoredIcon(ImageView iv, Bitmap icon, int color) {
        Bitmap colored = ColorUtils.changeColorFromBlack(icon, color);
        BitmapDrawable bmpDrawable = new BitmapDrawable(context.getResources(), colored);
        TransitionDrawable finalDrawable = new TransitionDrawable(new Drawable[] {
                new BitmapDrawable(context.getResources(), Bitmap.createBitmap(colored.getWidth(), colored.getHeight(), Bitmap.Config.ARGB_8888)),
                bmpDrawable
        });
        iv.setImageDrawable(finalDrawable);
        finalDrawable.startTransition(200);
    }
}
