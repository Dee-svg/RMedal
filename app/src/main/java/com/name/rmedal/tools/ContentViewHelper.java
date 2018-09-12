package com.name.rmedal.tools;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.name.rmedal.R;
import com.veni.tools.StatusBarTools;
import com.veni.tools.ToolBarUtils;

/**
 * 作者：kkan on 2017/12/15
 * 当前类注释:
 * 根布局视图构造器
 */
public class ContentViewHelper {
    private Context context;

    private TextView toolbarTvTitle;
    private Toolbar toolbarBaseTb;
    private FrameLayout toolbarLayout;
    private LinearLayout contentView;
    private FrameLayout userView;


    private int toolbarcolor = 0;

    //*视图构造器
    private LayoutInflater mInflater;
    private ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    /**
     * @param context      上下文
     * @param layout       布局id
     * @param toolbarcolor 标题栏颜色  0不需要标题栏
     */
    public ContentViewHelper(Context context, Object layout, int toolbarcolor) {
        this.context = context;
        this.toolbarcolor = toolbarcolor;
        mInflater = LayoutInflater.from(this.context);
        //初始化根布局
        initContentView();
        //初始化toolbar
        initToolBar();
        //初始化用户定义的布局
        if (layout != null) {
            initUserView(layout);
        }
    }

    /**
     * 构造空布局
     */
    private void initContentView() {
        //直接创建一个帧布局，作为视图容器的父容器
        contentView = new LinearLayout(context);
        contentView.setOrientation(LinearLayout.VERTICAL);
        contentView.setLayoutParams(params);

    }

    /**
     * 构造toolbar
     */
    private void initToolBar() {
        if (toolbarcolor != 0) {
            toolbarLayout = new FrameLayout(context);
            //通过inflater获取toolbar的布局文件
            mInflater.inflate(R.layout.layout_toolbar, toolbarLayout);

            toolbarTvTitle = (TextView) toolbarLayout.findViewById(R.id.toolbar_tv_title);
            toolbarBaseTb = (Toolbar) toolbarLayout.findViewById(R.id.toolbar_tb_id);
            toolbarBaseTb.setTitleTextColor(ContextCompat.getColor(context,
                    R.color.primary_text_default_material_dark));
            if (toolbarcolor != -1) {
                toolbarBaseTb.setBackgroundColor(ContextCompat.getColor(context, toolbarcolor));
            }
            onCreateCustomToolBar(context, toolbarBaseTb);
            // 添加toolbar布局
            contentView.addView(toolbarLayout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 初始化自定义布局
     */
    private void initUserView(Object layout) {
        // content FrameLayout
        userView = new FrameLayout(context);
        View view = null;
        if (layout instanceof Integer && (int) layout != 0) {
            view = mInflater.inflate((int) layout, null);
        } else if (layout instanceof View) {
            view = (View) layout;
        }
        userView.addView(view, params);
        contentView.addView(userView, params);
    }

    /**
     * 设置toolbar属性
     */
    public void onCreateCustomToolBar(Context context, Toolbar toolbarBaseTb) {
        ((AppCompatActivity) context).setSupportActionBar(toolbarBaseTb);
        ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_previous);
        }
    }

    /**
     *
     * @return 根布局视图
     */
    public LinearLayout getContentView() {
        return contentView;
    }

    /**
     * @return toolbar 布局视图 FrameLayout
     */
    public FrameLayout getToolbarLayout() {
        return toolbarLayout;
    }

    /**
     *
     * @return 自定义布局视图
     */
    public View getuserView() {
        return userView;
    }

    /**
     * @return toolbar标题 布局视图
     */
    public View getTitleTv() {
        return toolbarTvTitle;
    }

    /**
     * @return toolbar 布局视图 Toolbar
     */
    public Toolbar getToolBar() {
        return toolbarBaseTb;
    }

    /**
     * 设置 toolbar 按钮
     */
    public void setNavigationIcon(int resId) {
        if (toolbarBaseTb != null) {
            toolbarBaseTb.setNavigationIcon(resId);
        }
    }

    /**
     * 设置 toolbar 按钮
     */
    public void setNavigationIcon(Drawable icon) {
        if (toolbarBaseTb != null) {
            toolbarBaseTb.setNavigationIcon(icon);
        }
    }

    /**
     * 增加状态栏的高度
     */
    public void initToolbarState() {
        StatusBarTools.setPaddingSmart(context, toolbarBaseTb);
    }

    /**
     * 设置 toolbar 标题
     */
    public void setTitletext(CharSequence toolbarstring) {
        ToolBarUtils.getToolBarUtils().setTitletext(toolbarBaseTb, toolbarTvTitle, toolbarstring);
    }

    /**
     * 设置 toolbar 标题
     * mode =位置
     */
    public void setTitletext(CharSequence title, int mode) {
        ToolBarUtils.getToolBarUtils().setTitletext(toolbarBaseTb, toolbarTvTitle, title, mode);
    }

    /**
     * Toolbar添加子布局
     *
     * @param layoutResId
     */
    protected void setToolbarCustomView(@LayoutRes int layoutResId) {
        ToolBarUtils.getToolBarUtils().setToolbarCustomView(context, toolbarBaseTb, toolbarTvTitle, layoutResId);
    }

    protected void setToolbarCustomView(View view) {
        ToolBarUtils.getToolBarUtils().setToolbarCustomView(toolbarBaseTb, toolbarTvTitle, view);
    }
}
