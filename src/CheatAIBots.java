import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import sun.awt.image.JPEGImageDecoder;
import apoSkunkman.ApoSkunkmanConstants;
import apoSkunkman.ApoSkunkmanImageContainer;
import apoSkunkman.ai.ApoSkunkmanAIEnemy;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;
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

    private Field enemyPlayerField = null;
    private Field apoLevelField = null;

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

            loadPics();

            changePics();

            disallowBombs();

            slowBots();

            isInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadPics() throws Exception {
        trollStoneImage = ImageIO.read(new File(Meldanor.DIR, "ShallNotPass.png"));

        // DEEP COPY OF STONE IMAGE
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        originalStoneImage = copyImage(level.getStoneImage());

        rageImage = ImageIO.read(new File(Meldanor.DIR, "Rage.png"));
        rageNuclearImage = ImageIO.read(new File(Meldanor.DIR, "RageNuclear.png"));
        rageOmegaImage = ImageIO.read(new File(Meldanor.DIR, "RageOmega.png"));

    }

    // ©http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/Producesacopyofthesuppliedimage.htm
    public BufferedImage copyImage(BufferedImage image) {
        return scaledImage(image, image.getWidth(), image.getHeight());
    }

    /**
     * Produces a resized image that is of the given dimensions
     * 
     * @param image
     *            The original image
     * @param width
     *            The desired width
     * @param height
     *            The desired height
     * @return The new BufferedImage
     */
    public BufferedImage scaledImage(BufferedImage image, int width, int height) {
        BufferedImage newImage = createCompatibleImage(width, height);
        Graphics graphics = newImage.createGraphics();

        graphics.drawImage(image, 0, 0, width, height, null);

        graphics.dispose();
        return newImage;
    }

    /**
     * Creates an image compatible with the current display
     * 
     * @return A BufferedImage with the appropriate color model
     */
    public static BufferedImage createCompatibleImage(int width, int height) {
        GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        return configuration.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
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

        Collections.shuffle(images, rand);

        int i = 0;
        for (ApoSkunkmanAIEnemy enemyAI : apoLevel.getEnemies()) {
            ApoSkunkmanPlayer enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemyAI);
            enemyPlayer.setIBackground(images.get(i++));
        }
    }

    @Override
    public boolean isInit() {
        return isInit;
    }

    private long time = System.currentTimeMillis();

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

    private void handleLevel(long delta) {
        try {

            changeStoneImage();

            setSpeed();

            resetPoints();

            movePlayer();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void setSpeed() throws Exception {

//        Field apoPlayerField = ApoSkunkmanAIPlayer.class.getDeclaredField("player");
//        apoPlayerField.setAccessible(true);
//        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);
//        Field speedField = ApoSkunkmanPlayer.class.getDeclaredField("speed");
//        speedField.setAccessible(true);
//
//        speedField.set(player, randomFloat(0.001F, 1F));
    }

    private static final Random rand = new Random();

    private float randomFloat(float pMin, float pMax) {
        return pMin + rand.nextFloat() * (pMax - pMin);

    }

    private boolean right = false;

    private void movePlayer() {

        if (!right)
            apoPlayer.movePlayerRight();
        else
            apoPlayer.movePlayerLeft();

        right = !right;

    }

    private boolean changeStoneImage = false;
    private BufferedImage trollStoneImage;
    private BufferedImage originalStoneImage;

    private void changeStoneImage() throws Exception {
        BufferedImage newImage;

        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        if (changeStoneImage)
            newImage = trollStoneImage;
        else
            newImage = originalStoneImage;

        Graphics g = ApoSkunkmanImageContainer.iTile.getGraphics();
        g.drawImage(level.getGrasImage(), 1 * ApoSkunkmanConstants.TILE_SIZE, 0, null);
        g.drawImage(newImage, 1 * ApoSkunkmanConstants.TILE_SIZE, 0, null);

        level.getGame().makeBackground(false, false, false, false);

        changeStoneImage = !changeStoneImage;
    }

//    BufferedImage iLevelTileStone = ApoSkunkmanImageContainer.iTile.getSubimage(1 * ApoSkunkmanConstants.TILE_SIZE, 0, ApoSkunkmanConstants.TILE_SIZE, ApoSkunkmanConstants.TILE_SIZE);

}
