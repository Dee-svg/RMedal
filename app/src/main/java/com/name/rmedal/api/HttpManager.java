package com.name.rmedal.api;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.veni.tools.FutileTools;
import com.veni.tools.LogTools;
import com.veni.tools.SPTools;
import com.veni.tools.baserx.BasicParamsInterceptor;
import com.veni.tools.baserx.MyHttpLoggingInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者：kkan on 2017/11/30
 * 当前类注释:
 * 使用Retrofit 封装 的网络请求
 * 用例:
 * HttpManager.getOkHttpUrlService() .compose(RxSchedulers.<HttpRespose<Bean>io_main())
 * .subscribe(new RxSubscriber<Bean>(mContext, "加载框信息,不传不显示") {
 * public void _onNext(Bean data) {
 * //处理返回数据,根据需要返回给页面
 * }
 * public void _onError(int code, String message) {
 * //处理异常数据
 * mView.onError(code, message);
 * }
 * });
 */
public class HttpManager {
    //读超时长，单位：毫秒
    private static final int READ_TIME_OUT = 7676;
    //连接时长，单位：毫秒
    private static final int CONNECT_TIME_OUT = 7676;
    private static final String TAG = HttpManager.class.getSimpleName();

    /*服务器跟地址*/
    private static final String BASE_URL = "http://123.56.190.116:8082/api/";
    private volatile static HttpManager INSTANCE;

    private HttpUrlService httpUrlService;
    private OkHttpClient okHttpClient;


    /* 获取单例*/
    public static HttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager(BASE_URL);
                }
            }
        }
        return INSTANCE;
    }

    /*--------------公共参数,只添加请求头--------------*/
    private  String TOKEN = "";

    public HttpManager setToken(String token) {
        TOKEN = token;
        return INSTANCE;
    }

    //构造方法私有
    private HttpManager(String BaseUrl) {
        //缓存
        File cacheFile = new File(FutileTools.getContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
        //1.处理没有认证  http 401 Not Authorised 只增加头部信息
        Authenticator mAuthenticator2 = new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                if (responseCount(response) >= 2) {
                    // If both the original call and the call with refreshed token failed,it will probably keep failing, so don't try again.
                    return null;
                }
//                    refreshToken();
                return response.request().newBuilder()
                        .header("Authorization", TOKEN)
                        .build();
            }
        };
        //2. 请求的拦截处理
        /*
         * 如果你的 token 是空的，就是还没有请求到 token，比如对于登陆请求，是没有 token 的，
         * 只有等到登陆之后才有 token，这时候就不进行附着上 token。另外，如果你的请求中已经带有验证 header 了，
         * 比如你手动设置了一个另外的 token，那么也不需要再附着这一个 token.
         */
        Interceptor mRequestInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                if (TextUtils.isEmpty(TOKEN)) {
                    TOKEN = (String) SPTools.get(FutileTools.getContext(), AppConstant.KEY_ACCESS_TOKEN, "");
                }
                /*
                 * TOKEN == null，Login/Register noNeed Token
                 * noNeedAuth(originalRequest)    refreshToken api request is after log in before log out,but  refreshToken api no need auth
                 */
                if (TextUtils.isEmpty(TOKEN) || alreadyHasAuthorizationHeader(originalRequest) || noNeedAuth(originalRequest)) {
                    Response originalResponse = chain.proceed(originalRequest);
                    return originalResponse.newBuilder()
                            //get http request progress,et download app
                            .build();
                }
                Request authorisedRequest = originalRequest.newBuilder()
                        .header("Authorization", TOKEN)
                        .header("Connection", "Keep-Alive")  //新添加，time-out默认是多少呢？
                        .header("Content-Encoding", "gzip")  //使用GZIP 压缩内容，接收不用设置啥吧
                        .build();

                Response originalResponse = chain.proceed(authorisedRequest);

                //把统一拦截的header 打印出来
                new MyHttpLoggingInterceptor().logInterceptorHeaders(authorisedRequest);

                return originalResponse.newBuilder().build();
            }
        };
        // 添加公共参数 请求头和请求参数都增加
        BasicParamsInterceptor basicParamsInterceptor = new BasicParamsInterceptor.Builder()
//                .addHeaderParams("userName","123321")//添加公共参数
//                .addHeaderParams("device","")
                .build();

        //开启Log
        MyHttpLoggingInterceptor logInterceptor = new MyHttpLoggingInterceptor();
        logInterceptor.setLevel(MyHttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(mRequestInterceptor)
                .addInterceptor(basicParamsInterceptor)
                .addInterceptor(logInterceptor)
                .authenticator(mAuthenticator2)
                .cache(cache)
                .build();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls().create();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BaseUrl)
                .build();
        httpUrlService = retrofit.create(HttpUrlService.class);
    }

    /**
     * HttpUrlService
     */
    public HttpUrlService getOkHttpUrlService() {
        return getInstance().httpUrlService;
    }

    /**
     * OkHttpClient
     */
    private OkHttpClient getOkHttpClient() {
        return getInstance().okHttpClient;
    }

    /**
     * If both the original call and the call with refreshed token failed,it will probably keep failing, so don't try again.
     * count times ++
     *
     * @param response
     * @return
     */
    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

    /**
     * check if already has oauth header
     *
     * @param originalRequest
     */
    private static boolean alreadyHasAuthorizationHeader(Request originalRequest) {
        if (originalRequest.headers().toString().contains("Authorization")) {
            LogTools.w(TAG, "already add Auth header");
            return true;
        }
        return false;
    }

    /**
     * some request after login/oauth before logout
     * but they no need oauth,so do not add auth header
     *
     * @param originalRequest
     */
    private static boolean noNeedAuth(Request originalRequest) {
        if (originalRequest.headers().toString().contains("NeedOauthFlag")) {
            LogTools.d(TAG, "no need auth !");
            return true;
        }
        return false;
    }
}