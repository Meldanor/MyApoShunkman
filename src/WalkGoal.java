import java.awt.Point;
import java.util.LinkedList;

import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public class WalkGoal extends Goal {

    protected final Point goal;

    protected final ApoSkunkmanAIPlayer player;

    protected LinkedList<Node> path = null;

    public WalkGoal(final Point goal, final ApoSkunkmanAILevel apoLevel, final ApoSkunkmanAIPlayer player) {
        this.goal = goal;
        this.player = player;
        calculateGoal(goal, apoLevel, player);
    }

    private void calculateGoal(final Point goal, final ApoSkunkmanAILevel apoLevel, final ApoSkunkmanAIPlayer player) {
        AStar pathFinder = new AStar(apoLevel);
        pathFinder.update(apoLevel, goal);
        pathFinder.findWay(new Point((int) player.getX(), (int) player.getY()));
        path = pathFinder.getPath();
    }

    @Override
    public boolean isFinished() {
        // FINISHED WHEN PLAYER IS AT GOAL POSITION
        // OR
        // PATH IS INVALID OR EMPTY
        // (PRAY TO THE MACHINE GOD THAT THIS WILL NOT HAPPEN)
        return player.getX() == goal.x && player.getY() == goal.y || path == null || path.isEmpty();
    }

    @Override
    public void process() {
        // GO TO NEXT POINT
        moveNext(player);
    }

    protected void moveNext(ApoSkunkmanAIPlayer player) {
        Point p = path.removeFirst();

        // CALCULATE DIRECTION
        int diff = p.x - (int) player.getX();

        // CHECK X-AXIS
        if (diff > 0)
            player.movePlayerRight();
        else if (diff < 0)
            player.movePlayerLeft();
        else {
            // CHECK Y-AXIS
            diff = p.y - (int) player.getY();
            if (diff > 0)
                player.movePlayerDown();
            else if (diff < 0)
                player.movePlayerUp();
        }
    }

    @Override
    public GoalPriority getPriority() {
        return GoalPriority.LOW;
    }

}
