package choi.security.data;

import java.util.ArrayList;

/**
 * Created by Nate on 2017-10-16.
 */

public class DataManage {


    private ArrayList<Double> accX = new ArrayList<Double>();
    private ArrayList<Double> accY = new ArrayList<Double>();
    private ArrayList<Double> accZ = new ArrayList<Double>();
    private ArrayList<Long> accT = new ArrayList<Long>();
    private ArrayList<ArrayList<ArrayList<String>>> acc = new ArrayList<ArrayList<ArrayList<String>>>();

    private ArrayList<Double> laccX = new ArrayList<Double>();
    private ArrayList<Double> laccY = new ArrayList<Double>();
    private ArrayList<Double> laccZ = new ArrayList<Double>();
    private ArrayList<Long> laccT = new ArrayList<Long>();
    private ArrayList<ArrayList<ArrayList<String>>> lacc = new ArrayList<ArrayList<ArrayList<String>>>();

    private ArrayList<Double> gyrX = new ArrayList<Double>();
    private ArrayList<Double> gyrY = new ArrayList<Double>();
    private ArrayList<Double> gyrZ = new ArrayList<Double>();
    private ArrayList<Long> gyrT = new ArrayList<Long>();
    private ArrayList<ArrayList<ArrayList<String>>> gyr = new ArrayList<ArrayList<ArrayList<String>>>();

    private ArrayList<Double> ugyrX = new ArrayList<Double>();
    private ArrayList<Double> ugyrY = new ArrayList<Double>();
    private ArrayList<Double> ugyrZ = new ArrayList<Double>();
    private ArrayList<Long> ugyrT = new ArrayList<Long>();
    private ArrayList<ArrayList<ArrayList<String>>> ugyr = new ArrayList<ArrayList<ArrayList<String>>>();


    private ArrayList<Long> keytime = new ArrayList<Long>();
    private ArrayList<ArrayList<String>> keyTime = new ArrayList<ArrayList<String>>();

    private ArrayList<Float> keysize = new ArrayList<Float>();
    private ArrayList<ArrayList<String>> keySize = new ArrayList<ArrayList<String>>();


    private String username = "lsh";     //DeviceID로 교체해서 사용자에게 이름 입력받지 않도록 하면 됨
    private long inputtime;     //currentTimeMillis
    private String day;
    private String time;

    public DataManage(){

    }

    public void setKeytime(ArrayList<Long> input){
        keytime = input;

    }

    //입력 완료 후, 전체 저장 배열 초기화
    public void DataClearAll(){
        acc.clear();
        DataClearAcc();
        DataClearLAcc();
        lacc.clear();
        DataClearGyr();
        gyr.clear();
        DataClearUGyr();
        ugyr.clear();
        keysize.clear();
        keySize.clear();
        keytime.clear();
        keyTime.clear();
    }

    public void DataClearOnce(){
        DataClearAcc();
        DataClearLAcc();
        DataClearGyr();
        DataClearUGyr();
        keysize.clear();
        keytime.clear();
    }

    private void DataClearAcc(){
        accX.clear();
        accY.clear();
        accZ.clear();
        accT.clear();
    }

    private void DataClearLAcc(){
        laccX.clear();
        laccY.clear();
        laccZ.clear();
        laccT.clear();
    }

    private void DataClearGyr(){
        gyrX.clear();
        gyrY.clear();
        gyrZ.clear();
        gyrT.clear();
    }

    private void DataClearUGyr(){
        ugyrX.clear();
        ugyrY.clear();
        ugyrZ.clear();
        ugyrT.clear();
    }


    public boolean checkClear(){
        boolean bl = true;
        bl = checkClearAcc();
        bl = checkClearLAcc();
        bl = checkClearGyr();
        bl = checkClearUGyr();
        return bl;
    }

    private boolean checkClearAcc(){
        boolean bl = true;
        if (accX.isEmpty() == false)
            bl = false;
        if (accY.isEmpty() == false)
            bl = false;
        if (accZ.isEmpty() == false)
            bl = false;
        if (accT.isEmpty() == false)
            bl = false;
        if (keytime.isEmpty() == false)
            bl = false;
        if (keysize.isEmpty() == false)
            bl = false;
        return bl;
    }

    private boolean checkClearLAcc(){
        boolean bl = true;
        if (laccX.isEmpty() == false)
            bl = false;
        if (laccY.isEmpty() == false)
            bl = false;
        if (laccZ.isEmpty() == false)
            bl = false;
        if (laccT.isEmpty() == false)
            bl = false;
        return bl;
    }

    private boolean checkClearGyr(){
        boolean bl = true;
        if (gyrX.isEmpty() == false)
            bl = false;
        if (gyrY.isEmpty() == false)
            bl = false;
        if (gyrZ.isEmpty() == false)
            bl = false;
        if (gyrT.isEmpty() == false)
            bl = false;
        return bl;
    }

    private boolean checkClearUGyr(){
        boolean bl = true;
        if (ugyrX.isEmpty() == false)
            bl = false;
        if (ugyrY.isEmpty() == false)
            bl = false;
        if (ugyrZ.isEmpty() == false)
            bl = false;
        if (ugyrT.isEmpty() == false)
            bl = false;
        return bl;
    }


    public void tempAcc(Double x, Double y, Double z, long t){      //temp는 임시 저장소
        accX.add(x);
        accY.add(y);
        accZ.add(z);
        accT.add(t);
    }

    public void tempLAcc(Double x, Double y, Double z, long t){
        laccX.add(x);
        laccY.add(y);
        laccZ.add(z);
        laccT.add(t);
    }

    public void tempGyr(Double x, Double y, Double z, long t){
        gyrX.add(x);
        gyrY.add(y);
        gyrZ.add(z);
        gyrT.add(t);
    }

    public void tempUGyr(Double x, Double y, Double z, long t){
        ugyrX.add(x);
        ugyrY.add(y);
        ugyrZ.add(z);
        ugyrT.add(t);
    }


    public void tempKeyTime(long t){
        keytime.add(t);
//        Log.e("tempKeyTime", "t, " + t);
    }

    public void tempKeySize(Float t){
        keysize.add(t);
//        Log.e("tempKeysize", "t, " + t);
    }


    public void addOnceData(){          //1회 입력 완료 후, 저장하는 메소드
        addOnceTime();
        addOnceAcc();
        addOnceLAcc();
        addOnceGyr();
        addOnceUGyr();
        addOnceSize();
    }


    private void addOnceTime(){
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < keytime.size(); i++)
            temp.add(keytime.get(i).toString());
        keyTime.add(temp);
    }

    private void addOnceSize(){
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < keysize.size(); i++)
            temp.add(keysize.get(i).toString());
        keySize.add(temp);
    }


    private void addOnceAcc(){          // TimeEvent로 센서 값 추출 후, 배열에 저장
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        ArrayList<String> tempX = new ArrayList<String>();
        ArrayList<String> tempY = new ArrayList<String>();
        ArrayList<String> tempZ = new ArrayList<String>();
        ArrayList<String> tempT = new ArrayList<String>();
        for (int i= 0; i< accT.size(); i++){
            if (keytime.get(0) <= accT.get(i) && keytime.get(keytime.size()-1) >= accT.get(i)){
                tempX.add(accX.get(i).toString());
                tempY.add(accY.get(i).toString());
                tempZ.add(accZ.get(i).toString());
                tempT.add(accT.get(i).toString());
            }
        }
        temp.add(tempX);
        temp.add(tempY);
        temp.add(tempZ);
        temp.add(tempT);
        acc.add(temp);
    }

    private void addOnceLAcc(){         // TimeEvent로 센서 값 추출 후, 배열에 저장
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        ArrayList<String> tempX = new ArrayList<String>();
        ArrayList<String> tempY = new ArrayList<String>();
        ArrayList<String> tempZ = new ArrayList<String>();
        ArrayList<String> tempT = new ArrayList<String>();
        for (int i= 0; i< laccT.size(); i++){
            if (keytime.get(0) <= laccT.get(i) && keytime.get(keytime.size()-1) >= laccT.get(i)){
                tempX.add(laccX.get(i).toString());
                tempY.add(laccY.get(i).toString());
                tempZ.add(laccZ.get(i).toString());
                tempT.add(laccT.get(i).toString());
            }
        }
        temp.add(tempX);
        temp.add(tempY);
        temp.add(tempZ);
        temp.add(tempT);
        lacc.add(temp);
    }

    private void addOnceGyr(){      // TimeEvent로 센서 값 추출 후, 배열에 저장
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        ArrayList<String> tempX = new ArrayList<String>();
        ArrayList<String> tempY = new ArrayList<String>();
        ArrayList<String> tempZ = new ArrayList<String>();
        ArrayList<String> tempT = new ArrayList<String>();
        for (int i= 0; i< gyrT.size(); i++){
            if (keytime.get(0) <= gyrT.get(i) && keytime.get(keytime.size()-1) >= gyrT.get(i)){
                tempX.add(gyrX.get(i).toString());
                tempY.add(gyrY.get(i).toString());
                tempZ.add(gyrZ.get(i).toString());
                tempT.add(gyrT.get(i).toString());
            }
        }
        temp.add(tempX);
        temp.add(tempY);
        temp.add(tempZ);
        temp.add(tempT);
        gyr.add(temp);
    }

    private void addOnceUGyr(){     // TimeEvent로 센서 값 추출 후, 배열에 저장
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        ArrayList<String> tempX = new ArrayList<String>();
        ArrayList<String> tempY = new ArrayList<String>();
        ArrayList<String> tempZ = new ArrayList<String>();
        ArrayList<String> tempT = new ArrayList<String>();
        for (int i= 0; i< ugyrT.size(); i++){
            if (keytime.get(0) <= ugyrT.get(i) && keytime.get(keytime.size()-1) >= ugyrT.get(i)){
                tempX.add(ugyrX.get(i).toString());
                tempY.add(ugyrY.get(i).toString());
                tempZ.add(ugyrZ.get(i).toString());
                tempT.add(ugyrT.get(i).toString());
            }
        }
        temp.add(tempX);
        temp.add(tempY);
        temp.add(tempZ);
        temp.add(tempT);
        ugyr.add(temp);
    }




    public String getUsername(){
        return username;
    }

    public ArrayList<ArrayList<ArrayList<String>>> getAcc(){
        return acc;
    }

    public ArrayList<ArrayList<ArrayList<String>>> getLAcc(){
        return lacc;
    }

    public ArrayList<ArrayList<ArrayList<String>>> getGyr(){
        return gyr;
    }

    public ArrayList<ArrayList<ArrayList<String>>> getUGyr(){
        return ugyr;
    }


    public ArrayList<ArrayList<String>> getkeyTime(){
        return keyTime;
    }

    public ArrayList<ArrayList<String>> getkeySize(){
        return keySize;
    }


    public void setUsername(String input){
        username = input;
    }
}

