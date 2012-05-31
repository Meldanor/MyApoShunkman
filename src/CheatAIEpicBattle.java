import java.awt.Point;
import java.io.File;
import java.lang.reflect.Field;
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
                preparedBackground = true;
                prepareLevel();
            }

            setStart();

            initPlayerValue();

            isInit = true;
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

    private void loadPics() throws Exception {

        ApoSkunkmanImageContainer.iBomb = ImageIO.read(new File(MeldanorTroll.DIR, "TrollAtomicBomb.png"));
        TrollAtomicBombEntity.init(ImageIO.read(new File(MeldanorTroll.DIR, "AtomicPreEffect.png")));
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

    private void prepareLevel() throws Exception {

        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        ApoSkunkmanEntity[][] entities = level.getLevel();

        for (int y = 1; y < entities.length - 1; ++y) {
            for (int x = 1; x < entities[y].length - 1; ++x) {
                entities[y][x] = null;
            }
        }

        level.getGame().makeBackground(false, false, false, false);
    }

    private void initPlayerValue() throws Exception {
        ApoSkunkmanPlayer player = (ApoSkunkmanPlayer) apoPlayerField.get(apoPlayer);
        player.setCurWidth(ApoSkunkmanConstants.PLAYER_WIDTH_MAX);

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

    private long dropBombTimer = 1000L + RAND.nextInt(500);

    private long finishBattleTimer = 30000L;

    private static boolean finished = false;

    private void handleLevel(long delta) {
        try {

            if (finished)
                return;

            if ((dropBombTimer -= delta) <= 0)
                dropBomb();

            if ((finishBattleTimer -= delta) <= 0)
                finishBattle();

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

        dropBombTimer = 1500L + RAND.nextInt(500);
    }

    private void layBomb(int x, int y) throws Exception {
        layBomb(new Point(x, y));
    }

    private void layBomb(Point p) throws Exception {
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        // DON'T LAY THE BOMB DIRECTLY -> WE WANT A BOMB PRE EFFECT FOR IT
        level.getLevel()[p.y][p.x] = new TrollAtomicBombEntity(p.x, p.y, level, apoPlayer.getPlayer());
    }

    private void finishBattle() throws Exception {
        ApoSkunkmanLevel level = (ApoSkunkmanLevel) apoLevelField.get(apoLevel);
        ApoSkunkmanEntity[][] entities = level.getLevel();

        for (int y = 1; y < entities.length - 1; ++y) {
            for (int x = 1; x < entities[y].length - 1; ++x) {
                entities[y][x] = null;
            }
        }
        for (int y = 1; y < entities.length - 1; ++y) {
            for (int x = 1; x < entities[y].length - 1; ++x) {
                if (!(y == 7 && (x == 2 || x == 12)))

                    entities[y][x] = new TrollAtomicBombEntity(x, y, level, apoPlayer.getPlayer());
            }
        }

        CheatAIManager.displayMessage("Falscher Knopf...", apoLevel);

        finished = true;

    }

}
