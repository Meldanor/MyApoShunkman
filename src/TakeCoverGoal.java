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
            ;
//            setCancelled();
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

    public Point getBombPosition() {
        return new Point((int) bomb.getX(), (int) bomb.getY());
    }

    /**
     * Searching for the cover which is the closest point to the player and
     * calculate a path to it
     * 
     * @param apoLevel
     *            The level
     */
    private void findCover(ApoSkunkmanAILevel apoLevel) {

        // CHECK POSSIBILITES
        List<Point> possi = getPossibleCover(apoLevel);

        // CALCULATE PATH TO EVERY POSSIBLE COVER
        List<LinkedList<Node>> paths = new ArrayList<LinkedList<Node>>(12);

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

        if (paths.isEmpty()) {
            System.out.println("Keine Deckung gefunden");
        } else {
            // FIND THE PATH WITH THE SHORTEST WAY
            path = Collections.min(paths, SHORTEST_PATH);
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
    private List<Point> getPossibleCover(ApoSkunkmanAILevel apoLevel) {

        Point start = getBombPosition();
        int radius = bomb.getSkunkWidth();
        byte[][] byteLevel = apoLevel.getLevelAsByte();

        List<Point> covers = new LinkedList<Point>();
        findCover(start.x, start.y, radius, byteLevel, covers);
//        // LOOK FOR A COVER ON THE AXIS AND STORE THEM IN COVERS
//        checkAxis(start.x, start.y, radius, byteLevel, covers);
//        // LOOK FOR ANOTHER COVER - SEE SCHEMATA IN FUNCTION
//        checkOtherCover(start.x, start.y, byteLevel, covers);

        return covers;
    }

    private void findCover(int xS, int yS, int radius, byte[][] byteLevel, List<Point> covers) {

        // INDICATES WHETHER THERE IS A WAY ON THE AXIS
        boolean posX = true;
        boolean negX = true;
        boolean posY = true;
        boolean negY = true;

        // TEMP VARI
        Point cover = null;
        // THE OFFSET
        int i = 1;
        for (; i <= radius ; ++i) {

            // CHECK NEGATIVE X-OFFSET
            if (negX) {
                cover = getPoint(xS - i, yS, byteLevel);
                if (cover == null)
                    negX = false;
                else {
                    checkCover(xS - i, yS - 1, byteLevel, covers);
                    checkCover(xS - i, yS + 1, byteLevel, covers);
                }
            }
            // CHECK POSITIVE X-OFFSET
            if (posX) {
                cover = getPoint(xS + i, yS, byteLevel);
                if (cover == null)
                    posX = false;
                else {
                    checkCover(xS + i, yS - 1, byteLevel, covers);
                    checkCover(xS + i, yS + 1, byteLevel, covers);
                }
            }
            // CHECK NEGATIVE Y-OFFSET
            if (negY) {
                cover = getPoint(xS, yS - i, byteLevel);
                if (cover == null)
                    negY = false;
                else {
                    checkCover(xS - 1, yS - i, byteLevel, covers);
                    checkCover(xS + 1, yS - i, byteLevel, covers);
                }
            }
            // CHECK POSITIVE Y-OFFSET
            if (posY) {
                cover = getPoint(xS, yS + i, byteLevel);
                if (cover == null)
                    posY = false;
                else {
                    checkCover(xS - 1, yS + i, byteLevel, covers);
                    checkCover(xS + 1, yS + i, byteLevel, covers);
                }
            }
        }

        if (negX)
            checkCover(xS - i, yS, byteLevel, covers);
        if (posX)
            checkCover(xS + i, yS, byteLevel, covers);
        if (negY)
            checkCover(xS, yS - i, byteLevel, covers);
        if (posY)
            checkCover(xS, yS + i, byteLevel, covers);
    }

    private void checkCover(int xS, int yS, byte[][] byteLevel, List<Point> covers) {
        Point cover = getPoint(xS, yS, byteLevel);
        if (cover != null)
            covers.add(cover);
    }

//    private void checkAxis(int xS, int yS, int radius, byte[][] byteLevel, List<Point> covers) {
//
//        // -X AXIS FROM LEFT TO RIGHT - FROM BOMB SPOT GO LEFT
//        Point minusX = getPoint(xS - radius - 1, yS, byteLevel);
//        // Possible cover
//        if (minusX != null) {
//            // look if there is a direct way to the cover
//            for (int x = minusX.x + 1; x < xS; ++x) {
//                // there is a barrier on the way - no good cover
//                if (getPoint(x, yS, byteLevel) == null) {
//                    minusX = null;
//                    break;
//                }
//            }
//            if (minusX != null)
//                covers.add(minusX);
//        }
//
//        // +X AXIS FROM RIGHT TO LEFT - FROM BOMB SPOT GO RIGHT
//        Point plusX = getPoint(xS + radius + 1, yS, byteLevel);
//        // Possible cover
//        if (plusX != null) {
//            // look if there is a direct way to the cover
//            for (int x = plusX.x - 1; x > xS; --x) {
//                // there is a barrier on the way - no good cover
//                if (getPoint(x, yS, byteLevel) == null) {
//                    plusX = null;
//                    break;
//                }
//            }
//            if (plusX != null)
//                covers.add(plusX);
//        }
//
//        // -Y AXIS FROM BOTTOM TO TOP - FROM BOMB SPOT GO DOWN
//        Point minusY = getPoint(xS, yS - radius - 1, byteLevel);
//        // Possible cover
//        if (minusY != null) {
//            // look if there is a direct way to the cover
//            for (int y = minusY.x + 1; y < yS; ++y) {
//                // there is a barrier on the way - no good cover
//                if (getPoint(xS, y, byteLevel) == null) {
//                    minusY = null;
//                    break;
//                }
//            }
//            if (minusY != null)
//                covers.add(minusY);
//        }
//
//        // +Y AXIS FROM TOP TO BOTTOM - FROM BOMB SPOT GO UP
//        Point plusY = getPoint(xS, yS + radius + 1, byteLevel);
//        // Possible cover
//        if (plusY != null) {
//            // look if there is a direct way to the cover
//            for (int y = plusY.x - 1; y > yS; --y) {
//                // there is a barrier on the way - no good cover
//                if (getPoint(xS, y, byteLevel) == null) {
//                    plusY = null;
//                    break;
//                }
//            }
//            if (plusY != null)
//                covers.add(plusY);
//        }
//    }
//
//    private void checkOtherCover(int xS, int yS, byte[][] byteLevel, List<Point> covers) {
//
//        // the current checked cover
//        Point cover = null;
//
//        // @formatter:off        
//        /* THE SCHEMATA FOR FINDING POSSIBLE COVERS
//         * 
//         *      *   6   *   7   *
//         *      5   1   *   2   8
//         *      *   *   B   *   *
//         *      9   3   *   4   11
//         *      *   10  *   12  *
//         * 
//         */
//        // @formatter:on
//
//        // 1
//        cover = getPoint(xS - 1, yS - 1, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//        // 2
//        cover = getPoint(xS + 1, yS - 1, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//        // 3
//        cover = getPoint(xS - 1, yS + 1, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//        // 4
//        cover = getPoint(xS + 1, yS + 1, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//
//        // 5
//        cover = getPoint(xS - 2, yS - 1, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//        // 6
//        cover = getPoint(xS - 1, yS - 2, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//        // 7
//        cover = getPoint(xS + 1, yS - 2, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//        // 8
//        cover = getPoint(xS + 2, yS - 1, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//        // 9
//        cover = getPoint(xS - 2, yS + 1, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//        // 10
//        cover = getPoint(xS - 1, yS + 2, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//        // 11
//        cover = getPoint(xS + 2, yS + 1, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//        // 12
//        cover = getPoint(xS + 1, yS + 2, byteLevel);
//        if (cover != null)
//            covers.add(cover);
//    }
}
