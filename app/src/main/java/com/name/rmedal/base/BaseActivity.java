package com.name.rmedal.base;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.name.rmedal.R;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.tools.PermissionTools;
import com.veni.tools.base.mvp.BasePresenter;
import com.veni.tools.base.ui.ActivityBase;
import com.veni.tools.util.PermissionsUtils;
import com.veni.tools.widget.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * 基类Activity
 */
public abstract class BaseActivity<T extends BasePresenter> extends ActivityBase<T> {

    @Nullable
    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @Nullable
    @BindView(R.id.toolbar_title_ll)
    LinearLayout toolbarTitleLl;

    public UserBean userBean;
    public PermissionTools permissionTools;

    @Override
    public void doBeforeContentView() {
        super.doBeforeContentView();
        permissionTools = new PermissionTools(this);
    }

    @Override
    public void doAfterContentView() {
        super.doAfterContentView();
        ButterKnife.bind(this);
        upToolBarLeftFinish();
        userBean = AppTools.getUserBean(context);
        immersive(toolbarTitleView, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userBean = AppTools.getUserBean(context);
    }

    public void upToolBarLeftFinish(){
        if (toolbarTitleView != null) {
            toolbarTitleView.setLeftIconVisibility(true);
            toolbarTitleView.setLeftTextVisibility(true);
            //设置返回点击事件
            toolbarTitleView.setLeftFinish(context);
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
        if (requestCode == PermissionsUtils.PermissionsRequestCode) {

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                //grantResults[i] == PackageManager.PERMISSION_DENIED 拒绝权限
                //grantResults[i] == PackageManager.PERMISSION_GRANTED 通过权限
                boolean granted=grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //通过文件读写权限

                }else if (permission.equals(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)) {

                }
            }
        }
    }
}
