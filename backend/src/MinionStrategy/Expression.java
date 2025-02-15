package MinionStrategy;

import Game.Minion;

public interface Expression extends Node{
    public int evaluate(Minion minion) throws Exception;
}
