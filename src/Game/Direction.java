package Game;

import java.util.function.Function;

public enum Direction {
    UP("up"), DOWN("down"), UP_LEFT("upleft"), UP_RIGHT("upright"), DOWN_LEFT("downleft"), DOWN_RIGHT("downright");

    public static final Direction[] directions = {UP, UP_RIGHT, DOWN_RIGHT, DOWN, DOWN_LEFT, UP_LEFT};
    private final String direction;

    Direction(String direction) {
        this.direction = direction;
    }

    public String toString() {
        return direction;
    }

    public Function<Long, Pair<Long, Long>> transformDirection() throws Exception {
        return switch (direction) {
            case "up" -> (colum) -> new Pair<>(-1L, 0L);
            case "down" -> (colum) -> new Pair<>(1L, 0L);
            case "upleft" -> (colum) -> new Pair<>(-1L + (colum + 1) % 2, -1L);
            case "upright" -> (colum) -> new Pair<>(-1L + (colum + 1) % 2, 1L);
            case "downleft" -> (colum) -> new Pair<>((colum + 1) % 2, -1L) ;
            case "downright" -> (colum) ->  new Pair<>((colum + 1) % 2, 1L) ;
            default -> throw new Exception("from Direction.transformDirection, unknown direction: " + direction);
        };
    }
}
