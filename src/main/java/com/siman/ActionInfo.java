package main.java.com.siman;

/**
 * Created by Shuichi Tamayose on 2016/03/03.
 */
public class ActionInfo {
    public int getSoulCount;
    public int targetSoulDist;
    public int dangerValue;
    public boolean valid;
    public char[] commandList;

    public ActionInfo() {
        this.commandList = new char[]{'N'};
        this.valid = true;
        this.getSoulCount = 0;
        this.targetSoulDist = 9999;
        this.dangerValue = 0;
    }

    public boolean isValid() {
        return valid;
    }
}
