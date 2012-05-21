import java.lang.reflect.Field;

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

public class CheatAIBots implements Initiateable, Tickable {

    private boolean isInit = false;

    private ApoSkunkmanAILevel apoLevel;
    private ApoSkunkmanAIPlayer apoPlayer;

    private Field enemyPlayerField = null;

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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean isInit() {
        return isInit;
    }

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        if (isInit()) {
            this.apoLevel = apoLevel;
            this.apoPlayer = apoPlayer;

        } else {
            init(apoPlayer, apoLevel);
        }
    }

    public void killEnemies() throws Exception {
        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        ApoSkunkmanPlayer enemyPlayer = null;
        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemyPlayer);
            enemyPlayer.setLaySkunkman(true);
        }
    }
}
