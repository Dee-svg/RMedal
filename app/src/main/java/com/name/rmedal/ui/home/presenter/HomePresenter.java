package com.name.rmedal.ui.home.presenter;


import com.name.rmedal.R;
import com.name.rmedal.api.HttpRespose;
import com.name.rmedal.api.RxSubscriber;
import com.name.rmedal.modelbean.NewsBean;
import com.name.rmedal.tools.AppTools;
import com.name.rmedal.ui.AppConstant;
import com.name.rmedal.ui.home.contract.HomeContract;
import com.veni.tools.baserx.RxSchedulers;
import com.veni.tools.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 作者：Administrator on 2017/12/04 10:36
 * 当前类注释:
 * Presenter
 */
public class HomePresenter extends HomeContract.Presenter {

    @Override
    public void getWangYiNews(final int page) {

        //请求参数
        HashMap<String, String> param = new HashMap<>();
        param.put("page", page+"");
        param.put("count", AppConstant.pageSize+"");
        //正式调试
//        HttpManager.getInstance().getOkHttpUrlService().getWangYiNews(param)
        //测试数据
        AppTools.createListObservable(NewsBean.class)
                .compose(RxSchedulers.<HttpRespose<List<NewsBean>>>io_main())
                .doOnNext(new Consumer<HttpRespose<List<NewsBean>>>() {
                    @Override
                    public void accept(HttpRespose<List<NewsBean>> httpRespose) throws Exception {
                        int resposeCode = 201;
                        String resposeMes = "没有更多数据了！";
                        if(page<6){
                            List<NewsBean> newsBeans = new ArrayList<>();
                            NewsBean newsBean= JsonUtils.parseObject(AppConstant.newsjson, NewsBean.class);
                            for(int i=0;i<AppConstant.pageSize;i++){
                                newsBeans.add(newsBean);
                            }
                            httpRespose.setResult(newsBeans);
                            resposeCode = 200;
                            resposeMes = "数据获取成功！";
                        }
                        httpRespose.setCode(resposeCode);
                        httpRespose.setMessage(resposeMes);
                    }
                })
                .subscribe(new RxSubscriber<List<NewsBean>>(mContext, mContext.getString(R.string.loading)) {
                    @Override
                    public void _onNext(List<NewsBean> data) {
                        mView.return_NewsData(data);
                    }

                    @Override
                    public void onErrorSuccess(int code, String message, boolean issuccess) {
                        mView.onErrorSuccess(code, message, issuccess,true);
                    }

                });
    }
}
