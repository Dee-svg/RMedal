package com.veni.tools.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.veni.tools.R;
import com.veni.tools.VnUtils;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.ImageUtils;
import com.veni.tools.util.KeyboardUtils;

/**
 * @author by kkan on 2017/1/2.
 * 自定义标题控件
 * setLeftText
 * setLeftTextColor
 * setLeftTextSize
 * setLeftTextVisibility
 * setRightText
 * setRightTextCompoundDrawables
 * setRightTextColor
 * setRightTextSize
 * setRightTextVisibility
 * setTitle
 * setTitleColor
 * setTitleSize
 * setLeftIcon
 * setRightIcon
 * setLeftIconVisibility
 * setRightIconVisibility
 * setLeftFinish
 * setLeftOnClickListener
 * setRightOnClickListener
 * setLeftTextOnClickListener
 * setRightTextOnClickListener
 * setLeftIconOnClickListener
 * setRightIconOnClickListener
 */
public class TitleView extends FrameLayout {
    //*******************************************控件start******************************************
    private RelativeLayout mRootLayout;//根布局

    private TextAutoZoom mTvTitle;//Title的TextView控件

    private LinearLayout mLlLeft;//左边布局

    private ImageView mIvLeft;//左边ImageView控件

    private TextView mTvLeft;//左边TextView控件

    private LinearLayout mLlRight;//右边布局

    private ImageView mIvRight;//右边ImageView控件

    private TextView mTvRight;//右边TextView控件
    //===========================================控件end=============================================

    //*******************************************属性start*******************************************
    private String mTitle;//Title文字

    private int mTitleColor;//Title字体颜色

    private int mTitleSize;//Title字体大小

    private boolean mTitleVisibility;//Title是否显示

    private int mLeftIcon;//左边 ICON 引用的资源ID

    private int mRightIcon;//右边 ICON 引用的资源ID

    private boolean mLeftIconVisibility;//左边 ICON 是否显示

    private boolean mRightIconVisibility;//右边 ICON 是否显示

    private String mLeftText;//左边文字

    private int mLeftTextColor;//左边字体颜色

    private int mLeftTextSize;//左边字体大小

    private boolean mLeftTextVisibility;//左边文字是否显示

    private String mRightText;//右边文字

    private int mRightTextColor;//右边字体颜色

    private int mRightTextSize;//右边字体大小

    private boolean mRightTextVisibility;//右边文字是否显示
    //===========================================属性end=============================================

    public TitleView(Context context) {
        super(context);
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //导入布局
        initView(context, attrs);
    }

    private void initView(final Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.v_title_layout, this);

        mRootLayout = findViewById(R.id.root_layout);
        mTvTitle = findViewById(R.id.tv_v_title);
        mLlLeft = findViewById(R.id.title_left_ll);
        mIvLeft = findViewById(R.id.iv_left);
        mIvRight = findViewById(R.id.iv_right);
        mLlRight = findViewById(R.id.title_right_ll);
        mTvLeft = findViewById(R.id.tv_left);
        mTvRight = findViewById(R.id.tv_right);

        //获得这个控件对应的属性。
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TitleView);

        try {
            //获得属性值
            //getColor(R.styleable.RxTitle_RxBackground, getResources().getColor(R.color.transparent))
            mTitle = typedArray.getString(R.styleable.TitleView_title);//标题
            mTitleColor = typedArray.getColor(R.styleable.TitleView_titleColor, getResources().getColor(R.color.white));//标题颜色
            mTitleSize = typedArray.getDimensionPixelSize(R.styleable.TitleView_titleSize, ImageUtils.dpToPx(context, 18));//标题字体大小
            //TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics())
            mTitleVisibility = typedArray.getBoolean(R.styleable.TitleView_titleVisibility, true);

            mLeftIcon = typedArray.getResourceId(R.styleable.TitleView_leftIcon, R.drawable.ic_back);//左边图标
            mRightIcon = typedArray.getResourceId(R.styleable.TitleView_rightIcon, R.drawable.ic_set);//右边图标
            mLeftIconVisibility = typedArray.getBoolean(R.styleable.TitleView_leftIconVisibility, true);//左边图标是否显示
            mRightIconVisibility = typedArray.getBoolean(R.styleable.TitleView_rightIconVisibility, false);//右边图标是否显示

            mLeftText = typedArray.getString(R.styleable.TitleView_leftText);
            mLeftTextColor = typedArray.getColor(R.styleable.TitleView_leftTextColor, getResources().getColor(R.color.white));//左边字体颜色
            mLeftTextSize = typedArray.getDimensionPixelSize(R.styleable.TitleView_leftTextSize, ImageUtils.dpToPx(context, 8));//标题字体大小
            mLeftTextVisibility = typedArray.getBoolean(R.styleable.TitleView_leftTextVisibility, false);

            mRightText = typedArray.getString(R.styleable.TitleView_rightText);
            mRightTextColor = typedArray.getColor(R.styleable.TitleView_rightTextColor, getResources().getColor(R.color.white));//右边字体颜色
            mRightTextSize = typedArray.getDimensionPixelSize(R.styleable.TitleView_rightTextSize, ImageUtils.dpToPx(context, 8));//标题字体大小
            mRightTextVisibility = typedArray.getBoolean(R.styleable.TitleView_rightTextVisibility, false);

        } finally {
            //回收这个对象
            typedArray.recycle();
        }

        //*************以下属性初始化*****************************************************************************
        if (!DataUtils.isNullString(mTitle)) {
            setTitle(mTitle);
        }

        if (mTitleColor != 0) {
            setTitleColor(mTitleColor);
        }

        if (mTitleSize != 0) {
            setTitleSize(mTitleSize);
        }

        if (mLeftIcon != 0) {
            setLeftIcon(mLeftIcon);
        }

        if (mRightIcon != 0) {
            setRightIcon(mRightIcon);
        }

        setTitleVisibility(mTitleVisibility);

        setLeftText(mLeftText);

        setLeftTextColor(mLeftTextColor);

        setLeftTextSize(mLeftTextSize);

        setLeftTextVisibility(mLeftTextVisibility);

        setRightText(mRightText);

        setRightTextColor(mRightTextColor);

        setRightTextSize(mRightTextSize);

        setRightTextVisibility(mRightTextVisibility);

        setLeftIconVisibility(mLeftIconVisibility);

        setRightIconVisibility(mRightIconVisibility);

        initAutoFitEditText(context);
        //=========以上为属性初始化=================================================================================
    }

    private void initAutoFitEditText(Context context) {
        mTvTitle.clearFocus();
        mTvTitle.setEnabled(false);
        mTvTitle.setFocusableInTouchMode(false);
        mTvTitle.setFocusable(false);
        mTvTitle.setEnableSizeCache(false);
        //might cause crash on some devices
        mTvTitle.setMovementMethod(null);
        // can be added after layout inflation;
        mTvTitle.setMaxHeight(ImageUtils.dpToPx(context, 55f));
        //don't forget to add min text size programmatically
        mTvTitle.setMinTextSize(37f);
        try {
            TextAutoZoom.setNormalization((Activity) getContext(), mRootLayout, mTvTitle);
            KeyboardUtils.hideSoftInput((Activity) getContext());
        } catch (Exception e) {

        }
    }

    private int mLlLeft_width;
    private int mLlRight_width;

    /**
     * 自动缩进Title 的Padding属性
     */
    private void initAutoTitlePadding() {
        //获取显示View的宽高
        mLlLeft.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //只需要获取一次高度，获取后移除监听器
                        mLlLeft.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        //这里高度应该定义为成员变量，定义为局部为展示代码方便
                        mLlLeft_width = mLlLeft.getWidth();

                        mLlRight.getViewTreeObserver().addOnGlobalLayoutListener(
                                new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        //只需要获取一次高度，获取后移除监听器
                                        mLlRight.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                        //这里高度应该定义为成员变量，定义为局部为展示代码方便
                                        mLlRight_width = mLlRight.getWidth();
                                        mTvTitle.getViewTreeObserver().addOnGlobalLayoutListener(
                                                new ViewTreeObserver.OnGlobalLayoutListener() {
                                                    @Override
                                                    public void onGlobalLayout() {
                                                        //只需要获取一次高度，获取后移除监听器
                                                        mTvTitle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                                        int pd = (mLlLeft_width > mLlRight_width) ? mLlLeft_width : mLlRight_width;
                                                        mTvTitle.setPadding(pd,mTvTitle.getPaddingTop(),pd,mTvTitle.getPaddingBottom());
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }
    //*********以下为getView方法************

    public RelativeLayout getRootLayout() {
        return mRootLayout;
    }

    public TextAutoZoom getTvTitle() {
        return mTvTitle;
    }

    public LinearLayout getLlLeft() {
        return mLlLeft;
    }

    public ImageView getIvLeft() {
        return mIvLeft;
    }

    public TextView getTvLeft() {
        return mTvLeft;
    }

    public LinearLayout getLlRight() {
        return mLlRight;
    }

    public ImageView getIvRight() {
        return mIvRight;
    }

    public TextView getTvRight() {
        return mTvRight;
    }

    //*************以下为Title相关方法**********

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@StringRes int title) {
        setTitle(VnUtils.getContext().getString(title));
    }

    public void setTitle(String title) {
        mTitle = title;
        mTvTitle.setText(mTitle);
        initAutoTitlePadding();
    }

    public int getTitleColor() {
        return mTitleColor;
    }

    public void setTitleColor(int titleColor) {
        mTitleColor = titleColor;
        mTvTitle.setTextColor(mTitleColor);
    }

    public int getTitleSize() {
        return mTitleSize;
    }

    public void setTitleSize(int titleSize) {
        mTitleSize = titleSize;
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);
    }


    public boolean isTitleVisibility() {
        return mTitleVisibility;
    }

    public void setTitleVisibility(boolean titleVisibility) {
        mTitleVisibility = titleVisibility;
        if (mTitleVisibility) {
            mTvTitle.setVisibility(VISIBLE);
        } else {
            mTvTitle.setVisibility(GONE);
        }
    }

    //===========以上为  Title  相关方法===========

    //**************以下为  左边文字  相关方法************

    public String getLeftText() {
        return mLeftText;
    }

    public void setLeftText(@StringRes int title) {
        setLeftText(VnUtils.getContext().getString(title));
    }

    public void setLeftText(String leftText) {
        mLeftText = leftText;
        mTvLeft.setText(mLeftText);
        initAutoTitlePadding();
    }

    public int getLeftTextColor() {
        return mLeftTextColor;
    }

    public void setLeftTextColor(int leftTextColor) {
        mLeftTextColor = leftTextColor;
        mTvLeft.setTextColor(mLeftTextColor);
    }

    public int getLeftTextSize() {
        return mLeftTextSize;
    }

    public void setLeftTextSize(int leftTextSize) {
        mLeftTextSize = leftTextSize;
        mTvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLeftTextSize);
    }

    public boolean isLeftTextVisibility() {
        return mLeftTextVisibility;
    }

    public void setLeftTextVisibility(boolean leftTextVisibility) {
        mLeftTextVisibility = leftTextVisibility;
        if (mLeftTextVisibility) {
            mTvLeft.setVisibility(VISIBLE);
        } else {
            mTvLeft.setVisibility(GONE);
        }
    }

    public int getLeftIcon() {
        return mLeftIcon;
    }

    public void setLeftIcon(int leftIcon) {
        mLeftIcon = leftIcon;
        mIvLeft.setImageResource(mLeftIcon);

        initAutoTitlePadding();
    }

    public boolean isLeftIconVisibility() {
        return mLeftIconVisibility;
    }

    public void setLeftIconVisibility(boolean leftIconVisibility) {
        mLeftIconVisibility = leftIconVisibility;
        if (mLeftIconVisibility) {
            mIvLeft.setVisibility(VISIBLE);
        } else {
            mIvLeft.setVisibility(GONE);
        }
    }

    public void setLeftFinish(final Activity activity) {
        mLlLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    public void setLeftOnClickListener(OnClickListener onClickListener) {
        mLlLeft.setOnClickListener(onClickListener);
    }

    public void setLeftTextOnClickListener(OnClickListener onClickListener) {
        mTvLeft.setOnClickListener(onClickListener);
    }

    public void setLeftIconOnClickListener(OnClickListener onClickListener) {
        mIvLeft.setOnClickListener(onClickListener);
    }
    //=========以上为  左边文字  相关方法=========

    //**********以下为  右边文字  相关方法*****************
    public String getRightText() {
        return mRightText;
    }

    public void setRightText(@StringRes int title) {
        setRightText(VnUtils.getContext().getString(title));
    }

    public void setRightText(String rightText) {
        mRightText = rightText;
        mTvRight.setText(mRightText);
        initAutoTitlePadding();
    }

    public void setRightTextCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top,
                                              @Nullable Drawable right, @Nullable Drawable bottom) {

        if (left != null) {
            left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());//非常重要，必须设置，否则图片不会显示
        }
        if (top != null) {
            top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());//非常重要，必须设置，否则图片不会显示
        }
        if (right != null) {
            right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());//非常重要，必须设置，否则图片不会显示
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, bottom.getMinimumWidth(), bottom.getMinimumHeight());//非常重要，必须设置，否则图片不会显示
        }
        if (left != null || top != null || right != null || bottom != null) {
            mTvRight.setCompoundDrawables(left, top, right, bottom);
            mTvRight.setCompoundDrawablePadding(10);
            mTvRight.setPadding(left != null?20:0, top != null?20:0, /*right != null?*/20/*:0*/, bottom != null?20:0);
            initAutoTitlePadding();
        }
    }

    public int getRightTextColor() {
        return mRightTextColor;
    }

    public void setRightTextColor(int rightTextColor) {
        mRightTextColor = rightTextColor;
        mTvRight.setTextColor(mRightTextColor);
    }

    public int getRightTextSize() {
        return mRightTextSize;
    }

    public void setRightTextSize(int rightTextSize) {
        mRightTextSize = rightTextSize;
        mTvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRightTextSize);
    }

    public boolean isRightTextVisibility() {
        return mRightTextVisibility;
    }

    public void setRightTextVisibility(boolean rightTextVisibility) {
        mRightTextVisibility = rightTextVisibility;
        if (mRightTextVisibility) {
            mTvRight.setVisibility(VISIBLE);
            if (isRightIconVisibility()) {
                mTvRight.setPadding(0, 0, 0, 0);
            }
        } else {
            mTvRight.setVisibility(GONE);
        }
    }

    public int getRightIcon() {
        return mRightIcon;
    }

    public void setRightIcon(int rightIcon) {
        mRightIcon = rightIcon;
        mIvRight.setImageResource(mRightIcon);
        initAutoTitlePadding();
    }

    public boolean isRightIconVisibility() {
        return mRightIconVisibility;
    }

    public void setRightIconVisibility(boolean rightIconVisibility) {
        mRightIconVisibility = rightIconVisibility;
        if (mRightIconVisibility) {
            mIvRight.setVisibility(VISIBLE);
        } else {
            mIvRight.setVisibility(GONE);
        }
    }

    public void setRightOnClickListener(OnClickListener onClickListener) {
        mLlRight.setOnClickListener(onClickListener);
    }

    public void setRightTextOnClickListener(OnClickListener onClickListener) {
        mTvRight.setOnClickListener(onClickListener);
    }

    public void setRightIconOnClickListener(OnClickListener onClickListener) {
        mIvRight.setOnClickListener(onClickListener);
    }
    //=======以上为  右边文字  相关方法=========

}
