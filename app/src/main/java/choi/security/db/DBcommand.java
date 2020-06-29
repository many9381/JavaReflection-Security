package choi.security.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import choi.security.data.DataManage;
import choi.security.data.FeatureManage;
import choi.security.data.TrainManage;
import choi.security.data.setManage;
import choi.security.util.Converter;

public class DBcommand extends KeystrokeDB {
    public DBcommand(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Setting 테이블에 클래스 인자 받아서 넣기
    public void insertSetting(setManage setting) {
        String username = setting.getUsername();
        String pinNum = setting.getPinNum();
        boolean activity = setting.getActivity();
        boolean learned_data_delete = setting.getLearned_data_delete();
        int slidingwindow = setting.getSlidingwindow();
        int input_count = setting.getPinCount();
        boolean add_positive_data = setting.getRetrain_with_positive_data();
        boolean outlier_delete = setting.getOutlier_delete();
        String targetfeature = setting.getTargetfeature();
        String classifier = setting.getClassifier();

        String query = "insert into Setting values(null, '" + username + "', '" + pinNum + "', '" + activity + "', '"
                + learned_data_delete + "', '" + slidingwindow + "', '" + input_count + "', '" + add_positive_data
                + "', '" + outlier_delete + "', '" + targetfeature + "', '" + classifier + "');";

        insert(query);
        Log.e("DBCommand", "세팅 테이블에 insert 완료!!");
    }

    public ArrayList<Integer> loadRawdataID(String username){
        ArrayList<Integer> output = new ArrayList<Integer>();
        String field_name = "_id";
        String table_name = "rawdata";
        output = selecttoWhereNameToID(field_name, table_name, username);
        return output;
    }

    public ArrayList<Integer> loadFeatureID(String username){
        ArrayList<Integer> output = new ArrayList<Integer>();
        String field_name = "_id";
        String table_name = "feature";
        output = selecttoWhereNameToID(field_name, table_name, username);
        return output;
    }

    public FeatureManage loadFeatureValues(ArrayList<Integer> ids, int windowsize){
        FeatureManage fm = new FeatureManage();
        String field_name = "*";
        String table_name = "feature";
        Log.e("DBcommand", "ids: " + ids.size());

        if (windowsize != 0){
            for (int i = 0; i < windowsize; i++ ){
                Cursor cursor = selectWhereId(field_name, table_name, ids.get(i));
                while (cursor.moveToNext()){
                    fm.setKey(cursor.getString(4));     // 타임 특징
                    fm.setSize(cursor.getString(5));       // 사이즈
                }
            }
        }
        return fm;
    }

    public DataManage loadRawdataValues(ArrayList<Integer> ids, int windowsize){
        DataManage dm = new DataManage();
        String field_name = "*";
        String table_name = "rawdata";
        if (windowsize != 0){
            for (int i = 0; i < windowsize; i++ ){
                Cursor cursor = selectWhereId(field_name, table_name, ids.get(i));
                while (cursor.moveToNext()){
                    dm.setUsername(cursor.getString(1));
                }
            }
        }
        return dm;
    }

    // RawData 테이블에 데이저 터장
    public void insertRawData(DataManage dm, String username, String pinNum, int pinCount){
        // db 입력 날짜값
        long inputtime = System.currentTimeMillis();
        Date date = new Date(inputtime);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");
        String day = sdfNow.format(date);
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("HHmmssS");
        String time = sdfNow2.format(date);
        if ( checkDataSize(dm, pinCount) ){         // 데이터 사이즈가 입력 횟수와 동일하면,
            for (int i = 0; i < dm.getkeyTime().size(); i++){
                String keytime = Converter.convertArrayListStringToString(dm.getkeyTime().get(i));
                String size = Converter.convertArrayListStringToString(dm.getkeySize().get(i));

                String query = "insert into rawdata values(null, '" + username + "', '" + inputtime + "', '" + day + "', '" + time
                        + "', '" + keytime + "', '" + size
                        + "');";
                insert(query);
                Log.e("DBCommand", "rawdata 테이블 insert 완료" + inputtime);
            }
        }
    }

    // feature 클래스를 인자로 넣어서 디비에 저장하는 메소드
    public void insertFeature(FeatureManage feature) {
        // db 입력 날짜값
        long inputtime = System.currentTimeMillis();
        Date date = new Date(inputtime);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");
        String day = sdfNow.format(date);
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("HHmmssS");
        String time = sdfNow2.format(date);

        for (int i = 0; i < feature.getKeyTimeFeautre().size(); i++){

            String name = feature.getUsername();
            String key = Converter.convertArrayListDoubleToString(feature.getKeyTimeFeautre().get(i));
            String size = Converter.convertArrayListDoubleToString(feature.getSizeFeature().get(i));
            Log.e("DBcommand", "insertFeautre: " + name);
            String query = "insert into feature values(null, '" + name + "', '" + inputtime + "', '"
                    + day + "', '" + time + "', '" + key + "', '" + size + "');";
            insert(query);
            Log.e("DBCommand", "feature 테이블에 insert 완료");
        }
    }

    // TrainResult 테이블에 데이터 삽입
    public void insertTrainResult(TrainManage input) {
        String mean = Converter.convertArrayListDoubleToString(input.getMean());
        String min = Converter.convertArrayListDoubleToString(input.getMin());
        String max = Converter.convertArrayListDoubleToString(input.getMax());

        String query = "insert into trainresult values(null, '" + input.getUsername() + "', '"
                + input.getInputtime() + "', '" + input.getDay() + "', '" + input.getTime() + "', '"
                + mean + "', '" + min + "', '" + max + "', '" + input.getThreshold() + "', '" + input.getTargetFeature() + "', '" + input.getClassifier() + "');";
        insert(query);
        Log.e("DBCommand", "TrainResult 테이블에 insert 완료");
    }

    // 학습 데이터 불러오기
    public ArrayList<String> loadTrainResultValues(String name){
        ArrayList<String> data = new ArrayList<String>();
        String str = "";
        String field_name = "*";
        String table_name = "trainresult";

        Cursor cursor = selectWhereName(field_name, table_name, name);
//        Cursor cursor = selecttoTargetName2(field_name, table_name, name);

        while (cursor.moveToNext()){
            data.add(cursor.getString(1));	//username
            data.add(cursor.getString(2));	//inputtime
            data.add(cursor.getString(3));	//day
            data.add(cursor.getString(4));	//time
            data.add(cursor.getString(5));	//mean
            data.add(cursor.getString(6));	//min
            data.add(cursor.getString(7));	//max
            data.add(cursor.getString(8));	//threshold
            data.add(cursor.getString(9));	//targetfeature
            data.add(cursor.getString(10));	//classifier
        }
        cursor.close();
        Log.e("DBCommand", "loadTrainResultValues - success");
        return data;
    }

    private boolean checkDataSize(DataManage dm, int pinCount){
        boolean output = true;

        if (dm.getkeyTime().size() != pinCount){
            output = false;
            Log.e("checkDataSize", "keyTime error, size: " + dm.getkeyTime().size());
        }

        return output;
    }

    // 세팅 테이블 존재 여부 확인
    public String isSetting() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from Setting", null);
        while (cursor.moveToNext()) {
            str += cursor.getInt(0);
        }
        Log.e("DBCommand", "number of field : " + str);

        return str;
    }

    //세팅 불러오기 두번째
    public ArrayList<String> loadSettingValues(){
        ArrayList<String> data = new ArrayList<String>();
        String field_name = "*";
        String table_name = "Setting";
        Cursor cursor = select(field_name, table_name);
        while (cursor.moveToNext()){
            data.clear();                       //마지막 데이터만 저장
            data.add(cursor.getString(1));		//username
            data.add(cursor.getString(2));		//pin
            data.add(cursor.getString(3)); 		//자세구분
            data.add(cursor.getString(4)); 		//학습시 기존 데이터 삭제
            data.add(cursor.getString(5)); 		//정상 사용자 재학습 추가
            data.add(cursor.getString(6)); 		//아웃라이터 삭제
            data.add(cursor.getString(7)); 		//슬라이딩 윈도우
            data.add(cursor.getString(8)); 		//학습 입력 횟수
            data.add(cursor.getString(9)); 		//타겟 특징
            data.add(cursor.getString(10)); 	//분류기
        }
        cursor.close();

        return data;
    }
}
