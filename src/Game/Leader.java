package Game;

import java.util.*;
import java.util.Scanner;

public abstract class Leader {
    protected String leaderName;
    public String topordown;
    private List<Minion> ownedMinions = new ArrayList<Minion>();
    private double budget=1000;
    private Map<String, Integer> globalVariables = new HashMap<>();
    public List<Pair<Integer, Integer>> ownHexes = new ArrayList<>();
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

    public boolean buyHex(Pair<Integer, Integer> hexPosition){ //tempo Pair<Integer, Integer> hexPosition
        System.out.println("hi1");
        if(budget>=Constants.hex_price /*&& buyableHexes.contains(hexPosition)ยังทำงานไมู่ก*/) {
            System.out.println("hi2");
            game.board[hexPosition.getFirst()][hexPosition.getSecond()].setOwner(this);
            budget -= Constants.hex_price;
            ownHexes.add(Pair.of(hexPosition.getFirst(), hexPosition.getSecond()));
           //buyableHexes.clear();
           // setBuyableHexes();

            return true;
        }


        return false;
    }

    public void setOwnHexes(Pair<Integer, Integer> ownHexes) {
        this.ownHexes.add(ownHexes);
    }

    public Game getGame() {
        return game;
    }

    public boolean buyMinionAndPlaceAt(Pair<Integer, Integer> hexPosition, Minion minion) /*throws Exception */{
        if(budget>=Constants.minion_price /*&& ownHexes.contains(Pair.of(hexPosition.getFirst(), hexPosition.getSecond()))*/){//equal
            budget -= Constants.minion_price;
            Minion M01 = new Minion(hexPosition.getFirst(), hexPosition.getSecond() , this);

            ownedMinions.add(M01);
            game.setMiniononhex(M01,this,hexPosition.getFirst(),hexPosition.getSecond());

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