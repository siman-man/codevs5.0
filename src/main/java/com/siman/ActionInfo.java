package main.java.com.siman;

/**
 * Created by Shuichi Tamayose on 2016/03/03.
 */
public class ActionInfo {
    public int getSoulCount;
    public int targetSoulDist;
    public int dangerValue;
    public boolean valid;
    public char[] action;

    public ActionInfo() {
        this.action = new char[]{'N'};
    }

    public boolean isValid() {
        return valid;
    }
}
