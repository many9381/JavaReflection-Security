package choi.security;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.security.KeyStore;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class Security {
    private static final String CLASS_TAG = Security.class.getSimpleName();
    private static final String KEY_NAME = "example_key";
    Context mContext;
    Activity mAct;

    /*
        스크린샷 capture 방지
    */
    private void captureLock(Context context, Activity act, Object[] params) {
        act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
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


    private KeyguardManager mKeyguardManager;
    private FingerprintManager mFingerPrintManager;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private FingerprintManager.CryptoObject mCryptoObj;

    public void FIDO(final Context context, Activity act, Object[] params) {

        String FUNC_TAG = "FIDO";

        this.mContext = context;
        this.mAct = act;

        mKeyguardManager = mAct.getSystemService(KeyguardManager.class);
        mFingerPrintManager = mAct.getSystemService(FingerprintManager.class);

        LayoutInflater myInflater = LayoutInflater.from(context);
        layout =  myInflater.inflate(R.layout.fragment_fingerprint, null, false);

        createKey();
        showFingerPrintDialog();

    }

    private View layout;
    CancellationSignal cancellationSignal = new CancellationSignal();

    private void showFingerPrintDialog() {
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

    public void keyStroke(final Context context, Activity act, Object[] params) {

        KeyStrokeDialog keyStrokeDialog = new KeyStrokeDialog();
        keyStrokeDialog.showDialog(act);
    }
}


class KeyStrokeDialog {

    private Button button_train;                //학습 모드 액티비티로 전환
    private Button button_test;                 //실험 모드 액티비티로 전환
    private ImageButton button_setting;         //설정 모드 액티비티로 전환
    private TextView textview_user;             //현재 사용자 보여주기

    /*
    private setManage setting;
    private DBcommand DBmanager;
    private Properties2 properties;

     */

    public Context context;

    public void showDialog(Activity act) {

        final Dialog dialog = new Dialog(act, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.keystroke_mainselect);

        layoutInit(dialog);

        View.OnClickListener bul = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button_setting:
                        //callActivity(SettingActivity.class);
                        dialog.dismiss();
                        break;
                    case R.id.button_train:
                        //callActivity(TrainActivity.class);
                        dialog.dismiss();
                        break;
                    case R.id.button_test:
                        //callActivity(TestActivity.class);
                        dialog.dismiss();
                        break;
                }
            }
        };


        button_test.setOnClickListener(bul);
        button_train.setOnClickListener(bul);
        button_setting.setOnClickListener(bul);

        dialog.show();

    }

    private void callActivity(Class<?> cls){
        Intent temp = new Intent(this.context, cls);
        //temp.putExtra("setting_username", setting.getUsername());           // 세팅 테이블의 사용자 이름만 넘겨서, 해당 이름으로 각 액티비티에서 설정 값 및 학습 데이터 불러오기
        context.startActivity(temp);
    }

    // 액티비티 실행시 레이아웃 초기화 - 버튼, 텍스트 뷰 및 디비
    private void layoutInit(Dialog dialog){
        button_train = (Button)dialog.findViewById(R.id.button_train);
        button_test = (Button)dialog.findViewById(R.id.button_test);
        button_setting = (ImageButton)dialog.findViewById(R.id.button_setting);
        textview_user = (TextView)dialog.findViewById(R.id.textview_user);

        /*
        //프라퍼티 생성 - DB
        properties = new Properties2();
        properties.setDatabaseName("KeyStroke2017-2.db");
        //sqllite 생성
        DBmanager = new DBcommand(getApplicationContext(), properties.getDatabaseName(), null, 1);

        setting = new setManage();          // 사용자 설정 클래스

         */
    }
}