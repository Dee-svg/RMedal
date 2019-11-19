package com.name.rmedal.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;

import com.jaredrummler.android.widget.AnimatedSvgView;
import com.name.rmedal.BuildConfig;
import com.name.rmedal.R;
import com.name.rmedal.api.HttpManager;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.modelbean.CheckVersionBean;
import com.name.rmedal.modelbean.ModelSVG;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.ui.AppConstant;
import com.name.rmedal.ui.main.contract.WelcomeContract;
import com.name.rmedal.ui.main.presenter.WelcomePresenter;
import com.name.rmedal.widget.ProgressDialog;
import com.veni.tools.listener.OnNoFastClickListener;
import com.veni.tools.util.ACache;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.SPUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 * 欢迎页
 */
public class WelcomeActivity extends BaseActivity<WelcomePresenter> implements WelcomeContract.View {

    @BindView(R.id.welcome_svg_view)
    AnimatedSvgView mSvgView;
    @BindView(R.id.welcome_app_name)
    ImageView mAppName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    /*启用MVP一定要设置这句*/
    @Override
    public void initPresenter() {
        mPresenter.setVM(this);
    }

    private ProgressDialog progressDialog;
    private CheckVersionBean versionBean;
    private boolean svgisok = false;
    private boolean chikcisok = false;

    @Override
    public void initView(Bundle savedInstanceState) {
        swipeDragToClose();
        //设置公共参数Token
        HttpManager.getInstance().setToken(AppTools.getToken());
        //选择启动的SVG动画
        setSvg(ModelSVG.values()[0]);
        //检测版本更新
        CheckUpdate();
    }

    private void setSvg(final ModelSVG modelSvg) {
        mSvgView.setGlyphStrings(modelSvg.glyphs);
        mSvgView.setFillColors(modelSvg.colors);
        mSvgView.setViewportSize(modelSvg.width, modelSvg.height);
        mSvgView.setTraceResidueColor(0x32000000);
        mSvgView.setTraceColors(modelSvg.colors);
        mSvgView.rebuildGlyphData();
        mSvgView.start();
        mSvgView.setOnStateChangeListener(new AnimatedSvgView.OnStateChangeListener() {
            @Override
            public void onStateChange(int state) {
                if (AnimatedSvgView.STATE_FINISHED == state) {
                    svgisok = true;
                    isfirstin();
                }
            }
        });
    }

    /**
     * 检查是否有新版本，如果有就升级
     */
    private void CheckUpdate() {
        mRxManager.add(Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mAppName.setVisibility(View.VISIBLE);
                        mPresenter.checkVersion(BuildConfig.VERSION_NAME);
                    }
                }));

    }

    /**
     * 第一次打开app
     */
    private void isfirstin() {
        if (!svgisok || !chikcisok) {
            return;
        }
        UserBean userBean = AppTools.getUserBean(context);
        String userid = userBean.getUserId();
        //也可以判断其他  如token
//            HttpManager.getInstance().setToken(token);
        if (DataUtils.isNullString(userid)) {
            // 第一次打开app
            String isfirst = ACache.get(context).getAsString(AppConstant.FIRST_TIME);//这是一个测试的
            boolean isfirstsp = (Boolean) SPUtils.get(context, AppConstant.FIRST_TIME, true);
            if (DataUtils.isNullString(isfirst) || isfirstsp) {
                startActivityFinish(FirstStartActivity.class);
            } else {
                startActivityFinish(LoginActivity.class);
            }
        } else {
            startActivityFinish(MainActivity.class);
        }


    }

    @Override
    public void returnVersionData(CheckVersionBean data) {
        if (!DataUtils.isEmpty(data.getIsNeedUpdate()) && data.getIsNeedUpdate().equals("1")) {
            versionBean = data;
            if (permissionTools.isEnabledwrite()) {
                getdownload();
            }else {
                permissionTools.chickWrite().initPermission();
            }
        } else {
            chikcisok = true;
            isfirstin();
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
        finish();
    }

    @Override
    public void onDownLoadError(String msg) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        chikcisok = true;
        isfirstin();
    }

    @Override
    public void onErrorSuccess(int code, String message, boolean isSuccess, boolean showTips) {
        if (!isSuccess) {
            chikcisok = true;
            isfirstin();
        }
    }

    private void getdownload() {
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
                .setDialog_Right("取消")
                .setRightlistener(new OnNoFastClickListener() {
                    @Override
                    protected void onNoDoubleClick(View view) {
                        chikcisok = true;
                        isfirstin();
                    }
                }).builder().show();
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
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        getdownload();
                    } else {
                        chikcisok = true;
                        isfirstin();
                    }
                }
            }
        }
    }
}
