package Game;

import java.util.*;

public abstract class Leader {
    protected String leaderName;
    public String topordown;
    private List<Minion> ownedMinions = new ArrayList<Minion>();
    private double budget;
    private Map<String, Long> globalVariables = new HashMap<>();
    private List<Pair<Long, Long>> ownHexes = new ArrayList<>();
    private Game game;
    private long turnCount;

    public Leader(Game game, String leaderName ,String topordown) {
        this.game = game;
        this.budget = game.getSettingValue("init_budget");
        this.leaderName = leaderName;
        this.topordown = topordown;
    }

    public void executeMinionsStrategy() throws Exception {
        for(Minion m : ownedMinions){
            m.execute();
        }
    }

    public long getSpawnRemain(){
        return game.getSettingValue("max_spawns") - ownedMinions.size();
    }

    public String getName(){
        return leaderName;
    }

    public void turnBegin(){
        receiveInterest();
        buyHexStage();
        buyMinionStage();
    }

    public void turnEnd() throws Exception {
        executeMinionsStrategy();
    }

    public void buyHexStage(){

    }

    public void buyMinionStage(){

    }

    public boolean buyHex(Pair<Long, Long> hexPosition) throws Exception { //tempo Pair<Integer, Integer> hexPosition
        if (budget < game.getSettingValue("hex_purchase_cost")
            || game.hasMinionAt(hexPosition.getFirst(), hexPosition.getSecond())
                || game.getHexAt(hexPosition).hasOwner())
            return false;

        budget -= game.getSettingValue("hex_purchase_cost");
        game.buyHexAt(this, hexPosition.getFirst(), hexPosition.getSecond());
        ownHexes.add(Pair.of(hexPosition.getFirst(), hexPosition.getSecond()));
        return true;

//        if(budget>=Constants.hex_price){
//            budget -= Constants.hex_price;
//            //ownHexes.add(Pair.of(hexPosition.getFirst(), hexPosition.getSecond()));
//            return true;
//        }
//        return false;
    }

    public Game getGame() {
        return game;
    }

    public void setGlobalVariable(String name, long value){
        globalVariables.put(name, value);
    }

    public long getGlobalVariable(String name){
        if (!globalVariables.containsKey(name)) globalVariables.put(name, 0L);
        return globalVariables.get(name);
    }

    public boolean buyMinionAndPlaceAt(Pair<Long, Long> hexPosition, String minionType) throws Exception {
        if(budget < game.getSettingValue("spawn_cost")
            || game.hasMinionAt(hexPosition.getFirst(), hexPosition.getSecond())
                || !game.getHexAt(hexPosition).getLeader().equals(this)
        ) return false;

        budget -= game.getSettingValue("spawn_cost");
        Minion m = game.placeMinion(hexPosition.getFirst(), hexPosition.getSecond(), minionType, this);
        ownedMinions.add(m);
        return true;

//        if(budget>=Constants.minion_price && ownHexes.contains(Pair.of(hexPosition.getFirst(), hexPosition.getSecond()))){
//            budget -= Constants.minion_price;
//            Minion M01 = new Minion(hexPosition.getFirst(), hexPosition.getSecond() , this);
//
//            ownedMinions.add(M01);
//            return true;
//        }
//        return false;
    }

    private double calculateInterestPercentage(){
        boolean DONT_HAVE_BUDGET = budget == 0;
        long baseInterest = game.getSettingValue("interest_pct");
        if (DONT_HAVE_BUDGET) return 0;
        return baseInterest * Math.log10(budget) * Math.log(turnCount);
    }

    private double calculateInterest(){
        return calculateInterestPercentage() * budget / 100.0;
    }

    public long getInterest(){

        return (long)calculateInterest();
    }

    public long getBudget(){
        return (long)budget;
    }

    public double BudgetplusCal(){
        budget += getInterest();
        budget += Constants.turn_income;
        return (int)budget;
    }

    private void receiveInterest(){
        budget = Math.max(game.getSettingValue("max_budget"), budget + calculateInterest());
    }

    public List<Minion> getOwnedMinions(){
        return ownedMinions;
    }
}