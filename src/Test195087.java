import apoSkunkman.ai.ApoSkunkmanAI;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;

/**
 * This AI finds the way to the goal in an empty game without any bushes
 * @author Meldanor
 *
 */
public class Test195087 extends ApoSkunkmanAI {

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
        int xDiff = level.getGoalXPoint().x - (int) player.getX();
        if (xDiff != 0) {
            if (xDiff > 0)
                player.movePlayerRight();
            else
                player.movePlayerLeft();
        }
        int yDiff = level.getGoalXPoint().y - (int) player.getY();
        if (yDiff != 0) {
            if (yDiff > 0)
                player.movePlayerDown();
            else
                player.movePlayerUp();
        }

    }
}
