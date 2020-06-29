package choi.security.keystroke.util;

public class Properties2 {
    private String databaseName;		//database 이름

    public Properties2(){
    }

    public void setDatabaseName(String data){
        this.databaseName = data;
        System.setProperty("databaseName", databaseName);
    }

    public String getDatabaseName(){
        String ret;
        ret = System.getProperty("databaseName");
        return ret;
    }
}
