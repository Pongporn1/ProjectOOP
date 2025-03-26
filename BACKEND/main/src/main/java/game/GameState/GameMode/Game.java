package game.GameState.GameMode;

import com.example.main.models.GameData;
import com.example.main.models.HexPos;
import com.example.main.models.LeaderData;
import com.example.main.models.MinionHex;
import game.AST.Strategy;
import game.DataStructure.Pair;
import game.GameState.Game.Direction;
import game.GameState.Game.Hex;
import game.GameState.Game.Minion;
import game.GameState.Leader.Leader;
import game.Utility.Number;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static game.Utility.FunUtil.binaryPredicate;
import static game.Utility.FunUtil.foldl;


public abstract class Game {
    protected String gameId;
    protected Leader leader1, leader2;
    protected Hex[][] board;
    protected Map<String, Long> settings = new HashMap<String, Long>();
    protected Map<String, Pair<Strategy, Long>> minionKinds = new HashMap<>();
    protected int turn;
    protected int colAmount;
    protected int rowAmount;
    protected String state;
    protected int turnOf;

    public Map<String, Pair<Strategy, Long>> getMinions(){
        return minionKinds;
    }

    public void setConfig(Map<String, Long> config){
        settings = config;
    }

    public List<Hex> buyableHex(Leader leader){
        Set<Hex> adjacencyHex = new HashSet<>();
        for(Pair<Long, Long> hexPos : leader.getOwnHexes()) {
            adjacencyHex.addAll(adjacentHex(hexPos));
        }
        return adjacencyHex.stream().filter((hex) -> !hex.hasOwner()).toList();
    }

    public List<GameData> skipState(){
//        if(state.equals("buying")) state = "spawning";
//        else if(state.equals("spawning")){
//            state = "buying";
//            turnOf = turnOf == 2 ? 1 : 2;
//        }
        return processGame();
    }

    private MinionHex hexToMinionHex(Hex hex){
        return new MinionHex(hex.hasMinionOnHex() ? hex.getMinionOnHex().getMinionType():"None", hex.hasMinionOnHex() ? hex.getMinionOnHex().getOwner().getLeaderName() : "None");
    }

    private String hexToOwner(Hex hex){
        return hex.hasOwner() ? hex.getLeader().getLeaderName() : "None";
    }



    public GameData getGameData(){
        Hex[][] board = getBoard();
        String[][] owner = Arrays.stream(board).map((r) -> Arrays.stream(r).map(this::hexToOwner).toArray(String[]::new)).toArray(String[][]::new);
        MinionHex[][] minionsHex = Arrays.stream(board).map((r) -> Arrays.stream(r).map(this::hexToMinionHex).toArray(MinionHex[]::new)).toArray(MinionHex[][]::new);
        Leader leader1 = getFirstLeader();
        Leader leader2 = getSecondLeader();
        System.out.println(leader1.getMinionList());
        List<HexPos> leader1HexPos = buyableHex(leader1).stream().map((x) -> new HexPos(x.getPosition().getFirst(), x.getPosition().getSecond())).toList();
        List<HexPos> leader2HexPos = buyableHex(leader2).stream().map((x) -> new HexPos(x.getPosition().getFirst(), x.getPosition().getSecond())).toList();
        Pair<Integer, String> state = getState();
        String leader1State = state.getFirst() == 1 ? state.getSecond() : "other";
        String leader2State = state.getFirst() == 2 ? state.getSecond() : "other";
        GameData gameData = new GameData(getCurrentTurn(),owner, minionsHex, new HashMap<>(){{
            put(leader1.getLeaderName(), new LeaderData(leader1State, leader1.getLeaderName(), leader1.getBudget(), leader1.getMinionList().size(), leader1.getOwnHexAmount(), leader1HexPos));
            put(leader2.getLeaderName(), new LeaderData(leader2State, leader2.getLeaderName(), leader2.getBudget(), leader2.getMinionList().size(), leader2.getOwnHexAmount(), leader2HexPos));
        }});
        return gameData;
    }

    public Game(Map<String, Pair<Strategy, Long>> minionKinds) {
        this(minionKinds, 8, 8);
    }

    public Pair<Integer, String> getState(){
        return Pair.of(turnOf, state);
    }

    public Game(Map<String, Pair<Strategy, Long>> minionKinds, int rowAmount, int colAmount) {
        loadConfiguration();
        state = "first_spawning";
        turnOf = 1;
        this.minionKinds = minionKinds;
        this.rowAmount = rowAmount;
        this.colAmount = colAmount;
        board = new Hex[rowAmount][colAmount];
        for (int i = 0; i < rowAmount; i++) {
            for (int j = 0; j < colAmount; j++) {
                board[i][j] = new Hex(Pair.of(i, j));
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
        System.out.println(res);
        if(res == 0) res = binaryPredicate((x) -> x.getFirst() > x.getSecond(), leader1MinionsHealth, leader2MinionsHealth);
        System.out.println(res);
        if(res == 0) res = binaryPredicate((x) -> x.getFirst() > x.getSecond(), leader1.getBudget(), leader2.getBudget());
        System.out.println(res);

        System.out.println("Result");
        System.out.println("Minion Remain");
        System.out.println(leader1Minions.size() + " : " + leader2Minions.size());
        System.out.println("Minion Health Remain");
        System.out.println(leader1MinionsHealth + " : " + leader2MinionsHealth);
        System.out.println("Budget Remain");
        System.out.println(leader1.getBudget() + " : " + leader2.getBudget());
        if(res == 0) System.out.println("Tie");
        else if(res == 1) System.out.println("Player 1 Wins");
        else if(res == 2) System.out.println("Player 2 Wins");
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

    public Pair<Boolean, List<GameData>>  buyHexAt(Leader buyer, long row, long col) {
        List<GameData> data = new ArrayList<>();
        if (isHexOccupied(row, col)) return Pair.of(false, data);
        board[(int) row][(int) col].setOwner(buyer);
        data.add(getGameData());
        return Pair.of(true, data);
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
                return sign * (100 * Number.count_digit(m.getHealth()) + 10 * Number.count_digit(m.getDefense()) + distance);
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
                    if (col % 2 == 1) row++;
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
                    if (col % 2 == 1) row++;
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

    public List<GameData> executeMinions(Leader leader){
        List<GameData> data = new ArrayList<>();
        for (Minion m : leader.getOwnedMinions()) {
            if (leader.getBudget() <= 0) return data;
            executeMinionsAux(m, data);
        }
        return data;
    }

    private List<GameData>  executeMinionsAux(Minion m, List<GameData> data){
        try {
            m.execute();
            data.add(getGameData());
        }catch (Exception e) {
            System.out.println(e);
        }
        return data;
    }

    public Pair<Boolean, List<GameData>> spawnMinion(long row, long col, String minionType, Leader owner) {//??????
        Minion minion = new Minion(this, Pair.of(row, col), owner, minionType, minionKinds.get(minionType).getSecond());
        System.out.println(minion);
        getHexAt((int) row, (int) col).setMinionOnHex(minion);
        List<GameData> data = new ArrayList<>();
        owner.getMinionList().add((minion));
        data.add(getGameData());
//        if(state.equals("first_spawning") && turnOf == 1){
//            turnOf = 2;
//        }else if(state.equals("first_spawning") && turnOf == 2){
//            turnOf = 1;
//            state = "buying";
//        }else if(state.equals("spawning")){
//            state = "buying";
//            turnOf = turnOf == 2 ? 1 : 2;
//        }
        return Pair.of(true, data);
    }

    public List<GameData> processGame() {
        List<GameData> data = new ArrayList<>();
        System.out.println(turnOf + ": " + state);
        if(state.equals("first_spawning") && turnOf == 1){
            turnOf = 2;
            data.addAll(leader2.spawnMinionState());
            return data;
        }

        if(state.equals("first_spawning") && turnOf == 2){
            turnOf = 1;
            state = "buying";
            turn++;
            giveInterest(leader1);
            data.addAll(leader1.buyHexState());
            return data;
        }

        if(state.equals("spawning")){
            if(turnOf == 1){
                state = "execute";
                data.addAll(executeMinions(leader1));
            }else if(turnOf == 2){
                state = "execute";
                data.addAll(executeMinions(leader2));
            }

            state = "buying";
            turnOf = turnOf == 2 ? 1 : 2;

            if(turnOf == 1) {
                turn++;
                giveInterest(leader1);
                data.addAll(leader1.buyHexState());
            }
            else {
                giveInterest(leader2);
                data.addAll(leader2.buyHexState());
            }

            return data;
        }

        if(state.equals("buying")){
            state = "spawning";

            System.out.println(turnOf + ": " + state);
            if(turnOf == 1) data.addAll(leader1.spawnMinionState());
            else data.addAll(leader2.spawnMinionState());

            return data;
        }

//        if(turnOf == 1 && state.equals("buying")){
//            try {
//                data.addAll(executeMinions(leader1));
//            }catch (Exception e) {
//                System.out.println(e);
//            }
//            turn++;
//            giveInterest(leader1);
//            data.addAll(leader1.bu)
//        }else if(turnOf == 2 && state.equals("buying")){
//            try {
//                data.addAll(executeMinions(leader1));
//            }catch (Exception e) {
//                System.out.println(e);
//            }
//            giveInterest(leader2);
//        }

        return data;
    }

    public Pair<Boolean, List<GameData>> spawnMinion(Pair<Long, Long> pos, String minionType, Leader owner) {
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

    public Set<Hex> adjacentHex(Pair<Long, Long> pos) {
        Set<Hex> adjacentHex = new HashSet<>();
        //System.out.println("at:" + (pos.getFirst() + 1) + ", " + (pos.getSecond() + 1));
        for(int dir = 0; dir < 6; dir++){
            try {
                Hex h = getHexAt(getRelativePosition(pos, Direction.directions[dir], 1));
                //System.out.println("dir: " + Direction.directions[dir]);
                //System.out.println(h.getPosition().getFirst() + 1 + " " + (h.getPosition().getSecond() + 1));
                adjacentHex.add(h);
            }catch (Exception e) {
                continue;
            }
        }
        return adjacentHex;
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

    public void printShiftPositionBoard(){
        for(int i = 0; i < rowAmount * 2; i++){
            for(int j = 0; j < colAmount; j++){
                if(j % 2 != i % 2){
                    Hex h = board[i / 2][j];
                    String pos = h.getPosition().toString();
                    int offset = pos.length();
                    System.out.print(pos);
                    System.out.print(" ".repeat(10 - offset));

                }else{
                    System.out.print(" ".repeat(10 ));
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printShiftMinionBoard(){
        for(int i = 0; i < rowAmount * 2; i++){
            for(int j = 0; j < colAmount; j++){
                if(j % 2 != i % 2){
                    Hex h = board[i / 2][j];
                    Minion m = h.getMinionOnHex();
                    Leader o = h.getLeader();
                    String x = o == null ? "_" : o.leaderSymbol;

                    if (m == null) {
                        System.out.print(" ".repeat(7));
                        System.out.print(x);
                        System.out.print(" ".repeat(7));
                    }
                    else {
                        int minionTypeNameLength = m.getMinionType().length();
                        System.out.print(x+",");
                        System.out.print(m);
                        System.out.print(" ".repeat(11 - minionTypeNameLength));
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