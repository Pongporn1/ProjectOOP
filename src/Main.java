import Game.*;
import MinionStrategy.*;

public class Main {
    public static void main(String[] args) {
        String s = """
                if (t % 10 - 5) then move upleft
                else if(t % 10 - 4) then move downleft
                else if(t % 10 - 3) then move down
                else if(t % 10 - 2) then move downright
                else if(t % 10 - 1) then move upright
                else move up
                t = t + 1
                """;
        try {
            StrategyParser parse = new StrategyParser(s);
            Strategy strategy = parse.parse();
            StringBuilder sb = new StringBuilder();
            strategy.prettyPrint(sb);
            System.out.println(sb);
            Game game = new DuelMode();
            Leader p1 = game.getFirstLeader();
            Minion m = new Minion(strategy);
            for(int i = 0; i < 10; i++) {
                System.out.println(m.getPosition());
                m.execute();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}