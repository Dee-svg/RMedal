# 工作使我快乐，放假妨碍我进步
**生命在于加班**

## 项目介绍
   - 自己工作中的整理与总结
   - 沉浸工具-底部导航适配（小米9 华为mate20 vivo-nex 测试通过）

### 软件架构
   - Rxjava + MVP


### 目录结构
1.  [app](https://github.com/skpy5272/RMedal/tree/master/app)
   - api 网络请求封装和请求地址
   - base 基类
   - modelbean 实体类
   - ui 视图 mvp

2.  [tools-library 常用封装工具](https://github.com/skpy5272/RMedal/tree/master/tools-library)


#### 开发环境信息
1. AS版本3.4.1
2. jdk 1.8

#### 参与贡献

1. KKan (164994601@qq.com)


## 支持鸣谢

##### 1. [GitHub](https://github.com/)
##### 2. [码云](https://gitee.com/explore/recommend?lang=Android)
##### 3. [度娘](https://www.baidu.com/)

## 依赖

##### 1. [butterknife 注解](https://github.com/JakeWharton/butterknife)
      implementation 'com.jakewharton:butterknife:8.8.1'
##### 2. [BRVAH RecyclerAdapter框架](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
      implementation 'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.46'
##### 3. [SmartRefreshLayout下拉刷新上拉加载 ](https://github.com/scwang90/SmartRefreshLayout)
      implementation 'com.scwang.smartrefresh:SmartRefreshLayout:1.1.0'
      implementation 'com.scwang.smartrefresh:SmartRefreshHeader:1.1.0'//没有使用特殊Header，可以不加这行
##### 4. [SwipeBack侧滑退出](https://github.com/gongwen/SwipeBackLayout)
      implementation 'com.gongwen:swipeback:1.0.2'
##### 5. [手势交互](https://github.com/aritraroy/PatternLockView)
      implementation 'com.andrognito.patternlockview:patternlockview:1.0.0'
      implementation 'com.andrognito.patternlockview:patternlockview-reactive:1.0.0'//for RxJava2 adapter
##### 6. [SVG描边动画](http://blog.csdn.net/leaf_130/article/details/54848071)
      implementation 'com.jaredrummler:animated-svg-view:1.0.5'
##### 7. [BGABanner](https://github.com/bingoogolapple/BGABanner-Android)
      implementation 'cn.bingoogolapple:bga-banner:2.2.6'
##### 8. [底部导航按钮点击效果](https://github.com/aurelhubert/ahbottomnavigation)
      implementation 'com.aurelhubert:ahbottomnavigation:2.1.0'
##### 9. [Facebook.rebound 动画](http://facebook.github.io/rebound/)
      implementation 'com.facebook.rebound:rebound:0.3.8'
##### 10. [Retrofit](https://github.com/square/retrofit)
      implementation 'com.squareup.retrofit2:retrofit:2.6.0'
      implementation 'com.squareup.retrofit2:adapter-rxjava2:2.6.0'
#####     [Retrofit定义的gson解析](http://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson)
      implementation 'com.squareup.retrofit2:converter-gson:2.6.0'
##### 11. [Okhttp3日志拦截器](https://github.com/victorfan336/okhttp-logging-interceptor)
      implementation 'com.squareup.okhttp3:logging-interceptor:3.8.1'
##### 12. [Rxjava](https://github.com/ReactiveX/RxJava)
      implementation 'io.reactivex.rxjava2:rxjava:2.2.9'
##### 13. [Rxandroid](https://github.com/ReactiveX/RxAndroid)
      implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
##### 14. [Google Glide](https://github.com/bumptech/glide)
      implementation 'com.github.bumptech.glide:glide:4.9.0'
##### 15. [Zxing 二维码](https://github.com/zxing/zxing)
      implementation 'com.google.zxing:core:3.3.3'
      zxing_core_3.0.1.jar
      implementation 'com.github.yuzhiqiang1993:zxing:2.2.5'
