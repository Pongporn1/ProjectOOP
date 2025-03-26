package game.GameState.Game;

import game.Exception.InvalidGameIdException;
import game.GameState.GameMode.Game;

import java.util.HashMap;
import java.util.Map;

public class GameManager {
    private static GameManager instance;
    private Map<String, Game> games = new HashMap<>();
    private GameManager(){}
    public static GameManager instance(){
        if(instance == null) instance = new GameManager();
        return instance;
    }

    public Game getGameFromId(String id){
        if(games.containsKey(id)) return games.get(id);
        throw new InvalidGameIdException("Don't have game with id: " + id);
    }

    public Game createDuelGame(){
        return null;
    }
}
