package com.xiaoalei.android.weather.feature.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.xiaoalei.android.weather.R;
import com.xiaoalei.android.weather.about.AboutUsActivity;
import com.xiaoalei.android.weather.base.BaseActivity;
import com.xiaoalei.android.library.util.ActivityUtils;
import com.xiaoalei.android.library.util.DateConvertUtils;
import com.xiaoalei.android.weather.WeatherApplication;
import com.xiaoalei.android.weather.data.db.CityDatabaseHelper;
import com.xiaoalei.android.weather.data.db.entities.minimalist.Weather;
import com.xiaoalei.android.weather.feature.home.drawer.DrawerMenuPresenter;
import com.xiaoalei.android.weather.feature.home.drawer.DrawerMenuFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

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
    private LocationClient mLocationClient;
    private String cityId;

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
        getLoactionQX();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

               //设置 Header 为 Material风格
        ClassicsHeader header = new ClassicsHeader(this);
        header.setPrimaryColors(this.getResources().getColor(com.xiaoalei.android.weather.R.color.colorPrimary), Color.WHITE);
        this.smartRefreshLayout.setRefreshHeader(header);
        this.smartRefreshLayout.setOnRefreshListener(refreshLayout -> homePagePresenter.loadWeather(cityId, true));

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
            Intent intent = new Intent(this, AboutUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == com.xiaoalei.android.weather.R.id.action_feedback) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updatePageTitle(Weather weather) {
        cityId = weather.getCityId();
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


    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                SQLiteDatabase readableDatabase = CityDatabaseHelper.getInstance(getApplicationContext()).getReadableDatabase();
                Cursor cursor = readableDatabase.rawQuery("Select posID from HotCity where name=?", new String[]{bdLocation.getCity().substring(0, bdLocation.getCity().length() - 1)});
                if (cursor.moveToFirst()) {
                    do {
                        //遍历Cursor对象，取出数据并打印
                        cityId= cursor.getString(cursor.getColumnIndex("posID"));
                        homePagePresenter.loadWeather(cityId, true);

                    } while (cursor.moveToNext());
                }
                cursor.close();
                readableDatabase.close();
            }
        });
    }

    public void getLoactionQX() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }


    }

    //开启定位
    private void requestLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults ) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有的权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                      initLocation();
                        requestLocation();
                } else {
                    Toast.makeText(this, "发生了错误", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocation();
        requestLocation();
    }
}



