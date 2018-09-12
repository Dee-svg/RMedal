package com.name.rmedal.test;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.TextView;

import com.name.rmedal.R;
import com.name.rmedal.api.AppConstant;
import com.name.rmedal.base.BaseActivity;
import com.veni.tools.ACache;
import com.veni.tools.DataTools;
import com.veni.tools.SPTools;
import com.veni.tools.StatusBarTools;
import com.veni.tools.base.ActivityJumpOptionsTool;
import com.veni.tools.view.LabelsView;
import com.veni.tools.view.TitleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 */
public class CacheActivity extends BaseActivity {

    @BindView(R.id.cache_title_view)
    TitleView cacheTitleView;
    @BindView(R.id.cache_labels)
    LabelsView cacheLabels;
    @BindView(R.id.cache_data)
    TextView cacheData;

    /**
     * 启动入口
     */
    public static void startAction(Context context) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(CacheActivity.class)
                .customAnim()
                .start();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_cache;
    }

    @Override
    public void initPresenter() {

    }

    private List<String> labellist;
    private String sizeofSP;
    private String sizeofAC;

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        //增加状态栏的高度
        StatusBarTools.setPaddingSmart(this, cacheTitleView);
        //设置返回点击事件
        cacheTitleView.setLeftFinish(context);
        //设置显示标题
        cacheTitleView.setTitle("Cache");
        //设置侧滑退出
        setSwipeBackLayout(0);
        initlavlesData();
    }

    private void initlavlesData() {
        labellist = new ArrayList<>();
        labellist.add("读取ACache大小");
        labellist.add("读取SP大小");
        labellist.add("清除ACache");
        labellist.add("清除App_Sp缓存");
        labellist.add("清除App_Data缓存");
        labellist.add("清除App_所有缓存");
        cacheLabels.setLabels(labellist); //直接设置一个字符串数组就可以了。

        cacheLabels.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            public void onLabelClick(View label, String labelText, int position) {
                //label是被点击的标签，labelText是标签的文字，position是标签的位置。
                String labelstr = labellist.get(position);
                setfuctionview(labelstr);
            }
        });
    }

    /**
     * LabelsView 的点击事件
     */
    private void setfuctionview(String labelstr) {
        switch (labelstr) {
            case "读取ACache大小": {
                long acdatasize = ACache.get(context).getCacheSize();
                sizeofAC = DataTools.byte2FitSize(acdatasize);
                upcacheData();
                break;
            }
            case "读取SP大小": {
                long spdatasize = SPTools.getTotalCacheSize(context);
                sizeofSP = DataTools.byte2FitSize(spdatasize);
                upcacheData();
                break;
            }
            case "清除ACache": {
                ACache.get(context).clear();
                long acdatasize = ACache.get(context).getCacheSize();
                sizeofAC = DataTools.byte2FitSize(acdatasize);
                upcacheData();
                break;
            }
            case "清除App_Sp缓存": {
                SPTools.clear(context);
                long spdatasize = SPTools.getTotalCacheSize(context);
                sizeofSP = DataTools.byte2FitSize(spdatasize);
                upcacheData();
                break;
            }
            case "清除App_Data缓存": {
                SPTools.clearDataCache(context);
                long spdatasize = SPTools.getTotalCacheSize(context);
                sizeofSP = DataTools.byte2FitSize(spdatasize);
                upcacheData();
                break;
            }
            case "清除App_所有缓存": {
                SPTools.clearAllCache(context);
                long spdatasize = SPTools.getTotalCacheSize(context);
                sizeofSP = DataTools.byte2FitSize(spdatasize);
                upcacheData();
                break;
            }
        }
    }

    private void upcacheData() {
        String dd = "SP大小:" + sizeofSP + "\n" + "ACache大小:" + sizeofAC + "\n";
        cacheData.setText(dd);
    }
}
