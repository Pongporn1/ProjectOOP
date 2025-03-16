package game.AST;

import game.GameState.Game.Minion;

public class AllyExpression extends InfoExpression {

    @Override
    public long evaluate(Minion minion) {
        return minion.getAlly();
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append("ally");
    }
}
