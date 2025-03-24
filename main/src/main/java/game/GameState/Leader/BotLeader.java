package game.GameState.Leader;

import com.example.main.models.GameData;
import game.DataStructure.Pair;
import game.GameState.BotLeaderStrategy.BuyHexStrategy;
import game.GameState.BotLeaderStrategy.RandomizedBuyHexStrategy;
import game.GameState.BotLeaderStrategy.RandomizedSpawnStrategy;
import game.GameState.BotLeaderStrategy.SpawnMinionStrategy;
import game.GameState.Game.Minion;
import game.GameState.GameMode.Game;

import java.util.List;

public class BotLeader extends Leader {

    private SpawnMinionStrategy spawnMinionStrategy;
    private BuyHexStrategy buyHexStrategy;

    public BotLeader(Game game, String leaderName, String topordown) {
        super(game, leaderName,topordown);
        spawnMinionStrategy = new RandomizedSpawnStrategy();
        buyHexStrategy = new RandomizedBuyHexStrategy();
    }

    @Override
    public List<GameData> spawnMinionState() {
        //.out.println("Spawning Minion State");
        Pair<Boolean, List<GameData>> m = spawnMinionStrategy.spawnMinion(this, budget, game, ownHexes);

        if (!m.getFirst()){
            return skipState();
            //m.getSecond().addAll(skipState());
        }
        return m.getSecond();
    }

    @Override
    public List<GameData> buyHexState() {

        Pair<Boolean, List<GameData>> m = buyHexStrategy.buyHex(this, budget, game, ownHexes);
        if (!m.getFirst()){
            return skipState();
            //m.getSecond().addAll(skipState());
        }
        return m.getSecond();
    }
}
