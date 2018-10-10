package com.name.rmedal.html5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.tools.ContentViewHelper;
import com.veni.tools.StatusBarTools;
import com.veni.tools.base.ActivityJumpOptionsTool;
import com.veni.tools.view.ToastTool;


/**
 * 作者：kkan on 2017/11/30
 * 当前类注释:
 * 网页窗口封装
 */
public class WebViewActivity extends BaseActivity {

    public static final String INTENT_URL = "url";
    public static final String INTENT_TITLE = "title";
    public static final String INTENT_NEEDTITLE = "needtitle";


    public static void startAction(Context context, String url) {
        startAction(context, url, null, true);
    }

    public static void startAction(Context context, String url, String title) {
        startAction(context, url, title, false);
    }

    /**
     * 启动入口
     *
     * @param context   context
     * @param url       网络地址
     * @param title     显示标题
     * @param needtitle 是否获取url的标题
     */
    public static void startAction(Context context, String url, String title, boolean needtitle) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(WebViewActivity.class)
                .setBundle(INTENT_URL, url)
                .setBundle(INTENT_TITLE, title)
                .setBundle(INTENT_NEEDTITLE, needtitle)
                .customAnim()
                .start();
    }

    //不使用xml布局
    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initPresenter() {

    }

    private HTMLWebView mWebView;
    private boolean needtitle;

    @SuppressLint("AddJavascriptInterface")
    @Override
    public void initView(Bundle savedInstanceState) {
        String act_url = getIntent().getStringExtra(INTENT_URL);
        String act_title = getIntent().getStringExtra(INTENT_TITLE);
        act_title = act_title == null || act_title.equals("") ? getResources().getString(R.string.app_name) : act_title;
        needtitle = getIntent().getBooleanExtra(INTENT_NEEDTITLE, false);

        //根布局视图构造器
        ContentViewHelper contentViewHelper = new ContentViewHelper(context, null, R.color.colorPrimary);
        //根布局视图
        LinearLayout baseView = contentViewHelper.getContentView();

        mWebView = new HTMLWebView(context, context);

        // content FrameLayout
        FrameLayout userView = new FrameLayout(context);
        userView.addView(mWebView.getLayout(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        baseView.addView(userView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        //获取网页标题
        setTitle(act_title);
        mWebView.addWebUtilsListener(new WebUtilsListener() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (needtitle) {
                    setTitle(title);
                }
            }
        });
        //WebView Download监听
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //准备javascript注入
        mWebView.addJavascriptInterface(
                new Js2JavaInterface(), "Js2JavaInterface");
        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        } else {
            if (act_url != null) {
                mWebView.loadUrl(act_url);
            }
        }
        setContentView(baseView);
        setSwipeBackLayout(0);

        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        //增加状态栏的高度
        contentViewHelper.initToolbarState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mWebView != null) {
            mWebView.saveState(outState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mWebView != null) {
            mWebView.stopLoading();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.doDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                mWebView.releaseCustomview();
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * JavaScript注入回调
     */
    public class Js2JavaInterface {
        private Context context;
        private String TAG = "Js2JavaInterface";

        /**
         * Js回调 根据需要命名方法和参数
         */
        @JavascriptInterface
        public void showProduct(String productId) {
            if (productId != null) {
                //进行跳转商品详情
                ToastTool.normal("点击的商品的ID为:" + productId);
            } else {
                ToastTool.error("商品ID为空!");
            }
        }
    }
}