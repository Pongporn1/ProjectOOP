package game.GameState.BotLeaderStrategy;

import com.example.main.models.GameData;
import game.DataStructure.Pair;
import game.GameState.GameMode.Game;
import game.GameState.Leader.Leader;

import java.util.List;

public interface BuyHexStrategy {
    Pair<Boolean, List<GameData>> buyHex(Leader leader, double budget, Game game, List<Pair<Long, Long>> ownHex);
}
