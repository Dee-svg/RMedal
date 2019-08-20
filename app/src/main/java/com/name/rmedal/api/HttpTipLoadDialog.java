package com.name.rmedal.api;

import android.app.Activity;
import android.content.Context;

import com.name.rmedal.R;
import com.veni.tools.LogUtils;
import com.veni.tools.base.ui.AlertDialogBuilder;

/**
 * 作者：kkan on 2017/11/30
 * 当前类注释:
 * 网络请求的加载框
 */
public class HttpTipLoadDialog {
    private String TAG = "HttpTipLoadDialog";
    private volatile static HttpTipLoadDialog instance;
    private AlertDialogBuilder dialogBuilder = null;

    private Context context;

    /**
     * 单一实例
     */
    public static HttpTipLoadDialog getHttpTipLoadDialog() {
        if (instance == null) {
            synchronized (HttpTipLoadDialog.class) {
                if (instance == null) {
                    instance = new HttpTipLoadDialog();
                }
            }
        }
        return instance;
    }

    /**
     * showDialog & dismissDialog 在http 请求开始的时候显示，结束的时候消失
     * 当然不是必须需要显示的 !
     */
    public void showDialog(Context context, final String messageText) {
        this.context = context;
        if (context == null || !(context instanceof Activity) || ((Activity) context).isFinishing())
            return;
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                creatDialogBuilder().setDialog_message(messageText)
                        .setLoadingView(R.color.colorAccent)
                        .builder().show();
            }
        });
        LogUtils.wTag(TAG, "showDialog");
    }

    public void dismissDialog() {
        if (context == null || !(context instanceof Activity))
            return;             //maybe not good !
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                destroyDialogBuilder();
            }
        });
    }

    /**
     * 获取默认Dialog
     */
    private AlertDialogBuilder creatDialogBuilder() {
        destroyDialogBuilder();
        dialogBuilder = new AlertDialogBuilder(context);
        return dialogBuilder;
    }

    private void destroyDialogBuilder() {
        if (dialogBuilder != null) {
            dialogBuilder.dismissDialog();
            dialogBuilder = null;
        }
    }
}
