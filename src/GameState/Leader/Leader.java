package GameState.Leader;

import GameState.GameMode.Game;
import GameState.Game.*;

import java.util.*;
import java.util.Scanner;

public abstract class Leader {
    protected String leaderName;
    public String leaderSymbol;
    protected List<Minion> ownedMinions = new ArrayList<Minion>();
    protected double budget;
    protected Map<String, Long> globalVariables = new HashMap<>();
    protected List<Pair<Long, Long>> ownHexes = new ArrayList<>();
    protected Game game;
    protected long turnCount;
    protected boolean firstBuyMinion = true;

    public Leader(Game game, String leaderName, String leaderSymbol) {
        this.game = game;
        this.budget = game.getSettingValue("init_budget");
        this.leaderName = leaderName;
        this.leaderSymbol = leaderSymbol;
    }

    private void executeMinionsStrategy() throws Exception {
        for (Minion m : ownedMinions) {
            if (budget <= 0) return;
            m.execute();
        }
    }

    public boolean reduceBudget(long amount) {
        if (budget < amount) return false;
        budget -= amount;
        return true;
    }

    public void removeMinion(Minion minion) {
        ownedMinions.remove(minion);
    }


    public void receiveBudget(double amount){
        if(budget + amount > game.getSettingValue("max_budget")){
            System.out.println("Over Budget");
            budget = game.getSettingValue("max_budget");
            return;
        }
        budget += amount;
    }


    public long getSpawnRemain() {
        return game.getSettingValue("max_spawns") - ownedMinions.size();
    }

    public List<Minion> getMinionList() {
        return ownedMinions;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void turnBegin(int turn) throws Exception {
        turnCount = turn;
        if (turn == 0) {
            while (ownedMinions.isEmpty()) spawnMinionState();
            return;
        }

        buyHexState();
        spawnMinionState();
    }

    public void turnEnd() throws Exception {
        executeMinionsStrategy();
    }

    public void spawnMinionState() throws Exception {
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("wanna buy minnion y/n");
            String input = sc.nextLine();

            if (input.equals("y")) {
                System.out.println("Available Minions");
                for(String minion : game.availableMinions()){
                    System.out.print(minion + ", ");
                }
                System.out.println();
                System.out.print("What type of minion to spawn: ");
                String kindToSpawn = sc.nextLine();
                if(!game.availableMinions().contains(kindToSpawn)){
                    System.out.println("Minion doesn't exist");
                    continue;
                }
                System.out.println("where you want to spawn minion");
                long I1 = sc.nextLong();
                long I2 = sc.nextLong();
                if (spawnMinionAt(Pair.of(I1, I2), kindToSpawn)) {
                    break;
                }
            } else if (input.equals("n")) {
                break;
            }
        }
    }

    public void buyHexState() throws Exception {
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("wanna get hex y/n ");
            String input = sc.nextLine();
            if (input.equals("y")) {
                System.out.println("where");
                long I1 = sc.nextLong();
                long I2 = sc.nextLong();
                Pair<Long, Long> position = new Pair<>(I1, I2);
                if (buyHex(position)) break;
            } else if (input.equals("n")) {
                break;
            }
        }
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
    }

    /**
     * add position of hex that belong to leader into list
     *
     * @param ownHexes position of hex
     */
    public void addOwnHex(Pair<Long, Long> ownHexes) {
        this.ownHexes.add(ownHexes);
    }

    /**
     * receive game that leader playing on
     *
     * @return game that leader playing on
     */
    public Game getGame() {
        return game;
    }

    /**
     * set value of specific global variable of leader
     *
     * @param name  name of variable to set
     * @param value value of variable to set
     * @effect may result in change value in globalVariables
     */
    public void setGlobalVariable(String name, long value) {
        globalVariables.put(name, value);
    }

    /**
     * receive value of specific global variable of leader
     *
     * @param variable name of variable to get value
     * @return value of specific variable, 0 if variable isn't set before
     * @effect may result in set variable to 0 when variable has never set before
     * => variable is put into globalVariables with value 0
     */
    public long getGlobalVariable(String variable) {
        if (!globalVariables.containsKey(variable)) globalVariables.put(variable, 0L);
        return globalVariables.get(variable);
    }

    /**
     * method spawn minion with specific type onto specific hexPosition
     *
     * @param hexPosition position of (row, col) to spawn a minion
     * @param minionType  name of minion type to spawn
     * @return true if success to spawn minion, otherwise false
     * @effect get "spawn_cost" value from game setting
     * @effect may result in change of budget if success to spawn
     * @effect check hex of position to spawn in hex grid of game
     */
    public boolean spawnMinionAt(Pair<Long, Long> hexPosition, String minionType) {
        if ((budget < game.getSettingValue("spawn_cost") && !firstBuyMinion)
                || game.hasMinionAt(hexPosition.getFirst(), hexPosition.getSecond())
                || !game.getHexAt(hexPosition).getLeader().equals(this)
                || ownedMinions.size() >= game.getSettingValue("max_spawns")
        ) {
            System.out.println("Unable to spawn minion");
            return false;
        }

        budget -= game.getSettingValue("spawn_cost");
        Minion m = game.spawnMinion(hexPosition.getFirst(), hexPosition.getSecond(), minionType, this);
        ownedMinions.add(m);
        return true;
    }

    /**
     * Get leader's interest percentage rate of current budget base on current turn
     *
     * @return interest percentage rate of leader
     */
    public long getInterest() {
        return (long) game.calculateInterestPercentage(budget);
    }

    /**
     * Get current budget of leader
     *
     * @return current budget of leader
     */
    public long getBudget() {
        return (long) budget;
    }

    public double getExactBudget(){
        return budget;
    }

    /**
     * Add leader's budget from turn_budget and interest but will not exceed limit value
     * *** Still thinking ***
     * @effect may result in change of budget if budget isn't at limit
     * @effect get turn_budget and max_budget from game setting
     */
    private void receiveTurnBudgetAndInterest() {
        budget = Math.min(game.getSettingValue("max_budget"), budget + game.calculateInterest(budget));
        budget = Math.max(game.getSettingValue("max_budget"), budget + game.getSettingValue("turn_budget"));
    }

    /**
     * @return list of leader's minions
     */
    public List<Minion> getOwnedMinions() {
        return ownedMinions;
    }
}