package MinionStrategy;
import Game.Minion;

public class WhileStatement extends Statement {
    Statement statement;
    Expression condition;

    public WhileStatement(Statement statement, Expression condition) {
        this.condition = condition;
        this.statement = statement;
    }

    @Override
    public boolean execute(Minion target) throws Exception {
        for (int counter = 0; counter < 10000 && condition.evaluate(target) > 0; counter++){
            if(!statement.execute(target)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append("while (");
        condition.prettyPrint(s);
        s.append(") ");
        statement.prettyPrint(s, prefix);
    }
}
