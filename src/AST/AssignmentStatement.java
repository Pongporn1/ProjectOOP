package AST;
import GameState.Game.Minion;

public class AssignmentStatement extends Statement {

    private String variable;
    private Expression expression;

    public AssignmentStatement(String variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public boolean execute(Minion target) throws Exception {
        if (Identifier.specialVariable.containsKey(variable)) return true;
        if(Character.isUpperCase(variable.charAt(0))) target.getOwner().setGlobalVariable(variable, expression.evaluate(target));
        else target.setVariable(variable, expression.evaluate(target));
        return true;
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append(variable);
        s.append(" = ");
        expression.prettyPrint(s);
    }
}
