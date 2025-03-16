package game.Utility;

import game.DataStructure.Pair;

import java.lang.Number;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FunUtil {
    // [a] -> ((a, b) -> b) -> b
    public static <T, U> U foldl(Function<Pair<U, T>, U> accumulator, U identity, List<T> ls) {
        U result = identity;
        for (T t : ls) {
            result = accumulator.apply(new Pair<>(result, t));
        }
        return result;
    }

    public static <T, U> List<U> map(Function<T, U> f, List<T> ls) {
        List<U> result = new ArrayList<>();
        for (T t : ls) {
            result.add(f.apply(t));
        }
        return result;
    }

    /**
     * @param pred predicate
     * @param a first variable of predicate
     * @param b second variable of predicate
     * @return 3 if P(a, b) && P(b, a), 2 if only P(b, a) and 1 if only P(a, b) otherwise 0
     * @param <U> Type variable of predicate argument
     */
    public static  <U> int binaryPredicate(Function<Pair<U, U>, Boolean> pred, U a, U b) {
        int result = 0;
        if(pred.apply(Pair.of(a, b))) result |= 1;
        if(pred.apply(Pair.of(b, a))) result |= 2;
        return result;
    }

    public static <T extends Number> double sum(List<T> ls){
        return foldl((x) -> x.getFirst() + x.getSecond().doubleValue(), 0.0, ls);
    }
}
