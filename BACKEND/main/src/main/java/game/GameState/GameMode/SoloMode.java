package game.GameState.GameMode;

import com.example.main.models.GameData;
import game.AST.Strategy;
import game.DataStructure.Pair;
import game.GameState.Leader.BotLeader;
import game.GameState.Leader.PlayerLeader;

import java.util.List;
import java.util.Map;

public class SoloMode extends Game {

    public SoloMode(Map<String, Pair<Strategy, Long>> minionsStrategy, String leader){
        super(minionsStrategy);
        leader1 = new PlayerLeader(this, leader, "" + leader.charAt(0));
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3 - i; j++){
                setGridOwner(i, j, leader1);
                leader1.addOwnHex(Pair.of((long)i, (long)j));
            }
        }

        leader2 = new BotLeader(this, "Bot", "B");

        for(int i = 7; i > 5; i--){
            for(int j = 7; j > 11 - i; j--){
                setGridOwner(i, j, leader2);
                leader2.addOwnHex(Pair.of((long)i, (long)j));
            }
        }
        printOwnerBoard();
    }

//    @Override
//    public List<GameData> processGame() {
//        List<GameData> data = super.processGame();
//        if(turnOf == 2){
//            if(state.equals("buying")){
//                try {
//                    leader2.buyHexState();
//                    data.add(getGameData());
//                }catch (Exception e){
//                    System.out.println(e);
//                }
//            }
//
//            if(state.equals("spawning") || state.equals("first_spawning")){
//                try {
//                    leader2.spawnMinionState();
//                    data.add(getGameData());
//                }catch (Exception e){
//                    System.out.println(e);
//                }
//
//            }
//
//        }
//        return data;
//    }
}
