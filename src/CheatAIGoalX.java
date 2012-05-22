import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Random;

import javax.imageio.ImageIO;

import apoSkunkman.ApoSkunkmanConstants;
import apoSkunkman.ApoSkunkmanImageContainer;
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

public class CheatAIGoalX implements Tickable, Initiationable {

    private ApoSkunkmanAIPlayer apoPlayer;
    private ApoSkunkmanAILevel apoLevel;

    private final static Random RAND = new Random();

    // The field for the goal
    private Field goalPointField = null;
    // The field for the level
    private Field apoLevelField = null;

    private Field apoPlayerField = null;

    private boolean isInit = false;

    // 500 ms is tick
    private long time = System.currentTimeMillis();

    @Override
    public boolean isInit() {
        return isInit;
    }

    @Override
    public void init(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.apoLevel = apoLevel;
        this.apoPlayer = apoPlayer;
        try {

            Class<? extends ApoSkunkmanAILevel> apoLevelClass = apoLevel.getClass();

            // THE LEVEL
            apoLevelField = apoLevelClass.getDeclaredField("level");
            apoLevelField.setAccessible(true);

            // THE GOAL
            goalPointField = ApoSkunkmanLevel.class.getDeclaredField("goalX");
            goalPointField.setAccessible(true);

            // THE PLAYER
            apoPlayerField = apoPlayer.getClass().getDeclaredField("player");
            apoPlayerField.setAccessible(true);
            clearField();

            setStartPoints();

//            originalChestImage = ApoSkunkmanImageContainer.iGoalX;

            // © http://26.media.tumblr.com/avatar_af220d6da0cf_128.png
            replacedChestImage1 = ImageIO.read(new File(Meldanor.DIR, "YaoMing.png"));
            replacedChestImage2 = horiziontalFlip(replacedChestImage1);
            // © http://alltheragefaces.com/img/faces/png/okay-okay-clean.png
            finalChestImage = ImageIO.read(new File(Meldanor.DIR, "Okay.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        isInit = true;

    }

    private BufferedImage horiziontalFlip(BufferedImage img) throws Exception {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(w, h, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();
        return dimg;
    }

    private void clearField() throws Exception {
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);

        // DELETE THE COMPLETE FIELD
        ApoSkunkmanEntity[][] entityLevel = level.getLevel();
        for (int y = 0; y < entityLevel.length; ++y) {
            for (int x = 0; x < entityLevel[y].length; ++x) {
                entityLevel[y][x] = null;
            }
        }

        // RESET THE BACKGROUND
        level.getGame().makeBackground(false, false, false, false);
    }

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {

        if (isInit()) {
            this.apoLevel = apoLevel;
            this.apoPlayer = apoPlayer;

            handleLevel(System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
        } else
            init(apoPlayer, apoLevel);
    }

    private long runawayTimer = 5000L;

    private void handleLevel(long delta) {
        try {
            // MOVE THE GOAL UNTIL TIMER IS REACHED
            if ((runawayTimer -= delta) >= 0)
                moveGoal();

            // 500 ms BEFORE END PREPARE THE CHEST FOR CATCH
            if (runawayTimer - 500 <= 0)
                prepareChest();
            else
                // CHANGE THE IMAGE EVERY TICK
                changeChestImage();

            // MOVE THE PLAYER EVERY TICK
            movePlayer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStartPoints() throws Exception {

        // Randomize start point for the goal
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        Point goal = (Point) goalPointField.get(level);

        int width = apoLevel.getLevelAsByte().length - 3;
        int x = RAND.nextInt(width) + 1;
        int y = RAND.nextInt(width) + 1;
        goal.x = x;
        goal.y = y;

        // now calculate the players start position

        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);

        // PLAYER ALWAYS START ON THE LEFT SIDE OF THE GOAL
        player.setX((x - 1) * ApoSkunkmanConstants.TILE_SIZE);
        player.setY(y * ApoSkunkmanConstants.TILE_SIZE);
    }

    private void moveGoal() throws Exception {
        Random rand = new Random();
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        Point goal = (Point) goalPointField.get(level);

        int dx = (rand.nextBoolean() ? 1 : -1);
        int dy = (rand.nextBoolean() ? 1 : -1);

        int x = (int) (dx + apoPlayer.getX());
        int y = (int) (dy + apoPlayer.getY());

        if (x < 0 || x > ApoSkunkmanConstants.LEVEL_WIDTH - 2 || y < 0 || y > ApoSkunkmanConstants.LEVEL_HEIGHT - 2) {
            setStartPoints();

        } else
            goal.setLocation(x, y);

    }

    private void movePlayer() {

        Point p = apoLevel.getGoalXPoint();
        // CALCULATE DIRECTION
        int diff = p.x - (int) apoPlayer.getX();

        // CHECK X-AXIS
        if (diff > 0) {
            apoPlayer.movePlayerRight();
        } else if (diff < 0) {
            apoPlayer.movePlayerLeft();

        } else {
            // CHECK Y-AXIS
            diff = p.y - (int) apoPlayer.getY();
            if (diff > 0) {
                apoPlayer.movePlayerDown();
            } else if (diff < 0) {
                apoPlayer.movePlayerUp();
            }
        }
    }

    boolean funnyStuffActive = false;

//    private BufferedImage originalChestImage;
    private BufferedImage replacedChestImage1;
    private BufferedImage replacedChestImage2;

    private boolean changeImage = false;

    private void changeChestImage() {
        if (changeImage)
            ApoSkunkmanImageContainer.iGoalX = replacedChestImage1;
        else
            ApoSkunkmanImageContainer.iGoalX = replacedChestImage2;

        changeImage = !changeImage;
    }

    private BufferedImage finalChestImage;

    private void prepareChest() {

        ApoSkunkmanImageContainer.iGoalX = finalChestImage;

    }

}
