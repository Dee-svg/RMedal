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
import com.veni.tools.StatusBarTools;
import com.veni.tools.base.ActivityJumpOptionsTool;
import com.veni.tools.view.LabelsView;
import com.veni.tools.view.TitleView;
import com.veni.tools.view.ToastTool;

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
 * Toast
 */

public class ToastActivity extends BaseActivity {

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
                .setClass(ToastActivity.class)
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
        toastTitleView.setTitle("Toast_LabelsView");
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
        functionadapter =new BaseQuickAdapter<FunctionBean, BaseViewHolder>(R.layout.activity_toast_spink) {
            @Override
            protected void convert(BaseViewHolder viewHolder, FunctionBean item) {
            }
        };

        // Recyclerview 添加Header
        functionadapter.addHeaderView(upHeaderView());
        //开启Recyclerview Item的加载动画
        functionadapter.openLoadAnimation();
        // 初始化Recyclerview配置
        toastRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        toastRecyclerview.setAdapter(functionadapter);
    }

    /**
     * Recyclerview 添加Header
     * @return HeaderView
     */
    private View upHeaderView(){
        //添加Header
        View header = LayoutInflater.from(this).inflate(R.layout.activity_toast_lables, null, false);

        LabelsView toastLabels=  header.findViewById(R.id.toast_labels);
        labellist = new ArrayList<>();
        labellist.add("系统提示");
        labellist.add("普通提示");
        labellist.add("错误提示");
        labellist.add("警告提示");
        labellist.add("成功提示");
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
            case "系统提示":
                ToastTool.showToast("showToast");
                break;
            case "普通提示":
                ToastTool.normal("这是一个普通提示的Toast");
                break;
            case "信息提示":
                ToastTool.info("这是一个信息提示的Toast");
                break;
            case "错误提示":
                ToastTool.error("这是一个错误提示的Toast");
                break;
            case "警告提示":
                ToastTool.warning("这是一个警告提示的Toast");
                break;
            case "成功提示":
                ToastTool.success("这是一个成功提示的Toast");
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
                        toastRefreshlayout.finishRefresh();
                        toastRefreshlayout.finishLoadMore();
                    }
                }));
    }
}
