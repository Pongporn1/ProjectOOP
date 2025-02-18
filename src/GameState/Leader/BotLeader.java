package GameState.Leader;

import GameState.BotLeaderStrategy.BuyHexStrategy;
import GameState.BotLeaderStrategy.RandomizedSpawnStrategy;
import GameState.BotLeaderStrategy.SpawnMinionStrategy;
import GameState.GameMode.Game;

public class BotLeader extends Leader{

    private SpawnMinionStrategy spawnMinionStrategy;
    private BuyHexStrategy buyHexStrategy;

    public BotLeader(Game game, String leaderName, String topordown) {
        super(game, leaderName,topordown);
        spawnMinionStrategy = new RandomizedSpawnStrategy();
    }

    @Override
    public void spawnMinionState() throws Exception {
        System.out.println("Spawning Minion State");
        spawnMinionStrategy.spawnMinion(this, budget, game, ownHexes);
    }

    @Override
    public void buyHexState() throws Exception {
        if(buyHexStrategy != null) buyHexStrategy.buyHex();
    }
}
