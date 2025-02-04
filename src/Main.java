import Game.*;
import MinionStrategy.*;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String s = """
                move down
                """;
        try {
            StrategyParser parse = new StrategyParser(s);
            Strategy strategy = parse.parse();
            StringBuilder sb = new StringBuilder();
            strategy.prettyPrint(sb);
            System.out.println(sb);
            Map<String, Strategy> validMinions = new HashMap<>();
            validMinions.put("Proto", strategy);
            Game game = new DuelMode(validMinions);
            game.gameloop();

        } catch (Exception e) {
            System.out.println(e);
            // }
        }
    }
}
