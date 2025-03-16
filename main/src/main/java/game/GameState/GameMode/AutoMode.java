package game.GameState.GameMode;

import game.AST.Strategy;
import game.DataStructure.Pair;
import game.GameState.Leader.BotLeader;

import java.util.Map;

public class AutoMode extends Game {
    public AutoMode(Map<String, Pair<Strategy, Long>> minionKinds){
        this(minionKinds, 8, 8);
    }

    public AutoMode(Map<String, Pair<Strategy, Long>> minionKinds, int rowAmount, int colAmount){
        super(minionKinds, rowAmount, colAmount);
        leader1 = new BotLeader(this, "Bot1", "1");
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3 - i; j++){
                setGridOwner(i, j, leader1);
                leader1.addOwnHex(Pair.of((long)i, (long)j));
            }
        }

        leader2 = new BotLeader(this, "Bot2", "2");
        for(int i = 7; i > 5; i--){
            for(int j = 7; j > 11 - i; j--){
                setGridOwner(i, j, leader2);
                leader2.addOwnHex(Pair.of((long)i, (long)j));
            }
        }
        printOwnerBoard();
    }
}
