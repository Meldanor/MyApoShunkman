import java.lang.reflect.Field;
import java.util.Random;

import apoSkunkman.ai.ApoSkunkmanAIEnemy;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;
import apoSkunkman.entity.ApoSkunkmanPlayer;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public class CheatAIBots implements Initiationable, Tickable {

    private boolean isInit = false;

    private ApoSkunkmanAILevel apoLevel;
    private ApoSkunkmanAIPlayer apoPlayer;

    private Field enemyPlayerField = null;
    private Field apoLevelField = null;

    public CheatAIBots() {
        System.out.println("Es geht gegen Bots!");
    }

    @Override
    public void init(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.apoLevel = apoLevel;
        this.apoPlayer = apoPlayer;
        try {
            enemyPlayerField = ApoSkunkmanAIEnemy.class.getDeclaredField("player");
            enemyPlayerField.setAccessible(true);

            apoLevelField = ApoSkunkmanAILevel.class.getDeclaredField("level");
            apoLevelField.setAccessible(true);

            isInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean isInit() {
        return isInit;
    }

    private long time = System.currentTimeMillis();

    private static final long TICK_TIMER = 10L;

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        if (isInit()) {
            this.apoLevel = apoLevel;
            this.apoPlayer = apoPlayer;

            long diff = 0L;
            diff = System.currentTimeMillis() - time;

            // ONE TICK = 20 MS
            if (diff > TICK_TIMER) {
                handleLevel();
                time = System.currentTimeMillis();
            }

        } else {
            init(apoPlayer, apoLevel);
        }
    }

    private void handleLevel() {
        try {
            if (!disallowedBombs)
                disallowBombs();

            if (!slowedBots)
                slowBots();

            setSpeed();

            resetPoints();

            movePlayer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean disallowedBombs = false;

    // EVERY BOT HAS maxSkunksman = 0
    public void disallowBombs() throws Exception {
        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        ApoSkunkmanPlayer enemyPlayer = null;
        Field maxBombsField = ApoSkunkmanPlayer.class.getDeclaredField("maxSkunkman");
        maxBombsField.setAccessible(true);

        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
            maxBombsField.set(enemyPlayer, 0);
        }

        disallowedBombs = true;
    }

    // Every Enemy has only -1337 Points - To Bad for them
    private void resetPoints() throws Exception {
        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        ApoSkunkmanPlayer enemyPlayer = null;

        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
            enemyPlayer.setPoints(-1337);
        }

    }

    private boolean slowedBots = false;

    private void slowBots() throws Exception {
        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        ApoSkunkmanPlayer enemyPlayer = null;
        Field speed = ApoSkunkmanPlayer.class.getDeclaredField("speed");
        speed.setAccessible(true);

        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
            speed.set(enemyPlayer, 0.001f);
        }

        slowedBots = true;

    }

    private void setSpeed() throws Exception {

        Field apoPlayerField = ApoSkunkmanAIPlayer.class.getDeclaredField("player");
        apoPlayerField.setAccessible(true);
        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);
        Field speedField = ApoSkunkmanPlayer.class.getDeclaredField("speed");
        speedField.setAccessible(true);

        speedField.set(player, randomFloat(0.001F, 1F));
    }

    private static final Random rand = new Random();

    private float randomFloat(float pMin, float pMax) {
        return pMin + rand.nextFloat() * (pMax - pMin);

    }

    private boolean right = false;

    private void movePlayer() {

        if (!right)
            apoPlayer.movePlayerRight();
        else
            apoPlayer.movePlayerLeft();

        right = !right;

    }

}
