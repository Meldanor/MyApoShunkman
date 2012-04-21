import java.awt.Point;

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
 * Goal to plant a bomb at a certain position. This goal shall throw a
 * TakeCoverGoal but doesn't search for cover
 * 
 * @author Meldanor
 * 
 */
public class PlantBombGoal extends WalkGoal {

    /** Is the bomb planted? */
    private boolean bombPlanted;

    public PlantBombGoal(final Point bombSpot, final ApoSkunkmanAILevel apoLevel, final MeldanorPlayer player) {
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
            player.moveTo(path.removeFirst());
        // CAN PLANT THE BOMB
        else if (player.apoPlayer.getCurSkunkmanLay() < player.apoPlayer.getMaxSkunkman()) {
            // TODO: CHECK WHETHER THE PLAYER CAN TAKE COVER FROM ITS OWN BOMB
            // OR NOT!
            player.apoPlayer.laySkunkman();
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
