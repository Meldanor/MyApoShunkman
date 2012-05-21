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

public class CheatAIGoalX implements Tickable, Initiateable {

    private ApoSkunkmanAIPlayer apoPlayer;
    private ApoSkunkmanAILevel apoLevel;

    // The field for the goal
    private Field goalPointField = null;
    // The field for the level
    private Field apoLevelField = null;

    private Field apoPlayerField = null;

    private boolean isInit = false;

    // 500 ms is tick
    private long time = System.currentTimeMillis();

    private static final long TICK_TIMER = 500L;

    // Maximum ticks to run away
    private int runawayTicks = 30;

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

            originalChestImage = ApoSkunkmanImageContainer.iGoalX;

            // © http://26.media.tumblr.com/avatar_af220d6da0cf_128.png
            replacedChestImage1 = ImageIO.read(new File(Meldanor.DIR, "YaoMing.png"));
            replacedChestImage2 = horiziontalFlip(replacedChestImage1);
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
            long diff = 0L;
            diff = System.currentTimeMillis() - time;

            // ONE TICK = 500 MS
            if (diff > TICK_TIMER) {
                cheatGoalX();
                time = System.currentTimeMillis();
            }
        } else
            init(apoPlayer, apoLevel);
    }

    private void cheatGoalX() {
        try {

            changeChestImage();
            // DO OR UNDO FUNNY THINGS
            if (runawayTicks % 5 == 0) {
                if (funnyStuffActive)
                    undoFunnyStuff();
                else
                    doFunnyStuff();
            }

            // MOVE THE GOAL UNTIL TIMER IS REACHED
            if (runawayTicks-- > 0)
                moveGoal();

            movePlayer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setStartPoints() throws Exception {

        // Randomize start point for the goal
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        Point goal = (Point) goalPointField.get(level);

        Random rand = new Random();
        int width = apoLevel.getLevelAsByte().length - 3;
        int x = rand.nextInt(width) + 1;
        int y = rand.nextInt(width) + 1;
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

    private void doFunnyStuff() throws Exception {

        funnyStuffActive = true;
    }

    private BufferedImage originalChestImage;
    private BufferedImage replacedChestImage1;
    private BufferedImage replacedChestImage2;

    private void undoFunnyStuff() throws Exception {

        funnyStuffActive = false;
    }

    private void changeChestImage() {
        if (runawayTicks % 2 == 1)
            ApoSkunkmanImageContainer.iGoalX = replacedChestImage1;
        else
            ApoSkunkmanImageContainer.iGoalX = replacedChestImage2;

    }

}
