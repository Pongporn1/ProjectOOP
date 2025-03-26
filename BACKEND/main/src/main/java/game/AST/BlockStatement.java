package game.AST;

import game.GameState.Game.Minion;

import java.util.ArrayList;
import java.util.List;

public class BlockStatement extends Statement {

    List<Statement> statements;

    public BlockStatement(){
        statements = new ArrayList<>();
    }

    @Override
    public boolean execute(Minion target) throws Exception {
        for(Statement statement : statements){
           if(!statement.execute(target)){
                return false;
           }
        }
        return true;
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        if (statements.isEmpty()) {
            s.append("{}");
            return;
        }
        s.append("{\n");
        for (Statement statement : statements) {
            s.append("\t" + prefix);
            statement.prettyPrint(s, "\t" + prefix);
            s.append("\n");
        }
        s.append(prefix);
        s.append("}");
    }

    public void addStatement(Statement s) {
        statements.add(s);
    }
}
