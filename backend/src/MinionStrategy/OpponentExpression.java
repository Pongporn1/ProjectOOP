package MinionStrategy;
import Game.Minion;

public class OpponentExpression extends InfoExpression{
    @Override
    public int evaluate(Minion minion) throws Exception {
        return minion.getOpponent();
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append("opponent");
    }
}
