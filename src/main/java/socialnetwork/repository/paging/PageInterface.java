package socialnetwork.repository.paging;
import java.util.stream.Stream;

public interface PageInterface<E> {
    PageableInterface getPageable();

    PageableInterface nextPageable();

    Stream<E> getContent();


}
