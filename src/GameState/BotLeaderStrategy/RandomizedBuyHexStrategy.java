package GameState.BotLeaderStrategy;

import DataStructure.Pair;
import GameState.Game.Hex;
import GameState.GameMode.Game;
import GameState.Leader.Leader;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomizedBuyHexStrategy implements BuyHexStrategy{

    private float buyChange = 0.8f;

    public RandomizedBuyHexStrategy(float buyChange) {
        this.buyChange = buyChange;
    }

    public RandomizedBuyHexStrategy() {}


    @Override
    public void buyHex(Leader leader, double budget, Game game, List<Pair<Long, Long>> ownHex) {
        Random rand = new Random();
        float change = rand.nextFloat();
        if(change > buyChange) return;
        Set<Hex> adjacencyHex = new HashSet<>();
        for(Pair<Long, Long> hexPos : ownHex) {
            adjacencyHex.addAll(game.adjacentHex(hexPos));
        }
        List<Hex> availableHex = adjacencyHex.stream().filter((hex) -> !hex.hasOwner()).toList();
        if(availableHex.isEmpty()) return;
        int indexToBuy = rand.nextInt(availableHex.size());
        System.out.println(leader.getLeaderName() + " by hex at " + availableHex.get(indexToBuy).getPosition());
        Pair<Integer, Integer> pos = availableHex.get(indexToBuy).getPosition();
        leader.buyHex(Pair.of(pos.getFirst().longValue(), pos.getSecond().longValue()));
    }
}
