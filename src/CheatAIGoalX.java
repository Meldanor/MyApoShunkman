import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.imageio.ImageIO;

import apoSkunkman.ApoSkunkmanConstants;
import apoSkunkman.ApoSkunkmanImageContainer;
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

            initFields();

            loadImages();

            clearField();

            setStartPoints();

            generateGoalPath();

        } catch (Exception e) {
            e.printStackTrace();
        }

        isInit = true;

    }

    private void initFields() throws Exception {
        // THE LEVEL
        apoLevelField = ApoSkunkmanAILevel.class.getDeclaredField("level");
        apoLevelField.setAccessible(true);

        // THE GOAL
        goalPointField = ApoSkunkmanLevel.class.getDeclaredField("goalX");
        goalPointField.setAccessible(true);

        // THE PLAYER
        apoPlayerField = ApoSkunkmanAIPlayer.class.getDeclaredField("player");
        apoPlayerField.setAccessible(true);

        enemyPlayerField = ApoSkunkmanAIEnemy.class.getDeclaredField("player");
        enemyPlayerField.setAccessible(true);

        enemySpeedField = ApoSkunkmanPlayer.class.getDeclaredField("speed");
        enemySpeedField.setAccessible(true);

    }

    private void loadImages() throws Exception {
        // © http://26.media.tumblr.com/avatar_af220d6da0cf_128.png
        replacedChestImage1 = ImageIO.read(new File(MeldanorTroll.DIR, "YaoMing.png"));
        replacedChestImage2 = horiziontalFlip(replacedChestImage1);
        // © http://alltheragefaces.com/img/faces/png/okay-okay-clean.png
        finalChestImage = ImageIO.read(new File(MeldanorTroll.DIR, "Okay.png"));

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

    private void setStartPoints() throws Exception {

        // Randomize start point for the goal
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        Point goal = (Point) goalPointField.get(level);

        // GOAL START IN THE MIDDLE OF THE FIELD
        goal.x = (int) (ApoSkunkmanConstants.LEVEL_WIDTH / 2);
        goal.y = (int) (ApoSkunkmanConstants.LEVEL_HEIGHT / 2);

        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);

        // PLAYER ALWAYS START ON THE LEFT SIDE OF THE GOAL
        player.setX((goal.x - 1) * ApoSkunkmanConstants.TILE_SIZE);
        player.setY(goal.y * ApoSkunkmanConstants.TILE_SIZE);

        ApoSkunkmanAIEnemy[] enemies = apoLevel.getEnemies();
        if (enemies.length != 0)
            destroyEnemies(enemies);

    }

    private Field enemyPlayerField;
    private Field enemySpeedField;

    private void destroyEnemies(ApoSkunkmanAIEnemy[] enemies) throws Exception {

        CheatAIManager.displayMessage("Du nicht nehmen Kerze!", apoLevel);

        ApoSkunkmanPlayer enemyPlayer = null;
        for (ApoSkunkmanAIEnemy enemy : enemies) {
            enemyPlayer = (ApoSkunkmanPlayer) enemyPlayerField.get(enemy);
            enemyPlayer.setX(0);
            enemyPlayer.setY(0);
            enemySpeedField.set(enemyPlayer, 0.0F);
        }
    }

    private void generateGoalPath() throws Exception {
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        Point goal = (Point) goalPointField.get(level);
        int curX = goal.x;
        int curY = goal.y;

        int pathSize = 500;
        LinkedList<Point> pathList = new LinkedList<Point>();

        int directionX = 1;
        int directionY = 0;
        int tempSize = RAND.nextInt(3) + 2;
        for (int i = 0; i < tempSize; ++i) {
            curX += directionX;
            curY += directionY;
            if (isInside(curX, curY))
                pathList.add(new Point(curX, curY));
            else
                break;
        }

        int tempP = 0;
        boolean b = false;
        while (pathList.size() < pathSize) {
            tempP = pathList.size();
            if (directionX == 0) {
                directionX = (RAND.nextBoolean() ? 1 : -1);
                directionY = 0;
            } else if (directionY == 0) {
                directionX = 0;
                directionY = (RAND.nextBoolean() ? 1 : -1);
            }
            tempSize = RAND.nextInt(3) + 2;

            in : for (int i = 0; i < tempSize; ++i) {
                curX += directionX;
                curY += directionY;
                if (isInside(curX, curY))
                    pathList.add(new Point(curX, curY));
                else
                    break in;
            }
            if (tempP == pathList.size()) {
                if (b) {
                    break;
                } else
                    b = true;
            } else
                b = false;

        }

        goalPath = pathList;
        playerPath = new LinkedList<Point>();
        playerPath.add(new Point(goal));
        playerPath.addAll(pathList);

    }

    private boolean isInside(int x, int y) {
        return x >= 1 && y >= 1 && x < ApoSkunkmanConstants.LEVEL_WIDTH - 1 && y < ApoSkunkmanConstants.LEVEL_HEIGHT - 1;
    }

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        this.apoLevel = apoLevel;
        this.apoPlayer = apoPlayer;

        if (isInit()) {
            handleLevel(System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
        } else
            init(apoPlayer, apoLevel);
    }

    private void handleLevel(long delta) {
        try {
            moveGoal();

            if (goalPath.size() <= 1)
                prepareChest();
            else
                changeChestImage();

            movePlayer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Queue<Point> goalPath;

    private void moveGoal() throws Exception {
        if (goalPath.isEmpty())
            return;

        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        Point goal = (Point) goalPointField.get(level);

        goal.setLocation(goalPath.poll());

    }

    private Queue<Point> playerPath;

    private void movePlayer() {
        Point p = null;

        if (playerPath.isEmpty())
            p = apoLevel.getGoalXPoint();
        else
            p = playerPath.poll();
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
