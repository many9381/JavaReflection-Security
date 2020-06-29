package choi.security.keystroke.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Nate on 2017-10-17.
 */

public class Converter {

    public static String convertArrayListStringToString(ArrayList<String> input){
        if (input == null){
            Log.e("Convert", "convertArrayListStringToString error");
            return null;
        }
        String str = "";
        for (int i = 0; i < input.size(); i++) {
            if (i == input.size() - 1)
                str = str + input.get(i);
            else
                str = str + input.get(i) + ",";
        }
        return str;
    }

    public static String convertArrayListDoubleToString(ArrayList<Double> input){
        if (input == null){
            Log.e("Convert", "convertArrayListDoubleToString error");
            return null;
        }
        String str = "";
        for (int i = 0; i < input.size(); i++) {
            if (i == input.size() - 1)
                str = str + input.get(i);
            else
                str = str + input.get(i) + ",";
        }
        return str;
    }

    public static ArrayList<Double> convertStringtoArrayListDouble(String input){
        if (input == null)
            return null;

        // String to ArrayListString
        List<String> string_array = new ArrayList<String>(Arrays.asList(input.split(",")));

        // ArrayListString to ArrayListDouble
        ArrayList<Double> ret = new ArrayList<Double>(string_array.size());
        for (int i = 0; i < string_array.size(); i++) {
            String temp = string_array.get(i);
            double double_t = Double.parseDouble(temp);
            ret.add(double_t);
        }
        return ret;
    }
}

