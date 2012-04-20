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
    // GOOD GODIES HAVE A LOWER COSTS TO PREFAR THEM AT PATHFINDING
    
    /** Goodie to increase player speed. Costs = 10 */
    GOOD_FAST       (ApoSkunkmanAIConstants.GOODIE_GOOD_FAST,       10),
    /** Goodie to increase bomb slots. Costs = 10 */
    GOOD_BOMB       (ApoSkunkmanAIConstants.GOODIE_GOOD_SKUNKMAN,   10),
    /** Goodie to increase bomb range. Costs = 10 */
    GOOD_WIDTH      (ApoSkunkmanAIConstants.GOODIE_GOOD_WIDTH,      10),
    /** Goodie to increase all values by one. Costs = 3 */
    GOOD_GOD        (ApoSkunkmanAIConstants.GOODIE_GOOD_GOD,        3),

    /** Goodie to decrease player speed. Costs = 400 */
    BAD_FAST        (ApoSkunkmanAIConstants.GOODIE_BAD_FAST,        400),
    /** Goodie to decrease bomb slots. Costs = 400 */
    BAD_BOMB        (ApoSkunkmanAIConstants.GOODIE_BAD_SKUNKMAN,    400),
    /** Goodie to decrease bomb range. Costs = 400 */
    BAD_WIDTH       (ApoSkunkmanAIConstants.GOODIE_BAD_WIDTH,       400),
    /** Goodie to decrease all values by one. Costs = 800 */
    BAD_GOD         (ApoSkunkmanAIConstants.GOODIE_BAD_GOD,         800);
    //@formatter:on    

    // THE TYPE OF THE GOODIE
    private final int type;
    // THE HEURISTIC COSTS OF THE GOODIE
    private final int costs;

    private GoodieCosts(final int goodieType, final int costs) {
        this.type = goodieType;
        this.costs = costs;
    }

    public int getCosts() {
        return costs;
    }

    /**
     * 
     * @param type
     *            The type of the goodie. See {@link ApoSkunkmanAIConstants}
     * @return The heuristic costs of the goodie
     */
    public static int getCosts(int type) {
        for (GoodieCosts gCosts : values())
            if (gCosts.type == type)
                return gCosts.costs;

        throw new RuntimeException("Type '" + type + "' not supported!");
    }

}
