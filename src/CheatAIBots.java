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
import apoSkunkman.ai.ApoSkunkmanAIConstants;
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
        // EMPTY
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

            enemySpeedField = ApoSkunkmanPlayer.class.getDeclaredField("speed");
            enemySpeedField.setAccessible(true);

            loadPics();

            changePics();

            disallowBombs();

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
        List<BufferedImage> images = new ArrayList<BufferedImage>(3);
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
        level.getGame().makeBackground(false, false, false, false);
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
    private long bombWidthTimer = 2500L;
    private long mercyTimer = 20000L;

    private long enableSlowTimer = 15000L;
    private long disableSlowTimer = 5000L;

    private void handleLevel(long delta) {
        try {
            resetPoints();

            if (enemiesSlowed) {
                if ((disableSlowTimer -= delta) <= 0)
                    disableSlowEnemies();
            } else {
                if ((enableSlowTimer -= delta) <= 0)
                    slowEnemies();
            }

            if (haveMercy && (mercyTimer -= delta) <= 0) {
                haveMercy = false;
                System.out.println("No more mercy!");
            }

            if ((bombWidthTimer -= delta) <= 0)
                changeBombSize();

            if ((bombTimer -= delta) <= 0)
                dropBomb();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Field enemySpeedField = null;

    private boolean enemiesSlowed = false;

    private void slowEnemies() throws Exception {
        setEnemiesSpeed(ApoSkunkmanConstants.PLAYER_SPEED_MIN / 2);
        enemiesSlowed = true;
        enableSlowTimer = 15000L + RAND.nextInt(5000);
        System.out.println("You cannot flee!");
    }

    private void disableSlowEnemies() throws Exception {
        setEnemiesSpeed(ApoSkunkmanConstants.PLAYER_SPEED_MIN);
        enemiesSlowed = false;
        disableSlowTimer = 5000L + RAND.nextInt(5000);
        System.out.println("Run! But I will catch you!");
    }

    private void setEnemiesSpeed(float speed) throws Exception {

        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        ApoSkunkmanPlayer enemyPlayer = null;

        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
            enemySpeedField.set(enemyPlayer, speed);
        }
    }

    private Field bombWidthField;

    private void changeBombSize() throws Exception {
        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);

        int bombRadius = RAND.nextInt(ApoSkunkmanConstants.PLAYER_WIDTH_MAX - ApoSkunkmanConstants.PLAYER_WIDTH_MIN) + ApoSkunkmanConstants.PLAYER_WIDTH_MIN;
        bombWidthField.set(player, bombRadius);
        System.out.println("Run you fools! My bomb radius is now " + bombRadius);

        // CHANGE BOMB WIDTH TIMER IS 5000-5500
        bombWidthTimer = 5000L + RAND.nextInt(500);
    }

    // WHILE TRUE DON'T PLANT BOMBS NEAR BOTS
    private boolean haveMercy = true;

    private void dropBomb() throws Exception {

        // FIND A BOMB POSITIOn WHICH IS NOT ON OR IN THE COVER
        int x = playerPosition.x;
        int y = playerPosition.y;
        Point bombPoint = null;

        // BOMB POSITION IS OUTSIDE IN THE COVER
        do {
            x = RAND.nextInt(ApoSkunkmanConstants.LEVEL_WIDTH - 1) + 1;
            y = RAND.nextInt(ApoSkunkmanConstants.LEVEL_HEIGHT - 1) + 1;
            bombPoint = new Point(x, y);
        } while (bombPoint.distance(playerPosition) <= 1 || !isFree(bombPoint) || !checkEnemies(bombPoint));

        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        level.layBomb(x, y, apoPlayer.getPlayer());

        // BOMBTIMER IS BETWEEN 500 AND 1500
        bombTimer = 500 + RAND.nextInt(1000);
    }

    private boolean isFree(Point p) {
        return apoLevel.getLevelAsByte()[p.y][p.x] == ApoSkunkmanAIConstants.LEVEL_FREE;
    }

    private static final double MERCY_DISTANCE = 2.0;

    private boolean checkEnemies(Point bombPoint) {
        // I DON'T HAVE ANY MERCY NOW!
        if (!haveMercy)
            return true;

        double distance = 0.0;

        // GET DISTANCE FROM ALL ENEMIES TO THE BOMB POINT
        for (ApoSkunkmanAIEnemy enemy : apoLevel.getEnemies()) {
            distance = bombPoint.distance(enemy.getX(), enemy.getY());
            // IS IN MERCY DISTANCE :(
            if (distance < MERCY_DISTANCE)
                return false;
        }

        return true;
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
}
