package Game;

import java.util.*;

public abstract class Leader {
    protected String leaderName;
    private List<Minion> ownedMinions = new ArrayList<Minion>();
    private double budget;
    private Map<String, Integer> globalVariables = new HashMap<>();
    private List<Pair<Integer, Integer>> ownHexes = new ArrayList<>();
    private List<Pair<Integer, Integer>> buyableHexes = new ArrayList<>();
    private Game game;

    public Leader(Game game, String leaderName) {
        this.game = game;
        this.leaderName = leaderName;
    }

    public void executeMinionsStrategy() throws Exception {
        for(Minion m : ownedMinions){
            m.execute();
        }
    }

    public int getSpawnRemain(){
        return game.getSettingValue("max_spawns") - ownedMinions.size();
    }

    public void turnBegin(){

    }

    public void turnEnd(){

    }

    public boolean buyHex(Pair<Integer, Integer> hexPosition){
        return false;
    }

    public Game getGame() {
        return game;
    }

    public boolean buyMinionAndPlaceAt(Pair<Integer, Integer> hexPosition, Minion minion) throws Exception {
        return false;
    }

    public int getInterest(){
        return 0;
    }

    public int getBudget(){
        return (int)budget;
    }

    public List<Minion> getOwnedMinions(){
        return ownedMinions;
    }
}
