package main.java.com.siman;

/**
 * Created by Shuichi Tamayose on 2016/03/04.
 */
public class CommandList {
    public String spell;
    public boolean useSkill;
    public ActionInfo[] actions;
    public int eval;

    public CommandList() {
        this.eval = 0;
        this.useSkill = false;
        this.spell = "";
    }
}
