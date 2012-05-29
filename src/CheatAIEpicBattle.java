import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;

import javax.imageio.ImageIO;

import apoSkunkman.ApoSkunkmanConstants;
import apoSkunkman.ai.ApoSkunkmanAIEnemy;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;
import apoSkunkman.entity.ApoSkunkmanEntity;
import apoSkunkman.entity.ApoSkunkmanPlayer;
import apoSkunkman.level.ApoSkunkmanLevel;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einh�lt:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public class CheatAIEpicBattle implements Tickable, Initiationable {

    private boolean isInit = false;

    private static boolean preparedBackground = false;

    private ApoSkunkmanAILevel apoLevel;
    private ApoSkunkmanAIPlayer apoPlayer;

    private Field apoLevelField;
    private Field apoPlayerField;

    // Idee: Beide KIs stehen sich gegen�ber und zwischen beide ist eine Br�cke.
    // Der Hintergrund wechselt ca. alle 10
    @Override
    public void init(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {

        try {
            getFields();

            loadPics();

            if (!preparedBackground) {
                changeBackground(spaceTiles);
                preparedBackground = true;
            }

            setStart();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setStart() throws Exception {
        Point first = new Point(2, 7);
        Point second = new Point(12, 7);

        ApoSkunkmanAIEnemy enemy = apoLevel.getEnemies()[0];

        Point target;

        // CHECK WHETHER THE OTHER AI WAS FIRST
        if (enemy.getX() == first.x)
            target = second;
        else
            target = first;

        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);
        player.setX(target.x * ApoSkunkmanConstants.TILE_SIZE);
        player.setY(target.y * ApoSkunkmanConstants.TILE_SIZE);
    }

    private void getFields() throws Exception {

        apoLevelField = ApoSkunkmanAILevel.class.getDeclaredField("level");
        apoLevelField.setAccessible(true);

        apoPlayerField = ApoSkunkmanAIPlayer.class.getDeclaredField("player");
        apoPlayerField.setAccessible(true);
    }

    // � Patrick Hoesly
    // http://farm3.staticflickr.com/2737/4116782252_abf8819c39_o.jpg
    private BufferedImage spaceTiles = null;

    private void loadPics() throws Exception {
        spaceTiles = ImageIO.read(new File(Meldanor.DIR, "Space.png"));
    }

    @Override
    public boolean isInit() {
        return isInit;
    }

    private long time = System.currentTimeMillis();

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.apoLevel = apoLevel;
        this.apoPlayer = apoPlayer;
        if (isInit()) {

            handleLevel(System.currentTimeMillis() - time);
            time = System.currentTimeMillis();

        } else {
            init(apoPlayer, apoLevel);
        }
    }

    private void handleLevel(long delta) {
        try {
            apoPlayer.movePlayerDown();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void changeBackground(BufferedImage tiles) throws Exception {
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        ApoSkunkmanEntity[][] entities = level.getLevel();
        // REPLACE ALL IMAGES WITH THE ARMAGEDDON STYLE
        for (int y = 0; y < entities.length; ++y) {
            for (int x = 0; x < entities[y].length; ++x) {
                if (y == 7)
                    entities[y][x] = null;
                else
                    entities[y][x] = new TrollBackgroundEntity(tiles, x, y);
            }
        }

        level.getGame().makeBackground(false, false, false, false);
    }

}
