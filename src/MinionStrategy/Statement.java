package MinionStrategy;

import Game.Minion;

public abstract class Statement implements Executable{

    @Override
    public abstract boolean execute(Minion target) throws Exception;

}
