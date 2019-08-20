package com.veni.tools.util;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * 作者：kkan on 2017/12/19
 * 当前类注释:
 * 验证码倒计时
 */

public class CaptchaTime extends CountDownTimer {
    private TextView codeTv;
    private boolean countisfinish = true;
    private String onFinish_str;
    private String onTick_str;

    public CaptchaTime(TextView codeTv, long second) {
        super(second * TimeUtils.SEC, TimeUtils.SEC);
        this.codeTv = codeTv;
        this.onFinish_str = "重新获取";
        this.onTick_str = "s后重发";
        start();
    }
    /**
     * @param codeTv 验证码 TextView
     * @param second 倒计时 秒
     * @param onTick_str 倒计时 秒
     * @param onFinish_str 倒计时 秒
     */
    public CaptchaTime(TextView codeTv, long second, String onTick_str, String onFinish_str) {
        super(second * TimeUtils.SEC, TimeUtils.SEC);
        this.codeTv = codeTv;
        this.onFinish_str = onFinish_str;
        this.onTick_str = onTick_str;
        start();
    }

    public boolean isfinish() {
        return countisfinish;
    }

    @Override
    public void onFinish() {
        codeTv.setEnabled(true);
        codeTv.setText(onFinish_str);
        countisfinish = true;

    }

    @Override
    public void onTick(long millisUntilFinished) {
        codeTv.setEnabled(false);
        countisfinish = false;
        codeTv.setText(millisUntilFinished / 1000 + onTick_str);
    }
}
