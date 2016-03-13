package main.java.com.siman;

/**
 * Created by Shuichi Tamayose on 2016/03/03.
 */
public class ActionInfo {
    public int ninjaY;
    public int ninjaX;
    public int getSoulCount;
    public int getSoulCountFirst;
    public int targetId;
    public int alivePathCount;
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
        this.alivePathCount = 0;
        this.getSoulCountFirst = 0;
        this.notMoveNextCell = false;
        this.moveStone = false;
        this.unreach = false;
        this.positionValue = 0;
        this.targetId = -1;
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

        eval += 25000 * this.getSoulCount;
        eval += 25000 * this.getSoulCountFirst;
        eval += this.positionValue;
        eval -= (this.notMoveNextCell)? 50 : 0;

        if (this.getSoulCountFirst + this.getSoulCount > 0) {
            eval -= (this.moveStone)? 10000 : 0;
        } else {
            eval -= (this.moveStone)? 100 : 0;
        }

        return eval;
    }

    public String toString() {
        int targetY = this.targetId / Field.WIDTH;
        int targetX = this.targetId % Field.WIDTH;
        return String.format("ap = %d, fs = %d, soul = %d, y = %d, x = %d, eval = %d, com = %s, target = (%d,%d)",
                this.alivePathCount, this.getSoulCountFirst, this.getSoulCount, this.ninjaY, this.ninjaX,
                toEval(), this.commandList, targetY, targetX);
    }

    public void sum(ActionInfo info) {
        this.getSoulCount += info.getSoulCount;
        this.getSoulCountFirst = info.getSoulCountFirst;
        this.moveStone |= info.moveStone;
        this.notMoveNextCell |= info.notMoveNextCell;
    }
}
