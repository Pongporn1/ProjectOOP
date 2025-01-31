package MinionStrategy;
import Game.Minion;

public class AssignmentStatement extends Statement {

    String variable;
    Expression expression;

    public AssignmentStatement(String variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public boolean execute(Minion target) throws Exception {
        target.setVariable(variable, expression.evaluate(target));
        return true;
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append(variable);
        s.append(" = ");
        expression.prettyPrint(s);
    }
}
