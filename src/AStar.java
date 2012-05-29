import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

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

    // THE LEVEL NEVER CHANGE - JUST THE VALUES
    private AStarLevel level;

    // THE CURRENT GOAL - CAN CHANGE
    private Point goal;

    // OPENLIST AS BINARY HEAP QUEUE
    private PriorityQueue<Node> openList = new PriorityQueue<Node>();
    // CLOSED LIST BASED OF AN HASH SET
    private Set<Node> closedList = new HashSet<Node>();
    // THE LAST NODE ADDED TO CLOSED LIST
    private Node lastNode;

    public AStar(ApoSkunkmanAILevel apoLevel) {
        this.level = new AStarLevel(apoLevel);
    }

    // UPDATE THE LEVEL
    public void update(ApoSkunkmanAILevel apoLevel, Point goal) {
        this.goal = goal;
        this.level.update(apoLevel, goal);
    }

    public void findWay(Point start, boolean onlyFree) {
        // GET START NODE
        Node current = level.getNode(start.x, start.y, onlyFree);
        // ADD IT TO OPEN LIST
        openList.add(current);
        while (!openList.isEmpty()) {

            // REMOVE FIRST NODE
            // O(1)
            current = openList.poll();

            // ADD TO CLOSED LIST
            // O(1)
            closedList.add(current);

            // TEMP SAVE THE LAST ADDED NODE
            lastNode = current;

            // GOAL FOUND - STOP ASTAR
            if (current.equals(goal))
                return;

            // GET POSSIBLE NEXT NODES
            Node[] neighbors = level.getNeighbors(current, onlyFree);

            // CHECK POSSIBILITIES
            for (Node neighbor : neighbors) {
                // NODE IS OUTSIDE THE FIELD
                if (neighbor == null)
                    continue;
                // IGNORE NODES WHICH ARE IN CLOSED LIST
                if (closedList.contains(neighbor))
                    continue;
                // O(N)
                if (openList.contains(neighbor)) {
                    // NEW PATH IS BETTER
                    if (neighbor.getG() < current.getG()) {
                        // REMOVE AND ADD TO OPENLIST TO RESTORE SORT
                        // O(N)
                        openList.remove(neighbor);
                        // UPDATE PREV AND G
                        neighbor.setPrev(current);
                        // LOG(N)
                        openList.add(neighbor);
                    }
                } else {
                    // NODE IS NOT IN OPEN NOR CLOSED LIST - ADD IT
                    neighbor.setPrev(current);
                    // LOG(N)
                    openList.add(neighbor);
                }
            }
        }

        // NO WAY FOUND
        closedList = null;
    }

    // GENERATE THE FOUND PATH
    public LinkedList<Node> getPath() {

        // NO WAY FOUND
        if (closedList == null)
            return null;

        // THE PATH
        LinkedList<Node> list = new LinkedList<Node>();
        // GET LAST ADDED NODE
        Node node = lastNode;
        list.add(node);

        // ITERATE BACKWARDS THROUGH THE NODES
        while (node.getPrev() != null) {
            node = node.getPrev();
            list.addFirst(node);
        }

        // REMOVE POINT OF PLAYER FIGURE, BECAUSE WE DON'T NEED TO GO THERE
        list.removeFirst();
        return list;
    }
}
