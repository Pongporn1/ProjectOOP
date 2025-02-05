import Game.*;
import MinionStrategy.*;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String s = """
                move up
                """;
        try {
            StrategyParser parse = new StrategyParser(s);/////
            Strategy strategy = parse.parse();///////
            StringBuilder sb = new StringBuilder();
            strategy.prettyPrint(sb);
            System.out.println(sb);
            Map<String, Strategy> validMinions = new HashMap<>();
            validMinions.put("Proto", strategy);
            //////////////////////////////////////////////////////////////////////////
            Game game = new DuelMode(validMinions);
            game.gameloop();
            //victory checker///////////////////////////////////////////////////////////////
            if(game.getFirstLeader().getOwnedMinions().size() < game.getSecondLeader().getOwnedMinions().size()) {
                System.out.println("winner is " + game.getSecondLeader().getName());
            }else if (game.getFirstLeader().getOwnedMinions().size() > game.getSecondLeader().getOwnedMinions().size()){
                System.out.println("winner is " + game.getFirstLeader().getName());
            }else{
                 int sump1 = 0,sump2 = 0;
                for(Minion n : game.getFirstLeader().getOwnedMinions()) {
                    sump1 += n.getHealth();}
                for(Minion n : game.getSecondLeader().getOwnedMinions()) {
                    sump2 += n.getHealth();}
                if(sump1 > sump2) {System.out.println("winner is " + game.getFirstLeader().getName());}
                else if (sump1 < sump2) {System.out.println("winner is " + game.getSecondLeader().getName());}
                else {
                    if (game.getFirstLeader().getBudget() > game.getSecondLeader().getBudget()) {
                        System.out.println("winner is " + game.getFirstLeader().getName());
                    } else if (game.getFirstLeader().getBudget() < game.getSecondLeader().getBudget()) {
                        System.out.println("winner is " + game.getSecondLeader().getName());
                    } else {
                        System.out.println("Draw ");
                    }
                }
            }

            //victory check end here//////////////////////////////////////////////////////
        } catch (Exception e) {
            System.out.println(e);
            // }
        }
    }
}
