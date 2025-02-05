package Game;
import java.util.Random;

import MinionStrategy.Strategy;
import MinionStrategy.StrategyParser;

import java.util.HashMap;
import java.util.Map;

public class Minion {

    private String minionType;
    private Leader owner;
    private int health=Constants.HP, defense=Constants.DEF;
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

    public void execute() throws Exception {//////////////
        /*Random rand = new Random();             //nvm
        String s1 = """
                move down
                """;
        StrategyParser parse1 = new StrategyParser(s1);
        Strategy strategy1 = parse1.parse();
        String s2 = """
                move downright
                """;
        StrategyParser parse2 = new StrategyParser(s2);
        Strategy strategy2 = parse2.parse();
        String s3 = """
                move right
                """;
        StrategyParser parse3 = new StrategyParser(s3);
        Strategy strategy3 = parse3.parse();
        String s4 = """
                move up
                """;
        StrategyParser parse4 = new StrategyParser(s4);
        Strategy strategy4 = parse4.parse();
        String s5 = """
                move upleft
                """;
        StrategyParser parse5 = new StrategyParser(s5);
        Strategy strategy5 = parse5.parse();
        String s6 = """
                move downright
                """;
        StrategyParser parse6 = new StrategyParser(s6);
        Strategy strategy6 = parse6.parse();

        int random = rand.nextInt(3);
        if(owner.topordown == ("1")) {
            if(random == 0) {strategy1.execute(this);}
            else if(random == 1) {strategy2.execute(this);}
            else if(random == 2) {strategy3.execute(this);}
        }else if(owner.topordown == ("2")) {
            if(random == 0) {strategy4.execute(this);}
            else if(random == 1) {strategy5.execute(this);}
            else if(random == 2) {strategy6.execute(this);}
        }*/
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

    public boolean attack(Direction direction, Long cost) throws Exception {
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
