package com.example.mova.icons;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.model.Group;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.ColorUtils;

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

    public static Bitmap identicon(@NonNull User user, int size) {
        return identicon(user.getUsername(), size);
    }

    public static Bitmap identicon(@NonNull User user) {
        Resources res = context.getResources();
        int size = (int) res.getDimension(R.dimen.profileImage);
        return identicon(user, size);
    }

    public static int color(@NonNull String name) {
        return Identicon.color(name, hashGenerator);
    }

    public static int backgroundColor(@NonNull String name) {
        int color = color(name);
        return ColorUtils.lighten(color, 0.2f);
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
}
