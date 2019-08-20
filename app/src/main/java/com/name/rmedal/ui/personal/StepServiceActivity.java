package com.name.rmedal.ui.personal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.widget.stepcount.StepService;
import com.name.rmedal.widget.stepcount.UpdateUiCallBack;
import com.veni.tools.util.SPUtils;
import com.veni.tools.widget.TitleView;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StepServiceActivity extends BaseActivity {

    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.step_tv)
    TextView stepTv;

    @Override
    public int getLayoutId() {
        return R.layout.activity_step;
    }

    @Override
    public void initPresenter() {

    }

    private boolean isstart;
    private StepService mService;
    private String patrol_id = "8080";

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置显示标题
        toolbarTitleView.setTitle("Step");

        startStepService();
        bindStepService();
    }


    @OnClick({R.id.step_start, R.id.step_end})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.step_start:
                if(!isstart){
                    startStepService();
                    bindStepService();
                }
                break;
            case R.id.step_end:
                if(isstart){
                    isstart=false;
                    if(mService!=null){
                        mService.removeValues(patrol_id);
                    }
                    unbindStepService();
                }
                break;
        }
    }

    private UpdateUiCallBack mUiCallback = new UpdateUiCallBack() {
        @Override
        public void updateUi() {
            mRxManager.add(Observable.just("")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String o) throws Exception {
                            int step_number = (int) SPUtils.get(context, StepService.SP_STEPS + patrol_id, 0);
                            String st = step_number + "步";
                            stepTv.setText(st);
                        }
                    }));
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepService.StepBinder binder = (StepService.StepBinder) service;
            mService = binder.getService();
            mService.registerCallback(mUiCallback);
            //
            mService.resetValues(patrol_id);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void bindStepService() {
        try {
            bindService(new Intent(this, StepService.class), this.mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unbindStepService() {
        unbindService(this.mConnection);
    }

    private void startStepService() {
        isstart=true;
        startService(new Intent(this, StepService.class));
    }

}
