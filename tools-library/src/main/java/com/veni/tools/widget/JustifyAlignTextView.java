package com.veni.tools.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.veni.tools.R;
import com.veni.tools.util.DataUtils;
import com.veni.tools.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Here be dragons
 * 文字两端对齐
 * <com.veni.tools.widget.JustifyAlignTextView
 * android:layout_width="wrap_content"
 * android:layout_height="@dimen/hight_40"
 * android:paddingLeft="@dimen/hight_5"
 * android:paddingRight="@dimen/hight_10"
 * app:alinetitlecolor="@color/light_black"
 * app:isindent="true"
 * app:rowmax="6"
 * app:columnmax="1"
 * app:isindent_first="true"
 * app:alinetitlesize="@dimen/font_14"
 * app:alinetitle="@string/task_order_sign_people" />
 */
public class JustifyAlignTextView extends FrameLayout {
    private String TAG = JustifyAlignTextView.class.getSimpleName();
    private Context context;
    private TextView justifiedContentTv;//TextView控件

    private String contentString;//文本
    private int mTextColor;//文本颜色
    private int mTextSize;//文本大小
    private int rowmax;//文本长度
    private int columnmax;//最大行数
    private int endaline;//占位符添加的结束位置 size()-endaline 不参加缩进字符大小  如 ： = 2字符
    private boolean isindent;//文本两端缩进
    private boolean isindent_first;//首行两端缩进

    public JustifyAlignTextView(Context context) {
        super(context);
        initView(context, null);
    }

    public JustifyAlignTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public JustifyAlignTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(final Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.v_layout_justifyaline, this);
        justifiedContentTv = findViewById(R.id.justifyaline_content_tv);
        //获得这个控件对应的属性。
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.JustifyAlignTextView);

        try {
            //获得属性值
            contentString = typedArray.getString(R.styleable.JustifyAlignTextView_alinetitle);//文本
            mTextColor = typedArray.getColor(R.styleable.JustifyAlignTextView_alinetitlecolor, Color.parseColor("#070707"));//文本颜色
            mTextSize = typedArray.getDimensionPixelSize(R.styleable.JustifyAlignTextView_alinetitlesize, ImageUtils.dpToPx(context, 14));//文本大小
            rowmax = typedArray.getInt(R.styleable.JustifyAlignTextView_rowmax, -1);//文本大小
            columnmax = typedArray.getInt(R.styleable.JustifyAlignTextView_columnmax, -1);//最大行数
            endaline = typedArray.getInt(R.styleable.JustifyAlignTextView_endaline, 0);//占位符添加的结束位置
            isindent = typedArray.getBoolean(R.styleable.JustifyAlignTextView_isindent, false);//文本两端缩进
            isindent_first = typedArray.getBoolean(R.styleable.JustifyAlignTextView_isindent_first, false);//首行两端缩进
        } finally {
            //回收这个对象
            typedArray.recycle();
        }

        if (!DataUtils.isEmpty(contentString)) {
            setTitle(contentString);
        }
        if (mTextColor != 0) {
            setTitleColor(mTextColor);
        }

        if (mTextSize != 0) {
            setTitleSize(mTextSize);
        }
    }

    public CharSequence getContentString() {
        return contentString;
    }

    public void setTitle(@StringRes int title) {
        setTitle(context.getString(title));
    }

    public void setTitle(String title) {
        contentString = title;
        justifyAlign(contentString);
    }

    private void justifyAlign(String title) {
        //columnmax == -1 ||
        if (rowmax == -1 || DataUtils.isEmpty(title)) {
            justifiedContentTv.setText(title);
            return;
        }
        int textlength = title.length();
        int maxline = (textlength / rowmax) + ((textlength % rowmax) > 0 ? 1 : 0);
        if (columnmax > 0 && maxline > columnmax) {
            maxline = columnmax;
        }
        List<List<TextData>> maxlineTextlist = new ArrayList<>();
        //字符分段
        for (int i = 0; i < maxline; i++) {
            String substr;
            if (i < maxline - 1) {
                substr = title.substring((i * rowmax), ((i + 1) * rowmax));
            } else {
                substr = title.substring((i * rowmax));
            }
            char[] linestrchar = substr.toCharArray();
            //字符长度
            int charlength = linestrchar.length;
            //计算字符差值
            int dvalue = rowmax - charlength;
            //每个字符填充的占位符平均数量
            int placeholders = 0;
            // 剩余占位符数量
            int syplaceholder = 0;
            // 剩余占位符数量分配起始位置
            int syfullcharstart = 0;
            // 剩余占位符数量分配结束位置
            int syfullcharend = 0;
            //计算填充占位符&#160; 数量
            int fullcharsize = 0;
            if (dvalue > 0) {
                //计算填充占位符&#160; 数量 1个汉字 = 4个&#160;
                fullcharsize = dvalue * 4;
                placeholders = fullcharsize;
                syplaceholder = fullcharsize;
                if (endaline <= 0) {
                    endaline = 1;
                }
                if (charlength > endaline) {
                    //平均占位
                    placeholders = (fullcharsize / (charlength - endaline));
                    //剩余占位
                    syplaceholder = (fullcharsize % (charlength - endaline));
                    //占位起始位置
                    if ((charlength - endaline - syplaceholder) > 0) {
                        syfullcharstart = (charlength - endaline - syplaceholder) / 2;
                    }
                    //占位结束位置
                    syfullcharend = syfullcharstart + syplaceholder;
                }
            }
            List<TextData> textlist = new ArrayList<>();
            //原始字符赋值
            for (int j = 0; j < linestrchar.length; j++) {
                char sc = linestrchar[j];
                TextData textData = new TextData();
                textData.setDataStr(String.valueOf(sc));
                int insertnum = 0;
                //不是最后一个字符 并且开启两端对齐
                if (j < linestrchar.length - endaline && (isindent || maxline == 1)) {
                    insertnum = placeholders;
                    //分配剩余占位符
                    if (syplaceholder > 0 && j >= syfullcharstart && j < syfullcharend) {
                        insertnum = placeholders + 1;
                    }
                }
                textData.setDatainsertnum(insertnum);
                textlist.add(textData);
            }
            maxlineTextlist.add(textlist);
        }
//        LogUtils.e("maxlineTextlist=="+ JsonUtils.toJson(maxlineTextlist));
        StringBuilder stringBuilder = new StringBuilder();
        //创建两端缩进字符
        for (int i = 0; i < maxlineTextlist.size(); i++) {
            List<TextData> textlist = maxlineTextlist.get(i);
            int allindentnum = 0;
            for (int j = 0; j < textlist.size(); j++) {
                TextData textData = textlist.get(j);
//            for (TextData textData : textlist) {
                //原始字符填充
                String sequ = textData.getDataStr();
                stringBuilder.append(sequ);
                //填充原始字符 所需占位符数量
                int fillsize =0;
                if((i == 0)){
                    if(isindent_first){
                        fillsize =  getCharTypeFill(sequ);
                    }
                }else {
                    fillsize =  getCharTypeFill(sequ);
                }
                //单个文字字符填充数量
                int insertnum = textData.getDatainsertnum();
                //非文本两端缩进 首行缩进
                if (!isindent && !(i == 0 && isindent_first)) {
                    allindentnum = allindentnum + fillsize;
                    fillsize = 0;
                    allindentnum = allindentnum + insertnum;
                    insertnum = 0;
                }

                if (j >= textlist.size() - endaline && fillsize != 0) {
                    fillsize = 0;
                }

                insertnum = fillsize + insertnum;
                //填充占位符
                stringBuilder.append(getPlaceHolder(insertnum));
            }

            //字符换行
            if (i != maxlineTextlist.size() - 1) {
                //非文本两端缩进 首行缩进 占位符填充在末尾
                if (!isindent && !(i == 0 && isindent_first)) {
                    stringBuilder.append(getPlaceHolder(allindentnum));
                }
                stringBuilder.append("<br/>");
            }else if(maxline==1){
                stringBuilder.append(getPlaceHolder(allindentnum));
            }
        }

        justifiedContentTv.setText(Html.fromHtml(stringBuilder.toString()));

    }

    private StringBuilder getPlaceHolder(int insertnum) {
        StringBuilder stringBuffer = new StringBuilder();
        int num = insertnum / 4;
        int synum4 = insertnum % 4;
        int synum2 = synum4 / 2;
        synum4 = synum4 % 2;
        //1个汉字 = 4个&#160; = 4个&#8197; = 1个&#12288; = 2个&#8194;
        //填充占位符 &#12288;
        for (int k = 0; k < num; k++) {
            String defplaceholder = "&#12288;";
            stringBuffer.append(defplaceholder);
        }
        //填充占位符 &#8194;
        for (int k = 0; k < synum2; k++) {
            String defplaceholder = "&#8194;";
            stringBuffer.append(defplaceholder);
        }
        //填充占位符  &#160;
        for (int k = 0; k < synum4; k++) {
            String defplaceholder = "&#160;";
            stringBuffer.append(defplaceholder);
        }
        return stringBuffer;
    }

    private int getCharTypeFill(String value) {
        //计算填充占位符&#160; 数量
        int char_fill_size = 0;
        if (DataUtils.isChPunctuation(value)) {
            //判断是否为中文标点 填充1个占位符
            char_fill_size = 1;
        } else if (DataUtils.isPunctuation(value)) {
            //判断是否为英文标点 填充3个占位符
            char_fill_size = 3;
        } else if (DataUtils.isCapitalsEn(value)) {
            //判断大写英文 填充2个占位符
            char_fill_size = 1;
        } else if (DataUtils.isLowerEn(value)) {
            //判断小写英文 填充2个占位符
            char_fill_size = 2;
        }
        return char_fill_size;
    }

    private int getCharFill(String value) {
        //计算填充占位符&#160; 数量
        int char_fill_size = 0;
        if (DataUtils.isChPunctuation(value)) {
            //判断是否为中文标点 =3个占位符
            char_fill_size = 3;
        } else if (DataUtils.isChz(value)) {
            //判断是否为中文 =4个占位符
            char_fill_size = 4;
        } else if (DataUtils.isPunctuation(value)) {
            //判断是否为英文标点 =1个占位符
            char_fill_size = 1;
        } else if (DataUtils.isCapitalsEn(value)) {
            //判断大写英文 =3个占位符
            char_fill_size = 3;
        } else if (DataUtils.isLowerEn(value)) {
            //判断小写英文 =2个占位符
            char_fill_size = 2;
        }
        return char_fill_size;
    }

    private class TextData {
        int datainsertnum;
        int linefillnum;
        String dataStr;

        public int getDatainsertnum() {
            return datainsertnum;
        }

        public void setDatainsertnum(int datainsertnum) {
            this.datainsertnum = datainsertnum;
        }

        public String getDataStr() {
            return dataStr;
        }

        public void setDataStr(String dataStr) {
            this.dataStr = dataStr;
        }

        public int getLinefillnum() {
            return linefillnum;
        }

        public void setLinefillnum(int linefillnum) {
            this.linefillnum = linefillnum;
        }
    }

    public int getTitleColor() {
        return mTextColor;
    }

    public void setTitleColor(int titleColor) {
        mTextColor = titleColor;
        justifiedContentTv.setTextColor(mTextColor);
    }

    public int getTitleSize() {
        return mTextSize;
    }

    public void setTitleSize(int titleSize) {
        mTextSize = titleSize;
        justifiedContentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
    }
}
