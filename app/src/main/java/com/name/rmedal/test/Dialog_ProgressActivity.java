package com.name.rmedal.test;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.veni.tools.base.ActivityJumpOptionsTool;
import com.veni.tools.StatusBarTools;
import com.veni.tools.view.LabelsView;
import com.veni.tools.view.TitleView;
import com.veni.tools.view.progressing.SpinKitView;
import com.veni.tools.view.progressing.SpriteFactory;
import com.veni.tools.view.progressing.Style;
import com.veni.tools.view.progressing.sprite.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Dialog_ProgressActivity extends BaseActivity {

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
                .setClass(Dialog_ProgressActivity.class)
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
        toastTitleView.setTitle("Dialog_Progress");
        //设置侧滑退出
        setSwipeBackLayout(0);
        //SmartRefreshLayout 刷新加载监听
        toastRefreshlayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
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
        toastRefreshlayout.setRefreshHeader(new ClassicsHeader(context));

        // 初始化Recyclerview 的Adapter
        functionadapter =new BaseQuickAdapter<FunctionBean, BaseViewHolder>(R.layout.activity_toast_spink) {
            @Override
            protected void convert(BaseViewHolder viewHolder, FunctionBean item) {
                viewHolder.setText(R.id.spink_tv, item.getFunctionName());
                SpinKitView spinKitView = viewHolder.getView(R.id.spink_item);
                Sprite drawable = item.getSprite();
                spinKitView.setIndeterminateDrawable(drawable);
            }
        };
        //添加Recyclerview头部
        functionadapter.addHeaderView(upHeaderView());
        //开启Recyclerview Item的加载动画
        functionadapter.openLoadAnimation();
        //初始化Recyclerview配置
        toastRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        toastRecyclerview.setAdapter(functionadapter);

        //模拟数据
        List<FunctionBean> functionlist = new ArrayList<>();
        for(int i=0;i< Style.values().length;i++){
            Style style = Style.values()[i];
            Sprite drawable = SpriteFactory.create(style);
            drawable.setColor(R.color.colorAccent);
            //left 组件在容器X轴上的起点
            // top 组件在容器Y轴上的起点
            // right 组件的长度
            // bottom 组件的高度
            drawable.setBounds(0, 0, 100, 120);
            functionlist.add(new FunctionBean(style.name(),drawable,null));
        }
        //刷新adapter
        functionadapter.replaceData(functionlist);
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
        labellist.add("Dialog");
        labellist.add("loadingL");
        labellist.add("loadingT");
        labellist.add("loadingR");
        labellist.add("loadingB");
        labellist.add("三点上下晃动");
        labellist.add("两点上下晃动");
        labellist.add("波浪");
        labellist.add("对角旋转正方体");
        labellist.add("点追逐");
        labellist.add("圆圈 菊花");
        labellist.add("正方体网格");
        labellist.add("衰退圆圈");
        labellist.add("折叠正方体");
        labellist.add("复杂脉冲");
        labellist.add("复杂环形脉冲");
        toastLabels.setLabels(labellist); //直接设置一个字符串数组就可以了。

        toastLabels.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            public void onLabelClick(View label, String labelText, int position) {
                //label是被点击的标签，labelText是标签的文字，position是标签的位置。
                setfuctionview(labelText);
            }
        });
        return header;
    }

    /**
     *  LabelsView 的点击事件
     */
    private void setfuctionview(String labelstr) {
        switch (labelstr) {
            case "Dialog":
                creatDialogBuilder().setDialog_title("title")
                        .setDialog_message("message")
                        .setDialog_Left("leftbtn")
                        .setDialog_Right("rightbtn")
                        .setCancelable(true)
                        .setLeftlistener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setRightlistener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).builder().show();
                break;
            case "loadingL":
                creatDialogBuilder()
                        .setDialog_message("loading..两秒消失")
                        .setLoadingView(R.color.colorAccent)
                        .setCanceltime(2000)
                        .setDrawableseat(0)
                        .builder().show();
                break;
            case "loadingT":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .setLoadingView(R.color.colorAccent)
                        .setCancelable(true)
                        .setDrawableseat(1)
                        .builder().show();
                break;
            case "loadingR":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .setLoadingView(R.color.colorAccent)
                        .setCancelable(true)
                        .setDrawableseat(2)
                        .builder().show();
                break;
            case "loadingB":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .setCancelable(true)
                        .setDrawableseat(3)
                        .setLoadingView(R.color.colorAccent)
                        .builder().show();
                break;
            case "三点上下晃动":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .getThreeBounce(R.color.colorAccent)
                        .setCancelable(true)
                        .setLoadingView()
                        .builder().show();
                break;
            case "两点上下晃动":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .getDoubleBounce(R.color.colorAccent)
                        .setCancelable(true)
                        .setLoadingView()
                        .builder().show();
                break;
            case "波浪":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .getWave(R.color.colorAccent)
                        .setCancelable(true)
                        .setLoadingView()
                        .builder().show();
                break;
            case "对角旋转正方体":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .getWanderingCubes(R.color.colorAccent)
                        .setCancelable(true)
                        .setLoadingView()
                        .builder().show();
                break;
            case "点追逐":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .geChasingDots(R.color.colorAccent)
                        .setCancelable(true)
                        .setLoadingView()
                        .builder().show();
                break;
            case "圆圈 菊花":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .geCircle(R.color.colorAccent)
                        .setCancelable(true)
                        .setLoadingView()
                        .builder().show();
                break;
            case "正方体网格":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .geCubeGrid(R.color.colorAccent)
                        .setCancelable(true)
                        .setLoadingView()
                        .builder().show();
                break;
            case "衰退圆圈":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .geFadingCircle(R.color.colorAccent)
                        .setCancelable(true)
                        .setLoadingView()
                        .builder().show();
                break;
            case "折叠正方体":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .geFoldingCube(R.color.colorAccent)
                        .setCancelable(true)
                        .setLoadingView()
                        .builder().show();
                break;
            case "复杂脉冲":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .geMultiplePulse(R.color.colorAccent)
                        .setCancelable(true)
                        .setLoadingView()
                        .builder().show();
                break;
            case "复杂环形脉冲":
                creatDialogBuilder()
                        .setDialog_message("loading..")
                        .geMultiplePulseRing(R.color.colorAccent)
                        .setCancelable(true)
                        .setLoadingView()
                        .builder().show();
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
