package GameState.BotLeaderStrategy;
import GameState.Game.Pair;
import GameState.GameMode.Game;
import GameState.Leader.Leader;
import java.util.List;
import java.util.Random;

public class RandomizedSpawnStrategy implements SpawnMinionStrategy{

    private float spawnChange = 0.8f;

    public RandomizedSpawnStrategy(float spawnChange) {
        this.spawnChange = spawnChange;
    }

    public RandomizedSpawnStrategy() {}

    @Override
    public void spawnMinion(Leader leader, double budget, Game game, List<Pair<Long, Long>> ownHex) {
        //System.out.println("Own Hex:" + ownHex.size());
        Random rand = new Random();
        float change = rand.nextFloat();
        if(change > spawnChange) return;
        List<Pair<Long, Long>> availableHexPos = ownHex.stream().filter((pos) -> !game.getHexAt(pos).hasMinionOnHex()).toList();
        //System.out.println("Available Hex:" + availableHexPos.size());
        if (availableHexPos.isEmpty()) return;
        int indexToSpawn = rand.nextInt(availableHexPos.size());
        List<String> availableKind = game.availableMinions();
        int kindToSpawn = rand.nextInt(availableKind.size());
        System.out.println(leader.getLeaderName() + " spawn " + availableKind.get(kindToSpawn) + " at " + availableHexPos.get(indexToSpawn));
        leader.spawnMinionAt(availableHexPos.get(indexToSpawn), availableKind.get(kindToSpawn));
    }
}
