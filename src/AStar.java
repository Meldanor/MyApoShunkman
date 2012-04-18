import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeSet;

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
    private TreeSet<Node> closedList = new TreeSet<Node>();

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
            // O(1)
            current = openList.poll();

            closedList.add(current);

            // GOAL FOUND!
            if (current.equals(goal))
                return;

            // GET POSSIBLE NEXT NODES
            Node[] nextNodes = level.getNext(current);

            // CHECK POSSIBILITIES
            for (Node next : nextNodes) {
                // NODE IS OUTSIDE THE FIELD
                if (next == null)
                    continue;
                // IGNORE NODES WHICH ARE IN CLOSED LIST
                // O(LOG(N))
                if (closedList.contains(next))
                    continue;
                // O(N)
                if (openList.contains(next)) {
                    // NEW PATH IS BETTER
                    if (next.getG() < current.getG()) {
                        // REMOVE AND ADD TO OPENLIST TO RESTORE SORT
                        // O(N)
                        openList.remove(next);
                        // UPDATE PREV AND G
                        next.setPrev(current);
                        // LOG(N)
                        openList.add(next);
                    }
                } else {
                    // NODE IS NOT IN OPEN NOR CLOSED LIST - ADD IT
                    next.setPrev(current);
                    // LOG(N)
                    openList.add(next);
                }
            }
        }

        // TODO: HAVE TO THINK WHAT TO DO!
        throw new RuntimeException("Kein Weg gefunden!");
    }

    public LinkedList<Node> getPath() {
        LinkedList<Node> list = new LinkedList<Node>();
        Node node = closedList.first();
        list.add(node);

        while (node.getPrev() != null) {
            node = node.getPrev();
            list.add(node);
        }

        Collections.reverse(list);
        list.removeFirst();
        return list;
    }
}
