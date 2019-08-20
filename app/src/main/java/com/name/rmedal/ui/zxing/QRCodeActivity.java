package com.name.rmedal.ui.zxing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.ui.AppConstant;
import com.name.rmedal.ui.zxing.android.CaptureActivity;
import com.name.rmedal.ui.zxing.bean.ZxingConfig;
import com.name.rmedal.ui.zxing.common.Constant;
import com.name.rmedal.ui.zxing.decode.DecodeImgCallback;
import com.name.rmedal.ui.zxing.decode.DecodeImgThread;
import com.name.rmedal.ui.zxing.encode.CodeCreator;
import com.veni.tools.LogUtils;
import com.veni.tools.util.ACache;
import com.veni.tools.util.ClipboardUtils;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.ImageUtils;
import com.veni.tools.util.ToastTool;
import com.veni.tools.widget.TitleView;
import com.veni.tools.widget.ticker.TickerUtils;
import com.veni.tools.widget.ticker.TickerView;

import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 * 二维码
 */
public class QRCodeActivity extends BaseActivity {

    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.qrcode_create_count)
    TickerView qrcodeCreateCount;//生成二维码次数
    @BindView(R.id.qrcode_scaner_count)
    TickerView qrcodeScanerCount;//扫码次数
    @BindView(R.id.qrcode_create_codeiv)
    ImageView qrcodeCreateCodeiv;
    @BindView(R.id.qrcode_create_codetip)
    TextView qrcodeCreateCodetip;

    @Override
    public int getLayoutId() {
        return R.layout.activity_qrcode;
    }

    @Override
    public void initPresenter() {

    }

    private int create_count;
    private int scaner_count;
    private int bar_height = 300;
    private int bar_width = 1000;
    private int qr_math = 600;

    private Bitmap create_bitmap;

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置显示标题
        toolbarTitleView.setTitle(R.string.show_qrcode);

        Object cc = ACache.get(context).getAsObject(AppConstant.CreateCount);
        Object sc = ACache.get(context).getAsObject(AppConstant.ScanerCount);
        create_count = cc == null ? 0 : (int) cc;
        scaner_count = sc == null ? 0 : (int) sc;
        //设置需要滚动的字符
        qrcodeCreateCount.setCharacterList(TickerUtils.getDefaultNumberList());
        qrcodeScanerCount.setCharacterList(TickerUtils.getDefaultNumberList());
        //初始化数据
        upTickerViews();
        //设置监听
        initListener();
    }

    @OnClick({R.id.qrcode_scaner_ll, R.id.qrcode_create_qr, R.id.qrcode_create_bar})
    public void onViewClicked(View view) {
        if (antiShake.check(view.getId())) return;
        switch (view.getId()) {
            case R.id.qrcode_scaner_ll://扫码

                Intent intent =new Intent(context, CaptureActivity.class);

                ZxingConfig config = new ZxingConfig();
                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                config.setShake(true);//是否震动  默认为true
                config.setDecodeBarCode(true);//是否扫描条形码 默认为true
//                                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
//                                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
//                                config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, AppConstant.REQUEST_QRCODE);
                break;
            case R.id.qrcode_create_qr://生成二维码
                qrcodeCreateCodetip.setText("↓↓长按图片识别二维码↓↓");
                /*
                 * 生成二维码
                 * 1 边长必须 >=  151像素
                 * 否则生成的图片有可能无法识别
                 */

                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                create_bitmap = CodeCreator.builder(context)
                        .encode(getCharAndNumr(100))
                        .backColor(R.color.white)
                        .codeColor(R.color.black)
                        .codeFormat(CodeCreator.Type.QR)
                        .codeWidth(qr_math)
                        .codeHeight(qr_math)
                        .logo(logo)
                        .into(qrcodeCreateCodeiv);;

                upCreateData();
                break;
            case R.id.qrcode_create_bar:
                /*
                 * 生成条形码
                 * 1 宽必须大于高
                 * 2 宽必须 >= 510像素
                 *   宽 等于510像素时 高必须小于宽的1/2
                 * 否则生成的图片有可能无法识别
                 */
                qrcodeCreateCodetip.setText("↓↓长按图片识别条形码↓↓");
                create_bitmap = CodeCreator.builder(context)
                        .encode(getCharAndNumr(18))
                        .backColor(R.color.white)
                        .codeColor(R.color.black)
                        .codeFormat(CodeCreator.Type.Bar)
                        .codeWidth(bar_width)
                        .codeHeight(bar_height)
                        .into(qrcodeCreateCodeiv);
                upCreateData();
                break;
        }
    }

    private void initListener() {

        //设置长按识别二维码条形码
        qrcodeCreateCodeiv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (create_bitmap == null) {
                    return false;
                }
                // 开始对图像资源解码
                new DecodeImgThread(create_bitmap, new DecodeImgCallback() {
                    @Override
                    public void onImageDecodeSuccess(Result result) {
                        initDialogResult(result);
                    }

                    @Override
                    public void onImageDecodeFailed() {
                        ToastTool.error(R.string.scan_failed_tip);
                    }
                }).run();

                return true;
            }
        });
        //获取二维码显示View的宽高
        qrcodeCreateCodeiv.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //只需要获取一次高度，获取后移除监听器
                        qrcodeCreateCodeiv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        //这里高度应该定义为成员变量，定义为局部为展示代码方便
                        bar_height = qrcodeCreateCodeiv.getHeight() - ImageUtils.dpToPx(context, 20);
                        bar_width = qrcodeCreateCodeiv.getWidth() - ImageUtils.dpToPx(context, 20);
                        initQrBarHeight();
                    }
                });
    }

    /**
     * 根据显示View的大小
     * 动态计算二维码条形码边长
     */
    private void initQrBarHeight() {
        //计算qr_math 正方形二维码的宽度
        if (bar_width > bar_height) {
            qr_math = bar_height;
        } else {
            qr_math = bar_width;
        }
        //判断原始高度是否大于原始宽度
        if (bar_height > bar_width) {
            //使宽=高 高=宽
            bar_height = bar_height + bar_width;
            bar_width = bar_height - bar_width;//h
            bar_height = bar_height - bar_width;//w
        }
        //计算bar_height 是否过小
        if (bar_height > bar_width / 2) {
            bar_height = bar_width / 2;
        }
        /*
         * 生成条形码
         * 1 宽必须大于高
         * 2 宽必须 >= 510像素
         *   宽 等于510像素时 高必须小于宽的1/2
         * 否则生成的图片无法识别
         */
        if (bar_width < 510) {
            bar_width = 510;
            bar_height = 251;
        }
        /*
         * 生成二维码
         * 1 边长必须 >=  151像素
         * 否则生成的图片无法识别
         */
        if (qr_math < 151) {
            ToastTool.error("图片位置过小!");
            qr_math = 151;
        }
    }

    private void upTickerViews() {
        qrcodeCreateCount.setText("" + create_count);
        qrcodeScanerCount.setText("" + scaner_count);
    }

    private void initDialogResult(Result result) {
        BarcodeFormat type = result.getBarcodeFormat();
        String realContent = result.getText();
        if (BarcodeFormat.QR_CODE.equals(type)) {//二维码扫描结果
            LogUtils.v("二维码", realContent);
        } else if (BarcodeFormat.EAN_13.equals(type)) {//条形码扫描结果
            LogUtils.v("条形码", realContent);
        } else {//扫描结果
            LogUtils.v("扫描结果", "type---" + type + "--realContent--" + realContent);
        }
        if (realContent.equals("")) {
            ToastTool.error("扫描失败!");
        } else {
            upScanerData(realContent);
        }
    }

    private void upCreateData() {
        create_count++;
        ACache.get(context).put(AppConstant.CreateCount, create_count);
        upTickerViews();
    }

    private void upScanerData(final String zxqrcode) {
        scaner_count++;
        ACache.get(context).put(AppConstant.ScanerCount, scaner_count);
        creatDialogBuilder()
                .setDialog_title("扫描结果")
                .setDialog_message(zxqrcode)
                .setDialog_Left("确定")
                .setDialog_Right("取消")
                .setLeftlistener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastTool.success("扫描结果已复制到剪切板");
                        ClipboardUtils.copyText(context,zxqrcode);
                    }
                })
                .setCancelableOutside(true)
                .builder().show();
        upTickerViews();
    }

    /**
     * java生成随机数字和字母组合
     */
    public String getCharAndNumr(int le) {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < le; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (choice + random.nextInt(26)));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val.append(String.valueOf(random.nextInt(10)));
            }
        }
        return val.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) { // Successfully.
            if (requestCode == AppConstant.REQUEST_QRCODE) {//扫码回调
                Bundle bundle = data.getExtras();
                String zxqrcode = null;
                if (bundle != null) {
                    zxqrcode = bundle.getString("result");
                }
                LogUtils.eTag(TAG, "" + zxqrcode);
                if (!DataUtils.isNullString(zxqrcode)) {
                    upScanerData(zxqrcode);
                } else {
                    ToastTool.success("扫描失败!");
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) { // User canceled.
            LogUtils.eTag(TAG, " User canceled.");
        }
    }
}
