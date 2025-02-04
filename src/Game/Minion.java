package Game;

import MinionStrategy.Strategy;

import java.util.HashMap;
import java.util.Map;

public class Minion {

    private String minionType;
    private Leader owner;
    private int health, defense;
    private Map<String, Long> variables = new HashMap<>();
    private Pair<Long, Long> position;
    private Strategy strategy;
    private Game game;

    public Minion(Game game, Strategy strategy, Pair<Long, Long> position, Leader owner, String minionType) {
        this.game = game;
        this.minionType = minionType;
        this.strategy = strategy;
        this.owner = owner;
        this.position = position;
    }

//    public Minion(String minionType, Leader owner) {
//
//    }

//    public Minion(long frist, long second , Leader owner) {
//        //this.owner = owner;
//        //position = new Pair<>(frist, second);
//    }

    public Game getGame(){
        return game;
    }

    public void execute() throws Exception {
        strategy.execute(this);
    }

    public int getAlly() throws Exception {
        return game.getAlly(this);
    }

    public int getOpponent() throws Exception {
        return game.getOpponent(this);
    }

    public int getNearby(Direction direction) throws Exception {
        return game.getNearby(this, direction);
    }

    public int getHealth() {
        return health;
    }

    public int getDefense() {
        return defense;
    }

    public boolean attack(Direction direction, long cost) throws Exception {
        return false;
    }

    public boolean move(Direction direction) throws Exception {
        Pair<Long, Long> transform = (direction.transformDirection()).apply(position.getSecond());
        try {
            if (game.moveMinionByOffset(this, transform)) {
                position.setFirst(position.getFirst() + transform.getFirst());
                position.setSecond(position.getSecond() + transform.getSecond());
                return true;
            }
        }catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    public Leader getOwner() {
        return owner;
    }

    public Pair<Long, Long> getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return owner.topordown+":"+minionType;
    }

    public long getVariable(String variable) {
        if (!variables.containsKey(variable)) variables.put(variable, 0L);
        return variables.get(variable);
    }

    public void setVariable(String variable, long value) {
        variables.put(variable, value);
    }
}
