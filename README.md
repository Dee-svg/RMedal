# 工作使我快乐，放假妨碍我进步
**生命在于加班**

## 项目介绍
   - 自己工作中的整理与总结
   - ![ ](https://raw.githubusercontent.com/skpy5272/RMedal/master/appimg/Screenshot_20180910-090138.png)

### 软件架构
   - Retrofit+Rxjava+MVP架构


### 目录结构
1.  [app](https://github.com/skpy5272/RMedal/tree/master/app)
   - api 网络请求封装和请求地址
   - base 基类
   - modelbean 实体类
   - ui 视图 mvp

2.  [tools-library 常用封装工具](https://github.com/skpy5272/RMedal/tree/master/tools-library)
#### 备注个问题
1. 09-10测试的时候 关于SP清除缓存后Acache读取不到文件，加打印后就不复现这问题，以后会留意(已解决)

#### 开发环境信息
1. AS版本3.1
2. gradle 3.1.2

#### 参与贡献

1. KKan (164994601@qq.com)


## 支持鸣谢

##### 1. [GitHub](https://github.com/)
##### 2. [码云](https://gitee.com/explore/recommend?lang=Android)
##### 3. [度娘](https://www.baidu.com/)

## 依赖

##### 1. [butterknife 注解](https://github.com/JakeWharton/butterknife)
      implementation 'com.jakewharton:butterknife:8.0.1'
##### 2. [SmartRefreshLayout下拉刷新上拉加载 ](https://github.com/scwang90/SmartRefreshLayout)
      implementation 'com.scwang.smartrefresh:SmartRefreshLayout:1.0.3'
      implementation 'com.scwang.smartrefresh:SmartRefreshHeader:1.0.3'//没有使用特殊Header，可以不加这行
##### 3. [BRVAH](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
      implementation 'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.30'
##### 4. [SwipeBack侧滑退出](https://github.com/gongwen/SwipeBackLayout)
      implementation 'com.gongwen:swipeback:1.0.2'
##### 5. [手势交互](https://github.com/aritraroy/PatternLockView)
      implementation 'com.andrognito.patternlockview:patternlockview:1.0.0'
      implementation 'com.andrognito.patternlockview:patternlockview-reactive:1.0.0'//for RxJava2 adapter
##### 6. [SVG描边动画](http://blog.csdn.net/leaf_130/article/details/54848071)
      implementation 'com.jaredrummler:animated-svg-view:1.0.5'
##### 7. [BGABanner](https://github.com/bingoogolapple/BGABanner-Android)
      implementation 'cn.bingoogolapple:bga-banner:2.2.4'
##### 8. [底部导航按钮点击效果](https://github.com/aurelhubert/ahbottomnavigation)
      implementation 'com.aurelhubert:ahbottomnavigation:2.1.0'
##### 9. [图片选择](https://github.com/donglua/PhotoPicker)
      implementation 'me.iwf.photopicker:PhotoPicker:0.9.12@aar'
##### 10. [Facebook.rebound 动画](http://facebook.github.io/rebound/)
      implementation 'com.facebook.rebound:rebound:0.3.8'
##### 11. [Retrofit_HTTP请求](http://square.github.io/retrofit/)
      implementation 'com.squareup.retrofit2:retrofit:2.3.0'
      implementation 'com.squareup.retrofit2:adapter-rxjava2:2.3.0'
##### 12. [Gson解析](https://github.com/google/gson)
#####     [Retrofit定义的gson解析](http://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson)
      implementation 'com.squareup.retrofit2:converter-gson:2.3.0'
##### 13. [Okhttp3日志拦截器](https://github.com/victorfan336/okhttp-logging-interceptor)
      implementation 'com.squareup.okhttp3:logging-interceptor:3.8.1'
##### 14. [Rxjava](https://github.com/ReactiveX/RxJava)
      implementation 'io.reactivex.rxjava2:rxjava:2.1.8'
##### 15. [Rxandroid](https://github.com/ReactiveX/RxAndroid)
      implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'
##### 16. [Google Glide](https://github.com/bumptech/glide)
      implementation 'com.github.bumptech.glide:glide:3.7.0'
##### 17. [Zxing 二维码](https://github.com/zxing/zxing)
      zxing.jar
