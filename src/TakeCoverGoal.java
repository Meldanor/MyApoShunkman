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

/**
 * A goal to search and take cover. Created when a bomb is found which can hit
 * the player
 * 
 * @author Meldanor
 * 
 */
public class TakeCoverGoal extends Goal {

    private final MeldanorPlayer player;

    /** The path to the cover point */
    private LinkedList<Node> path;

    /** The bomb which started this goal */
    private ApoSkunkmanAILevelSkunkman bomb;

    /**
     * Comparator to find the shortest path to a cover
     */
    private final static Comparator<LinkedList<Node>> SHORTEST_PATH = new Comparator<LinkedList<Node>>() {
        @Override
        public int compare(LinkedList<Node> o1, LinkedList<Node> o2) {
            return o1.size() - o2.size();
        }
    };

    /**
     * Create a TakeCoverGoal and try to find a cover
     * 
     * @param player
     *            The player
     * @param apoLevel
     *            The level
     * @param bomb
     *            The bomb to run away from
     */
    public TakeCoverGoal(MeldanorPlayer player, ApoSkunkmanAILevel apoLevel, ApoSkunkmanAILevelSkunkman bomb) {
        this.player = player;
        this.bomb = bomb;
        findCover(apoLevel);
    }

    /**
     * @return True when the bomb exploded
     */
    @Override
    public boolean isFinished() {
        // PLAYER HAS TAKEN COVER OR BOMB HAS EXPLODED
        return bomb.getTimeToExplosion() <= 0;
    }

    /**
     * While player is not in the cover go to the cover
     */
    @Override
    public void process() {
        if (isCancelled())
            return;
        if (path.isEmpty())
            setCancelled();
        else
            player.moveTo(path);

    }

    /**
     * @return GoalPriority.CRITICAL
     */
    @Override
    public GoalPriority getPriority() {
        return GoalPriority.CRITICAL;
    }

    /**
     * Searching for the cover which is the closest point to the player and
     * calculate a path to it
     * 
     * @param apoLevel
     *            The level
     */
    private void findCover(ApoSkunkmanAILevel apoLevel) {
        // PLAYER POSITION
//        Point start = player.getPosition();
        // THE RADIUS OF THE BOMB
        int radius = bomb.getSkunkWidth() + 2;
        byte[][] byteLevel = apoLevel.getLevelAsByte();

        // CHECK POSSIBILITES
        List<Point> possi = getDirectCover(new Point((int) bomb.getX(), (int) bomb.getY()), radius, byteLevel);

        // CALCULATE PATH TO EVERY POSSIBLE COVER
        List<LinkedList<Node>> paths = new ArrayList<LinkedList<Node>>(4);

        for (Point pos : possi) {
            // THERE IS A COVER
            if (pos != null) {
                // FIND THE WAY TO THE COVER
                path = player.findWay(pos, apoLevel, true);
                // A PATH TO THE COVER EXISTS
                if (path != null) {
                    paths.add(path);
                    path = null;
                }
            }
        }

        // DIDN'T FIND A COVER DIRECTLY
        if (paths.isEmpty()) {
            System.out.println("Keine Deckung gefunden");
        } else {
            // FIND THE PATH WITH THE SHORTEST WAY
//            path = Collections.min(paths, SHORTEST_PATH);
//            int min = Integer.MAX_VALUE;
//            for (LinkedList<Node> list : paths) {
//                if (list.size() < min) {
//                    min = list.size();
//                    path = list;
//                }
//            }
            path = paths.get(0);
//            for (Node n : path)
//                System.out.println(n.x + ";" + n.y);
        }

    }

    /**
     * Check whether a point is inside the field or not a stone
     * 
     * @param x
     *            The x position
     * @param y
     *            The y position
     * @param byteLevel
     *            The bytelevel which contains the values for level types
     * @return A Point when x and y are inside the field and there isn't a
     *         stone. Otherwise return <code>null</code>
     */
    private Point getPoint(int x, int y, byte[][] byteLevel) {
        // IS INSIDE THE FIELD AND NOT A STONE
        if (y < byteLevel.length && y >= 0 && x < byteLevel[y].length && x >= 0 && byteLevel[y][x] != ApoSkunkmanAIConstants.LEVEL_STONE && byteLevel[y][x] != ApoSkunkmanAIConstants.LEVEL_BUSH)
            return new Point(x, y);
        // IS OUTSIDE
        else
            return null;
    }

    /**
     * Check possible cover which are on the same axis as the bomb is
     * 
     * @param start
     *            The start point
     * @param radius
     *            The radius of the bomb. The points have to be outside the
     *            radius
     * @param byteLevel
     *            The level
     * @return An Array with 4 Elements. When an element is null the cover is a
     *         stone or not in the field
     */
    private List<Point> getDirectCover(Point start, int radius, byte[][] byteLevel) {

        int xMax = start.x + (radius / 2) + 1;
        int yMax = start.y + (radius / 2) + 1;

        List<Point> covers = new LinkedList<Point>();
        Point cover = null;
        for (int y = start.y - (radius / 2) - 1; y < yMax; ++y) {
            for (int x = start.x - (radius / 2) - 1; x < xMax; ++x) {
                cover = getPoint(x, y, byteLevel);
                if (cover != null)
                    covers.add(cover);
            }
        }
        return covers;
    }
}
