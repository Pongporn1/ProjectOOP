package Game;

import MinionStrategy.Strategy;
import Utility.Number;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class Game {
    protected Leader leader1, leader2;
    protected Hex[][] board;
    protected Map<String, Long> settings = new HashMap<String, Long>();
    //protected Minion[] minionsKind;
    protected Map<String, Strategy> minionStrategies = new HashMap<>();
    protected int turn;
    protected int colAmount;
    protected int rowAmount;

    public Game(Map<String, Strategy> minionStrategies) {
        this(minionStrategies, 8, 8);
    }

    public Game(Map<String, Strategy> minionStrategies, int rowAmount, int colAmount) {
        loadConfiguration();
        this.minionStrategies = minionStrategies;
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

//    public void GameSet() {
//        for (int i = 0; i < 3; i++) {
//            board[0][i].setOwner(leader1);
//            leader1.ownHexes.add(Pair.of(0L, (long) i));
//        }
//        for (int i = 0; i < 2; i++) {
//            board[1][i].setOwner(leader1);
//            leader1.ownHexes.add(Pair.of(1L, (long) i));
//        }
//        //Pair<Integer,Integer> AAA = new Pair<>(0,1);
//        //Pair<Integer,Integer> AAAA = new Pair<>(1,1);
//        //board[0][1].setOwner(leader1);//leader1.setOwnHexes(AAA);
//        //System.out.println("test "+leader1.ownHexes.contains(AAA));
//        ///board[1][1].setOwner(leader1);//leader1.setOwnHexes(AAAA);
//
//        for (int i = 6; i < 8; i++) {
//            board[6][i].setOwner(leader2);
//            leader2.ownHexes.add(Pair.of(6L, (long) i));
//        }
//        for (int i = 5; i < 8; i++) {
//            board[7][i].setOwner(leader2);
//            leader2.ownHexes.add(Pair.of(7L, (long) i));
//        }
//    }

    public void gameLoop() throws Exception {

        for (int i = 0; i < getSettingValue("max_turns"); i++) {
            System.out.println("player1 turn:" + turn);
            leader1.turnBegin(turn);
            leader1.turnEnd();
            System.out.println("player2 turn:" + turn);
            leader2.turnBegin(turn);
            leader2.turnEnd();
            turn++;
            System.out.println("Test Nearby:" + getNearby(leader1.getOwnedMinions().get(0), Direction.DOWN_RIGHT));
            System.out.println("Leader 1 Minion List");
            for(Minion m : leader1.ownedMinions){
                System.out.println(m.minionDetail());
            }
            System.out.println("Leader 2 Minion List");
            for(Minion m : leader2.ownedMinions){
                System.out.println(m.minionDetail());
            }
        }
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
        if (destinationPosition.getFirst() < 0 || destinationPosition.getSecond() < 0
                || destinationPosition.getFirst() >= rowAmount || destinationPosition.getSecond() >= colAmount)
            throw new Exception("Can't move outside board, destination position is " + destinationPosition);
        boolean isMoveable = !hasMinionAt(destinationPosition.getFirst(), destinationPosition.getSecond());

        System.out.println("Move: " + dRow + ", " + dCol);
        if (isMoveable) {
            getHexAt(currentPosition).removeMinionOnHex();
            getHexAt(destinationPosition).setMinionOnHex(minion);
        }
        return isMoveable;
    }

    public boolean buyHexAt(Leader buyer, long row, long col) throws Exception {
        if (isHexOccupied(row, col)) return false;
        return board[(int) row][(int) col].setOwner(buyer);
    }

    public boolean isHexOccupied(long row, long col) {
        return getHexAt((int) row, (int) col).hasOwner();
    }

    public boolean moveMinionByOffset(Minion minion, Pair<Long, Long> offset) throws Exception {
        return moveMinionByOffset(minion, offset.getFirst().intValue(), offset.getSecond().intValue());
    }

    public int getAlly(Minion minion)  {
        Pair<Long, Long> currentPosition = minion.getPosition();
        for (int distance = 1; distance < Math.max(colAmount, rowAmount); distance++) {
            for (int direction = 1; direction <= 6; direction++) {
                Pair<Long, Long> checkedPosition;
                try {
                    checkedPosition = getPosition(currentPosition, Direction.directions[direction - 1], distance);
                } catch (Exception e) {
                    continue;
                }
                Hex checkedHex = getHexAt(checkedPosition);
                if (checkedHex.hasMinionOnGrid() && checkedHex.getMinionOnGrid().getOwner().equals(minion.getOwner())) {
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
                    checkedPosition = getPosition(currentPosition, Direction.directions[direction - 1], distance);
                } catch (Exception e) {
                    continue;
                }
                Hex checkedHex = getHexAt(checkedPosition);
                if (checkedHex.hasMinionOnGrid() && !checkedHex.getMinionOnGrid().getOwner().equals(minion.getOwner()))
                    return distance * 10 + direction;
            }
        }
        return 0;
    }

    public int getNearby(Minion minion, Direction direction) throws Exception {
        Pair<Long, Long> currentPosition = minion.getPosition();
        for (int distance = 1; distance < Math.max(colAmount, rowAmount); distance++) {
            Pair<Long, Long> checkedPosition;
            try {
                checkedPosition = getPosition(currentPosition, direction, distance);
            } catch (Exception e) {
                continue;
            }
            Hex checkedHex = getHexAt(checkedPosition);
            if (checkedHex.hasMinionOnGrid()) {
                Minion m = checkedHex.getMinionOnGrid();
                int sign = m.getOwner().equals(minion.getOwner()) ? 1 : -1;
                return sign * (100 * Utility.Number.count_digit(m.getHealth()) + 10 * Number.count_digit(m.getDefense()) + distance);
            }
        }
        return 0;
    }

    // Should Refactor and Optimized, Maybe we can do O(1) with utility function in Utility/Number class
    public Pair<Long, Long> getPosition(Pair<Long, Long> pos, Direction direction, int distance) throws Exception {
        switch (direction) {
            case Direction.UP -> {
                long col = pos.getFirst() - distance;
                long row = pos.getSecond();
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(col, row);
            }
            case Direction.DOWN -> {
                long col = pos.getFirst() + distance;
                long row = pos.getSecond();
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(col, row);
            }
            case Direction.DOWN_LEFT -> {
                long col = pos.getSecond();
                long row = pos.getFirst();
                for (distance--; distance >= 0; distance--) {
                    col--;
                    if (col % 2 == 1) row++;
                }
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(col, row);
            }
            case Direction.DOWN_RIGHT -> {
                long col = pos.getSecond();
                long row = pos.getFirst();
                for (distance--; distance >= 0; distance--) {
                    col++;
                    if (col % 2 == 1) row++;
                }
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(col, row);
            }
            case Direction.UP_LEFT -> {
                long col = pos.getSecond();
                long row = pos.getFirst();
                for (distance--; distance >= 0; distance--) {
                    col--;
                    if (col % 2 == 1) row--;
                }
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(col, row);
            }
            case Direction.UP_RIGHT -> {
                long col = pos.getSecond();
                long row = pos.getFirst();
                for (distance--; distance >= 0; distance--) {
                    col++;
                    if (col % 2 == 1) row--;
                }
                if (col >= colAmount || col < 0 || row >= rowAmount || row < 0)
                    throw new Exception("Position out of bound");
                return new Pair<>(col, row);
            }
        }
        throw new Exception("Unknown direction");
    }

    public boolean attackTo(Minion attacker, Direction direction, long damage) {
        Pair<Long, Long> attackerPosition = attacker.getPosition();
        try {
            Pair<Long, Long> positionToAttack = getPosition(attackerPosition, direction, 1);
            Hex hexToAttack = getHexAt(positionToAttack);
            return hexToAttack.getAttack(damage);
        }catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean hasMinionAt(long row, long col) {
        return hasMinionAt((int) row, (int) col);
    }

    public boolean hasMinionAt(int row, int col) {
        return board[row][col].hasMinionOnGrid();
    }

    public int getCurrentTurn() {
        return turn;
    }

    public int getMaxTurn() {
        return Constants.max_turn;
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

    public Minion placeMinion(long row, long col, String minionType, Leader owner) {//??????
        Minion minion = new Minion(this, minionStrategies.get(minionType), Pair.of(row, col), owner, minionType);
        getHexAt((int) row, (int) col).setMinionOnHex(minion);
        return minion;
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
        for (int i = 0; i < rowAmount; i++) {
            for (int j = 0; j < colAmount; j++) {
                System.out.print(board[i][j].ownerString());
            }
            System.out.println();
        }
    }

    public void printMinionBoard() {
        for (int i = 0; i < rowAmount; i++) {
            for (int j = 0; j < colAmount; j++) {
                Minion m = board[i][j].getMinionOnGrid();
                if (m == null) System.out.print("_ ");
                else System.out.print(m + " ");
            }
            System.out.println();
        }
        System.out.println();
    }


}