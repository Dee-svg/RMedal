package com.name.rmedal.ui.zxing;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.name.rmedal.R;
import com.name.rmedal.api.AppConstant;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.tools.AnimationTools;
import com.name.rmedal.tools.zxing.BeepTools;
import com.name.rmedal.tools.zxing.QrBarDecoder;
import com.name.rmedal.tools.zxing.scancode.CameraManager;
import com.name.rmedal.tools.zxing.scancode.CaptureActivityHandler;
import com.name.rmedal.tools.zxing.scancode.decoding.InactivityTimer;
import com.veni.tools.DataTools;
import com.veni.tools.LogTools;
import com.veni.tools.PermissionsTools;
import com.veni.tools.StatusBarTools;
import com.veni.tools.view.ToastTool;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 * 扫描二维码
 */
public class ScanerCodeActivity extends BaseActivity {

    @BindView(R.id.scaner_code_preview)
    SurfaceView scanerCodePreview;
    @BindView(R.id.scaner_code_capture_line)
    ImageView scanerCodeCaptureLine;
    @BindView(R.id.scaner_code_capture_layout)
    RelativeLayout scanerCodeCaptureLayout;//扫描框根布局
    @BindView(R.id.scaner_code_help_ll)
    LinearLayout scanerCodeHelpLl;//生成二维码 & 条形码 布局
    @BindView(R.id.scaner_code_capture_containter)
    RelativeLayout scanerCodeCaptureContainter;//整体根布局

    @Override
    public int getLayoutId() {
        return R.layout.activity_scaner_code;
    }

    @Override
    public void initPresenter() {

    }

    private InactivityTimer inactivityTimer;
    private CaptureActivityHandler handler;//扫描处理
    private int mCropWidth = 0;//扫描边界的宽度
    private int mCropHeight = 0;//扫描边界的高度
    private boolean hasSurface;//是否有预览
    private boolean vibrate = false;//扫描成功后是否震动
    private boolean mFlashing = true;//闪光灯开启状态
    private boolean miswes = false;//文件读写权限 true 通过

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        //增加状态栏的高度
        StatusBarTools.setPaddingSmart(this, scanerCodePreview);
        initView();//界面控件初始化
        initScanerAnimation();//扫描动画初始化
        CameraManager.init(context);//初始化 CameraManager
        hasSurface = false;
        vibrate = true;
        inactivityTimer = new InactivityTimer(this);
    }

    @OnClick({R.id.scaner_code_top_mask, R.id.scaner_code_top_back, R.id.scaner_code_top_openpicture})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scaner_code_top_mask://闪光灯 按钮
                light();
                break;
            case R.id.scaner_code_top_back:
                onBackPressed();
                break;
            case R.id.scaner_code_top_openpicture:
                if (miswes) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, AppConstant.GET_IMAGE_FROM_PHONE);
                } else {
                    PermissionsTools.with(context)
                            .addPermission(Manifest.permission.CAMERA)
                            .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .initPermission();
                }
                break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = scanerCodePreview.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);//Camera初始化
        } else {
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (!hasSurface) {
                        hasSurface = true;
                        initCamera(holder);
                    }
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    hasSurface = false;

                }
            });
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    //========================================打开本地图片识别二维码 end=================================
    //--------------------------------------打开本地图片识别二维码 start---------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) { // Successfully.
            if (requestCode == AppConstant.GET_IMAGE_FROM_PHONE) {//扫码
                ContentResolver resolver = getContentResolver();
                // 照片的原始资源地址
                Uri originalUri = data.getData();
                try {
                    // 使用ContentProvider通过URI获取原始图片
                    Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                    // 开始对图像资源解码
                    Result rawResult = QrBarDecoder.decodeFromPhoto(photo);
                    if (rawResult != null) {
                        initDialogResult(rawResult);
                    } else {
                        ToastTool.success("图片识别失败!");
                    }
                } catch (IOException ignored) {
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) { // User canceled.
            LogTools.e(TAG, " User canceled.");
        }
    }

    //==============================================================================================解析结果 及 后续处理 end
    private void initView() {
        //请求Camera权限 与 文件读写 权限
        List<String> permissionList = PermissionsTools.with(context)
                .addPermission(Manifest.permission.CAMERA)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .initPermission();
        miswes = DataTools.isEmpty(permissionList) ||!permissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /*
     * 注册权限申请回调
     *
     * @param requestCode  申请码
     * @param permissions  申请的权限
     * @param grantResults 结果
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                String camera = permissions[i];
                if (camera.equals(Manifest.permission.CAMERA) && i < grantResults.length) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {//拒绝相机权限
                        finish();
                    }
                }
                if (camera.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && i < grantResults.length) {
                    //通过文件读写权限
                    miswes = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }
        }
    }

    private void initScanerAnimation() {
        AnimationTools.ScaleUpDowm(scanerCodeCaptureLine);
    }

    public int getCropWidth() {
        return mCropWidth;
    }

    public void setCropWidth(int cropWidth) {
        mCropWidth = cropWidth;
        CameraManager.FRAME_WIDTH = mCropWidth;

    }

    public int getCropHeight() {
        return mCropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.mCropHeight = cropHeight;
        CameraManager.FRAME_HEIGHT = mCropHeight;
    }

    private void light() {
        if (mFlashing) {
            mFlashing = false;
            // 开闪光灯
            CameraManager.get().openLight();
        } else {
            mFlashing = true;
            // 关闪光灯
            CameraManager.get().offLight();
        }

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            Point point = CameraManager.get().getCameraResolution();
            AtomicInteger width = new AtomicInteger(point.y);
            AtomicInteger height = new AtomicInteger(point.x);
            int cropWidth = scanerCodeCaptureLayout.getWidth() * width.get() / scanerCodeCaptureContainter.getWidth();
            int cropHeight = scanerCodeCaptureLayout.getHeight() * height.get() / scanerCodeCaptureContainter.getHeight();
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
        } catch (IOException | RuntimeException ioe) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(ScanerCodeActivity.this);
        }
    }

    private void initDialogResult(Result result) {
        BarcodeFormat type = result.getBarcodeFormat();
        String realContent = result.getText();
        if (BarcodeFormat.QR_CODE.equals(type)) {//二维码扫描结果
            LogTools.v("二维码", realContent);
        } else if (BarcodeFormat.EAN_13.equals(type)) {//条形码扫描结果
            LogTools.v("条形码", realContent);
        } else {//扫描结果
            LogTools.v("扫描结果", realContent);
        }
        if (realContent.equals("")) {
            ToastTool.error("扫描失败!");
//        if (handler != null) {
//            // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
//            handler.sendEmptyMessage(R.id.restart_preview);
//        }
        } else {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("result", realContent);
            resultIntent.putExtras(bundle);
            this.setResult(Activity.RESULT_OK, resultIntent);
        }
        this.finish();
    }

    public void handleDecode(Result result) {
        inactivityTimer.onActivity();
        BeepTools.playBeep(context, vibrate);//扫描成功之后的振动与声音提示
        initDialogResult(result);
    }

    public Handler getHandler() {
        return handler;
    }

}