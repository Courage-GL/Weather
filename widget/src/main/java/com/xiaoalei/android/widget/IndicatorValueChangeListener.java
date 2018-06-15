package com.xiaoalei.android.widget;

/**
 * 污染指数改变的监听
 */
public interface IndicatorValueChangeListener {

    void onChange(int currentIndicatorValue, String stateDescription, int indicatorTextColor);
}
