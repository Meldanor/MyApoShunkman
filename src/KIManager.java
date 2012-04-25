import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import apoSkunkman.ai.ApoSkunkmanAIConstants;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAILevelSkunkman;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public class KIManager {

    private MeldanorPlayer player;
    private ApoSkunkmanAILevel apoLevel;

    private int levelType;

    private Goal currentGoal;
    private Goal coverGoal;

    private Queue<Goal> goalsForWalkLevel = new LinkedList<Goal>();

    public KIManager(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {
        player = new MeldanorPlayer(apoPlayer);
        this.apoLevel = apoLevel;
        levelType = apoLevel.getType();
        if (levelType == ApoSkunkmanAIConstants.LEVEL_TYPE_GOAL_X) {
            handleGoalLevel();
        }
    }

    private void handleGoalLevel() {
        // FIND WAY TO GOAL WITH BUSHES
        LinkedList<Node> path = player.findWay(apoLevel.getGoalXPoint(), apoLevel);
        byte[][] byteLevel = apoLevel.getLevelAsByte();

        // LOOK AT THE PATH AND FILTER ALL BOMB SPOTS
        Iterator<Node> iter = path.iterator();
        Node cur = null;
        Node prev = null;
        while (iter.hasNext()) {
            prev = cur;
            cur = iter.next();
            // THERE IS A BUSH
            if (byteLevel[cur.y][cur.x] == ApoSkunkmanAIConstants.LEVEL_BUSH) {
                // CREATE GOALS WITHOUT THE PATH - CALCULATE THEM WHEN IT IS
                // NEEDED
                goalsForWalkLevel.add(new PlantBombGoal(prev.getLocation(), player));
            }
        }
        goalsForWalkLevel.add(new WalkGoal(apoLevel.getGoalXPoint(), player));

        // PROCESS FIRST GOAL
        currentGoal = goalsForWalkLevel.poll();
        // CALCULATE FIRST
        ((WalkGoal) currentGoal).calculateWay(apoLevel);
    }

    // CHECK CURRENT LEVEL FOR BOMBS
    private void lookForBombs() {

        byte[][] byteLevel = apoLevel.getLevelAsByte();

        ApoSkunkmanAILevelSkunkman bomb = null;
        Queue<Goal> bombs = new LinkedList<Goal>();

        // ITERATE OVER ALL POINTS
        for (int y = 0; y < byteLevel.length; ++y) {
            for (int x = 0; x < byteLevel[y].length; ++x) {
                // THERE IS A BOMB
                if (byteLevel[y][x] == ApoSkunkmanAIConstants.LEVEL_SKUNKMAN) {
                    bomb = apoLevel.getSkunkman(y, x);
                    System.out.println("Bomb found at " + x + ";" + y);
                    // BOMB CAN HIT PLAYER -> SEARCH AND TAKE COVER
                    if (bombHitPlayer(bomb)) {
                        bombs.add(new TakeCoverGoal(player, apoLevel, bomb));

                        System.out.println("Bomb trifft");
                    }
                }
            }
        }

        // LOOK FOR THE MOST RELEVANT GOAL
        if (bombs.size() > 1) {
            // TODO: Implement this
        } else if (bombs.size() == 1) {
            this.coverGoal = bombs.poll();
            System.out.println("Muss in Deckung gehen");
        } else
            this.coverGoal = null;
    }

    private boolean bombHitPlayer(ApoSkunkmanAILevelSkunkman bomb) {
        float xDiff = Math.abs(bomb.getX() - player.apoPlayer.getX());
        float yDiff = Math.abs(bomb.getY() - player.apoPlayer.getY());
        int radius = bomb.getSkunkWidth();

        // PLAYER IS ON THE SAME LINE AS THE BOMB
        // AND IN THE BOMG RADIUS
        return ((xDiff == 0 || yDiff == 0) && (xDiff <= radius || yDiff <= radius));

    }

    public void tick() {

        // CHECK IF BOMB CAN KILL PLAYER
        this.lookForBombs();

        // HAVE TO TAKE COVER?
        if (coverGoal != null) {
            System.out.println("Geht in Deckung");
            if (coverGoal.isFinished() || coverGoal.isCancelled()) {
                System.out.println("Geht zur Deckung");
                coverGoal.process();
            } else {
                System.out.println("Ist in Deckung");
                coverGoal = null;
                // recalculate way
                ((WalkGoal) currentGoal).calculateWay(apoLevel);
            }
        }

        if (currentGoal.isFinished() || currentGoal.isCancelled()) {
            if (levelType == ApoSkunkmanAIConstants.LEVEL_TYPE_GOAL_X) {
                if (!goalsForWalkLevel.isEmpty()) {
                    currentGoal = goalsForWalkLevel.poll();
                    ((WalkGoal) currentGoal).calculateWay(apoLevel);
                } else
                    return;
            } else {
                // TODO: FIND A NEW ONE
            }

        }
        currentGoal.process();
    }

}
