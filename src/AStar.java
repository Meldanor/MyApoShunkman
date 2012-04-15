import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import apoSkunkman.ai.ApoSkunkmanAILevel;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public class AStar {

    private AStarLevel level;

    private Point goal;

    private PriorityQueue<Node> openList = new PriorityQueue<Node>();
    private List<Node> closedList = new LinkedList<Node>();

    public AStar(ApoSkunkmanAILevel apoLevel) {
        this.level = new AStarLevel(apoLevel);
    }

    public void update(ApoSkunkmanAILevel apoLevel, Point goal) {
        this.level.update(apoLevel, goal);
        this.goal = goal;
    }

    public void findWay(Point start) {
        // GET START NODE
        Node current = level.getNode(start);
        // ADD IT TO OPEN LIST
        openList.add(current);
        while (!openList.isEmpty()) {

            // REMOVE FIRST NODE
            current = openList.poll();

            closedList.add(current);

            // GOAL FOUND!
            if (current.equals(goal))
                break;

            // GET POSSIBLE NEXT NODES
            List<Node> nextNodes = level.getNext(current);

            // CHECK POSSIBILITIES
            for (Node next : nextNodes) {
                // NODE IS OUTSIDE THE FIELD
                if (next == null)
                    continue;
                // IGNORE NODES WHICH ARE IN CLOSED LIST
                if (closedList.contains(next))
                    continue;
                if (openList.contains(next)) {
                    // NEW PATH IS BETTER
                    if (next.getG() < current.getG()) {
                        // REMOVE AND ADD TO OPENLIST TO RESTORE SORT
                        openList.remove(next);
                        // UPDATE PREV AND G
                        next.setPrev(current);
                        openList.add(next);
                    }
                } else {
                    // NODE IS NOT IN OPEN NOR CLOSED LIST - ADD IT
                    next.setPrev(current);
                    openList.add(next);
                }
            }
        }

    }

    public LinkedList<Node> getPath() {
        LinkedList<Node> list = new LinkedList<Node>();
        list.add(closedList.remove(0));
        for (Node node : closedList)
            list.addFirst(node.getPrev());

        Collections.reverse(list);
        return list;
    }
}
