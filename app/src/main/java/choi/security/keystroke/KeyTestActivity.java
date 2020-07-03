package choi.security.keystroke;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;

import choi.security.ActivityLifeCycleCallback;
import choi.security.R;
import choi.security.keystroke.alg.ClassifierDistance;
import choi.security.keystroke.api.APIConst;
import choi.security.keystroke.data.*;
import choi.security.keystroke.db.DBcommand;
import choi.security.keystroke.util.Properties2;

public class KeyTestActivity extends AppCompatActivity {

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
    private TextView tv_username;             //현재 사용자 보여주기
    private TextView tv_result;

    // 디비
    private DBcommand DBmanager;
    private Properties2 properties;

    private setManage setting;
    private DataManage dm;
    private PinManage pm;
    private SensorManager sm;
    private FeatureManage fm;
    private TrainManage tm;
    private ClassifierDistance cd;

    //인텐트 관련
    final static int INTENT_IM_AUTH_KEYSTROKE = APIConst.INTENT_KEYSTROKE;
    boolean intent_user_cancel = true;
    int intent_keystroke_result;      //keystroke 결과
    int intent_pin_result;  //pin결과
    float intent_prob;
    int reqCode = 0;

    private String result_msg = "";

    private ActivityLifeCycleCallback activityLifeCycleCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.keystroke_test);

        layoutInit();
        loadIntent();
        loadSetting();
        loadTrainData();



        if(activityLifeCycleCallback == null){
            activityLifeCycleCallback = ActivityLifeCycleCallback.getInstance();
            Log.d("KeyTestAcitivity", "Create Callback Instance");
        }

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

    private void loadTrainData(){
        Log.e("TestActivity", "loadTrainData: " + DBmanager.isSetting());
        tm = new TrainManage();
        ArrayList<String> temp = new ArrayList<String>();
        temp = DBmanager.loadTrainResultValues(setting.getUsername());

        tm.setUsername(temp.get(0));
        tm.setInputtime(Long.parseLong(temp.get(1)));
        tm.setDay(temp.get(2));
        tm.setMean(temp.get(4));
        tm.setMin(temp.get(5));
        tm.setMax(temp.get(6));
        tm.setThreshold(Double.valueOf(temp.get(7)));

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
                pm.clearInputPin();         //사용자 입력한 PIN 숫자 초기화
                tv_inputbox.setText(pm.getinputPin());  //PIN숫자 보여주기
                Log.e("TestActivity", "Count: " + pm.getPinCount());
                if (pm.lastCount()){            // DB에 저장하기
                    DBmanager.insertRawData(dm, setting.getUsername(), setting.getPinNum(), setting.getPinCount());     // 디비에 로데이터 삽입
                    fm = new FeatureManage(dm, setting);            // 특징 추출
                    DBmanager.insertFeature(fm);                    // 디비에 특징 삽입
                    //학습 결과와 비교하기
                    if (cd.calcDistance(tm, fm) < tm.getThreshold()){
                        Log.e("TestActivity", "T, 임계값" + tm.getThreshold() + ", 결과: " + cd.calcDistance(tm, fm));
                        tv_result.setText("T, 임계값" + tm.getThreshold() + ", 결과: " + cd.calcDistance(tm, fm));
                        intent_keystroke_result = 1;
                        intent_pin_result = 1;
                        intent_prob = 100;
                        intent_user_cancel = false;
                        result_msg = "정상 사용자입니다.";
                        if (setting.getRetrain_with_positive_data() == true)
                            reTrain();
                    }
                    else{
                        Log.e("TestActivity", "F, 임계값" + tm.getThreshold() + ", 결과: " + cd.calcDistance(tm, fm) + ", 확률: " + (tm.getThreshold() / cd.calcDistance(tm, fm)));
                        //Toast.makeText(getApplicationContext(), "비정상 사용자입니다.", Toast.LENGTH_SHORT).show();
                        tv_result.setText("F, 임계값" + tm.getThreshold() + ", 결과: " + cd.calcDistance(tm, fm) + ", 확률: " + (tm.getThreshold() / cd.calcDistance(tm, fm)));
                        intent_keystroke_result = 0;
                        intent_pin_result = 1;
                        intent_prob = (float)(tm.getThreshold() / cd.calcDistance(tm, fm));
                        intent_user_cancel = false;
                        result_msg = "비정상 사용자입니다.";

                    }
                    dm.DataClearAll();
                    pm.clearInputPin();
                    pm.clearCurrentCount();
                }
                if (reqCode == APIConst.INTENT_KEYSTROKE)
                    processRequest(reqCode);
                else
                    Toast.makeText(getApplicationContext(), result_msg, Toast.LENGTH_SHORT).show();
            }
            else{
                if (reqCode == APIConst.INTENT_KEYSTROKE){
                    intent_user_cancel = false;
                    intent_keystroke_result = 0;
                    intent_pin_result = 0;
                    processRequest(reqCode);
                }
                else{
                    Toast.makeText(getApplicationContext(), "핀 번호가 틀렸습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                    dm.DataClearOnce();         //임시 데이터 공간 초기화
                    pm.clearInputPin();         //사용자 입력한 PIN 숫자 초기화
                    tv_inputbox.setText(pm.getinputPin());  //PIN숫자 보여주기
                }
            }
        }
    }

    private void reTrain(){
        if (setting.getSlidingwindow() == 0){ // total

        }
        else{       // moving window
            //db에서 rawdata 불러오기
            loadRawData();
            //db에서 feature 불러오기
            loadFeature();
        }
    }

    private void loadFeature(){
        ArrayList<Integer> featureIDs = new ArrayList<Integer>();
        featureIDs = DBmanager.loadFeatureID(setting.getUsername());         // id 목록으로 오름차순으로 불러오기
        fm = DBmanager.loadFeatureValues(featureIDs, setting.getSlidingwindow());
        Log.e("TestActivity", "loadFeature: " + fm.getAll().size());
    }

    private void loadRawData(){
        ArrayList<Integer> rawdataID = new ArrayList<Integer>();
        rawdataID = DBmanager.loadRawdataID(setting.getUsername());         // id 목록으로 오름차순으로 불러오기
        dm = DBmanager.loadRawdataValues(rawdataID, setting.getSlidingwindow());
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
        Log.e("TestActivity", "loadSetting: " + DBmanager.isSetting());
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
        setting.setPinCount(1);         // 테스트 액티비티에서는 핀 입력은 1회임
        pm.setPinNum(setting.getPinNum());
        pm.setPinCount(setting.getPinCount());          // 테스트 액티비티에서는 핀 입력은 1회임

        tv_username.setText("User : " + setting.getUsername());
        tv_pinNum.setText("PIN: " + pm.getPin());
        tv_inputbox.setHint(pm.getPin() + "을 입력하세요");
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
        tv_username = (TextView)findViewById(R.id.textview_username);
        tv_result = (TextView)findViewById(R.id.textview_result);

        // DB 정보 로드
        properties = new Properties2();
        properties.setDatabaseName("KeyStroke2017-2.db");
        DBmanager = new DBcommand(getApplicationContext(), properties.getDatabaseName(), null, 1);
        Log.e("TestActivity", "result: " + DBmanager.isSetting());

        setting = new setManage();
        dm = new DataManage();
        pm = new PinManage();
        cd = new ClassifierDistance();

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


        layoutInit();
        loadSetting();
        loadTrainData();

        Intent intent = getIntent();
        reqCode = intent.getIntExtra(APIConst.ARReq, 0);
        if (reqCode == APIConst.INTENT_KEYSTROKE){
            tv_pinNum.setVisibility(View.INVISIBLE);
            tv_result.setVisibility(View.INVISIBLE);
            tv_username.setVisibility(View.INVISIBLE);
            setTitle("Implicit Keystroke Auth");
            tv_inputbox.setHint("핀을 입력하세요");
            tv_inputbox.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
            tv_inputbox.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            setContentView(R.layout.activity_test_new);
        }

    }

    protected void onPause(){
        super.onPause();
        sm.unregisterListener(sel);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (reqCode == APIConst.INTENT_KEYSTROKE)
            processRequest(reqCode);
        else{
            //Intent intent = new Intent(KeyTestActivity.this, KeyMainActivity.class);
            //startActivity(intent);
            finish();
        }
    }

    //넣어야 되는 부분
    protected void processRequest(int reqCode) {
        Intent intent = new Intent();
        Bundle result = new Bundle();

        switch (reqCode) {
            case INTENT_IM_AUTH_KEYSTROKE:
                result.putLong(APIConst.Time, System.currentTimeMillis());
                result.putBoolean(APIConst.Keystroke_Auth_User_Cancel, intent_user_cancel);
                result.putBoolean(APIConst.InternalError, false);
                result.putInt(APIConst.Keystroke_Auth_Result, intent_keystroke_result); //<-boolean 아니라 int로 성공이면 1, 실패면 0으로
                result.putFloat(APIConst.Keystroke_Auth_Result_Prob, intent_prob);
                result.putInt(APIConst.PIN_Auth_Result, intent_pin_result);
                break;
        }

        intent.putExtra(APIConst.Bundle_AMResult, result);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
