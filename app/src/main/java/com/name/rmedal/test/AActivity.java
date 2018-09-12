package com.name.rmedal.test;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.name.rmedal.R;
import com.name.rmedal.api.AppConstant;
import com.name.rmedal.base.BaseActivity;
import com.veni.tools.StatusBarTools;
import com.veni.tools.base.ActivityJumpOptionsTool;
import com.veni.tools.view.TitleView;

import butterknife.BindView;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 */
public class AActivity extends BaseActivity {

    @BindView(R.id.toast_title_view)
    TitleView toastTitleView;

    /**
     * 启动入口
     */
    public static void startAction(Context context, View view) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(AActivity.class)
                .setView(view)
                .setActionString(AppConstant.TRANSITION_ANIMATION)
                .screenTransitAnim()
                .start();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_toast;
    }

    @Override
    public void initPresenter() {

    }


    @Override
    public void initView(Bundle savedInstanceState) {
        //设置启动动画对应的view
        View view = toastTitleView.getTvTitle();
        ViewCompat.setTransitionName(view, AppConstant.TRANSITION_ANIMATION);
        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        //增加状态栏的高度
        StatusBarTools.setPaddingSmart(this, toastTitleView);
        //设置返回点击事件
        toastTitleView.setLeftFinish(context);
        //设置显示标题
        toastTitleView.setTitle("popupwindows");
        //设置侧滑退出
        setSwipeBackLayout(0);

    }

}
