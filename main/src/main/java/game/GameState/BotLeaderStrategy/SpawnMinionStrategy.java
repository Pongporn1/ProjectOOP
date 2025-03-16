package game.GameState.BotLeaderStrategy;

import com.example.main.models.GameData;
import game.DataStructure.Pair;
import game.GameState.Game.Minion;
import game.GameState.GameMode.Game;
import game.GameState.Leader.Leader;

import java.util.List;

public interface SpawnMinionStrategy {
    Pair<Boolean, List<GameData>> spawnMinion(Leader leader, double budget, Game game, List<Pair<Long, Long>> ownHex);
}
