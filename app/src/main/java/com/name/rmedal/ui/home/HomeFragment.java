package com.name.rmedal.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.name.rmedal.R;
import com.name.rmedal.base.BaseFragment;
import com.name.rmedal.modelbean.BannerBean;
import com.name.rmedal.modelbean.NewsBean;
import com.name.rmedal.ui.AppConstant;
import com.name.rmedal.ui.bigimage.BigImageBean;
import com.name.rmedal.ui.bigimage.BigImagePagerActivity;
import com.name.rmedal.ui.home.contract.HomeContract;
import com.name.rmedal.ui.home.presenter.HomePresenter;
import com.name.rmedal.ui.main.MainActivity;
import com.name.rmedal.ui.web.WebViewActivity;
import com.name.rmedal.ui.zxing.android.CaptureActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.veni.tools.LogUtils;
import com.veni.tools.listener.OnNoFastClickListener;
import com.veni.tools.util.ACache;
import com.veni.tools.util.ClipboardUtils;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.DeviceUtils;
import com.veni.tools.util.JsonUtils;
import com.veni.tools.util.TimeUtils;
import com.veni.tools.util.ToastTool;
import com.veni.tools.widget.RunTextView;
import com.veni.tools.widget.TitleView;
import com.veni.tools.widget.verticalview.SimpleViewHolder;
import com.veni.tools.widget.verticalview.TextViewVertical;
import com.veni.tools.widget.verticalview.VerticalView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;


/**
 * 作者：kkan on 2018/2/24 14:41
 * 当前类注释:
 * 首页
 */
public class HomeFragment extends BaseFragment<HomePresenter> implements HomeContract.View {
    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.toolbar_line)
    View toolbarLine;
    @BindView(R.id.home_recycler_view)
    RecyclerView homeRecyclerView;
    @BindView(R.id.home_arrow_top)
    RelativeLayout homeArrowTop;
    @BindView(R.id.home_smart_refresh_layout)
    SmartRefreshLayout homeSmartRefreshLayout;

    /*onAttach()和 onHiddenChanged()根据需求可以保留或删除*/
    public void onAttach(Context context) {
        super.onAttach(context);
        //执行此方法 则说明会员Fragment 与Activity 绑定了
    }

    /**
     * Fragment 最前端界面显示状态
     *
     * @param hidden {@code true}: 不在最前端界面显示，相当于调用了onPause()
     *               {@code false}: 重新显示到最前端 ,相当于调用了onResume()
     *               进行网络数据刷新  此处执行必须要在 Fragment与Activity绑定了
     *               即需要添加判断是否完成绑定，
     *               否则将会报空（即非第一个显示出来的fragment，虽然onCreateView没有被调用,
     *               但是onHiddenChanged也会被调用，所以如果你尝试去获取活动的话，注意防止出现空指针）
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this);
    }

    private BaseQuickAdapter<NewsBean, BaseViewHolder> homeAdapter;
    private List<NewsBean> newsList;
    private int page = 1;
    private BGABanner bannerView;
    private RunTextView runtext;
    private TextViewVertical verticalView;
    private TextViewVertical verticalmoreView;
    //滚动的位置
    private int mScrollY = 0;

    @Override
    public void initView(Bundle savedInstanceState) {
        toolbarLine.setVisibility(View.GONE);
        //初始化TitleView
        bindTitleView();
        //初始化SmartRefreshLayout
        bindSmartRefreshLayout();
        //初始化QuickAdapter
        bindAdapter();
        //初始化TextViewVertical
        bindVertical();
        //请求数据
        viewOnRefresh();

        List<BannerBean> img_list = JsonUtils.parseArray(AppConstant.imgjson, BannerBean.class);

        bannerView.setData(img_list, null);

    }


    @OnClick({R.id.home_arrow_top})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_arrow_top:
                if (homeArrowTop.getVisibility() != View.GONE) {
                    homeArrowTop.setVisibility(View.GONE);
                }
                mScrollY = 0;
                homeRecyclerView.scrollToPosition(0);
                break;
        }
    }

    /**
     * 初始化QuickAdapter
     */
    private void bindAdapter() {
        // 初始化Recyclerview 的Adapter
        homeAdapter = new BaseQuickAdapter<NewsBean, BaseViewHolder>(R.layout.fragment_home_item) {
            @Override
            protected void convert(BaseViewHolder viewHolder, final NewsBean item) {
                ImageView function_item_iv = viewHolder.getView(R.id.home_item_iv);
                Glide.with(context).load(item.getImage()).apply(new RequestOptions()
                        .error(R.mipmap.ic_error_imageload)
                        .placeholder(R.mipmap.ic_holder_imageload).fitCenter())
                        .into(function_item_iv);

                viewHolder.setText(R.id.home_item_title, item.getTitle());
                viewHolder.setText(R.id.home_item_time, TimeUtils.formatDate(item.getPasstime()));
//                    function_item_iv.setImageDrawable(ContextCompat.getDrawable(context, image));

                viewHolder.setOnClickListener(R.id.home_item_ll, new OnNoFastClickListener() {
                    @Override
                    protected void onNoDoubleClick(View view) {

                        startActivity(WebViewActivity.class, WebViewActivity.startJumpObtain(item.getPath(), item.getTitle()));
                    }
                });
            }
        };
        View emptyView = LayoutInflater.from(context).inflate(R.layout.v_layout_empty_view, null, false);
        emptyView.setOnClickListener(new OnNoFastClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                viewOnRefresh();
            }
        });
        homeAdapter.setEmptyView(emptyView);

        // Recyclerview 添加Header
        homeAdapter.addHeaderView(bindHeaderView());

        //开启Recyclerview Item的加载动画
        homeAdapter.openLoadAnimation();
        // 初始化Recyclerview配置
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(context));
//        homeRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        homeRecyclerView.setAdapter(homeAdapter);
        homeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //获取手机屏幕的高
                if ((mScrollY * 1.5) >= DeviceUtils.getScreenHeights(context)) {
                    if (homeArrowTop.getVisibility() != View.VISIBLE) {
                        homeArrowTop.setVisibility(View.VISIBLE);
                    }
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        //停止滑动
//                    }
//                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//                        //正在滑动滑动
//                    }
                } else {
                    if (homeArrowTop.getVisibility() != View.GONE) {
                        homeArrowTop.setVisibility(View.GONE);
                    }
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mScrollY += dy;
//                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                //整个item的数量，必须是LinearLayout
//                int itemCount = manager.getItemCount();
//                //最后一个可见数量的位置
//                int lastItemPosition = manager.findLastVisibleItemPosition();
//                LogUtils.eTag(TAG, "滚动的位置---" + mScrollY,
//                        "item的数量---" + itemCount,
//                        "最后一个可见数量的位置---" + lastItemPosition);
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private View bindHeaderView() {
        //添加Header
        View headerView = LayoutInflater.from(context).inflate(R.layout.fragment_home_banner, null);

        bannerView = headerView.findViewById(R.id.home_item_banner);
        runtext = headerView.findViewById(R.id.home_item_runtext);
        verticalView = headerView.findViewById(R.id.home_item_vertical);
        verticalmoreView = headerView.findViewById(R.id.home_item_verticalmore);

        bannerView.setAdapter(new BGABanner.Adapter<ImageView, BannerBean>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, @Nullable BannerBean model, int position) {
                if (model != null) {
                    Glide.with(context).load(model.getImage_url()).apply(new RequestOptions()
                            .error(R.mipmap.ic_error_imageload)
                            .placeholder(R.mipmap.ic_holder_imageload).fitCenter())
                            .into(itemView);
                }
            }
        });
        bannerView.setDelegate(new BGABanner.Delegate<ImageView, BannerBean>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, @Nullable BannerBean model, int position) {
                if (model != null) {
                    //放大查看图片
                    List<BigImageBean> img_list = new ArrayList<>();
                    img_list.add(new BigImageBean(model.getImage_url()
                            , model.getImage_describe()));

                    startActivity(BigImagePagerActivity.class,
                            BigImagePagerActivity.startJump(img_list));
                }
            }
        });
        return headerView;
    }

    /**
     * 初始化SmartRefreshLayout
     */
    private void bindSmartRefreshLayout() {
        //SmartRefreshLayout 刷新加载监听
        homeSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                mPresenter.getWangYiNews(page);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                viewOnRefresh();
            }
        });
        //SmartRefreshLayout 刷新加载Header样式
        homeSmartRefreshLayout.setRefreshHeader(new ClassicsHeader(context));
    }

    /**
     * 初始化TitleView
     */
    private void bindTitleView() {
        toolbarTitleView.setTitle(R.string.homepage_title);
        toolbarTitleView.setRightIconVisibility(true);
        toolbarTitleView.setLeftIconVisibility(true);
        toolbarTitleView.setLeftIcon(R.mipmap.ic_nav);
        toolbarTitleView.setRightIcon(R.mipmap.ic_qrcode);
        toolbarTitleView.setLeftOnClickListener(new OnNoFastClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                FragmentActivity fragmentActivity = getActivity();
                if (fragmentActivity != null) {
                    ((MainActivity) fragmentActivity).onOffNavView();
                }
            }
        });
        toolbarTitleView.setRightOnClickListener(new OnNoFastClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                Intent intent =new Intent(context, CaptureActivity.class);
                startActivityForResult(intent, AppConstant.REQUEST_QRCODE);
            }
        });

    }

    private void bindVertical() {
       String runstr= "邻家小萝莉干了什么坏事，妈妈生气不理她了。爸爸温柔地告诉萝莉：妈妈在等你认错呢，快去吧。" +
                "萝莉就泪汪汪地过去对妈妈说：你……你是不是范冰冰？“啥？！”妈妈莫名其妙。萝莉继续泪汪汪：我认错了…";
        runtext.setText(runstr);

        ArrayList<String> titleList = new ArrayList<>();
        //模拟数据1
        titleList.add("你是天上最受宠的一架钢琴");
        titleList.add("我是丑人脸上的鼻涕");
        titleList.add("你发出完美的声音");
        titleList.add("我被默默揩去");
        titleList.add("你冷酷外表下藏着诗情画意");
        titleList.add("我已经够胖还吃东西");
        titleList.add("你踏着七彩祥云离去");
//        titleList.add("我被留在这里");

        //初始化TextViewVertical配置
        verticalmoreView.setData(titleList,new TextViewVertical.Adapter<String>() {
            @Override
            public void fillVerticalItem(VerticalView itemView, @Nullable String model, int position) {
                itemView.setContentText(model);
            }

            @Override
            public void onVerticalItemClick(VerticalView itemView, @Nullable String model, int position) {
                ToastTool.success("点击了 : " + position+"\nmodel"+model);
            }
        });

        verticalView.setData(R.layout.v_layout_empty_view,titleList,new TextViewVertical.ViewHolder<String>() {
            @Override
            public void onHolder(SimpleViewHolder holder, @Nullable final String model, final int position) {
                holder.setGone(R.id.empty_view_img,false);
                holder.setText(R.id.empty_view_tv,model);
                holder.setOnClickListener(R.id.empty_view_tv, new OnNoFastClickListener() {
                    @Override
                    protected void onNoDoubleClick(View view) {
                        ToastTool.success("点击了 : " + position+"\nmodel"+model);
                    }
                });
            }
        });
    }

    /**
     * 模拟数据2
     */
    private void setUPMarqueeView(List<View> views, ArrayList<String> titles) {
        Map<String, View> viewMap = new HashMap<>();
        for (int i = 0; i < titles.size(); i++) {
            final String title = titles.get(i);
            OnNoFastClickListener noFastClickListener = new OnNoFastClickListener() {
                @Override
                protected void onNoDoubleClick(View view) {
                    ToastTool.success("点击了 : " + title);
                }
            };
            LinearLayout moreView;
            if (i % 2 != 0) {
                moreView = (LinearLayout) viewMap.get("" + (i - 1));
                TextView tv2 = moreView.findViewById(R.id.runtext_item_tv2);
                RelativeLayout item_rl2 = moreView.findViewById(R.id.runtext_item_rl2);
                item_rl2.setVisibility(View.VISIBLE);
                //当数据是奇数时不需要赋值第二个，所以加了一个判断，并且把第二个布局给隐藏掉

                tv2.setText(title);
                tv2.setOnClickListener(noFastClickListener);
                //添加到循环滚动数组里面去
                views.add(moreView);
            } else {
                //设置滚动的单个布局
                moreView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.fragment_home_item_runtext, null);
                //初始化布局的控件
                TextView tv1 = moreView.findViewById(R.id.runtext_item_tv1);
                //进行对控件赋值
                tv1.setText(title);
                tv1.setOnClickListener(noFastClickListener);
                viewMap.put("" + i, moreView);
                if (i == (titles.size() - 1)) {
                    //添加到循环滚动数组里面去
                    views.add(moreView);
                }
            }
        }
    }

    /**
     * SmartRefreshLayout刷新
     */
    private void viewOnRefresh() {
        homeSmartRefreshLayout.setEnableLoadMore(true);
        page = 1;
        mPresenter.getWangYiNews(page);
    }

    @Override
    public void return_NewsData(List<NewsBean> data) {
        if (page == 1) {
            newsList = data;
        } else {
            newsList.addAll(data);
        }
        homeAdapter.replaceData(newsList);
        if (data.size() < AppConstant.pageSize) {
            homeSmartRefreshLayout.setEnableLoadMore(false);
        }
    }

    @Override
    public void onErrorSuccess(int code, String message, boolean isSuccess, boolean showTips) {
        homeSmartRefreshLayout.finishRefresh();
        homeSmartRefreshLayout.finishLoadMore();
        if (showTips && !isSuccess) {
            ToastTool.error(message);
        }
    }

    private void upScanerData(final String zxqrcode) {
        Object sc = ACache.get(context).getAsObject(AppConstant.ScanerCount);
        int scaner_count = sc == null ? 0 : (int) sc;
        scaner_count++;
        ACache.get(context).put(AppConstant.ScanerCount, scaner_count);
        creatDialogBuilder()
                .setDialog_title("扫描结果")
                .setDialog_message(zxqrcode)
                .setDialog_Left("确定")
                .setDialog_Right("取消")
                .setLeftlistener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastTool.success("扫描结果已复制到剪切板");
                        ClipboardUtils.copyText(context, zxqrcode);
                    }
                })
                .setCancelableOutside(true)
                .builder().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) { // Successfully.
            if (requestCode == AppConstant.REQUEST_QRCODE) {//扫码回调
                Bundle bundle = data.getExtras();
                String zxqrcode = null;
                if (bundle != null) {
                    zxqrcode = bundle.getString("result");
                }
                LogUtils.eTag(TAG, "" + zxqrcode);
                if (!DataUtils.isNullString(zxqrcode)) {
                    upScanerData(zxqrcode);
                } else {
                    ToastTool.success("扫描失败!");
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) { // User canceled.
            LogUtils.eTag(TAG, " User canceled.");
        }
    }
}

