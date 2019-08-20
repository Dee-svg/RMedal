package com.name.rmedal.ui.trade.presenter;


import com.name.rmedal.R;
import com.name.rmedal.api.HttpManager;
import com.name.rmedal.api.HttpRespose;
import com.name.rmedal.api.RxSubscriber;
import com.name.rmedal.modelbean.UserBean;
import com.name.rmedal.ui.trade.contract.TradeContract;
import com.veni.tools.baserx.RxSchedulers;

/**
 * 作者：Administrator on 2017/12/04 10:36
 * 当前类注释:
 * Presenter
 */
public class TradePresenter extends TradeContract.Presenter {

    @Override
    public void getUserData(String data,String deviceToken) {
        HttpManager.getInstance().getOkHttpUrlService().getUserData(data,deviceToken)
                .compose(RxSchedulers.<HttpRespose<UserBean>>io_main())
                .subscribe(new RxSubscriber<UserBean>(mContext, mContext.getString(R.string.loading)) {
                    @Override
                    public void _onNext(UserBean data) {
                        mView.return_UserData(data);
                    }

                    @Override
                    public void onErrorSuccess(int code, String message, boolean issuccess) {
                        mView.onErrorSuccess(code, message, issuccess,true);
                    }

                });
    }
}
