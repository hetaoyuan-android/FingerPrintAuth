package com.feelschaotic;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.feelschaotic.fingerprintauth.R;

public class MainActivity extends Activity {

    private static final int FingerPrintRetryMax = 5;
    private int fingerPrintRetryNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startFingerPrint();
        //android p build的编译版本需要到29
//        showBiometricPromptDialog(this.getApplicationContext());



    }

    private void startFingerPrint() {
        FingerPrintUtils.init(this, new FingerPrintUtils.FingerPrintResult() {
            @Override
            public void success() {
                //ToastUtils.showToast(LoginFingerActivity.this, "识别成功，关闭指纹识别功能");
                fingerPrintRetryNum = 0;
                FingerPrintUtils.cancelCallback();
                Toast.makeText(MainActivity.this, "识别成功", Toast.LENGTH_SHORT).show();

//                onAuthenticated();
            }

            @Override
            public void error(int code, CharSequence info) {
                //ToastUtils.showToast(LoginFingerActivity.this, "识别失败，关闭指纹识别功能\n"+"code=" + code + ",error info=" + info);
                Toast.makeText(MainActivity.this, "识别失败，请使用其他方式登录。", Toast.LENGTH_SHORT).show();
                fingerPrintRetryNum = 0;
                FingerPrintUtils.cancelCallback();
            }

            @Override
            public void retry(int code, CharSequence info) {
                fingerPrintRetryNum = fingerPrintRetryNum + 1;
                Toast.makeText(MainActivity.this, "识别失败,请重试。", Toast.LENGTH_SHORT).show();
                if(fingerPrintRetryNum > FingerPrintRetryMax) {
                    fingerPrintRetryNum = 0;
                    FingerPrintUtils.cancelCallback();
                }
            }
        });
    }

    /*
    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.Q)
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void showBiometricPromptDialog(Context context) {
        final KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(
                Context.KEYGUARD_SERVICE);

        if (keyguardManager.isKeyguardSecure()) {
            final BiometricPrompt.AuthenticationCallback authenticationCallback =
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(
                                BiometricPrompt.AuthenticationResult result) {
                            //successRunnable.run();
                            Log.d("TAG", "onAuthenticationSucceeded: ");
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            //Do nothing
                            Log.d("TAG", "onAuthenticationError: " + errorCode + ", str: " + errString);
                        }
                    };


            final Handler handler = new Handler(Looper.getMainLooper());

            final BiometricPrompt.Builder builder = new BiometricPrompt.Builder(context)
                    .setTitle("verify it is you?")
                    .setNegativeButton("Negative Btn", runnable -> handler.post(runnable), (dialog, which) -> {
                        Log.d("TAG", "showLockScreen: negative btn clicked, do nothing");
                    })
                    .setSubtitle("that is subtitile");

            if (keyguardManager.isDeviceSecure()) {
                builder.setDeviceCredentialAllowed(false);
            }

            final BiometricPrompt bp = builder.build();
            bp.authenticate(new CancellationSignal(),
                    runnable -> handler.post(runnable),
                    authenticationCallback);
        } else {
            Log.d("TAG", "showLockScreen:  no in scrue.... no password");
        }
    }
    */
}
