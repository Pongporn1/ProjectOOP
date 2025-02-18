package GameState.GameMode;

import GameState.Game.*;
import AST.Strategy;
import static Utility.FunUtil.*;
import Utility.Number;
import GameState.Leader.Leader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public abstract class Game {
    protected String gameId;
    protected Leader leader1, leader2;
    protected Hex[][] board;
    protected Map<String, Long> settings = new HashMap<String, Long>();
    protected Map<String, Pair<Strategy, Long>> minionKinds = new HashMap<>();
    protected int turn;
    protected int colAmount;
    protected int rowAmount;

    public Game(Map<String, Pair<Strategy, Long>> minionKinds) {
        this(minionKinds, 8, 8);
    }

    public Game(Map<String, Pair<Strategy, Long>> minionKinds, int rowAmount, int colAmount) {
        loadConfiguration();
        this.minionKinds = minionKinds;
        this.rowAmount = rowAmount;
        this.colAmount = colAmount;
        board = new Hex[rowAmount][colAmount];
        for (int i = 0; i < rowAmount; i++) {
            for (int j = 0; j < colAmount; j++) {
                board[i][j] = new Hex();
            }
        }
    }

    public void loadConfiguration() {
        //region Configuration File Read
        //https://www.w3schools.com/java/java_files_read.asp
        try {
            File myObj = new File("configuration.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                settings.put(data.split("=")[0], Long.parseLong(data.split("=")[1]));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        //endregion
    }

    public void runGame() throws Exception {

        leader1.turnBegin(0);
        leader2.turnBegin(0);
        turn = 1;
        for (int i = 1; i <= getSettingValue("max_turns"); i++) {
            giveTurnToLeader(leader1, turn);
            if(leader2.getMinionList().isEmpty()){
                System.out.println("Player 1 Win");
                return;
            }
            giveTurnToLeader(leader2, turn);
            if(leader1.getMinionList().isEmpty()){
                System.out.println("Player 2 Win");
                return;
            }
            turn++;
//            System.out.println("Test Nearby:" + getNearby(leader1.getOwnedMinions().get(0), Direction.DOWN_RIGHT));
//            System.out.println("GameState.Leader 1 Minion List");
//            for(Minion m : leader1.getMinionList()){
//                System.out.println(m.minionDetail());
//            }
//            System.out.println("GameState.Leader 2 Minion List");
//            for(Minion m : leader2.getMinionList()){
//                System.out.println(m.minionDetail());
//            }
        }
        CalculateMaxTurnResult();
    }

    private void CalculateMaxTurnResult(){
        List<Minion> leader1Minions = leader1.getMinionList();
        List<Minion> leader2Minions = leader2.getMinionList();

        long leader1MinionsHealth = foldl((current) ->  current.getFirst() + current.getSecond().getHealth(), 0L, leader1Minions );
        long leader2MinionsHealth = foldl((current) ->  current.getFirst() + current.getSecond().getHealth(), 0L, leader2Minions );

        int res = binaryPredicate((x) -> x.getFirst().size() > x.getSecond().size(), leader1Minions, leader2Minions);
        if(res == 0) res = binaryPredicate((x) -> x.getFirst() > x.getSecond(), leader1MinionsHealth, leader2MinionsHealth);
        if(res == 0) res = binaryPredicate((x) -> x.getFirst() > x.getSecond(), leader1.getBudget(), leader2.getBudget());

        System.out.println("Result");
        System.out.println("Minion Remain");
        System.out.println(leader1Minions.size() + " : " + leader2Minions.size());
        System.out.println("Minion Health Remain");
        System.out.println(leader1MinionsHealth + " : " + leader2MinionsHealth);
        System.out.println("Budget Remain");
        System.out.println(leader1.getBudget() + " : " + leader2.getBudget());
        if(res == 0) System.out.println("Tie");
        else if(res > 0) System.out.println("Player 2 Wins");
        else System.out.println("Player 1 Wins");
    }

    private void giveTurnToLeader(Leader leader, int turn) throws Exception {
        System.out.println("Turn #" + turn + " of " + leader.getLeaderName());
        System.out.println("Budget: " + leader.getBudget());
        giveInterest(leader);
        leader.turnBegin(turn);
        leader.turnEnd();
        printShiftMinionBoard();
    }

    /**
     * @return 0 if budget is 0 otherwise return interest percentage rate from
     * equation baseInterest * number of digit of budget * ln (currentTurn)
     * @effect get "interest_pct" value from game setting
     */
    public double calculateInterestPercentage(double budget) {
        boolean DONT_HAVE_BUDGET = (budget == 0);
        if (DONT_HAVE_BUDGET) return 0;
        long baseInterest = getSettingValue("interest_pct");
        return baseInterest * Math.log10(budget) * Math.log(turn);
    }

    /**
     * @return interest of current budget
     */
    public double calculateInterest(double budget) {
        double interestPercentage = calculateInterestPercentage(budget);
        System.out.println("interest percentage: " + interestPercentage);
        return interestPercentage * budget / 100.0;
    }

    /**
     * Add leader's budget from turn_budget and interest but will not exceed limit value
     * *** Still thinking ***
     * @effect may result in change of budget if budget isn't at limit
     * @effect get turn_budget and max_budget from game setting
     */
    private void giveInterest(Leader leader) {
        leader.receiveBudget(getSettingValue("turn_budget"));
        double budget = leader.getExactBudget();
        double interest = calculateInterest(budget);
        System.out.println("interest: " + interest);
        leader.receiveBudget(interest);
    }

    public Leader getFirstLeader() {
        return leader1;
    }

    public Leader getSecondLeader() {
        return leader2;
    }

    public boolean moveMinionByOffset(Minion minion, int dRow, int dCol) throws Exception {
        Pair<Long, Long> currentPosition = minion.getPosition();
        Pair<Long, Long> destinationPosition = new Pair<>(currentPosition.getFirst() + dRow, currentPosition.getSecond() + dCol);
        if(minion.getOwner().getBudget() < 1) throw new Exception("Don't have enough budget to move");
        if (destinationPosition.getFirst() < 0 || destinationPosition.getSecond() < 0
                || destinationPosition.getFirst() >= rowAmount || destinationPosition.getSecond() >= colAmount)
            throw new Exception("Can't move outside board, destination position is " + destinationPosition);
        boolean isMoveable = !hasMinionAt(destinationPosition.getFirst(), destinationPosition.getSecond());

        //System.out.println("Move: " + dRow + ", " + dCol);
        if (isMoveable) {
            getHexAt(currentPosition).removeMinionOnHex();
            getHexAt(destinationPosition).setMinionOnHex(minion);
        }
        return isMoveable;
    }

    public boolean buyHexAt(Leader buyer, long row, long col) {
        if (isHexOccupied(row, col)) return false;
        return board[(int) row][(int) col].setOwner(buyer);
    }

    public boolean isHexOccupied(long row, long col) {
        return getHexAt((int) row, (int) col).hasOwner();
    }

    public boolean moveMinionByOffset(Minion minion, Pair<Long, Long> offset) throws Exception {
        return moveMinionByOffset(minion, offset.getFirst().intValue(), offset.getSecond().intValue());
    }

    public boolean moveMinionByDirection(Minion minion, Direction direction) throws Exception {
        Pair<Long, Long> transform = (direction.transformDirection()).apply(minion.getPosition().getSecond());
        return moveMinionByOffset(minion, transform);
    }

    public int getAlly(Minion minion)  {
        Pair<Long, Long> currentPosition = minion.getPosition();
        for (int distance = 1; distance < Math.max(colAmount, rowAmount); distance++) {
            for (int direction = 1; direction <= 6; direction++) {
                Pair<Long, Long> checkedPosition;
                try {
                    checkedPosition = getRelativePosition(currentPosition, Direction.directions[direction - 1], distance);
                } catch (Exception e) {
                    continue;
                }
                Hex checkedHex = getHexAt(checkedPosition);
                if (checkedHex.hasMinionOnHex() && checkedHex.getMinionOnHex().getOwner().equals(minion.getOwner())) {
                    return distance * 10 + direction;
                }
            }
        }
        return 0;
    }

    public int getOpponent(Minion minion) {
        Pair<Long, Long> currentPosition = minion.getPosition();
        for (int distance = 1; distance < Math.max(colAmount, rowAmount); distance++) {
            for (int direction = 1; direction <= 6; direction++) {
                Pair<Long, Long> checkedPosition;
                try {
                    checkedPosition = getRelativePosition(currentPosition, Direction.directions[direction - 1], distance);
                } catch (Exception e) {
                    continue;
                }
                Hex checkedHex = getHexAt(checkedPosition);
                if (checkedHex.hasMinionOnHex() && !checkedHex.getMinionOnHex().getOwner().equals(minion.getOwner()))
                    return distance * 10 + direction;
            }
        }
        return 0;
    }

    public int getNearby(Minion minion, Direction direction)  {
        Pair<Long, Long> currentPosition = minion.getPosition();
        for (int distance = 1; distance < Math.max(colAmount, rowAmount); distance++) {
            Pair<Long, Long> checkedPosition;
            try {
                checkedPosition = getRelativePosition(currentPosition, direction, distance);
            } catch (Exception e) {
                continue;
            }
            Hex checkedHex = getHexAt(checkedPosition);
            if (checkedHex.hasMinionOnHex()) {
                Minion m = checkedHex.getMinionOnHex();
                int sign = m.getOwner().equals(minion.getOwner()) ? 1 : -1;
                return sign * (100 * Utility.Number.count_digit(m.getHealth()) + 10 * Number.count_digit(m.getDefense()) + distance);
            }
        }
        return 0;
    }

    // Should Refactor and Optimized, Maybe we can do O(1) with utility function in Utility/Number class
    public Pair<Long, Long> getRelativePosition(Pair<Long, Long> pos, Direction direction, int distance) throws Exception {
        switch (direction) {
            case Direction.UP -> {
                long col = pos.getSecond();
                long row = pos.getFirst() - distance;
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(row, col);
            }
            case Direction.DOWN -> {
                long col = pos.getSecond() ;
                long row = pos.getFirst() + distance;
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(row, col);
            }
            case Direction.DOWN_LEFT -> {
                long col = pos.getSecond();
                long row = pos.getFirst();
                for (distance--; distance >= 0; distance--) {
                    col--;
                    if (col % 2 == 0) row++;
                }
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(row, col);
            }
            case Direction.DOWN_RIGHT -> {
                long col = pos.getSecond();
                long row = pos.getFirst();
                for (distance--; distance >= 0; distance--) {
                    col++;
                    if (col % 2 == 0) row++;
                }
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(row, col);
            }
            case Direction.UP_LEFT -> {
                long col = pos.getSecond();
                long row = pos.getFirst();
                for (distance--; distance >= 0; distance--) {
                    col--;
                    if (col % 2 == 0) row--;
                }
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(row, col);
            }
            case Direction.UP_RIGHT -> {
                long col = pos.getSecond();
                long row = pos.getFirst();
                for (distance--; distance >= 0; distance--) {
                    col++;
                    if (col % 2 == 0) row--;
                }
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(row, col);
            }
        }
        throw new Exception("Unknown direction");
    }

    public boolean attackTo(Minion attacker, Direction direction, long damage) {
        Pair<Long, Long> attackerPosition = attacker.getPosition();
        try {
            Pair<Long, Long> positionToAttack = getRelativePosition(attackerPosition, direction, 1);
            System.out.println(direction);
            System.out.println(attacker.getPosition() + " attack -> " + positionToAttack);
            Hex hexToAttack = getHexAt(positionToAttack);
            return hexToAttack.getAttack(attacker, damage);
        }catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public void destroyMinion(Minion minion){
        Pair<Long, Long> pos = minion.getPosition();
        Hex hexOfMinion = getHexAt(pos);
        hexOfMinion.removeMinionOnHex();
        Leader owner = minion.getOwner();
        owner.removeMinion(minion);
    }

    public boolean hasMinionAt(long row, long col) {
        return hasMinionAt((int) row, (int) col);
    }

    public boolean hasMinionAt(int row, int col) {
        return board[row][col].hasMinionOnHex();
    }

    public int getCurrentTurn() {
        return turn;
    }

    public Hex getHexAt(int row, int col) {
        return board[row][col];
    }

    public Hex getHexAt(Pair<Long, Long> pos) {
        return getHexAt(pos.getFirst().intValue(), pos.getSecond().intValue());
    }

    public Hex[][] getBoard() {
        return board;
    }

    public long getSettingValue(String setting) {
        return settings.get(setting);
    }

    public boolean setGridOwner(int row, int col, Leader leader) { ////?????
        return getHexAt(row, col).setOwner(leader);
    }

    public Minion spawnMinion(long row, long col, String minionType, Leader owner) {//??????
        Minion minion = new Minion(this, Pair.of(row, col), owner, minionType, minionKinds.get(minionType).getSecond());
        getHexAt((int) row, (int) col).setMinionOnHex(minion);
        return minion;
    }

    public Minion spawnMinion(Pair<Long, Long> pos, String minionType, Leader owner) {
        return spawnMinion(pos.getFirst(), pos.getSecond(), minionType, owner);
    }

    public void executeMinion(Minion minion) throws Exception {
        Strategy minionStrategy = minionKinds.get(minion.getMinionType()).getFirst();
        minionStrategy.execute(minion);
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j].toString());
            }
            System.out.println();
        }// for board only
    }

    public void printBoard2() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j].toString2());
            }
            System.out.println();
        }// more mod
    }

    public void printOwnerBoard() {
        for(int i = 0; i < rowAmount * 2; i++){
            for(int j = 0; j < colAmount; j++){
                if(j % 2 != i % 2){
                    Leader m = board[i / 2][j].getLeader();

                    if (m == null) {
                        System.out.print("_____");
                        System.out.print(" ".repeat(10));
                    }
                    else {
                        int leaderNameLength = m.getLeaderName().length();
                        System.out.print(m.getLeaderName());
                        System.out.print(" ".repeat(15 - leaderNameLength));
                    }
                }else{
                    System.out.print(" ".repeat(15 ));
                }
            }
            System.out.println();
            System.out.println();
        }
        System.out.println();
    }

    public void printMinionBoard() {
        for (int i = 0; i < rowAmount; i++) {
            for (int j = 0; j < colAmount; j++) {
                Minion m = board[i][j].getMinionOnHex();
                if (m == null) System.out.print("_ ");
                else System.out.print(m + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // O(nlogn) + O(m)

    // O(mlogn)

    public void printShiftMinionBoard(){
        for(int i = 0; i < rowAmount * 2; i++){
            for(int j = 0; j < colAmount; j++){
                if(j % 2 != i % 2){
                    Minion m = board[i / 2][j].getMinionOnHex();

                    if (m == null) {
                        System.out.print(" ".repeat(7));
                        System.out.print("_");
                        System.out.print(" ".repeat(7));
                    }
                    else {
                        int minionTypeNameLength = m.getMinionType().length();

                        System.out.print(m);
                        System.out.print(" ".repeat(13 - minionTypeNameLength));
                    }
                }else{
                    System.out.print(" ".repeat(15 ));
                }
            }
            System.out.println();
        }
        System.out.println();
    }


    public List<String> availableMinions() {
        return minionKinds.keySet().stream().toList();
    }


}