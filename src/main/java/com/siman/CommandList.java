package main.java.com.siman;

/**
 * Created by Shuichi Tamayose on 2016/03/04.
 */
public class CommandList {
    public String spell;
    public boolean useSkill;
    public ActionInfo[] actions;

    public CommandList() {
        this.useSkill = false;
        this.spell = "";
    }
}
