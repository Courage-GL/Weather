package com.xiaoalei.android.weather.feature.home;

import com.xiaoalei.android.weather.data.db.entities.minimalist.Weather;
import com.xiaoalei.android.weather.base.BasePresenter;
import com.xiaoalei.android.weather.base.BaseView;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com)
 */
public interface HomePageContract {

    interface View extends BaseView<Presenter> {

        void displayWeatherInformation(Weather weather);
    }

    interface Presenter extends BasePresenter {

        void loadWeather(String cityId, boolean refreshNow);
    }
}
