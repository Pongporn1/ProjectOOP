package Game;

import MinionStrategy.Strategy;

import java.util.Map;

public class SoloMode extends Game {
    public SoloMode(Map<String, Strategy> minionStrategies) {
        super(minionStrategies);
    }
}
