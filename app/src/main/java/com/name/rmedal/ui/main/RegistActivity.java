package com.name.rmedal.ui.main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.ui.AppConstant;
import com.name.rmedal.ui.main.contract.RegistContract;
import com.name.rmedal.ui.main.presenter.RegistPresenter;
import com.veni.tools.listener.OnNoFastClickListener;
import com.veni.tools.util.ACache;
import com.veni.tools.util.CaptchaTime;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.JsonUtils;
import com.veni.tools.util.KeyboardUtils;
import com.veni.tools.util.SoftHideKeyBoardUtils;
import com.veni.tools.util.ToastTool;
import com.veni.tools.widget.PasswordEditText;
import com.veni.tools.widget.TitleView;
import com.veni.tools.widget.VerificationCodeView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 * 注册
 */
public class RegistActivity extends BaseActivity<RegistPresenter> implements RegistContract.View {

    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.regist_logo_iv)
    ImageView registLogoIv;
    @BindView(R.id.regist_mobile_et)
    EditText registMobileEt;
    @BindView(R.id.regist_password_et)
    EditText registPasswordEt;
    @BindView(R.id.regist_captcha_et)
    EditText registCaptchaEt;
    @BindView(R.id.regist_get_vercode)
    VerificationCodeView registGetVercode;
    @BindView(R.id.regist_get_captcha)
    TextView registGetCaptcha;
    @BindView(R.id.regist_content_ll)
    LinearLayout registContentLl;
    @BindView(R.id.regist_scrollView)
    ScrollView registScrollView;
    @BindView(R.id.regist_passwordagain_et)
    PasswordEditText registPasswordagainEt;

    @Override
    public int getLayoutId() {
        return R.layout.activity_regist;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this);
    }

    private String mobile = "";
    private String registp = "";
    private String registpag = "";
    private String captcha = "";
    private CaptchaTime timeCount;

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置显示标题
        toolbarTitleView.setTitle(R.string.show_regist);

        SoftHideKeyBoardUtils.assistActivity(context);
        //设置监听
        initListener();
        //获取上次获取验证码的倒计时
        long codetime = ACache.get(context).getKeyTimes(AppConstant.LG_Code);
        //倒计时大于两秒显示相关信息
        if (codetime / 1000 > 2) {
            timeCount = new CaptchaTime(registGetCaptcha, codetime / 1000);
            registMobileEt.setText(ACache.get(context).getAsString(AppConstant.LG_Code));
            registGetVercode.refreshCode();
        }
    }

    @OnClick({R.id.regist_content_ll, R.id.regist_btn, R.id.regist_get_captcha, R.id.regist_login})
    public void onViewClicked(View view) {
        KeyboardUtils.hideSoftInput(context);
        if (antiShake.check(view.getId())) return;
        switch (view.getId()) {
            case R.id.regist_content_ll:
                break;
            case R.id.regist_get_captcha://获取验证码
                if (DataUtils.isNullString(mobile)) {
                    ToastTool.error("请输入手机号");
                    return;
                }
                if (!DataUtils.isMobile(mobile)) {
                    ToastTool.error("请输入正确手机号");
                    return;
                }
                mPresenter.getCaptcha(mobile);
                break;
            case R.id.regist_btn://注册
                if (DataUtils.isNullString(mobile)) {
                    ToastTool.error(R.string.input_phone_tips);
                    return;
                }
                if (!DataUtils.isMobile(mobile)) {
                    ToastTool.error("请输入正确手机号");
                    return;
                }
                if (DataUtils.isNullString(captcha)) {
                    ToastTool.error(R.string.input_captcha_tips);
                    return;
                }
                String code = registGetVercode.getvCode();
                if (captcha.equals(code)) {
                    ToastTool.error("请输入正确验证码");
                    return;
                }
                if (DataUtils.isNullString(registp)) {
                    ToastTool.error(R.string.input_pw_tips);
                    return;
                }
                if (DataUtils.isNullString(registpag)) {
                    ToastTool.error(R.string.input_pwagain_tips);
                    return;
                }
                mPresenter.registUser(mobile, registp);
                break;
            case R.id.regist_login://登录
                finish();
                break;
        }
    }

    @Override
    public void return_UserData(final UserBean data) {
        creatDialogBuilder().setDialog_title("温馨提示")
                .setDialog_message("注册成功，是否立即登录?")
                .setDialog_Left("登录")
                .setLeftlistener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppTools.saveUserBean(context, JsonUtils.toJson(data));
                        startActivityFinish(MainActivity.class);
                    }
                })
                .setDialog_Right("取消")
                .setRightlistener(new OnNoFastClickListener() {
                    @Override
                    protected void onNoDoubleClick(View view) {
                        finish();
                    }
                })
                .builder().show();
    }

    @Override
    public void return_Captcha(UserBean data) {
        timeCount = new CaptchaTime(registGetCaptcha, 60);
        ACache.get(context).put(AppConstant.LG_Code, mobile, ACache.TIME_MINUTE);
        registGetVercode.setvCode(registGetVercode.getCharAndNumr());
    }

    @Override
    public void onErrorSuccess(int code, String message, boolean isSuccess, boolean showTips) {
        if (showTips) {
            if (isSuccess) {
                ToastTool.normal(message);
            } else {
                ToastTool.error(message);
            }
        }
    }

    /**
     * 设置监听
     */
    private void initListener() {
        registMobileEt.addTextChangedListener(new TextWatcher() {
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
        registPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    registp = "";
                    return;
                }
                if (!s.toString().matches("[A-Za-z0-9]+")) {
                    String temp = s.toString();
                    ToastTool.error("请输入数字或字母");
                    s.delete(temp.length() - 1, temp.length());
                    registPasswordEt.setSelection(s.length());
                    return;
                }
                registp = s.toString();
            }
        });
        registPasswordagainEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty())
                    return;
                if (!s.toString().matches("[A-Za-z0-9]+")) {
                    String temp = s.toString();
                    ToastTool.error("请输入数字或字母");
                    s.delete(temp.length() - 1, temp.length());
                    registPasswordagainEt.setSelection(s.length());
                    return;
                }
                registpag = s.toString();
            }
        });
        registCaptchaEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                captcha = s.toString();
            }
        });

    }
}
