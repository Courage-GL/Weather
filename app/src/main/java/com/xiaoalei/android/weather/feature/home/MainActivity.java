package com.xiaoalei.android.weather.feature.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoalei.android.weather.about.AboutUsActivity;
import com.xiaoalei.android.weather.base.BaseActivity;
import com.xiaoalei.android.library.util.ActivityUtils;
import com.xiaoalei.android.library.util.DateConvertUtils;
import com.xiaoalei.android.weather.WeatherApplication;
import com.xiaoalei.android.weather.data.db.entities.minimalist.Weather;
import com.xiaoalei.android.weather.feature.home.drawer.DrawerMenuPresenter;
import com.xiaoalei.android.weather.feature.home.drawer.DrawerMenuFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页
 */
public class MainActivity extends BaseActivity
        implements HomePageFragment.OnFragmentInteractionListener, DrawerMenuFragment.OnSelectCity {


    @BindView(com.xiaoalei.android.weather.R.id.refresh_layout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(com.xiaoalei.android.weather.R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(com.xiaoalei.android.weather.R.id.toolbar)
    Toolbar toolbar;
    @BindView(com.xiaoalei.android.weather.R.id.drawer_layout)
    DrawerLayout drawerLayout;

    //基本天气信息
    @BindView(com.xiaoalei.android.weather.R.id.temp_text_view)
    TextView tempTextView;
    @BindView(com.xiaoalei.android.weather.R.id.weather_text_view)
    TextView weatherNameTextView;
    @BindView(com.xiaoalei.android.weather.R.id.publish_time_text_view)
    TextView realTimeTextView;

    @Inject
    HomePagePresenter homePagePresenter;
    DrawerMenuPresenter drawerMenuPresenter;

    private String currentCityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(com.xiaoalei.android.weather.R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //设置 Header 为 Material风格
        ClassicsHeader header = new ClassicsHeader(this);
        header.setPrimaryColors(this.getResources().getColor(com.xiaoalei.android.weather.R.color.colorPrimary), Color.WHITE);
        this.smartRefreshLayout.setRefreshHeader(header);
        this.smartRefreshLayout.setOnRefreshListener(refreshLayout -> homePagePresenter.loadWeather(currentCityId, true));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, com.xiaoalei.android.weather.R.string.navigation_drawer_open, com.xiaoalei.android.weather.R.string.navigation_drawer_close);
        assert drawerLayout != null;
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //将HomeFragment添加到MainActivity中
        HomePageFragment homePageFragment = (HomePageFragment) getSupportFragmentManager().findFragmentById(com.xiaoalei.android.weather.R.id.fragment_container);
        if (homePageFragment == null) {

            homePageFragment = HomePageFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), homePageFragment, com.xiaoalei.android.weather.R.id.fragment_container);
        }

        DaggerHomePageComponent.builder()
                .applicationComponent(WeatherApplication.getInstance().getApplicationComponent())
                .homePageModule(new HomePageModule(homePageFragment))
                .build().inject(this);
        //侧拉的Fragment
        DrawerMenuFragment drawerMenuFragment = (DrawerMenuFragment) getSupportFragmentManager().findFragmentById(com.xiaoalei.android.weather.R.id.fragment_container_drawer_menu);
        if (drawerMenuFragment == null) {
            drawerMenuFragment = DrawerMenuFragment.newInstance(1);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), drawerMenuFragment, com.xiaoalei.android.weather.R.id.fragment_container_drawer_menu);
        }

        drawerMenuPresenter = new DrawerMenuPresenter(this, drawerMenuFragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.xiaoalei.android.weather.R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.xiaoalei.android.weather.R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == com.xiaoalei.android.weather.R.id.action_settings) {
            return true;
        } else if (id == com.xiaoalei.android.weather.R.id.action_about) {
            Intent intent=new Intent(this, AboutUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == com.xiaoalei.android.weather.R.id.action_feedback) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updatePageTitle(Weather weather) {
        currentCityId = weather.getCityId();
        smartRefreshLayout.finishRefresh();
        toolbar.setTitle(weather.getCityName());
        collapsingToolbarLayout.setTitle(weather.getCityName());

        tempTextView.setText(weather.getWeatherLive().getTemp());
        weatherNameTextView.setText(weather.getWeatherLive().getWeather());
        realTimeTextView.setText(getString(com.xiaoalei.android.weather.R.string.string_publish_time) + DateConvertUtils.timeStampToDate(weather.getWeatherLive().getTime(), DateConvertUtils.DATA_FORMAT_PATTEN_YYYY_MM_DD_HH_MM));
    }

    @Override
    public void addOrUpdateCityListInDrawerMenu(Weather weather) {
        drawerMenuPresenter.loadSavedCities();
    }

    @Override
    public void onSelect(String cityId) {

        assert drawerLayout != null;
        drawerLayout.closeDrawer(GravityCompat.START);

        new Handler().postDelayed(() -> homePagePresenter.loadWeather(cityId, false), 250);
    }
}
