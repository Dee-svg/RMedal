package com.name.rmedal.ui.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.widget.LinearLayout;

import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.veni.tools.LogUtils;
import com.veni.tools.base.ui.JumpOptions;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.ToastTool;
import com.veni.tools.widget.TitleView;
import com.veni.tools.widget.html.HTMLWebView;
import com.veni.tools.widget.html.WebUtilsListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：kkan on 2017/11/30
 * 当前类注释:
 * 网页窗口封装
 */
public class WebViewActivity extends BaseActivity {

    private static final String INTENT_WEBVIEWURL = "url";
    private static final String INTENT_WEBVIEWTITLE = "title";
    private static final String INTENT_WEBVIEWNEEDTITLE = "needtitle";
    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.htmlwebview_web)
    HTMLWebView htmlwebviewWeb;
    @BindView(R.id.webview_ll)
    LinearLayout webviewLl;

    public static JumpOptions startJump(String url) {
        return startJump(url, null, true);
    }

    public static JumpOptions startJumpObtain(String url, String title) {
        return startJump(url, title, true);
    }

    public static JumpOptions startJumpNoObtain(String url, String title) {
        return startJump(url, title, false);
    }

    /**
     * 启动入口
     *
     * @param url       网络地址
     * @param title     显示标题
     * @param needtitle 是否获取url的标题
     */
    public static JumpOptions startJump(String url, String title, boolean needtitle) {
        return new JumpOptions()
                .setBundle(INTENT_WEBVIEWURL, url)
                .setBundle(INTENT_WEBVIEWTITLE, title)
                .setBundle(INTENT_WEBVIEWNEEDTITLE, needtitle);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_htmlwebview;
    }

    @Override
    public void initPresenter() {

    }

    private boolean needtitle;

    @SuppressLint("AddJavascriptInterface")
    @Override
    public void initView(Bundle savedInstanceState) {

        String act_url = getIntent().getStringExtra(INTENT_WEBVIEWURL);
        String act_title = getIntent().getStringExtra(INTENT_WEBVIEWTITLE);
        act_title = DataUtils.isNullString(act_title) ? getResources().getString(R.string.app_name) : act_title;
        needtitle = getIntent().getBooleanExtra(INTENT_WEBVIEWNEEDTITLE, false);

        //获取网页标题
        toolbarTitleView.setTitle(act_title);


        htmlwebviewWeb.addWebUtilsListener(new WebUtilsListener() {
            @Override
            public void onReceivedTitle(String title) {
                if (needtitle) {
                    toolbarTitleView.setTitle(title);
                }
            }

            //WebView Download监听
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                LogUtils.eTag(TAG, "url--" + url);
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onJsAlert(String url, String message, JsResult result) {
                LogUtils.eTag(TAG, "onJsAlert--message" + message);
            }
        });

        //准备javascript注入
        htmlwebviewWeb.addJavascriptInterface(
                new Js2JavaInterface(), "Js2JavaInterface");

        if (savedInstanceState != null) {
            htmlwebviewWeb.restoreState(savedInstanceState);
        } else {
            if (act_url != null) {
                htmlwebviewWeb.loadUrl(act_url);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /**
     * JavaScript注入回调
     */
    public class Js2JavaInterface {
        private Context context;
        private String JsTAG = "Js2JavaInterface";

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (htmlwebviewWeb != null) {
            htmlwebviewWeb.saveState(outState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (htmlwebviewWeb != null) {
            htmlwebviewWeb.onResume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (htmlwebviewWeb != null) {
            htmlwebviewWeb.onStop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (htmlwebviewWeb != null) {
            htmlwebviewWeb.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (htmlwebviewWeb != null) {
            htmlwebviewWeb.doDestroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!htmlwebviewWeb.onKeyDown(keyCode, event)) {
            finish();
        }
        return htmlwebviewWeb.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (htmlwebviewWeb != null) {
            if (htmlwebviewWeb.closeAdWebPage()) {
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

}