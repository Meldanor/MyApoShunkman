import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;

import javax.imageio.ImageIO;

import apoSkunkman.ai.ApoSkunkmanAIEnemy;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;
import apoSkunkman.entity.ApoSkunkmanPlayer;

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

    private ApoSkunkmanAILevel apoLevel;
    private ApoSkunkmanAIPlayer apoPlayer;

    private Field apoPlayerField;
    private Field enemyPlayerField;
    private Field apoLevelField;

    public CheatAIHuman() {
        System.out.println("Wir helfen nun den Spielern!");
    }

    @Override
    public void init(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {

        try {

            getFields();

            loadPics();

            changePlayerPic(playerImage);

            isInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void changePlayerPic(BufferedImage image) throws Exception {

        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);
        player.setIBackground(image);
    }

    private void getFields() throws Exception {
        enemyPlayerField = ApoSkunkmanAIEnemy.class.getDeclaredField("player");
        enemyPlayerField.setAccessible(true);

        apoLevelField = ApoSkunkmanAILevel.class.getDeclaredField("level");
        apoLevelField.setAccessible(true);

        apoPlayerField = ApoSkunkmanAIPlayer.class.getDeclaredField("player");
        apoPlayerField.setAccessible(true);

    }

    // © http://cdn.memegenerator.net/images/160x/2769555.jpg
    private BufferedImage playerImage;

    private void loadPics() throws Exception {
        playerImage = ImageIO.read(new File(Meldanor.DIR, "GoodGuyGreg.png"));
    }

    public boolean isInit() {
        return isInit;
    }

    private long time = System.currentTimeMillis();

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.apoLevel = apoLevel;
        this.apoPlayer = apoPlayer;
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
