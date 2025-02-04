package Game;

import java.util.*;
import java.util.Scanner;

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

    public void setBuyableHexes(){

        for(Pair<Integer, Integer> p : ownHexes){

            int[][] metrix = new int[8][2] ;

             metrix[0][0] = p.getFirst()+1;         metrix[0][1] = p.getSecond();
             metrix[1][0] = p.getFirst()+1;         metrix[1][1] = p.getSecond()+1;
             metrix[2][0] = p.getFirst();         metrix[2][1] = p.getSecond()+1;
             metrix[3][0] = p.getFirst()-1;         metrix[3][1] = p.getSecond()+1;
             metrix[4][0] = p.getFirst()-1;         metrix[4][1] = p.getSecond();
             metrix[5][0] = p.getFirst()-1;         metrix[5][1] = p.getSecond()-1;
             metrix[6][0] = p.getFirst();         metrix[6][1] = p.getSecond()-1;
             metrix[7][0] = p.getFirst()+1;         metrix[7][1] = p.getSecond()-1;
             for(int i =0;i<8;i++) {
              if(0 <= metrix[i][0] && metrix[i][0] < 8 &&0 <= metrix[i][1] && metrix[i][1] < 8) {
                  Pair<Integer,Integer> X = new Pair<>(metrix[i][0], metrix[i][1]);

                  if(!ownHexes.contains(X) && !buyableHexes.contains(X)) { //need fix equal
                      buyableHexes.add(Pair.of(metrix[i][0], metrix[i][1]));
                      System.out.println(i);
                      System.out.println("add " + metrix[i][0] + " " + metrix[i][1]);

                  }
              }
             }
        }
    }

    public String getName(){
        return leaderName;
    }

    public void turnBegin(int turn) {
        if (turn != 1) {
            BudgetplusCal();
        }

        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("wanna get hex y/n ");
            String input = sc.nextLine();
            if (input.equals("y")) {
                System.out.println("where");
                int I1 = sc.nextInt();
                int I2 = sc.nextInt();
                Pair<Integer, Integer> posi = new Pair<>(I1, I2);
                buyHex(posi);
            }else if (input.equals("n")) {break;}
        }

        while (true) {
            if(turn == 1) {
                Scanner sc = new Scanner(System.in);
                System.out.println("where you want to spawn minnion");
                int I1 = sc.nextInt();
                int I2 = sc.nextInt();
                Minion m = new Minion(I1,I2,this);
                game.setMiniononhex(m,this,I1,I2);ownedMinions.add(m);
                break;
            }else{
                Scanner sc = new Scanner(System.in);
                System.out.println("wanna buy minnion y/n");
                String input = sc.nextLine();
                if (input.equals("y")) {
                    System.out.println("where");
                    int I1 = sc.nextInt();
                    int I2 = sc.nextInt();
                    Pair<Integer, Integer> posi = new Pair<>(I1, I2);
                    Minion m = new Minion(I1,I2,this);
                    System.out.println(buyMinionAndPlaceAt(posi,m));
                }else if (input.equals("n")) {break;}


            }
        }
    }

    public void turnEnd(){
     game.printBoard();
     System.out.println("---------------------------");
       game.printBoard2();
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

    public void setOwnHexes(Pair<Integer, Integer> ownHexes) {
        this.ownHexes.add(ownHexes);
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