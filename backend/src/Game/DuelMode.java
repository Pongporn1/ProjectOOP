package Game;

public class DuelMode extends Game{
    public DuelMode() {
        super();
        leader1 = new PlayerLeader(this, "Player1");
        leader2 = new PlayerLeader(this, "Player2");
    }
}
