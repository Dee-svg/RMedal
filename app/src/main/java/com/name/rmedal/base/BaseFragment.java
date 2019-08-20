package com.name.rmedal.base;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.name.rmedal.R;
import com.veni.tools.base.mvp.BasePresenter;
import com.veni.tools.base.ui.FragmentBase;
import com.veni.tools.widget.TitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:基类fragment
 */
public abstract class BaseFragment<T extends BasePresenter> extends FragmentBase<T> {
    @Nullable
    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @Nullable
    @BindView(R.id.toolbar_line)
    TextView toolbarLine;

    private Unbinder unbinder;

    @Override
    public void doAfterContentView() {
        super.doAfterContentView();
        if (rootView != null) {
            unbinder = ButterKnife.bind(this, rootView);
        }

        upToolBarLeftGone();

        immersive(toolbarTitleView, false);
    }

    public void upToolBarLeftGone(){
        if (toolbarTitleView != null) {
            toolbarTitleView.setLeftIconVisibility(false);
            toolbarTitleView.setLeftTextVisibility(false);
        }
        if (toolbarLine != null) {
            toolbarLine.setVisibility(View.GONE);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }


}
