package choi.security.keystroke;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import choi.security.R;
import choi.security.keystroke.data.*;
import choi.security.keystroke.db.DBcommand;
import choi.security.keystroke.util.Properties2;

public class KeyTrainActivity extends AppCompatActivity {

    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button button_confirm;
    private Button button_cancel;

    private TextView tv_inputbox;
    private TextView tv_pinNum;
    private TextView tv_status;
    private TextView textview_username;             //현재 사용자 보여주기

    // 디비
    private DBcommand DBmanager;
    private Properties2 properties;

    private setManage setting;
    private DataManage dm;
    private PinManage pm;
    private SensorManager sm;
    private FeatureManage fm;
    private TrainManage tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keystroke_train);
        layoutInit();
        loadIntent();
        loadSetting();
        View.OnTouchListener buttonlistener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (view.getId()){
                    case R.id.button_0:
                        buttonClicked(event, "0");
                        break;
                    case R.id.button_1:
                        buttonClicked(event, "1");
                        break;
                    case R.id.button_2:
                        buttonClicked(event, "2");
                        break;
                    case R.id.button_3:
                        buttonClicked(event, "3");
                        break;
                    case R.id.button_4:
                        buttonClicked(event, "4");
                        break;
                    case R.id.button_5:
                        buttonClicked(event, "5");
                        break;
                    case R.id.button_6:
                        buttonClicked(event, "6");
                        break;
                    case R.id.button_7:
                        buttonClicked(event, "7");
                        break;
                    case R.id.button_8:
                        buttonClicked(event, "8");
                        break;
                    case R.id.button_9:
                        buttonClicked(event, "9");
                        break;
                    case R.id.button_cancel:
                        buttonCancel(event);
                        break;
                    case R.id.button_confirm:
                        buttonConfirm(event);
                        break;
                }
                return false;
            }
        };

        button0.setOnTouchListener(buttonlistener);
        button1.setOnTouchListener(buttonlistener);
        button2.setOnTouchListener(buttonlistener);
        button3.setOnTouchListener(buttonlistener);
        button4.setOnTouchListener(buttonlistener);
        button5.setOnTouchListener(buttonlistener);
        button6.setOnTouchListener(buttonlistener);
        button7.setOnTouchListener(buttonlistener);
        button8.setOnTouchListener(buttonlistener);
        button9.setOnTouchListener(buttonlistener);
        button_confirm.setOnTouchListener(buttonlistener);
        button_cancel.setOnTouchListener(buttonlistener);
    }

    public void buttonCancel(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_UP){
            pm.clearInputPin();
            tv_inputbox.setText(pm.getinputPin());
            dm.DataClearOnce();     //1회 입력 배열 초기화
            while (dm.checkClear() != true)         //초기화 여부 확인
                Toast.makeText(getApplicationContext(), "아직 데이터가 초기화되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonConfirm(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_UP){
            if (pm.matchPin()){             // 핀번호 확인
                dm.addOnceData();           //1회 입력 데이터 저장
                pm.addCurrentCount();       //입력 횟수 증가
                tv_status.setText("현재 입력 상황: " + pm.getCurrentCount() + "/" + pm.getPinCount());
                dm.DataClearOnce();         //임시 데이터 공간 초기화
                pm.clearInputPin();         //사용자 입력한 PIN 숫자 초기화
                tv_inputbox.setText(pm.getinputPin());  //PIN숫자 보여주기
                if (pm.lastCount()){            // DB에 저장하기
                    DBmanager.insertRawData(dm, setting.getUsername(), setting.getPinNum(), setting.getPinCount());     // 디비에 로데이터 삽입
                    fm = new FeatureManage(dm, setting);            // 특징 추출
                    DBmanager.insertFeature(fm);                    // 디비에 특징 삽입
                    tm.insertTrainData(fm, setting);              // 학습 시작
                    DBmanager.insertTrainResult(tm);                // 디비에 학습 결과 삽입

                    dm.DataClearAll();
                    pm.clearInputPin();
                    pm.clearCurrentCount();
//                    tv_status.setText("현재 입력 상황: " + pm.getCurrentCount() + "/" + pm.getPinCount());

                    Intent intent = new Intent(KeyTrainActivity.this, KeyMainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "핀 번호가 틀렸습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                dm.DataClearOnce();         //임시 데이터 공간 초기화
                pm.clearInputPin();         //사용자 입력한 PIN 숫자 초기화
                tv_inputbox.setText(pm.getinputPin());  //PIN숫자 보여주기
            }
        }
    }

    private void buttonClicked(MotionEvent event, String num){
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            pm.addinputPin(num);
            dm.tempKeyTime(System.nanoTime());
            dm.tempKeySize(event.getSize());
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            dm.tempKeyTime(System.nanoTime());
            dm.tempKeySize(event.getSize());
        }
        tv_inputbox.setText(pm.getinputPin());
    }

    private void loadIntent(){
        Intent intent = this.getIntent();
        String username = intent.getStringExtra("setting_username");
        Log.e("Setting", username);
    }

    //설정 테이블 값 호출
    private void loadSetting(){
        ArrayList<String> temp = new ArrayList<>();
        temp = DBmanager.loadSettingValues();

        setting.setUsername(temp.get(0));
        setting.setPinNum(temp.get(1));
        setting.setActivity(Boolean.parseBoolean(temp.get(2)));
        setting.setLearned_data_delete(Boolean.parseBoolean(temp.get(3)));
        setting.setSlidingwindow(Integer.parseInt(temp.get(4)));
        setting.setPinCount(Integer.parseInt(temp.get(5)));
        setting.setAdd_positive_data(Boolean.parseBoolean(temp.get(6)));
        setting.setOutlier_delete(Boolean.parseBoolean(temp.get(7)));
        setting.setTargetfeatrue(temp.get(8));
        setting.setClassifier(temp.get(9));
        setting.setCurrentCount(0);
        pm.setPinNum(setting.getPinNum());
        pm.setPinCount(setting.getPinCount());

        textview_username.setText("User : " + setting.getUsername());
        tv_pinNum.setText("PIN: " + pm.getPin());
        tv_inputbox.setHint(pm.getPin() + "을 입력하세요");
        tv_status.setText(pm.getCurrentCount() + " / " + pm.getPinCount());
    }

    private void layoutInit(){
        button0 = (Button)findViewById(R.id.button_0);
        button1 = (Button)findViewById(R.id.button_1);
        button2 = (Button)findViewById(R.id.button_2);
        button3 = (Button)findViewById(R.id.button_3);
        button4 = (Button)findViewById(R.id.button_4);
        button5 = (Button)findViewById(R.id.button_5);
        button6 = (Button)findViewById(R.id.button_6);
        button7 = (Button)findViewById(R.id.button_7);
        button8 = (Button)findViewById(R.id.button_8);
        button9 = (Button)findViewById(R.id.button_9);
        button_confirm = (Button)findViewById(R.id.button_confirm);
        button_cancel = (Button)findViewById(R.id.button_cancel);

        tv_pinNum = (TextView)findViewById(R.id.textview_pinNum);
        tv_inputbox = (TextView)findViewById(R.id.textview_inputbox);
        tv_status = (TextView)findViewById(R.id.textview_status);
        textview_username = (TextView)findViewById(R.id.textview_username);

        //프라퍼티 생성 - DB
        properties = new Properties2();
        properties.setDatabaseName("KeyStroke2017-2.db");
        //sqllite 생성
        DBmanager = new DBcommand(getApplicationContext(), properties.getDatabaseName(), null, 1);
        Log.e("SettingActivity", "result: " + DBmanager.isSetting());

        setting = new setManage();
        dm = new DataManage();
        pm = new PinManage();
        tm = new TrainManage();

        sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    }

    SensorEventListener sel = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] v = event.values;
            switch(event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    dm.tempAcc((double)v[0], (double)v[1], (double)v[2], System.nanoTime());
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    dm.tempLAcc((double)v[0], (double)v[1], (double)v[2], System.nanoTime());
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    dm.tempGyr((double)v[0], (double)v[1], (double)v[2], System.nanoTime());
                    break;
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                    dm.tempUGyr((double)v[0], (double)v[1], (double)v[2], System.nanoTime());
                    break;

            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    protected void onResume(){
        super.onResume();
        int delay = SensorManager.SENSOR_DELAY_FASTEST;
        sm.registerListener(sel, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), delay);
        sm.registerListener(sel, sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), delay);
        sm.registerListener(sel, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE), delay);
        sm.registerListener(sel, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED), delay);

    }

    protected void onPause(){
        super.onPause();
        sm.unregisterListener(sel);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
