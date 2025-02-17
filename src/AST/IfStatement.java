package AST;
import GameState.Game.*;
public class IfStatement extends Statement {

    Expression condition;
    Statement trueStatement;
    Statement falseStatement;

    public IfStatement(Expression condition, Statement trueStatement, Statement falseStatement) {
        this.condition = condition;
        this.trueStatement = trueStatement;
        this.falseStatement = falseStatement;
    }

    @Override
    public boolean execute(Minion target) throws Exception {
        if (condition.evaluate(target) > 0) return trueStatement.execute(target);
        else return falseStatement.execute(target);
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append("if (");
        condition.prettyPrint(s);
        s.append(") ");
        s.append("then ");
        if (trueStatement instanceof IfStatement) {
            s.append("\n\t").append(prefix);
            trueStatement.prettyPrint(s, prefix + "\t");
        }else trueStatement.prettyPrint(s, prefix);
        if(falseStatement instanceof IfStatement) s.append("\n").append(prefix);
        else s.append(" ");
        s.append("else ");
        falseStatement.prettyPrint(s, prefix);

    }
}
