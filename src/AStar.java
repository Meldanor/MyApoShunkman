import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;

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

    private byte[][] level;

    // OPEN LIST - BINARY HEAP IMPLEMENTATION
    private LinkedList<Node> openList = new LinkedList<Node>();
    // CLOSED LIST - THE PATH
    private LinkedList<Node> closedList = new LinkedList<Node>();

    // PLAYERS POSITION
    private Point playerPosition;
    // TARGETS POSITION
    private Point goalPosition;

    public AStar(byte[][] level, Point playerPosition, Point goalPosition) {
        this.level = level;
        this.playerPosition = playerPosition;
        this.goalPosition = goalPosition;
    }

    public void calculate() {
        // START POINT
        Node current = new Node(playerPosition.x, playerPosition.y, 0, goalPosition, level);
        openList.add(current);

        Node[] result = null;
        int index = 0;

        // A-STERN
        while (!current.equals(goalPosition)) {
            System.out.println(current);
            // LOOK AT NODES AROUND THE CURRENT
            result = getNext(current);

            for (Node n : result) {
                if (n != null) {
                    // NODE WAS ALREADY VISITED
                    if (closedList.contains(n))
                        continue;
                    // NODE IS IN OPENLIST -> CHECK PATH LENGTH
                    else if ((index = openList.indexOf(n)) != -1) {
                        Node temp = openList.get(index);
                        // IF NEW PATH IS BETTER DELETE THE OLD
                        if (n.getG() < temp.getG()) {
                            n.setPrev(current);
                            openList.remove(index);
                            openList.add(n);
                        }
                    }
                    // NOT IN OPEN OR CLOSED LIST
                    else
                        openList.add(n);
                }
            }

            // SORT THE LIST - FUCK PERFORMANCE :D
            Collections.sort(openList);
            // REMOVE MIN
            current = openList.remove(0);
            // NO WAY TO GOAL WHEN LIST IS EMPTY
            if (current == null)
                throw new RuntimeException("Can't find a way to the goal!");
        }

    }

    public LinkedList<Node> getWay() {
        Collections.reverse(closedList);
        return closedList;
    }

    // GET NEXT POSSIBILITES TO GO
    private Node[] getNext(Node current) {
        Node[] result = new Node[4];

        int x = current.x;
        int y = current.y;
        int G = current.getG() + 1;

        result[0] = x >= 1 ? new Node(x - 1, y, G, goalPosition, level) : null;
        result[1] = x < level[0].length - 1 ? new Node(x + 1, y, G, goalPosition, level) : null;
        result[2] = y >= 1 ? new Node(x, y - 1, G, goalPosition, level) : null;
        result[3] = y < level.length - 1 ? new Node(x, y + 1, G, goalPosition, level) : null;

        return result;
    }

}
