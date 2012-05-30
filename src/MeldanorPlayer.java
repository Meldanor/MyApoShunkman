import java.awt.Point;
import java.util.LinkedList;

import apoSkunkman.ai.ApoSkunkmanAIConstants;
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

/**
 * Wrapper for {@link ApoSkunkmanAIPlayer} player to have usefull methods
 * 
 * @author Meldanor
 * 
 */
public class MeldanorPlayer {

    /** The apo player this MeldanorPlayer is wrapping around */
    public ApoSkunkmanAIPlayer apoPlayer;

    /** The direction the player has walked */
    private int direction = Integer.MIN_VALUE;

    public MeldanorPlayer(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.update(apoPlayer);
    }

    /**
     * Remove the first node of the path and go to this node
     * 
     * @param path
     *            A path where the distance of every node is only 1
     */
    public void moveTo(LinkedList<Node> path) {
        moveTo(path.removeFirst());
    }

    /**
     * Go one step to the point
     * 
     * @param p
     */
    public void moveTo(Point p) {

        // CALCULATE DIRECTION
        int diff = p.x - (int) apoPlayer.getX();

        // CHECK X-AXIS
        if (diff > 0) {
            apoPlayer.movePlayerRight();
            direction = ApoSkunkmanAIConstants.PLAYER_DIRECTION_RIGHT;
        } else if (diff < 0) {
            apoPlayer.movePlayerLeft();
            direction = ApoSkunkmanAIConstants.PLAYER_DIRECTION_LEFT;

        } else {
            // CHECK Y-AXIS
            diff = p.y - (int) apoPlayer.getY();
            if (diff > 0) {
                apoPlayer.movePlayerDown();
                direction = ApoSkunkmanAIConstants.PLAYER_DIRECTION_DOWN;
            } else if (diff < 0) {
                apoPlayer.movePlayerUp();
                direction = ApoSkunkmanAIConstants.PLAYER_DIRECTION_UP;
            }
        }
    }

    public void goBack() {
        switch (direction) {
            case ApoSkunkmanAIConstants.PLAYER_DIRECTION_RIGHT :
                apoPlayer.movePlayerLeft();
                break;
            case ApoSkunkmanAIConstants.PLAYER_DIRECTION_LEFT :
                apoPlayer.movePlayerRight();
                break;
            case ApoSkunkmanAIConstants.PLAYER_DIRECTION_DOWN :
                apoPlayer.movePlayerUp();
                break;
            case ApoSkunkmanAIConstants.PLAYER_DIRECTION_UP :
                apoPlayer.movePlayerDown();
                break;
            default :
                System.out.println("Kann nicht rückwärts gehen :(");
                break;

        }
        direction = Integer.MIN_VALUE;
    }

    public LinkedList<Node> findWay(final Point goal, final ApoSkunkmanAILevel apoLevel, boolean onlyFree) {
        AStar pathFinder = new AStar(apoLevel);
        pathFinder.update(apoLevel, goal);
        pathFinder.findWay(getPosition(), onlyFree);
        return pathFinder.getPath();
    }

    public Point getPosition() {
        return new Point(apoPlayer.getPlayerX(), apoPlayer.getPlayerY());
    }

    public void update(ApoSkunkmanAIPlayer apoPlayer) {
        this.apoPlayer = apoPlayer;
    }

    /**
     * @return The last walk direction. Integer.MIN_VALUE if player has never
     *         walked
     */
    public int getDirection() {
        return direction;
    }

}
