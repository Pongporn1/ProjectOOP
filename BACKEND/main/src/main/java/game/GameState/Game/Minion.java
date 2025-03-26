package game.GameState.Game;

import game.DataStructure.Pair;
import game.GameState.GameMode.Game;
import game.GameState.Leader.Leader;

import java.util.HashMap;
import java.util.Map;

public class Minion {

    private final String minionType;
    private final Leader owner;
    private long health;
    private final long defense;
    private final Map<String, Long> variables = new HashMap<>();
    private final Pair<Long, Long> position;
    private final Game game;

    public Minion(Game game, Pair<Long, Long> position, Leader owner, String minionType, long defense) {
        this.game = game;
        this.minionType = minionType;
        this.owner = owner;
        this.position = position;
        this.health = game.getSettingValue("init_hp");
        this.defense = defense;
    }

    public String getMinionType() {
        return minionType;
    }

    public Game getGame(){
        return game;
    }

    public void execute() throws Exception {
        game.executeMinion(this);
    }

    public int getAlly() {
        return game.getAlly(this);
    }

    public int getOpponent() {
        return game.getOpponent(this);
    }

    public int getNearby(Direction direction) {
        return game.getNearby(this, direction);
    }

        public long getHealth() {
        return health;
    }

    public long getDefense() {
        return defense;
    }

    public boolean attack(Direction direction, long damage) throws Exception {
        if (!owner.reduceBudget(damage + 1)) return false;
        return game.attackTo(this, direction, damage);
    }

    public void getDamage(Minion attacker, long damage){
        health = Math.max(0, health - Math.max(1, damage - defense));
        if(health == 0){
            System.out.println(owner.getLeaderName() + "'s " + minionType + " " + position + " was destroy");
            System.out.println("by " + attacker.owner.getLeaderName() + "'s " + attacker.minionType + " " + attacker.position);
            game.destroyMinion(this);
        }
    }

    public boolean move(Direction direction) throws Exception {
        System.out.print(position + " move " + direction + " to ");
        Pair<Long, Long> transform = (direction.transformDirection()).apply(position.getSecond());
        try {
            if (game.moveMinionByOffset(this, transform)) {
                position.setFirst(position.getFirst() + transform.getFirst());
                position.setSecond(position.getSecond() + transform.getSecond());
                System.out.println(position);
                return true;
            }
        }catch (Exception e) {
            System.out.println(e);
            return false;
        }
        System.out.println();
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
        return owner.leaderSymbol +":"+minionType;
    }

    public String minionDetail(){
        return minionType + "\nHP: " + health + "\nDefense: " + defense;
    }

    public long getVariable(String variable) {
        if (!variables.containsKey(variable)) variables.put(variable, 0L);
        return variables.get(variable);
    }

    public void setVariable(String variable, long value) {
        variables.put(variable, value);
    }
}
