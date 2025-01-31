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

    public Function<Integer, Pair<Integer, Integer>> transformDirection() throws Exception {
        return switch (direction) {
            case "up" -> (colum) -> new Pair<>(-1, 0);
            case "down" -> (colum) -> new Pair<>(1, 0);
            case "upleft" -> (colum) -> colum % 2 == 0 ? new Pair<>(-1, -1) : new Pair<>(0, -1);
            case "upright" -> (colum) -> colum % 2 == 0 ? new Pair<>(-1, 1) : new Pair<>(0, 1);
            case "downleft" -> (colum) -> colum % 2 == 0 ? new Pair<>(0, -1) : new Pair<>(1, -1);
            case "downright" -> (colum) -> colum % 2 == 0 ? new Pair<>(0, 1) : new Pair<>(1, 1);
            default -> throw new Exception("");
        };
    }
}
