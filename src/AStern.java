import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import apoSkunkman.ai.ApoSkunkmanAIConstants;

public class AStern {

    private byte[][] level;

    private ArrayList<ApoPoint> openList = new ArrayList<ApoPoint>(1000);

    private Point goal;

    public AStern(Point start, Point goal, byte[][] level) {
        ApoPoint ap = new ApoPoint(start, null, goal, 0.0);
        openList.add(ap);
        this.goal = goal;
        this.level = level;
    }

    public void findGoal() {

        ArrayList<ApoPoint> closedList = new ArrayList<ApoPoint>(100);

        ApoPoint current = null;
        do {
            // search for minimal cost
            current = openList.remove(getMin());
            if (current.getPoint().equals(goal))
                break;
            closedList.add(current);

            ApoPoint[] possibilities = getPossibilites(current);
            for (ApoPoint pos : possibilities) {
                if (pos == null)
                    continue;
                int index = openList.indexOf(pos);
                if (index != -1) {
                    ApoPoint old = openList.get(index);
                    if (pos.getG() < old.getG())
                        old.setPrevious(pos.getPrevious());
                    continue;
                }

                index = closedList.indexOf(pos);
                if (index != -1)
                    continue;

                openList.add(pos);
            }
        }
        while (!openList.isEmpty());
    }

    public ArrayList<ApoPoint> getPath() {
        if (openList.isEmpty())
            return null;
        Collections.reverse(openList);
        return openList;
    }

    public int getMin() {

        double minF = openList.get(0).getF();
        double minH = openList.get(0).getH();
        int index = 0;
        for (int i = 1; i < openList.size(); ++i) {
            ApoPoint apo = openList.get(0);
            if (apo.getF() < minF || (apo.getF() == minF && apo.getH() < minH)) {
                index = i;
                minF = apo.getF();
                minH = apo.getH();
            }
        }
        return index;
    }

    public ApoPoint[] getPossibilites(ApoPoint current) {

        ArrayList<ApoPoint> possible = new ArrayList<ApoPoint>(4);
        int x = current.getX();
        int y = current.getY();
        double G = current.getG() + 1.0;
        if (level[y][x + 1] == ApoSkunkmanAIConstants.LEVEL_FREE)
            possible.add(new ApoPoint(x + 1, y, current, goal, G));
        if (level[y][x - 1] == ApoSkunkmanAIConstants.LEVEL_FREE)
            possible.add(new ApoPoint(x - 1, y, current, goal, G));
        if (level[y + 1][x] == ApoSkunkmanAIConstants.LEVEL_FREE)
            possible.add(new ApoPoint(x, y + 1, current, goal, G));
        if (level[y - 1][x] == ApoSkunkmanAIConstants.LEVEL_FREE)
            possible.add(new ApoPoint(x, y - 1, current, goal, G));

        possible.remove(current.getPrevious());
        System.out.println(Arrays.toString(possible
                .toArray(new ApoPoint[possible.size()])));
        return possible.toArray(new ApoPoint[possible.size()]);
    }

}
