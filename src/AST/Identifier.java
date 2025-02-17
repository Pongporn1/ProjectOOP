package AST;

import GameState.Game.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class Identifier implements Expression {
    private final String identifier;
    public static final Map<String, Function<Minion, Long>> specialVariable = new HashMap<>() {{
        put("col", (m) -> m.getPosition().getSecond());
        put("row", (m) -> m.getPosition().getFirst());
        put("budget", (m) -> m.getOwner().getBudget());
        put("int", (m) -> m.getOwner().getInterest());
        put("maxbudget", (m) -> m.getOwner().getGame().getSettingValue("max_budget"));
        put("spawnsremain", (m) -> m.getOwner().getSpawnRemain());
        put("random", (m) -> new Random().nextLong(0, 1000));
    }};

    public Identifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public long evaluate(Minion minion) {
        if(specialVariable.containsKey(identifier)) return specialVariable.get(identifier).apply(minion);
        if(Character.isUpperCase(identifier.charAt(0))) return minion.getOwner().getGlobalVariable(identifier);
        return minion.getVariable(identifier);
    }

    @Override
    public void prettyPrint(StringBuilder s, String prefix) {
        s.append(prefix);
        s.append(identifier);
    }
}
