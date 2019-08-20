package com.veni.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import com.gw.swipeback.SwipeBack;
import com.veni.tools.listener.OnDelayListener;
import com.veni.tools.util.NetWorkUtils;
import com.veni.tools.util.ToastTool;

import java.util.List;

/**
 * Created by kkan on 2016/1/24.
 * 常用工具类
 */
public class VnUtils {

    private static Application application;
    private static Context context;

    /**
     * 初始化工具类
     *
     * @param application 上下文
     */
    public static void init(Application application, boolean logswitch, boolean logtofile,
                            boolean borderSwitch, boolean logHeadSwitch, @LogUtils.LogLevel int... leave_nos) {

        //初始化侧滑退出
        SwipeBack.init(application);
        VnUtils.application = application;
        VnUtils.context = application;
        LogUtils.getConfig()
                .setLogSwitch(logswitch)
                .setLog2FileSwitch(logtofile)
                .setBorderSwitch(borderSwitch)
                .setGlobalTag("Log")
                .setLogHeadSwitch(logHeadSwitch,leave_nos);
    }

    /**
     * 在某种获取不到 Context 的情况下，即可以使用才方法获取 Context
     * <p>
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        return context;
    }

    public static Application getApp() {
        return application;
    }

    /**
     * content所属的activity是否被销毁
     *
     * @param context 需要判断的content
     * @return
     */
    public static boolean IsDestroyed(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (((Activity) context).isDestroyed()) {
                return true;
            }
        }
        return ((Activity) context).isFinishing();
    }

    //延时任务封装
    public static void delayToDo(long delayTime, final OnDelayListener onDelayListener) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                onDelayListener.doSomething();
            }
        }, delayTime);
    }

    public static void setWebSettings(WebView webView){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);  //开启javascript
        webSettings.setDomStorageEnabled(true);  //开启DOM
        webSettings.setDefaultTextEncodingName("utf-8"); //设置编码
        // // web页面处理
        webSettings.setAllowFileAccess(true);// 支持文件流
        // webSettings.setSupportZoom(true);// 支持缩放
        // webSettings.setBuiltInZoomControls(true);// 支持缩放
        webSettings.setUseWideViewPort(true);// 调整到适合webview大小
        webSettings.setLoadWithOverviewMode(true);// 调整到适合webview大小
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);// 屏幕自适应网页,如果没有这个，在低分辨率的手机上显示可能会异常
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        webSettings.setBlockNetworkImage(true);
        //开启缓存机制
        webSettings.setAppCacheEnabled(true);
        //根据当前网页连接状态
        if (NetWorkUtils.getNetWorkType(context) == NetWorkUtils.NETWORK_WIFI) {
            //设置无缓存
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //设置缓存
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * 获取服务是否开启
     *
     * @param context   上下文
     * @param className 完整包名的服务类名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isRunningService(Context context, String className) {
        // 进程的管理者,活动的管理者
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取正在运行的服务，最多获取1000个
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(1000);
        // 遍历集合
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            ComponentName service = runningServiceInfo.service;
            if (className.equals(service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 注册监听网络状态的广播
     *
     * @param context
     * @return
     */
    public static BroadcastReceiverNetWork initRegisterReceiverNetWork(Context context, Handler handler) {
        // 注册监听网络状态的服务
        BroadcastReceiverNetWork mReceiverNetWork = new BroadcastReceiverNetWork();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiverNetWork, mFilter, "permission.ALLOW_BROADCAST", handler);
        return mReceiverNetWork;
    }

    /**
     * 网络状态改变广播
     */
    public static class BroadcastReceiverNetWork extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int netType = NetWorkUtils.getNetWorkType(context);

            switch (netType) {//获取当前网络的状态
                case NetWorkUtils.NETWORK_WIFI:// wifi的情况下
                    ToastTool.success("切换到wifi环境下");
                    break;
                case NetWorkUtils.NETWORK_2G:
                    ToastTool.info("切换到2G环境下");
                    break;
                case NetWorkUtils.NETWORK_3G:
                    ToastTool.info("切换到3G环境下");
                    break;
                case NetWorkUtils.NETWORK_4G:
                    ToastTool.info("切换到4G环境下");
                    break;
                case NetWorkUtils.NETWORK_NO:
                    ToastTool.error(context, "当前无网络连接").show();
                    break;
                case NetWorkUtils.NETWORK_UNKNOWN:
                    ToastTool.normal("未知网络");
                    break;
            }

        }
    }

    /**
     * Edittext 首位小数点自动加零，最多两位小数
     *
     * @param editText
     */
    public static void setEdTwoDecimal(EditText editText) {
        setEdDecimal(editText, 3);
    }

    public static void setEdDecimal(EditText editText, int count) {
        if (count < 1) {
            count = 1;
        }

        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

        //设置字符过滤
        final int finalCount = count;
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.toString().equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index = dest.toString().indexOf(".");
                    int mlength = dest.toString().substring(index).length();
                    if (mlength == finalCount) {
                        return "";
                    }
                }
                return null;
            }
        }});
    }

    public static void setEditNumberPrefix(final EditText edSerialNumber, final int number) {
        edSerialNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String s = edSerialNumber.getText().toString();
                    String temp = "";
                    for (int i = s.length(); i < number; i++) {
                        s = "0" + s;
                    }

                    for (int i = 0; i < number; i++) {
                        temp += "0";
                    }
                    if (s.equals(temp)) {
                        s = temp.substring(1) + "1";
                    }
                    edSerialNumber.setText(s);
                }
            }
        });
    }

    public static Handler getBackgroundHandler() {
        HandlerThread thread = new HandlerThread("background");
        thread.start();
        Handler mBackgroundHandler = new Handler(thread.getLooper());
        return mBackgroundHandler;
    }
}
