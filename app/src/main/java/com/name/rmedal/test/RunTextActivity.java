package com.name.rmedal.test;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.name.rmedal.R;
import com.name.rmedal.api.AppConstant;
import com.name.rmedal.base.BaseActivity;
import com.veni.tools.StatusBarTools;
import com.veni.tools.base.ActivityJumpOptionsTool;
import com.veni.tools.view.RunTextView;
import com.veni.tools.view.TextViewVertical;
import com.veni.tools.view.TextViewVerticalMore;
import com.veni.tools.view.TitleView;
import com.veni.tools.view.ToastTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 *  RunText
 */

public class RunTextActivity extends BaseActivity {

    @BindView(R.id.runtext_title_view)
    TitleView runtextTitleView;
    @BindView(R.id.runtext_runtitle)
    RunTextView runtextRuntitle;
    @BindView(R.id.runtext_vertical_view)
    TextViewVertical runtextVerticalView;
    @BindView(R.id.runtext_verticalmore_view)
    TextViewVerticalMore runtextVerticalmoreView;

    /**
     * 启动入口
     */
    public static void startAction(Context context, View view) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(RunTextActivity.class)
                .setView(view)
                .setActionString(AppConstant.TRANSITION_ANIMATION)
                .screenTransitAnim()
                .start();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_runtext;
    }

    @Override
    public void initPresenter() {

    }

    private ArrayList<String> titleList = new ArrayList<>();

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置启动动画对应的view
        View view = runtextTitleView.getTvTitle();
        ViewCompat.setTransitionName(view, AppConstant.TRANSITION_ANIMATION);
        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        //增加状态栏的高度
        StatusBarTools.setPaddingSmart(this, runtextTitleView);
        //设置返回点击事件
        runtextTitleView.setLeftFinish(context);
        //设置显示标题
        runtextTitleView.setTitle("RunTextView");
        //设置侧滑退出
        setSwipeBackLayout(0);

        //模拟数据1
        titleList.add("你是天上最受宠的一架钢琴");
        titleList.add("我是丑人脸上的鼻涕");
        titleList.add("你发出完美的声音");
        titleList.add("我被默默揩去");
        titleList.add("你冷酷外表下藏着诗情画意");
        titleList.add("我已经够胖还吃东西");
        titleList.add("你踏着七彩祥云离去");
        titleList.add("我被留在这里");

        //初始化TextViewVertical配置
        runtextVerticalView.setTextList(titleList);
        runtextVerticalView.setText(26, 5, 0xff766156);//设置属性
        runtextVerticalView.setTextStillTime(3000);//设置停留时长间隔
        runtextVerticalView.setAnimTime(300);//设置进入和退出的时间间隔
        runtextVerticalView.setOnItemClickListener(new TextViewVertical.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ToastTool.success(context, "点击了 : " + titleList.get(position), Toast.LENGTH_SHORT, true).show();
            }
        });

        List<View> views = new ArrayList<>();
        setUPMarqueeView(views, 11);
        runtextVerticalmoreView.setViews(views);
        runtextVerticalmoreView.setOnItemClickListener(new TextViewVerticalMore.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                ToastTool.success("点击了 : " + position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        runtextVerticalView.startAutoScroll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        runtextVerticalView.stopAutoScroll();
    }

    /**
     * 模拟数据2
     */
    private void setUPMarqueeView(List<View> views, int size) {
        for (int i = 0; i < size; i = i + 2) {
            //设置滚动的单个布局
            LinearLayout moreView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.activity_runtext_item, null);
            //初始化布局的控件
            TextView tv1 = (TextView) moreView.findViewById(R.id.runtext_item_tv1);
            TextView tv2 = (TextView) moreView.findViewById(R.id.runtext_item_tv2);
            //进行对控件赋值
            tv1.setText("五一欢乐与您共享，ＸＸ节能高清惊喜大促销。");
            if (size > i + 1) {
                //因为淘宝那儿是两条数据，但是当数据是奇数时就不需要赋值第二个，所以加了一个判断，还应该把第二个布局给隐藏掉
                tv2.setText("五一充值送机，你准备好了吗？");
            } else {
                tv2.setVisibility(View.GONE);
            }

            //添加到循环滚动数组里面去
            views.add(moreView);
        }
    }
}
