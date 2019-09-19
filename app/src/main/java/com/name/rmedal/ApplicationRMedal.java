package com.name.rmedal;

import android.support.multidex.MultiDexApplication;

import com.name.rmedal.tools.CrashLogTools;
import com.veni.tools.LogUtils;
import com.veni.tools.VnUtils;

/**
 * 作者：kkan on 2017/12/04 10:36
 * 当前类注释:
 */

public class ApplicationRMedal  extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化VnUtils  BuildConfig.DEBUG 为false时日志不打印
        VnUtils.init(this, BuildConfig.DEBUG,
                false,false,
                true, LogUtils.LogLevel.V, LogUtils.LogLevel.D, LogUtils.LogLevel.I);
        /*
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }*/

        //崩溃日志收集
        CrashLogTools.init(this);

    }
}
