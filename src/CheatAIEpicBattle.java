import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
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

public class CheatAIEpicBattle implements Tickable, Initiationable {

    private boolean isInit = false;

    private static boolean preparedBackground = false;

    private ApoSkunkmanAILevel apoLevel;
    private ApoSkunkmanAIPlayer apoPlayer;

    private Field apoLevelField;
    private Field apoPlayerField;

    private static final Random RAND = new Random();

    private boolean isLeftOne;

    // Idee: Beide KIs stehen sich gegenüber und zwischen beide ist eine Brücke.
    // Der Hintergrund wechselt ca. alle 10
    @Override
    public void init(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {

        try {
            getFields();

            loadPics();

            if (!preparedBackground) {
                changeBackground(spaceTiles);
                createBridge();
                preparedBackground = true;
            }

            setStart();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getFields() throws Exception {

        apoLevelField = ApoSkunkmanAILevel.class.getDeclaredField("level");
        apoLevelField.setAccessible(true);

        apoPlayerField = ApoSkunkmanAIPlayer.class.getDeclaredField("player");
        apoPlayerField.setAccessible(true);
    }

    // © Patrick Hoesly
    // http://farm3.staticflickr.com/2737/4116782252_abf8819c39_o.jpg
    private BufferedImage spaceTiles = null;

    private void loadPics() throws Exception {
        spaceTiles = ImageIO.read(new File(Meldanor.DIR, "Space.png"));

        // TODO: Load the bridge textures
//        brigdeStartImage = ImageIO.read(new File(Meldanor.DIR, "BLA"));
//        brigdeCorpseImage = ImageIO.read(new File(Meldanor.DIR, "BLA2"));

        // TODO: Load atomic bombs instead of skunks
//        ApoSkunkmanImageContainer.iBomb = ImageIO.read(new File(Meldanor.DIR, "TrollAtomicBomb.png"));
//        TrollAtomicBombEntity.init(ImageIO.read(new File(Meldanor.DIR, "AtomicPreEffect.png")));
    }

    private BufferedImage brigdeStartImage;
    private BufferedImage brigdeCorpseImage;

    private void createBridge() throws Exception {

        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        ApoSkunkmanEntity[][] entities = level.getLevel();

        int y = 7;

        // CREATE THE START OF THE BRIDGE
        entities[y][1] = new TrollBridgeEntity(brigdeStartImage, 1, y);
        entities[y][13] = new TrollBridgeEntity(brigdeStartImage, 13, y);

        // CREATE THE CORPUS OF THE BRIDGE
        for (int x = 2; x <= 12; ++x)
            entities[y][x] = new TrollBridgeEntity(brigdeCorpseImage, x, y);
    }

    private void setStart() throws Exception {
        Point first = new Point(2, 7);
        Point second = new Point(12, 7);

        ApoSkunkmanAIEnemy enemy = apoLevel.getEnemies()[0];

        Point target;

        // CHECK WHETHER THE OTHER AI WAS FIRST
        if (enemy.getX() == first.x) {
            target = second;
            isLeftOne = false;
        } else {
            target = first;
            isLeftOne = true;
        }

        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);
        player.setX(target.x * ApoSkunkmanConstants.TILE_SIZE);
        player.setY(target.y * ApoSkunkmanConstants.TILE_SIZE);
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

    private long dropBombTimer = 1000L;

    private void handleLevel(long delta) {
        try {

            if ((dropBombTimer -= delta) <= 0)
                dropBomb();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dropBomb() throws Exception {

        int x = 0;
        int y = 0;

        // RANDOM POINT
        do {
            x = RAND.nextInt(13) + 1;
            y = RAND.nextInt(13) + 1;
        }
        // ARE NOT ALLOWDED TO BE ON THE SAME X OR Y AXIS AS THE MELDANORS
        while ((y == 7) || (x == 2 || x == 12));

        // LAY BOMB
        layBomb(x, y);

        dropBombTimer = 1500L;
    }

    private void layBomb(int x, int y) throws Exception {
        layBomb(new Point(x, y));
    }

    private void layBomb(Point p) throws Exception {
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        // DON'T LAY THE BOMB DIRECTLY -> WE WANT A BOMB PRE EFFECT FOR IT
        level.getLevel()[p.y][p.x] = new TrollAtomicBombEntity(p.x, p.y, level, apoPlayer.getPlayer());
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
