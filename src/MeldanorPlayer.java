import java.awt.Point;

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

/**
 * Wrapper for {@link ApoSkunkmanAIPlayer} player to have usefull methods
 * 
 * @author Meldanor
 * 
 */
public class MeldanorPlayer {

    public ApoSkunkmanAIPlayer apoPlayer;

    public MeldanorPlayer(ApoSkunkmanAIPlayer apoPlayer) {
        this.apoPlayer = apoPlayer;
    }

    public void moveTo(Point p) {

        // CALCULATE DIRECTION
        int diff = p.x - (int) apoPlayer.getX();

        // CHECK X-AXIS
        if (diff > 0)
            apoPlayer.movePlayerRight();
        else if (diff < 0)
            apoPlayer.movePlayerLeft();
        else {
            // CHECK Y-AXIS
            diff = p.y - (int) apoPlayer.getY();
            if (diff > 0)
                apoPlayer.movePlayerDown();
            else if (diff < 0)
                apoPlayer.movePlayerUp();
        }
    }

}
