package GameState.GameMode;

import AST.Strategy;
import DataStructure.Pair;
import GameState.Leader.*;
import java.util.Map;

public class SoloMode extends Game{

    public SoloMode(Map<String, Pair<Strategy, Long>> minionsStrategy, String leader){
        super(minionsStrategy);
        leader1 = new PlayerLeader(this, leader, "" + leader.charAt(0));
        leader1.receiveBudget(getSettingValue("init_budget"));
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3 - i; j++){
                setGridOwner(i, j, leader1);
                leader1.addOwnHex(Pair.of((long)i, (long)j));
            }
        }

        leader2 = new BotLeader(this, "Bot", "B");
        leader2.receiveBudget(getSettingValue("init_budget"));
        for(int i = 7; i > 5; i--){
            for(int j = 7; j > 11 - i; j--){
                setGridOwner(i, j, leader2);
                leader2.addOwnHex(Pair.of((long)i, (long)j));
            }
        }
        printOwnerBoard();
    }
}
