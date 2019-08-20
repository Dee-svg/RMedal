package com.veni.tools.widget.html;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.veni.tools.LogUtils;
import com.veni.tools.R;
import com.veni.tools.VnUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：kkan on 2017/11/30
 * 当前类注释:
 * WebView重写
 */
public class HTMLWebView extends FrameLayout {
    private String TAG = HTMLWebView.class.getSimpleName();
    private FrameLayout mainContent;
    private WebView mainWeb;
    private FrameLayout customContent;
    private FrameLayout frameProgress;
    private TextView tvProgress;

    private View mCustomView;
    private Context mContext;
    private MyWebChromeClient mWebChromeClient;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private List<String> browerUrlList = null;

    public HTMLWebView(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public HTMLWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        //导入布局
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.v_layout_htmlwebview, this);

        mainContent = findViewById(R.id.htmlwebview_main_content);
        mainWeb = findViewById(R.id.htmlwebview_main_web);
        customContent = findViewById(R.id.fullscreen_custom_content);
        frameProgress = findViewById(R.id.htmlwebview_frame_progress);
        tvProgress = findViewById(R.id.htmlwebview_tv_progress);

        mWebChromeClient = new MyWebChromeClient();
        mainWeb.setWebChromeClient(mWebChromeClient);

        mainWeb.setWebViewClient(new MyWebViewClient());

        //WebView Download监听
        mainWeb.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (webUtilsListener != null) {
                    webUtilsListener.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
                }
            }
        });
        VnUtils.setWebSettings(mainWeb);
    }

    public WebView getMainWeb() {
        return mainWeb;
    }

    public void restoreState(Bundle inState) {
        mainWeb.restoreState(inState);
    }

    public void saveState(Bundle outState) {
        mainWeb.saveState(outState);
    }

    public void loadUrl(String url) {
        mainWeb.loadUrl(url);
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    public void addJavascriptInterface(Object obj, String interfaceName) {
        mainWeb.addJavascriptInterface(obj, interfaceName);
    }


    public void onResume() {
        if (mainWeb != null) {
            mainWeb.onResume();
        }
    }

    public void onStop() {
        if (mainWeb != null) {
            mainWeb.freeMemory();
            mainWeb.stopLoading();
        }
    }

    public void onPause() {
        if (mainWeb != null) {
            mainWeb.onPause();
        }
    }

    public void doDestroy() {
        if (mainContent != null) {
            mainContent.removeView(mainWeb);
        }
        if (mainWeb != null) {
            mainWeb.clearView();
            mainWeb.freeMemory();
            mainWeb.removeAllViews();
            mainWeb.destroy();
        }
    }

    /**
     * 释放WebView
     */
    public void releaseCustomview() {
        if (mWebChromeClient != null) {
            mWebChromeClient.onHideCustomView();
        }
        onStop();
    }

    /**
     * 关闭该web页面
     */
    public boolean closeAdWebPage() {
        if (mainWeb.canGoBack()) {
            mainWeb.goBack();
            return false;
        }
        releaseCustomview();
        onStop();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mainWeb.canGoBack()) {
                mainWeb.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            HTMLWebView.this.setVisibility(View.GONE);
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            customContent.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            customContent.setVisibility(View.VISIBLE);

            mCustomViewCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }
            mCustomView.setVisibility(View.GONE);
            customContent.removeView(mCustomView);
            mCustomView = null;
            customContent.setVisibility(View.GONE);
            mCustomViewCallback.onCustomViewHidden();
            HTMLWebView.this.setVisibility(View.VISIBLE);

            super.onHideCustomView();
        }

        /**
         * 网页加载标题回调
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (webUtilsListener != null) {
                webUtilsListener.onReceivedTitle(title);
            }
        }


        /**
         * 网页加载进度回调
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            // 设置进行进度
            ((Activity) mContext).getWindow().setFeatureInt(
                    Window.FEATURE_PROGRESS, newProgress * 100);
            tvProgress.setText("正在加载,已完成" + newProgress + "%...");
            tvProgress.postInvalidate(); // 刷新UI
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            if (webUtilsListener != null) {
                webUtilsListener.onJsAlert(url, message, result);
            }
            return super.onJsAlert(view, url, message, result);

        }
    }

    private WebUtilsListener webUtilsListener;

    /**
     * @param webUtilsListener 监听
     */
    public void addWebUtilsListener(WebUtilsListener webUtilsListener) {
        this.webUtilsListener = webUtilsListener;
    }

    /**
     * 判断是不是浏览器地址
     */
    private boolean getInterceptUrl(String url) {
        if (browerUrlList == null || browerUrlList.size() == 0) {
            browerUrlList = new ArrayList<>();
            browerUrlList.add("http");
            browerUrlList.add("https");
            browerUrlList.add("about");
            browerUrlList.add("javascript");
        }
        Uri mUri = Uri.parse(url);
        return browerUrlList.contains(mUri.getScheme());
    }

    private class MyWebViewClient extends WebViewClient {
        /**
         * 加载过程中 拦截加载的地址url
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //这边因为考虑到之前项目的问题，这边拦截的url过滤掉了zttmall://开头的地址
            //在其他项目中 大家可以根据实际情况选择不拦截任何地址，或者有选择性拦截
            LogUtils.eTag(TAG, "shouldOverrideUrlLoading---url---" + url);
            if (!url.startsWith("zttmall://")) {
                boolean isbrower = getInterceptUrl(url);
                if (isbrower) {
                    return false;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    //如果另外的应用程序WebView，我们可以进行重用
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID,
                            VnUtils.getContext().getPackageName());
                    try {
                        VnUtils.getContext().startActivity(intent);
                        return true;
                    } catch (ActivityNotFoundException ex) {
                    }
                }
                return false;
            } else {
                return true;
            }
        }

        /**
         * 页面加载过程中，加载资源回调的方法
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        /**
         * 页面加载完成回调的方法
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 加载完成隐藏进度界面,显示WebView内容
            frameProgress.post(new Runnable() {
                @Override
                public void run() {
                    frameProgress.setVisibility(View.GONE);
                }
            });
            mainContent.post(new Runnable() {
                @Override
                public void run() {
                    mainContent.setVisibility(View.VISIBLE);
                }
            });
            // 关闭图片加载阻塞
            view.getSettings().setBlockNetworkImage(false);

        }

        /**
         * 页面开始加载调用的方法
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            HTMLWebView.this.requestFocus();
            HTMLWebView.this.requestFocusFromTouch();
        }
    }

}
