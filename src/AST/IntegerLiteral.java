package AST;
import GameState.Game.*;
public record IntegerLiteral(int value) implements Expression {

    @Override
    public long evaluate(Minion minion) {
        return value;
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append(value);
    }
}
