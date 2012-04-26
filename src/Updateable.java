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

/**
 * Interface for classes who has to update their level and apo player values
 * every time(thanks to the great api ...)
 * 
 * @author Meldanor
 * 
 */
public interface Updateable {

    public void update(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel);

}
