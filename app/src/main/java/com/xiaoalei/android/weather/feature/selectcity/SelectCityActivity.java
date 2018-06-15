package com.xiaoalei.android.weather.feature.selectcity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.xiaoalei.android.weather.base.BaseActivity;
import com.xiaoalei.android.library.util.ActivityUtils;
import com.xiaoalei.android.weather.WeatherApplication;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 */
public class SelectCityActivity extends BaseActivity {

    @BindView(com.xiaoalei.android.weather.R.id.toolbar)
    Toolbar toolbar;

    SelectCityFragment selectCityFragment;

    @Inject
    SelectCityPresenter selectCityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.xiaoalei.android.weather.R.layout.activity_select_city);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        selectCityFragment = (SelectCityFragment) getSupportFragmentManager().findFragmentById(com.xiaoalei.android.weather.R.id.fragment_container);
        if (selectCityFragment == null) {
            selectCityFragment = SelectCityFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), selectCityFragment, com.xiaoalei.android.weather.R.id.fragment_container);
        }

        DaggerSelectCityComponent.builder()
                .applicationComponent(WeatherApplication.getInstance().getApplicationComponent())
                .selectCityModule(new SelectCityModule(selectCityFragment))
                .build().inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.search_city, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == com.xiaoalei.android.weather.R.id.action_search) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            RxSearchView.queryTextChanges(searchView)
                    .map(charSequence -> charSequence == null ? null : charSequence.toString().trim())
                    .throttleLast(100, TimeUnit.MILLISECONDS)
                    .debounce(100, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(searchText -> selectCityFragment.cityListAdapter.getFilter().filter(searchText));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}