package com.xiaoalei.android.weather.di.module;

import android.content.Context;

import com.xiaoalei.android.weather.WeatherApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class ApplicationModule {

    private Context context;

    public ApplicationModule(Context context) {

        this.context = context;
    }

    @Provides
    @Singleton
    WeatherApplication provideApplication() {

        return (WeatherApplication) context.getApplicationContext();
    }

    @Provides
    @Singleton
    Context provideContext() {

        return context;
    }
}
