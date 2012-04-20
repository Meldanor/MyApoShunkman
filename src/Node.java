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

    // THE PREVIOUS NODE - IMPORTANT TO CREATE THE PATH
    private Node prev;

    // CASHED HASH - NEVER CHANGE BECAUSE WE DON'T CHANGE X OR Y
    private final int hash;

    public Node(int x, int y) {
        super(x, y);
        // CALCULATE THE HASH BECAUSE IT CAN'T CHANGE
        hash = super.hashCode();
    }

    /**
     * @return Return the path length to this node
     */
    public double getG() {
        return G;
    }

    /**
     * Calculate the values for a normal non goodie node and recalculate F
     * 
     * @param goal
     *            The goal from the path finder
     * @param type
     *            The type of the level tile
     */
    public void updateNormalNode(Point goal, byte type) {
        // CALCULATE HEURISTIC
        // H = DISTANCE TO GOAL * WEIGHT OF LEVEL TYPE
        this.H = goal.distance(x, y) * LevelCosts.getCosts(type);
        calculateF();
    }

    /**
     * Calculate the values for a goodie node and recalculate F
     * 
     * @param goal
     *            The goal from the path finder
     * @param goodie
     *            The goodie on the level tile
     */
    public void updateGoodieNode(Point goal, ApoSkunkmanAILevelGoodie goodie) {

        // CALCULATE HEURISTIC
        // H = DISTANCE TO GOAL * WEIGHT OF LEVEL TYPE
        this.H = goal.distance(x, y) * GoodieCosts.getCosts(goodie.getGoodie());
        calculateF();
    }

    /**
     * <code>
     * F =G + H <br>
     * COST = COSTS FOR THE WAY + HEURISTIC COSTS OF THE NODE</code>
     */
    private void calculateF() {
        this.F = this.H + this.G;
    }

    /**
     * Update the previous node of the node and recalculate G and F
     * 
     * @param prev
     */
    public void setPrev(Node prev) {
        this.prev = prev;
        updateG(prev.getG() + 1.0);
    }

    /**
     * Update the path lengh to this node and recalculate F
     * 
     * @param G
     *            The new path length
     */
    private void updateG(double G) {
        this.G = G;
        calculateF();
    }

    /**
     * @return The previous node of this node
     */
    public Node getPrev() {
        return prev;
    }

    /**
     * This node is less than that node when this has a lower F or if equals are
     * lower H
     * 
     * @param that
     */
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

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        // EQUALS WHEN X AND Y ARE THE SAME
        return super.equals(obj);
    }
}
