package choi.security.keystroke.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

//import etri.keystroke.data.FeatureManage;
//import etri.keystroke.util.Converter;

/**
 * Created by Nate on 2017-10-13.
 */

public class KeystrokeDB extends SQLiteOpenHelper {
    public KeystrokeDB(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE Setting( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username STRING,"
                + "pinNum STRING,"
                + "activity BOOLEAN,"
                + "init_trained_data BOOLEAN,"
                + "slidingwindow INTEGER,"
                + "input_count INTEGER,"
                + "retrain_with_positive_data BOOLEAN,"
                + "delete_outlier BOOLEAN,"
                + "targetfeature STRING,"
                + "classifier STRING );");

        Log.e("KeystrokeDB", "Setting 테이블 생성 완료!!");

        db.execSQL("CREATE TABLE rawdata( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username STRING,"
                + "inputtime LONG,"
                + "day STRING,"
                + "time STRING,"
                + "keytime STRING,"
                + "size STRING );");

        Log.e("KeystrokeDB", "rawdata 테이블 생성 완료!!");

        db.execSQL("CREATE TABLE feature( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username STRING,"
                + "inputtime LONG,"
                + "day STRING,"
                + "time STRING,"
                + "key STRING,"
                + "size STRING );");

        Log.e("KeystrokeDB", "feature 테이블 생성 완료!!");

        db.execSQL("CREATE TABLE trainresult( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username STRING,"
                + "inputtime STRING,"
                + "day STRING,"
                + "time STRING,"
                + "mean STRING,"
                + "min STRING,"
                + "max STRING,"
                + "threshold STRING,"
                + "targetfeature STRING,"
                + "classifier STRING );");

        Log.e("KeystrokeDB", "trainresult 테이블 생성 완료!!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createTable(String query){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        Log.e("KeystrokeDB", "테이블 생성 완료!!");
        db.close();
    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
        Log.e("KeystrokeDB", "테이블 삭제 완료");
    }

    //테이름이름으로 조회
    public Cursor select(String field_name, String table_name){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + field_name + " from " + table_name, null);
        return cursor;
    }

    //이름으로 조회
    public Cursor selectWhereName(String field_name, String table_name, String where_name){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + field_name + " from " + table_name + " where username='"+where_name+"'", null);
        return cursor;
    }

    //id로 조회
    public Cursor selectWhereId(String field_name, String table_name, int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + field_name + " from " + table_name + " where _id='" + id+"'", null);
        return cursor;
    }

    public int selecttoWhereLong(String field_name, String table_name, long where_name){
        SQLiteDatabase db = getReadableDatabase();
        int data = 0;
        Cursor cursor = db.rawQuery("select " + field_name + " from " + table_name + " where " + where_name, null);
        while(cursor.moveToNext()){
            data = cursor.getInt(0);
        }
        return data;
    }


    //자세구분없이 학습결과 조회하기
    public Cursor selecttoTargetName(String field_name, String table_name, String where_name){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + field_name + " from " + table_name + " where username='" + where_name +"'", null);
        return cursor;
    }

    //자세구분하여 학습결과 조회하기
    public Cursor selecttoTargetName2(String field_name, String table_name, String where_name, String where_posture){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + field_name + " from " + table_name + " where username='" + where_name +"' and posture='"+where_posture+"'", null);
        return cursor;
    }

    //사용자 이름으로 검색하여 _id 오름차순으로 정렬하여 조회
    public ArrayList<Integer> selecttoWhereNameToID(String field_name, String table_name, String where_name){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Integer> data = new ArrayList<Integer>();
        Cursor cursor = db.rawQuery("select " + field_name + " from " + table_name + " where username='" + where_name +"' order by _id desc", null);
        while(cursor.moveToNext()){
            data.add( cursor.getInt(0));
        }
        return data;
    }

    //이름과 자세로 갖고오기
    public ArrayList<Integer> selecttoWhereNamePosture(String field_name, String table_name, String where_name, String where_posture){
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Integer> data = new ArrayList<Integer>();
        Cursor cursor = db.rawQuery("select " + field_name + " from " + table_name + " where username='"+ where_name
                +"' and posture='" + where_posture +"' order by _id desc", null);
        while(cursor.moveToNext()){
            data.add( cursor.getInt(0));
        }
        return data;
    }
}

