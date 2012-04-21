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

public class WalkGoal extends Goal {

    protected Point goal;

    protected MeldanorPlayer player;

    protected LinkedList<Node> path = null;

    public WalkGoal(final Point goal, final ApoSkunkmanAILevel apoLevel, final MeldanorPlayer player) {
        this.goal = goal;
        this.player = player;
        calculateGoal(goal, apoLevel, player);
    }

    protected void calculateGoal(final Point goal, final ApoSkunkmanAILevel apoLevel, final MeldanorPlayer player) {
        AStar pathFinder = new AStar(apoLevel);
        pathFinder.update(apoLevel, goal);
        pathFinder.findWay(new Point((int) player.apoPlayer.getX(), (int) player.apoPlayer.getY()));
        path = pathFinder.getPath();
    }

    @Override
    public boolean isFinished() {
        // FINISHED WHEN PLAYER IS AT GOAL POSITION
        // OR
        // PATH IS INVALID OR EMPTY
        // (PRAY TO THE MACHINE GOD THAT THIS WILL NOT HAPPEN)
        return player.apoPlayer.getX() == goal.x && player.apoPlayer.getY() == goal.y || path == null || path.isEmpty();
    }

    @Override
    public void process() {
        // GO TO NEXT POINT
        player.moveTo(path.removeFirst());
    }

    @Override
    public GoalPriority getPriority() {
        return GoalPriority.LOW;
    }

}
