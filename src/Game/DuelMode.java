package Game;

import MinionStrategy.Strategy;

import java.util.Map;

public class DuelMode extends Game{

    public DuelMode(Map<String, Strategy> minionsStrategy){
        super(minionsStrategy);
        leader1 = new PlayerLeader(this, "Player1", "1");
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3 - i; j++){
                setGridOwner(i, j, leader1);
            }
        }

        leader2 = new PlayerLeader(this, "Player2", "2");
        for(int i = 7; i > 5; i--){
            for(int j = 7; j > 11 - i; j--){
                setGridOwner(i, j, leader2);
            }
        }
        printOwnerBoard();
    }

//    public DuelMode() {
//        super();
//        leader1 = new PlayerLeader(this, "A","T");
//        leader2 = new PlayerLeader(this, "B","D");
//        this.GameSet();
//        this.printBoard();
//        System.out.println("-----------------------------");
//        Minion m = new Minion(1,1,leader1);
//        Minion n = new Minion(6,6,leader2);
//        setMiniononhex(m,leader1,1,1);
//        setMiniononhex(n,leader2,6,6);
//        this.printBoard2();
//        System.out.println("------------------------------");
//        System.out.println(board[1][1].removeMinionOnHex());
//        this.printBoard2();
//        //this.gameloop
//    }
}
