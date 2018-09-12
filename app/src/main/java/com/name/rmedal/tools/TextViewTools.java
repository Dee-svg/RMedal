package com.name.rmedal.tools;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.widget.TextView;

/**
 * 作者：kkan on 2017/12/18
 * 当前类注释:
 * TextView 工具类
 * 经常遇到一些信息的展示,类似这样而且左右颜色不一致的
 * 订单号:18863256211
 * 手机号码:18863256211
 * 姓名:18863256211
 * TextViewTools.upitemtvforhtml_onetv(tv,"订单号","18863256211","#b2b2b2", "#4a4a4a");
 */

public class TextViewTools {

    public static TextView upitemtvforhtml_onetv(TextView tvcontent, String tip, String content) {
        return upitemtvforhtml_onetv(tvcontent, tip, content, "#b2b2b2", "#4a4a4a");
    }

    public static TextView upitemtvforhtml_onetv(TextView tvcontent, String tip, String content, String tipcolor, String contentcolor) {
        tvcontent.setText(Html.fromHtml("<font color='" + tipcolor + "'>" + tip + "</font>&nbsp;" +
                "<font color='" + contentcolor + "'>" + content + "</font>"));
        return tvcontent;
    }

    public static void setCompoundDrawable(TextView compoundTv, int leftdrawableid, int topdrawableid,
                                           int rightdrawableid, int bottomdrawableid) {
        Context context=compoundTv.getContext();
        //left top right bottom
        compoundTv.setCompoundDrawables(getdrawersetbounds(context,leftdrawableid), getdrawersetbounds(context,topdrawableid)
                , getdrawersetbounds(context,rightdrawableid), getdrawersetbounds(context,bottomdrawableid));
    }

    public static void setLeftCompoundDrawable(TextView compoundTv, int leftdrawableid, @Nullable Drawable topdrawableid,
                                               @Nullable Drawable rightdrawableid, @Nullable Drawable bottomdrawableid) {
        Context context=compoundTv.getContext();
        //left top right bottom
        compoundTv.setCompoundDrawables(getdrawersetbounds(context,leftdrawableid), getdrawersetbounds(topdrawableid)
                , getdrawersetbounds(rightdrawableid), getdrawersetbounds(bottomdrawableid));
    }

    public static void setCompoundDrawables(TextView compoundTv, @Nullable Drawable left, @Nullable Drawable top,
                                            @Nullable Drawable right, @Nullable Drawable bottom) {
        //left top right bottom
        compoundTv.setCompoundDrawables(getdrawersetbounds(left), getdrawersetbounds(top)
                , getdrawersetbounds(right), getdrawersetbounds(bottom));
    }

    private static Drawable getdrawersetbounds(Context context,int id) {
        if(id==0){
            return null;
        }
        Drawable drawable = ContextCompat.getDrawable(context, id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//非常重要，必须设置，否则图片不会显示
        return drawable;
    }

    private static Drawable getdrawersetbounds(Drawable drawable) {
        if(drawable==null){
            return null;
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//非常重要，必须设置，否则图片不会显示
        return drawable;
    }
}
