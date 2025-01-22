package Game;

import MinionStrategy.Strategy;

import java.util.HashMap;
import java.util.Map;

public class Minion {

    private String minionType;
    private Leader owner;
    private int health, defense;
    private Map<String, Integer> variables = new HashMap<>();
    private Pair<Integer, Integer> position;
    private Strategy strategy;
    private Game game;

    public Minion(Strategy strategy) {
        position = new Pair<>(5, 5);
        this.strategy = strategy;
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
        Pair<Integer, Integer> transform = (direction.transformDirection()).apply(position.getSecond());
        position.setFirst(position.getFirst() + transform.getFirst());
        position.setSecond(position.getSecond() + transform.getSecond());
        return true;
    }

    public Leader getOwner() {
        return owner;
    }

    public Pair<Integer, Integer> getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return minionType;
    }

    public int getVariable(String variable) {
        if (!variables.containsKey(variable)) variables.put(variable, 0);
        return variables.get(variable);
    }

    public void setVariable(String variable, int value) {
        variables.put(variable, value);
    }
}
