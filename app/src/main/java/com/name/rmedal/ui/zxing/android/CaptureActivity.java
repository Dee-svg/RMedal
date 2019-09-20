package com.name.rmedal.ui.zxing.android;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.ui.zxing.bean.ZxingConfig;
import com.name.rmedal.ui.zxing.camera.CameraManager;
import com.name.rmedal.ui.zxing.common.Constant;
import com.name.rmedal.ui.zxing.decode.DecodeImgCallback;
import com.name.rmedal.ui.zxing.decode.DecodeImgThread;
import com.name.rmedal.ui.zxing.decode.ImageUtil;
import com.name.rmedal.ui.zxing.view.ViewfinderView;
import com.veni.tools.LogUtils;
import com.veni.tools.util.ToastTool;
import com.veni.tools.widget.TitleView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;


public class CaptureActivity extends BaseActivity {
    @BindView(R.id.scaner_code_preview)
    SurfaceView scanerCodePreview;
    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.toolbar_line)
    TextView toolbarLine;
    @BindView(R.id.toolbar_title_ll)
    LinearLayout toolbarTitleLl;
    @BindView(R.id.scaner_code_scanerview)
    ViewfinderView scanerCodeScanerview;
    @BindView(R.id.scaner_code_flash_light_tv)
    TextView scanerCodeFlashLightTv;
    @BindView(R.id.scaner_code_flash_light_rl)
    RelativeLayout scanerCodeFlashLightRl;
    @BindView(R.id.scaner_code_album_rl)
    RelativeLayout scanerCodeAlbumRl;
    @BindView(R.id.scaner_code_bottom_ll)
    LinearLayout scanerCodeBottomLl;

    public ZxingConfig config;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
//    private SurfaceHolder surfaceHolder;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void doBeforeContentView() {
        super.doBeforeContentView();

        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLACK);
        }

        permissionTools.chickWrite().chickCamear().initPermission();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_capture;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitleView();

        /*先获取配置信息*/
        try {
            config = (ZxingConfig) getIntent().getExtras().get(Constant.INTENT_ZXING_CONFIG);
        } catch (Exception e) {
            LogUtils.iTag(TAG, "config==" + e.toString());
        }

        if (config == null) {
            config = new ZxingConfig();
        }

        setViewGone();
        scanerCodeScanerview.setZxingConfig(config);

        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);

    }

    @OnClick({R.id.scaner_code_flash_light_rl, R.id.scaner_code_album_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scaner_code_flash_light_rl:
                /*切换闪光灯*/
                cameraManager.switchFlashLight(handler);
                break;
            case R.id.scaner_code_album_rl:
                /*打开相册*/
                if (permissionTools.isEnabledwrite()) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.REQUEST_IMAGE);
                } else {
                    permissionTools.chickWrite().chickCamear().initPermission();
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtils.iTag(TAG, "onResume");

        cameraManager = new CameraManager(context, config);
        scanerCodeScanerview.setCameraManager(cameraManager);
        handler = null;

        SurfaceHolder surfaceHolder = scanerCodePreview.getHolder();

        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
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
                    cameraManager.closeDriver();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    if (hasSurface) {
                        initCamera(holder);
                    }
                }
            });
        }
        inactivityTimer.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.iTag(TAG, "onPause");
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
//        beepManager.close();
        cameraManager.closeDriver();

//        if (!hasSurface) {
//            surfaceHolder.removeCallback(this);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inactivityTimer.shutdown();
        scanerCodeScanerview.stopAnimator();
    }

    private void setViewGone() {
        //显示下方的其他功能布局
        scanerCodeBottomLl.setVisibility(config.isShowbottomLayout() ? View.VISIBLE : View.GONE);
        //显示闪光灯按钮
        scanerCodeFlashLightRl.setVisibility(config.isShowFlashLight() ? View.VISIBLE : View.GONE);
        //显示相册按钮
        scanerCodeAlbumRl.setVisibility(config.isShowAlbum() ? View.VISIBLE : View.GONE);
        //是否有闪光灯
        scanerCodeFlashLightRl.setVisibility(isSupportCameraLedFlash() ? View.VISIBLE : View.GONE);
    }

    private void initTitleView() {
        //使用Margin 就需要在处理下
//        statusBarUtils.clearMargin(toolbarTitleView);
//        barPaddingSmart(toolbarTitleView);
        toolbarTitleView.setTitle(R.string.scan_code);
        toolbarLine.setVisibility(View.GONE);
        toolbarTitleLl.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        toolbarTitleView.setBackgroundColor(ContextCompat.getColor(context, R.color.alpha_80_black));
        toolbarTitleView.setTitleColor(ContextCompat.getColor(context, R.color.white));
        toolbarTitleView.setLeftTextColor(ContextCompat.getColor(context, R.color.white));
        toolbarTitleView.setLeftIcon(R.drawable.ic_back);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        LogUtils.eTag(TAG, "cameraManager.isOpen()==" + cameraManager.isOpen());
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager);
            }
        } catch (IOException ioe) {
            LogUtils.eTag(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            LogUtils.eTag(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.scan_code);
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public ViewfinderView getViewfinderView() {
        return scanerCodeScanerview;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        scanerCodeScanerview.drawViewfinder();
    }


    /**
     * 是否有闪光灯
     */
    private boolean isSupportCameraLedFlash() {
        PackageManager pm = getPackageManager();
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo featureInfo : features) {
                    if (featureInfo != null && PackageManager.FEATURE_CAMERA_FLASH.equals(featureInfo.name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * @param flashState 切换闪光灯图片
     */
    public void switchFlashImg(int flashState) {
        scanerCodeFlashLightTv.setSelected(flashState == Constant.FLASH_OPEN);
        scanerCodeFlashLightTv.setText((flashState == Constant.FLASH_OPEN) ? R.string.close_flash : R.string.open_flash);
    }

    /**
     * @param rawResult 返回的扫描结果
     */
    public void handleDecode(Result rawResult) {

        inactivityTimer.onActivity();
        BeepManager.playBeep(context,config.isPlayBeep(),config.isShake());

        Intent intent = getIntent();
        intent.putExtra("result", rawResult.getText());
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_IMAGE && resultCode == RESULT_OK) {
            String path = ImageUtil.getImageAbsolutePath(this, data.getData());


            new DecodeImgThread(path, new DecodeImgCallback() {
                @Override
                public void onImageDecodeSuccess(Result result) {
                    handleDecode(result);
                }

                @Override
                public void onImageDecodeFailed() {
                    ToastTool.error(R.string.scan_failed_tip);
                }
            }).run();


        }
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
            }
        }
    }
}
