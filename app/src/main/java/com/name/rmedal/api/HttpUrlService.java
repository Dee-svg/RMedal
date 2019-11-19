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
 * <p>
 * 参考 https://blog.csdn.net/u010566681/article/details/52105536
 */
public interface HttpUrlService {
    //版本更新
    @GET("system/checkVersion")
    Observable<HttpRespose<CheckVersionBean>> checkVersion(@QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST("system/login")
    Observable<HttpRespose<UserBean>> getLogin(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("system/getUserData")
    Observable<HttpRespose<UserBean>> getUserData(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("system/registUser")
    Observable<HttpRespose<UserBean>> registUser(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("system/getCaptcha")
    Observable<HttpRespose<UserBean>> getCaptcha(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("system/getWangYiNews")
    Observable<HttpRespose<List<NewsBean>>> getWangYiNews(@FieldMap Map<String, String> map);

    /***示例***/
    @GET("system/xxxx")
    Observable<HttpRespose<CheckVersionBean>> get1(@QueryMap Map<String, String> map);

    @GET("system/xxxxx")
    Observable<HttpRespose<UserBean>> get2(@Header("cookie") String session, @Query("name") String name);

    @FormUrlEncoded
    @POST("system/xxxxx")
    Observable<HttpRespose<UserBean>> post1(@Field("data") String data, @Field("deviceToken") String deviceToken);

    //POST文件上传
    @FormUrlEncoded
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("system/xxxxx")
    Observable<HttpRespose<List<UserBean>>> post2(@PartMap Map<String, RequestBody> params, @Part("password") RequestBody password);

    @Multipart
    @POST("system/xxxxx")
    Observable<HttpRespose<UserBean>> post3(@Part MultipartBody.Part photo);

    //apk下载
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

}
