package com.veni.tools.view.imageload;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.veni.tools.DataTools;
import com.veni.tools.R;

import java.io.File;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * 图片加载工具类 使用glide框架封装
 * <p>
 * 缓存策略 diskCacheStrategy(DiskCacheStrategy.ALL)
 * all:缓存源资源和转换后的资源
 * none:不作任何磁盘缓存
 * source:缓存源资源
 * result：缓存转换后的资源
 * DiskCacheStrategy.NONE 什么都不缓存
 * DiskCacheStrategy.SOURCE 仅仅只缓存原来的全分辨率的图像
 * DiskCacheStrategy.RESULT 仅仅缓存最终的图像，即降低分辨率后的（或者是转换后的）
 * DiskCacheStrategy.ALL 缓存所有版本的图像（默认行为）
 * <p>
 * 动态转换 centerCrop()
 * <p>
 * Glide.get(this).clearDiskCache();//清理磁盘缓存 需要在子线程中执行
 * Glide.get(this).clearMemory();//清理内存缓存  可以在UI主线程中进行
 * 
 * Glide.with(context)
 * .load(url)// 加载图片资源
 * //                .skipMemoryCache(false)//是否将图片放到内存中
 * //                .diskCacheStrategy(DiskCacheStrategy.ALL)//磁盘图片缓存策略
 * //                .dontAnimate()//不执行淡入淡出动画
 * .crossFade(100)// 默认淡入淡出动画300ms
 * //                .override(300,300)//图片大小
 * .placeholder(R.drawable.shouye_haibao)// 占位图片
 * //                .error(R.drawable.shouye_haibao)//图片加载错误显示
 * .centerCrop()//  fitCenter()
 * //                .animate()// 执行的动画
 * //                .bitmapTransform(null)// bitmap操作
 * //                .priority(Priority.HIGH)// 当前线程的优先级
 * //                .signature(new StringSignature("ssss"))
 * .into(iv);
 */
public class ImageLoaderTool {

    public static void display(Context context, ImageView imageView, String url, int placeholder, int error) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url).
                placeholder(placeholder)
                .error(error).crossFade().into(imageView);
    }

    public static void displaynullplace(Context context, ImageView imageView, String url) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .error(R.drawable.ic_empty_picture)
                .into(imageView);
    }

    public static void display(Context context, ImageView imageView, String url) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .crossFade().into(imageView);
    }

    public static void displayoverride(Context context, ImageView imageView, String url) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .centerCrop().override(1090, 1090 * 3 / 4)
                .crossFade().into(imageView);
    }

    public static void display(Context context, ImageView imageView, File url) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .crossFade().into(imageView);
    }

    public static void displaySmallPhoto(Context context, ImageView imageView, String url) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url).asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .thumbnail(0.5f)
                .into(imageView);
    }

    public static void displayBigPhoto(Context context, ImageView imageView, String url) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url).asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .into(imageView);
    }

    public static void display(Context context, ImageView imageView, int url) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .crossFade().into(imageView);
    }

    public static void displayCircle(Context context, ImageView imageView, String url) {
        displayCircle(context, imageView, url, R.drawable.toux2);
    }

    /**
     * 圆形图片
     */
    public static void displayCircle(Context context, ImageView imageView, String url, int errimage) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(errimage)
                .centerCrop().transform(new GlideCircleTransfrom(context)).into(imageView);
    }

    public static void displayCircle(Context context, ImageView imageView, int resId) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(resId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.toux2)
                .centerCrop().transform(new GlideCircleTransfrom(context)).into(imageView);
    }

    /**
     * 圆角图片
     */
    public static void displayRound(Context context, ImageView imageView, String url, int rounddp) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.toux2)
                .centerCrop().transform(new GlideRoundTransform(context, rounddp)).into(imageView);
    }

    /**
     * 圆角图片
     */
    public static void displayRound(Context context, ImageView imageView, String url) {
        if(DataTools.IsDestroyed(context)){
            return;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.toux2)
                .centerCrop().transform(new GlideRoundTransform(context)).into(imageView);
    }
}
