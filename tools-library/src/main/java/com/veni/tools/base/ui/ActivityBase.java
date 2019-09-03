package com.veni.tools.base.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gw.swipeback.SwipeBack;
import com.gw.swipeback.annotations.EnableSwipeBack;
import com.gw.swipeback.annotations.SetSwipeParameter;
import com.veni.tools.R;
import com.veni.tools.base.mvp.BasePresenter;
import com.veni.tools.base.mvp.TUtil;
import com.veni.tools.baserx.RxManager;
import com.veni.tools.listener.AntiShake;
import com.veni.tools.util.ActivityTManager;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.PermissionsUtils;
import com.veni.tools.util.StatusBarUtils;

import java.util.List;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * 基类Activity
 */
@EnableSwipeBack()
@SetSwipeParameter(isSwipeFromEdge = true)
public abstract class ActivityBase<T extends BasePresenter>  extends AppCompatActivity {
    /*--------------- 子类实现 ---------------*/
    /**
     * 获取布局文件
     */
    public abstract int getLayoutId();
    /**
     * 简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
     */
    public abstract void initPresenter();
    /**
     * 初始化view
     */
    public abstract void initView(Bundle savedInstanceState);

    public T mPresenter;//Presenter 对象
    public StatusBarUtils statusBarUtils;
    public boolean enabledinternet = false;//网络
    public boolean enabledreadPhoneState = false;//读取手机状态
    public boolean enabledcamear = false;//相机权限
    public boolean enabledwrite = false;//文件读写权限

    public ActivityBase context;
    private AlertDialogBuilder dialogBuilder = null;
    public String TAG;
    public RxManager mRxManager;//Rxjava管理
    public AntiShake antiShake;//防止重复点击

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeContentView();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        doAfterContentView();
        this.initPresenter();
        this.initView(savedInstanceState);
    }
    //设置layout前配置
    @CallSuper
    public void doBeforeContentView() {
        // 设置竖屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this;
        TAG = context.getClass().getSimpleName();
        ActivityTManager.get().add(context);
        mRxManager = new RxManager();
        antiShake = new AntiShake();
    }

    //设置layout后配置
    @CallSuper
    public void doAfterContentView() {
        mPresenter = TUtil.getT(this, 0);
        if (mPresenter != null) {
            mPresenter.mContext = this;
        }
    }

    //关闭侧滑返回
    public void swipeDragToClose() {
        SwipeBack.enableDragToClose(context, false);
    }

    // 标题栏不是通用的  标题栏高度需要在每个Fragment或者侧滑菜单中设置
    public void immersive(View view, boolean dark) {
        if (statusBarUtils == null) {
            statusBarUtils = new StatusBarUtils();
        }
        //设置沉侵状态栏,
        statusBarUtils.immersive(context);
        //状态栏字体颜色及icon变黑
        statusBarUtils.darkMode(context, dark);
        //增加状态栏的高度
        barPaddingSmart(view);
    }

    //增加状态栏的高度
    public void barPaddingSmart(View view) {
        if (view != null) {
            //使用view背景色沉浸{@link #@drawable/bg_toolbar_blue},
            statusBarUtils.setPaddingSmart(context, view);
        }
    }
    //增加状态栏的高度
    public void barMargin(View view) {
        if (view != null) {
            //使用父布局背景色沉浸{@link #@color/colorPrimaryDark},
            statusBarUtils.setMargin(context, view);
        }
    }

    public void chickCamear() {
        List<String> permissionList = PermissionsUtils.with(context)
                .addPermission(Manifest.permission.CAMERA)
                .initPermission();
        enabledcamear = DataUtils.isEmpty(permissionList) || !permissionList.contains(Manifest.permission.CAMERA);
    }

    public void chickWrite() {
        List<String> permissionList = PermissionsUtils.with(context)
                .addPermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .initPermission();
        enabledwrite = DataUtils.isEmpty(permissionList) || !permissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void chickInternet() {
        List<String> permissionList = PermissionsUtils.with(context)
                .addPermission(Manifest.permission.INTERNET)
                .initPermission();
        enabledinternet = DataUtils.isEmpty(permissionList) || !permissionList.contains(Manifest.permission.INTERNET);
    }

    public void chickReadPhoneState() {
        List<String> permissionList = PermissionsUtils.with(context)
                .addPermission(Manifest.permission.READ_PHONE_STATE)
                .initPermission();
        enabledreadPhoneState = DataUtils.isEmpty(permissionList) || !permissionList.contains(Manifest.permission.READ_PHONE_STATE);
    }

    public void startActivity(@NonNull Class<? extends Activity> activity) {
        startActivity(activity, new JumpOptions());
    }

    public void startActivityFinish(@NonNull Class<? extends Activity> activity) {
        startActivity(activity, new JumpOptions().setFinish(true));
    }

    public void startActivity(@NonNull Class<? extends Activity> activity,
                              JumpOptions jumpOptions) {
        Intent intent = jumpOptions.getIntent(context, activity);
        startActivity(intent);
        if (jumpOptions.isFinishFlag()) {
            context.finish();
        }
    }

    public String getResString(@StringRes int id) {
        return context.getResources().getString(id);
    }

    /**
     * 获取默认Dialog
     */
    public AlertDialogBuilder creatDialogBuilder() {
        destroyDialogBuilder();
        dialogBuilder = new AlertDialogBuilder(context);
        return dialogBuilder;
    }

    public void destroyDialogBuilder() {
        if (dialogBuilder != null) {
            dialogBuilder.dismissDialog();
            dialogBuilder = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRxManager != null) {
            mRxManager.clear();
        }
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        //关闭Dialog
        destroyDialogBuilder();
        ActivityTManager.get().remove(context);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
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
        if (requestCode == PermissionsUtils.PermissionsRequestCode) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                //grantResults[i] == PackageManager.PERMISSION_DENIED 拒绝权限
                //grantResults[i] == PackageManager.PERMISSION_GRANTED 通过权限

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && i < grantResults.length) {
                    //通过文件读写权限
                    enabledwrite = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                } else if (permission.equals(Manifest.permission.CAMERA) && i < grantResults.length) {
                    //通过相机权限
                    enabledcamear = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                } else if (permission.equals(Manifest.permission.INTERNET)) {
                    if (!enabledinternet) {
                        enabledinternet = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    }
                } else if (permission.equals(Manifest.permission.READ_PHONE_STATE)) {
                    if (!enabledreadPhoneState) {
                        enabledreadPhoneState = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    }
                }
            }
        }
    }
}
