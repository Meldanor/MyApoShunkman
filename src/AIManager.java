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

public class AIManager implements Tickable {

    // KI MANAGER HAS ALL NECESSARY VALUES?
    private boolean isInit = false;

    // WRAPPER FOR APO PLAYER CLASS
    private MeldanorPlayer melPlayer;

    private Tickable ai;

    public AIManager() {
        // EMPTY CONSTRUCTOR
    }

    public void init(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {

        // CREATE WRAPPER
        melPlayer = new MeldanorPlayer(apoPlayer, apoLevel);

        if (apoLevel.getType() == ApoSkunkmanAIConstants.LEVEL_TYPE_GOAL_X)
            // AI HANDLER FOR THE GOAL-X TASK
            ai = new AIGoalX(melPlayer, apoLevel);
        else
            // AI HANDLER TO DESTROY LEFT-RIGHT-BOT
            ai = new AILeftRightBot(melPlayer, apoPlayer, apoLevel);

        isInit = true;

    }

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        if (isInit)
            ai.tick(apoPlayer, apoLevel);
        else
            init(apoPlayer, apoLevel);
    }
}
