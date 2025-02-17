package GameState.Game;

public class Pair <T, U>{
    private T first;
    private U second;
    public Pair(T first, U second){
        this.first = first;
        this.second = second;
    }

    public static <T, U> Pair<T, U> of(T first, U second){
        return new Pair<>(first, second);
    }

    public T getFirst(){
        return first;
    }

    public void setFirst(T first){
        this.first = first;
    }

    public U getSecond(){
        return second;
    }

    public void setSecond(U second){
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
