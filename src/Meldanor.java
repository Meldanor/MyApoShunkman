/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

import apoSkunkman.ai.ApoSkunkmanAI;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;

public class Meldanor extends ApoSkunkmanAI {
    // my AI
    private Tickable AI;

    @Override
    public String getPlayerName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getAuthor() {
        return "Kilian Gaertner";
    }

    @Override
    public void load(String path) {
        // ENABLE THIS TO ACTIVATE THE NORMAL AI
        AI = new AIManager();
    }

    @Override
    public void think(ApoSkunkmanAILevel level, ApoSkunkmanAIPlayer player) {
        AI.tick(player, level);
    }

    @Override
    public String getImage() {
        // © BY http://blogs.igalia.com/xrcalvar/files/2012/05/Dancing_Troll.gif
        return "DancingTroll.png";
    }

}
