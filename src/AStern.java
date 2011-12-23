import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import apoSkunkman.ai.ApoSkunkmanAIConstants;

public class AStern {

    private byte[][] level;

    private ArrayList<ApoPoint> openList;

    private Point goal;

    public AStern(Point start, Point goal, byte[][] level) {
        ApoPoint ap = new ApoPoint(start, null, goal, 0.0);
        openList = new ArrayList<ApoPoint>(1000);
        openList.add(ap);
        this.goal = goal;
        this.level = level;
    }

    public void findGoal() {

        ArrayList<ApoPoint> closedList = new ArrayList<ApoPoint>(100);

        ApoPoint current = null;
        do {
            // search for minimal cost
            current = Collections.min(openList);
            openList.remove(current);
            if (current.getPoint().equals(goal)) {
                System.out.println("Ziel gefunden!");
                break;
            }
            closedList.add(current);

            ApoPoint[] possibilities = getPossibilites(current);
            for (ApoPoint pos : possibilities) {
                if (pos == null || closedList.contains(pos))
                    continue;
                int index = openList.indexOf(pos);
                if (index != -1) {
                    ApoPoint old = openList.get(index);
                    if (pos.getG() < old.getG()) {
                        pos.setPrevious(current);
                    }
                    continue;
                }

                openList.add(pos);
            }
        }
        while (!openList.isEmpty());
        writeFile();
    }

    private void writeFile() {
        LinkedList<Point> result = getPath();
        try {
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(
                    "C:/Users/Meldanor/Desktop/result.txt"));
            for (Point p : result) {
                bWriter.write("(" + p.x + "," + p.y + ")");
                bWriter.newLine();
            }
            bWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public LinkedList<Point> getPath() {
        if (openList.isEmpty())
            return null;
        LinkedList<Point> result = new LinkedList<Point>();
        for (int i = openList.size() - 1; i >= 0; --i)
            result.add(openList.get(i).getPrevious().getPoint());

        result.add(goal);
        return result;
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
