import java.lang.reflect.Field;

import Stinker.StinkerMain;
import apoSkunkman.ai.ApoSkunkmanAI;
import apoSkunkman.ai.ApoSkunkmanAIConstants;
import apoSkunkman.ai.ApoSkunkmanAIEnemy;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;
import apoSkunkman.ai.ai.ApoAILeftRight;
import apoSkunkman.ai.ai.ApoAIRunner;
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

public class CheatAI implements Tickable, Initiateable {

    private boolean isInit = false;
    private Tickable cheatAIHandler;

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {

        if (isInit)
            cheatAIHandler.tick(apoPlayer, apoLevel);
        else
            init(apoPlayer, apoLevel);

    }

    @Override
    public void init(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        if (apoLevel.getType() == ApoSkunkmanAIConstants.LEVEL_TYPE_GOAL_X)
            cheatAIHandler = new CheatAIGoalX();
        else if (apoLevel.getType() == ApoSkunkmanAIConstants.LEVEL_TYPE_STANDARD && areBots(apoLevel.getEnemies()))
            cheatAIHandler = new CheatAIBots();
        else
            cheatAIHandler = new CheatAIHuman();
        this.isInit = true;

    }

    // GETTING THE AI OF ALL PLAYERS AND CHECK WHETHER THEY EXTENDS A STANDARD
    // AI OR ARE HUMANS
    private boolean areBots(ApoSkunkmanAIEnemy[] enemies) {

        try {
            Field enemyPlayerField = ApoSkunkmanAIEnemy.class.getDeclaredField("player");
            enemyPlayerField.setAccessible(true);
            ApoSkunkmanPlayer enemyPlayer = null;
            ApoSkunkmanAI ai = null;
            for (ApoSkunkmanAIEnemy apoSkunkmanAIEnemy : enemies) {
                enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(apoSkunkmanAIEnemy);
                ai = enemyPlayer.getAi();
                // AI IS NOT A STANDARD AI - MUST BE HUMAN
                if (!(ai instanceof ApoAILeftRight || ai instanceof ApoAIRunner || ai instanceof StinkerMain))
                    return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean isInit() {
        return isInit;
    }

}
