package com.name.rmedal.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.jaredrummler.android.widget.AnimatedSvgView;
import com.name.rmedal.R;
import com.name.rmedal.api.AppConstant;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.modelbean.ModelSVG;
import com.name.rmedal.modelbean.PersonalModelBean;
import com.name.rmedal.ui.main.contract.SVGContract;
import com.name.rmedal.ui.main.presenter.SVGPresenter;
import com.veni.tools.ACache;
import com.veni.tools.LogTools;
import com.veni.tools.SPTools;
import com.veni.tools.StatusBarTools;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.observers.LambdaObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 * 欢迎页
 */
public class SVGActivity extends BaseActivity<SVGPresenter> implements SVGContract.View {

    @BindView(R.id.animated_svg_view)
    AnimatedSvgView mSvgView;
    @BindView(R.id.app_name)
    ImageView mAppName;
    private Handler checkhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAppName.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_svg;
    }

    /*启用MVP一定要设置这句*/
    @Override
    public void initPresenter() {
        mPresenter.setVM(this);
    }

    private boolean svgisok = false;
    private boolean chikcisok = false;

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        //选择启动的SVG动画
        setSvg(ModelSVG.values()[0]);
        //启用的网络接口
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
                LogTools.e(TAG, "state--" + state);
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
                        Message msg = checkhandler.obtainMessage();
                        checkhandler.sendMessage(msg);
                        mPresenter.checkVersion("1");
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
        // 第一次打开app
        String isfirst = ACache.get(context).getAsString(AppConstant.FIRST_TIME);
        boolean isfirstsp = (Boolean) SPTools.get(context, AppConstant.FIRST_TIME, true);
//                        if ((Boolean) SPTools.get(context, SPTools.FIRST_TIME, true)) {
        if (isfirst == null || isfirstsp) {
            FirstStartActivity.startAction(context);
        } else {
            MainActivity.startAction(context);
        }
    }

    @Override
    public void returnVersionData(List<PersonalModelBean> data) {
        chikcisok = true;
        isfirstin();
    }

    @Override
    public void onError(int code, String errtipmsg) {
        chikcisok = true;
        isfirstin();
    }
}
