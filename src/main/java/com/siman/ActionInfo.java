package main.java.com.siman;

/**
 * Created by Shuichi Tamayose on 2016/03/03.
 */
public class ActionInfo {
    public int ninjaY;
    public int ninjaX;
    public int getSoulCount;
    public int targetId;
    public int targetDist;
    public int positionValue;
    public boolean valid;
    public boolean moveStone;
    public boolean unreach;
    public boolean notMoveNextCell;
    public String commandList;

    public ActionInfo() {
        this.commandList = "N";
        this.valid = true;
        this.getSoulCount = 0;
        this.notMoveNextCell = false;
        this.moveStone = false;
        this.unreach = false;
        this.positionValue = 0;
        this.targetId = -1;
        this.targetDist = -1;
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
        eval += positionValue;
        eval -= 10 * this.targetDist;
        eval -= (this.notMoveNextCell)? 50 : 0;
        eval -= (this.moveStone)? 5 : 0;

        return eval;
    }

    public String toString() {
        int targetY = this.targetId / Field.WIDTH;
        int targetX = this.targetId % Field.WIDTH;
        return String.format("y = %d, x = %d, eval = %d, com = %s, target = (%d,%d), dist = %d", this.ninjaY, this.ninjaX,
                toEval(), this.commandList, targetY, targetX, this.targetDist);
    }
}
