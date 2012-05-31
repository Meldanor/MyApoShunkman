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
import apoSkunkman.ApoSkunkmanImageContainer;
import apoSkunkman.ai.ApoSkunkmanAIConstants;
import apoSkunkman.ai.ApoSkunkmanAIEnemy;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;
import apoSkunkman.entity.ApoSkunkmanBush;
import apoSkunkman.entity.ApoSkunkmanEntity;
import apoSkunkman.entity.ApoSkunkmanGoodie;
import apoSkunkman.entity.ApoSkunkmanPlayer;
import apoSkunkman.entity.ApoSkunkmanSkunkman;
import apoSkunkman.entity.ApoSkunkmanStone;
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
    private Field enemyPlayerField;
    private Field apoLevelField;

    private static final Random RAND = new Random();

    public CheatAIBots() {
        // EMPTY
    }

    @Override
    public void init(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.apoLevel = apoLevel;
        this.apoPlayer = apoPlayer;
        try {

            getFields();

            loadPics();

            TrollBombEntity.init(preBombEffect, apoPlayer.getPlayer());

            changePics();

            setStartPosition();

            isInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getFields() throws Exception {
        enemyPlayerField = ApoSkunkmanAIEnemy.class.getDeclaredField("player");
        enemyPlayerField.setAccessible(true);

        apoLevelField = ApoSkunkmanAILevel.class.getDeclaredField("level");
        apoLevelField.setAccessible(true);

        apoPlayerField = ApoSkunkmanAIPlayer.class.getDeclaredField("player");
        apoPlayerField.setAccessible(true);

        bombWidthField = ApoSkunkmanPlayer.class.getDeclaredField("curWidth");
        bombWidthField.setAccessible(true);

        maxBombsField = ApoSkunkmanPlayer.class.getDeclaredField("maxSkunkman");
        maxBombsField.setAccessible(true);

        enemySpeedField = ApoSkunkmanPlayer.class.getDeclaredField("speed");
        enemySpeedField.setAccessible(true);
    }

    // © http://img834.imageshack.us/img834/1918/textest.jpg
    private BufferedImage preBombEffect;

    private void loadPics() throws Exception {
        TrollStoneEntity.setImage(ImageIO.read(new File(MeldanorTroll.DIR, "ShallNotPass.png")));

        preBombEffect = ImageIO.read(new File(MeldanorTroll.DIR, "BombEffect.png"));

        rageImage = ImageIO.read(new File(MeldanorTroll.DIR, "Rage.png"));
        rageNuclearImage = ImageIO.read(new File(MeldanorTroll.DIR, "RageNuclear.png"));
        rageOmegaImage = ImageIO.read(new File(MeldanorTroll.DIR, "RageOmega.png"));

        armageddonTiles = ImageIO.read(new File(MeldanorTroll.DIR, "Armageddon2.png"));

        enemyArmageddonSkin = ImageIO.read(new File(MeldanorTroll.DIR, "EnemyArmageddon.png"));
        playerAmrageddonSkin = ImageIO.read(new File(MeldanorTroll.DIR, "EnrageTroll.png"));

        resetTiles();
    }

    private void resetTiles() throws Exception {
        ApoSkunkmanImageContainer.iTile = ApoSkunkmanImageContainer.iTileAntje;
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        ApoSkunkmanEntity[][] entities = level.getLevel();
        ApoSkunkmanEntity entity;
        for (int y = 0; y < entities.length; ++y) {
            for (int x = 0; x < entities[y].length; ++x) {
                entity = entities[y][x];
                if (entity != null) {
                    if (entity instanceof ApoSkunkmanStone)
                        entity.setIBackground(level.getStoneImage());
                    else if (entity instanceof ApoSkunkmanBush) {
                        entity.setIBackground(level.getBushImage());
                    }
                }
            }
        }

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

    // THE TIMERS' INITIAL VALUES
    private long bombTimer = 1000L;
    private long bombWidthTimer = 10000L;
    private long mercyTimer = 15000L;

    private long enrageTimer = 35000L;

    private void handleLevel(long delta) {
        try {
            resetPoints();
            // DON'T DO ANYTHING WHEN WE ARE PREPARING ARMAGEDDON
            if (ragePhase == 0) {
                // WHILE WE HAVE MERCY NO BOMBS ARE PLANTED NEAR THE BOTS
                if (haveMercy && (mercyTimer -= delta) <= 0) {
                    haveMercy = false;
                    CheatAIManager.displayMessage("NO MORE MERCY!", apoLevel);
                }

                // CHANGE THE BOMB RADIUS
                if ((bombWidthTimer -= delta) <= 0)
                    changeBombSize();

                // PLACE BOMB
                if ((bombTimer -= delta) <= 0)
                    dropBomb();
            }
            // GOING ENRAGE
            if (!isEnrage && (enrageTimer -= delta) <= 0)
                goEnrage();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Field enemySpeedField;

    private void setEnemiesSpeed(float speed) throws Exception {

        // GET ALL ENEMIES AND SET THE ADJUST THE MOVEMENTSPEED
        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        ApoSkunkmanPlayer enemyPlayer = null;

        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
            enemySpeedField.set(enemyPlayer, speed);
        }
    }

    private Field bombWidthField;

    private void changeBombSize() throws Exception {
        // GET OUR PLAYER BECAUSE WE DROP THE BOMB
        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);

        // BOMB RADIUS = VALUE BETWEEN OR EQUALS 1 UNTIL 15
        int bombRadius = RAND.nextInt(ApoSkunkmanConstants.PLAYER_WIDTH_MAX - ApoSkunkmanConstants.PLAYER_WIDTH_MIN) + ApoSkunkmanConstants.PLAYER_WIDTH_MIN;
        bombWidthField.set(player, bombRadius);

        CheatAIManager.displayMessage("Have you heard? My bombs' radius is now " + bombRadius, apoLevel);

        // BOMB TIMER = 10000L UNTIL 12500
        bombWidthTimer = 10000L + RAND.nextInt(2500);
    }

    // WHILE TRUE DON'T PLANT BOMBS NEAR BOTS
    private boolean haveMercy = true;

    private void dropBomb() throws Exception {

        int x = playerPosition.x;
        int y = playerPosition.y;
        Point bombPoint = null;

        // SEARCH FOR A FREE BOMB SPOT
        // WHICH CAN'T HIT US
        // OR WHILE HAVING MERCY THE BOTS
        do {
            x = RAND.nextInt(ApoSkunkmanConstants.LEVEL_WIDTH - 1) + 1;
            y = RAND.nextInt(ApoSkunkmanConstants.LEVEL_HEIGHT - 1) + 1;
            bombPoint = new Point(x, y);
        } while (bombPoint.distance(playerPosition) <= 1 || !isFree(bombPoint) || !checkEnemies(bombPoint));

        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        level.getLevel()[y][x] = new TrollBombEntity(x, y, level);

        // BOMBTIMER IS BETWEEN 500 AND 1500 IN NON ENRAGE MODE
        if (!isEnrage)
            bombTimer = 500L + RAND.nextInt(1000);
        // BOMB TIMER IS NOW 250
        else
            bombTimer = 250L;
    }

    // RETURNS TRUE WHEN ON THE FIELD IS NOTHING(NOR A GOODIE)
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

    private Field maxBombsField = null;

    // SET THE MAXIMUM COUNT OF BOMBS THE BOTS ARE ALLOWED TO LAY
    public void setBotsMaxBombs(int bombCount) throws Exception {

        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        ApoSkunkmanPlayer enemyPlayer = null;

        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
            maxBombsField.set(enemyPlayer, bombCount);
        }

    }

    // EVERY ENEMY HAS ONLY -1337 POINTS
    private void resetPoints() throws Exception {
        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        ApoSkunkmanPlayer enemyPlayer = null;

        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
            enemyPlayer.setPoints(-1337);
        }
    }

    private boolean isEnrage = false;

    // ENTER THE ENRAGE MODE IN 4 STEPS
    private int ragePhase = 0;

    // ©http://th06.deviantart.net/fs70/PRE/i/2011/047/1/2/lava_texture_stock_by_mavrosh_stock-d39o7zp.jpg
    private BufferedImage armageddonTiles;

    private void goEnrage() throws Exception {

        // CHANGE LEVEL STYLE
        changeArmageddonStyle();

        // WE HAVE JUST ENTERED THE ENRAGE
        // PREPARE EVERYTHING
        if (ragePhase == 0) {
            // BOTS CAN'T SET ANYTHING
            setBotsMaxBombs(0);
            // BOTS CAN'T MOVE
            setEnemiesSpeed(0.0F);
            // REMOVE SKUNKMANS AND GOODIES AND REPLACE ENEMIES SKINS
            prepareArmageddon();
            CheatAIManager.displayMessage("ENOUGH OF THIS! NOW YOU HAVE TO PAY FOR TROLLING ME!", apoLevel);
        }

        // FINISHED THE TRANSFORMATION
        if (ragePhase == 3) {
            // BOTS ARE ALLOWED TO MOVE NOW
            setEnemiesSpeed(ApoSkunkmanConstants.PLAYER_SPEED_MIN);
            // BOTS CAN PLACE BOMBS
            setBotsMaxBombs(1);
            isEnrage = true;
            // AFTER THIS RAGESTAGE == 0
            ragePhase = -1;
        }

        // TIME UNTIL NEXT RAGE STEP
        enrageTimer = 1000L;
        ++ragePhase;
    }

    // © http://alltheragefaces.com/img/faces/png/sad-numb.png
    private BufferedImage enemyArmageddonSkin;
    private BufferedImage playerAmrageddonSkin;

    private void prepareArmageddon() throws Exception {
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        ApoSkunkmanEntity[][] entities = level.getLevel();
        ApoSkunkmanEntity entity;

        // ALL BUSHES SPAWN BAD GOODIES
        // DELETE EXISTING GOODIES
        // DELETE EXISTING SKUNKMAN
        for (int y = 0; y < entities.length; ++y) {
            for (int x = 0; x < entities[y].length; ++x) {
                entity = entities[y][x];
                if (entity != null) {
                    if (entity instanceof ApoSkunkmanBush) {
                        ApoSkunkmanBush bush = (ApoSkunkmanBush) entity;
                        bush.setGoodie(ApoSkunkmanConstants.GOODIE_BAD_GOD);
                    } else if (entity instanceof ApoSkunkmanGoodie || entity instanceof ApoSkunkmanSkunkman) {
                        entities[y][x] = null;
                    }

                }
            }
        }

        // CHANGE ENEMIES SKIN
        for (ApoSkunkmanAIEnemy enemyAI : apoLevel.getEnemies()) {
            ApoSkunkmanPlayer enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemyAI);
            enemyPlayer.setIBackground(enemyArmageddonSkin);
        }

        // CHANGE PLAYER SKIN
        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);
        player.setIBackground(playerAmrageddonSkin);

    }

    private void changeArmageddonStyle() throws Exception {
        BufferedImage bImage = armageddonTiles.getSubimage(0, ApoSkunkmanConstants.TILE_SIZE * ragePhase, armageddonTiles.getWidth(), ApoSkunkmanConstants.TILE_SIZE);
        ApoSkunkmanImageContainer.iTile = bImage;
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        ApoSkunkmanEntity[][] entities = level.getLevel();
        ApoSkunkmanEntity entity;
        // REPLACE ALL IMAGES WITH THE ARMAGEDDON STYLE
        for (int y = 0; y < entities.length; ++y) {
            for (int x = 0; x < entities[y].length; ++x) {
                entity = entities[y][x];
                if (entity != null) {
                    if (entity instanceof ApoSkunkmanStone)
                        entity.setIBackground(level.getStoneImage());
                    else if (entity instanceof ApoSkunkmanBush)
                        entity.setIBackground(level.getBushImage());

                }
            }
        }

        level.getGame().makeBackground(false, false, false, false);
    }
}
