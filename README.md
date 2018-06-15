# SimpleWeather


## 一. 前言

推荐阅读：
**SimpleWeather是 Android 平台上一款开源天气 App ，目前还在开发中。项目基于 MVP 架构，采用各主流开源库实现。开发此项目主要是为展示各种开源库的使用方式以及 Android 项目的设计方案，并作为团队项目开发规范的一部分。**

采用的开源库包括：

* RxJava
* Retrofit2
* OKHttp3
* ORMLite
* Dagger2
* ButterKnife
* RetroLambd

**App Module包结构**

```Java
-com.xiaoalei.android.weather
    + base	  // MVP 各组件的基类及相关基础类
    + data    // MVP 中所有 Model 层的数据处理都在这里
    - feature       // 业务 feature，feature 内按页面划分，如果是大型项目可以按业务模块划分，对于特大型项目建议走模块化（组件化）方案，每个业务模块再按照 SimpleWeather 的分包规则来分包
        + home
        - selectcity
            - xxActivity.java // Activity 作为全局的控制者，用来负责创建 View 和 Presenter 的实例
            - xxFragment.java 
            - xxPresenter.java
            - xxContract.java // 契约类，用来统一管理 View 和 Presenter 的接口
    + util
    - AppConstants.java        // App 全局常量
    - WeatherApplication.java  // Application 类
    - WelcomeActivity.java     // 放在这里是为了便于查找应用程序入口
```


