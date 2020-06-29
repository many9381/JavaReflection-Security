package choi.security.data;

import android.util.Log;

import java.util.ArrayList;

import choi.security.util.Converter;

/**
 * Created by Nate on 2017-10-17.
 */

public class FeatureManage {

    private String username;
    private long inputtime;     //currentTimeMillis
    private String day;
    private String time;

    private ArrayList<ArrayList<Double>> key;
    private ArrayList<ArrayList<Double>> size;
    private ArrayList<ArrayList<Double>> acc;
    private ArrayList<Double> lacc;
    private ArrayList<Double> gyr;
    private ArrayList<Double> ugyr;

    private ArrayList<ArrayList<Double>> all;

    public FeatureManage(){

    }

    public FeatureManage(DataManage dm, setManage setting){         //추후 세팅값만 전달받아서 DB에서 sliding window 크기만큼 불러와서 dm에 저장하고 특징 추출하도록 변경 필요
        username = dm.getUsername();
        key = extractKeyFeature(dm.getkeyTime(), setting.getPinCount());
        size = extractSizeFeature(dm.getkeySize(), setting.getPinCount());
        acc = extractAccFeature(dm.getAcc(), setting.getPinCount());
        all = addAllfeatures(key, size);
        Log.e("FeatureManage", "allFeature size: " + all.size());
    }

    public void setUsername(String name){
        username = name;
    }
    public void setKey(String input){
        ArrayList<Double> temp = new ArrayList<Double>();
        temp = Converter.convertStringtoArrayListDouble(input);
        key.add(temp);
    }
    public void setSize(String input){
        ArrayList<Double> temp = new ArrayList<Double>();
        temp = Converter.convertStringtoArrayListDouble(input);
        size.add(temp);
    }

    private ArrayList<ArrayList<Double>> addAllfeatures(ArrayList<ArrayList<Double>> key, ArrayList<ArrayList<Double>> size){
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
        //각 특징별로 모두 동일한 사이즈(윈도우 사이즈 크기)인지 확인하기 추가 필요

        for (int i = 0; i < key.size(); i++){
            ArrayList<Double> temp = new ArrayList<Double>();
            for (int j = 0; j < key.get(i).size(); j++){
                temp.add(key.get(i).get(j));
            }
            for (int j = 0; j < size.get(i).size(); j++){
                temp.add(size.get(i).get(j));
            }

            output.add(temp);
        }
        return output;
    }

    public ArrayList<ArrayList<Double>> getAll(){
        return all;
    }

    private ArrayList<ArrayList<Double>> extractKeyFeature(ArrayList<ArrayList<String>> keyTime, int pinCount){
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
        if (keyTime.size() == pinCount){
            for (int i = 0; i < keyTime.size(); i++){
                ArrayList<Double> temp = new ArrayList<Double>();
                for (int j = 0; j < keyTime.get(i).size(); j+= 2){
                    temp.add(Double.valueOf(keyTime.get(i).get(j+1)) - Double.valueOf(keyTime.get(i).get(j)));          // DU 특징
                }
                for (int j = 1; j < keyTime.get(i).size()-1; j+= 2){
                    temp.add(Double.valueOf(keyTime.get(i).get(j+1)) - Double.valueOf(keyTime.get(i).get(j)));          // UD 특징
                }
                for (int j = 1; j < keyTime.get(i).size()-1; j+= 2){
                    temp.add(Double.valueOf(keyTime.get(i).get(j+2)) - Double.valueOf(keyTime.get(i).get(j)));          // UU 특징    // 추가 부분
                }
                for (int j = 0; j < keyTime.get(i).size()-2; j+= 2){
                    temp.add(Double.valueOf(keyTime.get(i).get(j+2)) - Double.valueOf(keyTime.get(i).get(j)));          // DD 특징    // 추가 부분
                }
                output.add(temp);
            }
        }
        else{
            Log.e("FeatureManage", "extractKeyFeature Error - size");
        }
        return output;
    }

    private ArrayList<ArrayList<Double>> extractAccFeature(ArrayList<ArrayList<ArrayList<String>>> acc, int pinCount){
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();

        return output;
    }
    // size 특징 추출
    private ArrayList<ArrayList<Double>> extractSizeFeature(ArrayList<ArrayList<String>> keySize, int pinCount){
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
        if (keySize.size() == pinCount){
            for (int i = 0; i < keySize.size(); i++){
                ArrayList<Double> temp = new ArrayList<Double>();
                for (int j = 0; j < keySize.get(i).size(); j++)
                    temp.add(Double.valueOf(keySize.get(i).get(j)));          // rawdata 그대로 사용
                output.add(temp);
            }
        }
        else{
            Log.e("FeatureManage", "extractSizeFeature Error - size");
        }
        return output;
    }

    // xy 특징 추출
    private ArrayList<ArrayList<Double>> extractXYFeature(ArrayList<ArrayList<String>> keyXY, int pinCount){
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
        if (keyXY.size() == pinCount){
            for (int i = 0; i < keyXY.size(); i++){
                ArrayList<Double> temp = new ArrayList<Double>();
                for (int j = 0; j < keyXY.get(i).size(); j++)
                    temp.add(Double.valueOf(keyXY.get(i).get(j)));          // rawdata 그대로 사용
                output.add(temp);
            }
        }
        else{
            Log.e("FeatureManage", "extractSizeFeature Error - size");
        }
        return output;
    }

    public String getUsername(){
        return username;
    }

    public ArrayList<ArrayList<Double>> getKeyTimeFeautre(){
        return key;
    }

    public ArrayList<ArrayList<Double>> getSizeFeature(){
        return size;
    }


}
