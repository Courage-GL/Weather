package com.xiaoalei.android.weather.feature.selectcity;

import com.xiaoalei.android.weather.di.component.ApplicationComponent;
import com.xiaoalei.android.weather.di.scope.ActivityScoped;

import dagger.Component;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com)
 *         2016/11/30
 */
@ActivityScoped
@Component(modules = SelectCityModule.class, dependencies = ApplicationComponent.class)
public interface SelectCityComponent {

    void inject(SelectCityActivity selectCityActivity);
}
