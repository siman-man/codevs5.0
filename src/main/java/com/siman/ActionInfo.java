package main.java.com.siman;

/**
 * Created by Shuichi Tamayose on 2016/03/03.
 */
public class ActionInfo {
    public int ninjaY;
    public int ninjaX;
    public int getSoulCount;
    public int targetSoulDist;
    public int dangerValue;
    public boolean valid;
    public boolean moveStone;
    public int createFixStoneCount;
    public char[] commandList;

    public ActionInfo() {
        this.commandList = new char[]{'N'};
        this.valid = true;
        this.getSoulCount = 0;
        this.createFixStoneCount = 0;
        this.moveStone = false;
        this.targetSoulDist = 9999;
        this.dangerValue = 0;
    }

    public boolean isValid() {
        return valid;
    }

    public int toEval() {
        int eval = 0;

        eval += 500 * this.getSoulCount;
        eval -= 3 * this.targetSoulDist;
        eval -= 50 * this.createFixStoneCount;

        eval -= this.dangerValue;

        return eval;
    }
}
