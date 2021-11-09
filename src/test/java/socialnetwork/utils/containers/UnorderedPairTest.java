package socialnetwork.utils.containers;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnorderedPairTest {
    @Test
    void constructorTest(){
        UnorderedPair<String, Long> pair = new UnorderedPair<>("John", 1234L);
        assertEquals("John", pair.first);
        assertEquals(1234L, pair.second);
    }

    @Test
    void equalsAndHashCodeTest(){
        UnorderedPair<String, Long> pair1 = new UnorderedPair<>("John", 1234L);
        UnorderedPair<Long, String> pair2 = new UnorderedPair<>(1234L, "John");
        assertEquals(pair1, pair2);
        assertEquals(pair1.hashCode(), pair2.hashCode());
    }
}