package com.veni.tools.widget.html;

import android.webkit.JsResult;

/**
 * Created by kkan on 2017/6/16.
 * Web 回调
 */
public interface WebUtilsListener {
    void onReceivedTitle(String title);

    void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);

    void onJsAlert(String url, String message, JsResult result);
}
