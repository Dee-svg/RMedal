package com.name.rmedal.ui.personal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.name.rmedal.BuildConfig;
import com.name.rmedal.R;
import com.name.rmedal.base.BaseFragment;
import com.name.rmedal.modelbean.CheckVersionBean;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.ui.AppConstant;
import com.name.rmedal.ui.main.LoginActivity;
import com.name.rmedal.ui.personal.contract.PersonalContract;
import com.name.rmedal.ui.personal.presenter.PersonalPresenter;
import com.name.rmedal.ui.web.WebViewActivity;
import com.name.rmedal.ui.zxing.QRCodeActivity;
import com.name.rmedal.widget.ProgressDialog;
import com.veni.tools.listener.OnNoFastClickListener;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.StatusBarUtils;
import com.veni.tools.util.ToastTool;
import com.veni.tools.widget.TitleView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 作者：kkan on 2018/2/24 14:41
 * 当前类注释:
 * 个人中心
 */

public class PersonalFragment extends BaseFragment<PersonalPresenter> implements PersonalContract.View {

    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.toolbar_line)
    View toolbarLine;
    @BindView(R.id.personal_head_layout)
    FrameLayout personalHeadLayout;
    @BindView(R.id.personal_set)
    ImageView personalSet;
    @BindView(R.id.personal_bg_view)
    ImageView personalBgView;
    @BindView(R.id.personal_touxiang)
    ImageView personalTouxiang;
    @BindView(R.id.personal_name)
    TextView personalName;

    /**
     * Fragment 最前端界面显示状态
     *
     * @param hidden {@code true}: 不在最前端界面显示，相当于调用了onPause()
     *               {@code false}: 重新显示到最前端 ,相当于调用了onResume()
     *               进行网络数据刷新  此处执行必须要在 Fragment与Activity绑定了
     *               即需要添加判断是否完成绑定，
     *               否则将会报空（即非第一个显示出来的fragment，虽然onCreateView没有被调用,
     *               但是onHiddenChanged也会被调用，所以如果你尝试去获取活动的话，注意防止出现空指针）
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            upUserData();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_personal;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this);
    }

    private CheckVersionBean versionBean;
    private ProgressDialog progressDialog;
    DrawerLayout mainDrawer;

    @Override
    public void initView(Bundle savedInstanceState) {
        String string = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            string = bundle.getString(AppConstant.INTENT_DATATYPE);
        }
        //隐藏左侧按钮
        toolbarTitleView.setLeftIconVisibility(false);
        toolbarLine.setVisibility(View.GONE);
        if (!DataUtils.isNullString(string) && string.equals("Nav")) {
            toolbarTitleView.setTitle("");
            personalSet.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams lp = toolbarTitleView.getLayoutParams();
            lp.height = StatusBarUtils.getStatusBarHeight(context);
            toolbarTitleView.setLayoutParams(lp);
            toolbarTitleView.setBackgroundColor(Color.parseColor("#009688"));
        } else {
            toolbarTitleView.setRightIconVisibility(true);
            toolbarTitleView.setTitle(R.string.personal_title);
            toolbarTitleView.setBackgroundResource(R.drawable.bg_toolbar_green);
            toolbarTitleView.setRightIcon(R.drawable.ic_set);

            toolbarTitleView.setRightOnClickListener(new OnNoFastClickListener() {
                @Override
                protected void onNoDoubleClick(View view) {
                    startActivity(SettingActivity.class);
                }
            });
        }

        upUserData();
    }

    public void setMainDrawer(DrawerLayout mainDrawer) {
        this.mainDrawer = mainDrawer;
    }

    private void upUserData() {
        UserBean userBean = AppTools.getUserBean(context);
        if (userBean == null) {
            return;
        }
        if (personalName != null) {
            personalName.setText(userBean.getUserName());
        }
        if (personalTouxiang != null) {
            Glide.with(context).load(userBean.getCustomerImg()).apply(new RequestOptions()
                    .error(R.mipmap.ic_touxiang).circleCrop()).into(personalTouxiang);
        }
    }

    @OnClick({R.id.personal_set, R.id.personal_setting, R.id.personal_qrcode, R.id.personal_aboutus,
            R.id.personal_feedback, R.id.personal_update, R.id.personal_exit})
    public void onViewClicked(final View view) {
        if (antiShake.check(view.getId())) return;
        if (mainDrawer != null) {
            mainDrawer.closeDrawer(GravityCompat.START);
            mainDrawer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewClick(view.getId());
                }
            }, 500);
            return;
        }
        viewClick(view.getId());
    }

    private void viewClick(int viewid) {

        switch (viewid) {
            case R.id.personal_set: // 设置
            case R.id.personal_setting: // 设置
                startActivity(SettingActivity.class);
                break;
            case R.id.personal_qrcode:// 生成二维码
                startActivity(QRCodeActivity.class);
                break;
            case R.id.personal_aboutus:// 关于我们
                startActivity(WebViewActivity.class, WebViewActivity.startJump(AppConstant.startUrl));
                break;
            case R.id.personal_feedback:// 问题反馈
                startActivity(FeedBackActivity.class);
                break;
            case R.id.personal_update:// 检测更新
                mPresenter.checkVersion(BuildConfig.VERSION_NAME);
                break;
            case R.id.personal_exit:
                creatDialogBuilder().setDialog_title("温馨提示")
                        .setDialog_message("是否退出应用?")
                        .setDialog_Left("退出")
                        .setLeftlistener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivityFinish(LoginActivity.class);
                            }
                        })
                        .setDialog_Right("取消")
                        .builder().show();
                break;
        }
    }

    private void getdownload(boolean down) {
        if (down) {
            String htmstr = versionBean.getDesc();
            creatDialogBuilder().setDialog_title("更新提示")
                    .setDialog_message(Html.fromHtml(htmstr))
                    .setDialog_Left("更新")
                    .setLeftlistener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPresenter.download(versionBean.getAppDownUrl());
                        }
                    })
                    .setDialog_Right("取消").builder().show();
        }
    }

    @Override
    public void returnVersionData(CheckVersionBean data) {
        versionBean = data;
        permissionTools.chickWrite().initPermission();
        if (permissionTools.isEnabledwrite()) {
            getdownload(permissionTools.isEnabledwrite());
        }
    }

    @Override
    public void onStartDownload(long length) {
        mRxManager.add(Observable.just(length)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long o) throws Exception {
                        progressDialog = new ProgressDialog(context);
                        progressDialog.show();
                    }
                }));

    }

    @Override
    public void onDownLoadProgress(int progress) {
        String progressdes = progress + "%";
        if (progressDialog != null) {
            progressDialog.setProgress(progress);
            progressDialog.setPrecentdes(progressdes);
        }

    }

    @Override
    public void onDownLoadCompleted(final File file) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        AppTools.installApp(context, file);
    }

    @Override
    public void onDownLoadError(String msg) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onErrorSuccess(int code, String message, boolean isSuccess, boolean showTips) {
        if (showTips && !isSuccess) {
            ToastTool.error(message);
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
                if (camera.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && i < grantResults.length) {
                    getdownload(grantResults[i] == PackageManager.PERMISSION_GRANTED);
                }
            }
        }
    }
}

