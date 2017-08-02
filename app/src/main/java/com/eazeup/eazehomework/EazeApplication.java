package com.eazeup.eazehomework;

import android.app.Application;

import com.eazeup.eazehomework.di.components.CoreComponent;
import com.eazeup.eazehomework.di.components.DaggerCoreComponent;
import com.eazeup.eazehomework.di.modules.AppModule;
import com.eazeup.eazehomework.di.modules.GiphyModule;
import com.eazeup.eazehomework.di.modules.NetModule;
import com.facebook.stetho.Stetho;

public class EazeApplication extends Application {

    private CoreComponent mCoreComponent;

    static EazeApplication sInstance;

    //todo can load this in at build time
    private static final String BASE_URL = "http://api.giphy.com/v1/gifs/";
    private static final String TOKEN = "dc6zaTOxFJmzC";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        initInjector();

        Stetho.initializeWithDefaults(this);
    }

    void initInjector() {
        //@formatter:off
        mCoreComponent = DaggerCoreComponent.builder()
            .giphyModule(new GiphyModule(TOKEN))
            .appModule(new AppModule(this))
            .netModule(new NetModule(BASE_URL))
            .build();
        //@formatter:on
    }

    /**
     * Expose our Injector {@link CoreComponent} for use anywhere in the app
     *
     * @return
     */
    public CoreComponent getCoreComponent() {
        return mCoreComponent;
    }

    public static EazeApplication getInstance() {
        return sInstance;
    }
}
