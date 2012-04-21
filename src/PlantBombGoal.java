import java.awt.Point;

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

public class PlantBombGoal extends WalkGoal {

    private boolean bombPlanted;

    public PlantBombGoal(Point bombSpot, ApoSkunkmanAILevel apoLevel, ApoSkunkmanAIPlayer player) {
        super(bombSpot, apoLevel, player);
    }

    @Override
    public boolean isFinished() {
        return bombPlanted;
    }

    @Override
    public void process() {
        // GO TO BOMB SPOT
        if (!path.isEmpty())
            moveNext(player);
        // CAN PLANT THE BOMB
        else if (player.getCurSkunkmanLay() < player.getMaxSkunkman()) {
            player.laySkunkman();
            bombPlanted = true;
        } 
        // HAVE TO WAIT TO PLANT THE BOMB
        else
            return;
    }

    @Override
    public GoalPriority getPriority() {
        return GoalPriority.NORMAL;
    }

}
