package choi.security;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import choi.security.keystroke.KeyMainActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Security test = new Security();
        //test.FIDO(this, null);

        test.keyStroke(this, null);
        //test.captureLock(this, null);


        //new Security().FIDO(this, this, null);
    }
}