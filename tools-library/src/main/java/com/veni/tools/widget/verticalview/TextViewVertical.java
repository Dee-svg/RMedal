package com.veni.tools.widget.verticalview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.veni.tools.R;
import com.veni.tools.listener.OnNoFastClickListener;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.ImageUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 作者：kkan on 2018/2/24 14:41
 * 当前类注释:
 * 多行单行文字上下滚动
 */
public class TextViewVertical extends ViewFlipper {

    /**
     * 适配器，在 fillVerticalItem 方法中填充数据，加载标签背景图片等
     * item 点击事件监听器
     * 需配合{@link #setData(List,Adapter)}使用
     *
     * @param <T> item 数据模型
     */
    public interface Adapter<T> {
        void fillVerticalItem(VerticalView itemView, @Nullable T model, int position);
        void onVerticalItemClick(VerticalView itemView, @Nullable T model, int position);
    }

    /**
     * ViewHolder，在 fillVerticalItem 方法中填充数据，加载标签背景图片等
     * 使用 ViewHolder 后 Adapter OnVerticalItemClick及在xml中设置的属性
     * 除isSetAnimDuration及interval 外 失效
     * 需配合{@link #setData(int, List,ViewHolder)}
     * @param <T> item 数据模型
     */
    public interface ViewHolder<T> {
        void onHolder(SimpleViewHolder holder, @Nullable T model, int position);
    }

    private Context context;

    private int labelImage;//标签背景
    private String labelStr;//标签文字
    private int labelColor;//标签文字颜色
    private int labelSize;//标签文字大小
    private boolean labelVisibility;//显示标签
    private int contentColor;//文字颜色
    private int contentSize;//文字大小
    private int maxshow;//最大展示数
    private boolean isSetAnimDuration = false;
    private int interval = 5000;//设置停留时长间隔
    private int animDuration = 500;//设置进入和退出的时间间隔

    public TextViewVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        //获得这个控件对应的属性。
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextViewVertical);
        try {
            //获得属性值
            isSetAnimDuration = typedArray.getBoolean(R.styleable.TextViewVertical_setAnimDuration, false);//开启进入和退出
            interval = typedArray.getInteger(R.styleable.TextViewVertical_interval, 5000);//设置停留时长间隔
            maxshow = typedArray.getInteger(R.styleable.TextViewVertical_maxshow, 1);//最大展示数
            labelImage = typedArray.getResourceId(R.styleable.TextViewVertical_labelImage, R.drawable.corners2_stroke1_red);//标签背景
            labelStr = typedArray.getString(R.styleable.TextViewVertical_labelStr);//标签文字
            labelColor = typedArray.getColor(R.styleable.TextViewVertical_labelColor, getResources().getColor(R.color.red));//标签文字颜色
            labelSize = typedArray.getDimensionPixelSize(R.styleable.TextViewVertical_labelSize, ImageUtils.dpToPx(context, 9));//标签文字大小
            labelVisibility = typedArray.getBoolean(R.styleable.TextViewVertical_labelVisibility, true);//显示标签
            contentColor = typedArray.getColor(R.styleable.TextViewVertical_contentColor, getResources().getColor(R.color.red));//文字颜色
            contentSize = typedArray.getDimensionPixelSize(R.styleable.TextViewVertical_contentSize, ImageUtils.dpToPx(context, 14));//文字大小

        } finally {
            //回收这个对象
            typedArray.recycle();
        }
        if (interval < 2000) {
            interval = 2000;
        }
        animDuration = interval / 10;//设置进入和退出的时间间隔
        setFlipInterval(interval);
        Animation animIn = AnimationUtils.loadAnimation(context, R.anim.anim_marquee_in);
        Animation animOut = AnimationUtils.loadAnimation(context, R.anim.anim_marquee_out);
        if (isSetAnimDuration) {
            animIn.setDuration(animDuration);
            animOut.setDuration(animDuration);
        }
        setInAnimation(animIn);
        setOutAnimation(animOut);
    }

    /**
     * 设置数据模型和文案，布局资源默认为 VerticalView
     *
     * @param adapter 适配器
     * @param models 每一页的数据模型集合
     */
    public void setData(final List<?> models, Adapter adapter) {
        setData(0,models,null,adapter);
    }
    /**
     * 设置数据模型和文案，布局资源默认为 VerticalView
     *
     * @param models 每一页的数据模型集合
     */
    public void setData(@LayoutRes int layoutResId , final List<?> models, ViewHolder mViewHolder) {
        setData(layoutResId,models,mViewHolder,null);
    }
    /**
     * 设置数据模型和文案
     * @param layoutResId 布局资源id
     * @param models 每一页的数据模型集合
     */
    private void setData(@LayoutRes int layoutResId, final List<?> models, ViewHolder mViewHolder, Adapter adapter) {
        List<View> views = new ArrayList<>();
        int maxsize = (models.size() / maxshow) + ((models.size() % maxshow) > 0 ? 1 : 0);

        LinearLayout.LayoutParams parentparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //子控件的LayoutParams
        // 宽度为0,高度为WRAP_CONTENT,权重为1,权重也可以不指定
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        //设置控件的显示位置,相当于控件的layout_gravity属性
        params.gravity = Gravity.CENTER_VERTICAL;

        for (int i = 0; i < maxsize; i++) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(parentparams);

            for (int j = 0; j < maxshow; j++) {
                final int currentPosition = (maxshow * i) + j;
                if (currentPosition < models.size()) {
                    View view;
                    if (layoutResId == 0) {
                        view = new VerticalView(context);
                    } else {
                        view = LayoutInflater.from(context).inflate(layoutResId, null);
                    }
                    linearLayout.addView(setViewData(view, currentPosition, models,mViewHolder,adapter), params);
                }
            }
            views.add(linearLayout);
        }
        setViews(views);
    }


    private View setViewData(View view, final int currentPosition, final List<?> models, ViewHolder mViewHolder, final Adapter mAdapter) {
        if (view instanceof VerticalView) {
            view = getDetVerticalView(new VerticalView(context));
            if (mAdapter != null) {
                view.setOnClickListener(new OnNoFastClickListener() {
                    @Override
                    protected void onNoDoubleClick(View view) {
                        if (isIndexNotOutOfBounds(currentPosition, models)) {
                            mAdapter.onVerticalItemClick((VerticalView) view, models.get(currentPosition), currentPosition);
                        } else if (isCollectionEmpty(models)) {
                            mAdapter.onVerticalItemClick((VerticalView) view, null, currentPosition);
                        }
                    }
                });
                if (isIndexNotOutOfBounds(currentPosition, models)) {
                    mAdapter.fillVerticalItem((VerticalView) view, models.get(currentPosition), currentPosition);
                } else if (isCollectionEmpty(models)) {
                    mAdapter.fillVerticalItem((VerticalView) view, null, currentPosition);
                }
            }
        } else {
            SimpleViewHolder viewHolder = new SimpleViewHolder(view);
            if (mViewHolder != null) {
                if (isIndexNotOutOfBounds(currentPosition, models)) {
                    mViewHolder.onHolder(viewHolder, models.get(currentPosition), currentPosition);
                } else if (isCollectionEmpty(models)) {
                    mViewHolder.onHolder(viewHolder, null, currentPosition);
                }
            }
        }
        return view;
    }

    private VerticalView getDetVerticalView(VerticalView verticalView) {
        if (!DataUtils.isNullString(labelStr)) {
            verticalView.setLabelText(labelStr);
        }
        if (labelImage != 0) {
            verticalView.setLabelImage(labelImage);
        }
        if (labelColor != 0) {
            verticalView.setLabelColor(labelColor);
        }
        if (labelSize != 0) {
            verticalView.setLabelSize(labelSize);
        }
        if (contentColor != 0) {
            verticalView.setContentColor(contentColor);
        }
        if (contentSize != 0) {
            verticalView.setContentSize(contentSize);
        }
        verticalView.setLabelVisibility(labelVisibility);
        return verticalView;
    }

    /**
     * 设置循环滚动的View数组
     *
     * @param views View数组
     */
    private void setViews(List<View> views) {
        if (views == null || views.size() == 0) return;
        removeAllViews();
        for (int i = 0; i < views.size(); i++) {
            addView(views.get(i));
        }
        startFlipping();
    }

    public int getLabelImage() {
        return labelImage;
    }

    public String getLabelStr() {
        return labelStr;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public int getLabelSize() {
        return labelSize;
    }

    public boolean isLabelVisibility() {
        return labelVisibility;
    }

    public int getContentColor() {
        return contentColor;
    }

    public int getContentSize() {
        return contentSize;
    }

    public static boolean isIndexNotOutOfBounds(int position, Collection collection) {
        return isCollectionNotEmpty(collection) && position < collection.size();
    }

    public static boolean isCollectionNotEmpty(Collection collection, Collection... args) {
        return !isCollectionEmpty(collection, args);
    }

    public static boolean isCollectionEmpty(Collection collection, Collection... args) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        for (Collection arg : args) {
            if (arg == null || arg.isEmpty()) {
                return true;
            }
        }
        return false;
    }

}