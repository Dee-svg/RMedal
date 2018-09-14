package com.name.rmedal.bigimage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.name.rmedal.R;
import com.name.rmedal.base.BaseActivity;
import com.name.rmedal.tools.SystemUiVisibilityUtil;
import com.veni.tools.JsonTools;
import com.veni.tools.TabLayoutTools;
import com.veni.tools.base.ActivityJumpOptionsTool;
import com.veni.tools.interfaces.OnNoFastClickListener;
import com.veni.tools.StatusBarTools;
import com.veni.tools.view.ViewPagerFixed;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 作者：kkan on 2018/04/24
 * 当前类注释:
 * 查看大图 Glide 多张
 */
public class BigImagePagerActivity extends BaseActivity {

    @BindView(R.id.photo_det_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bigimage_pager)
    ViewPagerFixed bigimagViewPager;
    @BindView(R.id.bigimage_guideGroup)
    TextView bigimageGuideGroup;//第几章图片 1/5
    @BindView(R.id.bigimage_title_tv)
    TextView bigimageTitleTv;//图片标题

    private boolean mIsToolBarHidden;
    private boolean mIsStatusBarHidden;

    public static final String INTENT_IMGLISTJSON = "img_list_json";
    public static final String INTENT_POSITION = "position";
    public static final String INTENT_TITLE = "needtitle";

    public static void startAction(Context context, String imglistjson, int position) {
        startAction(context, imglistjson, position, false);
    }

    /**
     * 页面跳转参数
     *
     * @param context     context
     * @param imglistjson 图片集合的json数据
     * @param position    默认选中图片的位置
     * @param needtitle   是否读取 BigImageBean中的 图片标题
     */
    public static void startAction(Context context, String imglistjson, int position, boolean needtitle) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(BigImagePagerActivity.class)
                .setBundle(INTENT_IMGLISTJSON, imglistjson)
                .setBundle(INTENT_POSITION, position)
                .setBundle(INTENT_TITLE, needtitle)
                .customAnim()
                .start();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bigimage_pager;
    }

    @Override
    public void initPresenter() {

    }

    //图片数据
    private List<BigImageBean> img_list = new ArrayList<>();
    //是否读取 BigImageBean中的 图片标题
    private boolean needtitle;

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置侧滑退出
        setSwipeBackLayout(0);
        //设置沉侵状态栏
        StatusBarTools.immersive(context);
        //增加状态栏的高度
        StatusBarTools.setPaddingSmart(context, toolbar);
        //第一次进入显示标题栏
        toolBarFadeIn();
        //初始化标题栏
        initToolbar();
        //设置背景黑色
        initBackground();
        //默认选中图片的位置
        int startPos = getIntent().getIntExtra(INTENT_POSITION, 0);
        needtitle = getIntent().getBooleanExtra(INTENT_TITLE, false);
        String imgUrls = getIntent().getStringExtra(INTENT_IMGLISTJSON);
        img_list = JsonTools.parseArray(imgUrls, BigImageBean.class);
        //设置描述和标题
        setPhotoDetailTitle(startPos);
        //设置适配器
        BigImageAdapter mAdapter = new BigImageAdapter(context);
        mAdapter.setDatas(img_list);
        bigimagViewPager.setAdapter(mAdapter);
        //ViewPager滑动监听
        bigimagViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
        bigimagViewPager.setCurrentItem(startPos);
    }

    /**
     * 初始化标题栏
     */
    private void initToolbar() {
        toolbar.setTitle("图片详情");
        onCreateCustomToolBar(toolbar, true);
    }

    /**
     * 图片显示的标题和描述
     *
     * @param position 图片位置
     */
    public void setPhotoDetailTitle(int position) {
        if (img_list.size() > position) {
            if (img_list.size() == 1) {
                bigimageGuideGroup.setVisibility(View.GONE);
                bigimageTitleTv.setVisibility(View.GONE);
            } else {
                bigimageGuideGroup.setVisibility(View.VISIBLE);
                bigimageTitleTv.setVisibility(View.VISIBLE);
                String phototitle = img_list.get(position).getImage_describe();
                String view_title = img_list.get(position).getImage_view_title();
                if (needtitle && !view_title.equals("")) {
                    toolbar.setTitle(view_title);
                } else {
                    toolbar.setTitle("图片详情");
                }
                bigimageTitleTv.setText(phototitle);
                bigimageGuideGroup.setText(getString(R.string.photo_detail_num, position + 1,
                        img_list.size()));
            }
        } else {
            bigimageTitleTv.setVisibility(View.GONE);
            bigimageGuideGroup.setVisibility(View.GONE);
        }
    }

    private class BigImageAdapter extends PagerAdapter {

        private List<BigImageBean> datas = new ArrayList<>();
        private LayoutInflater inflater;
        private Context context;

        public void setDatas(List<BigImageBean> datas) {
            if (datas != null)
                this.datas = datas;
        }

        public BigImageAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            if (datas == null) return 0;
            return datas.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.item_pager_image, container, false);
            ImageView imageView = view.findViewById(R.id.image);

            String path = this.datas.get(position).getImage_url();
            Uri uri;
            if (path.startsWith("http")) {
                uri = Uri.parse(path);
            } else {
                uri = Uri.fromFile(new File(path));
            }
            imageView.setOnClickListener(new OnNoFastClickListener() {
                @Override
                protected void onNoDoubleClick(View view) {
                    hideOrShowToolbaranimate();
                    hideOrShowStatusBar();
                }
            });

            //loading
            final ProgressBar loading = new ProgressBar(context);
            FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            loadingLayoutParams.gravity = Gravity.CENTER;
            loading.setLayoutParams(loadingLayoutParams);
            ((FrameLayout) view).addView(loading);


            loading.setVisibility(View.VISIBLE);

            Glide.with(context).load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(me.iwf.photopicker.R.drawable.__picker_ic_photo_black_48dp)
                    .error(R.drawable.ic_empty_picture)
                    .dontAnimate().dontTransform().override(800, 800)
                    .thumbnail(0.1f)
                    .listener(new RequestListener<Uri, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                            loading.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            loading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);

            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
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

    /**
     * 设置背景黑色
     */
    private void initBackground() {
        ColorDrawable mBackground = new ColorDrawable(Color.BLACK);
        TabLayoutTools.getRootView(this).setBackgroundDrawable(mBackground);
    }

    /**
     * toolbar 显示或隐藏的动画
     */
    protected void hideOrShowToolbaranimate() {
        toolbar.animate()
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
        } else {
            SystemUiVisibilityUtil.exit(context);
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
