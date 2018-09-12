package com.name.rmedal.tools.zxing;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 作者：kkan on 2018/9/11 16:48
 * <p>
 * 当前类注释:
 * 二维码&条形码生成方式
 * QRCode.builder(str).
 * backColor(getResources().getColor(R.color.white)).
 * codeColor(getResources().getColor(R.color.black)).
 * codeSide(600).
 * into(mIvQrCode);
 */
public class QrBarEncoder {
    public enum Type {
        QR,
        Bar
    }
    /**
     * 获取二维码建造者
     *
     * @param text 样式字符串文本
     * @return {@link Builder}
     */
    public static Builder builder(@NonNull CharSequence text) {
        return new Builder(text);
    }

    public static class Builder {

        private int backgroundColor = 0xffffffff;

        private int codeColor = 0xff000000;

        private int codeWidth = 1000;

        private int codeHeight = 300;

        private CharSequence content;

        /*
         * 条形码
         * BarcodeFormat.CODE_128
         * 二维码
         * BarcodeFormat.QR_CODE
         */
        private Type barcodetype= Type.QR;

        public Builder codeFormat(@NonNull Type barcodetype) {
            this.barcodetype = barcodetype;
            return this;
        }
        public Builder backColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder codeColor(int codeColor) {
            this.codeColor = codeColor;
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

        public Builder(@NonNull CharSequence text) {
            this.content = text;
        }

        public Bitmap into(ImageView imageView) {
            BarcodeFormat barcodeFormat;
            if(barcodetype == Type.QR){
                barcodeFormat=BarcodeFormat.QR_CODE;
            }else {
                barcodeFormat=BarcodeFormat.CODE_128;
            }
            Bitmap bitmap = creatCodeFormat(content, codeWidth, codeHeight, backgroundColor, codeColor,barcodeFormat);
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
            return bitmap;
        }
    }

    private static Bitmap creatCodeFormat(CharSequence content, int code_width, int code_height, int backgroundColor, int codeColor,BarcodeFormat barcodeFormat) {
        Bitmap bitmap = null;
        // 判断URL合法性
        if (content == null || content.length() < 1) {
            return null;
        }
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 容错级别 这里选择最高H级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            // 图像数据转换，使用了矩阵转换 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            BitMatrix bitMatrix = writer.encode(content.toString(), barcodeFormat, code_width, code_height, hints);
            int[] pixels = new int[code_width * code_height];
//             下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < code_height; y++) {
                for (int x = 0; x < code_width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * code_width + x] = codeColor;
                    } else {
                        pixels[y * code_width + x] = backgroundColor;
                    }
                }
            }
            bitmap = Bitmap.createBitmap(code_width, code_height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, code_width, 0, 0, code_width, code_height);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
