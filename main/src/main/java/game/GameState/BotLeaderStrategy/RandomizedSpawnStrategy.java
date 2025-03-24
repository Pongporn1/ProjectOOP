package game.GameState.BotLeaderStrategy;
import com.example.main.models.GameData;
import game.DataStructure.Pair;
import game.GameState.Game.Minion;
import game.GameState.GameMode.Game;
import game.GameState.Leader.Leader;

import java.util.List;
import java.util.Random;

public class RandomizedSpawnStrategy implements SpawnMinionStrategy {

    private float spawnChange = 0.8f;

    public RandomizedSpawnStrategy(float spawnChange) {
        this.spawnChange = spawnChange;
    }

    public RandomizedSpawnStrategy() {}

    @Override
    public Pair<Boolean, List<GameData>> spawnMinion(Leader leader, double budget, Game game, List<Pair<Long, Long>> ownHex) {
        Random rand = new Random();
        float change = rand.nextFloat();

        if(change > spawnChange && !leader.isFirstBuyMinion()) return Pair.of(Boolean.FALSE, null);
        List<Pair<Long, Long>> availableHexPos = ownHex.stream().filter((pos) -> !game.getHexAt(pos).hasMinionOnHex()).toList();
        if (availableHexPos.isEmpty()) return Pair.of(Boolean.TRUE, null);
        int indexToSpawn = rand.nextInt(availableHexPos.size());
        List<String> availableKind = game.availableMinions();
        int kindToSpawn = rand.nextInt(availableKind.size());
        //System.out.println(leader.getLeaderName() + " spawn " + availableKind.get(kindToSpawn) + " at " + availableHexPos.get(indexToSpawn));
        return leader.spawnMinionAt(availableHexPos.get(indexToSpawn), availableKind.get(kindToSpawn));
    }
}
