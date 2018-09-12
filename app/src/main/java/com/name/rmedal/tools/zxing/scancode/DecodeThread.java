package com.name.rmedal.tools.zxing.scancode;

import android.os.Handler;
import android.os.Looper;


import com.name.rmedal.ui.zxing.ScanerCodeActivity;

import java.util.concurrent.CountDownLatch;

/**
 * 描述: 解码线程
 */
final class DecodeThread extends Thread {

    private final CountDownLatch handlerInitLatch;
    ScanerCodeActivity activity;
    private Handler handler;

    DecodeThread(ScanerCodeActivity activity) {
        this.activity = activity;
        handlerInitLatch = new CountDownLatch(1);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(activity);
        handlerInitLatch.countDown();
        Looper.loop();
    }

}
