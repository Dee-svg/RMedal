package com.name.rmedal.ui.main.presenter;


import com.name.rmedal.api.HttpManager;
import com.name.rmedal.api.HttpRespose;
import com.name.rmedal.api.RxSubscriber;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.ui.main.contract.LoginContract;
import com.veni.tools.baserx.RxSchedulers;

import java.util.HashMap;

import io.reactivex.functions.Action;

/**
 * 作者：Administrator on 2017/12/04 10:36
 * 当前类注释:
 * Presenter
 */
public class LoginPresenter extends LoginContract.Presenter {

    @Override
    public void getLogin(final String phone, final String password) {
        //请求参数
        HashMap<String, String> param = new HashMap<>();
        param.put("phone", phone);
        param.put("password", password);
        HttpManager.getInstance().getOkHttpUrlService().getLogin(param)
                .compose(RxSchedulers.<HttpRespose<UserBean>>io_main())
                //TODO 正式环境 doFinally 需要注释掉
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        int resposeCode = 200;
                        String resposeMes = "登录成功！";
                        UserBean userBean=new UserBean();
                        userBean.setPhone(phone);
                        mView.return_UserData(userBean);
                        mView.onErrorSuccess(resposeCode, resposeMes, true, false);
                    }
                })
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
}
