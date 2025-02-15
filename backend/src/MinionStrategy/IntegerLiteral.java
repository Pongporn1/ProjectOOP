package MinionStrategy;
import Game.*;
public record IntegerLiteral(int value) implements Expression {

    @Override
    public int evaluate(Minion minion) {
        return value;
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append(value);
    }
}
