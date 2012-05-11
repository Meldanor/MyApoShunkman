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
    private boolean bombPlanted = false;

    /**
     * Create a PlantBombGoal WITHOUT calculating the way to the bomb spot
     * 
     * @param bombSpot
     *            The bombspot where the bomb should be planted
     * @param player
     *            The player
     */
    public PlantBombGoal(final Point bombSpot, final MeldanorPlayer player) {
        super(bombSpot, player);
    }

    /**
     * Create a PlantBombGoal WITH calculating the way to the bomb spot
     * 
     * @param bombSpot
     *            The bombspot where the bomb should be planted
     * @param apoLevel
     *            The apo level to find the way
     * @param player
     *            the player
     */
    public PlantBombGoal(final Point bombSpot, final ApoSkunkmanAILevel apoLevel, final MeldanorPlayer player) {
        super(bombSpot, apoLevel, player);
    }

    @Override
    /** @return The goal is finished when the bomb has been planted */
    public boolean isFinished() {
        return bombPlanted;
    }

    /**
     * While player is not at the bombspot, walk to it. When player is there,
     * try to plant the bomb. Player will wait when he can't plant the bomb
     */
    @Override
    public void process() {
        if (path == null) {
//            System.out.println(goal);
//            System.out.println(player.getPosition());
            setCancelled();
            return;
        }
        // GO TO BOMB SPOT
        if (!path.isEmpty())
            player.moveTo(path);
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

    /** @return GoalPriority.NORMAL */
    @Override
    public GoalPriority getPriority() {
        return GoalPriority.NORMAL;
    }

}
