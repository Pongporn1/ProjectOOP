import AST.Strategy;
import DataStructure.Pair;
import GameState.GameMode.AutoMode;
import GameState.GameMode.Game;
import GameState.GameMode.SoloMode;
import Parser.StrategyParser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class TestGameState {

    @Test
    void BotModeRun() throws Exception {
        String stra1 = """
                oppoLoc = opponent
                allyLoc = ally
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
                        if(oppoLoc % 10 - 5) then shoot upleft 10
                        else if(oppoLoc % 10 - 4) then shoot downleft 10
                        else if(oppoLoc % 10 - 3) then shoot down 10
                        else if(oppoLoc % 10 - 2) then shoot downright 10
                        else if(oppoLoc % 10 - 1) then shoot upright 10
                        else shoot up 10
                    }
                }
                else {
                    ranPos = 1 + (random % 6)
                    if(ranPos - 5) then {
                        move up
                    }else if (ranPos - 4) then {
                        move down
                    }else if (ranPos - 3) then {
                        move downleft
                    }else if (ranPos - 2) then {
                        move downright
                    }else if (ranPos - 1) then {
                        move upleft
                    }else {
                        move upright
                    }
                }
                """;

            StrategyParser parse = new StrategyParser(stra1);
            Strategy strategy = parse.parse();

            Map<String, Pair<Strategy, Long>> minionKinds = new HashMap<>();
            minionKinds.put("Proto", Pair.of(strategy, 0L));
            Game game = new AutoMode(minionKinds);
            game.runGame();
    }

    @Test
    void SoloModeRun() throws Exception {
        String stra1 = """
                oppoLoc = opponent
                allyLoc = ally
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
                        if(oppoLoc % 10 - 5) then shoot upleft 10
                        else if(oppoLoc % 10 - 4) then shoot downleft 10
                        else if(oppoLoc % 10 - 3) then shoot down 10
                        else if(oppoLoc % 10 - 2) then shoot downright 10
                        else if(oppoLoc % 10 - 1) then shoot upright 10
                        else shoot up 10
                    }
                }
                else {
                    ranPos = 1 + (random % 6)
                    if(ranPos - 5) then {
                        move up
                    }else if (ranPos - 4) then {
                        move down
                    }else if (ranPos - 3) then {
                        move downleft
                    }else if (ranPos - 2) then {
                        move downright
                    }else if (ranPos - 1) then {
                        move upleft
                    }else {
                        move upright
                    }
                }
                """;

        StrategyParser parse = new StrategyParser(stra1);
        Strategy strategy = parse.parse();

        Map<String, Pair<Strategy, Long>> minionKinds = new HashMap<>();
        minionKinds.put("Proto", Pair.of(strategy, 0L));
        Game game = new SoloMode(minionKinds, "");
        game.runGame();
    }
}
