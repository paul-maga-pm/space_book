package socialnetwork.service;

public interface Observer<E> {
    void update(E e);
}
