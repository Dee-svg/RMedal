package com.name.rmedal.ui.bigimage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.veni.tools.LogUtils;
import com.veni.tools.VnUtils;
import com.veni.tools.base.ui.JumpOptions;
import com.veni.tools.listener.OnNoFastClickListener;
import com.veni.tools.util.JsonUtils;
import com.veni.tools.util.SystemUiVisibilityUtil;
import com.veni.tools.widget.TitleView;
import com.veni.tools.widget.TouchImageView;
import com.veni.tools.widget.ViewPagerFixed;
import com.veni.tools.widget.imageload.ImageLoaderTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：kkan on 2018/04/24
 * 当前类注释:
 * 查看大图 Glide 多张
 */
public class BigImagePagerActivity extends BaseActivity {

    private static final String INTENT_BIGIMAGEIMGLISTJSON = "img_list_json";
    private static final String INTENT_BIGIMAGEPOSITION = "position";
    private static final String INTENT_BIGIMAGETITLE = "needtitle";

    @BindView(R.id.bigimage_pager)
    ViewPagerFixed bigimagePager;
    @BindView(R.id.bigimage_pager_image)
    TouchImageView bigimagePagerImage;
    @BindView(R.id.bigimage_title_tv)
    TextView bigimageTitleTv;//图片标题
    @BindView(R.id.bigimage_guideGroup)
    TextView bigimageGuideGroup;//第几章图片 1/5
    @BindView(R.id.toolbar_title_view)
    TitleView toolbarTitleView;
    @BindView(R.id.toolbar_line)
    TextView toolbarLine;
    @BindView(R.id.toolbar_title_ll)
    LinearLayout toolbarTitleLl;
    @BindView(R.id.rl_rootview)
    RelativeLayout rlRootview;


    public static JumpOptions startJump(List<BigImageBean> img_list) {
        return startJump(img_list, 0, false);
    }

    public static JumpOptions startJump(List<BigImageBean> img_list, int position) {
        return startJump(img_list, position, false);
    }

    /**
     * 页面跳转参数
     *
     * @param img_list  图片集合的json数据
     * @param position  默认选中图片的位置
     * @param needtitle 是否读取 BigImageBean中的 图片标题
     */
    public static JumpOptions startJump(List<BigImageBean> img_list, int position, boolean needtitle) {
        String imglistjson = JsonUtils.toJson(img_list);
        return new JumpOptions()
                .setBundle(INTENT_BIGIMAGEIMGLISTJSON, imglistjson)
                .setBundle(INTENT_BIGIMAGEPOSITION, position)
                .setBundle(INTENT_BIGIMAGETITLE, needtitle);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bigimage_pager;
    }

    @Override
    public void initPresenter() {

    }

    private boolean mIsToolBarHidden;
    private boolean mIsStatusBarHidden;
    //图片数据
    private List<BigImageBean> img_list = new ArrayList<>();
    //是否读取 BigImageBean中的 图片标题
    private boolean needtitle;


    @Override
    public void initView(Bundle savedInstanceState) {

        toolbarLine.setVisibility(View.GONE);
        clooseNavigationBarReset();
        //第一次进入显示标题栏
        toolBarFadeIn();
        //初始化标题栏
        initToolbar();
        //设置背景黑色
        initBackground();
        //默认选中图片的位置
        int startPos = getIntent().getIntExtra(INTENT_BIGIMAGEPOSITION, 0);
        needtitle = getIntent().getBooleanExtra(INTENT_BIGIMAGETITLE, false);
        String imgUrls = getIntent().getStringExtra(INTENT_BIGIMAGEIMGLISTJSON);
        img_list = JsonUtils.parseArray(imgUrls, BigImageBean.class);
        //设置描述和标题
        setPhotoDetailTitle(startPos);
        LogUtils.eTag(TAG, "img_list---" + JsonUtils.toJson(img_list));
        if (img_list.size() == 1) {
            bigimagePagerImage.setVisibility(View.VISIBLE);
            bigimagePager.setVisibility(View.GONE);
            setPagerImage(bigimagePagerImage, img_list.get(0).getImage_url());
        } else {
            //设置适配器
            BigImageAdapter mAdapter = new BigImageAdapter(context);
            mAdapter.setDatas(img_list);
            bigimagePager.setAdapter(mAdapter);
            //ViewPager滑动监听
            bigimagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    setPhotoDetailTitle(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            //ViewPager选中的位置
            bigimagePager.setCurrentItem(startPos);
        }
    }

    //当api>21时 关闭全屏沉浸时的底部增高
    private void clooseNavigationBarReset() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBarUtils.setGonemNavBarTintView(false);
        }
    }

    /**
     * 初始化标题栏
     */
    private void initToolbar() {
        toolbarTitleView.setTitle("图片详情");
        toolbarTitleLl.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        toolbarTitleView.setBackgroundResource(R.drawable.bg_toolbar_trans_balck);
        toolbarTitleView.setTitleColor(ContextCompat.getColor(context, R.color.white));
    }

    /**
     * 图片显示的标题和描述
     *
     * @param position 图片位置
     */
    public void setPhotoDetailTitle(int position) {
        if (img_list.size() > 1 && img_list.size() > position) {
            bigimageGuideGroup.setVisibility(View.VISIBLE);
            bigimageTitleTv.setVisibility(View.VISIBLE);
            String phototitle = img_list.get(position).getImage_describe();
            String view_title = img_list.get(position).getImage_view_title();
            if (needtitle && !view_title.equals("")) {
                toolbarTitleView.setTitle(view_title);
            } else {
                toolbarTitleView.setTitle("图片详情");
            }
            bigimageTitleTv.setText(phototitle);
            bigimageGuideGroup.setText(getString(R.string.bigphoto_detail_num, position + 1,
                    img_list.size()));
        } else {
            bigimageTitleTv.setVisibility(View.GONE);
            bigimageGuideGroup.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private class BigImageAdapter extends PagerAdapter {

        private List<BigImageBean> datas = new ArrayList<>();
        private LayoutInflater inflater;
        private Context context;

        public void setDatas(List<BigImageBean> datas) {
            if (datas != null) {
                this.datas = datas;
            }
        }

        public BigImageAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.activity_bigimage_pager_item, container, false);
            TouchImageView imageView = view.findViewById(R.id.bigimage_pager_item_image);

            String path = this.datas.get(position).getImage_url();

            setPagerImage(imageView, path);

            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // 覆写destroyItem并且空实现,这样每个View视图就不会被销毁
//            container.removeView((View) object);
//            super.destroyItem(container, position, object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    private void setPagerImage(final TouchImageView imageView, String path) {
        imageView.setOnClickListener(new OnNoFastClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                hideOrShowToolbaranimate();
                hideOrShowStatusBar();
            }
        });
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        ImageLoaderTool.with(context)
                .loadUrl(uri)
                .setError(R.mipmap.ic_error_imageload)
                .setPlaceholder(R.mipmap.ic_holder_imageload)
                .override(800, 800).into(imageView);
    }

    /**
     * 设置背景黑色
     */
    private void initBackground() {
        ColorDrawable mBackground = new ColorDrawable(Color.BLACK);
        VnUtils.getRootView(this).setBackgroundDrawable(mBackground);
    }

    /**
     * toolbar 显示或隐藏的动画
     */
    protected void hideOrShowToolbaranimate() {
        toolbarTitleView.animate()
                .alpha(mIsToolBarHidden ? 1.0f : 0.0f)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        mIsToolBarHidden = !mIsToolBarHidden;
    }

    /**
     * 显示或隐藏状态栏
     */
    private void hideOrShowStatusBar() {
        if (mIsStatusBarHidden) {
            SystemUiVisibilityUtil.enter(context);
            statusBarUtils.setGonemNavBarTintView(true);
        } else {
            SystemUiVisibilityUtil.exit(context);
            statusBarUtils.setGonemNavBarTintView(false);

        }
        mIsStatusBarHidden = !mIsStatusBarHidden;
    }

    /**
     * 显示标题栏
     */
    private void toolBarFadeIn() {
        mIsToolBarHidden = true;
        hideOrShowToolbaranimate();
    }

}
