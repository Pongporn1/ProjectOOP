package game.AST;

import game.GameState.Game.Minion;

public interface Executable extends Node {
    public boolean execute(Minion target) throws Exception;
}