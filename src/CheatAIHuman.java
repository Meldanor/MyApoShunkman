import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Random;

import javax.imageio.ImageIO;

import apoSkunkman.ApoSkunkmanConstants;
import apoSkunkman.ai.ApoSkunkmanAIConstants;
import apoSkunkman.ai.ApoSkunkmanAIEnemy;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAILevelSkunkman;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;
import apoSkunkman.entity.ApoSkunkmanPlayer;
import apoSkunkman.entity.ApoSkunkmanSkunkman;
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

public class CheatAIHuman implements Tickable, Initiationable {

    private boolean isInit = false;

    private final static Random RAND = new Random();

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

    // � http://cdn.memegenerator.net/images/160x/2769555.jpg
    private BufferedImage playerImage;

    // �http://levelselect.co.uk/wp-content/uploads/2010/03/portal_thumbnail.png
    private BufferedImage portalStartImage;
    private BufferedImage portalEndImage;

    private void loadPics() throws Exception {
        playerImage = ImageIO.read(new File(Meldanor.DIR, "GoodGuyGreg.png"));

        portalStartImage = ImageIO.read(new File(Meldanor.DIR, "PortalStart.png"));
        portalEndImage = ImageIO.read(new File(Meldanor.DIR, "PortalEnd.png"));
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

    private long checkPlayerTimer = 1750L;

    private void handleLevel(long delta) {
        try {

            checkMySelf();

            if ((checkPlayerTimer -= delta) <= 0)
                checkEnemiesInDanger();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    // IS THERE A BOMB WHICH CAN HIT MYSELF?
    private void checkMySelf() throws Exception {

        byte[][] byteLevel = apoLevel.getLevelAsByte();

        Point myPosition = new Point((int) apoPlayer.getX(), (int) apoPlayer.getY());

        for (int y = 0; y < byteLevel.length; ++y) {
            for (int x = 0; x < byteLevel[y].length; ++x) {
                if (byteLevel[y][x] == ApoSkunkmanAIConstants.LEVEL_SKUNKMAN) {
                    ApoSkunkmanAILevelSkunkman skunk = apoLevel.getSkunkman(y, x);
                    // IN MY ROW / COLUMN AND CAN HIT ME
                    if (bombCanHit(myPosition, skunk, byteLevel))
                        teleportRandom();
                }
            }
        }

    }

    private boolean bombCanHit(Point playerPoint, ApoSkunkmanAILevelSkunkman bomb, byte[][] byteLevel) {

        Point bombPoint = new Point((int) bomb.getX(), (int) bomb.getY());
        // I'M STANDING ON THE BOMB
        if (bombPoint.equals(playerPoint))
            return true;

        Point diff = new Point(bombPoint.x - playerPoint.x, bombPoint.y - playerPoint.y);

        // BOMB IS NOT IN THE ROW OF PLAYER
        // OR
        // BOMB RADIUS TOO LOW
        if ((diff.x != 0 && diff.y != 0) || playerPoint.distance(bombPoint) > bomb.getSkunkWidth())
            return false;

        diff.x = (int) Math.signum(diff.x);
        diff.y = (int) Math.signum(diff.y);

        // CHECK WHETHER THERE IS A BARRIER BETWEEN BOMB AND PLAYER
        if (diff.x != 0) {
            for (int x = bombPoint.x + diff.x; x < playerPoint.x; x += diff.x) {
                if (isBarrier(x, bombPoint.y, byteLevel))
                    return false;
            }
        } else {
            for (int y = bombPoint.y + diff.y; y < playerPoint.y; y += diff.y) {
                if (isBarrier(bombPoint.x, y, byteLevel))
                    return false;
            }
        }

        // THERE WAS NO BARIER BETWEEN PLAYER AND BOMB
        return true;
    }

    private boolean isBarrier(int x, int y, byte[][] byteLevel) {
        return byteLevel[y][x] == ApoSkunkmanAIConstants.LEVEL_BUSH || byteLevel[y][x] == ApoSkunkmanAIConstants.LEVEL_STONE;
    }

    private void teleportRandom() throws Exception {

        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);

        int x = (int) player.getX() / ApoSkunkmanConstants.TILE_SIZE;
        int y = (int) player.getY() / ApoSkunkmanConstants.TILE_SIZE;

        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        if (level.getLevel()[y][x] == null)
            level.getLevel()[y][x] = new TrollPortalEntity(portalStartImage, x, y, level);

        Point targetPoint = null;

        // SEARCH FOR A TILE TO TELEPORT TO
        do {
            x = RAND.nextInt(ApoSkunkmanConstants.LEVEL_WIDTH - 1) + 1;
            y = RAND.nextInt(ApoSkunkmanConstants.LEVEL_HEIGHT - 1) + 1;
            targetPoint = new Point(x, y);
        } while (!isFree(targetPoint));

        // TELEPORT THE PLAYER TO THIS RANDOM POSITION
        player.setX(targetPoint.x * ApoSkunkmanConstants.TILE_SIZE);
        player.setY(targetPoint.y * ApoSkunkmanConstants.TILE_SIZE);

        level.getLevel()[targetPoint.y][targetPoint.x] = new TrollPortalEntity(portalEndImage, x, y, level);
    }

    // RETURNS TRUE WHEN ON THE FIELD IS NOTHING(NOR A GOODIE)
    private boolean isFree(Point p) {
        return apoLevel.getLevelAsByte()[p.y][p.x] == ApoSkunkmanAIConstants.LEVEL_FREE;
    }

    private void checkEnemiesInDanger() throws Exception {

        byte[][] byteLevel = apoLevel.getLevelAsByte();
        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        Point playerPosition = null;
        ApoSkunkmanPlayer player = null;

        // LOOK FOR THE BOMB
        for (int y = 0; y < byteLevel.length; ++y) {
            for (int x = 0; x < byteLevel[y].length; ++x) {
                // THERE IS A BOMB
                if (byteLevel[y][x] == ApoSkunkmanAIConstants.LEVEL_SKUNKMAN) {
                    // GET THE SKUNK
                    ApoSkunkmanAILevelSkunkman skunk = apoLevel.getSkunkman(y, x);

                    // IGNORE BOMBS WHICH WAS PLANTED A FEW MOMENTS AGO
                    if (!(skunk.getTimeToExplosion() >= ApoSkunkmanConstants.SKUNKMAN_TIME_TO_EXPLODE - 1000)) {

                        // CHECK IF THE BOMB CAN HIT ANY ENEMY
                        for (ApoSkunkmanAIEnemy enemy : enemies) {
                            playerPosition = new Point((int) enemy.getX(), (int) enemy.getY());

                            // CAN THE BOMB HIT THE ENEMY?
                            if (bombCanHit(playerPosition, skunk, byteLevel)) {
                                player = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
                                deleteBomb(skunk, player);
                                CheatAI.displayMessage("You were in danger...I've delete the bomb", apoLevel);

                            }
                        }
                    } else
                        System.out.println("gerade gelegt");
                }
            }
        }

        checkPlayerTimer = 1750L;

    }

    private void deleteBomb(ApoSkunkmanAILevelSkunkman bomb, ApoSkunkmanPlayer player) throws Exception {
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        if (level.getLevel()[(int) bomb.getY()][(int) bomb.getX()] instanceof ApoSkunkmanSkunkman) {
            level.getLevel()[(int) bomb.getY()][(int) bomb.getX()] = null;
            player.setCurSkunkman(player.getCurSkunkman() - 1);
        }
    }
}
