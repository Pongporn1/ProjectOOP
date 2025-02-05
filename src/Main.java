import Game.*;
import MinionStrategy.*;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String s = """
                oppoLoc = opponent
                if (oppoLoc) then
                {
                    if (oppoLoc / 10 - 1) then {
                        if(oppoLoc % 10 - 5) then move upleft
                        else if(oppoLoc % 10 - 4) then move downleft
                        else if(oppoLoc % 10 - 3) then move down
                        else if(oppoLoc % 10 - 2) then move downright
                        else if(oppoLoc % 10 - 1) then move upright
                        else move up
                    } else {
                        if(oppoLoc % 10 - 5) then shoot upleft 1
                        else if(oppoLoc % 10 - 4) then shoot downleft 1
                        else if(oppoLoc % 10 - 3) then shoot down 1
                        else if(oppoLoc % 10 - 2) then shoot downright 1
                        else if(oppoLoc % 10 - 1) then shoot upright 1
                        else shoot up 1
                    }
                }
                else {
                    move down
                }
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
            game.gameLoop();


        } catch (Exception e) {
            System.out.println(e);
            // }
        }
    }
}
