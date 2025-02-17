package AST;
import GameState.Game.Minion;
import GameState.Game.Direction;
public class NearbyExpression extends InfoExpression{

    private Direction direction;

    public NearbyExpression(Direction direction){
        this.direction = direction;
    }

    @Override
    public long evaluate(Minion minion) throws Exception {
        return minion.getNearby(direction);
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append(prefix);
        s.append("nearby ").append(direction);
    }
}
