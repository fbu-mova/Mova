package com.example.mova;

import okhttp3.OkHttpClient;

public class NounProject {
    protected OkHttpClient client;

    public NounProject() {
        client = new OkHttpClient();
    }

    public void getIcons(String term) {

    }

    public void getIcons(String term, Config config) {

    }

    public static class Config {
        public Integer limitToPublicDomain, limit, offset;
    }
}
