package game.GameState.BotLeaderStrategy;

import com.example.main.models.GameData;
import game.DataStructure.Pair;
import game.GameState.Game.Hex;
import game.GameState.GameMode.Game;
import game.GameState.Leader.BotLeader;
import game.GameState.Leader.Leader;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomizedBuyHexStrategy implements BuyHexStrategy {

    private float buyChange = 0.8f;

    public RandomizedBuyHexStrategy(float buyChange) {
        this.buyChange = buyChange;
    }

    public RandomizedBuyHexStrategy() {}


    @Override
    public Pair<Boolean, List<GameData>> buyHex(Leader leader, double budget, Game game, List<Pair<Long, Long>> ownHex) {
        Random rand = new Random();
        float change = rand.nextFloat();
        if(change > buyChange) return Pair.of(false, null);
        Set<Hex> adjacencyHex = new HashSet<>();
        for(Pair<Long, Long> hexPos : ownHex) {
            adjacencyHex.addAll(game.adjacentHex(hexPos));
        }
        List<Hex> availableHex = adjacencyHex.stream().filter((hex) -> !hex.hasOwner()).toList();
        if(availableHex.isEmpty()) return Pair.of(false, null);
        int indexToBuy = rand.nextInt(availableHex.size());
        System.out.println(leader.getLeaderName() + " by hex at " + availableHex.get(indexToBuy).getPosition());
        Pair<Integer, Integer> pos = availableHex.get(indexToBuy).getPosition();
        return leader.buyHex(Pair.of(pos.getFirst().longValue(), pos.getSecond().longValue()));
    }
}
