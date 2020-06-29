package choi.security.keystroke.data;

import java.util.ArrayList;

import choi.security.keystroke.alg.ClassifierDistance;
import choi.security.keystroke.util.Converter;

/**
 * Created by Nate on 2017-10-17.
 */

public class TrainManage {
    private String username;
    private long inputtime;
    private String day;
    private String time;

    private String targetFeature;
    private String classifier;

    private ArrayList<Double> mean = new ArrayList<Double>();
    private ArrayList<Double> min = new ArrayList<Double>();
    private ArrayList<Double> max = new ArrayList<Double>();
    private double threshold;

    private ClassifierDistance cd = new ClassifierDistance();           // 거리 계산 클래스


    public TrainManage(){

    }

    public void insertTrainData(FeatureManage fm, setManage setting){
        username = setting.getUsername();
        mean = cd.getMean(fm);
        min = cd.getMin(fm);
        max = cd.getMax(fm);
        threshold = cd.getThreshold(fm);
        targetFeature = setting.getTargetfeature();
        classifier = setting.getClassifier();
    }

    public ArrayList<Double> getMean(){
        return mean;
    }

    public ArrayList<Double> getMin(){
        return min;
    }

    public ArrayList<Double> getMax(){
        return max;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public long getInputtime() {
        return inputtime;
    }
    public void setInputtime(long inputtime) {
        this.inputtime = inputtime;
    }
    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getTargetFeature() {
        return targetFeature;
    }
    public void setTargetFeature(String targetFeature) {
        this.targetFeature = targetFeature;
    }
    public String getClassifier() {
        return classifier;
    }
    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }
    public double getThreshold() {
        return threshold;
    }
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
    public void setMean(String input){
        this.mean = Converter.convertStringtoArrayListDouble(input);
    }

    public void setMin(String input){
        this.min = Converter.convertStringtoArrayListDouble(input);
    }

    public void setMax(String input){
        this.max = Converter.convertStringtoArrayListDouble(input);
    }


}
