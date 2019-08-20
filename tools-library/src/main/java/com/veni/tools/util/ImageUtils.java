package com.veni.tools.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;


/**
 * Created by kkan on 2016/1/24.
 * 图像工具类
 *
 * 单位转换
 * dpToPx                       : dp转px
 * dip2px                      : dip转px
 * pxTODp                       : px转dp
 * px2dip                      : px转dip
 * sp2px                       : sp转px
 * px2sp                       : px转sp
 */

public class ImageUtils {

    /**
     * dip转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dipToPx(Context context, float dpValue) {
        return dpToPx(context,dpValue);
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dip
     *
     * @param pxValue px值
     * @return dip值
     */
    public static int pxToDip(Context context, float pxValue) {
        return pxToDp(context,pxValue);
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    public static int pxToDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param spValue sp值
     * @return px值
     */
    public static int spToPx(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px转sp
     *
     * @param pxValue px值
     * @return sp值
     */
    public static int pxToSp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * Resize the bitmap
     *
     * @param bitmap 图片引用
     * @param width 宽度
     * @param height 高度
     * @return 缩放之后的图片引用
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }
    public static Bitmap fileToBitmap(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    public static Bitmap fileToBitmap(String filePath, int inSampleSize) {
        return BitmapFactory.decodeFile(filePath,getBitmapOption(inSampleSize)); //将图片的长和宽缩小味原来的1/2
    }

    private static BitmapFactory.Options getBitmapOption(int inSampleSize){
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }
}
