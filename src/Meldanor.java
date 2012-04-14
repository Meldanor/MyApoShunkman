/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

import java.awt.Point;
import java.util.LinkedList;

import apoSkunkman.ai.ApoSkunkmanAI;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;

public class Meldanor extends ApoSkunkmanAI {

    private LinkedList<Node> path;

    @Override
    public String getPlayerName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getAuthor() {
        return "Kilian Gaertner";
    }

    @Override
    public void think(ApoSkunkmanAILevel level, ApoSkunkmanAIPlayer player) {
        if (path == null)
            findWay(level.getLevelAsByte(), player, level);
        else if (!path.isEmpty())
            moveTo(player, path.removeFirst());
        else
            throw new RuntimeException("Empty path but no goal :(");

    }
    private void findWay(byte[][] LinkedList, ApoSkunkmanAIPlayer player, ApoSkunkmanAILevel level) {
        AStar pathFinder = new AStar(LinkedList, new Point((int) player.getX(), (int) player.getY()), level.getGoalXPoint());
        pathFinder.calculate();
        path = pathFinder.getWay();
    }

    private void moveTo(ApoSkunkmanAIPlayer player, Point p) {

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
}
