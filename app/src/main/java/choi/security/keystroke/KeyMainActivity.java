package choi.security.keystroke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import choi.security.ActivityLifeCycleCallback;
import choi.security.R;
import choi.security.keystroke.data.setManage;
import choi.security.keystroke.db.DBcommand;
import choi.security.keystroke.util.Properties2;

/***
 * 2017 버전 키스트로크 인증 시연 앱
 * 코드 간결화 적용 및 특징 결합 후, 정규화 적용과 거리 기반 알고리즘으로 유사도 비교하여 인증
 */

public class KeyMainActivity extends AppCompatActivity {

    private Button button_train;                //학습 모드 액티비티로 전환
    private Button button_test;                 //실험 모드 액티비티로 전환
    private ImageButton button_setting;         //설정 모드 액티비티로 전환
    private TextView textview_user;             //현재 사용자 보여주기

    private setManage setting;

    private DBcommand DBmanager;
    private Properties2 properties;
    private ActivityLifeCycleCallback activityLifeCycleCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keystroke_mainselect);


        /*
            ActivityLifeCycleCallback 를 등록하는 과정
            Security 앱과 Reflection 앱의 singleton 따로 동작하는것 같아보임
            Reflection 의 callback singleton 을 전달 받는 것으로 구현
         */
        if(getIntent().getExtras() != null && activityLifeCycleCallback == null) {
            activityLifeCycleCallback = (ActivityLifeCycleCallback) getIntent().getSerializableExtra("callBack");
            Log.d("KeyMainAcitivity", "Got Intent");
        }
        else if(activityLifeCycleCallback == null){
            activityLifeCycleCallback = ActivityLifeCycleCallback.getInstance();
            Log.d("KeyMainAcitivity", "Create Callback Instance");
        }
        getApplication().registerActivityLifecycleCallbacks(activityLifeCycleCallback);


        layoutInit();           // 레이아웃 초기화
        checkSettingDB();       // 설정 테이블 확인

        View.OnClickListener bul = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button_setting:
                        callActivity(KeySettingActivity.class);
                        //finish();
                        break;
                    case R.id.button_train:
                        callActivity(KeyTrainActivity.class);
                        //finish();
                        break;
                    case R.id.button_test:
                        callActivity(KeyTestActivity.class);
                        //finish();
                        break;
                }
            }
        };
        button_test.setOnClickListener(bul);
        button_train.setOnClickListener(bul);
        button_setting.setOnClickListener(bul);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getApplication().unregisterActivityLifecycleCallbacks(activityLifeCycleCallback);

    }

    private void callActivity(Class<?> cls){
        Intent temp = new Intent(this, cls);
        //temp.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        temp.putExtra("setting_username", setting.getUsername()); // 세팅 테이블의 사용자 이름만 넘겨서, 해당 이름으로 각 액티비티에서 설정 값 및 학습 데이터 불러오기
        //temp.putExtra("callBack", activityLifeCycleCallback);
        startActivity(temp);
    }

    // 액티비티 실행시 레이아웃 초기화 - 버튼, 텍스트 뷰 및 디비
    private void layoutInit(){
        button_train = (Button)findViewById(R.id.button_train);
        button_test = (Button)findViewById(R.id.button_test);
        button_setting = (ImageButton)findViewById(R.id.button_setting);
        textview_user = (TextView)findViewById(R.id.textview_user);

        //프라퍼티 생성 - DB
        properties = new Properties2();
        properties.setDatabaseName("KeyStroke2017-2.db");
        //sqllite 생성
        DBmanager = new DBcommand(getApplicationContext(), properties.getDatabaseName(), null, 1);

        setting = new setManage();          // 사용자 설정 클래스
    }

    private void checkSettingDB(){
        if (DBmanager.isSetting() == "")
            callActivity(KeySettingActivity.class);
        else{
            loadSetting();
            textview_user.setText(setting.getUsername());
        }
    }


    //설정 테이블 값 호출
    public void loadSetting(){
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
    }
}
