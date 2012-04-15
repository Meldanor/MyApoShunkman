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

    private Node[][] level;

    public AStarLevel(ApoSkunkmanAILevel level, Point goal) {
        byte[][] byteLevel = level.getLevelAsByte();
        this.level = new Node[byteLevel.length][byteLevel[0].length];
        createLevel();
    }

    private void createLevel() {
        // Y-AXIS
        for (int y = 0; y < this.level.length; ++y)
            // X-AXIS
            for (int x = 0; x < this.level[y].length; ++x)
                level[y][x] = new Node(x, y);

    }

    // CALL THIS EVERY TICK TO UPDATE TO LEVEL AND PREPARE EVERYTHING FOR A*
    public void update(ApoSkunkmanAILevel apoLevel, byte[][] byteLevel, Point goal) {

        // Y-AXIS
        for (int y = 0; y < this.level.length; ++y) {
            // X-AXIS
            for (int x = 0; x < this.level[y].length; ++x) {
                Node node = getNode(x, y);
                // IS GOODIE
                if (byteLevel[y][x] == ApoSkunkmanAIConstants.LEVEL_GOODIE)
                    node.updateGoodieNode(goal, apoLevel.getGoodie(y, x));
                // NORMAL
                else
                    node.updateNormalNode(goal, byteLevel[y][x]);
            }
        }
    }

    public Node getNode(int x, int y) {
        return level[y][x];
    }

}
