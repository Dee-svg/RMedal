package com.veni.tools.widget.verticalview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.veni.tools.R;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.ImageUtils;

/**
 * 作者：kkan on 2018/2/24 14:41
 * 当前类注释:
 * 多行单行文字上下滚动 子控件
 */
public class VerticalView extends FrameLayout {

    private RelativeLayout mLabel;//标签
    private TextView mLabelTv;//标签文字
    private TextView mContentTv;//右边文字

    private int labelImage;//标签背景
    private String labelStr;//标签文字
    private int labelColor;//标签文字颜色
    private int labelSize;//标签文字大小
    private boolean labelVisibility;//显示标签
    private int contentColor;//文字颜色
    private int contentSize;//文字大小

    public VerticalView(@NonNull Context context) {
        super(context);
        //导入布局
        init(context, null);
    }

    public VerticalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //导入布局
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.v_textvertical_layout, this);

        mLabel = findViewById(R.id.textvertical_label);
        mLabelTv = findViewById(R.id.textvertical_label_tv);
        mContentTv = findViewById(R.id.textvertical_content_tv);

        //获得这个控件对应的属性。
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextViewVertical);
        try {
            //获得属性值
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
        if (!DataUtils.isNullString(labelStr)) {
            setLabelText(labelStr);
        }
        if (labelImage != 0) {
            setLabelImage(labelImage);
        }
        if (labelColor != 0) {
            setLabelColor(labelColor);
        }
        if (labelSize != 0) {
            setLabelSize(labelSize);
        }
        if (contentColor != 0) {
            setContentColor(contentColor);
        }
        if (contentSize != 0) {
            setContentSize(contentSize);
        }
        setLabelVisibility(labelVisibility);


    }
/*
标签背景 可以放图片
 */
    public void setLabelImage(int labelImage) {
        this.labelImage = labelImage;
        mLabel.setBackgroundResource(labelImage);
    }

    public void setLabelText(String labelStr) {
        this.labelStr = labelStr;
        mLabelTv.setText(labelStr);
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        mLabelTv.setTextColor(labelColor);

    }

    public void setLabelSize(int labelSize) {
        this.labelSize = labelSize;
        mLabelTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, labelSize);
    }

    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
        mContentTv.setTextColor(contentColor);
    }

    public void setContentText(String contentStr) {
        mContentTv.setText(contentStr);
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
        mContentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentSize);

    }

    public void setLabelVisibility(boolean labelVisibility) {
        this.labelVisibility = labelVisibility;
        mLabel.setVisibility(labelVisibility ? VISIBLE : GONE);
    }
}
