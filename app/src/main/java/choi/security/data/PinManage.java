package choi.security.data;

import android.util.Log;

/**
 * Created by Nate on 2017-10-16.
 */

public class PinManage {

    String pinNum;
    String inputPin = "";
    int pinCount;
    int CurrentCount = 0;

    public void setPinNum(String input){            // 핀 번호 설정
        pinNum = input;
    }

    public void setPinCount(int input){
        pinCount = input;
    }

    public void setCurrentCount(int input){
        CurrentCount += 1;
    }

    public boolean matchPin(){
        boolean bl = false;
        Log.e("matchPin", "pinNum: " + pinNum + ", inputPin: " + inputPin);
        if (pinNum.equals(inputPin))
            bl = true;
        return bl;
    }

    public boolean lastCount(){
        boolean bl = false;
        if (CurrentCount == pinCount)
            bl = true;
        return bl;
    }

    public void clearInputPin(){
        inputPin = "";
    }

    public void addinputPin(String input){
        inputPin += input;
    }

    public String getinputPin(){
        return inputPin;
    }

    public String getPin(){
        return pinNum;
    }

    public int getPinCount(){
        return pinCount;
    }

    public int getCurrentCount(){
        return CurrentCount;
    }

    public void addCurrentCount(){
        CurrentCount ++;
    }

    public void clearCurrentCount(){
        CurrentCount = 0;
    }
}
