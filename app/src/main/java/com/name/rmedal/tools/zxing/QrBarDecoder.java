package com.name.rmedal.tools.zxing;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.name.rmedal.tools.zxing.scancode.BitmapLuminanceSource;
import com.veni.tools.ImageTools;

import java.util.Hashtable;
import java.util.Vector;

/**
 * 作者：kkan on 2018/9/11 16:48
 * <p>
 * 当前类注释:
 * 解析图片中的 二维码 或者 条形码
 */
public class QrBarDecoder {

    /**
     *
     * @param photo 待解析的图片
     * @return Result 解析结果，解析识别时返回NULL
     */
    public static Result decodeFromPhoto(Bitmap photo) {
        Result rawResult = null;
        Bitmap bitmap =Bitmap.createBitmap(photo);
        if (bitmap != null) {
            Bitmap smallBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2);// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
            bitmap.recycle(); // 释放原始图片占用的内存，防止out of memory异常发生

            MultiFormatReader multiFormatReader = new MultiFormatReader();

            // 解码的参数
            Hashtable<DecodeHintType, Object> hints = new Hashtable<>(2);
            // 可以解析的编码类型
            Vector<BarcodeFormat> decodeFormats = new Vector<>();
            if (decodeFormats.isEmpty()) {
                decodeFormats = new Vector<>();

                Vector<BarcodeFormat> product_formats = new Vector<>(5);
                product_formats.add(BarcodeFormat.UPC_A);
                product_formats.add(BarcodeFormat.UPC_E);
                product_formats.add(BarcodeFormat.EAN_13);
                product_formats.add(BarcodeFormat.EAN_8);
                // product_formats.add(BarcodeFormat.RSS14);
                Vector<BarcodeFormat> one_d_formats = new Vector<>(product_formats.size() + 4);
                one_d_formats.addAll(product_formats);
                one_d_formats.add(BarcodeFormat.CODE_39);
                one_d_formats.add(BarcodeFormat.CODE_93);
                one_d_formats.add(BarcodeFormat.CODE_128);
                one_d_formats.add(BarcodeFormat.ITF);
                Vector<BarcodeFormat> qr_code_formats = new Vector<>(1);
                qr_code_formats.add(BarcodeFormat.QR_CODE);
                Vector<BarcodeFormat> data_matrix_formats = new Vector<>(1);
                data_matrix_formats.add(BarcodeFormat.DATA_MATRIX);

                // 这里设置可扫描的类型，我这里选择了都支持
                decodeFormats.addAll(one_d_formats);
                decodeFormats.addAll(qr_code_formats);
                decodeFormats.addAll(data_matrix_formats);
            }
            hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
            // 设置继续的字符编码格式为UTF8
            // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
            // 设置解析配置参数
            multiFormatReader.setHints(hints);

            // 开始对图像资源解码
            try {
                rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(smallBitmap))));
            } catch (Exception ignored) {
            }
        }
        return rawResult;
    }
}
