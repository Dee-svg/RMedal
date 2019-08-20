package com.name.rmedal.ui.zxing.encode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

/**
 * 作者：kkan on 2018/9/11 16:48
 * <p>
 * 当前类注释:
 * 二维码&条形码生成方式
 * CodeCreator.builder(context).
 * encode(str).
 * codeFormat(Type.QR).
 * backColor(R.color.white).
 * codeColor(R.color.black)).
 * codeWidth(600).
 * codeHeight(600).
 * logo(logo).
 * into(mIvQrCode);
 */
public class CodeCreator {
    public enum Type {
        QR,
        Bar
    }
    /**
     * 二维码构造器
     */
    public static Builder builder(@NonNull Context context) {
        return new Builder(context);
    }

    public static class Builder {

        private Context context;
        private int backgroundColor = 0xffffffff;

        private int codeColor = 0xff000000;

        private int codeWidth = 1000;

        private int codeHeight = 300;

        private String content;

        private Bitmap logo;

        private int margin=0; //白边大小，取值范围0~4
        /*
         * 条形码
         * BarcodeFormat.CODE_128
         * 二维码
         * BarcodeFormat.QR_CODE
         */
        private Type barcodetype= Type.QR;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder encode(@NonNull String text) {
            this.content = text;
            return this;
        }

        public Builder codeFormat(@NonNull Type barcodetype) {
            this.barcodetype = barcodetype;
            return this;
        }
        public Builder backColor(@ColorRes int backgroundColor) {
            this.backgroundColor = ContextCompat.getColor(context,backgroundColor);;
            return this;
        }

        public Builder codeColor(@ColorRes int codeColor) {
            this.codeColor = ContextCompat.getColor(context,codeColor);;
            return this;
        }

        public Builder codeWidth(int codeWidth) {
            this.codeWidth = codeWidth;
            return this;
        }

        public Builder codeHeight(int codeHeight) {
            this.codeHeight = codeHeight;
            return this;
        }

        public Builder margin(int margin) {
            this.margin = margin;
            return this;
        }

        public Builder logo(Bitmap logo) {
            this.logo = logo;
            return this;
        }

        public Bitmap into(ImageView imageView) {
            BarcodeFormat barcodeFormat;
            if(barcodetype == Type.QR){
                barcodeFormat= BarcodeFormat.QR_CODE;
            }else {
                barcodeFormat= BarcodeFormat.CODE_128;
            }
            Bitmap bitmap = createQRCode(content, codeWidth, codeHeight, backgroundColor, codeColor,barcodeFormat,margin,logo);
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
            return bitmap;
        }

    }

    /*生成二维码*/
    private static Bitmap createQRCode(String content, int code_width, int code_height,
                                       int backgroundColor, int codeColor, BarcodeFormat barcodeFormat, int margin, Bitmap logo) {

        if (TextUtils.isEmpty(content)) {
            return null;
        }
        /*偏移量*/
        int offsetX = code_width / 2;
        int offsetY = code_height / 2;

        /*生成logo*/
        Bitmap logoBitmap = null;

        if (logo != null) {
            Matrix matrix = new Matrix();
            float scaleFactor = Math.min(code_width * 1.0f / 5 / logo.getWidth(), code_height * 1.0f / 5 / logo.getHeight());
            matrix.postScale(scaleFactor, scaleFactor);
            logoBitmap = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
        }


        /*如果log不为null,重新计算偏移量*/
        int logoW = 0;
        int logoH = 0;
        if (logoBitmap != null) {
            logoW = logoBitmap.getWidth();
            logoH = logoBitmap.getHeight();
            offsetX = (code_width - logoW) / 2;
            offsetY = (code_height - logoH) / 2;
        }

        /*指定为UTF-8*/
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, margin);
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(content,
                    barcodeFormat, code_width, code_height, hints);

            // 二维矩阵转为一维像素数组,也就是一直横着排了
            int[] pixels = new int[code_width * code_height];
            for (int y = 0; y < code_height; y++) {
                for (int x = 0; x < code_width; x++) {
                    if (x >= offsetX && x < offsetX + logoW && y >= offsetY && y < offsetY + logoH) {
                        int pixel = logoBitmap.getPixel(x - offsetX, y - offsetY);
                        if (pixel == 0) {
                            if (matrix.get(x, y)) {
                                pixel = codeColor;
                            } else {
                                pixel = backgroundColor;
                            }
                        }
                        pixels[y * code_width + x] = pixel;
                    } else {
                        if (matrix.get(x, y)) {
                            pixels[y * code_width + x] = codeColor;
                        } else {
                            pixels[y * code_width + x] = backgroundColor;
                        }
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(code_width, code_height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, code_width, 0, 0, code_width, code_height);
            return bitmap;


        } catch (WriterException e) {

            System.out.print(e);
            return null;
        }
    }

}
