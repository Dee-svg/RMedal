package com.name.rmedal.ui;

/**
 * 作者：kkan on 2017/11/30
 * 当前类注释:
 * APP常量
 */
public class AppConstant {
    public static final String startUrl = "https://github.com/skpy5272/RMedal";
    private static final String imgBaseUrl = "https://raw.githubusercontent.com/skpy5272/RMedal/master/appimg/";
    /*测试数据*/
    public static final String imgjson =
            "[{ \"image_url\": \"" + startUrl + "ResImage/raw/master/imageres/1.jpg\"," +
                    "\"image_view_title\": \"图片详情\",\"image_describe\": \"图片赏析1111\"}," +
                    "{\"image_url\": \"" + startUrl + "ResImage/raw/master/imageres/2.jpg\"," +
                    "\"image_view_title\": \"图片详情\",\"image_describe\": \"图片赏析2222\"}," +
                    "{\"image_url\": \"" + startUrl + "ResImage/raw/master/imageres/3.jpg\"," +
                    "\"image_view_title\": \"图片详情\",\"image_describe\": \"图片赏析3333\"}," +
                    "{\"image_url\": \"" + startUrl + "ResImage/raw/master/imageres/4.jpg\"," +
                    "\"image_view_title\": \"图片详情\",\"image_describe\": \"图片赏析4444\"}," +
                    "{\"image_url\": \"" + startUrl + "ResImage/raw/master/imageres/5.jpg\"," +
                    "\"image_view_title\": \"图片详情\",\"image_describe\": \"图片赏析5555\"}," +
                    "{\"image_url\": \"" + startUrl + "ResImage/raw/master/imageres/6.jpg\"," +
                    "\"image_view_title\": \"图片详情\",\"image_describe\": \"图片赏析6666\"}," +
                    "{\"image_url\": \"" + startUrl + "ResImage/raw/master/imageres/7.jpg\"," +
                    "\"image_view_title\": \"图片详情\",\"image_describe\": \"图片赏析7777\"}]";
    public static final String newsjson =
            "{\"path\":\"" + imgBaseUrl + "\"," +
                    "\"image\":\"" + imgBaseUrl + "8.jpg\"," +
                    "\"title\":\"Rxjava+MVP架构   快速开发框架 \"," +
                    "\"passtime\":\"2019-05-16 10:00:41\"}";

    public static final String img_head = imgBaseUrl + "9.jpg";
    public static final String download_url = "http://gdown.baidu.com/data/wisegame/938c368dda77aab2/baidu_48235776.apk";

    /*固定数据*/
    public static final int pageSize = 20;

    /*SP  ACache 键*/
    public static final String PatternlockKey = "Patternlock";//手势密码
    public static final String PatternlockOK = "Patternlock_ok";//是否验证过手势密码
    public static final String YZPatternlock = "YZPatternlock";//验证手势密码
    public static final String CJPatternlock1 = "CJPatternlock1";//创建手势密码
    public static final String CJPatternlock2 = "CJPatternlock2";//创建手势密码

    public static final String FingerprintKey = "FingerprintKey";//指纹密码
    public static final String FingerprintKey1 = "FingerprintKey1";//设置指纹密码
    public static final String YZFingerprint= "YZFingerprint";//验证指纹密码
    public static final String FingerprintOK = "FingerprintOK";//是否验证过指纹密码

    public static final String FIRST_TIME = "first_time";//是否第一次登陆
    public static final String LG_Code = "login_Code";//登录验证码计时
    public static final String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";//TOKEN
    public static final String ScanerCount = "scaner_count";//扫码次数
    public static final String CreateCount = "create_count";//生成二维码次数

    /*浮动按钮 RxBus 事件*/
    public static final String MENU_SHOW_HIDE = "MENU_SHOW_HIDE";

    /*Intent返回值*/
    public static final int GET_IMAGE_FROM_PHONE = 5000;//相册二维码识别
    public static final int REQUEST_QRCODE = 5001;//二维码扫描
    public static final int REQUEST_CODEHEAD = 5002;//头像
    /*Intent传值*/
    public static final String INTENT_WEBVIEWURL = "url";
    public static final String INTENT_WEBVIEWTITLE = "title";
    public static final String INTENT_WEBVIEWNEEDTITLE = "needtitle";
    public static final String INTENT_DATATYPE = "data_type";

}
