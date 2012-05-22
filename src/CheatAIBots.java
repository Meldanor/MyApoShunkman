import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public class CheatAIBots implements Initiationable, Tickable {

    private boolean isInit = false;

    private ApoSkunkmanAILevel apoLevel;
    private ApoSkunkmanAIPlayer apoPlayer;

    private Field apoPlayerField;
    private Field enemyPlayerField = null;
    private Field apoLevelField = null;

    private static final Random RAND = new Random();

    public CheatAIBots() {
        System.out.println("Es geht gegen Bots!");
    }

    @Override
    public void init(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.apoLevel = apoLevel;
        this.apoPlayer = apoPlayer;
        try {
            enemyPlayerField = ApoSkunkmanAIEnemy.class.getDeclaredField("player");
            enemyPlayerField.setAccessible(true);

            apoLevelField = ApoSkunkmanAILevel.class.getDeclaredField("level");
            apoLevelField.setAccessible(true);

            apoPlayerField = ApoSkunkmanAIPlayer.class.getDeclaredField("player");
            apoPlayerField.setAccessible(true);

            bombWidthField = ApoSkunkmanPlayer.class.getDeclaredField("curWidth");
            bombWidthField.setAccessible(true);

            loadPics();

            changePics();

            disallowBombs();

            slowBots();

            setStartPosition();

            isInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadPics() throws Exception {
        TrollStoneEntity.setImage(ImageIO.read(new File(Meldanor.DIR, "ShallNotPass.png")));

        rageImage = ImageIO.read(new File(Meldanor.DIR, "Rage.png"));
        rageNuclearImage = ImageIO.read(new File(Meldanor.DIR, "RageNuclear.png"));
        rageOmegaImage = ImageIO.read(new File(Meldanor.DIR, "RageOmega.png"));

    }

    // © http://alltheragefaces.com/img/faces/png/rage-rage.png
    private BufferedImage rageImage;
    // © http://alltheragefaces.com/img/faces/png/rage-nuclear.png
    private BufferedImage rageNuclearImage;
    // © http://alltheragefaces.com/img/faces/png/rage-omega.png
    private BufferedImage rageOmegaImage;

    private void changePics() throws Exception {
        List<BufferedImage> images = new ArrayList<>(3);
        images.add(rageImage);
        images.add(rageNuclearImage);
        images.add(rageOmegaImage);

        Collections.shuffle(images, RAND);

        int i = 0;
        for (ApoSkunkmanAIEnemy enemyAI : apoLevel.getEnemies()) {
            ApoSkunkmanPlayer enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemyAI);
            enemyPlayer.setIBackground(images.get(i++));
        }
    }

    private void setStartPosition() throws Exception {

        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        // PLAYER POSITION IS IN THE MIDDLE OF THE FIELD
        int x = ApoSkunkmanConstants.LEVEL_WIDTH / 2;
        int y = ApoSkunkmanConstants.LEVEL_HEIGHT / 2;

        // SAVE THEM
        playerPosition = new Point(x, y);

        // BUILD A WALL AROUND THE PLAYER
        ApoSkunkmanEntity[][] entityField = level.getLevel();

        entityField[y][x] = null;

        player.setX(x * ApoSkunkmanConstants.TILE_SIZE);
        player.setY(y * ApoSkunkmanConstants.TILE_SIZE);

        entityField[y + 1][x] = new TrollStoneEntity(x, y + 1);
        entityField[y - 1][x] = new TrollStoneEntity(x, y - 1);
        entityField[y][x + 1] = new TrollStoneEntity(x + 1, y);
        entityField[y][x - 1] = new TrollStoneEntity(x - 1, y);

        // UNSURE WHETHER THIS IS NEEDED
        // level.getGame().makeBackground(false, false, false, false);
    }

    @Override
    public boolean isInit() {
        return isInit;
    }

    private long time = System.currentTimeMillis();

    private Point playerPosition;

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        if (isInit()) {
            this.apoLevel = apoLevel;
            this.apoPlayer = apoPlayer;

            handleLevel(System.currentTimeMillis() - time);
            time = System.currentTimeMillis();

        } else {
            init(apoPlayer, apoLevel);
        }
    }

    private long bombTimer = 1000L;

    private void handleLevel(long delta) {
        try {
            resetPoints();

            if ((bombTimer -= delta) <= 0)
                dropBomb();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Field bombWidthField;

    private void dropBomb() throws Exception {

        // FIND A BOMB POSITIOn WHICH IS NOT ON OR IN THE COVER
        int x = playerPosition.x;
        int y = playerPosition.y;
        Point bombPoint = new Point(x, y);

        // BOMB POSITION IS OUTSIDE IN THE COVER
        while (bombPoint.distance(playerPosition) <= 1) {
            // VALUES ARE BETWEEN 1 AND 14
            x = RAND.nextInt(ApoSkunkmanConstants.LEVEL_WIDTH - 1) + 1;
            y = RAND.nextInt(ApoSkunkmanConstants.LEVEL_HEIGHT - 1) + 1;
            bombPoint = new Point(x, y);
        }

        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);

        int bombRadius = 1;
        bombWidthField.set(player, bombRadius);
        level.layBomb(x, y, apoPlayer.getPlayer());

        // BOMBTIMER IS BETWEEN 500 AND 1500
        bombTimer = 500 + RAND.nextInt(1000);
    }

    // EVERY BOT HAS maxSkunksman = 0
    public void disallowBombs() throws Exception {
        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        ApoSkunkmanPlayer enemyPlayer = null;
        Field maxBombsField = ApoSkunkmanPlayer.class.getDeclaredField("maxSkunkman");
        maxBombsField.setAccessible(true);

        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
            maxBombsField.set(enemyPlayer, 0);
        }

    }

    // Every Enemy has only -1337 Points - To Bad for them
    private void resetPoints() throws Exception {
        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        ApoSkunkmanPlayer enemyPlayer = null;

        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
            enemyPlayer.setPoints(-1337);
        }

    }
    private void slowBots() throws Exception {
        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        ApoSkunkmanPlayer enemyPlayer = null;
        Field speed = ApoSkunkmanPlayer.class.getDeclaredField("speed");
        speed.setAccessible(true);

        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
            speed.set(enemyPlayer, 0.001f);
        }
    }
}
