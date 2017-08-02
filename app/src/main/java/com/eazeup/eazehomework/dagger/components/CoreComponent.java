package com.eazeup.eazehomework.dagger.components;

import com.eazeup.eazehomework.dagger.modules.AppModule;
import com.eazeup.eazehomework.dagger.modules.GiphyModule;
import com.eazeup.eazehomework.dagger.modules.NetModule;
import com.eazeup.eazehomework.dagger.modules.OkhttpGlideModule;
import com.eazeup.eazehomework.ui.AbstractGifGridActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { AppModule.class, NetModule.class, GiphyModule.class })
public interface CoreComponent {

    void inject(AbstractGifGridActivity activity);

    void inject(OkhttpGlideModule glideModule);
}
