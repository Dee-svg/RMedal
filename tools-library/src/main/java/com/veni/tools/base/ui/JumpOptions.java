package com.veni.tools.base.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;

import com.veni.tools.R;

import java.io.Serializable;

/**
 * 作者：kkan on 2017/12/22
 * 当前类注释:
 * Activity跳转管理类
 */

public class JumpOptions {

    private int enterResId = R.anim.anim_right_in;//进入动画
    private int exitResId = R.anim.anim_left_out;//退出动画
    private Intent intent;
    private boolean finishFlag;
    private Bundle bundle;
    private ActivityOptionsCompat options = null;


    /**
     *  设置跳转参数
     */
    public JumpOptions setBundle(String key, Object value) {
        if (this.bundle == null) {
            this.bundle = new Bundle();
        }
        if (value == null) {
            return this;
        }
        if (value instanceof String && value.toString().length() > 0) {
            bundle.putString(key, (String) value);
        } else if (value instanceof Integer) {
            bundle.putInt(key, (Integer) value);
        } else if (value instanceof Double) {
            bundle.putDouble(key, (Double) value);
        } else if (value instanceof Boolean) {
            bundle.putBoolean(key, (Boolean) value);
        } else if (value instanceof Serializable) {
            bundle.putSerializable(key, (Serializable) value);
        } else {
            bundle.putString(key, value + "");
        }

        return this;
    }



    /**
     * 是否关闭act
     * @param finish ture 关闭
     */
    public JumpOptions setFinish(boolean finish) {
        this.finishFlag=finish;
        return this;
    }

    /**
     * 设置进入动画
     */
    public JumpOptions setEnterResId(int enterResId) {
        this.enterResId = enterResId;
        return this;
    }

    /**
     * 设置退出动画
     */
    public JumpOptions setExitResId(int exitResId) {
        this.exitResId = exitResId;
        return this;
    }

    public Intent getIntent(Context context, @NonNull Class<? extends Activity> activity) {
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bundle != null){
            intent.putExtras(bundle);
        }
        return intent;
    }

    public int getEnterResId() {
        return enterResId;
    }

    public int getExitResId() {
        return exitResId;
    }

    public boolean isFinishFlag() {
        return finishFlag;
    }

    /**
     * 启动activity
     *  @param activity 设置跳转目标Class
     */
    @Deprecated
    public void start(Context context, @NonNull Class<? extends Activity> activity, int requestCode) {
        intent = getIntent(context,activity);

        this.options = ActivityOptionsCompat.makeCustomAnimation(context,
                enterResId, exitResId);
        if (context == null || intent == null) {
            return;
        }
        ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, options.toBundle());

    }

    /**
     * 启动activity
     *  设置跳转目标Class
     */
    @Deprecated
    public void start(Context context, @NonNull Class<? extends Activity> activity) {
        intent = getIntent(context,activity);
        this.options = ActivityOptionsCompat.makeCustomAnimation(context,
                enterResId, exitResId);
        if (context == null || intent == null) {
            return;
        }
//        context.startActivity(intent,options.toBundle());
        ActivityCompat.startActivity(context, intent, options.toBundle());
        if (finishFlag) {
            ((Activity) context).finish();
        }
    }
}
