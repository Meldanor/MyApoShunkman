import java.lang.reflect.Field;
import java.util.ArrayList;

import Stinker.StinkerMain;
import apoSkunkman.ai.ApoSkunkmanAI;
import apoSkunkman.ai.ApoSkunkmanAIConstants;
import apoSkunkman.ai.ApoSkunkmanAIEnemy;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;
import apoSkunkman.ai.ai.ApoAILeftRight;
import apoSkunkman.ai.ai.ApoAIRunner;
import apoSkunkman.entity.ApoSkunkmanFire;
import apoSkunkman.entity.ApoSkunkmanPlayer;
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

public class CheatAIManager implements Tickable, Initiationable {

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
        try {

            // INIT FIELDS
            apoLevelField = ApoSkunkmanAILevel.class.getDeclaredField("level");
            apoLevelField.setAccessible(true);

            fireListField = ApoSkunkmanLevel.class.getDeclaredField("fire");
            fireListField.setAccessible(true);

            if (isThereAnotherMeldanor(apoLevel.getEnemies())) {
                displayMessage("Es kann nur einen Meldanor geben!", apoLevel);
                cheatAIHandler = new CheatAIEpicBattle();
            } else if (apoLevel.getType() == ApoSkunkmanAIConstants.LEVEL_TYPE_GOAL_X)
                cheatAIHandler = new CheatAIGoalX();
            else if (apoLevel.getType() == ApoSkunkmanAIConstants.LEVEL_TYPE_STANDARD && areBots(apoLevel.getEnemies()))
                cheatAIHandler = new CheatAIBots();
            else
                cheatAIHandler = new CheatAIHuman();

            this.isInit = true;
        } catch (Exception e) {

        }

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

    private boolean isThereAnotherMeldanor(ApoSkunkmanAIEnemy[] enemies) {
        try {
            Field enemyPlayerField = ApoSkunkmanAIEnemy.class.getDeclaredField("player");
            enemyPlayerField.setAccessible(true);
            ApoSkunkmanAI ai = null;
            for (ApoSkunkmanAIEnemy apoSkunkmanAIEnemy : enemies) {

                // WHEN AI == NULL , AI IS HUMAN
                ai = ((ApoSkunkmanPlayer) enemyPlayerField.get(apoSkunkmanAIEnemy)).getAi();
                if (ai == null)
                    continue;

                if (ai.getPlayerName().equalsIgnoreCase("MeldanorTroll"))
                    return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public boolean isInit() {
        return isInit;
    }

    private static Field fireListField;
    private static Field apoLevelField;

    @SuppressWarnings("unchecked")
    public static void displayMessage(String message, ApoSkunkmanAILevel apoLevel) throws Exception {
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        ArrayList<ApoSkunkmanFire> fires = (ArrayList<ApoSkunkmanFire>) fireListField.get(level);

        // DELETE OLD MESSAGES -> WE DISPLAY ONLY ONE MESSAGE AT ONE TIME
        // THIS AVOIDS ARTEFACTS
        for (int i = 0; i < fires.size(); ++i) {
            if (fires.get(i) instanceof TrollMessageEntity) {
                fires.get(i).setBVisible(false);
                fires.remove(i);
                break;
            }
        }
        fires.add(new TrollMessageEntity(message));
    }

}
