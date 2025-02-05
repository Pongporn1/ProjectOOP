package Game;

import java.util.*;
import java.util.Scanner;

public abstract class Leader {
    protected String leaderName;
    public String topordown;
    protected List<Minion> ownedMinions = new ArrayList<Minion>();
    protected double budget;
    protected Map<String, Long> globalVariables = new HashMap<>();
    protected List<Pair<Long, Long>> ownHexes = new ArrayList<>();
    protected Game game;
    protected long turnCount;
    protected boolean firstBuyMinion = true;

    public Leader(Game game, String leaderName, String topordown) {
        this.game = game;
        this.budget = game.getSettingValue("init_budget");
        this.leaderName = leaderName;
        this.topordown = topordown;
    }

    public void executeMinionsStrategy() throws Exception {
        for (Minion m : ownedMinions) {
            m.execute();
        }
    }

    public long getSpawnRemain() {
        return game.getSettingValue("max_spawns") - ownedMinions.size();
    }

    public void setBuyableHexes() {

//        for(Pair<Long, Long> p : ownHexes){
//
//            int[][] metrix = new int[8][2] ;
//
//             metrix[0][0] = p.getFirst()+1;         metrix[0][1] = p.getSecond();
//             metrix[1][0] = p.getFirst()+1;         metrix[1][1] = p.getSecond()+1;
//             metrix[2][0] = p.getFirst();         metrix[2][1] = p.getSecond()+1;
//             metrix[3][0] = p.getFirst()-1;         metrix[3][1] = p.getSecond()+1;
//             metrix[4][0] = p.getFirst()-1;         metrix[4][1] = p.getSecond();
//             metrix[5][0] = p.getFirst()-1;         metrix[5][1] = p.getSecond()-1;
//             metrix[6][0] = p.getFirst();         metrix[6][1] = p.getSecond()-1;
//             metrix[7][0] = p.getFirst()+1;         metrix[7][1] = p.getSecond()-1;
//             for(int i =0;i<8;i++) {
//              if(0 <= metrix[i][0] && metrix[i][0] < 8 &&0 <= metrix[i][1] && metrix[i][1] < 8) {
//                  Pair<Integer,Integer> X = new Pair<>(metrix[i][0], metrix[i][1]);
//
//                  if(!ownHexes.contains(X) && !buyableHexes.contains(X)) { //need fix equal
//                      buyableHexes.add(Pair.of(metrix[i][0], metrix[i][1]));
//                      System.out.println(i);
//                      System.out.println("add " + metrix[i][0] + " " + metrix[i][1]);
//
//                  }
//              }
//             }
//        }
    }

    public String getName() {
        return leaderName;
    }

    public void turnBegin(int turn) throws Exception {
        if (turn == 0) {
            while (ownedMinions.isEmpty()) {
                Scanner sc = new Scanner(System.in);
                System.out.println("where you want to spawn minion");
                long I1 = sc.nextLong();
                long I2 = sc.nextLong();
                System.out.println("I1: " + I1);
                System.out.println("I2: " + I2);
                buyMinionAndPlaceAt(Pair.of(I1, I2), "Proto");
            }
            return;
        }
        receiveTurnBudgetAndInterest();
        buyHexState();
        buyMinionState();
    }

    public void turnEnd() throws Exception {
        executeMinionsStrategy();
        game.printOwnerBoard();
        System.out.println("---------------------------");
        game.printMinionBoard();
    }

    public void buyMinionState() throws Exception {
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("wanna buy minnion y/n");
            String input = sc.nextLine();
            if (input.equals("y")) {
                System.out.println("where");
                long I1 = sc.nextLong();
                long I2 = sc.nextLong();
                if (buyMinionAndPlaceAt(Pair.of(I1, I2), "Proto")) {
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

//        if(budget>=Constants.hex_price){
//            budget -= Constants.hex_price;
//            //ownHexes.add(Pair.of(hexPosition.getFirst(), hexPosition.getSecond()));
//            return true;
//        }
//        return false;
    }

    public void addOwnHex(Pair<Long, Long> ownHexes) {
        this.ownHexes.add(ownHexes);
    }

    public Game getGame() {
        return game;
    }

    public void setGlobalVariable(String name, long value) {
        globalVariables.put(name, value);
    }

    public long getGlobalVariable(String name) {
        if (!globalVariables.containsKey(name)) globalVariables.put(name, 0L);
        return globalVariables.get(name);
    }

    public boolean buyMinionAndPlaceAt(Pair<Long, Long> hexPosition, String minionType) throws Exception {
        if ((budget < game.getSettingValue("spawn_cost") && !firstBuyMinion)
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

    private double calculateInterestPercentage() {
        boolean DONT_HAVE_BUDGET = budget == 0;
        long baseInterest = game.getSettingValue("interest_pct");
        if (DONT_HAVE_BUDGET) return 0;
        return baseInterest * Math.log10(budget) * Math.log(turnCount);
    }

    private double calculateInterest() {
        return calculateInterestPercentage() * budget / 100.0;
    }

    public long getInterest() {

        return (long) calculateInterest();
    }

    public long getBudget() {
        return (long) budget;
    }

    public double BudgetplusCal() {
        budget += getInterest();
        budget += Constants.turn_income;
        return (int) budget;
    }

    private void receiveTurnBudgetAndInterest() {
        budget = Math.max(game.getSettingValue("max_budget"), budget + calculateInterest());
    }

    public List<Minion> getOwnedMinions() {
        return ownedMinions;
    }
}