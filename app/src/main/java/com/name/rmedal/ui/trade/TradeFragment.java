package com.name.rmedal.ui.trade;

import android.os.Bundle;
import android.view.View;

import com.name.rmedal.R;
import com.name.rmedal.base.BaseFragment;
import com.name.rmedal.modelbean.CardDataItem;
import com.name.rmedal.ui.AppConstant;
import com.name.rmedal.ui.bigimage.BigImageBean;
import com.name.rmedal.ui.bigimage.BigImagePagerActivity;
import com.name.rmedal.ui.trade.adapter.CardGroupAdapter;
import com.name.rmedal.widget.cardslide.CardSlidePanel;
import com.veni.tools.LogUtils;
import com.veni.tools.listener.OnNoFastClickListener;
import com.veni.tools.util.ImageUtils;
import com.veni.tools.util.JsonUtils;
import com.veni.tools.util.ToastTool;
import com.veni.tools.widget.ShoppingView;
import com.veni.tools.widget.TitleView;
import com.veni.tools.widget.ticker.TickerUtils;
import com.veni.tools.widget.ticker.TickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 作者：kkan on 2018/2/24 14:41
 * 当前类注释:
 * 购物车
 */

public class TradeFragment extends BaseFragment{

    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.trade_sv_1)
    ShoppingView tradeSv1;
    @BindView(R.id.trade_made_count)
    TickerView tradeMadeCount;
    @BindView(R.id.trade_cardgroup)
    CardSlidePanel tradeCardgroup;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_trade;
    }

    @Override
    public void initPresenter() {

    }

    private CardSlidePanel.CardSwitchListener cardSwitchListener;// 左右滑动监听
    private CardGroupAdapter cardGroupAdapter;//层叠卡片的Adapter
    private List<CardDataItem> dataList = new ArrayList<>();
    @Override
    public void initView(Bundle savedInstanceState) {
        toolbarTitleView.setLeftIconVisibility(false);
        toolbarTitleView.setRightTextVisibility(true);
        //设置显示标题
        toolbarTitleView.setTitle(R.string.trade_title);
        toolbarTitleView.setRightText("添加");
        toolbarTitleView.setRightTextSize(ImageUtils.dpToPx(context,15));
        toolbarTitleView.setRightTextOnClickListener(new OnNoFastClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                cardGroupAdapter.upData(dataList);
            }
        });

        //设置需要滚动的字符
        tradeMadeCount.setCharacterList(TickerUtils.getDefaultNumberList());
        tradeMadeCount.setText("￥0.0", true);
        //购物车控件点击监听
        tradeSv1.setOnShoppingClickListener(new ShoppingView.ShoppingClickListener() {
            @Override
            public void onAddClick(int num) {
                tradeMadeCount.setText("￥："+num, true);
            }

            @Override
            public void onMinusClick(int num) {
                tradeMadeCount.setText("￥："+num, false);
            }
        });

        // 1. 左右滑动监听
        cardSwitchListener = new CardSlidePanel.CardSwitchListener() {

            @Override
            public void onShow(int index) {
                LogUtils.eTag(TAG, "正在显示---" + index);
            }

            @Override
            public void onCardVanish(int index, int type) {
                LogUtils.eTag(TAG, "正在消失---" + index + " 消失type=" + type);
            }
        };
        // 添加左右滑动监听
        tradeCardgroup.setCardSwitchListener(cardSwitchListener);
        //初始化层叠卡片的Adapter
        cardGroupAdapter = new CardGroupAdapter(context);
        cardGroupAdapter.setOnCatdItemListener(new CardGroupAdapter.OnCatdItemListener() {
            @Override
            public void onViewClickMask(View mView, CardDataItem itemData) {
                //放大查看图片
                List<BigImageBean> img_list = new ArrayList<>();
                img_list.add(itemData);

                startActivity(BigImagePagerActivity.class,
                        BigImagePagerActivity.startJump(img_list));
            }

            @Override
            public void onViewClickContent(View mView, CardDataItem itemData) {
                ToastTool.normal(""+itemData.getImage_describe());
            }
        });
        tradeCardgroup.setAdapter(cardGroupAdapter);
        //更新显示数据
        dataList = JsonUtils.parseArray(AppConstant.imgjson, CardDataItem.class);
        cardGroupAdapter.upData(dataList);


        tradeMadeCount.setText("￥0.0", true);
        tradeSv1.setTextNum(0);
    }


    @OnClick({R.id.trade_made_count})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.trade_made_count:
                tradeSv1.setTextNum(1);
                tradeMadeCount.setText("￥：1", false);
                break;
        }
    }

}

