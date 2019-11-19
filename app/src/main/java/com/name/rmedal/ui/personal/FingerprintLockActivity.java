package com.name.rmedal.ui.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.tools.FingerprintUtil;
import com.name.rmedal.ui.AppConstant;
import com.veni.tools.LogUtils;
import com.veni.tools.base.ui.JumpOptions;
import com.veni.tools.util.ACache;
import com.veni.tools.util.ActivityTManager;
import com.veni.tools.util.ToastTool;
import com.veni.tools.widget.TitleView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * 作者：kkan on 2019/04/10
 * 当前类注释:
 * 安全管理-指纹
 */
public class FingerprintLockActivity extends BaseActivity {
    /**
     * 页面跳转参数
     *
     * @param type type
     */
    public static JumpOptions startJump(String type) {
        return new JumpOptions()
                .setBundle(AppConstant.INTENT_DATATYPE, type);
    }

    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.fingerprint_lock_login_bypw)
    TextView fingerprintLockLoginBypw;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fingerprint_lock;
    }

    @Override
    public void initPresenter() {
    }

    private String type = "";

    @Override
    public void initView(Bundle savedInstanceState) {
        toolbarTitleView.setTitle(getResources().getString(R.string.show_fingerprint_lock));
        type = getIntent().getStringExtra(AppConstant.INTENT_DATATYPE);
        if (type.equals(AppConstant.YZFingerprint)) {
            fingerprintLockLoginBypw.setVisibility(View.VISIBLE);
        }
        showFingerprint();
    }

    @OnClick({R.id.fingerprint_lock_img, R.id.fingerprint_lock_tv, R.id.fingerprint_lock_login_bypw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fingerprint_lock_img:
            case R.id.fingerprint_lock_tv:
                showFingerprint();
                break;
            case R.id.fingerprint_lock_login_bypw:
                break;
        }
    }
    private void showFingerprint(){

        FingerprintUtil.callFingerPrint(context, new FingerprintUtil.OnCallBackListenr() {
            @Override
            public void onSupportFailed() {
                ToastTool.normal("您的手机不支持指纹功能");
            }

            @Override
            public void onInsecurity() {
                ToastTool.normal("您还未设置锁屏，请先设置锁屏并添加一个指纹");
            }

            @Override
            public void onEnrollFailed() {
                ToastTool.normal("您至少需要在系统设置中添加一个指纹");

            }

            @Override
            public void onAuthenticationCancel() {

            }

            @Override
            public void onAuthenticationSucceeded() {
                LogUtils.eTag(TAG, "onAuthenticationSucceeded----");

                ToastTool.normal("指纹认证成功");
                if (type.equals(AppConstant.FingerprintKey1)) {
                    ACache.get(context).put(AppConstant.FingerprintKey + userBean.getUserId(), "1");
                    ACache.get(context).remove(AppConstant.FingerprintOK+ userBean.getUserId());
                } else {
                    ACache.get(context).put(AppConstant.FingerprintOK+ userBean.getUserId(), "1", ACache.TIME_DAY);
                }
                context.finish();
            }
        });
    }


    // 用来判断 两次返回键退出app
    private boolean isExit = false;

    @Override
    public void onBackPressed() {
        if(fingerprintLockLoginBypw.getVisibility()==View.VISIBLE){
            if (!isExit) {
                isExit = true;
                ToastTool.normal("再按一次退出程序");
                mRxManager.add(Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                isExit = false;
                            }
                        }));

                return;
            }
            ActivityTManager.get().AppExit(context, false);
        }else {
            super.onBackPressed();
        }

    }
}
