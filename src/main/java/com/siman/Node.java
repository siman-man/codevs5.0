package main.java.com.siman;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by siman on 3/8/16.
 */
public class Node {
    public int eval;
    public int field;
    ActionInfo info;
    List<String> actionHistory;

    public Node() {
        this.eval = 0;
        actionHistory = new ArrayList<String>();
    }
}

class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node nodeA, Node nodeB) {
        return nodeB.info.toEval() - nodeA.info.toEval();
    }
}