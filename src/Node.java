import java.awt.Point;

import apoSkunkman.ai.ApoSkunkmanAILevelGoodie;

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

    // WAY TO GOAL
    private double G = 0.0;
    // ESTIMATED COSTS
    private double H = 0.0;
    // SUM OF G AND H
    private double F = 0.0;

    private Node prev;

    public Node(int x, int y) {
        super(x, y);
    }

    public double getG() {
        return G;
    }

    public void updateG(double G) {
        this.G = G;
        calculateF();
    }

    public void updateNormalNode(Point goal, byte type) {
        // CALCULATE HEURISTIC
        // H = DISTANCE TO GOAL * WEIGHT OF LEVEL TYPE
        this.H = goal.distance(x, y) * Costs.getCosts(type);
        calculateF();
    }

    public void updateGoodieNode(Point goal, ApoSkunkmanAILevelGoodie goodie) {

        // CALCULATE HEURISTIC
        // H = DISTANCE TO GOAL * WEIGHT OF LEVEL TYPE
        this.H = goal.distance(x, y) * GoodieCosts.getCosts(goodie.getGoodie());
        calculateF();
    }

    private void calculateF() {
        this.F = this.H + this.G;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
        updateG(prev.getG() + 1.0);
    }

    public Node getPrev() {
        return prev;
    }

    @Override
    public int compareTo(Node that) {
        // THIS IS BETTER
        if (this.F < that.F)
            return -1;
        // THAT IS BETTER
        else if (this.F > that.F)
            return 1;
        // BOTH HAVE SAME F SO "H" IS IMPORTANT
        else
            return (int) (this.H - that.H);

    }

}
