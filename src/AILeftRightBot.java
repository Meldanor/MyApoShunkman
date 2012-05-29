import sun.security.krb5.internal.APOptions;
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

public class AILeftRightBot implements Tickable {

    private MeldanorPlayer melPlayer;

    private ApoSkunkmanAILevel apoLevel;
    private ApoSkunkmanAIPlayer apoPlayer;

    public AILeftRightBot(MeldanorPlayer melPlayer, ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.melPlayer = melPlayer;
        this.apoLevel = apoLevel;
        this.apoPlayer = apoPlayer;

        init();
    }
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.apoLevel = apoLevel;
        this.apoPlayer = apoPlayer;
        melPlayer.update(apoPlayer, apoLevel);

        handleLevel();
    }

    private void handleLevel() {
        // TODO Auto-generated method stub

    }

}
