package MinionStrategy;
import Game.*;

public class AttackCommand extends Statement{

    private final Direction direction;
    private final Expression expression;

    public AttackCommand(Direction direction, Expression expression) {
        this.direction = direction;
        this.expression = expression;
    }

    @Override
    public boolean execute(Minion target) throws Exception {
        target.getOwner().reduceBudget(1);
        long cost = expression.evaluate(target);
        if(target.getOwner().getBudget() < cost)
            throw new Exception("Have not enough budget to shoot at: " + cost);
        target.attack(direction, cost);
        return false;
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append("shoot ");
        s.append(direction);
    }
}
