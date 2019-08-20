package com.veni.tools.baserx;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 作者：kkan on 2018/05/21
 * 当前类注释:
 * 下载参数拦截器
 */
public class DownloadInterceptor implements Interceptor {
    private DownloadListener downloadListener;

    public DownloadInterceptor(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(new DownLoadResBody(response.body(), downloadListener)).build();
    }

}
