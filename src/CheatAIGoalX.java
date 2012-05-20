import java.awt.Point;
import java.lang.reflect.Field;

import apoSkunkman.ApoSkunkmanConstants;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;
import apoSkunkman.entity.ApoSkunkmanBush;
import apoSkunkman.entity.ApoSkunkmanEntity;
import apoSkunkman.entity.ApoSkunkmanGoodie;
import apoSkunkman.game.ApoSkunkmanPanel;
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

public class CheatAIGoalX implements Tickable, Initiateable {

    private ApoSkunkmanAIPlayer apoPlayer;
    private ApoSkunkmanAILevel apoLevel;

    private boolean isInit = false;

    // 500 ms is tick
    private long time = System.currentTimeMillis();

    private static final long TICK_TIMER = 500L;

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {

        if (isInit()) {
            long diff = 0L;
            diff = System.currentTimeMillis() - time;

            // ONE TICK = 500 MS
            if (diff > TICK_TIMER) {
                cheatGoalX();
                time = System.currentTimeMillis();
            }
        } else
            init(apoPlayer, apoLevel);
    }

    @Override
    public void init(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.apoLevel = apoLevel;
        this.apoPlayer = apoPlayer;
        try {

            Class<? extends ApoSkunkmanAILevel> apoLevelClass = apoLevel.getClass();
            apoLevelField = apoLevelClass.getDeclaredField("level");
            apoLevelField.setAccessible(true);
            goalPointField = ApoSkunkmanLevel.class.getDeclaredField("goalX");
            goalPointField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        isInit = true;

    }

    @Override
    public boolean isInit() {
        return isInit;
    }

    private boolean isFieldClear = false;

    private void cheatGoalX() {
        try {
            if (!isFieldClear)
                clearField();
            moveTo(apoLevel.getGoalXPoint());
            moveGoal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Field goalPointField = null;
    private Field apoLevelField = null;

    private void moveGoal() throws Exception {
        if (goalPointField == null || apoLevelField == null) {

        }
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        Point goal = (Point) goalPointField.get(level);
        // CALCULATE DIRECTION
        int xDiff = (int) Math.signum(apoPlayer.getX() - goal.x);
        int yDiff = (int) Math.signum(apoPlayer.getY() - goal.y);
        goal.translate(xDiff, yDiff);
        goalPointField.set(level, goal);
    }

    private void clearField() throws Exception {
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        ApoSkunkmanEntity[][] entityLevel = level.getLevel();
        for (int y = 0; y < entityLevel.length; ++y) {
            for (int x = 0; x < entityLevel[y].length; ++x) {
                entityLevel[y][x] = new TrollEntity(level.getGrasImage(), x, y);
            }
        }

        System.out.println("Gras gezeichnet");
        isFieldClear = true;

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
}
