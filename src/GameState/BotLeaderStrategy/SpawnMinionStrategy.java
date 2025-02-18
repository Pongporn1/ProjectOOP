package GameState.BotLeaderStrategy;

import GameState.Game.Hex;
import GameState.Game.Pair;
import GameState.GameMode.Game;
import GameState.Leader.Leader;

import java.util.List;

public interface SpawnMinionStrategy {
    void spawnMinion(Leader leader, double budget, Game game, List<Pair<Long, Long>> ownHex);
}
