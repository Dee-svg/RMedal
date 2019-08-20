package com.name.rmedal.tools;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.name.rmedal.BuildConfig;
import com.name.rmedal.api.HttpRespose;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.ui.AppConstant;
import com.veni.tools.LogUtils;
import com.veni.tools.VnUtils;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.EncryptUtils;
import com.veni.tools.util.JsonUtils;
import com.veni.tools.util.SPUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 作者：kkan on 2018/02/26
 * 当前类注释:
 * 依赖中没有的或者需要经常更改的Tools
 */

public class AppTools {

    private static final String TAG = AppTools.class.getSimpleName();
    /**
     * AES 密钥
     */
    public static final String SECRETKEY = "jingtum2017tudou";

    /**
     * SP  用户数据
     */
    public static final String USERDATA = "user_data";


    public static String getToken() {
        return (String) SPUtils.get(VnUtils.getContext(), AppConstant.KEY_ACCESS_TOKEN, "");
    }

    public static void saveToken(Context context, String token) {
        SPUtils.put(context, AppConstant.KEY_ACCESS_TOKEN, token);
    }

    public static void saveUserBean(Context context, String userdata) {
        SPUtils.put(context, USERDATA, userdata);
    }

    public static UserBean getUserBean(Context context) {
        String value = (String) SPUtils.get(context, USERDATA, "");
        UserBean userBean = JsonUtils.parseObject(value, UserBean.class);
        return userBean == null ? new UserBean() : userBean;
    }

    public static<T> Observable<HttpRespose<T>> createObservable(Class<T> clas){
        return  Observable.create(new ObservableOnSubscribe<HttpRespose<T>>() {
            @Override
            public void subscribe(ObservableEmitter<HttpRespose<T>> emitter) throws Exception {
                HttpRespose<T> httpRespose =new HttpRespose<>();
                httpRespose.setCode(200);
                httpRespose.setMessage("");

                emitter.onNext(httpRespose);
                emitter.onComplete();
            }
        });
    }

    public static<T> Observable<HttpRespose<List<T>>> createListObservable(Class<T> clas){
        return  Observable.create(new ObservableOnSubscribe<HttpRespose<List<T>>>() {
            @Override
            public void subscribe(ObservableEmitter<HttpRespose<List<T>>> emitter) throws Exception {
                HttpRespose<List<T>> httpRespose =new HttpRespose<>();
                httpRespose.setCode(200);
                httpRespose.setMessage("");

                emitter.onNext(httpRespose);
                emitter.onComplete();
            }
        });
    }
    public static String getSubUrl(String url) {
        String baseurl = "";
        if (url.startsWith("https://")) {
            baseurl = url.substring(8);
            baseurl = baseurl.substring(0,baseurl.indexOf('/') + 1);
            baseurl="https://"+baseurl;
        } else if (url.startsWith("http://")) {
            baseurl = url.substring(7);
            baseurl = baseurl.substring(0,baseurl.indexOf('/') + 1);
            baseurl="http://"+baseurl;
        }
        return baseurl;
    }

    public static void installApp(Context context, File file) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(Intent.ACTION_VIEW);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static String encAESCode(HashMap<String, Object> param) {
        String content = JsonUtils.toJson(param);
        LogUtils.d(TAG, "加密前数据-->" + content);
        if (content == null) {
            return "";
        }
        byte[] conb = content.getBytes();
        byte[] secreb = SECRETKEY.getBytes();
        String encryptResultStr = EncryptUtils.encryptAES2HexString(conb, secreb);
        LogUtils.d(TAG, "加密后-->" + encryptResultStr);
        return encryptResultStr;
    }

    public static String desAESCode(String content) {
        if (DataUtils.isEmpty(content)) {
            LogUtils.d(TAG, "解密字符为空");
            return "";
        }
        LogUtils.d(TAG, "解密前json数据--->" + content);
        byte[] secreb = SECRETKEY.getBytes();
        byte[] decryptResult = EncryptUtils.decryptHexStringAES(content, secreb);
        String decryptString = null;
        try {
            decryptString = new String(decryptResult, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        LogUtils.d(TAG, "解密后json数据--->" + decryptString);
        return decryptString;
    }
}
