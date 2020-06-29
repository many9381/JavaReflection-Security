package choi.security;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.security.KeyStore;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import choi.security.keystroke.KeyMainActivity;

public class Security {
    private static final String CLASS_TAG = Security.class.getSimpleName();
    private static final String KEY_NAME = "example_key";
    Activity mAct;

    /*
        스크린샷 capture 방지
    */
    public void captureLock(Activity act, Object[] params) {
        act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        Context test;
        try {
            test = act.getApplicationContext().createPackageContext("choi.security",
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        /*
        try {
            Context context = act.createPackageContext("choi.security", Context.CONTEXT_IGNORE_SECURITY);
            PackageManager pm = context.getPackageManager();

            PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            ActivityInfo[] list = info.activities;

            ActivityManager actSecu = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            final String clsName =  "choi.security";
            final String actName = "keystroke.KeyMainActivity";
            ComponentName component =
                    new ComponentName(clsName, String.format("%s.%s", clsName, actName));

            ActivityInfo actInfo = pm.getActivityInfo(component, PackageManager.MATCH_DEFAULT_ONLY | PackageManager.GET_META_DATA);

            Toast.makeText(mAct, "인증 성공", Toast.LENGTH_LONG).show();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

         */


    }

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

    private Context mContext = null;
    public View getSecurityLayout(Activity act, int rid) {

        if(mContext == null) {
            try {
                mContext = act.createPackageContext("choi.security", Context.CONTEXT_IGNORE_SECURITY);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        LayoutInflater myInflater = LayoutInflater.from(mContext);
        return myInflater.inflate(mContext.getResources().getLayout(rid), null, true);
    }


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

        Context test = null;
        try {
            test = act.createPackageContext("choi.security", Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        View layout = getSecurityLayout(act, R.layout.fragment_fingerprint);

        createKey();
        showFingerPrintDialog(layout);

    }


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

    public static void main(String[] args) {
        Log.d("TEST", "TEST");

    }


    public void keyStroke(Activity act, Object[] params) {

        /*
        Intent intent = new Intent(Intent.ACTION_MAIN)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addCategory(Intent.CATEGORY_LAUNCHER);

         */

        final String clsName =  "choi.security";
        final String actName = "keystroke.KeyMainActivity";
        ComponentName component =
                new ComponentName(clsName, String.format("%s.%s", clsName, actName));
        Intent intent = new Intent(Intent.ACTION_MAIN)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //.addCategory(Intent.CATEGORY_LAUNCHER)
                .setComponent(component);
        act.startActivity(intent);

        /*
        Intent intent = new Intent(act, KeyMainActivity.class);
        act.startActivity(intent);
         */


    }

}
