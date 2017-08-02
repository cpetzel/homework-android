package com.eazeup.eazehomework.di.modules;

import java.io.InputStream;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.eazeup.eazehomework.EazeApplication;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

/**
 * Will help us intercept OkHttp requests when using {@link Glide}
 */
@GlideModule
public class OkhttpGlideModule extends AppGlideModule {

    @Inject
    OkHttpClient mClient;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        //todo tweak the cache
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void registerComponents(Context context, Registry registry) {
        EazeApplication.getInstance().getCoreComponent().inject(this);
        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(mClient);
        registry.replace(GlideUrl.class, InputStream.class, factory);
    }
}
