package com.name.rmedal.test;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.modelbean.FunctionBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.veni.tools.ACache;
import com.veni.tools.DataTools;
import com.veni.tools.TimeTools;
import com.veni.tools.base.ActivityJumpOptionsTool;
import com.veni.tools.StatusBarTools;
import com.veni.tools.view.LabelsView;
import com.veni.tools.view.TitleView;

import java.util.ArrayList;
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
 * 时效存储
 */

public class ACacheActivity extends BaseActivity {

    @BindView(R.id.toast_title_view)
    TitleView toastTitleView;
    @BindView(R.id.toast_refreshlayout)
    SmartRefreshLayout toastRefreshlayout;
    @BindView(R.id.toast_recyclerview)
    RecyclerView toastRecyclerview;

    /**
     * 启动入口
     */
    public static void startAction(Context context) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(ACacheActivity.class)
                .customAnim()
                .start();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_toast;
    }

    @Override
    public void initPresenter() {

    }

    private List<String> labellist;

    private BaseQuickAdapter<FunctionBean, BaseViewHolder> functionadapter;

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        //增加状态栏的高度
        StatusBarTools.setPaddingSmart(this, toastTitleView);
        //设置返回点击事件
        toastTitleView.setLeftFinish(context);
        //设置显示标题
        toastTitleView.setTitle("时效存储");
        //设置侧滑退出
        setSwipeBackLayout(0);

        //SmartRefreshLayout 刷新加载监听
        toastRefreshlayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                clooserefreshlayout();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                clooserefreshlayout();
            }
        });
        //SmartRefreshLayout 刷新加载Header样式
        toastRefreshlayout.setRefreshHeader(new ClassicsHeader(context));
        // 初始化Recyclerview 的Adapter
        functionadapter = new BaseQuickAdapter<FunctionBean, BaseViewHolder>(R.layout.activity_toast_spink) {
            @Override
            protected void convert(BaseViewHolder viewHolder, FunctionBean item) {
                viewHolder.setVisible(R.id.spink_item, false)
                        .setText(R.id.spink_tv, item.getFunctionName());
            }
        };
        // Recyclerview 添加Header
        functionadapter.addHeaderView(upHeaderView());
        //开启Recyclerview Item的加载动画
        functionadapter.openLoadAnimation();
        // 初始化Recyclerview配置
        toastRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        toastRecyclerview.setAdapter(functionadapter);

        replaceadapter("请插入或读取数据");
    }

    /**
     * 刷新数据
     * @param tipstr 模拟数据
     */
    private void replaceadapter(String tipstr) {
        List<FunctionBean> functionlist = new ArrayList<>();
        functionlist.add(new FunctionBean(tipstr, 0, null));
        functionadapter.replaceData(functionlist);
    }

    /**
     * Recyclerview 添加Header
     * @return HeaderView
     */
    private View upHeaderView() {
        //添加Header
        View header = LayoutInflater.from(this).inflate(R.layout.activity_toast_lables, null, false);

        LabelsView toastLabels = header.findViewById(R.id.toast_labels);
        labellist = new ArrayList<>();
        labellist.add("插入数据");
        labellist.add("读取插入数据");
        toastLabels.setLabels(labellist); //直接设置一个字符串数组就可以了。

        toastLabels.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            public void onLabelClick(View label, String labelText, int position) {
                //label是被点击的标签，labelText是标签的文字，position是标签的位置。
                String labelstr = labellist.get(position);
                setfuctionview(labelstr);
            }
        });
        return header;
    }

    /**
     *  LabelsView 的点击事件
     */
    private void setfuctionview(String labelstr) {
        switch (labelstr) {
            case "插入数据": {
                String date= TimeTools.getCurrentDate(TimeTools.dateFormatYMDHMS);
                ACache.get(context).put("数据", date, ACache.TIME_MINUTE);
                long time = ACache.get(context).getKeyTimes("数据");
                replaceadapter("插入数据\nkey:数据\nvalue:"+date+"\n剩余时间:" + time);
                break;
            }
            case "读取插入数据": {
                String value = ACache.get(context).getAsString("数据");
                long time = ACache.get(context).getKeyTimes("数据");
                replaceadapter("读取数据\nkey:数据\nvalue:" + value + "\n剩余时间:" + time);
                break;
            }
        }

    }

    /**
     * 模拟刷新加载
     */
    private void clooserefreshlayout() {
        mRxManager.add(Observable.timer(1500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        toastRefreshlayout.finishRefresh();
                        toastRefreshlayout.finishLoadMore();
                        replaceadapter("请插入或读取数据");
                    }
                }));
    }
}
