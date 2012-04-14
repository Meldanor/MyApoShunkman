import java.awt.Point;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

@SuppressWarnings("serial")
public class Node extends Point implements Comparable<Node> {

    // STEPS ON THE PATH
    private int G;
    // HEURISTIC
    private double H;

    private double F;

    private Node prev;

    public Node(int x, int y) {
        super(x, y);
    }

    public Node(int x, int y, int G, Point goal, byte[][] level) {
        this(x, y);
        this.G = G;
        // CALCULATE HEURISTIC - DISTANCE * VALUE
        H = goal.distance(x, y) * Costs.getCosts(level[y][x]);
        F = G + H;
    }

    public int getG() {
        return G;
    }

    @Override
    public int compareTo(Node o) {
        // LOWER F IS BETTER
        if (this.F < o.F)
            return -1;
        else if (this.F > o.F)
            return +1;
        // IF F IS THE SAME LOWER H IS BETTEr
        else
            return (int) (this.H - o.H);

    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Node))
            return false;
        if (obj == this)
            return true;

        return super.equals(obj);

    }

    @Override
    public String toString() {
        return super.toString();
    }
}
