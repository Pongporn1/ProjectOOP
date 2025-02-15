package MinionStrategy;
import Game.Minion;

public class AllyExpression extends InfoExpression{

    @Override
    public int evaluate(Minion minion) throws Exception {
        return minion.getAlly();
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append("ally");
    }
}
