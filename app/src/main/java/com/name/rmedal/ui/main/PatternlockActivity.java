package com.name.rmedal.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.andrognito.rxpatternlockview.RxPatternLockView;
import com.andrognito.rxpatternlockview.events.PatternLockCompoundEvent;
import com.name.rmedal.R;
import com.name.rmedal.api.AppConstant;
import com.name.rmedal.base.BaseActivity;
import com.veni.tools.ACache;
import com.veni.tools.LogTools;
import com.veni.tools.StatusBarTools;
import com.veni.tools.base.ActivityJumpOptionsTool;

import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 * 手势密码
 */
public class PatternlockActivity extends BaseActivity {

    /**
     * 启动入口
     */
    public static void startAction(Context context) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(PatternlockActivity.class)
                .customAnim()
                .start();
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

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        ACache.get(context).put(AppConstant.PatternlockKey, "012345678");
        //初始化手势密码参数
        initlockview();
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
        //设置手势密码滑动监听
        mRxManager.add(RxPatternLockView.patternChanges(patterLockView)
                .subscribe(new Consumer<PatternLockCompoundEvent>() {
                    @Override
                    public void accept(PatternLockCompoundEvent event) throws Exception {
                        if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_STARTED) {
                            LogTools.d(getClass().getName(), "Pattern drawing started");
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_PROGRESS) {
                            LogTools.d(getClass().getName(), "Pattern progress: " +
                                    PatternLockUtils.patternToString(patterLockView, event.getPattern()));
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_COMPLETE) {
                            LogTools.d(getClass().getName(), "Pattern complete: " +
                                    PatternLockUtils.patternToString(patterLockView, event.getPattern()));

                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_CLEARED) {
                            LogTools.d(getClass().getName(), "Pattern has been cleared");
                        }
                    }
                }));
    }

    /**
     * 手势密码滑动监听
     */
    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            LogTools.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            LogTools.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(patterLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            LogTools.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(patterLockView, pattern));
            String inputpwd=PatternLockUtils.patternToString(patterLockView, pattern);
            String pwd=  ACache.get(context).getAsString(AppConstant.PatternlockKey);
            LogTools.d(getClass().getName(), "pwd" +pwd);
            if(pwd.equals(inputpwd)){
                ACache.get(context).put(AppConstant.PatternlockOK, "1", ACache.TIME_DAY);
                finish();
            }else {
                patterLockView.clearPattern();
            }
        }

        @Override
        public void onCleared() {
            LogTools.d(getClass().getName(), "Pattern has been cleared");
        }
    };

}
