package Game;

import MinionStrategy.Strategy;

import java.util.Map;

public class DuelMode extends Game{

    public DuelMode(Map<String, Strategy> minionsStrategy){
        super(minionsStrategy);
        leader1 = new PlayerLeader(this, "Player1", "1");
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3 - i; j++){
                setGridOwner(i, j, leader1);
                leader1.addOwnHex(Pair.of((long)i, (long)j));
            }
        }

        leader2 = new PlayerLeader(this, "Player2", "2");
        for(int i = 7; i > 5; i--){
            for(int j = 7; j > 11 - i; j--){
                setGridOwner(i, j, leader2);
                leader2.addOwnHex(Pair.of((long)i, (long)j));
            }
        }
        printOwnerBoard();
    }
}
