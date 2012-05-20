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
        this.isInit = true;

    }

    @Override
    public boolean isInit() {
        return isInit;
    }

}
