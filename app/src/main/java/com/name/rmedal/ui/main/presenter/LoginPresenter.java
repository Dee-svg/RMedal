package com.name.rmedal.ui.main.presenter;


import com.name.rmedal.R;
import com.name.rmedal.api.HttpRespose;
import com.name.rmedal.api.RxSubscriber;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.tools.dao.UserDaoUtil;
import com.name.rmedal.ui.main.contract.LoginContract;
import com.veni.tools.baserx.RxSchedulers;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 作者：Administrator on 2017/12/04 10:36
 * 当前类注释:
 * Presenter
 */
public class LoginPresenter extends LoginContract.Presenter {

    @Override
    public void getLogin(final String phone, final String password) {
        //正式调试
       /* //请求参数
        HashMap<String, String> param = new HashMap<>();
        param.put("phone", phone);
        param.put("password", password);
        HttpManager.getInstance().getOkHttpUrlService().getLogin(param)
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
                        String resposeMes = "用户不存在！";
                        UserBean userBean = null;
                        if (userBeanList != null && userBeanList.size() > 0) {
                            for (UserBean bean : userBeanList) {
                                if (bean.getPhone().equals(phone)) {
                                    if(bean.getPassword()==null){
                                        daoUtil.deleteUserBean(bean.getId());
                                        break;
                                    }
                                    if (bean.getPassword().equals(password)) {
                                        userBean = bean;
                                        resposeCode = 200;
                                        resposeMes = "成功";
                                    } else {
                                        resposeMes = "密码错误！";
                                    }
                                    break;
                                }
                            }
                        }
                        if (userBean != null) {
                            httpRespose.setResult(userBean);
                        }
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
}
