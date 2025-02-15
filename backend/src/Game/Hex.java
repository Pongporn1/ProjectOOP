package Game;

public class Hex {
    private Pair<Integer, Integer> position;
    private Minion minionOnGrid;
    private Leader leaderOwner;

    public boolean setOwner(Leader leader) {
        leaderOwner = leader;
        return true;
    }

    public boolean setMinionOnHex(Minion minion) {
        minionOnGrid = minion;
        return true;
    }

    public boolean removeMinionOnHex() {
        if (minionOnGrid == null) return false;
        minionOnGrid = null;
        return true;
    }

    public boolean hasMinionOnGrid() {
        return minionOnGrid != null;
    }

    public Minion getMinionOnGrid() {
        return minionOnGrid;
    }

}
