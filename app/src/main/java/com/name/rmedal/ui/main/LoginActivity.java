package com.name.rmedal.ui.main;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.name.rmedal.R;
import com.name.rmedal.api.HttpManager;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.tools.AnimationTools;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.ui.main.contract.LoginContract;
import com.name.rmedal.ui.main.presenter.LoginPresenter;
import com.veni.tools.util.ActivityTManager;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.JsonUtils;
import com.veni.tools.util.KeyboardUtils;
import com.veni.tools.util.StatusBarUtils;
import com.veni.tools.util.ToastTool;
import com.veni.tools.widget.KeyboardLayout;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View  {

    @BindView(R.id.login_logo_iv)
    ImageView loginLogoIv;
    @BindView(R.id.login_mobile_et)
    EditText loginMobileEt;
    @BindView(R.id.login_password_et)
    EditText loginPasswordEt;
    @BindView(R.id.login_content_ll)
    LinearLayout loginContentLl;
    @BindView(R.id.login_scrollView)
    ScrollView loginScrollView;
    @BindView(R.id.login_service)
    LinearLayout loginService;
    @BindView(R.id.login_root_iv)
    KeyboardLayout loginRootRv;


    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this);
    }

    private String mobile = "";
    private String loginp = "";
    private int lastdist;
    private boolean viewIsZoom = true;

    @Override
    public void initView(Bundle savedInstanceState) {
        swipeDragToClose();
        immersive(loginRootRv,true);

        //清空缓存token 重新加载HttpManager
        AppTools.saveToken(context, "");
        HttpManager.getInstance().setToken("");
        //设置监听
        initListener();

    }

    @OnClick({R.id.login_content_ll, R.id.login_btn,
            R.id.login_regist, R.id.login_about_us, R.id.login_contact_service})
    public void onViewClicked(View view) {
        KeyboardUtils.hideSoftInput(context);
        if (antiShake.check(view.getId())) return;
        switch (view.getId()) {
            case R.id.login_content_ll:
                break;
            case R.id.login_regist://注册
                startActivity(RegistActivity.class);
                break;
            case R.id.login_about_us://关于我们
                break;
            case R.id.login_contact_service://联系客服
                break;
            case R.id.login_btn://登录
                if (DataUtils.isNullString(mobile)) {
                    ToastTool.error("请输入手机号");
                    return;
                }
                if (!DataUtils.isMobile(mobile)) {
                    ToastTool.error("请输入正确手机号");
                    return;
                }
                if (DataUtils.isNullString(loginp)) {
                    ToastTool.error("请输入密码");
                    return;
                }
                mPresenter.getLogin(mobile,loginp);
                break;
        }
    }

    @Override
    public void return_UserData(UserBean data) {
        AppTools.saveUserBean(context, JsonUtils.toJson(data));
        startActivityFinish(MainActivity.class);
    }

    @Override
    public void onErrorSuccess(int code, String message, boolean isSuccess, boolean showTips) {
        if (showTips) {
            if(isSuccess){
                ToastTool.normal(message);
            }else {
                ToastTool.error(message);
            }
        }
    }

    /**
     * 设置监听
     */
    private void initListener() {
        loginMobileEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mobile = s.toString();
            }
        });
        loginPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()){
                    loginp = "";
                    return;
                }
                if (!s.toString().matches("[A-Za-z0-9]+")) {
                    String temp = s.toString();
                    ToastTool.error("请输入数字或字母");
                    s.delete(temp.length() - 1, temp.length());
                    loginPasswordEt.setSelection(s.length());
                    return;
                }
                loginp = s.toString();
            }
        });

        //键盘高度变化监听
        loginRootRv.setKeyboardListener(new KeyboardLayout.KeyboardLayoutListener() {
            @Override
            public void onKeyboardStateChanged(boolean isActive, int keyboardHeight, int bottom) {
                int contentLlBottom = loginContentLl.getBottom();
                int dist = contentLlBottom - bottom;
                if (isActive) {
                    scrollToBottom();
                }
                if (keyboardHeight > 0) {
                    if ((dist > 0 &&viewIsZoom) ||lastdist != dist) {
                        viewIsZoom = false;
                        lastdist = dist;
//                        ZoomIn(dist);
                    }
                    if (loginService.getVisibility() != View.INVISIBLE) {
                        loginService.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if(!viewIsZoom){
                        viewIsZoom = true;
//                        ZoomOut();
                    }
                    if (loginService.getVisibility() != View.VISIBLE) {
                        loginService.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    /**
     * 弹出软键盘时将SVContainer滑到底
     * 滑动高度根据实际情况修改
     */
    private void scrollToBottom() {
        loginScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginScrollView.smoothScrollTo(0, loginScrollView.getBottom() + StatusBarUtils.getStatusBarHeight(context));
            }
        }, 100);
    }

    private void ZoomIn(int dist) {
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(loginContentLl, "translationY", 0.0f, -dist);
        mAnimatorTranslateY.setDuration(300);
        mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
        mAnimatorTranslateY.start();
        AnimationTools.zoomIn(loginLogoIv, 0.6f, dist);
    }

    private void ZoomOut() {
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(loginContentLl, "translationY", loginContentLl.getTranslationY(), 0);
        mAnimatorTranslateY.setDuration(300);
        mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
        mAnimatorTranslateY.start();
        //键盘收回后，logo恢复原来大小，位置同样回到初始位置
        AnimationTools.zoomOut(loginLogoIv, 0.6f);
    }

    // 用来判断 两次返回键退出app
    private boolean isExit = false;
    @Override
    public void onBackPressed() {
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
    }
}
