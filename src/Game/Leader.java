package Game;

import java.util.*;

public abstract class Leader {
    protected String leaderName;
    public String topordown;
    private List<Minion> ownedMinions = new ArrayList<Minion>();
    private double budget;
    private Map<String, Integer> globalVariables = new HashMap<>();
    private List<Pair<Integer, Integer>> ownHexes = new ArrayList<>();
    private List<Pair<Integer, Integer>> buyableHexes = new ArrayList<>();   ///???
    private Game game;

    public Leader(Game game, String leaderName ,String topordown) {
        this.game = game;
        this.leaderName = leaderName;
        this.topordown = topordown;
    }

    public void executeMinionsStrategy() throws Exception {
        for(Minion m : ownedMinions){
            m.execute();
        }
    }

    public int getSpawnRemain(){
        return game.getSettingValue("max_spawns") - ownedMinions.size();
    }

    public String getName(){
        return leaderName;
    }

    public void turnBegin(){

    }

    public void turnEnd(){

    }

    public boolean buyHex(Pair<Integer, Integer> hexPosition){ //tempo Pair<Integer, Integer> hexPosition
        if(budget>=Constants.hex_price){
            budget -= Constants.hex_price;
            ownHexes.add(Pair.of(hexPosition.getFirst(), hexPosition.getSecond()));
            return true;
        }


        return false;
    }

    public Game getGame() {
        return game;
    }

    public boolean buyMinionAndPlaceAt(Pair<Integer, Integer> hexPosition, Minion minion) throws Exception {
        if(budget>=Constants.minion_price && ownHexes.contains(Pair.of(hexPosition.getFirst(), hexPosition.getSecond()))){
            budget -= Constants.minion_price;
            Minion M01 = new Minion(hexPosition.getFirst(), hexPosition.getSecond() , this);

            ownedMinions.add(M01);


            return true;
        }
        return false;
    }

    public int getInterest(){
        return (int) budget*Constants.interest/100; //need fix
    }

    public int getBudget(){
        return (int)budget;
    }

    public double BudgetplusCal(){
        budget += getInterest();
        budget += Constants.turn_income;
        return (int)budget;
    }

    public List<Minion> getOwnedMinions(){
        return ownedMinions;
    }
}