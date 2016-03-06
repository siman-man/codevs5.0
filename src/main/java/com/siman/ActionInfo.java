package main.java.com.siman;

/**
 * Created by Shuichi Tamayose on 2016/03/03.
 */
public class ActionInfo {
    public int ninjaY;
    public int ninjaX;
    public int getSoulCount;
    public int targetSoulDist;
    public int baseEval;
    public boolean valid;
    public boolean moveStone;
    public boolean unreach;
    public boolean soulHide;
    public int createFixStoneCount;
    public String commandList;

    public ActionInfo() {
        this.commandList = "N";
        this.valid = true;
        this.getSoulCount = 0;
        this.baseEval = 0;
        this.createFixStoneCount = 0;
        this.moveStone = false;
        this.soulHide = false;
        this.unreach = false;
        this.targetSoulDist = -1;
    }

    public boolean isValid() {
        return valid;
    }

    /**
     * 行動した際の動作からの評価値を出す
     * @return
     */
    public int toEval() {
        int eval = 0;

        eval += 5000 * this.getSoulCount;
        eval -= 5 * this.targetSoulDist;
        eval -= 50 * this.createFixStoneCount;
        eval -= (this.moveStone)? 1 : 0;
        eval -= (this.soulHide)? 100 : 0;

        return eval;
    }
}
