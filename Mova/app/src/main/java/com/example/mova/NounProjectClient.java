package com.example.mova;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.util.Log;

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
    protected static final String API_ROOT = "http://api.thenounproject.com";

    protected OkHttpClient client;
    protected Context context;

    public NounProjectClient(Context context) {
        this.context = context;
        client = new OkHttpClient();

        // Authenticate client
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(getApiKey(), getApiSecret());
        consumer.setTokenWithSecret(getApiKey(), getApiSecret());
        client.interceptors().add(new SigningInterceptor(consumer));
    }

    public void getIcons(String term, AsyncUtils.TwoItemCallback<Icon[], Throwable> cb) {
        getIcons(term, new Config(), cb);
    }

    public void getIcons(String term, Config config, AsyncUtils.TwoItemCallback<Icon[], Throwable> cb) {
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

                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);

                    JSONArray arr = json.getJSONArray("icons");
                    Icon[] icons = new Icon[arr.length()];
                    for (int i = 0; i < icons.length; i++) {
                        try {
                            icons[i] = new Icon(arr.getJSONObject(i));
                        } catch (JSONException e) {
                            Log.e("NounProjectClient", "Failed to parse response to get icons for term " + term + " at item " + i, e);
                            cb.call(icons, e);
                        }
                    }
                    cb.call(icons, null);
                } catch (Exception e) {
                    Log.e("NounProjectClient", "Failed to parse response to get icons for term " + term, e);
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

    public static class Config {
        public Boolean limitToPublicDomain;
        public Integer limit, offset;
    }

    public static class Icon {
        public final String attribution;
        public final String attributionPreviewUrl;
        public final String[] collections;
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
        public final int year;
        // Does not support sponsors

        public Icon(JSONObject obj) throws JSONException {
            attribution = obj.getString("attribution");
            attributionPreviewUrl = obj.getString("attribution_preview_url");

            JSONArray collections = obj.getJSONArray("collections");
            this.collections = new String[collections.length()];
            for (int i = 0; i < collections.length(); i++) {
                this.collections[i] = collections.getString(i);
            }

            String dateUploaded = obj.getString("date_uploaded");
            SimpleDateFormat dateFmt = new SimpleDateFormat("YYYY-MM-dd");
            Date toSet;
            try {
                toSet = dateFmt.parse(dateUploaded);
            } catch (ParseException e) {
                toSet = null;
            }
            this.dateUploaded = toSet;

            iconUrl = obj.getString("icon_url");
            id = obj.getInt("id");
            isActive = obj.getInt("is_active") == 1;
            licenseDescription = obj.getString("license_description");
            permalink = API_ROOT + obj.getString("permalink");
            previewUrl = obj.getString("preview_url");
            previewUrl42 = obj.getString("preview_url_42");
            previewUrl84 = obj.getString("preview_url_84");
            term = obj.getString("term");
            termId = obj.getInt("term_id");
            termSlug = obj.getString("term_slug");
            uploader = new Uploader(obj.getJSONObject("uploader"));
            uploaderId = obj.getInt("uploader_id");
            year = obj.getInt("year");
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
