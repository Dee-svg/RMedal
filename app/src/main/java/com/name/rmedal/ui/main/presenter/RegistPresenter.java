package com.name.rmedal.ui.main.presenter;


import com.name.rmedal.R;
import com.name.rmedal.api.HttpRespose;
import com.name.rmedal.api.RxSubscriber;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.tools.dao.UserDaoUtil;
import com.name.rmedal.ui.main.contract.RegistContract;
import com.veni.tools.baserx.RxSchedulers;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 作者：Administrator on 2017/12/04 10:36
 * 当前类注释:
 * Presenter
 */
public class RegistPresenter extends RegistContract.Presenter {

    @Override
    public void registUser(final String phone, final String password) {
        //正式调试
       /* //请求参数
        HashMap<String, String> param = new HashMap<>();
        param.put("phone", phone);
        param.put("password", password);
        HttpManager.getInstance().getOkHttpUrlService().registUser(param)
                .compose(RxSchedulers.<HttpRespose<CheckVersionBean>>io_main()).subscribe(new RxSubscriber<CheckVersionBean>() {
            @Override
            public void _onNext(CheckVersionBean data) {
                mView.returnVersionData(data);
            }

            @Override
            public void onErrorSuccess(int code, String message, boolean issuccess) {
                mView.onErrorSuccess(code, message, issuccess, false);
            }
        });*/
        //测试数据
        AppTools.createObservable(UserBean.class)
                .compose(RxSchedulers.<HttpRespose<UserBean>>io_main())
                .doOnNext(new Consumer<HttpRespose<UserBean>>() {
                    @Override
                    public void accept(HttpRespose<UserBean> httpRespose) throws Exception {

                        UserDaoUtil daoUtil = new UserDaoUtil(mContext);
                        List<UserBean> userBeanList = daoUtil.queryUserBeanByPhone(phone);
                        int resposeCode = 201;
                        String resposeMes = "用户已存在！";
                        UserBean userBean = null;
                        if (userBeanList != null && userBeanList.size() > 0) {
                            for (UserBean bean : userBeanList) {
                                if (bean.getPhone().equals(phone)) {
                                    break;
                                }
                            }
                        } else {
                            userBean = new UserBean();
                            userBean.setPassword(password);
                            userBean.setUserId(phone);
                            userBean.setPhone(phone);
                            daoUtil.insertUserBean(userBean);
                            resposeCode = 200;
                            resposeMes = "注册成功";
                        }
                        httpRespose.setResult(userBean);
                        httpRespose.setCode(resposeCode);
                        httpRespose.setMessage(resposeMes);
                    }
                })
                .subscribe(new RxSubscriber<UserBean>(this, mContext.getString(R.string.loading)) {
                    @Override
                    public void _onNext(UserBean data) {
                        mView.return_UserData(data);
                    }

                    @Override
                    public void onErrorSuccess(int code, String message, boolean issuccess) {
                        mView.onErrorSuccess(code, message, issuccess, true);
                    }

                });
    }

    @Override
    public void getCaptcha(String phone) {
        //正式调试
//        HttpManager.getInstance().getOkHttpUrlService().getUserData(phone,password)
        //测试数据
        AppTools.createObservable(UserBean.class)
                .compose(RxSchedulers.<HttpRespose<UserBean>>io_main())
                .doOnNext(new Consumer<HttpRespose<UserBean>>() {
                    @Override
                    public void accept(HttpRespose<UserBean> httpRespose) throws Exception {
                        String resposeMes = "验证码已发送";
                        UserBean userBean = null;
                        httpRespose.setResult(userBean);
                        httpRespose.setMessage(resposeMes);
                    }
                })
                .subscribe(new RxSubscriber<UserBean>(this, mContext.getString(R.string.loading)) {
                    @Override
                    public void _onNext(UserBean data) {
                        mView.return_Captcha(data);
                    }

                    @Override
                    public void onErrorSuccess(int code, String message, boolean issuccess) {
                        mView.onErrorSuccess(code, message, issuccess, true);
                    }

                });
    }
}
