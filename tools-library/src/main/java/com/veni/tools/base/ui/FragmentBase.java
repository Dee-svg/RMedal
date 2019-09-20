package com.veni.tools.base.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.veni.tools.util.StatusBarUtils;

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

}
