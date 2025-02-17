package GameState.Game;

import GameState.Leader.Leader;

public class Hex {
    private Pair<Integer, Integer> position;
    private Minion minionOnGrid;
    private Leader leaderOwner = null;
    public boolean hasminion = false;

    public boolean setOwner(Leader leader) {
        leaderOwner = leader;
        return true;
    }

    public Leader getLeader() {

        return leaderOwner;
    }

    public boolean hasOwner() {
        return leaderOwner != null;
    }

    public boolean setMinionOnHex(Minion minion) {
        if(minionOnGrid != null) return false;
        minionOnGrid = minion;
        return true;
    }

    public boolean getAttack(long damage){
        if(minionOnGrid == null) return false;
        minionOnGrid.getDamage(damage);
        return true;
    }

    public boolean removeMinionOnHex() {
        if (minionOnGrid == null) return false;
        minionOnGrid = null;
        hasminion = false;
        return true;
    }

    public boolean hasMinionOnHex() {
        return minionOnGrid != null;
    }

    public Minion getMinionOnHex() {
        return minionOnGrid;
    }

    public String toString2() {
        if (hasminion == false) {
            return "O";
        } else {
            if (minionOnGrid.getOwner().leaderSymbol == "T") {
                return "X";
            } else {
                return "Y";
            }
        }
        //for board with minion
    }

    public String ownerString() {
        if (!hasOwner()) return "_";
        return leaderOwner.leaderSymbol;
    }

    @Override
    public String toString() {
        if (leaderOwner == null) {
            return "O";
        } else {
            if (leaderOwner.leaderSymbol == "T") {
                return "a";
            } else if (leaderOwner.leaderSymbol == "D") {
                return "b";
            } else {
                return "e";
            }
        }
        //for board with owned hex
    }
}