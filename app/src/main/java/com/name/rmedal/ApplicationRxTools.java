package com.name.rmedal;

import android.app.Application;

import com.veni.tools.FutileTools;
import com.veni.tools.LogTools;

/**
 * Created by vonde on 2016/12/23.
 */

public class ApplicationRxTools extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化FutileTools,获取全文context 和内部初始化的CrashLogTools(崩溃日志处理工具)
        FutileTools.init(this);
        /*初始化LogTools
         *BuildConfig.LOG_DEBUG 为false时日志不打印 *
         * 默认release包该值为false *
         * debug包该值为true *
         * build.gradle中已设置*/
        LogTools.init(this, BuildConfig.LOG_DEBUG, false);
    }

}
