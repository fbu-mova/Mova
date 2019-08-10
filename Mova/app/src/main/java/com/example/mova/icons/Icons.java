package com.example.mova.icons;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.GoalComposeActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.ImageComponent;
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

    private DelegatedResultActivity activity;
    private NounProjectClient client;

    public Icons(DelegatedResultActivity activity) {
        this.activity = activity;
        client = new NounProjectClient(activity);
    }

    public static Icons from(DelegatedResultActivity activity) {
        return new Icons(activity);
    }

    public Bitmap identicon(@NonNull String name, int size) {
        Bitmap identicon = Identicon.generate(name, hashGenerator);
        identicon = Bitmap.createScaledBitmap(identicon, size, size, false);
        return identicon;
    }

    public Bitmap identicon(@NonNull String name) {
        Resources res = activity.getResources();
        int size = (int) res.getDimension(R.dimen.profileImage);
        return identicon(name, size);
    }

    public Bitmap identicon(@NonNull User user, int size) {
        return identicon(user.getUsername(), size);
    }

    public Bitmap identicon(@NonNull User user) {
        Resources res = activity.getResources();
        int size = (int) res.getDimension(R.dimen.profileImage);
        return identicon(user, size);
    }

    public void nounIcons(@NonNull String term, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        client.getIcons(term, cb);
    }

    public  void nounIcons(@NonNull String term, int limit, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        NounProjectClient.GetIconsConfig config = new NounProjectClient.GetIconsConfig();
        config.limit = limit;
        client.getIcons(term, config, cb);
    }

    public void nounIcons(@NonNull String term, NounProjectClient.GetIconsConfig config, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        client.getIcons(term, config, cb);
    }

    public void svgNounIcons(@NonNull String term, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        svgNounIcons(term, new NounProjectClient.GetIconsConfig(), cb);
    }

    public void svgNounIcons(@NonNull String term, int limit, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
        NounProjectClient.GetIconsConfig config = new NounProjectClient.GetIconsConfig();
        config.limit = limit;
        svgNounIcons(term, config, cb);
    }

    public void svgNounIcons(@NonNull String term, NounProjectClient.GetIconsConfig config, AsyncUtils.TwoItemCallback<NounProjectClient.Icon[], Throwable> cb) {
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

    public void nounIcon(@NonNull String term, AsyncUtils.TwoItemCallback<NounProjectClient.Icon, Throwable> cb) {
        client.getIcon(term, cb);
    }

    public void nounIcon(int id, AsyncUtils.TwoItemCallback<NounProjectClient.Icon, Throwable> cb) {
        client.getIcon(id, cb);
    }

    public void displayIdenticon(String name, CardView cv, ImageView iv) {
        iv.setImageBitmap(identicon(name));
        cv.setCardBackgroundColor(backgroundColor(name));
    }

    public void displayIdenticon(User user, CardView cv, ImageView iv) {
        displayIdenticon(user.getUsername(), cv, iv);
    }

    public void displayNounIcon(NounProjectClient.Icon icon, CardView cv, ImageView iv) {
        displayNounIcon(icon, cv, iv, new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                activity.runOnUiThread(() -> {
                    transitionColoredIcon(iv, resource, color(icon.term));
                    cv.setCardBackgroundColor(backgroundColor(icon.term));
                });
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    public void displayNounIcon(Group group, CardView cv, ImageView iv) {
        if (group.getNounIconId() == 0) {
            Log.i("Icons", "Found noun icon 0 for goal \"" + group.getName() + "\"; displaying placeholder");
            displayPlaceholder(cv, iv);
            return;
        }

        group.getNounIcon(activity, (icon, e) -> {
            if (e != null) {
                Log.e("Icons", "Failed to get icon for group " + group.getName());
                displayPlaceholder(cv, iv);
            } else {
                displayNounIcon(icon, cv, iv);
            }
        });
    }

    public void displayNounIcon(Goal goal, CardView cv, ImageView iv) {
        if (goal.getNounIconId() == 0) {
            Log.i("Icons", "Found noun icon 0 for goal \"" + goal.getTitle() + "\"; displaying placeholder");
            displayPlaceholder(cv, iv);
            return;
        }

        goal.getNounIcon(activity, (icon, e) -> {
            activity.runOnUiThread(() -> {
                if (e != null) {
                    Log.e("Icons", "Failed to get icon for goal " + goal.getTitle());
                    displayPlaceholder(cv, iv);
                } else {
                    displayNounIcon(icon, cv, iv, new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            activity.runOnUiThread(() -> {
                                ColorUtils.Hue hue = goal.getHue();
                                if (hue == null) hue = ColorUtils.Hue.random();
                                int color = ColorUtils.getColor(activity.getResources(), hue, ColorUtils.Lightness.Mid);
                                int bgColor = ColorUtils.getColor(activity.getResources(), hue, ColorUtils.Lightness.UltraLight);

                                transitionColoredIcon(iv, resource, color);
                                if(cv != null) cv.setCardBackgroundColor(bgColor);
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                }
            });
        });
    }

    public void displayPlaceholder(CardView cv, ImageView iv) {
        int placeholderColor = ColorUtils.randomColorInScheme(activity.getResources(), false, ColorUtils.ColorType.Dark);
        iv.setImageBitmap(ImageUtils.makeTransparentBitmap(100, 100));
        if (cv != null) cv.setCardBackgroundColor(placeholderColor);
    }

    private void displayNounIcon(NounProjectClient.Icon icon, CardView cv, ImageView iv, CustomTarget<Bitmap> target) {
        Glide.with(activity)
             .asBitmap()
             .load(Icons.lowestResImage(icon))
             .into(target);
    }

    private void transitionColoredIcon(ImageView iv, Bitmap icon, int color) {
        Bitmap colored = ColorUtils.changeColorFromBlack(icon, color);
        BitmapDrawable bmpDrawable = new BitmapDrawable(activity.getResources(), colored);
        TransitionDrawable finalDrawable = new TransitionDrawable(new Drawable[] {
            new BitmapDrawable(activity.getResources(), Bitmap.createBitmap(colored.getWidth(), colored.getHeight(), Bitmap.Config.ARGB_8888)),
            bmpDrawable
        });
        iv.setImageDrawable(finalDrawable);
        finalDrawable.startTransition(200);
    }

    public interface NounIconDialogHandler {
        void onSelect(NounProjectClient.Icon icon);
        void onCancel();
//        void onError(Throwable e);
    }

    public void showNounIconDialog(String term, String emptyText, NounIconDialogHandler handler) {
        term = term.toLowerCase();
        final String xTerm = term;

        if (xTerm.equals("")) {
            Toast.makeText(activity, emptyText, Toast.LENGTH_SHORT).show();
            return;
        }

        View view = activity.getLayoutInflater().inflate(R.layout.layout_recycler_view, null);
        RecyclerView rv = view.findViewById(R.id.rv);

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle("Choose an icon")
                .setView(view)
                .setNegativeButton("Cancel", (dialog, which) -> handler.onCancel())
                .create();

        List<NounProjectClient.Icon> icons = new ArrayList<>();
        DataComponentAdapter<NounProjectClient.Icon> adapter = new DataComponentAdapter<NounProjectClient.Icon>(activity, icons) {
            @Override
            protected Component makeComponent(NounProjectClient.Icon item, Component.ViewHolder holder) {
                ImageComponent component = new ImageComponent(Icons.highestResImage(item));
                component.setOnClick(() -> {
                    alertDialog.dismiss();
                    handler.onSelect(item);
                });
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(NounProjectClient.Icon item) {
                return new ImageComponent.Inflater();
            }
        };

        rv.setLayoutManager(new GridLayoutManager(activity, 4));
        rv.setAdapter(adapter);
        // TODO: Add padding

        nounIcons(term, 20, (suggestedIcons, e) -> {
            activity.runOnUiThread(() -> {
                if (e != null) {
                    Log.e("Icons", "Failed to load suggested icons", e);
                    Toast.makeText(activity, "Couldn't find any icons for \"" + xTerm + "\"", Toast.LENGTH_LONG).show();
                    // TODO: Create friendlier UI for this
                    // TODO: Differentiate between network errors and no icons found
                    return;
                }

                Collections.addAll(icons, suggestedIcons);
                adapter.notifyDataSetChanged();
            });
        });

        alertDialog.show();
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
}
