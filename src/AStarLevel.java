import java.awt.Point;

import apoSkunkman.ai.ApoSkunkmanAIConstants;
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
 * This class contains another data view of the level to provide data
 * 
 * @author Meldanor
 * 
 */
public class AStarLevel {

    // A LEVEL BASED OF THE BYTELEVEL VALUES
    private Node[][] level;

    // LEVEL FROM APOSKUNKMAN
    private byte[][] byteLevel;

    public AStarLevel(ApoSkunkmanAILevel apoLevel) {
        byteLevel = apoLevel.getLevelAsByte();
        level = new Node[byteLevel.length][byteLevel[0].length];
        createLevel();
    }

    // GENERATE THE NODES WITHOUT ANY WEIGHTS
    private void createLevel() {
        // Y-AXIS
        for (int y = 0; y < level.length; ++y)
            // X-AXIS
            for (int x = 0; x < level[y].length; ++x) {
                // IGNORE NOT WALKABLE NODES
                if (byteLevel[y][x] != ApoSkunkmanAIConstants.LEVEL_STONE)
                    level[y][x] = new Node(x, y);
            }
    }

    // UPDATE THE WEIGHTS OF ALL NODES
    public void update(ApoSkunkmanAILevel apoLevel, Point goal) {

        byteLevel = apoLevel.getLevelAsByte();

        // Y-AXIS
        for (int y = 0; y < level.length; ++y) {
            // X-AXIS
            for (int x = 0; x < level[y].length; ++x) {
                Node node = getNode(x, y, false);
                if (node != null) {
                    node = updateNode(node, goal, apoLevel, byteLevel[y][x]);
                    level[y][x] = node;
                }
            }
        }
    }

    // UPDATE NODES COSTS
    private Node updateNode(Node node, Point goal, ApoSkunkmanAILevel apoLevel, byte type) {
        // WHAT IS ON THE POSITION?
        switch (type) {
        // A GOODIE - DIFFER BAD AND GOOD GOODIES
            case ApoSkunkmanAIConstants.LEVEL_GOODIE :
                node.updateGoodieNode(goal, apoLevel.getGoodie(node.y, node.x));
                break;
            // FREE OR BUSH OR BOMB
            case ApoSkunkmanAIConstants.LEVEL_FREE :
            case ApoSkunkmanAIConstants.LEVEL_BUSH :
            case ApoSkunkmanAIConstants.LEVEL_SKUNKMAN :
                node.updateNormalNode(goal, type);
                break;
        }

        return node;
    }

    /**
     * Check if x and y is inside the field and return the node on this
     * position. If not it returns <code>null</code>
     * 
     * @param x
     *            The x position
     * @param y
     *            The x position
     * @return <code>Null</code> when position is outside the field OR there is
     *         a not walkable field on the position. Otherwise the node will
     *         returned
     */
    public Node getNode(int x, int y, boolean onlyFree) {
        // IS INSIDE THE FIELD
        if (isInside(x, y)) {
            // DON'T CARE WHAT TILE IT IS
            if (!onlyFree)
                return level[y][x];
            // RETURN ONLY NODE WHEN WAY IS FREE
            else if (isFree(x, y))
                return level[y][x];
            // NO ACCESSABLE POINT
            else
                return null;
        }
        // IS OUTSIDE
        else
            return null;
    }

    public boolean isInside(int x, int y) {
        return y < level.length && y >= 0 && x < level[y].length && x >= 0;
    }

    public boolean isFree(int x, int y) {
        byte tile = byteLevel[y][x];
        return tile == ApoSkunkmanAIConstants.LEVEL_FREE || tile == ApoSkunkmanAIConstants.LEVEL_GOODIE || tile == ApoSkunkmanAIConstants.LEVEL_SKUNKMAN;
    }

    /**
     * Get and check the neighbors of the point. Neighbors have this format: <br>
     * 
     * <pre>
     *      N
     * N    P    N
     *      N
     * </pre>
     * 
     * @param p
     * @return
     */
    public Node[] getNeighbors(Point p, boolean onlyFree) {
        // @formatter:off
        return new Node[] {
            getNode(p.x + 1, p.y, onlyFree),
            getNode(p.x - 1, p.y, onlyFree),
            getNode(p.x, p.y + 1, onlyFree),
            getNode(p.x, p.y - 1, onlyFree)
        };
        // @formatter:on
    }
}
