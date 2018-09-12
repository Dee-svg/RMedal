package com.name.rmedal.ui.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.name.rmedal.R;
import com.name.rmedal.base.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.veni.tools.StatusBarTools;
import com.veni.tools.view.ShoppingView;
import com.veni.tools.view.TitleView;
import com.veni.tools.view.ticker.TickerUtils;
import com.veni.tools.view.ticker.TickerView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 作者：kkan on 2018/2/24 14:41
 * 当前类注释:
 * 购物车
 */

public class TradeFragment extends BaseFragment{

    @BindView(R.id.trade_title_view)
    TitleView tradeTitleView;
    @BindView(R.id.trade_sv_1)
    ShoppingView tradeSv1;
    @BindView(R.id.trade_made_count)
    TickerView tradeMadeCount;
    @BindView(R.id.trade_refreshlayout)
    SmartRefreshLayout tradeRefreshlayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_trade;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //增加状态栏的高度
        StatusBarTools.setPaddingSmart(context, tradeTitleView);
        //设置显示标题
        tradeTitleView.setTitle("添加购物车");

        //设置需要滚动的字符
        tradeMadeCount.setCharacterList(TickerUtils.getDefaultNumberList());
        tradeMadeCount.setText("￥0.0", true);
        //购物车控件点击监听
        tradeSv1.setOnShoppingClickListener(new ShoppingView.ShoppingClickListener() {
            @Override
            public void onAddClick(int num) {
                tradeMadeCount.setText("数量："+num, true);
            }

            @Override
            public void onMinusClick(int num) {
                tradeMadeCount.setText("数量："+num, false);
            }
        });

        //SmartRefreshLayout 刷新加载监听
        tradeRefreshlayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                clooserefreshlayout();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                clooserefreshlayout();
            }
        });
        //SmartRefreshLayout 刷新加载Header样式
        tradeRefreshlayout.setRefreshHeader(new ClassicsHeader(context));
    }


    @OnClick({R.id.trade_made_count})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.trade_made_count:
                tradeSv1.setTextNum(1);
                break;
        }
    }

    /**
     * 模拟刷新加载
     */
    private void clooserefreshlayout() {
        mRxManager.add(Observable.timer(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        tradeMadeCount.setText("￥0.0", true);
                        tradeSv1.setTextNum(0);
                        tradeRefreshlayout.finishRefresh();
                        tradeRefreshlayout.finishLoadMore();
                    }
                }));

    }
}

