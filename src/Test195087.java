import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;

import apoSkunkman.ai.ApoSkunkmanAI;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;

/**
 * This AI finds the way to the goal in an empty game without any bushes
 * 
 * @author Meldanor
 * 
 */
public class Test195087 extends ApoSkunkmanAI {

    private LinkedList<Point> path;
    private int index = -1;

    @Override
    public String getPlayerName() {
        return this.getClass().getName();
    }

    @Override
    public String getAuthor() {
        return "Kilian Gaertner";
    }

    @Override
    public void think(ApoSkunkmanAILevel level, ApoSkunkmanAIPlayer player) {
        if (index == -1) {
            AStern a = new AStern(new Point((int) player.getX(),
                    (int) player.getY()), level.getGoalXPoint(),
                    level.getLevelAsByte());
            System.out.println("Starte A* von "
                    + new Point((int) player.getX(), (int) player.getY())
                    + " nach" + level.getGoalXPoint());
            a.findGoal();
            path = a.getPath();
            if (path == null) {
                System.out.println("Kann keinen Weg finden!");
                index = -2;
                return;
            }
            index = 0;
        }
        else if (index == -2)
            return;

        else {
            if (index != -3) {
                System.out.println("LOL");
                visualizeWay(player);
                index = -3;
            }
            System.out.println("Hab gefunden");
            moveToPoint(player, path.removeFirst());

        }
    }

    private void moveToPoint(ApoSkunkmanAIPlayer player, Point p) {
        int xDiff = p.x - (int) player.getX();
        if (xDiff != 0) {
            if (xDiff > 0)
                player.movePlayerRight();
            else
                player.movePlayerLeft();
        }
        int yDiff = p.y - (int) player.getY();
        if (yDiff != 0) {
            if (yDiff > 0)
                player.movePlayerDown();
            else
                player.movePlayerUp();
        }
    }

    private void visualizeWay(ApoSkunkmanAIPlayer player) {
        for (Point p : path)
            player.drawRect(p.x * 20, p.y * 20, 5, 5, true, 10, Color.BLUE);
    }
}
