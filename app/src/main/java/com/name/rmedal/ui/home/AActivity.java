package com.name.rmedal.ui.home;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.veni.tools.widget.TitleView;

import butterknife.BindView;

/**
 * 作者：kkan on 2018/04/20
 * 当前类注释:
 */
public class AActivity extends BaseActivity {

    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.a_recycler_view)
    RecyclerView aRecyclerView;
    @BindView(R.id.a_smart_refresh_layout)
    SmartRefreshLayout aSmartRefreshLayout;


    @Override
    public int getLayoutId() {
        return R.layout.activity_a;
    }

    @Override
    public void initPresenter() {

    }


    @Override
    public void initView(Bundle savedInstanceState) {
        //设置显示标题
        toolbarTitleView.setTitle("a");

    }
}
