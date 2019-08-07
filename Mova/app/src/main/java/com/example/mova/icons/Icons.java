package com.example.mova.icons;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

import com.example.mova.R;
import com.example.mova.model.Group;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;

public class Icons {
    private static Identicon.HashGeneratorInterface hashGenerator = new MessageDigestHashGenerator("MD5");
    private static Context context;
    private static NounProjectClient client;

    public static void setContext(Context context) {
        Icons.context = context;
        client = new NounProjectClient(context);
    }

    public static Bitmap identicon(String name, int size) {
        Bitmap identicon = Identicon.generate(name, hashGenerator);
        identicon = Bitmap.createScaledBitmap(identicon, size, size, false);
        return identicon;
    }

    public static Bitmap identicon(User user, int size) {
        return identicon(user.getUsername(), size);
    }

    public static Bitmap identicon(User user) {
        Resources res = context.getResources();
        int size = (int) res.getDimension(R.dimen.profileImage);
        return identicon(user, size);
    }

    public static int color(String name) {
        return Identicon.color(name, hashGenerator);
    }

    public static int color(User user) {
        return color(user.getUsername());
    }

    public static int color(Group group) {
        return color(group.getName());
    }

    public static int color(Tag tag) {
        return color(tag.getName());
    }

    public static void nounIcons(String term, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        client.getIcons(term, cb);
    }

    public static void nounIcon(String term, AsyncUtils.TwoItemCallback<NounProjectClient.Icon, Throwable> cb) {
        client.getIcon(term, cb);
    }

    public static void nounIcon(int id, AsyncUtils.TwoItemCallback<NounProjectClient.Icon, Throwable> cb) {
        client.getIcon(id, cb);
    }

    public static String lowestResImage(NounProjectClient.Icon icon) {
        if (icon.previewUrl42 != null) return icon.previewUrl42;
        if (icon.previewUrl84 != null) return icon.previewUrl84;
        if (icon.previewUrl != null)   return icon.previewUrl;
        if (icon.iconUrl != null)      return icon.iconUrl;
        return null;
    }

    public static String highestResImage(NounProjectClient.Icon icon) {
        if (icon.iconUrl != null)      return icon.iconUrl;
        if (icon.previewUrl != null)   return icon.previewUrl;
        if (icon.previewUrl84 != null) return icon.previewUrl84;
        if (icon.previewUrl42 != null) return icon.previewUrl42;
        return null;
    }
}
