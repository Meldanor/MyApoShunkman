/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

import apoSkunkman.ai.ApoSkunkmanAIConstants;

public enum LevelCosts {

    // @formatter:off
    // WALKABLE FIELD
    /** A walkable tile. Costs = 20 */
    FREE        (ApoSkunkmanAIConstants.LEVEL_FREE,     20),
    /** A bombable bush tile. Costs = 500 */
    BUSH        (ApoSkunkmanAIConstants.LEVEL_BUSH,     500),
    /** A bomb on the tile. Costs = 2000 */
    BOMB        (ApoSkunkmanAIConstants.LEVEL_SKUNKMAN, 2000);
    
    // @formatter:on

    // THE TYPE OF THE GOODIE
    private final byte type;
    // THE HEURISTIC COSTS OF THE GOODIE
    private final int costs;

    private LevelCosts(byte type, int costs) {
        this.type = type;
        this.costs = costs;
    }

    /**
     * 
     * @param type
     *            The type of the tile. See {@link ApoSkunkmanAIConstants}
     * @return The heuristic costs of the tile
     */
    public static int getCosts(byte type) {
        for (LevelCosts cost : values())
            if (cost.type == type)
                return cost.costs;
        throw new RuntimeException("Type '" + type + "' not supported!");
    }

}
