import apoSkunkman.ai.ApoSkunkmanAIConstants;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public enum GoodieCosts {

    //@formatter:off
    // LOWER COSTS THAN FREE WAY
    GOOD_FAST       (ApoSkunkmanAIConstants.GOODIE_GOOD_FAST,       10),
    GOOD_SKUN       (ApoSkunkmanAIConstants.GOODIE_GOOD_SKUNKMAN,   10),
    // LOWER THAN ALL -> INCREASE ALL VALUES
    GOOD_GOD        (ApoSkunkmanAIConstants.GOODIE_GOOD_GOD,        3),
    GOOD_WIDTH      (ApoSkunkmanAIConstants.GOODIE_GOOD_WIDTH,      10),

    BAD_FAST        (ApoSkunkmanAIConstants.GOODIE_BAD_FAST,        400),
    BAD_SKUN        (ApoSkunkmanAIConstants.GOODIE_BAD_SKUNKMAN,    400),
    BAD_GOD         (ApoSkunkmanAIConstants.GOODIE_BAD_GOD,         800),
    BAD_WIDTH       (ApoSkunkmanAIConstants.GOODIE_BAD_WIDTH,       400);

    //@formatter:on    

    private final int type;
    private final int costs;

    private GoodieCosts(final int goodieType, final int costs) {
        this.type = goodieType;
        this.costs = costs;
    }

    public int getCosts() {
        return costs;
    }

    public static int getCosts(int type) {
        for (GoodieCosts gCosts : values())
            if (gCosts.type == type)
                return gCosts.costs;

        throw new RuntimeException("Type '" + type + "' not supported!");
    }

}
