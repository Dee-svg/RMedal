package com.veni.tools.base.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veni.tools.base.mvp.BasePresenter;
import com.veni.tools.base.mvp.TUtil;
import com.veni.tools.baserx.RxManager;
import com.veni.tools.listener.AntiShake;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.PermissionsUtils;
import com.veni.tools.util.StatusBarUtils;

import java.util.List;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:基类fragment
 */
public abstract class FragmentBase<T extends BasePresenter> extends Fragment {
    /*********************子类实现*****************************/
    //获取布局文件
    public abstract int getLayoutId();

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    public abstract void initPresenter();

    //初始化view
    public abstract void initView(Bundle savedInstanceState);

    public T mPresenter;//Presenter 对象
    public StatusBarUtils statusBarUtils;
    public boolean enabledinternet = false;//网络
    public boolean enabledreadPhoneState = false;//读取手机状态
    public boolean enabledcamear = false;//相机权限
    public boolean enabledwrite = false;//文件读写权限

    protected View rootView;
    public Context context;
    protected String TAG;
    private AlertDialogBuilder dialogBuilder = null;
    public RxManager mRxManager;//Rxjava管理
    protected AntiShake antiShake;//防止重复点击

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        doBeforeContentView();
        if (rootView == null && getLayoutId() != 0) {
            rootView = inflater.inflate(getLayoutId(), container, false);

        }
        doAfterContentView();
        initPresenter();
        initView(savedInstanceState);
        return rootView;
    }

    //设置layout前配置
    @CallSuper
    public void doBeforeContentView() {
        context = getContext();
        TAG = getClass().getSimpleName();
        mRxManager = new RxManager();
        antiShake = new AntiShake();
    }

    //设置layout后配置
    @CallSuper
    public void doAfterContentView() {
        mPresenter = TUtil.getT(this, 0);
        if (mPresenter != null) {
            mPresenter.mContext = this.getActivity();
        }
    }


    // 标题栏不是通用的  标题栏高度需要在每个Fragment或者侧滑菜单中设置
    public void immersive(View view, boolean dark) {
        if (statusBarUtils == null) {
            statusBarUtils = new StatusBarUtils();
        }
//        //设置沉侵状态栏,需在Activity中设置
//        statusBarUtils.immersive(getActivity());
//        //状态栏字体颜色及icon变黑
        statusBarUtils.darkMode(getActivity(), dark);
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
        List<String> permissionList = PermissionsUtils.with(this)
                .addPermission(Manifest.permission.CAMERA)
                .initPermission();
        enabledcamear = DataUtils.isEmpty(permissionList) || !permissionList.contains(Manifest.permission.CAMERA);
    }

    public void chickWrite() {
        List<String> permissionList = PermissionsUtils.with(this)
                .addPermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .initPermission();
        enabledwrite = DataUtils.isEmpty(permissionList) || !permissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void chickInternet() {
        List<String> permissionList = PermissionsUtils.with(this)
                .addPermission(Manifest.permission.INTERNET)
                .initPermission();
        enabledinternet = DataUtils.isEmpty(permissionList) || !permissionList.contains(Manifest.permission.INTERNET);
    }

    public void chickReadPhoneState() {
        List<String> permissionList = PermissionsUtils.with(this)
                .addPermission(Manifest.permission.READ_PHONE_STATE)
                .initPermission();
        enabledreadPhoneState = DataUtils.isEmpty(permissionList) || !permissionList.contains(Manifest.permission.READ_PHONE_STATE);
    }

    public void startActivity(@NonNull Class<? extends Activity> activity) {
        startActivity(activity,new JumpOptions());
    }

    public void startActivityFinish(@NonNull Class<? extends Activity> activity) {
        startActivity(activity,new JumpOptions().setFinish(true));
    }

    public void startActivity(@NonNull Class<? extends Activity> activity,
                              JumpOptions jumpOptions) {
        Intent intent = jumpOptions.getIntent(context,activity);
        startActivity(intent);
        if(jumpOptions.isFinishFlag()){
            ((Activity)context).finish();
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mRxManager != null) {
            mRxManager.clear();
        }
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        //关闭Dialog
        destroyDialogBuilder();
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
