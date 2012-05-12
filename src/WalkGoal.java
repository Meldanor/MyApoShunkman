import java.awt.Point;
import java.util.LinkedList;

import apoSkunkman.ai.ApoSkunkmanAILevel;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

/**
 * A goal for walking to a certain position without looking for enemies or
 * bushes
 * 
 * @author Meldanor
 * 
 */
public class WalkGoal extends Goal {

    /** The goal the player walks to */
    protected Point goal;

    /** The player who is walking */
    protected MeldanorPlayer player;

    /** The path to the goal */
    protected LinkedList<Node> path = null;

    /**
     * Create a WalkGoal WITHOUT calculating the way to the goal
     * 
     * @param goal
     *            The goal
     * @param player
     *            The player
     */
    public WalkGoal(final Point goal, final MeldanorPlayer player) {
        this.player = player;
        this.goal = goal;
    }

    /**
     * Create a WalkGoal WITH calculating the way to the goal
     * 
     * @param goal
     *            The goal
     * @param apoLevel
     *            The level
     * @param player
     *            The player
     */
    public WalkGoal(final Point goal, final ApoSkunkmanAILevel apoLevel, final MeldanorPlayer player) {
        this(goal, player);
        path = player.findWay(goal, apoLevel, true);
        // NO WAY FOUND
        if (path == null)
            setCancelled();
    }

    /**
     * @return True when the player has reached the goal point
     */
    @Override
    public boolean isFinished() {
        // FINISHED WHEN PLAYER IS AT GOAL POSITION
        // OR
        // PATH IS INVALID OR EMPTY
        // (PRAY TO THE MACHINE GOD THAT THIS WILL NOT HAPPEN)
        return player.getPosition().equals(goal) || path == null || path.isEmpty();
    }

    /**
     * Walk to the next point on the path
     */
    @Override
    public void process() {
        if (isCancelled())
            return;
        if (path.isEmpty())
            setCancelled();
        else
            // GO TO NEXT POINT
            player.moveTo(path);
    }

    /**
     * @return GoalPriority.LOW
     */
    @Override
    public GoalPriority getPriority() {
        return GoalPriority.LOW;
    }

    /**
     * Calculate the way from the players position to the goal point <br>
     * When no way is found the goal is cancelled!
     * 
     * @param apoLevel
     *            The level
     */
    public void calculateWay(ApoSkunkmanAILevel apoLevel) {
        path = player.findWay(goal, apoLevel, false);
        // NO WAY FOUND
        if (path == null) {
            setCancelled();
            System.out.println("Kein Weg gefunden zu " + goal);
        }
    }
}
