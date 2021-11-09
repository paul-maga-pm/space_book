package socialnetwork.utils.containers;


import java.util.Objects;

/**
 * UnorderedPair container
 * @param <T1> type of the first element of the pair
 * @param <T2> type of the second element of the pair
 * Order in the container is irrelevant
 */
public class UnorderedPair<T1, T2> {
    public T1 first;
    public T2 second;

    /**
     * Constructor that creates a new pair formed by the given elements
     * @param first first element
     * @param second second element
     */
    public UnorderedPair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Copy constructor that creates a new pair equal to the given pair
     * @param other pair that will be copied
     */
    public UnorderedPair(UnorderedPair<T1, T2> other){
        this.first = other.first;
        this.second = other.second;
    }

    /**
     * Checks if this and o are equal
     * Order is irrelevant, so pair (1,2) is equal to pair (2,1)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnorderedPair)) return false;
        UnorderedPair<?, ?> pair = (UnorderedPair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second) ||
                Objects.equals(first, pair.second) && Objects.equals(second, pair.first);
    }

    /**
     * Returns hashCode of this
     */
    @Override
    public int hashCode() {
        int hashFirst = Objects.hashCode(first);
        int hashSecond = Objects.hashCode(second);
        int minHash = Math.min(hashFirst, hashSecond);
        int maxHash = Math.max(hashFirst, hashSecond);
        return Objects.hash(minHash, maxHash);
    }
}
