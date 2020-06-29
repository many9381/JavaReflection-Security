package choi.security.data;

public class setManage {
    private String username;
    private String pinNum;
    private int pinCount;
    private int CurrentCount;

    private boolean activity;
    private boolean init_trained_data;
    private int slidingwindow;
    private boolean retrain_with_positive_data;
    private boolean delete_outlier;
    private String targetfeature;
    private String classifier;

    public setManage(){
        username = "";
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String input){
        username = input;
    }

    public void setPinNum(String input){
        pinNum = input;
    }
    public String getPinNum(){
        return pinNum;
    }

    public void setPinCount(int input){
        pinCount = input;
    }
    public int getPinCount(){
        return pinCount;
    }

    public void setCurrentCount(int input){
        CurrentCount = input;
    }
    public int getCurrentCount(){
        return CurrentCount;
    }

    public boolean getActivity(){
        return activity;
    }
    public void setActivity(boolean input){
        activity = input;
    }
    public boolean getLearned_data_delete(){
        return init_trained_data;
    }
    public void setLearned_data_delete(boolean input){
        init_trained_data = input;
    }
    public int getSlidingwindow(){
        return slidingwindow;
    }
    public void setSlidingwindow(int input){
        slidingwindow = input;
    }

    public boolean getRetrain_with_positive_data(){
        return retrain_with_positive_data;
    }
    public void setAdd_positive_data(boolean input){
        retrain_with_positive_data = input;
    }
    public boolean getOutlier_delete(){
        return delete_outlier;
    }
    public void setOutlier_delete(boolean input){
        delete_outlier = input;
    }
    public String getTargetfeature(){
        return targetfeature;
    }
    public void setTargetfeatrue(String input){
        targetfeature = input;
    }
    public String getClassifier(){
        return classifier;
    }
    public void setClassifier(String input){
        classifier = input;
    }
}

