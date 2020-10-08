package choi.security;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class Security extends Application{
    private static final String CLASS_TAG = Security.class.getSimpleName();
    private static final String KEY_NAME = "example_key";
    Activity mAct;


    /**
     * 현재 상태 저장용(임시)한
     *
     * @param params
     * @return
     */
    private HashMap<String, Boolean> getSecuritySwitch(Object[] params) {
        HashMap<String, Boolean> hashM = null;
        if(params != null) {
            hashM = (HashMap<String, Boolean>) params[0];
            Log.d(CLASS_TAG, "SecuritySwitch" + hashM.get("captureLock"));
        }
        return hashM;
    }


    /*
        스크린샷 capture 방지
    */
    public void captureLock(Activity act, Object[] params) {

        ActivityLifeCycleCallback activityLifeCycleCallback;
        HashMap<String, Boolean> securitySwitch = getSecuritySwitch(params);

        activityLifeCycleCallback = ActivityLifeCycleCallback.getInstance();

        if(securitySwitch == null) {
            Log.d(CLASS_TAG, "SecuritySwitch NULL !!  ");
            return;
        }
        if(securitySwitch.get("captureLock") == false) {
            securitySwitch.put("captureLock", true);
            activityLifeCycleCallback.switchCapturelock();
            Log.d(CLASS_TAG, activityLifeCycleCallback.toString() + " NEW false !!  " + activityLifeCycleCallback.TFCaptureLock.toString());
            act.getApplication().registerActivityLifecycleCallbacks(activityLifeCycleCallback);
        }
        else {
            securitySwitch.put("captureLock", false);
            Log.d(CLASS_TAG, activityLifeCycleCallback.toString() + " NEW true !!  " + activityLifeCycleCallback.TFCaptureLock.toString());
            act.getApplication().unregisterActivityLifecycleCallbacks(activityLifeCycleCallback);
        }


            /*
            Intent intent = new Intent( mContext, MonitorService.class);
            mContext.startService(intent);
             */

        //

        //activityLifeCycleCallback.switchCapturelock();
        // True False 스위치 기능

    }

    /**
     *  FIDO 인증 알람창 관련 클래스
     *  showFingerPrintDialog 메소드에서 동작한 지문 인증 결과에 따라,
     *  알람창의 내부동작(인증성공, 실패시, 에러시 동작) 구현
     *
     */

    private class FingerprintListener extends FingerprintManager.AuthenticationCallback {

        private FingerprintManager.CryptoObject cryptoObject;
        private Dialog dialog;

        public FingerprintListener(Dialog dialog, FingerprintManager.CryptoObject cryptoObject) {
            super();
            this.dialog = dialog;
            this.cryptoObject = cryptoObject;
        }


        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            dialog.dismiss();
            Toast.makeText(mAct, "인증 성공", Toast.LENGTH_LONG).show();
            Log.i(CLASS_TAG, "인증 성공");
        }


        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();

            Log.i(CLASS_TAG, "인증 실패");
            Toast.makeText(mAct, "Not Authenticated", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Toast.makeText(mAct, errString, Toast.LENGTH_LONG).show();
        }
    }

    /**
     *  FIDO 알람창의 레이아웃을 가져오는 함수
     *  FIDO 함수가 호출 될 때의 activity 는 호출하는 액티비티(보안기능 호출앱)이므로,
     *  보안앱(Security)에 대한 정보가 전혀 없음. 따라서 createPackageContext 메소드를 통해
     *  보안 앱에대한 정보를 packageName 을 통해 보안앱의 리소스(레이아웃)을 가져오도록 함.
     */
    private Context mContext = null;
    public View getSecurityLayout(Activity act, int rid) {

        if(mContext == null) {
            try {
                mContext = act.createPackageContext("choi.security", Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        LayoutInflater myInflater = LayoutInflater.from(mContext);
        return myInflater.inflate(mContext.getResources().getLayout(rid), null, true);
    }


    /**
     *  FIDO 호출 함수.
     *  지문 인증에 필요한 레이아웃, 키값을 설정하고, FIDO 인증 알림창을 띄우는 함수.
     */
    private KeyguardManager mKeyguardManager;
    private FingerprintManager mFingerPrintManager;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private FingerprintManager.CryptoObject mCryptoObj;

    public void FIDO(Activity act, Object[] params) {

        String FUNC_TAG = "FIDO";
        this.mAct = act;

        mKeyguardManager = mAct.getSystemService(KeyguardManager.class);
        mFingerPrintManager = mAct.getSystemService(FingerprintManager.class);

        View layout = getSecurityLayout(act, R.layout.fragment_fingerprint);

        createKey();
        showFingerPrintDialog(layout);

    }


    /**
     *  FIDO 인증 알림창(Dialog)을 띄우는 함수.
     *  @params FIDO layout 뷰
     *  AlertDialog.builder를 통해 파라미터로 가져온 layout 형태로 보여줌.
     *
     */
    CancellationSignal cancellationSignal = new CancellationSignal();

    private void showFingerPrintDialog(View layout) {
        try {

            //AlertDialog.Builder builder = new AlertDialog.Builder(mAct);
            AlertDialog.Builder builder = new AlertDialog.Builder(mAct);
            //builder.setView(R.layout.fragment_fingerprint);
            builder.setView(layout)
                    .setCancelable(false)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancellationSignal.cancel();
                            dialog.dismiss();
                        }
                    });
            Dialog dialog = builder.create();
            dialog.show();

            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
            Cipher lu = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            lu.init(Cipher.ENCRYPT_MODE, key);

            mCryptoObj = new FingerprintManager.CryptoObject(lu);
            FingerprintListener listener = new FingerprintListener(dialog, mCryptoObj);


            mFingerPrintManager.authenticate(mCryptoObj, cancellationSignal,
                    0, listener, null);


        }catch(Exception e){
            e.printStackTrace();
            Log.e(CLASS_TAG, Arrays.toString(e.getStackTrace()) + " " + e.getMessage());
            Toast.makeText(mAct, e.getMessage() + " Dialog", Toast.LENGTH_LONG).show();
        }
    }

    /**
     *  지문인증을 위한 암호키 생성
     */
    private void createKey() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            mKeyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            mKeyGenerator.generateKey();

        } catch (Exception e) {
            Toast.makeText(mAct, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 테스트 용
     *
     */
    private void notifyDialog(final Context context, Activity act, String string) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
        alertDialogBuilder.setTitle("test");
        alertDialogBuilder.setMessage(string);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Dialog_Toast", Toast.LENGTH_LONG).show();
            }});

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * 키스트로크 호출 함수.
     * @param act 현재 액티비티(보안기능 호출 앱)
     * @param params 리플렉션을 통 호출 당시 전달받은 파라미터
     */
    public void keyStroke(Activity act, Object[] params) {

        final String clsName =  "choi.security";
        final String actName = "keystroke.KeyMainActivity";
        /*
        ComponentName component =

                new ComponentName(clsName, String.format("%s.%s", clsName, actName));
         */

        HashMap<String, Boolean> securitySwitch = getSecuritySwitch(params);

        Intent intent = new Intent()
                //.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                //.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setClassName(clsName, String.format("%s.%s", clsName, actName))
                .putExtra("securitySwitch", securitySwitch);
                //.setComponent(component);

        act.startActivity(intent);


    }

}


