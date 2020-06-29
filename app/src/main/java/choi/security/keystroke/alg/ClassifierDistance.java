package choi.security.keystroke.alg;

import android.util.Log;

import java.util.ArrayList;

import choi.security.keystroke.data.FeatureManage;
import choi.security.keystroke.data.TrainManage;

/**
 * Created by Nate on 2017-10-17.
 */

public class ClassifierDistance {

    public ArrayList<Double> getMean(FeatureManage fm){
        ArrayList<Double> output = new ArrayList<Double>();
        ArrayList<Double> min = new ArrayList<Double>();
        ArrayList<Double> max = new ArrayList<Double>();
        ArrayList<ArrayList<Double>> scalid_all = new ArrayList<ArrayList<Double>>();

        min = getMin(fm);
        max = getMax(fm);
        double temp = 0.0;

        //min-max 정규화
        for (int i = 0; i < fm.getAll().size(); i++){
            ArrayList<Double> temp_scalid = new ArrayList<Double>();
            for (int j = 0; j < fm.getAll().get(i).size(); j++){
                temp_scalid.add ((fm.getAll().get(i).get(j) - min.get(j)) / (max.get(j) - min.get(j)));
            }
            scalid_all.add(temp_scalid);
        }

        //평균 구하기
        for (int i = 0; i < scalid_all.get(0).size(); i++){                // 각 행의 열 먼저 반복
            double sum = 0.0;
            for (int j = 0; j < scalid_all.size(); j++){
                sum += scalid_all.get(j).get(i);
            }
            output.add(sum/scalid_all.size());
        }
        Log.e("ClassifierDistnace", "getMean: " + output.size() + ", " + output.get(0)+ ", " + output.get(1));
        return output;
    }

    public ArrayList<Double> getMin(FeatureManage fm){
        ArrayList<Double> output = new ArrayList<Double>();
        //min 구하기
        for (int i = 0; i < fm.getAll().get(0).size(); i++){                // 각 행의 열 먼저 반복
            double min = 0.0;
            for (int j = 0; j < fm.getAll().size(); j++){
                if (j == 0 )                                                // 첫 번 째 행부터 시작
                    min = fm.getAll().get(j).get(i);
                else{
                    if (min > fm.getAll().get(j).get(i))
                        min = fm.getAll().get(j).get(i);
                }
            }
            output.add(min);
        }
        Log.e("ClassifierDistnace", "getMin: " + output.size() + ", " + output.get(0)+ ", " + output.get(1));
        return output;
    }

    public ArrayList<Double> getMax(FeatureManage fm){
        ArrayList<Double> output = new ArrayList<Double>();
        //min 구하기
        for (int i = 0; i < fm.getAll().get(0).size(); i++){                // 각 행의 열 먼저 반복
            double max = 0.0;
            for (int j = 0; j < fm.getAll().size(); j++){
                if (j == 0 )                                                // 첫 번 째 행부터 시작
                    max = fm.getAll().get(j).get(i);
                else{
                    if (max < fm.getAll().get(j).get(i))
                        max = fm.getAll().get(j).get(i);
                }
            }
            output.add(max);
            Log.e("getMax", ""+ max);
        }
        Log.e("ClassifierDistnace", "getMax: " + output.size());
        return output;
    }

    public double getThreshold(FeatureManage fm){
        double output = 0.0;
        ArrayList<Double> mean = getMean(fm);
        ArrayList<Double> min = getMin(fm);
        ArrayList<Double> max = getMax(fm);
        ArrayList<ArrayList<Double>> scalid_temp = new ArrayList<ArrayList<Double>>();

        //학습 데이터 정규화
        for (int i = 0; i < fm.getAll().size(); i++){
            ArrayList<Double> temp = new ArrayList<Double>();
            for (int j = 0; j < fm.getAll().get(i).size(); j++){
                temp.add((fm.getAll().get(i).get(j) - min.get(j)) / (max.get(j) - min.get(j)));
            }
            scalid_temp.add(temp);
        }

        //거리 계산 평균
        for (int i = 0; i < scalid_temp.size(); i++){
            double temp = 0;
            for (int j = 0; j < scalid_temp.get(i).get(j); j++){
                temp += Math.abs(mean.get(j) - scalid_temp.get(i).get(j));
            }
            output += temp;
        }
        Log.e("ClassifierDistance", "학습 데이터 수: " + scalid_temp.size() + ", 임계값: " + output/scalid_temp.size());

        double weight = 1.5;
        output = ((output/scalid_temp.size())*100) * weight;
        return output;
    }

    public double calcDistance(TrainManage tm, FeatureManage fm){
        double output = 0.0;
        ArrayList<Double> scalid_all = new ArrayList<Double>();
        //min-max 정규화
        for (int i = 0; i < fm.getAll().get(0).size(); i++){
            scalid_all.add ((fm.getAll().get(0).get(i) - tm.getMin().get(i)) / (tm.getMax().get(i) - tm.getMin().get(i)));
        }

        for (int i = 0; i < tm.getMean().size(); i++)
            output += Math.abs((tm.getMean().get(i) - scalid_all.get(i)));

        return output;
    }
}
