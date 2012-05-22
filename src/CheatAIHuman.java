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

public class CheatAIHuman implements Tickable, Initiationable {

    private boolean isInit = false;

    public CheatAIHuman() {
        System.out.println("Wir helfen nun den Spielern!");
    }

    @Override
    public void init(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {

        isInit = true;

    }

    public boolean isInit() {
        return isInit;
    }

    private long time = System.currentTimeMillis();

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        if (isInit) {

            handleLevel(System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
        }

        else
            init(apoPlayer, apoLevel);

    }

    private void handleLevel(long delta) {

    }

}
