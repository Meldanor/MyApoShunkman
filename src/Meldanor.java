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
        if (path == null) {
            findWay(player, level);
            System.out.println("Way found!");
        } else if (!path.isEmpty()) {
            Node n = path.removeFirst();
            System.out.println((int) player.getX() + "," + (int) player.getY() + " zu " + n.x + "," + n.y);
            moveTo(player, n);
        } else
            path = null;
        // DANCE OR SO

    }
    private void findWay(ApoSkunkmanAIPlayer player, ApoSkunkmanAILevel level) {
        AStar pathFinder = new AStar(level);
        pathFinder.update(level, level.getGoalXPoint());
        pathFinder.findWay(new Point((int) player.getX(), (int) player.getY()));
        path = pathFinder.getPath();
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
