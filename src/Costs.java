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

public enum Costs {

    // @formatter:off

    FREE        (ApoSkunkmanAIConstants.LEVEL_FREE,     20),
    STONE       (ApoSkunkmanAIConstants.LEVEL_STONE,    1000),
    BUSH        (ApoSkunkmanAIConstants.LEVEL_BUSH,     500),
    SKUNKMAN    (ApoSkunkmanAIConstants.LEVEL_SKUNKMAN, 2000);
    
    // @formatter:on

    private final byte type;
    private final int costs;

    private Costs(byte type, int costs) {
        this.type = type;
        this.costs = costs;
    }

    public static int getCosts(byte type) {
        for (Costs cost : values())
            if (cost.type == type)
                return cost.costs;
        throw new RuntimeException("Type '" + type + "' not supported!");
    }

}
