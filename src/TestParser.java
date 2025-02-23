import AST.Strategy;
import DataStructure.Pair;
import GameState.GameMode.DuelMode;
import GameState.GameMode.Game;
import Parser.StrategyParser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class TestParser {

    @Test
    void GoodTest1() {
        String a = """
                t = t + 1  # keeping track of the turn number
                m = 0  # number of random moves this turn
                while (3 - m) {  # made less than 3 random moves
                  if (budget - 100) then {} else done  # too poor to do anything else
                  opponentLoc = opponent
                  if (opponentLoc / 10 - 1)
                  then  # opponent afar
                    if (opponentLoc % 10 - 5) then move upleft
                    else if (opponentLoc % 10 - 4) then move downleft
                    else if (opponentLoc % 10 - 3) then move down
                    else if (opponentLoc % 10 - 2) then move downright
                    else if (opponentLoc % 10 - 1) then move upright
                    else move up
                  else if (opponentLoc)
                  then  # opponent adjacent to this minion
                    if (opponentLoc % 10 - 5) then {
                      cost = 10 ^ (nearby upleft % 100 + 1)
                      if (budget - cost) then shoot upleft cost else {}
                    }
                    else if (opponentLoc % 10 - 4) then {
                      cost = 10 ^ (nearby downleft % 100 + 1)
                      if (budget - cost) then shoot downleft cost else {}
                    }
                    else if (opponentLoc % 10 - 3) then {
                      cost = 10 ^ (nearby down % 100 + 1)
                      if (budget - cost) then shoot down cost else {}
                    }
                    else if (opponentLoc % 10 - 2) then {
                      cost = 10 ^ (nearby downright % 100 + 1)
                      if (budget - cost) then shoot downright cost else {}
                    }
                    else if (opponentLoc % 10 - 1) then {
                      cost = 10 ^ (nearby upright % 100 + 1)
                      if (budget - cost) then shoot upright cost else {}
                    }
                    else {
                      cost = 10 ^ (nearby up % 100 + 1)
                      if (budget - cost) then shoot up cost else {}
                    }
                  else {  # no visible opponent; move in a random direction
                    try = 0  # keep track of number of attempts
                    while (3 - try) {  # no more than 3 attempts
                      success = 1
                      dir = random % 6
                      # (nearby <dir> % 10 + 1) ^ 2 is positive if adjacent cell in <dir> has no ally
                      if ((dir - 4) * (nearby upleft % 10 + 1) ^ 2) then move upleft
                      else if ((dir - 3) * (nearby downleft % 10 + 1) ^ 2) then move downleft
                      else if ((dir - 2) * (nearby down % 10 + 1) ^ 2) then move down
                      else if ((dir - 1) * (nearby downright % 10 + 1) ^ 2) then move downright
                      else if (dir * (nearby upright % 10 + 1) ^ 2) then move upright
                      else if ((nearby up % 10 + 1) ^ 2) then move up
                      else success = 0
                      if (success) then try = 3 else try = try + 1
                    }
                    m = m + 1
                  }
                }  # end while
                """;

        String b = """
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
        String c = """
                move up
                move down
                while (int) {if (int) then done else {}}
                {{{{if (T - t) then {T = T - t} else {T = T + t}}}}}
                """;
        try {
            StrategyParser parse1 = new StrategyParser(a);
            Strategy strategy1 = parse1.parse();
            StrategyParser parse2 = new StrategyParser(b);
            Strategy strategy2 = parse2.parse();
            StrategyParser parse3 = new StrategyParser(c);
            Strategy strategy3 = parse3.parse();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}