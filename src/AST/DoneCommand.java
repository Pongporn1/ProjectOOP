package AST;
import GameState.Game.*;
public class DoneCommand extends Statement{

    @Override
    public boolean execute(Minion target) {
        return false;
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append("done");
    }
}
