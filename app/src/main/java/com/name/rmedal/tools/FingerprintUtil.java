package com.name.rmedal.tools;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.name.rmedal.R;
import com.veni.tools.listener.OnNoFastClickListener;
import com.veni.tools.util.DeviceUtils;

public class FingerprintUtil {
    private static CancellationSignal cancellationSignal;
    private static android.os.CancellationSignal os_cancellationSignal;

    public static boolean getFingerprintIsok(Context context) {
       return getFingerprintIsok(context,null);
    }
    private static boolean getFingerprintIsok(Context context, @Nullable OnCallBackListenr listener) {
        if (Build.VERSION.SDK_INT < 23) {
            if (listener != null) {
                listener.onSupportFailed();
            }
            return false;
        }
        KeyguardManager keyguardManager = context.getSystemService(KeyguardManager.class);
        FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(context);
        if (!managerCompat.isHardwareDetected()) {////判断设备是否支持
            if (listener != null) {
                listener.onSupportFailed();
            }
            return false;
        }
        if (!keyguardManager.isKeyguardSecure()) {//判断设备是否处于安全保护中 未设置锁屏
            if (listener != null) {
                listener.onInsecurity();
            }
            return false;
        }
        if (!managerCompat.hasEnrolledFingerprints()) { //判断设备是否已经注册过指纹
            if (listener != null) {
                listener.onEnrollFailed();//未注册
            }
            return false;
        }
        return true;
    }

    public static void callFingerPrint(Context context, @NonNull OnCallBackListenr listener) {
        FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(context);
        if (!getFingerprintIsok(context, listener)) {
            return;
        }
//        listener.onAuthenticationStart(); //开始指纹识别
        showDeleteDialog(context, listener);
        init23Api(managerCompat, listener);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//        showDeleteDialog(context);
        //init23Api(fingerprintManager, listener);
//        } else {
//            init28Api(context, listener);
//        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private static void init23Api(FingerprintManagerCompat managerCompat, final OnCallBackListenr listener) {
        cancellationSignal = new CancellationSignal(); //必须重新实例化，否则cancel 过一次就不能再使用了
        managerCompat.authenticate(null, 0, cancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
            // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息，比如华为的提示就是：尝试次数过多，请稍后再试。
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                if (plice_room_dialog != null && errorMsg != null && plice_room_dialog.isShowing()) {
                    errorMsg.setText(errString);
                }
//                listener.onAuthenticationError(errMsgId, errString);
            }

            // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
            @Override
            public void onAuthenticationFailed() {
                if (plice_room_dialog != null && errorMsg != null && plice_room_dialog.isShowing()) {
                    errorMsg.setText("指纹认证失败，请再试一次");
                }
//                listener.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                if (plice_room_dialog != null && errorMsg != null && plice_room_dialog.isShowing()) {
                    errorMsg.setText(helpString);
                }
//                listener.onAuthenticationHelp(helpMsgId, helpString);
            }

            // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
//                result.getCryptoObject().getSignature()
                if (plice_room_dialog != null) {
                    plice_room_dialog.dismiss();
                }
                listener.onAuthenticationSucceeded();
            }
        }, null);
    }

    @TargetApi(Build.VERSION_CODES.P)
    private static void init28Api(Context context,final OnCallBackListenr listener) {

        BiometricPrompt mBiometricPrompt;
        BiometricPrompt.AuthenticationCallback mAuthenticationCallback;
        mBiometricPrompt = new BiometricPrompt.Builder(context)
                .setTitle("指纹验证")
                .setDescription("")
                .setNegativeButton("取消", context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //handle cancel result
//                        listener.onAuthenticationCancel();
                    }
                }).build();

        os_cancellationSignal = new android.os.CancellationSignal();
        os_cancellationSignal.setOnCancelListener(new android.os.CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                //handle cancel result
//                listener.onAuthenticationCancel();
            }
        });
        mAuthenticationCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
//                listener.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                listener.onAuthenticationSucceeded();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
//                listener.onAuthenticationFailed();
            }
        };
        mBiometricPrompt.authenticate(os_cancellationSignal, context.getMainExecutor(), mAuthenticationCallback);
    }

    public interface OnCallBackListenr {
        void onSupportFailed();

        void onInsecurity();

        void onEnrollFailed();

//        void onAuthenticationStart();

        void onAuthenticationCancel();

//        void onAuthenticationError(int errMsgId, CharSequence errString);

//        void onAuthenticationFailed();

//        void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

        void onAuthenticationSucceeded();
    }

    public static void cancel() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
        if (os_cancellationSignal != null) {
            os_cancellationSignal.cancel();
        }
        if (plice_room_dialog != null) {
            plice_room_dialog.dismiss();
        }
    }

    private static TextView errorMsg;

    private static void showDeleteDialog(Context context,final OnCallBackListenr listener) {
        View mDialogView = View.inflate(context, R.layout.fingerprint_dialog, null);
        errorMsg = mDialogView.findViewById(R.id.error_msg);
        mDialogView.findViewById(R.id.cancel).setOnClickListener(new OnNoFastClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                listener.onAuthenticationCancel();
                cancel();
                plice_room_dialog.dismiss();
            }
        });
        getDialog(context, mDialogView, listener);
    }

    private static Dialog plice_room_dialog;

    private static void getDialog(Context context, View mDialogView,final OnCallBackListenr listener) {
        if (plice_room_dialog != null) {
            plice_room_dialog.dismiss();
        }
        plice_room_dialog = new Dialog(context, R.style.NormalDialogStyle);
        plice_room_dialog.setContentView(mDialogView);
        plice_room_dialog.setCancelable(true);
        plice_room_dialog.setCanceledOnTouchOutside(false);
        plice_room_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.onAuthenticationCancel();
                cancel();
            }
        });

        Window dialogWindow = plice_room_dialog.getWindow();
        if (dialogWindow != null) {

            int width = DeviceUtils.getScreenWidth(context);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (width * 0.80);
//            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;

            dialogWindow.setAttributes(lp);
            dialogWindow.setGravity(Gravity.CENTER);
        }
        plice_room_dialog.show();
    }
}
