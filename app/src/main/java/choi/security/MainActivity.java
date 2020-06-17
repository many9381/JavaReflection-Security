package choi.security;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {

    //String CLASS_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Security test = new Security();
        test.FIDO(getApplicationContext(), this, null);
         */

        //new Security().FIDO(getApplicationContext(), this, null);
        new Security().keyStroke(getApplicationContext(), this, null);

    }

    public void getActivityContext(){

        Log.d("KEY", "Main: ");
        Context tt = this.getApplicationContext();
        Toast.makeText(tt, "asdfasdfasdf", Toast.LENGTH_LONG).show();

        //return this.getApplicationContext();



    }
}