package choi.security.keystroke;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


import choi.security.ActivityLifeCycleCallback;
import choi.security.R;
import choi.security.keystroke.data.setManage;
import choi.security.keystroke.db.DBcommand;
import choi.security.keystroke.util.Properties2;


public class KeySettingActivity extends AppCompatActivity {

    // 액티비티 버튼
    private Button button_new;
    private Button button_confirm;
    private Button button_filesave;
    private Button button_userchange;
    private Button button_targetfeature;
    private Button button_classifier;
    Button button_pinCount;
    Button button_slidingwindow;

    // 설정 항목들 - 사용자 및 PIN 정보
    private EditText edittext_user;
    private EditText edittext_pin;

    // 설정 항목들 - 학습 구분 정보
    private CheckBox checkbox_posture;
    private CheckBox checkbox_learned_data_delete;
    private CheckBox checkbox_add_positive_data;
    private CheckBox checkbox_outlier_delete;

    private setManage setting;

    // 신규 버튼 클릭 여부
    boolean button_new_istouched;

    // 사용자 변경용 변수
    private String selected_username;
    private String selected_feature;
    private String selected_classifier;

    // 디비
    private DBcommand DBmanager;
    private Properties2 properties;

    // TrainResult 관련 설정
    private String targetFeature; // time, acc, gyr, all
    private String Classifier; // 맨하튼
    private ActivityLifeCycleCallback activityLifeCycleCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keystroke_setting);
        setTitle("Keystroke2017 - Setting");

        // 임시 조치
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        layoutInit();
        loadIntent();

        View.OnClickListener bul = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button_new:
                        clickedNew();
                        break;
                    case R.id.button_userchange:
                        break;
                    case R.id.button_confirm:
                        clickedConfirm();
                        break;
                }
            }
        };
        button_new.setOnClickListener(bul);
        button_userchange.setOnClickListener(bul);
        button_confirm.setOnClickListener(bul);
    }

    private void layoutInit(){
        // 액티비티 버튼 - 사용자 신규, 변경, 핀 입력 횟수, 슬라이딩 윈도우
        button_new = (Button)findViewById(R.id.button_new);
        button_userchange = (Button)findViewById(R.id.button_userchange);
        button_pinCount = (Button)findViewById(R.id.button_pinCount);
        button_slidingwindow = (Button)findViewById(R.id.button_slidingwindow);
        button_targetfeature = (Button) findViewById(R.id.button_feature);
        button_classifier = (Button) findViewById(R.id.button_classifier);

        // 확인, 파일 저장 버튼
        button_confirm = (Button) findViewById(R.id.button_confirm);
        button_filesave = (Button) findViewById(R.id.button_filesave);

        // 설정 항목들 - 사용자 구분 정보
        edittext_user = (EditText)findViewById(R.id.edittext_user);
        edittext_pin = (EditText)findViewById(R.id.edittext_pin);

        // 설정 항목들 - 학습 구분 정보
        checkbox_posture = (CheckBox) findViewById(R.id.checkbox_posture);
        checkbox_learned_data_delete = (CheckBox) findViewById(R.id.checkbox_learned_data_delete);
        checkbox_add_positive_data = (CheckBox) findViewById(R.id.checkbox_realuser_data_add);
        checkbox_outlier_delete = (CheckBox) findViewById(R.id.checkbox_outlier);

        // DB 정보 로드
        properties = new Properties2();
        DBmanager = new DBcommand(getApplicationContext(), properties.getDatabaseName(), null, 1);
        Log.e("SettingActivity", "result: " + DBmanager.isSetting());

        setting = new setManage();
    }

    private void loadIntent(){
        Intent intent = this.getIntent();
        String username = intent.getStringExtra("setting_username");
        Log.e("Setting", username);
        if (username.equals("")){
            AlertDialog.Builder alert = new AlertDialog.Builder(KeySettingActivity.this);
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // 닫기
                }
            });
            alert.setMessage("최초 사용입니다. NEW 버튼을 터치하여 사용자 정보를 설정해 주세요.");
            alert.show();
        }
        else{

        }
    }

    private void clickedNew(){
        button_new_istouched = true;									//new 버튼 터치됨
        //활성화
        edittext_user.setEnabled(true);
        edittext_pin.setEnabled(true);
        checkbox_posture.setEnabled(true);
        checkbox_learned_data_delete.setEnabled(true);
        checkbox_add_positive_data.setEnabled(true);
        checkbox_outlier_delete.setEnabled(true);
        button_targetfeature.setEnabled(true);
        button_classifier.setEnabled(true);
        // 초기화
        edittext_user.setText("");
        edittext_pin.setText("");
        checkbox_posture.setChecked(false);
        checkbox_learned_data_delete.setChecked(false);
        checkbox_add_positive_data.setChecked(false);
        checkbox_outlier_delete.setChecked(false);
        button_pinCount.setText("5");
        button_slidingwindow.setText("5");
    }

    private void clickedConfirm(){
        if (button_new_istouched == true){
            saveSetting();
            Intent intent = new Intent(KeySettingActivity.this, KeyMainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //세팅 클래스에 모든 데이터 저장하기
    public void saveSetting() {
        String username = edittext_user.getText().toString();
        String pinNum= edittext_pin.getText().toString();
        boolean activity = checkbox_posture.isChecked();
        boolean learned_data_delete = checkbox_learned_data_delete.isChecked();
        int slidingwindow = Integer.parseInt(button_slidingwindow.getText().toString());
        int input_count = Integer.parseInt(button_pinCount.getText().toString());
        boolean add_positive_data = checkbox_add_positive_data.isChecked();
        boolean outlier_delete = checkbox_outlier_delete.isChecked();
        String target_feature = button_targetfeature.getText().toString();
        String classifier = button_classifier.getText().toString();

        // 설정 클래스에 설정하기
        setting.setUsername(username);
        setting.setPinNum(pinNum);
        setting.setActivity(activity);
        setting.setLearned_data_delete(learned_data_delete);
        setting.setSlidingwindow(slidingwindow);
        setting.setPinCount(input_count);
        setting.setAdd_positive_data(add_positive_data);
        setting.setOutlier_delete(outlier_delete);
        setting.setTargetfeatrue(target_feature);
        setting.setClassifier(classifier);

        targetFeature = button_targetfeature.getText().toString();
        Classifier = button_classifier.getText().toString();

        DBmanager.insertSetting(setting);
    }

    //뒤로가기 버튼 - 해당 사용자 설정이 변경된 경우, 디비 삭제후 다시 삽입하는 방식으로 인텐트 호출시 사용자 이름 전달할 필요 없음


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Intent intent = new Intent(KeySettingActivity.this, KeyMainActivity.class);
        //startActivity(intent);
        finish();

    }
}
