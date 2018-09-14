package com.name.rmedal.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.modelbean.CardDataItem;
import com.name.rmedal.test.adapter.CardGroupAdapter;
import com.veni.tools.LogTools;
import com.veni.tools.StatusBarTools;
import com.veni.tools.base.ActivityJumpOptionsTool;
import com.veni.tools.view.TitleView;
import com.veni.tools.view.cardslide.CardSlidePanel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 * 拖拽式层叠卡片
 */

public class CardGroupActivity extends BaseActivity {

    @BindView(R.id.cardgroupview_title_view)
    TitleView cardgroupviewTitleView;
    @BindView(R.id.cardgroupview_view)
    CardSlidePanel slidePanel;

    /**
     * 启动入口
     */
    public static void startAction(Context context) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(CardGroupActivity.class)
                .customAnim()
                .start();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_cardgroupview;
    }

    @Override
    public void initPresenter() {

    }

    private CardSlidePanel.CardSwitchListener cardSwitchListener;// 左右滑动监听
    private CardGroupAdapter cardGroupAdapter;//层叠卡片的Adapter
    private List<CardDataItem> dataList = new ArrayList<>();

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        //增加状态栏的高度
        StatusBarTools.setPaddingSmart(this, cardgroupviewTitleView);
        //设置返回点击事件
        cardgroupviewTitleView.setLeftFinish(context);
        //设置显示标题
        cardgroupviewTitleView.setTitle("拖拽式层叠卡片");
        //设置标题栏右侧图片
        cardgroupviewTitleView.setRightIcon(R.mipmap.card_left2);
        //显示标题栏右侧图片
        cardgroupviewTitleView.setRightIconVisibility(true);
        //标题栏右侧图片点击事件
        cardgroupviewTitleView.setRightIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCard();
                addCard();
                cardGroupAdapter.upData(dataList);
            }
        });
        //设置侧滑退出
        setSwipeBackLayout(0);
        // 1. 左右滑动监听
        cardSwitchListener = new CardSlidePanel.CardSwitchListener() {

            @Override
            public void onShow(int index) {
                LogTools.e(TAG, "正在显示---" + index);
            }

            @Override
            public void onCardVanish(int index, int type) {
                LogTools.e(TAG, "正在消失---" + index + " 消失type=" + type);
            }
        };
        // 添加左右滑动监听
        slidePanel.setCardSwitchListener(cardSwitchListener);
        //初始化层叠卡片的Adapter
        cardGroupAdapter = new CardGroupAdapter(context);
        slidePanel.setAdapter(cardGroupAdapter);
        //更新显示数据
        addCard();
        addCard();
        cardGroupAdapter.upData(dataList);
    }

    /**
     * 模拟数据
     */
    private void addCard() {
        dataList.add(new CardDataItem("http://a0.att.hudong.com/31/35/300533991095135084358827466.jpg"
                ,"美女111"));
        dataList.add(new CardDataItem("http://a3.topitme.com/1/21/79/1128833621e7779211o.jpg"
                ,"美女222"));
        dataList.add(new CardDataItem("http://x.itunes123.com/uploadfiles/1b13c3044431fb712bb712da97f42a2d.jpg"
                ,"美女333"));
        dataList.add(new CardDataItem("http://x.itunes123.com/uploadfiles/a3864382d68ce93bb7ab84775cb12d17.jpg"
                ,"美女444"));
        dataList.add(new CardDataItem("http://c.hiphotos.baidu.com/image/pic/item/9d82d158ccbf6c81924a92c5b13eb13533fa4099.jpg"
                ,"美女555"));
    }


}
