import Game.*;
import MinionStrategy.*;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String s = """
                t = t + 1
                if (t % 10 - 6) then move upleft
                else if(t % 10 - 5) then move downleft
                else if(t % 10 - 4) then move down
                else if(t % 10 - 3) then move downright
                else if(t % 10 - 2) then move upright
                else move up
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
            game.beginGame();
            Leader p1 = game.getFirstLeader();
            Leader p2 = game.getSecondLeader();
            p1.turnBegin();
            p1.buyMinionAndPlaceAt(Pair.of(1L,1L),"Proto");
            p1.turnEnd();
            game.printMinionBoard();
            p2.turnBegin();
            p2.buyMinionAndPlaceAt(Pair.of(7L,7L),"Proto");
            game.printMinionBoard();
            p2.turnEnd();
            game.printMinionBoard();
            p1.turnBegin();
            p1.turnEnd();
            game.printMinionBoard();
            p2.turnBegin();
            p2.turnEnd();
            game.printMinionBoard();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}