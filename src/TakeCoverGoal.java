import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import apoSkunkman.ai.ApoSkunkmanAIConstants;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAILevelSkunkman;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public class TakeCoverGoal extends Goal {

    private final MeldanorPlayer player;

    private Point cover;

    private LinkedList<Node> path;

    private ApoSkunkmanAILevelSkunkman bomb;

    public TakeCoverGoal(MeldanorPlayer player, ApoSkunkmanAILevel apoLevel, ApoSkunkmanAILevelSkunkman bomb) {
        this.player = player;
        this.bomb = bomb;
        findCover(apoLevel);
    }

    @Override
    public boolean isFinished() {
        // PLAYER HAS TAKEN COVER OR BOMB HAS EXPLODED
        return bomb.getTimeToExplosion() <= 0;
    }

    @Override
    public void process() {
        if (isCancelled())
            return;
        player.moveTo(path);

    }
    @Override
    public GoalPriority getPriority() {
        return GoalPriority.CRITICAL;
    }

    // SEARCHING FOR COVER AND CALCULATE A PATH TO IT
    private void findCover(ApoSkunkmanAILevel apoLevel) {
        Point start = player.getPosition();
//        Point pBomb = new Point((int) bomb.getX(), (int) bomb.getY());
        int radius = bomb.getSkunkWidth() + 1;
        byte[][] byteLevel = apoLevel.getLevelAsByte();

        // CHECK POSSIBILITES
        Point[] possi = getDirectCover(start, radius, byteLevel);

        List<LinkedList<Node>> paths = new ArrayList<LinkedList<Node>>(4);
        for (Point pos : possi) {
            if (pos == null) {
                path = player.findWay(cover, apoLevel);
                if (path != null) {
                    paths.add(path);
                    path = null;
                }
            }
        }

        // DIDN'T FIND A COVER DIRECTLY
        if (paths.isEmpty())
            ;
        // TODO: SEARCH FOR COVERES THEIR AREN'T ON THE AXIS
        // TODO: THINK LIKE A JUMPER

        else {
            // FIND THE PATH WITH THE SHORTEST WAY
            path = Collections.min(paths, new Comparator<LinkedList<Node>>() {
                @Override
                public int compare(LinkedList<Node> o1, LinkedList<Node> o2) {
                    return o1.size() - o2.size();
                }
            });
        }

    }

    private Point getPoint(int x, int y, byte[][] byteLevel) {
        // IS INSIDE THE FIELD AND NOT A STONE
        if (y < byteLevel.length && y >= 0 && x < byteLevel[y].length && x >= 0 && byteLevel[y][x] != ApoSkunkmanAIConstants.LEVEL_STONE && byteLevel[y][x] != ApoSkunkmanAIConstants.LEVEL_BUSH)
            return new Point(x, y);
        // IS OUTSIDE
        else
            return null;
    }

    private Point[] getDirectCover(Point start, int radius, byte[][] byteLevel) {
        // @formatter:off
        return new Point[] {
                getPoint(start.x + radius,  start.y,            byteLevel),
                getPoint(start.x - radius,  start.y,            byteLevel),
                getPoint(start.x ,          start.y + radius,   byteLevel),
                getPoint(start.x ,          start.y - radius,   byteLevel),
        };
        // @formatter:on
    }
}
