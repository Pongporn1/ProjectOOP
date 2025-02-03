package Game;

public class DuelMode extends Game{
    public DuelMode() {
        super();
        leader1 = new PlayerLeader(this, "A","T");
        leader2 = new PlayerLeader(this, "B","D");
        this.GameSet();
        this.printBoard();
        System.out.println("-----------------------------");
        Minion m = new Minion(1,1,leader1);
        Minion n = new Minion(6,6,leader2);
        setMiniononhex(m,leader1,1,1);
        setMiniononhex(n,leader2,6,6);
        this.printBoard2();
        System.out.println("------------------------------");
        System.out.println(board[1][1].removeMinionOnHex());
        this.printBoard2();
        //this.gameloop
    }
}
