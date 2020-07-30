package com.feelschaotic;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;


/**
 * 生物识别工具类
 */
public class FingerPrintUtils {

    private static FingerprintManagerCompat mFingerprintManagerCompat;
    private static CancellationSignal mCancellationSignal;

    public interface FingerPrintResult {
        void success();
        void error(int code, CharSequence info);
        void retry(int code, CharSequence info);
    }

    public static boolean cancelCallback(){
        if(mCancellationSignal!=null&&!mCancellationSignal.isCanceled()){
            mCancellationSignal.cancel();
            mCancellationSignal=null;
            return true;
        }else{
            return false;
        }
    }

    public static void init(Context context, final FingerPrintResult fpResult) {
        if (context == null || fpResult == null) {
            throw new RuntimeException("FingerPrintUtils.init()参数Context或者FingerPrintResult为null");
        }
        if (mFingerprintManagerCompat == null || mCancellationSignal == null) {
            mFingerprintManagerCompat = FingerprintManagerCompat.from(context);
            mCancellationSignal = new CancellationSignal();
        }
        if (mFingerprintManagerCompat.isHardwareDetected() && mFingerprintManagerCompat.hasEnrolledFingerprints()) {
            mFingerprintManagerCompat.authenticate(null, 0, mCancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                        fpResult.error(FingerprintManager.FINGERPRINT_ERROR_LOCKOUT, errString);
                    }else {
                        //fpResult.retry(errorCode,errString);
                    }
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    fpResult.retry(helpCode,helpString);
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    fpResult.success();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    fpResult.retry(0,"authentication failed");
                }
            }, null);
        } else {
            fpResult.error(-1, "硬件设备不支持指纹认证");
        }
    }

}
