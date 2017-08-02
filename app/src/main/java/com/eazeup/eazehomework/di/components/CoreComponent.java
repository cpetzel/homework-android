package com.eazeup.eazehomework.di.components;

import com.eazeup.eazehomework.di.modules.AppModule;
import com.eazeup.eazehomework.di.modules.GiphyModule;
import com.eazeup.eazehomework.di.modules.NetModule;
import com.eazeup.eazehomework.di.modules.OkhttpGlideModule;
import com.eazeup.eazehomework.ui.AbstractGifGridActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { AppModule.class, NetModule.class, GiphyModule.class })
public interface CoreComponent {

    void inject(AbstractGifGridActivity activity);

    void inject(OkhttpGlideModule glideModule);
}
