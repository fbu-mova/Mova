package com.example.mova.icons;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.mova.activities.MainActivity;
import com.example.mova.model.Group;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;

public class Icons {
    private static Identicon.HashGeneratorInterface hashGenerator = new MessageDigestHashGenerator("MD5");
    private static NounProjectClient client;

    public static void setContext(Context context) {
        client = new NounProjectClient(context);
    }

    public static Bitmap identicon(String name) {
        Bitmap identicon = Identicon.generate(name, hashGenerator);
        return identicon;
    }

    public static Bitmap identicon(User user) {
        return identicon(user.getUsername());
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
}
