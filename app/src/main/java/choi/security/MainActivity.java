package choi.security;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {

    String CLASS_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Security test = new Security();
        test.FIDO(getApplicationContext(), this, null);
         */
        try {
            new Security().FIDO(getApplicationContext(), this, null);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

    }
}