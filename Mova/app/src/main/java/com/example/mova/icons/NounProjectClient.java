package com.example.mova.icons;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.util.Log;

import com.example.mova.R;
import com.example.mova.utils.AsyncUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

public class NounProjectClient {
    protected static final String API_ROOT = "https://api.thenounproject.com";

    protected OkHttpClient client;
    protected Context context;

    public NounProjectClient(Context context) {
        this.context = context;

        // Authenticate client
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(getApiKey(), getApiSecret());
        client = new OkHttpClient.Builder()
            .addInterceptor(new SigningInterceptor(consumer))
            .build();
    }

    public void getIcons(String term, AsyncUtils.TwoItemCallback<Icon[], Throwable> cb) {
        getIcons(term, new GetIconsConfig(), cb);
    }

    public void getIcons(String term, GetIconsConfig config, AsyncUtils.TwoItemCallback<Icon[], Throwable> cb) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_ROOT + "/icons/" + term).newBuilder();
        if (config.limitToPublicDomain != null) {
            urlBuilder.addQueryParameter("limit_to_public_domain", (config.limitToPublicDomain) ? "1" : "0");
        }
        if (config.limit != null) {
            urlBuilder.addQueryParameter("limit", Integer.toString(config.limit));
        }
        if (config.offset != null) {
            urlBuilder.addQueryParameter("offset", Integer.toString(config.offset));
        }
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("NounProjectClient", "Failed to get icons for term " + term, e);
                cb.call(null, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    IOException e = new IOException("Unexpected code " + response);
                    cb.call(null, e);
                    throw e; // FIXME: Should likely not both call and throw
                }

                Icon[] icons = null;
                Exception e = null;
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);

                    JSONArray arr = json.getJSONArray("icons");
                    icons = new Icon[arr.length()];
                    for (int i = 0; i < icons.length; i++) {
                        icons[i] = new Icon(arr.getJSONObject(i));
                    }
                } catch (Exception e1) {
                    e = e1;
                    Log.e("NounProjectClient", "Failed to parse response to get icons for term " + term, e);
                } finally {
                    cb.call(icons, e);
                }
            }
        });
    }

    public void getIcon(String term, AsyncUtils.TwoItemCallback<Icon, Throwable> cb) {
        String url = API_ROOT + "/icon/" + term;
        Request request = new Request.Builder()
                .url(url)
                .build();
        getIcon(request, cb);
    }

    public void getIcon(int id, AsyncUtils.TwoItemCallback<Icon, Throwable> cb) {
        String url = API_ROOT + "/icon/" + id;
        Request request = new Request.Builder()
                .url(url)
                .build();
        getIcon(request, cb);
    }

    private void getIcon(Request request, AsyncUtils.TwoItemCallback<Icon, Throwable> cb) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("NounProjectClient", "Failed to get icon", e);
                cb.call(null, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    IOException e = new IOException("Unexpected code " + response);
                    cb.call(null, e);
                    throw e; // FIXME: Should likely not both call and throw
                }

                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    Icon icon = new Icon(json.getJSONObject("icon"));
                    cb.call(icon, null);
                } catch (Exception e) {
                    Log.e("NounProjectClient", "Failed to parse response to get icon", e);
                    cb.call(null, e);
                }
            }
        });
    }

    protected String getApiKey() {
        return context.getResources().getString(R.string.nounProjectKey);
    }

    protected String getApiSecret() {
        return context.getResources().getString(R.string.nounProjectSecret);
    }

    protected static String tryGetString(JSONObject obj, String name) {
        try {
            return obj.getString(name);
        } catch (JSONException e) {
            return null;
        }
    }

    protected static Integer tryGetInt(JSONObject obj, String name) {
        try {
            return obj.getInt(name);
        } catch (JSONException e) {
            return null;
        }
    }

    public static class GetIconsConfig {
        public Boolean limitToPublicDomain;
        public Integer limit, offset;
    }

    public static class Icon {
        public final String attribution;
        public final String attributionPreviewUrl;
        public final Date dateUploaded;
        public final String iconUrl;
        public final int id;
        public final boolean isActive;
        public final String licenseDescription;
        public final String permalink;
        public final String previewUrl;
        public final String previewUrl42;
        public final String previewUrl84;
        public final String term;
        public final int termId;
        public final String termSlug;
        public final Uploader uploader;
        public final int uploaderId;
        public final Integer year;
        // Does not support sponsors
        // Does not support collections
        // Does not support tags

        public Icon(JSONObject obj) throws JSONException {
            attribution = tryGetString(obj, "attribution");
            attributionPreviewUrl = tryGetString(obj, "attribution_preview_url");

//            JSONArray collections = obj.getJSONArray("collections");
//            this.collections = new Collection[collections.length()];
//            for (int i = 0; i < collections.length(); i++) {
//                this.collections[i] = new Collection(collections.getJSONObject(i));
//            }

            String dateUploaded = obj.getString("date_uploaded");
            SimpleDateFormat dateFmt = new SimpleDateFormat("YYYY-MM-dd");
            Date toSet;
            try {
                toSet = dateFmt.parse(dateUploaded);
            } catch (ParseException e) {
                toSet = null;
            }
            this.dateUploaded = toSet;

            iconUrl = tryGetString(obj, "icon_url");
            id = obj.getInt("id");
            isActive = Integer.valueOf(1).equals(tryGetInt(obj, "is_active"));
            licenseDescription = tryGetString(obj, "license_description");
            permalink = API_ROOT + obj.getString("permalink");
            previewUrl = tryGetString(obj, "preview_url");
            previewUrl42 = tryGetString(obj, "preview_url_42");
            previewUrl84 = tryGetString(obj, "preview_url_84");
            term = obj.getString("term");
            termId = obj.getInt("term_id");
            termSlug = obj.getString("term_slug");
            uploader = new Uploader(obj.getJSONObject("uploader"));
            uploaderId = obj.getInt("uploader_id");
            year = tryGetInt(obj, "year");
        }
    }

    public static class Uploader {
        public final String location;
        public final String name;
        public final String permalink;
        public final String username;

        public Uploader(JSONObject obj) throws JSONException {
            location = obj.getString("location");
            name = obj.getString("name");
            permalink = API_ROOT + obj.getString("permalink");
            username = obj.getString("username");
        }
    }
}
