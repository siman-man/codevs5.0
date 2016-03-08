package main.java.com.siman;

import java.util.Comparator;

/**
 * Created by siman on 3/8/16.
 */
public class Node {
    public int eval;
    public int field;

    public Node() {
        this.eval = 0;
    }
}

class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node nodeA, Node nodeB) {
        return nodeA.eval - nodeB.eval;
    }
}