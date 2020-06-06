package choi.security;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

;

public class Security {
    private static final String CLASS_TAG = Security.class.getSimpleName();
    private static final String KEY_NAME = "example_key";

    /*
        스크린샷 capture 방지
    */
    private void captureLock(Context context, Activity act, Object[] params) {
        act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }

    Context context;


    public void FIDO(Context context, Activity act, Object[] params) {
        String FUNC_TAG = "FIDO";

        // TODO: 기능지원이 안될 시 강제종료 기능 추가

        Toast.makeText(act, "tasdf",Toast.LENGTH_SHORT).show();

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

}
