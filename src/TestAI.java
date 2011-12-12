import java.util.Random;

import apoSkunkman.ai.ApoSkunkmanAI;
import apoSkunkman.ai.ApoSkunkmanAILevel;
import apoSkunkman.ai.ApoSkunkmanAIPlayer;

public class TestAI extends ApoSkunkmanAI {

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
        // Do something really special
    }
    
    

}
