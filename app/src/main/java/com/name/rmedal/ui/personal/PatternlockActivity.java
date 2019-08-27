package com.name.rmedal.ui.personal;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.ui.AppConstant;
import com.veni.tools.LogUtils;
import com.veni.tools.base.ui.JumpOptions;
import com.veni.tools.util.ACache;
import com.veni.tools.util.ToastTool;

import java.util.List;

import butterknife.BindView;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 * 手势密码
 */
public class PatternlockActivity extends BaseActivity {

    public static JumpOptions startJump(String data_type) {
        return   new JumpOptions()
                .setBundle(AppConstant.INTENT_DATATYPE, data_type);
    }

    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.profile_name)
    TextView profileName;
    @BindView(R.id.patter_lock_view)
    PatternLockView patterLockView;
    @Override
    public int getLayoutId() {
        return R.layout.activity_patternlock;
    }

    @Override
    public void initPresenter() {

    }

    private String data_type;
    private String pwd_input;
    @Override
    public void initView(Bundle savedInstanceState) {
        immersive(null,false);

        Glide.with(context).load(userBean.getCustomerImg()).apply(new RequestOptions()
                .error(R.mipmap.ic_touxiang).circleCrop()).into(profileImage);
        data_type = getIntent().getStringExtra(AppConstant.INTENT_DATATYPE);
        //初始化手势密码参数
        initlockview();

        switch (data_type) {
            case AppConstant.CJPatternlock1://创建手势密码
                profileName.setText("创建手势密码");
                break;
            case AppConstant.YZPatternlock://验证手势密码
                profileName.setText("验证手势密码");
                break;
        }
    }

    private void upUIData(String inputpwd) {
        switch (data_type) {
            case AppConstant.YZPatternlock: {//验证手势密码
                String pwd = ACache.get(context).getAsString(AppConstant.PatternlockKey+ userBean.getId());
                if (pwd.equals(inputpwd)) {
                    ACache.get(context).put(AppConstant.PatternlockOK+ userBean.getId(), "1", ACache.TIME_DAY);
                    finish();
                } else {
                    LogUtils.e(TAG, "pwd" + pwd);
                    patterLockView.clearPattern();
                }
                break;
            }
            case AppConstant.CJPatternlock1: {//创建手势密码
                profileName.setText("再次滑动手势密码");
                data_type = AppConstant.CJPatternlock2;
                pwd_input = inputpwd;
                patterLockView.clearPattern();
                break;
            }
            case AppConstant.CJPatternlock2: {//创建手势密码
                patterLockView.clearPattern();
                if (pwd_input.equals(inputpwd)) {
                    ACache.get(context).put(AppConstant.PatternlockKey+ userBean.getId(), inputpwd);
                    ACache.get(context).remove(AppConstant.PatternlockOK+ userBean.getId());
                    ToastTool.normal("手势密码创建成功！");
                    finish();
                } else {
                    ToastTool.error("两次密码不一致！");
                }
                break;
            }
        }
    }
    /**
     * 初始化手势密码参数
     */
    private void initlockview(){
        //更改行（或列）中的点的数目
        patterLockView.setDotCount(3);
        //在正常状态下改变点的大小
        patterLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
        //更改选定状态点的大小
        patterLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
        //更改路径的宽度
        patterLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
        //视图样式？
        patterLockView.setAspectRatioEnabled(true);
        // ASPECT_RATIO_SQUARE 方格
        // ASPECT_RATIO_WIDTH_BIAS 偏宽
        // ASPECT_RATIO_HEIGHT_BIAS 偏高
        patterLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        //视图显示位置
        patterLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        //更改动画点的持续时间
        patterLockView.setDotAnimationDuration(150);
        //更改路径结束动画的持续时间
        patterLockView.setPathEndAnimationDuration(100);
        //在正确的状态下设置模式视图的颜色
        patterLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.white));
        //在默认状态 视图的颜色
        patterLockView.setNormalStateColor(ResourceUtils.getColor(this, R.color.white));
        //在错误状态视图的颜色
        patterLockView.setWrongStateColor(ResourceUtils.getColor(this, R.color.red));
        //设置隐身模式（隐藏模式图）
        patterLockView.setInStealthMode(false);
        //在绘制图形时启用振动反馈
        patterLockView.setTactileFeedbackEnabled(true);
        //完全禁用模式锁定视图中的任何输入
        patterLockView.setInputEnabled(true);
        /*以下监听选择一个就行*/
        //设置手势密码滑动监听
        patterLockView.addPatternLockListener(mPatternLockViewListener);
    }

    /**
     * 手势密码滑动监听
     */
    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            LogUtils.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            LogUtils.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(patterLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            LogUtils.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(patterLockView, pattern));
            String inputpwd= PatternLockUtils.patternToString(patterLockView, pattern);
            upUIData(inputpwd);
        }

        @Override
        public void onCleared() {
            LogUtils.d(getClass().getName(), "Pattern has been cleared");
        }
    };

}
