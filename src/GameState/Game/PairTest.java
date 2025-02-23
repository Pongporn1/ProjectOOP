package GameState.Game;

import DataStructure.Pair;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class PairTest {
    @Test
    public void EQ(){
        Pair<Long, Long> s = Pair.of(1L, 2L);
        Pair<Long, Long> e = Pair.of(1L, 2L);
        Set<Pair<Long, Long>> set = new HashSet<>();
        set.add(s);
        set.add(e);
        System.out.println(set.size());
    }
}