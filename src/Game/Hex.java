package Game;

public class Hex {
    private Pair<Integer, Integer> position;
    private Minion minionOnGrid;
    private Leader leaderOwner = null;
    public boolean hasminion = false;

    public boolean setOwner(Leader leader) {
        leaderOwner = leader;
        return true;
    }

    /*public boolean Hasminion() {

        return hasminion;
    }*/

    public Leader getLeader() {

        return leaderOwner;
    }

    public void setMinionOnHex(Minion minion) {
        if(leaderOwner != null) {
        minionOnGrid = minion;
        hasminion = true;
        }
    }

    public boolean removeMinionOnHex() {
        if (minionOnGrid == null) return false;
        minionOnGrid = null;
        hasminion = false;
        return true;
    }

    public boolean hasMinionOnGrid() {
        return minionOnGrid != null;
    }

    public Minion getMinionOnGrid() {
        return minionOnGrid;
    }


    public String toString2() {
        if(hasminion == false){
            return "O";
        }else{
            if(minionOnGrid.getOwner().topordown == "T"){return "X";}
            else{return "Y";}


        }
        //for board with minion
    }

    @Override
   public String toString() {
 if(leaderOwner == null){
     return "O";
 }else{
     if(leaderOwner.topordown == "T"){return "a";}
     else if (leaderOwner.topordown == "D") {return "b";}
     else{return "e";}
 }
 //for board with owned hex
    }
}