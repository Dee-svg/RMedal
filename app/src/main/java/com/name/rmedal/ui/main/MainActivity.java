package com.name.rmedal.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.modelbean.CheckVersionBean;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.ui.AppConstant;
import com.name.rmedal.ui.home.HomeFragment;
import com.name.rmedal.ui.main.contract.MainContract;
import com.name.rmedal.ui.main.presenter.MainPresenter;
import com.name.rmedal.ui.personal.FingerprintLockActivity;
import com.name.rmedal.ui.personal.PatternlockActivity;
import com.name.rmedal.ui.personal.PersonalFragment;
import com.name.rmedal.ui.trade.TradeFragment;
import com.name.rmedal.widget.ProgressDialog;
import com.veni.tools.util.ACache;
import com.veni.tools.util.ActivityTManager;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.JsonUtils;
import com.veni.tools.util.PermissionsUtils;
import com.veni.tools.util.ToastTool;

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
 * 主页
 */
public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {
    @BindView(R.id.main_bottom_navigation)
    AHBottomNavigation mainBottomNavigation;
    @BindView(R.id.main_nav_view)
    FrameLayout mainNavView;
    @BindView(R.id.main_drawer)
    DrawerLayout mainDrawer;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    /*启用MVP一定要设置这句*/
    @Override
    public void initPresenter() {
        mPresenter.setVM(this);
    }

    // 用来判断 两次返回键退出app
    private boolean isExit = false;
    private UserBean userBean;
    private CheckVersionBean versionBean;
    private int mainposition = -1;
    private HomeFragment homeFragment;
    private TradeFragment tradeFragment;
    private PersonalFragment personalFragment;
    private PersonalFragment personalNav;
    private ProgressDialog progressDialog;

    @Override
    public void initView(Bundle savedInstanceState) {
        swipeDragToClose();

        userBean = AppTools.getUserBean(context);
        //初始化底部按钮
        initBottomNavigation();
        //初始化侧滑菜单
        initDrawerLayout();
        //设置底部默认选中按钮
        mainBottomNavigation.setCurrentItem(0, true);
        //调用网络请求
        mPresenter.getUserData(userBean.getUserId());
    }

    /* 初始化底部按钮*/
    private void initBottomNavigation() {
        //Create items
        AHBottomNavigationItem homepage = new AHBottomNavigationItem(R.string.homepage, R.mipmap.ic_main_homepage, android.R.color.white);
        AHBottomNavigationItem tradepage = new AHBottomNavigationItem(R.string.trade, R.mipmap.ic_main_trade, android.R.color.white);
        AHBottomNavigationItem vippage = new AHBottomNavigationItem(R.string.personal, R.mipmap.ic_main_personal, android.R.color.white);
        // Add items
        mainBottomNavigation.addItem(homepage);
        mainBottomNavigation.addItem(tradepage);
        mainBottomNavigation.addItem(vippage);
        //始终显示文字
        mainBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        // Set background color
        mainBottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(context, R.color.home_bottom_nav_color));
        // 禁用CoordinatorLayout内部的转换
        mainBottomNavigation.setBehaviorTranslationEnabled(false);
        // 启用FloatingActionButton的转换
//        mainBottomNavigation.manageFloatingActionButtonBehavior(floatingActionButton);

        // Change colors
        mainBottomNavigation.setAccentColor(ContextCompat.getColor(context, R.color.green));
        mainBottomNavigation.setInactiveColor(ContextCompat.getColor(context, R.color.blue));
        //强制着色绘图(例如，对于带有图标的字体有用)
        mainBottomNavigation.setForceTint(true);
        // 在导航栏下显示颜色(API 21+)
        // Don't forget these lines in your style-v21
        // <item name="android:windowTranslucentNavigation">true</item>
        // <item name="android:fitsSystemWindows">true</item>
//        mainBottomNavigation.setTranslucentNavigationEnabled(true);

        mainBottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                AHBottomNavigationItem selecetitem = mainBottomNavigation.getItem(position);
                String title = selecetitem.getTitle(context);
                if (title.equals(getResString(R.string.personal))) {
                    if (!chickLockKey()) {
                        mainBottomNavigation.setCurrentItem(mainposition);
                        return false;
                    }
                }
                if (mainposition != position) {
                    mainposition = position;
                    SwitchTo(selecetitem);
                }
                return true;
            }
        });
    }

    /**
     * 侧滑菜单
     * inflateHeaderView 进来的布局要宽一些
     */
    private void initDrawerLayout() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (personalNav != null) {
            transaction.hide(personalNav);
        }
        //我
        if (personalNav == null) {
            personalNav = new PersonalFragment();
            Bundle bundle = new Bundle();
            bundle.putString(AppConstant.INTENT_DATATYPE, "Nav");
            personalNav.setArguments(bundle);
            personalNav.setMainDrawer(mainDrawer);
            transaction.add(R.id.main_nav_view, personalNav);
        } else {
            transaction.show(personalNav);
        }
        transaction.commitAllowingStateLoss();
    }

    private boolean chickLockKey() {
        if(chickFingerprintLockKey()){
            startActivity(FingerprintLockActivity.class, FingerprintLockActivity.startJump(AppConstant.YZFingerprint));
            return false;
        }else if(chickPatternLockKey()){
            startActivity(PatternlockActivity.class, PatternlockActivity.startJump(AppConstant.YZPatternlock));
            return false;
        }
        return true;
    }

    private boolean chickFingerprintLockKey() {
        String hasfingerprint = ACache.get(context).getAsString(AppConstant.FingerprintKey + userBean.getId());
        String value = ACache.get(context).getAsString(AppConstant.FingerprintOK + userBean.getId());
        return (hasfingerprint != null) && (value == null);
    }

    private boolean chickPatternLockKey() {
        String haspatternlock = ACache.get(context).getAsString(AppConstant.PatternlockKey + userBean.getId());
        String value = ACache.get(context).getAsString(AppConstant.PatternlockOK + userBean.getId());
        return (haspatternlock != null) && (value == null);
    }

    /* 底部按钮点击事件*/
    private void SwitchTo(AHBottomNavigationItem selecetitem) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.setTransition(FragmentTransaction.TRANSIT_EXIT_MASK);//Fragment切换动画效果1
        transaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);//Fragment切换动画效果2
        hideFragments(transaction);
        String title = selecetitem.getTitle(context);

        if (getResString(R.string.homepage).equals(title)) {
            //首页
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
                transaction.add(R.id.main_framelayout, homeFragment);
            } else {
                transaction.show(homeFragment);
            }
        } else if (getResString(R.string.trade).equals(title)) {
            //购物
            if (tradeFragment == null) {
                tradeFragment = new TradeFragment();
                transaction.add(R.id.main_framelayout, tradeFragment);
            } else {
                transaction.show(tradeFragment);
            }
        } else if (getResString(R.string.personal).equals(title)) {
            //我
            if (personalFragment == null) {
                personalFragment = new PersonalFragment();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstant.INTENT_DATATYPE, "111");
                personalFragment.setArguments(bundle);
                transaction.add(R.id.main_framelayout, personalFragment);
            } else {
                transaction.show(personalFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    public void onOffNavView() {
        boolean isDrawerOpen = mainDrawer.isDrawerOpen(mainNavView);
        if (isDrawerOpen) {
            mainDrawer.closeDrawer(mainNavView);
        } else {
            mainDrawer.openDrawer(mainNavView);
        }
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (tradeFragment != null) {
            transaction.hide(tradeFragment);
        }
        if (personalFragment != null) {
            transaction.hide(personalFragment);
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

    @Override
    public void return_UserData(UserBean data) {
        userBean = data;
        AppTools.saveUserBean(context, JsonUtils.toJson(data));
    }

    @Override
    public void returnVersionData(CheckVersionBean data) {
        versionBean = data;
        List<String> permissionList = PermissionsUtils.with(context)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .initPermission();
        boolean down = DataUtils.isEmpty(permissionList)
                || !permissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        getdownload(down);
    }

    @Override
    public void onStartDownload(long length) {
        mRxManager.add(Observable.just(length)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
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

    @Override
    public void onBackPressed() {
        boolean isDrawerOpen = mainDrawer.isDrawerOpen(mainNavView);
        if (isDrawerOpen) {
            isExit = false;
            mainDrawer.closeDrawer(mainNavView);
            return;
        }
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
