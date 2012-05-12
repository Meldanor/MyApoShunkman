import java.awt.Point;
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

public class AIManager implements Updateable, Tickable {

    // KI MANAGER HAS ALL NECESSARY VALUES?
    boolean isInitialized = false;

    // WRAPPER FOR APO PLAYER CLASS
    private MeldanorPlayer player;

    private ApoSkunkmanAILevel apoLevel;

    private int levelType;

    // CURRENT GOAL TO ACHIEVE
    private Goal currentGoal;

    // GOAL TO TAKE COVER (AFTER BOMB HAS BEEN FOUND)
    private Goal coverGoal;

    // GOALS FOR THE GOAL TYPE LEVEL
    private Queue<Goal> goalsForWalkLevel = new LinkedList<Goal>();

    public AIManager() {
        // EMPTY CONSTRUCTOR
    }

    private void handleGoalLevel() {
        // FIND WAY TO GOAL WITH BUSHES
        LinkedList<Node> path = player.findWay(apoLevel.getGoalXPoint(), apoLevel, false);
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
        LinkedList<TakeCoverGoal> bombs = new LinkedList<TakeCoverGoal>();

        // ITERATE OVER ALL POINTS
        for (int y = 0; y < byteLevel.length; ++y) {
            for (int x = 0; x < byteLevel[y].length; ++x) {
                // THERE IS A BOMB
                if (byteLevel[y][x] == ApoSkunkmanAIConstants.LEVEL_SKUNKMAN) {
                    bomb = apoLevel.getSkunkman(y, x);
                    // BOMB CAN HIT PLAYER -> SEARCH AND TAKE COVER
                    if (canHitPlayer(bomb))
                        bombs.add(new TakeCoverGoal(player, apoLevel, bomb));

                }
            }
        }

        // LOOK FOR THE MOST RELEVANT GOAL
        if (bombs.size() > 1) {
            // FIND OUT WHICH BOMB IS THE NEAREST
            // THE SHORTEST BOMB IS THE BOMB WITH HIGH PRIORITY
            double distance = Double.MAX_VALUE;
            double curDistance = 0.0;
            Point bombSpot = null;
            Point playerPos = player.getPosition();

            for (TakeCoverGoal goal : bombs) {
                bombSpot = goal.getBombPosition();
                curDistance = Math.abs(playerPos.distance(bombSpot));
                // IS CURRENT BOMB IS NEARER THEN REFERENCE?
                if (curDistance < distance) {
                    distance = curDistance;
                    this.coverGoal = goal;
                }
            }

        } else if (bombs.size() == 1)
            this.coverGoal = bombs.getFirst();
        else
            this.coverGoal = null;
    }

    private boolean canHitPlayer(ApoSkunkmanAILevelSkunkman bomb) {
        float xDiff = Math.abs(bomb.getX() - player.apoPlayer.getX());
        float yDiff = Math.abs(bomb.getY() - player.apoPlayer.getY());
        int radius = bomb.getSkunkWidth();

        // PLAYER IS ON THE SAME LINE AS THE BOMB
        // AND IN THE BOMG RADIUS
        return ((xDiff == 0 || yDiff == 0) && (xDiff <= radius || yDiff <= radius));

    }

    @Override
    public void tick(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {

        // UPDATE VALUES
        this.update(apoPlayer, apoLevel);

        // CHECK IF BOMB CAN KILL PLAYER
        if (coverGoal == null)
            this.lookForBombs();

        // HAVE TO TAKE COVER GOAL?
        if (coverGoal != null) {
            if (!coverGoal.isFinished()) {
                coverGoal.process();
                return;
            } else {
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
                    // UGLY FIX!
                    if (currentGoal.isCancelled()) {
                        player.goBack();
                        ((WalkGoal) currentGoal).calculateWay(apoLevel);
                    }
                } else {
                    System.out.println("Keine weiteren Ziele");
                    return;

                }
            } else {
                System.out.println("Muss was neues finden :/");
                // TODO: FIND A NEW ONE
            }

        }
        currentGoal.process();
    }
    // UPDATING VALUES
    @Override
    public void update(ApoSkunkmanAIPlayer apoPlayer, ApoSkunkmanAILevel apoLevel) {

        if (isInitialized) {
            this.apoLevel = apoLevel;
            this.player.update(apoPlayer, apoLevel);
        } else {
            player = new MeldanorPlayer(apoPlayer, apoLevel);
            this.apoLevel = apoLevel;
            levelType = apoLevel.getType();
            if (levelType == ApoSkunkmanAIConstants.LEVEL_TYPE_GOAL_X) {
                handleGoalLevel();

            }
            isInitialized = true;
        }
    }

}
