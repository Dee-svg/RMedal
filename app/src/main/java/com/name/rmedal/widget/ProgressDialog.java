package com.name.rmedal.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.name.rmedal.R;

/**
 * 进度对话框
 */
public class ProgressDialog {
    private Context context;
    private Dialog progressDialog;
    private ProgressBar progressBar;
    private TextView progressTitle;
    private TextView progressDes;
    private TextView progressPrecent;
    private CharSequence title = "更新";
    private CharSequence des = "玩命儿更新中，请稍后...";
    private CharSequence precentdes = "0%";
    private int progress = 0;

    public ProgressDialog(Context context) {
        this.context = context;
        init();
    }

    public ProgressDialog(Context context, CharSequence title, CharSequence des, CharSequence precentdes, int progress) {
        this.context = context;
        this.title = title;
        this.des = des;
        this.precentdes = precentdes;
        this.progress = progress;
        init();
    }
    private void init(){
        progressDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog_Alert);
        View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        progressTitle = view.findViewById(R.id.dialog_progress_title);
        progressDes = view.findViewById(R.id.dialog_progress_des);
        progressBar = view.findViewById(R.id.dialog_progress_bar);
        progressPrecent = view.findViewById(R.id.dialog_progress_precent);
        progressDialog.setContentView(view);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressTitle.setText(title);
        progressDes.setText(des);
        progressBar.setProgress(progress);
        progressPrecent.setText(precentdes);
    }
    public ProgressDialog setTitle(final CharSequence title) {
        progressTitle.post(new Runnable() {
            @Override
            public void run() {
                progressTitle.setText(title);
            }
        });
        this.title = title;
        return this;
    }

    public ProgressDialog setDes(final CharSequence des) {
        progressDes.post(new Runnable() {
            @Override
            public void run() {
                progressDes.setText(des);
            }
        });
        this.des = des;
        return this;
    }

    public ProgressDialog setPrecentdes(final CharSequence precentdes) {
        progressPrecent.post(new Runnable() {
            @Override
            public void run() {
                progressPrecent.setText(precentdes);
            }
        });
        this.precentdes = precentdes;
        return this;
    }

    public ProgressDialog setProgress(final int progress) {
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressBar.setProgress(progress, true);
                } else {
                    progressBar.setProgress(progress);
                }
            }
        });
        this.progress = progress;
        return this;
    }

    public void show() {
        if(progressDialog!=null){
            progressDialog.show();
        }
    }
    public void dismiss() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

}
