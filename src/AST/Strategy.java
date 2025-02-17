package AST;

import GameState.Game.Minion;
import java.util.ArrayList;
import java.util.List;

public class Strategy implements Executable
{
    private final List<Statement> statements;

    public Strategy(Statement statement){
        statements = new ArrayList<>();
        statements.add(statement);
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
        for(Statement statement : statements){
            statement.prettyPrint(s);
            s.append("\n");
        }
    }

    public void addStatement(Statement s) {
        statements.add(s);
    }
}
