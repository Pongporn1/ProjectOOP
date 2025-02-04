package MinionStrategy;
import Game.Minion;

public class RightAssociativeBinaryExpression implements Expression {
    private Expression left, right;
    private String operator;

    public RightAssociativeBinaryExpression(String operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public long evaluate(Minion minion) throws Exception {
        return  (int) Math.pow(left.evaluate(minion), right.evaluate(minion));
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append(prefix);
        left.prettyPrint(s);
        s.append(" ");
        s.append(operator);
        s.append(" ");
        right.prettyPrint(s);
    }
}
