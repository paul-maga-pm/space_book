package socialnetwork.utils.containers;

import java.util.Objects;

public class OrderedPair<T1, T2> {
    public T1 first;
    public T2 second;

    public OrderedPair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderedPair)) return false;
        OrderedPair<?, ?> that = (OrderedPair<?, ?>) o;
        return Objects.equals(first, that.first)
                && Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
