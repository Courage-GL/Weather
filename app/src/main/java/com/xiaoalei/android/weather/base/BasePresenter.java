package com.xiaoalei.android.weather.base;

/**
 * presenter interface,所有Presenter必须实现此接口
 */
public interface BasePresenter {

    //订阅
    void subscribe();
    //取消订阅
    void unSubscribe();
}
