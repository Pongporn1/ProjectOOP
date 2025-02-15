package MinionStrategy;
import Game.Minion;
public class LeftAssociativeBinaryExpression implements Expression {

    private Expression left, right;
    private String operator;

    public LeftAssociativeBinaryExpression(String operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public int evaluate(Minion minion) throws Exception {
        return switch (operator){
            case "+" -> left.evaluate(minion) + right.evaluate(minion);
            case "-" -> left.evaluate(minion) - right.evaluate(minion);
            case "*" -> left.evaluate(minion) * right.evaluate(minion);
            case "/" -> left.evaluate(minion) / right.evaluate(minion);
            case "%" -> left.evaluate(minion) % right.evaluate(minion);
            default -> throw new Exception("unknown operator: " + operator);
        };
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append(prefix);
        left.prettyPrint(s);
        s.append(" ").append(this.operator).append(" ");
        right.prettyPrint(s);
    }
}
