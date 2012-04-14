import apoSkunkman.ai.ApoSkunkmanAI;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;

public class Meldanor extends ApoSkunkmanAI {

    @Override
    public String getPlayerName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getAuthor() {
        return "Kilian Gaertner";
    }

    @Override
    public void think(ApoSkunkmanAILevel level, ApoSkunkmanAIPlayer player) {
        player.movePlayerDown();

    }

}
