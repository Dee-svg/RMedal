package com.name.rmedal.ui.main.presenter;


import com.name.rmedal.api.HttpManager;
import com.name.rmedal.api.HttpRespose;
import com.name.rmedal.api.RxSubscriber;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.ui.main.contract.RegistContract;
import com.veni.tools.baserx.RxSchedulers;

import java.util.HashMap;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 作者：Administrator on 2017/12/04 10:36
 * 当前类注释:
 * Presenter
 */
public class RegistPresenter extends RegistContract.Presenter {

    @Override
    public void registUser(final String phone, final String password) {
        //请求参数
        HashMap<String, String> param = new HashMap<>();
        param.put("phone", phone);
        param.put("password", password);
        HttpManager.getInstance().getOkHttpUrlService().registUser(param)
                //TODO 正式环境 doFinally 需要注释掉
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        int resposeCode = 200;
                        String resposeMes = "注册成功！";
                        UserBean userBean=new UserBean();
                        userBean.setPhone(phone);
                        mView.return_UserData(userBean);
                        mView.onErrorSuccess(resposeCode, resposeMes, true, false);
                    }
                })
                .compose(RxSchedulers.<HttpRespose<UserBean>>io_main())
                .subscribe(new RxSubscriber<UserBean>(this) {
                    @Override
                    public void _onNext(UserBean data) {
                        mView.return_UserData(data);
                    }

                    @Override
                    public void onErrorSuccess(int code, String message, boolean issuccess) {
                        mView.onErrorSuccess(code, message, issuccess, false);
                    }
                });
    }

    @Override
    public void getCaptcha(String phone) {
        //请求参数
        HashMap<String, String> param = new HashMap<>();
        param.put("phone", phone);
        HttpManager.getInstance().getOkHttpUrlService().getCaptcha(param)
                //TODO 正式环境 doOnNext 需要注释掉
                .doOnNext(new Consumer<HttpRespose<UserBean>>() {
                    @Override
                    public void accept(HttpRespose<UserBean> httpRespose) throws Exception {
                        String resposeMes = "验证码已发送";
                        int resposeCode = 200;
                        UserBean userBean = null;
                        httpRespose.setResult(userBean);
                        httpRespose.setMessage(resposeMes);
                        mView.return_Captcha(userBean);
                        mView.onErrorSuccess(resposeCode, resposeMes, true, false);
                    }
                })
                .compose(RxSchedulers.<HttpRespose<UserBean>>io_main())
                .subscribe(new RxSubscriber<UserBean>(this) {
                    @Override
                    public void _onNext(UserBean data) {
                        mView.return_Captcha(data);
                    }

                    @Override
                    public void onErrorSuccess(int code, String message, boolean issuccess) {
                        mView.onErrorSuccess(code, message, issuccess, false);
                    }
                });
    }
}
