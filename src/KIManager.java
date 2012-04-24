import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import apoSkunkman.ai.ApoSkunkmanAIConstants;
import apoSkunkman.ai.ApoSkunkmanAILevel;
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

    public void tick() {
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
