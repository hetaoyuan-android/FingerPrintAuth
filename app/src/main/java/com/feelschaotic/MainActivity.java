package com.feelschaotic;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
}
