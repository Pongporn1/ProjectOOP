package Game;

import java.util.HashMap;
import java.util.Map;

public abstract class Game {
    protected Leader leader1, leader2;
    protected Hex[][] board;
    protected Map<String, Integer> settings = new HashMap<String, Integer>();
    protected Minion[] minionsKind;

    protected int turn;

    public Game() {
        board = new Hex[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Hex();
            }
        }
    }

    public Leader getFirstLeader() {
        return leader1;
    }

    public Leader getSecondLeader() {
        return leader2;
    }

    public boolean moveMinion(Minion minion, int dRow, int dCol) throws Exception {
        return true;
    }

    public int getAlly(Minion minion) throws Exception {
        return 0;
    }

    public int getOpponent(Minion minion) {
        return 0;
    }

    public int getNearby(Minion minion, Direction direction) {
        return 0;
    }

    private Pair<Integer, Integer> getPosition(Pair<Integer, Integer> pos, Direction direction, int distance) {
        return null;
    }

    public boolean hasMinionAt(int row, int col) {
        return false;
    }

    public int getCurrentTurn() {
        return turn;
    }

    public int getMaxTurn() {
        return 0;
    }

    public Hex getHexAt(int row, int col) {
        return null;
    }

    public Hex getHexAt(Pair<Integer, Integer> pos) {
        return getHexAt(pos.getFirst(), pos.getSecond());
    }

    public Hex[][] getBoard() {
        return board;
    }

    public int getSettingValue(String setting) {
        return 0;
    }

    public boolean setGridOwner(int row, int col, Leader leader) {
        return false;
    }

    public boolean placeMinion(int row, int col, Minion minion) {
        return false;
    }

    public void printBoard() {

    }
}
