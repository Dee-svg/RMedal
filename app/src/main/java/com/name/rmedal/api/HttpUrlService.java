package com.name.rmedal.api;


import com.name.rmedal.modelbean.CheckVersionBean;
import com.name.rmedal.modelbean.NewsBean;
import com.name.rmedal.modelbean.UserBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 作者：kkan on 2017/11/30
 * 当前类注释:
 * 请求地址
 * 表单的方式传递键值对@FormUrlEncoded  不能和@Body 同时使用
 * 单文件上传@Multipart  例 @Part MultipartBody.Part photo 不能和@Body 同时使用
 * 多文件上传@PartMap 例 registerUser(@PartMap Map<String, RequestBody> params,  @Part("password") RequestBody password);
 * <p>
 * 使用json 方式请求
 * 更改 Content-Type   @Headers({"Content-Type: application/json","Accept: application/json"})
 * 参数设置为registerUser(@PartMap Map<String, RequestBody> params,  @Part("password") RequestBody password);
 * <p>
 * 表单方式请求
 * 更改@FormUrlEncoded
 * 更改 @POST("system/getLastVersion")
 * 更改 getLastVersion(@FieldMap Map<String, String> map);
 */
public interface HttpUrlService {
    //版本更新
    @GET("system/getLastVersion")
    Observable<HttpRespose<CheckVersionBean>> getLastVersion(@QueryMap Map<String, String> map);

    //版本更新
    @FormUrlEncoded
    @POST("system/getLastVersion")
    Observable<HttpRespose<UserBean>> getUserData(@Field("data") String data, @Field("deviceToken") String deviceToken);
    //POST文件上传
    @FormUrlEncoded
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("system/getLastVersion")
    Observable<HttpRespose<List<UserBean>>> getUserBean(@PartMap Map<String, RequestBody> params, @Part("password") RequestBody password);

    @Multipart
    @POST("system/getLastVersion")
    Observable<HttpRespose<UserBean>> upUserPhoto(@Part MultipartBody.Part photo);

    @GET("system/getLastVersion")
    Observable<HttpRespose<UserBean>> getUserData(@Header("cookie") String session, @Query("name") String name, @Query("password") String password);

    @FormUrlEncoded
    @POST("getWangYiNews")
    Observable<HttpRespose<List<NewsBean>>> getWangYiNews(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("toutiao/index")
    Observable<HttpRespose<UserBean>> getNewslist(@FieldMap Map<String, String> map);

    //apk下载
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

}
