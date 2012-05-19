import java.awt.Point;
import java.lang.reflect.Field;

import apoSkunkman.ApoSkunkmanConstants;
import apoSkunkman.ai.ApoSkunkmanAIConstants;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;
import apoSkunkman.entity.ApoSkunkmanEntity;
import apoSkunkman.entity.ApoSkunkmanPlayer;
import apoSkunkman.entity.ApoSkunkmanStone;
import apoSkunkman.level.ApoSkunkmanLevel;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public class CheatAI implements Tickable {

    // 500 ms is tick
    private long time = System.currentTimeMillis();

    private static final long TICK_TIMER = 1000L;

    private ApoSkunkmanAIPlayer apoPlayer;
    private ApoSkunkmanAILevel apoLevel;

    public CheatAI() {

    }

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.apoPlayer = apoPlayer;
        this.apoLevel = apoLevel;

        long diff = 0L;
        diff = System.currentTimeMillis() - time;

        // ONE TICK = 500 MS
        if (diff > TICK_TIMER) {
            if (apoLevel.getType() == ApoSkunkmanAIConstants.LEVEL_TYPE_GOAL_X)
                cheatGoalX();
            else
                cheatLevel();

            time = System.currentTimeMillis();
        }

    }

    private boolean isFieldClear = false;
    private boolean areTilesRemoved = false;

    private void cheatGoalX() {
        try {
            if (!isFieldClear)
                clearField();
            else if (!areTilesRemoved)
                removeTiles();
//            if (!b) {
//                Class<? extends ApoSkunkmanAIPlayer> clazz = apoPlayer.getClass();
//                Field clazzPlayer = clazz.getDeclaredField("player");
//                clazzPlayer.setAccessible(true);
//                ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) clazzPlayer.get(apoPlayer);
//                player.setX(apoLevel.getGoalXPoint().x * ApoSkunkmanConstants.TILE_SIZE);
//                player.setY(apoLevel.getGoalXPoint().y * ApoSkunkmanConstants.TILE_SIZE);
//            }
//            b = true;
            moveTo(apoLevel.getGoalXPoint());
            moveGoal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveGoal() throws Exception {
        Class<? extends ApoSkunkmanAILevel> apoLevelClass = apoLevel.getClass();
        Field apoLevelField = apoLevelClass.getDeclaredField("level");
        apoLevelField.setAccessible(true);
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        Field pointField = level.getClass().getDeclaredField("goalX");
        pointField.setAccessible(true);
        Point goal = level.getGoalX();

        // CALCULATE DIRECTION
        int xDiff = (int) Math.signum(apoPlayer.getX() - goal.x);
        int yDiff = (int) Math.signum(apoPlayer.getY() - goal.y);
        goal.translate(xDiff, yDiff);
        pointField.set(level, goal);

    }
    private void clearField() throws Exception {
        Class<? extends ApoSkunkmanAILevel> apoLevelClass = apoLevel.getClass();
        Field apoLevelField = apoLevelClass.getDeclaredField("level");
        apoLevelField.setAccessible(true);
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        ApoSkunkmanEntity[][] entityLevel = level.getLevel();
        for (int y = 0; y < entityLevel.length; ++y) {
            for (int x = 0; x < entityLevel[y].length; ++x) {
                if (entityLevel[y][x] != null) {
                    entityLevel[y][x].setIBackground(level.getGrasImage());
                }
            }
        }

        System.out.println("Gras gezeichnet");
        isFieldClear = true;

    }

    private void removeTiles() throws Exception {
        Class<? extends ApoSkunkmanAILevel> apoLevelClass = apoLevel.getClass();
        Field apoLevelField = apoLevelClass.getDeclaredField("level");
        apoLevelField.setAccessible(true);
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        ApoSkunkmanEntity[][] entityLevel = level.getLevel();
        for (int y = 0; y < entityLevel.length; ++y) {
            for (int x = 0; x < entityLevel[y].length; ++x) {
                entityLevel[y][x] = null;
            }
        }

        System.out.println("Feld sauber");
        areTilesRemoved = true;
    }

    public void moveTo(Point p) {

        // CALCULATE DIRECTION
        int diff = p.x - (int) apoPlayer.getX();

        // CHECK X-AXIS
        if (diff > 0) {
            apoPlayer.movePlayerRight();
        } else if (diff < 0) {
            apoPlayer.movePlayerLeft();

        } else {
            // CHECK Y-AXIS
            diff = p.y - (int) apoPlayer.getY();
            if (diff > 0) {
                apoPlayer.movePlayerDown();
            } else if (diff < 0) {
                apoPlayer.movePlayerUp();
            }
        }
    }

    public Point getPosition() {
        return new Point((int) apoPlayer.getX(), (int) apoPlayer.getY());
    }

    private void cheatLevel() {
        // TODO Auto-generated method stub

    }
}
